package com.immymemine.kevin.musicplayer.events;

import com.immymemine.kevin.musicplayer.service.PlayerInfo;

/**
 * Created by quf93 on 2017-10-28.
 */

public class PlayerInfoEvent implements Event {

    private PlayerInfo playerInfo;

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }
}
