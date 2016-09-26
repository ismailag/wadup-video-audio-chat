package com.example.ismail.wadup;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
public class SinchCallClientListener implements CallClientListener {
    Activity a ;
    Call call ;
    public SinchCallClientListener(Activity activity) {
        a=activity ;
    }

    @Override
    public void onIncomingCall(CallClient callClient, Call incomingCall) {

        DataHolder.getInstance().setCall(incomingCall);
       if (DataHolder.getInstance().isDone())
       {
           a=DataHolder.getInstance().getAct() ;
           if (! incomingCall.getDetails().isVideoOffered())
           {incomingCall.addCallListener(new SinchCallListener(a));}
           else
           {
               DataHolder.getInstance().setVidadd(false);
               incomingCall.addCallListener(new SinchVidListner(a));
           }

           Button button = (Button) a.findViewById(R.id.call) ;
        Button aud =(Button) a.findViewById(R.id.next) ;
        Button vid =(Button) a.findViewById(R.id.nextv) ;
        TextView callState = (TextView) a.findViewById(R.id.fullscreen_content);
        callState.setText("Ringing");
        button.setText("Answer");
        Button dec = (Button) a.findViewById(R.id.Decline) ;
        aud.setVisibility(View.GONE);
        vid.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);
        dec.setVisibility(View.VISIBLE);}
        else
       {
           Intent intent = new Intent(a.getApplicationContext(), FullscreenActivity.class);
           intent.putExtra("recipientId",incomingCall.getRemoteUserId());
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           a.startActivity(intent);


       }

        DataHolder.getInstance().play();

    }
}