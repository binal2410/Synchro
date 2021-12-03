package com.synchro.model;

public class ConnectDeviceModel {
    public String deviceName;
    public String macAddress;
    public String isCoded;
    public String battery_level = "N/A";
    public long battery_level_time;
    public boolean isConnected;
    public boolean isBlink;

    public ConnectDeviceModel( String deviceName,String macAddress, String isCoded, boolean isConnected, boolean isBlink) {
        this.deviceName = deviceName;
        this.macAddress = macAddress;
        this.isCoded = isCoded;
        this.isConnected = isConnected;
        this.isBlink = isBlink;
    }
}
