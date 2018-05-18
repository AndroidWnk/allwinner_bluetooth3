package com.etrans.bluetooth.event;

public class AutoConnectAcceptEvent {

    public boolean autoConnect = false;//蓝牙自动连接状态
    public boolean autoAccept = false;//蓝牙自动接听状态
    public AutoConnectAcceptEvent(boolean autoConnect,boolean autoAccept){
        this.autoConnect = autoConnect;
        this.autoAccept = autoAccept;
    }

}
