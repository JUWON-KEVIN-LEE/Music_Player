package com.immymemine.kevin.musicplayer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.immymemine.kevin.musicplayer.R;
import com.immymemine.kevin.musicplayer.adapter.DetailViewPagerAdater;
import com.immymemine.kevin.musicplayer.events.ActivityToServiceEvent;
import com.immymemine.kevin.musicplayer.events.BusProvider;
import com.immymemine.kevin.musicplayer.events.CurrentTimeEvent;
import com.immymemine.kevin.musicplayer.events.Event;
import com.immymemine.kevin.musicplayer.events.IsPlayingEvent;
import com.immymemine.kevin.musicplayer.events.PlayerInfoEvent;
import com.immymemine.kevin.musicplayer.model.MusicItem;
import com.immymemine.kevin.musicplayer.service.PlayerInfo;
import com.immymemine.kevin.musicplayer.service.PlayerService;
import com.immymemine.kevin.musicplayer.utils.Const;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.immymemine.kevin.musicplayer.utils.FileUtil.ITEMS;

public class DetailActivity extends AppCompatActivity {
        // view
        ViewPager viewPager;
        SeekBar playerSeekBar;
        ImageView ivList;
        ImageButton ibPre, ibBack, ibStart, ibForward, ibNext;
        TextView timeView;

        // music player info
        private boolean isPlaying;
        private int mCurrentPosition;
        private PlayerInfo playerInfo;

        // service
        Intent intent;

        // event bus
        EventBus bus;
        ActivityToServiceEvent postEventToService;

        // data...
        List<MusicItem> musicData = new ArrayList<>();
        boolean isFromOnCreate;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detail);

            // data
            musicData = ITEMS;

            isFromOnCreate = true;
            // view
            initView();
            initViewPager();

            bus = BusProvider.getInstance();
            bus.register(this);

            // service
            intent = new Intent(this, PlayerService.class);
            startService(intent);
            postEventToService = new ActivityToServiceEvent();
        }

        @Override
        protected void onDestroy() {
            // event bus 해제
            bus.unregister(this);
            super.onDestroy();
        }

        private void initView() {
            playerSeekBar = (SeekBar) findViewById(R.id.playerSeekBar);
            playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser) {
                        postEventToService.setCommand(Const.SEEK_TO);
                        postEventToService.setmCurrentPosition(progress);
                        bus.post(postEventToService);
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

            ivList = (ImageView) findViewById(R.id.iv_list);
            ivList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postEventToService.setCommand(Const.ACTIVITY_MOVE);
                    bus.post(postEventToService);
                    finish();
                }
            });
            ibPre = (ImageButton) findViewById(R.id.ib_pre);
            ibStart = (ImageButton) findViewById(R.id.ib_start);
            ibNext = (ImageButton) findViewById(R.id.ib_next);

            ibBack = (ImageButton) findViewById(R.id.ib_back);
            ibForward = (ImageButton) findViewById(R.id.ib_forward);

            timeView = (TextView) findViewById(R.id.timeView);
        }
        DetailViewPagerAdater adapter;
        private void initViewPager() {
            viewPager = (ViewPager) findViewById(R.id.viewPager);
            adapter = new DetailViewPagerAdater(this, musicData);
            viewPager.setAdapter(adapter);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    mCurrentPosition = position;
                    if(!isFromOnCreate)
                        play(mCurrentPosition);
                    // 여기서 service 에 post 해줘야함
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
    //        viewPager.setCurrentItem(mCurrentPosition);
        }

        @Subscribe
        public void subscriber(Event event) {
            if(event instanceof IsPlayingEvent) {
                if( ((IsPlayingEvent) event).isPlaying() )
                    ibStart.setImageResource(android.R.drawable.ic_media_pause);
                else
                    ibStart.setImageResource(android.R.drawable.ic_media_play);
            }

            if(event instanceof PlayerInfoEvent) {
                // player info setting
                mCurrentPosition = ((PlayerInfoEvent) event).getPlayerInfo().getmCurrentPosition();
                // 여기서 viewpager refresh 를 해주자
                viewPager.setCurrentItem(mCurrentPosition);
                isFromOnCreate = false;
                playerSeekBar.setMax((int)musicData.get(mCurrentPosition).getDuration());
            }

            if( event instanceof CurrentTimeEvent ) {
                playerSeekBar.setProgress( ((CurrentTimeEvent) event).getmCurrentTime() );
            }

            // TODO 액티비티 간 이동시 받을 event subscriber 를 만들어주자
        }

        public void play(View view) {
            if(isPlaying) {
                pause();
            } else {
                postEventToService.setCommand(Const.PLAY);
                bus.post(postEventToService);
                isPlaying = true;
                ibStart.setImageResource(android.R.drawable.ic_media_pause);
            }
        }

        public void play(int position) { // 포지션 값에 의한 play
            postEventToService.setCommand(Const.PLAYWITHPOSITION);
            postEventToService.setmCurrentPosition(position);
            bus.post(postEventToService);

            isPlaying = true;
            ibStart.setImageResource(android.R.drawable.ic_media_pause);
            mCurrentPosition = position;
            viewPager.setCurrentItem(mCurrentPosition);
            playerSeekBar.setMax((int)musicData.get(mCurrentPosition).getDuration());
        }

        public void pause() {
            postEventToService.setCommand(Const.PAUSE);
            bus.post(postEventToService);
            isPlaying = false;
            ibStart.setImageResource(android.R.drawable.ic_media_play);
        }

        public void next(View view) {
            postEventToService.setCommand(Const.NEXT);
            bus.post(postEventToService);

            isPlaying = true;
            ibStart.setImageResource(android.R.drawable.ic_media_pause);
            if(mCurrentPosition == musicData.size()-1) {
                mCurrentPosition = 0;
            } else {
                mCurrentPosition++;
            }
            viewPager.setCurrentItem(mCurrentPosition);
            playerSeekBar.setMax((int)musicData.get(mCurrentPosition).getDuration());
        }

        public void pre(View view) {
            postEventToService.setCommand(Const.PRE);
            bus.post(postEventToService);

            isPlaying = true;
            ibStart.setImageResource(android.R.drawable.ic_media_pause);
            if(mCurrentPosition == 0) {
                mCurrentPosition = musicData.size()-1;
            } else {
                mCurrentPosition--;
            }
            viewPager.setCurrentItem(mCurrentPosition);
            playerSeekBar.setMax((int)musicData.get(mCurrentPosition).getDuration());
        }

        public void ff() {

        }

        public void fb() {

        }

}
