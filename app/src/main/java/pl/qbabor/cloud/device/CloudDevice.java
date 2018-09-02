package pl.qbabor.cloud.device;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * !-- This is Singleton, because Socket and OutputStream are not serializable, and can't be passed with intent --!
 * Created by Jakub on 05-Apr-18.
 */
public class CloudDevice implements Serializable{

    private final String mName;

    private final String mAddress;


    private final BluetoothDevice mDevice;

    private BluetoothSocket mSocket;

    private OutputStream mOutputStream;


    private boolean isConnected = false;


    public CloudDevice(BluetoothDevice device) {
        this.mDevice = device;
        this.mName = device.getName();
        this.mAddress = device.getAddress();
    }

    /* Getters and setters */

    protected BluetoothDevice getDevice() {
        return mDevice;
    }

    protected BluetoothSocket getSocket() {
        return mSocket;
    }

    protected void setSocket(BluetoothSocket mSocket) {
        this.mSocket = mSocket;
    }

    protected OutputStream getOutputStream() {
        return mOutputStream;
    }

    protected void setOutputStream(OutputStream mOutputStream) {
        this.mOutputStream = mOutputStream;
    }

    public boolean isConnected() {
        return isConnected;
    }

    protected void setConnected(boolean connected) {
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
        return "Chmura";
    }


}
