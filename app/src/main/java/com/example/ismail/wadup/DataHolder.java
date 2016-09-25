package com.example.ismail.wadup;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;

import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;

import java.io.IOException;

public class DataHolder {
    private Call call;
    private MediaPlayer player ;
    private int type ;
    private SinchClient client ;
    private boolean vidadd ;
    private String  callerid ;
    private String recepient ;
    private boolean done=false;
    private Activity act ;

    public Activity getAct() {
        return act;
    }

    public void setAct(Activity act) {
        this.act = act;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setids(String c , String r)
    {callerid=c ;recepient=r ;
    }
    public String getCallerid()
    {
        return callerid ;
    }
    public String getRecepient()
    {
        return recepient;
    }
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
    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}

}
