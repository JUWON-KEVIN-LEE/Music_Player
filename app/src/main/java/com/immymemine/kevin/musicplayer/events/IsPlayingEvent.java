package com.immymemine.kevin.musicplayer.events;

/**
 * Created by quf93 on 2017-10-27.
 */

public class IsPlayingEvent implements Event{
    // play 시에 event 처리
    private boolean isPlaying;

    public boolean isPlaying() {
        return isPlaying;
    }
    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
}
