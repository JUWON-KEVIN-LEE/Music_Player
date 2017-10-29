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
import com.immymemine.kevin.musicplayer.events.BusProvider;
import com.immymemine.kevin.musicplayer.events.CurrentTimeEvent;
import com.immymemine.kevin.musicplayer.events.Event;
import com.immymemine.kevin.musicplayer.model.MusicItem;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.immymemine.kevin.musicplayer.utils.FileUtil.ITEMS;

/**
 * Created by quf93 on 2017-10-16.
 */

public class DetailViewPagerAdater extends PagerAdapter {

    Context context;
    List<MusicItem> musicData;
    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    public DetailViewPagerAdater(Context context, List<MusicItem> musicData) {
        this.context = context;
        this.musicData = musicData;

        BusProvider.getInstance().register(this);
    }

    View view;
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        view = LayoutInflater.from(context).inflate(R.layout.detail, null);

        TextView titleView = (TextView) view.findViewById(R.id.titleView);
        titleView.setText(musicData.get(position).getTitle());

        TextView artistView = (TextView) view.findViewById(R.id.artistView);
        artistView.setText(musicData.get(position).getArtist());

        TextView durationView = (TextView) view.findViewById(R.id.durationView);
        durationView.setText(sdf.format( musicData.get(position).getDuration() ));

        CircleImageView civView = (CircleImageView) view.findViewById(R.id.civView);
        Glide.with(context).load(musicData.get(position).getAlbumUri()).into(civView);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return ITEMS.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Subscribe
    public void subscriber(Event event) {
        if( event instanceof CurrentTimeEvent ) {
            TextView timeView = (TextView) view.findViewById(R.id.timeView);
            timeView.setText(sdf.format(((CurrentTimeEvent) event).getmCurrentTime() + ""));
        }
    }
}
