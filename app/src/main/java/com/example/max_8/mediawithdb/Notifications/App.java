package com.example.max_8.mediawithdb.Notifications;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
     //psfs
    public static final String CHANNEL_11_ID = "channel11";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationMetiod();
    }
    //here settings channels
    private void createNotificationMetiod() {
       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

           NotificationChannel channel = new NotificationChannel(CHANNEL_11_ID,
                   "Channel 11", NotificationManager.IMPORTANCE_HIGH);
           channel.setDescription("This is channel 11");



       NotificationManager manager = getSystemService(NotificationManager.class);
       manager.createNotificationChannel(channel);
       }


    }//END
}
