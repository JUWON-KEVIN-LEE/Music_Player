package com.immymemine.kevin.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.immymemine.kevin.musicplayer.events.ActivityToServiceEvent;
import com.immymemine.kevin.musicplayer.events.BusProvider;
import com.immymemine.kevin.musicplayer.events.CurrentTimeEvent;
import com.immymemine.kevin.musicplayer.events.Event;
import com.immymemine.kevin.musicplayer.events.IsPlayingEvent;
import com.immymemine.kevin.musicplayer.events.PlayerInfoEvent;
import com.immymemine.kevin.musicplayer.model.MusicItem;
import com.immymemine.kevin.musicplayer.utils.Const;
import com.immymemine.kevin.musicplayer.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener {
    public PlayerService() {
    }
    // media player
    private MediaPlayer mp;

    // data
    private List<MusicItem> data = null;

    // mp info
    private PlayerInfo playerInfo;
    private int mCurrentPosition;
    private boolean isPlaying;

    // event bus
    EventBus bus;
    PlayerInfoEvent palyerInfoEventToActivity;
    IsPlayingEvent isPlayingEventToActivity;
    CurrentTimeEvent currentTimeEventToActivity;

    TimeWorker worker;
    @Override
    public void onCreate() {
        Log.d("onCreate","========== run ==========");
        // get music data inst
        data = FileUtil.ITEMS;

        mCurrentPosition = 0;
        isPlaying = false;

        // create media player
        mp = MediaPlayer.create(this, data.get(mCurrentPosition).getMusicUri());
        mp.setLooping(false);
        mp.setOnCompletionListener(this);

        // event bus 등록
        bus = BusProvider.getInstance();
        bus.register(this);
        palyerInfoEventToActivity = new PlayerInfoEvent();
        isPlayingEventToActivity = new IsPlayingEvent();
        currentTimeEventToActivity = new CurrentTimeEvent();

        // thread run
        worker = new TimeWorker();
        worker.start();
    }

    private void setServiceInfoToPlayerInfo() {
        if(playerInfo == null)
            playerInfo = new PlayerInfo();

//        MusicItem item = data.get(mCurrentPosition);
//        playerInfo.setTitle(item.getTitle());
//        playerInfo.setArtist(item.getArtist());
        playerInfo.setmCurrentPosition(mCurrentPosition);
//        playerInfo.setmDuration(mp.getDuration());
//        playerInfo.setAlbumArtUri(item.getAlbumUri());
    }

    private void postPlayerInfoEvent() {
        setServiceInfoToPlayerInfo();
        palyerInfoEventToActivity.setPlayerInfo(playerInfo);
        bus.post(palyerInfoEventToActivity);
    }

    private void postIsPlayingEvent() {
        isPlayingEventToActivity.setPlaying(isPlaying);
        bus.post(isPlayingEventToActivity);
    }

    private void postCurrentTimeEvent() {
        currentTimeEventToActivity.setmCurrentTime(mp.getCurrentPosition());
        bus.post(currentTimeEventToActivity);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 여기서 activity 로 이벤트를 보내준다
        // 정보가 담긴 player info 를 event bus 를 통해서 post
        postPlayerInfoEvent(); // >>> 받아서 ui refresh

        // play = true 면 thread 에서 계속적으로 보내준다
        // play = false 면 다른 액티비티에서 start service 시에 한번만 정보를 보내주면 된다
        if(!isPlaying) {
            postIsPlayingEvent();
            postCurrentTimeEvent();
        } else {
            postIsPlayingEvent();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // free the resources
        mp.release();
        // event bus 해제
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void subscriber(Event event) {
        if(event instanceof ActivityToServiceEvent) {
            String cmd = ((ActivityToServiceEvent) event).getCommand();
            switch (cmd) {
                case Const.PLAY:
                    play();
                    break;
                case Const.PLAYWITHPOSITION:
                    mCurrentPosition = ((ActivityToServiceEvent) event).getmCurrentPosition();
                    play(mCurrentPosition);
                    break;
                case Const.PAUSE:
                    pause();
                    break;
                case Const.NEXT:
                    next();
                    break;
                case Const.PRE:
                    prev();
                    break;
                case Const.FF:
                    seekTo(mp.getCurrentPosition() + 5000);
                    break;
                case Const.FB:
                    seekTo(mp.getCurrentPosition() - 5000);
                    break;
                case Const.SEEK_TO:
                    seekTo(((ActivityToServiceEvent) event).getmCurrentPosition());
                    break;
                case Const.ACTIVITY_MOVE:
                    postPlayerInfoEvent();
                    postIsPlayingEvent();
                    break;
            }
        }
    }

    class TimeWorker extends Thread {
        public TimeWorker() {
        }

        private boolean runFlag = false;
        public void setRunFlag(boolean runFlag) {
            this.runFlag = runFlag;
        }

        @Override
        public void run() {
            while(true) {
                if(isPlaying) {
                    postCurrentTimeEvent();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void play() {
        if(mp!=null) {
            mp.start();
            isPlaying = mp.isPlaying(); // > runPostThread flag
            worker.setRunFlag(true);
        }
    }

    public void play(int position) {
        if(mp != null) {
            mCurrentPosition = position;
            if (isPlaying) {
                mp.pause();
                mp.reset();
                // mp = MediaPlayer.create(this, data.get(mCurrentPosition).getMusicUri());
                // create 함수는 매번 new 를 시킨다 >>> 비효율적
                try {
                    mp.setDataSource(this, data.get(mCurrentPosition).getMusicUri());
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mp.reset();
                try {
                    mp.setDataSource(this, data.get(mCurrentPosition).getMusicUri());
                    mp.prepare();
                    mp.start();
                    isPlaying = mp.isPlaying();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            worker.setRunFlag(true);
        }
    }

    private void pause() {
        if(mp != null) {
            mp.pause();
            isPlaying = mp.isPlaying();
            worker.setRunFlag(false);
        }
    }

    private void next() {
        // move position
        if(mCurrentPosition == data.size()-1) {
            mCurrentPosition = 0;
        } else {
            mCurrentPosition++;
        }

        play(mCurrentPosition);
    }

    public void prev() {
        // move position
        if(mCurrentPosition == 0) {
            mCurrentPosition = data.size()-1;
        } else {
            mCurrentPosition--;
        }

        play(mCurrentPosition);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
        postPlayerInfoEvent();
    }

    private void seekTo(int position) {
        mp.seekTo(position);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
