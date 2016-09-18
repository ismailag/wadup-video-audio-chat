package com.example.ismail.wadup;

import android.app.Activity;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.video.VideoController;

public class SinchCallClientListener implements CallClientListener {
    Activity a ;
    Call call ;
    public SinchCallClientListener(Activity activity) {
        a=activity ;
    }

    @Override
    public void onIncomingCall(CallClient callClient, Call incomingCall) {
        Button button = (Button) a.findViewById(R.id.call) ;
        //call = incomingCall;
        //call.answer();
        if (! incomingCall.getDetails().isVideoOffered())
        {incomingCall.addCallListener(new SinchCallListener(a));}
        else
        {
            DataHolder.getInstance().setVidadd(false);
            incomingCall.addCallListener(new SinchVidListner(a));
        }
        DataHolder.getInstance().setCall(incomingCall);
        TextView callState = (TextView) a.findViewById(R.id.fullscreen_content);
        callState.setText("Ringing");
        button.setText("Answer");
        Button dec = (Button) a.findViewById(R.id.Decline) ;
        dec.setVisibility(View.VISIBLE);
        DataHolder.getInstance().play();

    }
}