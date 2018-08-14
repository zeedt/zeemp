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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zeed.zeemp.R;
import com.zeed.zeemp.adapters.AudioPlayerAdapter;
import com.zeed.zeemp.models.Audio;

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

    ImageView imageView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_audio_list, container, false);
        // Inflate the layout for this fragment
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);



        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        audioPlayerAdapter = new AudioPlayerAdapter(getActivity().getApplicationContext(),audioList,this);

        recyclerView.setAdapter(audioPlayerAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        audioPlayerAdapter.notifyDataSetChanged();

        imageView = (ImageView) view.findViewById(R.id.play_or_pause);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Hello", "onClick: ");
            }
        });


        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void click(Audio audio) {
        if (audio != null) {
            TextView textView = getView().findViewById(R.id.audio_title);
            textView.setText(audio.getTitle());
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        fetchMusicWithContentProvider(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
        Log.d("TAG", "fetchMusicWithContentProvider: Fetched");
    }


}
