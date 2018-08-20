package com.example.cloud.cloudcontrol;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.cloud.cloudcontrol.com.example.cloud.device.CloudDevice;

import java.io.IOException;

/**
 * Class to make service where connection of device can be accessed from many Activities.
 * Connected device can't be passed with intrnt because streams and sockets are not serialized.
 * <p>
 * Created by Jakub on 08-Apr-18.
 */
public class ConnectionService extends Service {

    private final IBinder mBinder = new LocalBinder();

    /**
     * Device that is connected to app
     */
    private CloudDevice connectedCloudDevice = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Connects device. If connection is successful, cloudDevice is set to connectedCloudDevice.
     *
     * @param cloudDevice device, that you want to connect with app.
     * @throws IOException when connection error occurs
     */
    public void connectDevice(CloudDevice cloudDevice) throws IOException {
        cloudDevice.connect();
        setConnectedCloudDevice(cloudDevice);
    }

    /**
     * Return this instance of LocalService so clients can call public methods.
     */
    public class LocalBinder extends Binder {

        ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    /**
     * @return connected device. null when no device is connected.
     */
    public CloudDevice getConnectedCloudDevice() {
        return connectedCloudDevice;
    }

    /**
     * sets device as connected device.
     *
     * @param connectedCloudDevice
     */
    private void setConnectedCloudDevice(CloudDevice connectedCloudDevice) {
        this.connectedCloudDevice = connectedCloudDevice;
    }
}
