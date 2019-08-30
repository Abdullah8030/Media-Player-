package com.example.max_8.mediawithdb.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.max_8.mediawithdb.Activitys.PlayActivity;

public class NotificationPauseReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if(PlayActivity.mediaPlayer.isPlaying()){
            PlayActivity.pauseMusic();
        }


    }



}
