package com.zeed.zeemp.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.zeed.zeemp.R;
import com.zeed.zeemp.fragments.AudioListFragment;
import com.zeed.zeemp.fragments.DetailsFragment;
import com.zeed.zeemp.fragments.StatelessFragment;
import com.zeed.zeemp.models.Audio;
import com.zeed.zeemp.services.MediaPlayerBindService;
import com.zeed.zeemp.services.MediaPlayerService;
import com.zeed.zeemp.services.MediaPlayerService.LocalBinder;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AudioListFragment.OnFragmentInteractionListener, DetailsFragment.OnFragmentInteractionListener {


    public FragmentTransaction fragmentTransaction;

    public AudioListFragment audioListFragment;

    public DetailsFragment detailsFragment;

    StatelessFragment statelessFragment;

    public MediaPlayerBindService mediaPlayerBindService;
    private MediaPlayerService mediaPlayerServiceBound;

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            LocalBinder localBinder = (LocalBinder)iBinder;
            mediaPlayerServiceBound = localBinder.getService();
            audioListFragment.setPlayOrPause();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindAudioService();
        statelessFragment =
                (StatelessFragment) getSupportFragmentManager()
                        .findFragmentByTag("headless");
        if (statelessFragment == null) {
            statelessFragment = new StatelessFragment();
            getSupportFragmentManager().beginTransaction().add(statelessFragment,"headless");
        }

        if (savedInstanceState != null) {
            getSupportFragmentManager().executePendingTransactions();
        }
        audioListFragment = new AudioListFragment();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment,audioListFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void playMusic(Audio audio) throws IOException {
        statelessFragment.currentlyPlayed = audio;
        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.putExtra("currentlyPlayed", audio);
        intent.putExtra("action", "play");
        startService(intent);
    }

    @Override
    public Integer playOrPauseMusic() {

        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.putExtra("action", "playorpause");
        startService(intent);
        return 0;
    }

    @Override
    public boolean isMusicPlaying() {
        return mediaPlayerServiceBound.isPlaying();
    }

    @Override
    public void gotoDetailsFragment() {
        detailsFragment = new DetailsFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment,detailsFragment);
        fragmentTransaction.commit();

        fragmentTransaction.addToBackStack("DetailsFragment");

    }

    @Override
    public Audio getCurrentlyPlayed() {
        return  (mediaPlayerServiceBound == null) ? null : mediaPlayerServiceBound.currentlyPlayed;
    }

    private void bindAudioService()
    {
        if(mediaPlayerBindService == null) {
            Intent intent = new Intent(MainActivity.this, MediaPlayerService.class);

            // Below code will invoke serviceConnection's onServiceConnected method.
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }
    private void unBindAudioService()
    {
            unbindService(serviceConnection);

    }

    @Override
    public void onDestroy(){
        unBindAudioService();
        super.onDestroy();
    }

    @Override
    public Pair<Integer, Integer> getDurationAndPosition() {
        if (mediaPlayerServiceBound == null) {
            return null;
        }
        return mediaPlayerServiceBound.getDurationAndPosition();
    }

    @Override
    public void seekToPosition(int seekBarPosition) {
        mediaPlayerServiceBound.seekToPosition(seekBarPosition);
    }

    @Override
    public boolean hasMusicCompleted() {
        return mediaPlayerServiceBound.isCompleted();
    }

    public void doNothing(View view) {
        return;
    }


}
