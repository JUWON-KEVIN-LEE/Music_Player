package com.immymemine.kevin.musicplayer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.immymemine.kevin.musicplayer.R;
import com.immymemine.kevin.musicplayer.adapter.ViewPagerAdapter;
import com.immymemine.kevin.musicplayer.custom_view.CircleImageView;
import com.immymemine.kevin.musicplayer.fragments.AlbumFragment;
import com.immymemine.kevin.musicplayer.fragments.ArtistFragment;
import com.immymemine.kevin.musicplayer.fragments.GenreFragment;
import com.immymemine.kevin.musicplayer.fragments.InteractionListener;
import com.immymemine.kevin.musicplayer.fragments.PlayerFragment;
import com.immymemine.kevin.musicplayer.model.MusicItem;
import com.immymemine.kevin.musicplayer.service.PlayerService;
import com.immymemine.kevin.musicplayer.utils.FileUtil;
import com.immymemine.kevin.musicplayer.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends PermissionUtil implements InteractionListener{
    // view
    TextView titleView, artistView;
    ViewPager viewPager;
    TabLayout tabLayout;
    CircleImageView civ_album;
    ImageButton ibPre, ibStart, ibNext;

    // mp info
    private boolean isBound;
    private int mCurrentPosition;
    private static List<MusicItem> data = new ArrayList<>();

    //service
    PlayerService playerService;
    Intent intent;
    @Override
    public void init() {
        // view
        initView();
        initViewPager();
        initTabLayout();

        // data load from external storage
        data = FileUtil.readMusicList(this);

        // service bind
        intent = new Intent(this, PlayerService.class);
        this.bindService(intent, con, Context.BIND_AUTO_CREATE);

        mCurrentPosition = 0;
    }

    private final ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.CustomBinder binder = (PlayerService.CustomBinder) service;
            playerService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    public void unBind() {
        unbindService(con);
    }

    // initiate view objects
    private void initView() {
        titleView = (TextView) findViewById(R.id.titleView);
        artistView = (TextView) findViewById(R.id.artistView);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        // circle image view
        civ_album = (CircleImageView) findViewById(R.id.civ_album);

        civ_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), DetailActivity.class);
                startActivity(intent);
            }
        });

        // button
        ibStart = (ImageButton) findViewById(R.id.ib_start);
        ibNext = (ImageButton) findViewById(R.id.ib_next);
        ibNext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ff();
                return false;
            }
        });

        ibPre = (ImageButton) findViewById(R.id.ib_pre);
        ibPre.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fb();
                return false;
            }
        });
    }

    private void initTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("노래"));
        tabLayout.addTab(tabLayout.newTab().setText("앨범"));
        tabLayout.addTab(tabLayout.newTab().setText("아티스트"));
        tabLayout.addTab(tabLayout.newTab().setText("장르"));

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    private void initViewPager() {
        List<Fragment> list = new ArrayList<>();
        PlayerFragment p = PlayerFragment.getInstance();
        AlbumFragment a = AlbumFragment.getInstance();
        ArtistFragment ar = ArtistFragment.getInstance();
        GenreFragment g = GenreFragment.getInstance();
        list.add(p);    list.add(a);    list.add(ar);   list.add(g);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }


    public void play(View view) {
        Log.d("isBound", isBound + "");
        if(isBound) {
            if(isPlaying)
                pause(view);
            else {
                playerService.play();
                isPlaying = playerService.getIsPlaying();
            }

            setUI();
        }
    }

    public void pause(View view) {
        if(isBound) {
            playerService.pause();
            isPlaying = playerService.getIsPlaying();

            setUI();
        }
    }

    int timePosition;
    public void ff() {
        timePosition = playerService.getPosition();
        playerService.seekTo(timePosition+5000);
        mCurrentPosition = playerService.getmCurrentPosition();
    }

    public void fb() {
        timePosition = playerService.getPosition();
        if(timePosition < 5000) {
            if(isBound) {
                playerService.pre();
                mCurrentPosition = playerService.getmCurrentPosition();
                isPlaying = playerService.getIsPlaying();

                setUI();
            }
        } else {
            playerService.seekTo(playerService.getPosition()-5000);
        }
    }
    public void next(View view) {
        if(isBound) {
            playerService.next();
            mCurrentPosition = playerService.getmCurrentPosition();
            isPlaying = playerService.getIsPlaying();

            setUI();
        }
    }

    public void pre(View view) {
        if(isBound) {
            playerService.pre();
            mCurrentPosition = playerService.getmCurrentPosition();
            isPlaying = playerService.getIsPlaying();

            setUI();
        }
    }

    @Override
    public void playByList(int position) { // 포지션 값에 의한 play
        if(isBound) {
            playerService.play(position);
            isPlaying = playerService.getIsPlaying();
            mCurrentPosition = playerService.getmCurrentPosition();

            setUI();
        }
    }

    private boolean isPlaying;
    @Override
    protected void onResume() {
        if(isBound) {
            isPlaying = playerService.getIsPlaying();
            mCurrentPosition = playerService.getmCurrentPosition();
        }

        setUI();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unBind();
        super.onDestroy();
    }

    public void setUI() {
        if(!titleView.getText().toString().equals(data.get(mCurrentPosition).getTitle())) {
            Glide.with(this).load(data.get(mCurrentPosition).getAlbumUri()).into(civ_album);
            titleView.setText(data.get(mCurrentPosition).getTitle());
            artistView.setText(data.get(mCurrentPosition).getArtist());
        }

        if(isPlaying)
            ibStart.setImageResource(android.R.drawable.ic_media_pause);
        else
            ibStart.setImageResource(android.R.drawable.ic_media_play);
    }
}
