package com.zeed.zeemp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

    private static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1;

    AudioPlayerAdapter audioPlayerAdapter;
    RecyclerView recyclerView;
    private Audio currentlyPlayed;
    Integer index = 0;
    public TextView audioTitleTextView;
    public TextView albumTitleTextView;

    ImageView showSearchView;

    private boolean handlerRunning = false;

    private SeekBar seekBar;

    ImageView playOrPause;

    private SearchView searchView;

    private ConstraintLayout searchViewParentLayout;
    private ConstraintLayout showSearchViewParentLayout;
    private CoordinatorLayout fragmentLayout;

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
//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        actionBar.setTitle("ZeeMp");

        fragmentLayout = (CoordinatorLayout) view.findViewById(R.id.fragment_layout);

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

        showSearchView = (ImageView) view.findViewById(R.id.showSearchView);

        searchViewParentLayout = view.findViewById(R.id.search_view_layout);

        assignBottomModalSheetSeekBarToVariable(view);

        audioTitleTextView = (TextView) view.findViewById(R.id.audio_title);
        audioTitleTextView.setSelected(true);
        albumTitleTextView = (TextView) view.findViewById(R.id.album_title);
        currentlyPlayed = mListener.getCurrentlyPlayed();
        setCurrentlyPlayedTitle(currentlyPlayed);
        playOrPause = (ImageView) view.findViewById(R.id.play_or_pause);

        searchView = (SearchView) view.findViewById(R.id.search_view);
        final EditText editText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setTextColor(Color.WHITE);

        ImageView closeButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchViewParentLayout.setVisibility(View.GONE);
                showSearchViewParentLayout.setVisibility(View.VISIBLE);
                editText.setText("");
                audioPlayerAdapter.getFilter().filter("");
                audioPlayerAdapter.notifyDataSetChanged();
            }
        });


        showSearchViewParentLayout = view.findViewById(R.id.showSearchViewParentLayout);

        showSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchViewParentLayout.setVisibility(View.VISIBLE);
                showSearchViewParentLayout.setVisibility(View.GONE);
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                audioPlayerAdapter.getFilter().filter(newText);
//                audioPlayerAdapter.
                return true;
            }
        });

        playOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentlyPlayed == null) {
                    if (audioList == null || audioList.size() == 0) {
                        return;
                    }
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
        getReadExternalStoragePermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getActivity().getSupportLoaderManager().initLoader(0, null, this);
        }
        showBottomModalSheet();
        return view;
    }


    private void showBottomModalSheet() {
        audioTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED ) {
                    playOrPause.setVisibility(View.GONE);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    playOrPause.setVisibility(View.VISIBLE);
                }
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
        String[] cols = {MediaStore.Audio.Media.TITLE,
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getActivity().getSupportLoaderManager().restartLoader(0, null, this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        fetchMusicWithContentProvider(cursor);
        mListener.updateMusicPlayerWithAudioList(audioList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("", "onLoaderReset: ");
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
            if (audioList.isEmpty()) {
                return;
            }
            audioTitleTextView.setText(audioList.get(0).getTitle());
            albumTitleTextView.setText(audioList.get(0).getAlbum());
        }
    }

    public void setPlayOrPause() {
        if (mListener != null && mListener.isMusicPlaying()) {
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
        void seekToPosition(int seekBarPosition);
        boolean hasMusicCompleted();
        void updateMusicPlayerWithAudioList(List<Audio> audioList);
    }

    public void setCurrentlyPlayedTitle(Audio audio) {

        if (audio == null) {
            return;
        }
        audioTitleTextView.setText(audio.getTitle());
        albumTitleTextView.setText(audio.getAlbum());
    }

    public void assignPlayOrPauseButtonModalSheetButtonsToVariable(View view) {
        bottomModalSheetPlayOrPause = view.findViewById(R.id.play_or_pause_bms);

        bottomModalSheetPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (currentlyPlayed == null && !audioList.isEmpty()) {
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
                if (audioList.isEmpty()) {
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
            try {
                setPlayOrPause();
                handlerRunning = true;
                if (mListener != null) {
                    Pair<Integer,Integer> durationAndPosition = mListener.getDurationAndPosition();
                    if (durationAndPosition != null) {
                        int seekBarPosition = (durationAndPosition.second * 100) / durationAndPosition.first;

                        // TODO: Set animation
        //                if(android.os.Build.VERSION.SDK_INT >= 11){
        //                    ObjectAnimator animation = ObjectAnimator.ofInt(seekBar, "progress", seekBar.getProgress(), seekBarPosition * 100);
        //                    animation.setDuration(durationAndPosition.second); // 0.5 second
        //                    animation.setInterpolator(new DecelerateInterpolator());
        //                    animation.start();
        //                } else {
        //                }
                        seekBar.setProgress(seekBarPosition);
                    }
                    handler.postDelayed(updateSeekBar, DELAY_TIME);
                }
            } catch (Exception e) {
                Log.e("Error", "run: Error occurred due to", e);
            }
        }
    };

    public void updateProgressBar() {
        if (!handlerRunning) {
            handler.postDelayed(updateSeekBar, DELAY_TIME);
        }
    }


    private void assignBottomModalSheetSeekBarToVariable(View view) {
        seekBar = (SeekBar) view.findViewById(R.id.app_seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 99) {
                    bottomModalSheetNext.performClick();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int seekBarProgress = seekBar.getProgress();
                mListener.seekToPosition(seekBarProgress);

            }
        });
    }

    private void getReadExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_REQUEST);
            } else {

            }
        }
    }

}
