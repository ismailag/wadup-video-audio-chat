package com.example.ismail.wadup;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class SinchCallListener  implements CallListener {
    Activity a ;
    TextView callState ;
    Button dec ;
    SinchCallListener(Activity hi )
    {
      this.a=hi ;
        callState = (TextView) a.findViewById(R.id.fullscreen_content);
        dec=(Button) a.findViewById(R.id.Decline) ;
    }
    @Override
     public void onCallEnded(Call endedCall) {
        //call ended by either party
        DataHolder.getInstance().setCall(null);
        Button button = (Button) a.findViewById(R.id.call) ;
        button.setText("Call");
        a.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        callState.setText("Call ended");
        DataHolder.getInstance().stop();
        dec.setVisibility(View.GONE);
    }
    @Override
    public void onCallEstablished(Call establishedCall) {
        DataHolder.getInstance().stop();
        a.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        callState.setText("Connected");
        dec.setVisibility(View.GONE);

    }
    @Override
    public void onCallProgressing(Call progressingCall) {
        //call is ringing
        callState.setText("Ringing");
    }
    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        //don't worry about this right now
    }
}
