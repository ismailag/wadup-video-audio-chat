package com.example.ismail.wadup;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;

import android.view.View;

import com.firebase.client.Firebase;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SinchVidListner implements VideoCallListener {
    private Activity a ;
    private TextView callState ;
    private Button dec,aud,vid,button ;
    private LinearLayout gl ;
    private LinearLayout gl2;


    public SinchVidListner(Activity act)
    {
        a=act;
        callState = (TextView) a.findViewById(R.id.fullscreen_content);
        dec=(Button) a.findViewById(R.id.Decline) ;
        aud=(Button) a.findViewById(R.id.next) ;
        vid=(Button) a.findViewById(R.id.nextv) ;
        button=(Button) a.findViewById(R.id.call);
    }
    @Override
    public void onVideoTrackAdded(Call call) {

        if (! DataHolder.getInstance().isVidadd())
        {VideoController vc = DataHolder.getInstance().getClient().getVideoController();
            gl= (LinearLayout) a.findViewById(R.id.trust) ;
           gl2=(LinearLayout) a.findViewById(R.id.local) ;
            if (gl!=null)
            { gl.removeAllViews();}
            if (gl2!=null)
            { gl2.removeAllViews(); gl2.setBackgroundColor(Color.BLACK);
            }
            gl2.addView(vc.getLocalView());
            gl.addView(vc.getRemoteView());
            DataHolder.getInstance().setVidadd(true);}
    }

    @Override
    public void onCallProgressing(Call call) {
        callState.setText("Ringing");
    }

    @Override
    public void onCallEstablished(Call call) {
        DataHolder.getInstance().stop();
        AudioController audio =DataHolder.getInstance().getClient().getAudioController() ;
        audio.enableSpeaker();
        a.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        callState.setText("");
        dec.setVisibility(View.GONE);
        aud.setVisibility(View.GONE);
        vid.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);

    }

    @Override
    public void onCallEnded(Call call) {
        DataHolder.getInstance().setCall(null);
        button.setText("Call");
        a.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
       if (gl!=null)
       {gl.removeAllViews();}
        if (gl2!=null)
        {gl2.setBackgroundColor(0x0099cc);
            gl2.removeAllViews();}
        //gl.setBackgroundColor(0x0099cc);
        callState.setText("Call ended");
        DataHolder.getInstance().stop();
        dec.setVisibility(View.GONE);
        aud.setVisibility(View.VISIBLE);
        vid.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);
        DataHolder.getInstance().setVidadd(false);

        if(call.getDetails().getEndCause()== CallEndCause.DENIED)
            Toast.makeText(a,"Call declined",Toast.LENGTH_SHORT).show();
        else if (call.getDetails().getEndCause()==CallEndCause.NO_ANSWER)
            Toast.makeText(a,"No answer",Toast.LENGTH_SHORT).show();
        else if (call.getDetails().getEndCause()==CallEndCause.FAILURE)
            Toast.makeText(a,"Connection failed",Toast.LENGTH_SHORT).show();
        else if (call.getDetails().getEndCause()==CallEndCause.OTHER_DEVICE_ANSWERED)
            Toast.makeText(a,"Destination occupied by another call",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> list) {

    }
}
