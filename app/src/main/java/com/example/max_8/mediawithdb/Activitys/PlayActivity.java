package com.example.max_8.mediawithdb.Activitys;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max_8.mediawithdb.Model.UploadSongs;
import com.example.max_8.mediawithdb.Notifications.NotificationPauseReceiver;
import com.example.max_8.mediawithdb.Notifications.Notification_Play_Receiver;
import com.example.max_8.mediawithdb.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.max_8.mediawithdb.Notifications.App.CHANNEL_11_ID;

public class PlayActivity extends AppCompatActivity {

    private static SeekBar MseekBar;
    private static int newPosition;
    private static ImageView nextImage,previousImage;
    private TextView songName,ArtistName;
    private static TextView timeStart;
    private static TextView timeEnd;
    private UploadSongs value;
    private static SimpleDateFormat timeFormat;
    private static Thread thread;
    private Bundle bundle;
    private ProgressDialog mProgress;
    private Uri uri;
    private CircularImageView circularImageView;
    private static PlayActivity mn;
    private static Bitmap bitmap;

    private static NotificationManagerCompat notificationCompat;
    private MediaSessionCompat mediaSessionCompat;
    private static Button play_button;
    public static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
           setUp();
           setUpBundole();
           setUpNot();
        play_button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(mediaPlayer.isPlaying()){
                //stop and give option to start again
                pauseMusic();
            }else{
                startMusic();
            }

        }
    });
        MseekBar.setMax(mediaPlayer.getDuration());
        MseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //change from user
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration        = mediaPlayer.getDuration();
                timeStart.setText(timeFormat.format(new Date(currentPosition)));
                timeEnd.setText(timeFormat.format(new Date(duration - currentPosition)));

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setUpBundole(){

        if( bundle != null ) {

            timeEnd.setText(bundle.getString("duration"));
            songName.setText(bundle.getString("title"));
            ArtistName.setText(bundle.getString("artist"));
            Picasso.with(PlayActivity.this).load(bundle.getString("images")).placeholder(R.drawable.audio).into(circularImageView);


            value = (UploadSongs) bundle.get("List");

            mediaPlayer = new MediaPlayer();
            uri = Uri.parse(value.getSongLink());
            Thread thread = new Thread(){

                @Override
                public void run() {
                    try {
                        mediaPlayer = MediaPlayer.create(PlayActivity.this,uri);
                        Thread.sleep(500);
                        mProgress.dismiss();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            };thread.start();
        }//end here
    }
    private void setUp(){

        MseekBar = (SeekBar) findViewById(R.id.seekBar);
        songName = (TextView) findViewById(R.id.song_name_ID);
        ArtistName = (TextView) findViewById(R.id.Artist_name_ID);
        timeStart = (TextView) findViewById(R.id.textTimeStart);
        timeEnd = (TextView) findViewById(R.id.textEndMusic);
        play_button = (Button) findViewById(R.id.play_id);
        circularImageView = (CircularImageView)findViewById(R.id.imageView);
        timeFormat = new SimpleDateFormat("mm:ss");
        nextImage = (ImageView)findViewById(R.id.next15Second);
        previousImage =(ImageView)findViewById(R.id.prev15Second);

         bundle = getIntent().getExtras();
        mProgress = new ProgressDialog(PlayActivity.this);
        mProgress.setMessage("loading ..");
        mProgress.setCancelable(false);
        mProgress.show();


        nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next15();
            }
        });
        previousImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous15();
            }
        });

    }
    private void setUpNot(){

        notificationCompat = NotificationManagerCompat.from(this);
        mn = PlayActivity.this;
        mediaSessionCompat = new MediaSessionCompat(this,"tag");

    }
    public static void pauseMusic() {

        if (mediaPlayer != null) {
            mediaPlayer.pause();
            play_button.setText("Play");
        }
    }
    public static void startMusic()  {

        if(mediaPlayer != null){
            mediaPlayer.start();
            updateThread();
            play_button.setText("Pause");
        }

    }

    private static void updateThread() {
//lecture 115
        thread = new Thread() {

            @Override
            public void run() {
                try {
                    while (mediaPlayer != null && mediaPlayer.isPlaying()) {

                            Thread.sleep(50);

                        mn.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    newPosition = mediaPlayer.getCurrentPosition();
                                    int newMax = mediaPlayer.getDuration();
                                    MseekBar.setMax(newMax);
                                    MseekBar.setProgress(newPosition);


                                    //update the text
                                    timeStart.setText(timeFormat.format(new Date(newPosition)));
                                    timeEnd.setText(timeFormat.format(new Date(newMax - newPosition)));
                                }catch (Exception e){
                                    thread.currentThread().interrupt(); // restore interrupted status
                                }
                            }
                        });
                    }//end for

                } catch (Exception e) {
                    e.printStackTrace();
					notificationCompat.cancelAll();
                }
            }

        };
        thread.start();

    }

    private void notificationMethod(){


        //here play
        Intent playButton = new Intent(this, Notification_Play_Receiver.class);
        PendingIntent Actioncontent = PendingIntent.getBroadcast(
                this
                , 0
                , playButton
                , 0);//change with new message
        //----------------------------

        //here pause
        Intent puseButton = new Intent(this, NotificationPauseReceiver.class);
        PendingIntent Actioncontent2 = PendingIntent.getBroadcast(
                this
                , 0
                , puseButton
                , 0);//change with new message
        //--------------------------

        Picasso.with(PlayActivity.this).load(bundle.getString("images")).placeholder(R.drawable.audio).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmapp, Picasso.LoadedFrom from) {
                bitmap = bitmapp;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        if(bitmap == null){
            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.audio);
        }


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_11_ID)
                .setSmallIcon(R.drawable.guitar)
                .setContentTitle(bundle.getString("title"))
                .setContentText(bundle.getString("artist"))
                .setLargeIcon(bitmap)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.play, "Play", Actioncontent)
                .addAction(R.drawable.pause,"pause",Actioncontent2)
                .setStyle(new android.support.v4.media.app.NotificationCompat
                        .MediaStyle()
                        .setShowActionsInCompactView(0,1)
                        .setMediaSession(mediaSessionCompat.getSessionToken())

                )
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
                .build();
        notificationCompat.notify(1, notification);

    }

    private void next15(){


        if(newPosition < mediaPlayer.getDuration())
        {
            mediaPlayer.seekTo(newPosition + 15519);

            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            timeStart.setText(timeFormat.format(new Date(currentPosition)));
            timeEnd.setText(timeFormat.format(new Date(duration - currentPosition)));
        }else{
            mediaPlayer.seekTo(0);

            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            timeStart.setText(timeFormat.format(new Date(currentPosition)));
            timeEnd.setText(timeFormat.format(new Date(duration - currentPosition)));

        }
    }

    private void previous15(){

         if(newPosition < 15519){


            mediaPlayer.seekTo( 15519 - newPosition);

            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            timeStart.setText(timeFormat.format(new Date(currentPosition)));
            timeEnd.setText(timeFormat.format(new Date(duration - currentPosition)));
        }else{


            mediaPlayer.seekTo(newPosition - 15519);

            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            timeStart.setText(timeFormat.format(new Date(currentPosition)));
            timeEnd.setText(timeFormat.format(new Date(duration - currentPosition)));
        }




    }
    @Override
    protected void onStop() {
        super.onStop();
        notificationMethod();
        ShowListActivity activity = new ShowListActivity();
        if(activity.Ischeack){
         if(mediaPlayer != null && mediaPlayer.isPlaying()){

             try {
                 notificationCompat.cancelAll();
                 thread.interrupt();
                 thread = null;
                 mediaPlayer.stop();
                 mediaPlayer.release();
                 mediaPlayer = null;

             }catch (NullPointerException e){}

        }
      }
    }

        @Override
    protected void onDestroy() {
            super.onDestroy();
            if(mediaPlayer != null && mediaPlayer.isPlaying()) {
                try {
                    notificationCompat.cancelAll();
                    thread.interrupt();
                    thread = null;
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                } catch (NullPointerException e) { }
            }
    }





}
