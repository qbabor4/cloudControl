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
 * Created by Jakub on 05-Apr-18.
 */

public class CloudDevice implements Parcelable {

    private boolean isConnected = false;

    private final BluetoothDevice mDevice;

    private final String mName;
    private final String mAddress;

    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;




    public CloudDevice(BluetoothDevice device) {
        this.mDevice = device;
        this.mName = device.getName();
        this.mAddress = device.getAddress();
    }

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
     * Sends message with correct protocol frame
     */
    public void sendMessage(String msg) throws IOException {
        send('#' + msg + '>');
    }

    /* Getters and setters */

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // tu jakos wpisac cału obiekt ?? TODO
    }
}
