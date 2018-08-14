package com.zeed.zeemp.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zeed.zeemp.R;
import com.zeed.zeemp.models.Audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by teamapt on 12/08/2018.
 */

public class AudioPlayerAdapter extends RecyclerView.Adapter<AudioPlayerAdapter.ViewHolder> {

    private Context context;

    private List<Audio> audioList = new ArrayList<>();

    private RecyclerItemOnClickListener recyclerItemOnClickListener;

    private final MediaPlayer mediaPlayer = new MediaPlayer();

    public boolean isPlaying = false;


    public AudioPlayerAdapter(Context context, List<Audio> audioList, RecyclerItemOnClickListener recyclerItemOnClickListener) {
        this.context = context;
        this.audioList = audioList;
        this.recyclerItemOnClickListener = recyclerItemOnClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.textView.setText(audioList.get(position).getTitle());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(audioList.get(position).getData());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    isPlaying = true;
                    recyclerItemOnClickListener.click(audioList.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.audio_name);
            cardView = itemView.findViewById(R.id.recycler_card_view);
        }
    }

    public interface RecyclerItemOnClickListener {
        void click(Audio audio);
    }


}
