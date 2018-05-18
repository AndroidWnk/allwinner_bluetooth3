package com.etrans.bluetooth.event;

/**
 * Created by Administrator on 2016/7/27.
 */
public class BaseEvent {
    private int eventCode;

    public BaseEvent(int eventCode) {
        this.eventCode = eventCode;
    }

    public int getEventCode() {
        return eventCode;
    }
}
