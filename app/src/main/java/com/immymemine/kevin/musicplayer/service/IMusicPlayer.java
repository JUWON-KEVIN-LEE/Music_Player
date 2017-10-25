package com.immymemine.kevin.musicplayer.service;

/**
 * Created by quf93 on 2017-10-24.
 */

public interface IMusicPlayer {
    void play();
    void pause();
    void next();
    void prev();
    void ff();
    void fb();

    void setUI();

    boolean getIsPlaying();
}
