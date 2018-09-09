package com.zeed.zeemp.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zeed.zeemp.R;
import com.zeed.zeemp.adapters.AudioPlayerAdapter;
import com.zeed.zeemp.models.Audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AudioListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AudioPlayerAdapter.RecyclerItemOnClickListener {

    final List<Audio> audioList = new ArrayList<>();


    AudioPlayerAdapter audioPlayerAdapter;
    RecyclerView recyclerView;
    private Audio currentlyPlayed;
    Integer index = 0;
    public TextView audioTitleTextView;
    public TextView albumTitleTextView;

    private boolean handlerRunning = false;

    private SeekBar seekBar;

    ImageView playOrPause;

    private OnFragmentInteractionListener mListener;
    RecyclerView.LayoutManager layoutManager;

    Handler handler = new Handler();


    ConstraintLayout bottomSheet;

    ImageView bottomModalSheetPlayOrPause;
    ImageView bottomModalSheetPrevious;
    ImageView bottomModalSheetNext;

    BottomSheetBehavior sheetBehavior;

    private static final int DELAY_TIME = 100;

    private Integer playValue = 0;

    public AudioListFragment() {
        // Required empty public constructor
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

        assignPlayOrPauseButtonModalSheetButtonsToVariable(view);
        assignNextButtonModalSheetButtonsToVariable(view);
        assignPreviousButtonModalSheetButtonsToVariable(view);

        recyclerView.setAdapter(audioPlayerAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        audioPlayerAdapter.notifyDataSetChanged();

        assignBottomModalSheetSeekBarToVariable(view);

        audioTitleTextView = (TextView) view.findViewById(R.id.audio_title);
        audioTitleTextView.setSelected(true);
        albumTitleTextView = (TextView) view.findViewById(R.id.album_title);
        currentlyPlayed = mListener.getCurrentlyPlayed();
        setCurrentlyPlayedTitle(currentlyPlayed);
        playOrPause = (ImageView) view.findViewById(R.id.play_or_pause);

        playOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentlyPlayed == null) {
                    currentlyPlayed = audioList.get(0);
                    try {
                        mListener.playMusic(currentlyPlayed);
                        audioTitleTextView.setText(currentlyPlayed.getTitle());
                        albumTitleTextView.setText(currentlyPlayed.getAlbum());
                        index = 0;
                        updateProgressBar();
                    } catch (IOException e) {
                        Log.d("Error", "onClick: Error occured while playing the first loaded music");
                    }
                    return;
                }

                Log.d("Hello", "onClick: ");
                playValue = mListener.playOrPauseMusic();
                updateProgressBar();
            }
        });


        bottomSheet = (ConstraintLayout) view.findViewById(R.id.bottom_sheet);

        sheetBehavior = BottomSheetBehavior.from(bottomSheet);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        showBottomModalSheet();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void showBottomModalSheet() {
        audioTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
            audioTitleTextView.setText(audio.getTitle());
            albumTitleTextView.setText(audio.getAlbum());
            mListener.playMusic(audio);
            currentlyPlayed = audio;
            index = audioList.indexOf(currentlyPlayed);
            updateProgressBar();
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
        currentlyPlayed = mListener.getCurrentlyPlayed();
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
            audioTitleTextView.setText(currentlyPlayed.getTitle());
            albumTitleTextView.setText(currentlyPlayed.getAlbum());
        } else {
            if (audioList == null || audioList.size() == 0) {
                return;
            }
            audioTitleTextView.setText(audioList.get(0).getTitle());
            albumTitleTextView.setText(audioList.get(0).getAlbum());
        }
    }

    public void setPlayOrPause() {
        if (mListener.isMusicPlaying()) {
            playOrPause.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
            bottomModalSheetPlayOrPause.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
        } else {
            playOrPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            bottomModalSheetPlayOrPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
        void playMusic(Audio audio) throws IOException;
        Integer playOrPauseMusic();
        boolean isMusicPlaying();
        void gotoDetailsFragment();
        Audio getCurrentlyPlayed();
        Pair<Integer,Integer> getDurationAndPosition();
    }

    public void setCurrentlyPlayedTitle(Audio audio) {

        if (audio == null) {
            return;
        }
        audioTitleTextView.setText(audio.getTitle());
        albumTitleTextView.setText(audio.getAlbum());
    }

    public void playOrPauseAudioInBottomModalSheet() {

    }

    public void assignPlayOrPauseButtonModalSheetButtonsToVariable(View view) {
        bottomModalSheetPlayOrPause = view.findViewById(R.id.play_or_pause_bms);

        bottomModalSheetPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (currentlyPlayed == null && audioList != null && audioList.size() > 0) {
                        currentlyPlayed = audioList.get(0);
                        mListener.playMusic(currentlyPlayed);
                        audioTitleTextView.setText(currentlyPlayed.getTitle());
                        albumTitleTextView.setText(currentlyPlayed.getAlbum());
                        index = 0;
                    } else {
                        mListener.playOrPauseMusic();
                    }
                    updateProgressBar();
                } catch (Exception e) {

                }
            }
        });
    }
    public void assignNextButtonModalSheetButtonsToVariable(View view) {
        bottomModalSheetNext = view.findViewById(R.id.next_audio);

        bottomModalSheetNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioList == null || audioList.size() < 1) {
                    return;
                }
                if (audioList.size() <= (index+1) ) {
                    return;
                }
                try {
                    currentlyPlayed = audioList.get(index+1);
                    index = index + 1;
                    mListener.playMusic(currentlyPlayed);
                    audioTitleTextView.setText(currentlyPlayed.getTitle());
                    albumTitleTextView.setText(currentlyPlayed.getAlbum());
                    updateProgressBar();
                } catch (IOException e) {
                    Log.e("Error", "onClick: Error occured while playing the next music");
                }
            }
        });
    }
    public void assignPreviousButtonModalSheetButtonsToVariable(View view) {
        bottomModalSheetPrevious = view.findViewById(R.id.prev_audio);

        bottomModalSheetPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioList == null || audioList.size() < 1 || index == 0) {
                    return;
                }
                if ((index-1) < 0) {
                    return;
                }
                try {
                    currentlyPlayed = audioList.get(index-1);
                    index = index - 1;
                    mListener.playMusic(currentlyPlayed);
                    audioTitleTextView.setText(currentlyPlayed.getTitle());
                    albumTitleTextView.setText(currentlyPlayed.getAlbum());
                    updateProgressBar();
                } catch (IOException e) {
                    Log.e("Error", "onClick: Error occured while playing the next music");
                }
            }
        });
    }


    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            setPlayOrPause();
            handlerRunning = true;
            Pair<Integer,Integer> durationAndPosition = mListener.getDurationAndPosition();
            if (durationAndPosition != null) {
                int seekBarPosition = (durationAndPosition.second * 100) / durationAndPosition.first;

                if(android.os.Build.VERSION.SDK_INT >= 11){
                    ObjectAnimator animation = ObjectAnimator.ofInt(seekBar, "progress", seekBar.getProgress(), seekBarPosition * 100);
                    animation.setDuration(1000); // 0.5 second
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();
                } else {
                    seekBar.setProgress(seekBarPosition);
                }
            }
            handler.postDelayed(updateSeekBar, DELAY_TIME);
        }
    };

    public void updateProgressBar() {
        if (!handlerRunning) {
            handler.postDelayed(updateSeekBar, DELAY_TIME);
        }
    }


    private void assignBottomModalSheetSeekBarToVariable(View view) {
        seekBar = (SeekBar) view.findViewById(R.id.app_seek_bar);
        seekBar.setMax(100*100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


}
