package com.immymemine.kevin.musicplayer.events;

/**
 * Created by quf93 on 2017-10-28.
 */

public class CurrentTimeEvent implements Event{
    private int mCurrentTime;

    public int getmCurrentTime() {
        return mCurrentTime;
    }

    public void setmCurrentTime(int mCurrentTime) {
        this.mCurrentTime = mCurrentTime;
    }
}
