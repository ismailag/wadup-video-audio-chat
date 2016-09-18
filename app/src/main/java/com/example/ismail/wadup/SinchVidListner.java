package com.example.ismail.wadup;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;

import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;
import com.sinch.android.rtc.video.VideoScalingType;

import android.app.Service;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class SinchVidListner implements VideoCallListener {
    private Activity a ;
    private TextView callState ;
    private Button dec ;
    private LinearLayout gl ;
    private LinearLayout gl2;


    public SinchVidListner(Activity act)
    {
        a=act;
        callState = (TextView) a.findViewById(R.id.fullscreen_content);
        dec=(Button) a.findViewById(R.id.Decline) ;

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

    }

    @Override
    public void onCallEnded(Call call) {
        DataHolder.getInstance().setCall(null);
        Button button = (Button) a.findViewById(R.id.call) ;
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
        DataHolder.getInstance().setVidadd(false);

    }

    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> list) {

    }
}
