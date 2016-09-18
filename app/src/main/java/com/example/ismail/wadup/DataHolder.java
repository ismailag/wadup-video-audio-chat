package com.example.ismail.wadup;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;

import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;

public class DataHolder {
    private Call call;
    private MediaPlayer player ;
    private int type ;
    private SinchClient client ;
    private boolean vidadd ;

    public boolean isVidadd() {
        return vidadd;
    }

    public void setVidadd(boolean vidadd) {
        this.vidadd = vidadd;
    }

    public void setClient(SinchClient client) {
        this.client = client;
    }
    public SinchClient getClient() {
        return client;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
    public MediaPlayer getplayer() {
        return this.player;
    }
    public void setplayer(MediaPlayer player) {
        this.player = player;
    }
public void play (){
    if (! player.isPlaying())
    {
        player.start();
    }
}
    public void stop ()
    {
        if (player.isPlaying())
        {
            player.pause();
            player.seekTo(0);
        }
    }
    public Call getCall() {return call;}
    public void setCall(Call data) {this.call = data;}
    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}

}
