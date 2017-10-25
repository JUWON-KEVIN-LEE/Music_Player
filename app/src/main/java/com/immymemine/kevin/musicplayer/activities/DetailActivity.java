package com.immymemine.kevin.musicplayer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.immymemine.kevin.musicplayer.R;
import com.immymemine.kevin.musicplayer.adapter.DetailViewPagerAdater;
import com.immymemine.kevin.musicplayer.model.MusicItem;
import com.immymemine.kevin.musicplayer.service.PlayerService;
import com.immymemine.kevin.musicplayer.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

import static com.immymemine.kevin.musicplayer.R.id.artistView;
import static com.immymemine.kevin.musicplayer.R.id.civ_album;
import static com.immymemine.kevin.musicplayer.R.id.titleView;

public class DetailActivity extends AppCompatActivity {
    // view
    ViewPager viewPager;
    SeekBar playerSeekBar;
    ImageView ivList;
    ImageButton ibPre, ibBack, ibStart, ibForward, ibNext;

    // music player info
    private boolean isBound;
    private int mCurrentPosition;
    private static List<MusicItem> data = new ArrayList<>();

    // service
    PlayerService playerService;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // music data
        data = FileUtil.ITEMS;

        // view
        initView();
        initViewPager();


        // service
        intent = new Intent(this, PlayerService.class);
        this.bindService(intent, con, Context.BIND_AUTO_CREATE);

        setSeekBar();
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


    private void initView() {
        playerSeekBar = (SeekBar) findViewById(R.id.playerSeekBar);
        ivList = (ImageView) findViewById(R.id.iv_list);
        ivList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ibPre = (ImageButton) findViewById(R.id.ib_pre);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        ibStart = (ImageButton) findViewById(R.id.ib_start);

        ibForward = (ImageButton) findViewById(R.id.ib_forward);
        ibNext = (ImageButton) findViewById(R.id.ib_next);
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        DetailViewPagerAdater adapter = new DetailViewPagerAdater(this, data);
        viewPager.setAdapter(adapter);

        mCurrentPosition = 0;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                playByList(mCurrentPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setCurrentItem(mCurrentPosition);
    }

    public void play(View view) {
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

    public boolean runFlag;
    private void setSeekBar() {
        new Thread() {
            public void run() {
                while(runFlag) {
                    playerSeekBar.setProgress(playerService.getPosition());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    playerService.seekTo(progress);
                    seekBar.setProgress(progress);
                }
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
