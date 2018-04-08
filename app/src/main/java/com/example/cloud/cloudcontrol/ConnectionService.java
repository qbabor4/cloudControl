package com.example.cloud.cloudcontrol;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.LocalServerSocket;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created by Jakub on 08-Apr-18.
 */

public class ConnectionService extends Service{

    private final IBinder mBinder = new LocalBinder();

    private BluetoothDevice mDevice = null;
    private BluetoothSocket mSocket = null;
    private OutputStream mOutputStream = null;

    private CloudDevice connectedCloudDevice = null;

    private final Random mGenerator = new Random();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public int getRandomNumber(){
        return mGenerator.nextInt(100);
    }

    public void connectDevice(CloudDevice cloudDevice) throws IOException {
            cloudDevice.connect();
            setConnectedCloudDevice(cloudDevice);
    }

    public class LocalBinder extends Binder {

        /* Return this instance of LocalService so clients can call public methods */
        ConnectionService getService(){
            return ConnectionService.this;
        }

    }

    public BluetoothDevice getmDevice() {
        return mDevice;
    }

    private void setmDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
    }

    public BluetoothSocket getmSocket() {
        return mSocket;
    }

    private void setmSocket(BluetoothSocket mSocket) {
        this.mSocket = mSocket;
    }

    public OutputStream getmOutputStream() {
        return mOutputStream;
    }

    private void setmOutputStream(OutputStream mOutputStream) {
        this.mOutputStream = mOutputStream;
    }

    public CloudDevice getConnectedCloudDevice() {
        return connectedCloudDevice;
    }

    private void setConnectedCloudDevice(CloudDevice connectedCloudDevice) {
        this.connectedCloudDevice = connectedCloudDevice;
    }
}
