package com.example.ismail.wadup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
   private SinchClient sinchClient ;
    private Call call ;
    private Button button ;
    private String recipientId;
    private String callerId ;
    private MediaPlayer player ;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        setTitle("");
        android.content.Context context = this.getApplicationContext();
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        player = MediaPlayer.create(this, notification);
        player.setLooping(true);
        DataHolder.getInstance().setplayer(player);
        Intent intent = getIntent();
        callerId = intent.getStringExtra("callerId");
        recipientId = intent.getStringExtra("recipientId");
        sinchClient = Sinch.getSinchClientBuilder().context(context)
                .applicationKey("8f602396-3987-475f-b0ea-a5e115db3085")
                .applicationSecret("jZ89mRY/REWZ9TbXUtxDdg==")
                .environmentHost("sandbox.sinch.com")
                .userId(callerId )
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
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.call).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.Decline).setOnTouchListener(mDelayHideTouchListener);
        button = (Button) findViewById(R.id.call) ;
        DataHolder.getInstance().setClient(sinchClient);
        ActivityCompat.requestPermissions(FullscreenActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        ActivityCompat.requestPermissions(FullscreenActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
   public void makecall(View v)
   {//DataHolder.getInstance().getCall().addCallListener(new SinchCallListener(this));
        int type = DataHolder.getInstance().getType();
       call=DataHolder.getInstance().getCall() ;
       if (call == null && sinchClient.isStarted()) {//make a call
           if (type==R.id.next) //when it's a just voice call
           {call=sinchClient.getCallClient().callUser(recipientId) ;
           call.addCallListener(new SinchCallListener(this));}
           else
           {
               call=sinchClient.getCallClient().callUserVideo(recipientId) ;
               DataHolder.getInstance().setClient(sinchClient);
               call.addCallListener(new SinchVidListner(this));

           }
           DataHolder.getInstance().setCall(call);
           button.setText("Hang Up");

       } else if (button.getText()=="Hang Up"){ //hangup
           call.hangup();
           call=null ;
           DataHolder.getInstance().setCall(call) ;
           button.setText("Call");
       }
       else if  (button.getText()=="Answer")  { //answer
           DataHolder.getInstance().stop();
           call.answer();
           if (type==R.id.next) //when it's a just voice call
           {call.addCallListener(new SinchCallListener(this));}
           else
           {
               call.addCallListener(new SinchVidListner(this));}
           button.setText("Hang Up");
       }
       else if (!sinchClient.isStarted())
           sinchClient.start();
   }
    public void endcall(View v)
    {   call=DataHolder.getInstance().getCall();
        if (call!=null)
    {
        call.hangup();
        call=null ;
        DataHolder.getInstance().setCall(call);
        DataHolder.getInstance().stop();
        button.setText("Call");}
    }

    @Override
    protected void onDestroy() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminateGracefully();
        super.onDestroy();
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
                    Toast.makeText(FullscreenActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
