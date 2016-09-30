package com.example.ismail.wadup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
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
    private Button vid , aud ;

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
        Intent intent = getIntent();

        recipientId = intent.getStringExtra("recipientId");
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        sinchClient=DataHolder.getInstance().getClient();
        findViewById(R.id.call).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.Decline).setOnTouchListener(mDelayHideTouchListener);
        new Firebase("https://wadup-a1d4b.firebaseio.com/.info/connected").addValueEventListener(new Connection_Mon());
        button = (Button) findViewById(R.id.call) ;
        vid =(Button) findViewById(R.id.nextv);
        aud=(Button) findViewById(R.id.next);
        //DataHolder.getInstance().setClient(sinchClient);

            if (! DataHolder.getInstance().isDone())
            {   call=DataHolder.getInstance().getCall() ;
                if (! call.getDetails().isVideoOffered())
            {call.addCallListener(new SinchCallListener(this));}
            else
            {
                DataHolder.getInstance().setVidadd(false);
                call.addCallListener(new SinchVidListner(this));
            }
                TextView callState = (TextView) findViewById(R.id.fullscreen_content);
                callState.setText("Ringing");
                button.setText("Answer");
                Button dec = (Button) findViewById(R.id.Decline) ;
                aud.setVisibility(View.GONE);
                vid.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
                dec.setVisibility(View.VISIBLE);
                DataHolder.getInstance().setDone(true);
            }
        DataHolder.getInstance().setAct(this);
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

       int type = v.getId();
       DataHolder.getInstance().setType(type);
       call=DataHolder.getInstance().getCall() ;
       if (call == null && sinchClient.isStarted()) {//make a call
           if (! DataHolder.getInstance().isOnline())
           {
               Toast.makeText(this, "connection Lost",Toast.LENGTH_LONG).show();
               return;
           }
           aud.setVisibility(View.GONE);
           vid.setVisibility(View.GONE);
           button.setVisibility(View.VISIBLE);
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

          button.setVisibility(View.GONE);
           aud.setVisibility(View.VISIBLE);
           vid.setVisibility(View.VISIBLE);
       }
       else if  (button.getText()=="Answer")  { //answer
           DataHolder.getInstance().stop();
           call.answer();
           if (type==R.id.next) //when it's a just voice call
           {call.addCallListener(new SinchCallListener(this));
           Button dec=(Button) findViewById(R.id.Decline) ;
                   dec.setVisibility(View.GONE);
           }
           else
           {
               call.addCallListener(new SinchVidListner(this));}
           Button dec=(Button) findViewById(R.id.Decline) ;
           dec.setVisibility(View.GONE);
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
        button.setText("Call");
        button.setVisibility(View.GONE);
        aud.setVisibility(View.VISIBLE);
        vid.setVisibility(View.VISIBLE);
    }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endcall(button) ;

    }

@Override
    protected void onPause()
{
    super.onPause();
    endcall(button) ;

}
    @Override
    protected void onStop()
    {
        super.onStop();
        endcall(button) ;
    }


}
