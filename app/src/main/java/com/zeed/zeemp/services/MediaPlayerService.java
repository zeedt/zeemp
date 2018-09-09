package com.zeed.zeemp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.Pair;
import android.util.Log;

import com.zeed.zeemp.R;
import com.zeed.zeemp.activities.MainActivity;
import com.zeed.zeemp.models.Audio;


/**
 * Created by zeed on 01/09/2018.
 */

public class MediaPlayerService extends Service {
    static final int NOTIFICATION_ID = 543;
    MediaPlayer mediaPlayer;

    public Audio currentlyPlayed;

    private boolean isPlaying = false;

    MediaPlayerBindService mediaPlayerBindService = new MediaPlayerBindService();

    LocalBinder localBinder = new LocalBinder();

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        try {
            String action = intent.getStringExtra("action");
            if ("play".equals(action)) {
                Audio audio = (Audio) intent.getSerializableExtra("currentlyPlayed");
                playMedia(audio);
            }
            if ("playorpause".equals(action)) {
                playOrPauseMedia();
            }

            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.setAction("Ation");  // A string containing the action name
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_arrow_black_24dp);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setTicker(getResources().getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_pause_circle_outline_black_24dp)
//                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(contentPendingIntent)
                    .setOngoing(true)
//                .setDeleteIntent(contentPendingIntent)  // if needed
                    .build();
            notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
            startForeground(NOTIFICATION_ID, notification);

        } catch (Exception e) {
            Log.d("Error", "onStart: Error occurred due to ", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    public void playMedia(Audio audio) throws Exception {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(audio.getData());
        mediaPlayer.prepare();
        mediaPlayer.start();
        currentlyPlayed = audio;
        isPlaying = true;
    }

    public void playOrPauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        } else {
            int currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(currentPosition);
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    public Pair<Integer, Integer> getDurationAndPosition(){
        if (mediaPlayer == null) {
            return  null;
        }

        return new Pair<>(mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition());
    }

}
