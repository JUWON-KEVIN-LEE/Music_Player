package com.immymemine.kevin.musicplayer.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.immymemine.kevin.musicplayer.R;
import com.immymemine.kevin.musicplayer.custom_view.CircleImageView;
import com.immymemine.kevin.musicplayer.model.MusicItem;

import java.util.List;

/**
 * Created by quf93 on 2017-10-16.
 */

public class DetailViewPagerAdater extends PagerAdapter {
    Context context;
    List<MusicItem> mValues;
    public DetailViewPagerAdater(Context context, List<MusicItem> mValues) {
        this.context = context;
        this.mValues = mValues;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.detail, null);

        TextView titleView = (TextView) view.findViewById(R.id.titleView);
        titleView.setText(mValues.get(position).getTitle());

        TextView artistView = (TextView) view.findViewById(R.id.artistView);
        artistView.setText(mValues.get(position).getArtist());

        CircleImageView civView = (CircleImageView) view.findViewById(R.id.civView);
        Glide.with(context).load(mValues.get(position).getAlbumUri()).into(civView);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
