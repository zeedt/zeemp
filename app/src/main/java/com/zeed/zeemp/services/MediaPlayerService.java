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
import com.zeed.zeemp.models.AudioWrapper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zeed on 01/09/2018.
 */

public class MediaPlayerService extends Service {
    static final int NOTIFICATION_ID = 543;
    MediaPlayer mediaPlayer;

    public Audio currentlyPlayed;

    private boolean isPlaying = false;

    private int currentlyPlayedIndex = 0;

    private boolean completed = false;

    LocalBinder localBinder = new LocalBinder();

    private List<Audio> audioList = new ArrayList<>();

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
                List<Audio> audios = (List<Audio>) intent.getSerializableExtra("audioLists");
                playMedia(audio);
            }
            if ("playorpause".equals(action)) {
                playOrPauseMedia();
            }
            if ("updateAudioList".equals(action)) {
                try {
                    audioList = (audioList == null) ? new ArrayList<Audio>() : audioList;
                    audioList.clear();
                    AudioWrapper audioWrapper = (AudioWrapper) intent.getSerializableExtra("audioListWrapper");
                    if (audioWrapper == null || audioWrapper.getAudioList() == null  || audioWrapper.getAudioList().size() == 0) {
                        return;
                    }

                    audioList.addAll(audioWrapper.getAudioList());

                    currentlyPlayedIndex = audioList.indexOf(currentlyPlayed);
                } catch (Exception e) {
                    Log.e("ERROR: ", "onStart: Error occured due to ",e );
                }

            }

            if ("playNext".equals(action)) {
                if (audioList == null || audioList.size() == 0 || audioList.size() < (currentlyPlayedIndex+1)) {
                    return;
                }
                playMedia(audioList.get(currentlyPlayedIndex+1));
                currentlyPlayedIndex +=1;
            }

            if ("playPrevious".equals(action)) {
                if (audioList == null || audioList.size() == 0 || currentlyPlayedIndex-1 < 0) {
                    return;
                }
                playMedia(audioList.get(currentlyPlayedIndex-1));
                currentlyPlayedIndex -=1;
            }


            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    completed = true;
                }
            });

            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.setAction("Ation");  // A string containing the action name
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Intent playOrPauseIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
            playOrPauseIntent.putExtra("action","playorpause");
            playOrPauseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent playOrPausePendingItent = PendingIntent.getService(this,4, playOrPauseIntent,0);

            Intent nextIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
            nextIntent.putExtra("action","playNext");
            nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent nextPendingItent = PendingIntent.getService(this,2, nextIntent,0);

            Intent previousIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
            previousIntent.putExtra("action","playPrevious");
            previousIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent previousPendingItent = PendingIntent.getService(this,3, previousIntent,0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_arrow_black_24dp);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setTicker(getResources().getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.app_name))
                    .setSmallIcon(R.drawable.zeemp)
                    .addAction(R.drawable.ic_skip_previous_black_24dp,"Prev", previousPendingItent)
                    .addAction(R.drawable.ic_play_arrow_black_24dp,"Play", playOrPausePendingItent)
                    .addAction(R.drawable.ic_skip_next_black_24dp,"Next", nextPendingItent)
//                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(contentPendingIntent)
                    .setOngoing(true)
//                .setDeleteIntent(contentPendingIntent)  // if needed
                    .build();
            notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
            startForeground(NOTIFICATION_ID, notification);

//            notification.actions[0].actionIntent.

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
        completed = false;
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
        completed = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isCompleted() {
        return completed;
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

    public void seekToPosition(int seekBarPosition) {

        try {
            if (mediaPlayer != null) {

                int position = (seekBarPosition * mediaPlayer.getDuration()) / 100;

                mediaPlayer.seekTo(position);
                completed = false;

            }
        } catch (IllegalStateException e) {
            Log.e("Error", "seekToPosition: Error occurred while seeking to position due to ", e);
        }

    }

}
