package com.zeed.zeemp.activities;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.zeed.zeemp.R;
import com.zeed.zeemp.adapters.AudioPlayerAdapter;
import com.zeed.zeemp.fragments.AudioListFragment;
import com.zeed.zeemp.models.Audio;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AudioListFragment.OnFragmentInteractionListener {

    final List<Audio> audioList = new ArrayList<>();

    RecyclerView recyclerView;

    AudioPlayerAdapter audioPlayerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        AudioListFragment audioListFragment = new AudioListFragment();
        fragmentTransaction.replace(R.id.fragment,audioListFragment);
        fragmentTransaction.commit();
    }


//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        String[] cols = {MediaStore.Audio.Media.DISPLAY_NAME,
//                MediaStore.Audio.Media.DURATION,
//                MediaStore.Audio.Media.DATA,
//                MediaStore.Audio.Media.ARTIST,
//                MediaStore.Audio.AudioColumns.ALBUM,
//                MediaStore.Audio.AudioColumns.ALBUM_ID
//        };
//
//        return new CursorLoader(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cols,null,null,null);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        fetchMusicWithContentProvider(cursor);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }
//
//
//    public void fetchMusicWithContentProvider(Cursor cursor) {
//        audioList.clear();
//        while (cursor.moveToNext()){
//            Audio audio = new Audio();
//            audio.setTitle(cursor.getString(0));
//            audio.setDuration(cursor.getString(1));
//            audio.setData(cursor.getString(2));
//            audio.setArtist(cursor.getString(3));
//            audio.setAlbum(cursor.getString(4));
//            audio.setAlbumId(cursor.getString(5));
//            audioList.add(audio);
//        }
//        audioPlayerAdapter.notifyDataSetChanged();
//        Log.d("TAG", "fetchMusicWithContentProvider: Fetched");
//    }
//
//    public List<Audio> getAudioList() {
//        return audioList;
//    }
//
//    @Override
//    public void click(Audio audio) {
//        throw new UnsupportedOperationException("");
//    }
//
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
