package com.example.ismail.wadup;

import android.*;
import android.Manifest;
import android.app.AlarmManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import java.util.ArrayList;
import java.util.List;
public class Users extends AppCompatActivity  {
String callerid ;

    Firebase pres ;
    List<String> names = new ArrayList<String>();
    List<String> keys = new  ArrayList<String>() ;
    private ProgressDialog progress ;
    private MediaPlayer player;
    private SinchClient sinchClient;
    private Firebase con;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        setTitle("Connected users");
        progress=new ProgressDialog(this) ;
       final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
         con= new Firebase("https://wadup-a1d4b.firebaseio.com/.info/connected");
        callerid=user.getUid() ;
        connect();
        pres = new Firebase("https://wadup-a1d4b.firebaseio.com/pres/"+callerid);
        pres.keepSynced(true);
        ActivityCompat.requestPermissions(Users.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        ActivityCompat.requestPermissions(Users.this, new String[]{android.Manifest.permission.CAMERA}, 1);
        Firebase ref = new Firebase("https://wadup-a1d4b.firebaseio.com/pres/");
        final ListView li =(ListView) findViewById(R.id.usersListView) ;
        pres.setValue(user.getEmail());
        pres.onDisconnect().removeValue();
        assert li != null;
        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 progress.setMessage("Loading ..."); progress.show();
                String recipientId=keys.get(position) ;
                Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
                //intent.putExtra("callerId", callerid);
                DataHolder.getInstance().setDone(true) ;
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("recipientId", recipientId);
                startActivity(intent);
                progress.dismiss();
            }
        });
        Query queryRef = ref.orderByKey();
        queryRef.addChildEventListener(new ChildEventListener() {

            TextView user=new TextView(Users.this) ;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey() ;
                if ( !callerid.equals(key))
                {names.add(dataSnapshot.getValue(String.class));keys.add(key);
                ArrayAdapter<String> namesArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.one_user, names);
                li.setAdapter(namesArrayAdapter);}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            names.remove(dataSnapshot.getValue(String.class)) ;
                keys.remove(dataSnapshot.getKey()) ;
                ArrayAdapter<String> namesArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.one_user, names);
                li.setAdapter(namesArrayAdapter);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }
    public void connect ()
    {

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        player = MediaPlayer.create(this, notification);
        player.setLooping(true);
        DataHolder.getInstance().setplayer(player);

        sinchClient = Sinch.getSinchClientBuilder().context(this)
                .applicationKey("8f602396-3987-475f-b0ea-a5e115db3085")
                .applicationSecret("jZ89mRY/REWZ9TbXUtxDdg==")
                .environmentHost("sandbox.sinch.com")
                .userId(callerid )
                .build();
        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportCalling(true);
        //   sinchClient.setSupportManagedPush(true);
// or
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection() ;
        sinchClient.addSinchClientListener(new SinchClientListener() {
            public void onClientStarted(SinchClient client) { }
            public void onClientStopped(SinchClient client) { }
            public void onClientFailed(SinchClient client, SinchError error) { }
            public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration registrationCallback) { }
            public void onLogMessage(int level, String area, String message) { }
        });
        sinchClient.startListeningOnActiveConnection();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener(this));
        sinchClient.start();
        DataHolder.getInstance().setClient(sinchClient);
        DataHolder.getInstance().setDone(false);
    }
    @Override
    protected void onStart() {
        super.onStart();

        con.addValueEventListener(new Connection_Mon()) ;

        DataHolder.getInstance().setDone(false);
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener(this));

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminateGracefully();
        pres.removeValue();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(Users.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
