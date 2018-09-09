package com.zeed.zeemp.services;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Binder;
import android.util.Log;

import com.zeed.zeemp.models.Audio;

import java.io.IOException;

/**
 * Created by zeed on 02/09/2018.
 */

public class MediaPlayerBindService extends Binder {

    private MediaPlayer mediaPlayer;

    public Context getContext() {
        return context;
    }

    private Context context;

    private void setContext (Context context) {
        this.context = context;
    }


    public void startAudio(Audio audio) {


            try {
                if (mediaPlayer == null ) {
                    initAudioPlayer(audio);
                    mediaPlayer.start();
                } else {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(audio.getData());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                Log.e("ERROR", "startAudio: Error occured while attempting to play audio",e );
            }

    }

    private void initAudioPlayer(Audio audio) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audio.getData());
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e("Error", "initAudioPlayer: Error occurred while initialising media player ",e );
        }

    }

}
