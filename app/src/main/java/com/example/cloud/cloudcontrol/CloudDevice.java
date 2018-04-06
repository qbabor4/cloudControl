package com.example.cloud.cloudcontrol;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Jakub on 05-Apr-18.
 */

public class CloudDevice {

    private boolean isConnected = false;

    private final BluetoothDevice device;

    private final String name;

    private final String address;


    public CloudDevice(BluetoothDevice device){
        setDevice(device);

    }

    public void connect(){

    }

    public void disconnect(){

    }

    public void send(String msg){

    }

    /* Getters and setters */

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public boolean isConnected() {
        return isConnected;

    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    @Override
    public String toString(){
        return "chmura";
    }
}