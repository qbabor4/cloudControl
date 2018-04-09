package com.example.cloud.cloudcontrol;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;

/**
 * !-- This is Singleton, because Socket and OutputStream are not serializable, and can't be passed with intent --!
 * Created by Jakub on 05-Apr-18.
 */
public class CloudDevice implements Serializable{

//    private static final CloudDevice instance = new CloudDevice();
//
//    public static CloudDevice getInstance(){
//        return instance;
//    }

    private final String mName;
    private final String mAddress;

    private boolean isConnected = false;

    private final BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;

    public CloudDevice(BluetoothDevice device) {
        this.mDevice = device;
        this.mName = device.getName();
        this.mAddress = device.getAddress();
    }

//    public CloudDevice(){
//
//    }

    /**
     * Connects to device.
     * Sets socket and gets outputStream.
     *
     * @throws IOException when connection error occurs
     */
    public void connect() throws IOException {
        mSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString(EDefaultData.BLUETOOTH_MODULE_UUID.getData()));
        mSocket.connect();

        mOutputStream = mSocket.getOutputStream();
        setConnected(true);
    }

    /**
     * Disconnects from device.
     * Closes outputstream and socket.
     * @throws IOException when error occurs while disconnecting
     */
    public void disconnect() throws IOException {
        mOutputStream.close();
        mSocket.close();
        setConnected(false);
    }

    private void send(String msg) throws IOException {
        mOutputStream.write(msg.getBytes());
    }



    /**
     * Sends color with correct protocol frame
     */
    public void sendColor(String color) throws IOException {
//        send('<' + msg + '>');
        send('<' + "col" + '#' + color + '>' );
        // wysłać z STX EXT
        // dac do enuma z protokołem TODO

    }

    // wiecej funkcji zwracajacych protoków, kolorm raibow itd ... TODO

    /* Getters and setters */

    public boolean isConnected() {
        return isConnected;
    }

    private void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getmAddress() {
        return mAddress;
    }

    public String getmName() {
        return mName;
    }


    /**
     * Returned value is seen in list of bluetooth devices
     * @return name od device
     */
    @Override
    public String toString() {
        return "chmura";
    }


}
