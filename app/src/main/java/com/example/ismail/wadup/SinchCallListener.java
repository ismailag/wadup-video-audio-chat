package com.example.ismail.wadup;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class SinchCallListener  implements CallListener {
    Activity a ;
    TextView callState ;
    Button dec ;
    Button aud , vid , button ;
    SinchCallListener(Activity hi )
    {
      this.a=hi ;
        callState = (TextView) a.findViewById(R.id.fullscreen_content);
        dec=(Button) a.findViewById(R.id.Decline) ;
        aud=(Button) a.findViewById(R.id.next);
        vid=(Button) a.findViewById(R.id.nextv) ;
        button=(Button) a.findViewById(R.id.call) ;
    }
    @Override
     public void onCallEnded(Call endedCall) {
        //call ended by either party
        DataHolder.getInstance().setCall(null);
        //Button button = (Button) a.findViewById(R.id.call) ;
        button.setText("Call");
        a.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        callState.setText("Call ended");
        DataHolder.getInstance().stop();
        button.setVisibility(View.GONE);
        dec.setVisibility(View.GONE);
        aud.setVisibility(View.VISIBLE);
        vid.setVisibility(View.VISIBLE);
        if(endedCall.getDetails().getEndCause()==CallEndCause.DENIED)
             Toast.makeText(a,"Call declined",Toast.LENGTH_SHORT).show();
        else if (endedCall.getDetails().getEndCause()==CallEndCause.NO_ANSWER)
            Toast.makeText(a,"No answer",Toast.LENGTH_SHORT).show();
        else if (endedCall.getDetails().getEndCause()==CallEndCause.FAILURE)
            Toast.makeText(a,"Connection failed",Toast.LENGTH_SHORT).show();
        else if (endedCall.getDetails().getEndCause()==CallEndCause.OTHER_DEVICE_ANSWERED)
            Toast.makeText(a,"Destination occupied by another call",Toast.LENGTH_LONG).show();

    }
    @Override
    public void onCallEstablished(Call establishedCall) {
        DataHolder.getInstance().stop();
        a.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        callState.setText("Connected");
        dec.setVisibility(View.GONE);
        aud.setVisibility(View.GONE);
        vid.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);
    }
    @Override
    public void onCallProgressing(Call progressingCall) {
        //call is ringing
        callState.setText("Ringing");
    }
    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
    }
}
