package com.zeed.zeemp.activities;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.zeed.zeemp.R;
import com.zeed.zeemp.adapters.AudioPlayerAdapter;
import com.zeed.zeemp.fragments.AudioListFragment;
import com.zeed.zeemp.fragments.DetailsFragment;
import com.zeed.zeemp.fragments.StatelessFragment;
import com.zeed.zeemp.models.Audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AudioListFragment.OnFragmentInteractionListener, DetailsFragment.OnFragmentInteractionListener {

    final List<Audio> audioList = new ArrayList<>();

    RecyclerView recyclerView;

    public AudioPlayerAdapter audioPlayerAdapter;

    public MediaPlayer mediaPlayer = new MediaPlayer();

    public FragmentTransaction fragmentTransaction;

    public AudioListFragment audioListFragment;

    public DetailsFragment detailsFragment;

    StatelessFragment statelessFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void playMusic(Audio audio) throws IOException {
        statelessFragment.currentlyPlayed = audio;
        mediaPlayer.reset();
        mediaPlayer.setDataSource(audio.getData());
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    @Override
    public Integer playOrPauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            return 0;
        } else {
            int currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(currentPosition);
            mediaPlayer.start();
            return 1;
        }
    }

    @Override
    public boolean isMusicPlaying() {
        if (mediaPlayer.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void gotoDetailsFragment() {
        detailsFragment = new DetailsFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment,detailsFragment);
        fragmentTransaction.commit();

        fragmentTransaction.addToBackStack("DetailsFragment");

    }


}
