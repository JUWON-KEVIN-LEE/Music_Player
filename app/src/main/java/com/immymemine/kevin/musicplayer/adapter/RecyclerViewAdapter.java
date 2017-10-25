package com.immymemine.kevin.musicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.immymemine.kevin.musicplayer.R;
import com.immymemine.kevin.musicplayer.fragments.InteractionListener;
import com.immymemine.kevin.musicplayer.model.MusicItem;
import com.immymemine.kevin.musicplayer.utils.FileUtil;

import java.util.List;

/**
 * Created by quf93 on 2017-10-15.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<MusicItem> mValues = null;
    InteractionListener mListener;
    Context context;
    public RecyclerViewAdapter(Context context, InteractionListener mListener) {
        this.context = context;
        this.mListener = mListener;
        if(mValues == null) {
            mValues = FileUtil.ITEMS;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.position = position;
        holder.bindHolder();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        MusicItem mItem;
        ImageView albumArtView;
        TextView titleView;
        TextView artistView;
        int position;

        public ViewHolder(View itemView) {
            super(itemView);
            albumArtView = (ImageView) itemView.findViewById(R.id.albumArtView);
            titleView = (TextView) itemView.findViewById(R.id.titleView);
            artistView = (TextView) itemView.findViewById(R.id.artistView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.playByList(position);
                }
            });
        }

        public void bindHolder() {
            Glide.with(context).load(mItem.getAlbumUri()).into(albumArtView);
            titleView.setText(mItem.getTitle());
            artistView.setText(mItem.getArtist());
        }
    }
}
