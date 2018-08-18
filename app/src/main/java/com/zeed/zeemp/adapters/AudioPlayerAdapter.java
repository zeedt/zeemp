package com.zeed.zeemp.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.zeed.zeemp.R;
import com.zeed.zeemp.models.Audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by teamapt on 12/08/2018.
 */

public class AudioPlayerAdapter extends RecyclerView.Adapter<AudioPlayerAdapter.ViewHolder> implements Filterable {

    private Context context;

    private List<Audio> audioList = new ArrayList<>();
    private List<Audio> allAudioList = new ArrayList<>();

    private RecyclerItemOnClickListener recyclerItemOnClickListener;

    private final MediaPlayer mediaPlayer = new MediaPlayer();

    public boolean isPlaying = false;

    private AudioListFilter audioListFilter = new AudioListFilter();



    public AudioPlayerAdapter(Context context, List<Audio> audioList, RecyclerItemOnClickListener recyclerItemOnClickListener) {
        this.context = context;
        if (audioList == null) {
            this.audioList = new ArrayList<>();
            this.allAudioList = new ArrayList<>();
        } else {
            this.audioList = audioList;
            this.allAudioList = audioList;
        }
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
                try {
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

    @Override
    public Filter getFilter() {
        if (audioListFilter == null) {
            audioListFilter = new AudioListFilter();
        }

        return audioListFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        private ConstraintLayout cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.audio_name);
            cardView = itemView.findViewById(R.id.recycler_card_view);
        }
    }

    public interface RecyclerItemOnClickListener {
        void click(Audio audio) throws IOException;
    }

    public void pauseAudio () {
        if (isPlaying) {
            mediaPlayer.pause();
        } else {

        }
    }

    public class AudioListFilter extends Filter {

        FilterResults filterResults = new FilterResults();

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint == null || constraint.length() == 0) {
                filterResults.values = allAudioList;
                filterResults.count = allAudioList.size();
            } else {
                List<Audio> filteredAudio = new ArrayList<>();
                for (Audio audio:allAudioList) {
                    if (audio == null || audio.getTitle() == null) {
                        continue;
                    }
                    if (audio.getTitle().toLowerCase().trim().contains(constraint.toString().toLowerCase())) {
                        filteredAudio.add(audio);
                    }
                }
                filterResults.values = filteredAudio;
                filterResults.count = filteredAudio.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            audioList = (List<Audio>) results.values;
            notifyDataSetChanged();
        }
    }

}
