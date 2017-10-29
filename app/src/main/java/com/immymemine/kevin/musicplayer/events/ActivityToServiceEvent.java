package com.immymemine.kevin.musicplayer.events;

/**
 * Created by quf93 on 2017-10-28.
 */

public class ActivityToServiceEvent implements Event{
    private String command;
    private int mCurrentPosition;

    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    public int getmCurrentPosition() {
        return mCurrentPosition;
    }
    public void setmCurrentPosition(int mCurrentPosition) {
        this.mCurrentPosition = mCurrentPosition;
    }
}
