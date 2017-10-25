package com.immymemine.kevin.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.immymemine.kevin.musicplayer.model.MusicItem;
import com.immymemine.kevin.musicplayer.utils.FileUtil;

import java.io.IOException;
import java.util.List;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener {
    public PlayerService() {
    }

    private MediaPlayer mp;
    private List<MusicItem> data = null;
    private int mCurrentPosition = 0;
    private boolean isPlaying = false;

    @Override
    public void onCreate() {
        // get music data
        data = FileUtil.ITEMS;

        // init Media Player
        mp = MediaPlayer.create(this, data.get(mCurrentPosition).getMusicUri());
        mp.setLooping(false);
        mp.setOnCompletionListener(this);
    }

    private final IBinder binder = new CustomBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    public void play() {
        if(data.size() != 0) {
            mp.start();
            isPlaying = mp.isPlaying();
        }
    }

    public void play(int position) {
        if(data.size() != 0) {
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
        }
    }

    public void pause() {
        mp.pause();
        isPlaying = mp.isPlaying();
    }

    public void next() {
        // move position
        if(mCurrentPosition == data.size()-1) {
            mCurrentPosition = 0;
        } else {
            mCurrentPosition++;
        }

        play(mCurrentPosition);
    }

    public void pre() {
        // move position
        if(mCurrentPosition == 0) {
            mCurrentPosition = data.size()-1;
        } else {
            mCurrentPosition--;
        }

        play(mCurrentPosition);
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }
    public int getmCurrentPosition() { return mCurrentPosition; }
    public int getPosition() {
        return mp.getCurrentPosition();
    }
    public void seekTo(int position) {
        mp.seekTo(position);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    public class CustomBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
