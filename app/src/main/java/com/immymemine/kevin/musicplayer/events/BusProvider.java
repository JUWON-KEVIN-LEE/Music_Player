package com.immymemine.kevin.musicplayer.events;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by quf93 on 2017-10-28.
 */

public class BusProvider {
    private static EventBus bus = null;

    public static EventBus getInstance() {
        if(bus == null) {
            bus = new EventBus();
        }
        return bus;
    }

    private BusProvider() {

    }
}
