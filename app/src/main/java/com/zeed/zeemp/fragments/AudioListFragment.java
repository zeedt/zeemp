package com.zeed.zeemp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zeed.zeemp.R;
import com.zeed.zeemp.adapters.AudioPlayerAdapter;
import com.zeed.zeemp.models.Audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AudioListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AudioListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudioListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AudioPlayerAdapter.RecyclerItemOnClickListener {

    final List<Audio> audioList = new ArrayList<>();


    AudioPlayerAdapter audioPlayerAdapter;
    RecyclerView recyclerView;
    private Audio currentlyPlayed;
    Integer index;
    private TextView audioTitleTextView;

    ImageView imageView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OnFragmentInteractionListener mListener;
    RecyclerView.LayoutManager layoutManager;

    private Integer playValue = 0;

    public AudioListFragment() {
        // Required empty public constructor
    }


    public static AudioListFragment newInstance(String param1, String param2) {
        AudioListFragment fragment = new AudioListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_audio_list, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("ZeeMp");

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        audioPlayerAdapter = new AudioPlayerAdapter(getActivity().getApplicationContext(),audioList,this);

        recyclerView.setAdapter(audioPlayerAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        audioPlayerAdapter.notifyDataSetChanged();
        audioTitleTextView = (TextView) view.findViewById(R.id.audio_title);

        imageView = (ImageView) view.findViewById(R.id.play_or_pause);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentlyPlayed == null) {
                    currentlyPlayed = audioList.get(0);
                    try {
                        mListener.playMusic(currentlyPlayed);
                        setPlayOrPause();
                    } catch (IOException e) {
                        Log.d("Error", "onClick: Error occured while playing the first loaded music");
                    }
                    return;
                }

                Log.d("Hello", "onClick: ");
                playValue = mListener.playOrPauseMusic();
                setPlayOrPause();
            }
        });

        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        showDetailsFragment();
        return view;
    }

    private void showDetailsFragment() {
        audioTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoDetailsFragment();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scrolling, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem menuItem = menu.findItem(R.id.search);

        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                audioPlayerAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void click(Audio audio) throws IOException {
        if (audio != null) {
            TextView textView = getView().findViewById(R.id.audio_title);
            textView.setText(audio.getTitle());
            mListener.playMusic(audio);
            currentlyPlayed = audio;
            setPlayOrPause();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] cols = {MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ALBUM_ID
        };

        return new CursorLoader(getActivity().getApplicationContext(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cols,null,null,null);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().restartLoader(0,null,this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        fetchMusicWithContentProvider(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("", "onLoaderReset: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void fetchMusicWithContentProvider(Cursor cursor) {
        audioList.clear();
        while (cursor.moveToNext()){
            Audio audio = new Audio();
            audio.setTitle(cursor.getString(0));
            audio.setDuration(cursor.getString(1));
            audio.setData(cursor.getString(2));
            audio.setArtist(cursor.getString(3));
            audio.setAlbum(cursor.getString(4));
            audio.setAlbumId(cursor.getString(5));
            audioList.add(audio);
        }
        audioPlayerAdapter.notifyDataSetChanged();
        if (currentlyPlayed != null) {
            index = audioList.indexOf(currentlyPlayed);
            layoutManager.scrollToPosition(index);
        } else {
            audioTitleTextView.setText(audioList.get(0).getTitle());
        }
    }

    public void setPlayOrPause() {
        if (mListener.isMusicPlaying()) {
            imageView.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
        } else {
            imageView.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
        void playMusic(Audio audio) throws IOException;
        Integer playOrPauseMusic();
        boolean isMusicPlaying();
        void gotoDetailsFragment();
    }
}
