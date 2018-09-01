package com.example.cloud.cloudcontrol.com.example.cloud.device;

import com.example.cloud.cloudcontrol.EDefaultData;
import com.example.cloud.protocol.ProtocolMessages;

import java.io.IOException;
import java.util.UUID;

public class CloudDeviceActionsImpl implements ICloudDeviceActions {

    private CloudDevice device;

    @Override
    public void setDevice(CloudDevice device) {
        this.device = device;
    }


    @Override
    public void connect() throws IOException, NoDeviceException {
        if (device != null){
            device.setSocket(device.getDevice().createRfcommSocketToServiceRecord(UUID.fromString(EDefaultData.BLUETOOTH_MODULE_UUID.getData())));
            device.getSocket().connect();
        } else {
            throw new NoDeviceException("Device is not set");
        }

        device.setOutputStream(device.getSocket().getOutputStream());
        device.setConnected(true);
    }

    @Override
    public void disconnect() throws IOException {
        device.getOutputStream().close();
        device.getSocket().close();
        device.setConnected(false);
    }

    @Override
    public void sendColor(String color) throws IOException {
        send(ProtocolMessages.getColorMessage(color));
    }

    @Override
    public void sendRainbow(int brightness) throws IOException {
        send(ProtocolMessages.getRainbowMessage(brightness));
    }

    @Override
    public void sendAllTheSameChanging(int brightness) throws IOException {
        send(ProtocolMessages.getAllTheSameChangingMessage(brightness));
    }

    private void send(String msg) throws IOException {
        if (device != null && device.getOutputStream() != null) {
            device.getOutputStream().write(msg.getBytes());
        }
    }

}
