package com.example.max_8.mediawithdb.Notifications;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import com.example.max_8.mediawithdb.Activitys.PlayActivity;

public class Notification_Play_Receiver extends BroadcastReceiver {



    //receive here
    @Override
    public void onReceive(Context context, Intent intent) {
           if(!PlayActivity.mediaPlayer.isPlaying()){
               PlayActivity.startMusic();
           }

    }



}
