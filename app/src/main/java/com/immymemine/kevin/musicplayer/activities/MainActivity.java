package com.immymemine.kevin.musicplayer.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.immymemine.kevin.musicplayer.R;
import com.immymemine.kevin.musicplayer.adapter.ViewPagerAdapter;
import com.immymemine.kevin.musicplayer.custom_view.CircleImageView;
import com.immymemine.kevin.musicplayer.events.ActivityToServiceEvent;
import com.immymemine.kevin.musicplayer.events.BusProvider;
import com.immymemine.kevin.musicplayer.events.Event;
import com.immymemine.kevin.musicplayer.events.IsPlayingEvent;
import com.immymemine.kevin.musicplayer.events.PlayerInfoEvent;
import com.immymemine.kevin.musicplayer.fragments.AlbumFragment;
import com.immymemine.kevin.musicplayer.fragments.ArtistFragment;
import com.immymemine.kevin.musicplayer.fragments.GenreFragment;
import com.immymemine.kevin.musicplayer.fragments.InteractionListener;
import com.immymemine.kevin.musicplayer.fragments.PlayerFragment;
import com.immymemine.kevin.musicplayer.model.MusicItem;
import com.immymemine.kevin.musicplayer.service.PlayerService;
import com.immymemine.kevin.musicplayer.utils.Const;
import com.immymemine.kevin.musicplayer.utils.FileUtil;
import com.immymemine.kevin.musicplayer.utils.PermissionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.immymemine.kevin.musicplayer.utils.FileUtil.ITEMS;

public class MainActivity extends PermissionUtil implements InteractionListener{
    // view
    TextView titleView, artistView;
    ViewPager viewPager;
    TabLayout tabLayout;
    CircleImageView civ_album;
    ImageButton ibPre, ibStart, ibNext;

    // mp info
    private boolean isPlaying;
    private int mCurrentPosition;

    // service
    Intent intent;

    // event bus
    EventBus bus;
    ActivityToServiceEvent postEventToService;

    // data...
    List<MusicItem> musicData = new ArrayList<>();
    @Override
    public void init() {
        // ---------- onCreate 에서 실행 ----------
        // view load
        initView();
        initViewPager();
        initTabLayout();

        // data load from external storage
        FileUtil.readMusicList(this);

        // service start >>> onCreate >>> onStartCommand
        intent = new Intent(this, PlayerService.class);
        startService(intent);

        // event bus 등록
        bus = BusProvider.getInstance();
        bus.register(this);
        postEventToService = new ActivityToServiceEvent();

        // mp info 초기화
        isPlaying = false;

        // data
        musicData = ITEMS;
    }

    // initiate view objects
    private void initView() {
        View.OnClickListener mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        };

        titleView = (TextView) findViewById(R.id.titleView);
        titleView.setOnClickListener(mListener);
        artistView = (TextView) findViewById(R.id.artistView);
        artistView.setOnClickListener(mListener);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        // circle image view
        civ_album = (CircleImageView) findViewById(R.id.civ_album);
        civ_album.setOnClickListener(mListener);

        // button
        ibStart = (ImageButton) findViewById(R.id.ib_start);
        ibNext = (ImageButton) findViewById(R.id.ib_next);

        ibNext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                while(v.isPressed()) {
                    ff();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

        ibPre = (ImageButton) findViewById(R.id.ib_pre);
        ibPre.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                while(v.isPressed()) {
                    fb();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return true;
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

    @Override
    protected void onDestroy() {
        // 서비스 종료
        stopService(intent);

        // event bus 해제
        bus.unregister(this);

        super.onDestroy();
    }


    public void play(View view) {
        if(!isPlaying) {
            postEventToService.setCommand(Const.PLAY);
            bus.post(postEventToService);
            isPlaying = true;
            ibStart.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            pause();
        }
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
        if(mCurrentPosition == musicData.size()-1) {
            mCurrentPosition = 0;
        } else {
            mCurrentPosition++;
        }
        ibStart.setImageResource(android.R.drawable.ic_media_pause);
        titleView.setText( musicData.get(mCurrentPosition).getTitle() );
        artistView.setText( musicData.get(mCurrentPosition).getArtist() );
        Glide.with(this).load( musicData.get(mCurrentPosition).getAlbumUri() ).into(civ_album);
    }

    public void pre(View view) {
        postEventToService.setCommand(Const.PRE);
        bus.post(postEventToService);

        isPlaying = true;
        if(mCurrentPosition == 0) {
            mCurrentPosition = musicData.size()-1;
        } else {
            mCurrentPosition--;
        }
        ibStart.setImageResource(android.R.drawable.ic_media_pause);
        titleView.setText( musicData.get(mCurrentPosition).getTitle() );
        artistView.setText( musicData.get(mCurrentPosition).getArtist() );
        Glide.with(this).load( musicData.get(mCurrentPosition).getAlbumUri() ).into(civ_album);
    }

    public void ff() {
        postEventToService.setCommand(Const.FF);
        bus.post(postEventToService);

    }

    public void fb() {
        postEventToService.setCommand(Const.FB);
        bus.post(postEventToService);

    }

    @Override
    public void playByList(int position) { // 포지션 값에 의한 play
        // post event
        postEventToService.setCommand(Const.PLAYWITHPOSITION);
        postEventToService.setmCurrentPosition(position);
        bus.post(postEventToService);

        // view
        isPlaying = true;
        mCurrentPosition = position;
        ibStart.setImageResource(android.R.drawable.ic_media_pause);
        titleView.setText( musicData.get(mCurrentPosition).getTitle() );
        artistView.setText( musicData.get(mCurrentPosition).getArtist() );
        Glide.with(this).load( musicData.get(mCurrentPosition).getAlbumUri() ).into(civ_album);
    }

    @Subscribe
    public void subscriber(Event event) {
        if(event instanceof PlayerInfoEvent) {
            // player info setting
            mCurrentPosition = ((PlayerInfoEvent) event).getPlayerInfo().getmCurrentPosition();

            titleView.setText( musicData.get(mCurrentPosition).getTitle() );
            artistView.setText( musicData.get(mCurrentPosition).getArtist() );
            Glide.with(this).load( musicData.get(mCurrentPosition).getAlbumUri() ).into(civ_album);
        }

        if(event instanceof IsPlayingEvent) {
            if( ((IsPlayingEvent) event).isPlaying() )
                ibStart.setImageResource(android.R.drawable.ic_media_pause);
            else
                ibStart.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    public void post(int flag) {
        if(flag > 0) {
            postEventToService.setCommand(Const.PLAYWITHPOSITION);
            postEventToService.setmCurrentPosition(flag);
            bus.post(postEventToService);
        } else {
            switch (flag) {
                case Const.P:
                    break;
                case Const.PA:
                    break;
                case Const.N:
                    break;
                case Const.PR:
                    break;
            }
        }
    }
}
