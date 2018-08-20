package com.example.cloud.cloudcontrol.com.example.cloud.device;

import com.example.cloud.protocol.ProtocolMessages;

import java.io.IOException;

public class CloudDeviceDaoImpl implements ICloudDeviceDao {

    @Override
    public void connect() throws IOException {

    }

    @Override
    public void disconnect() throws IOException {

    }

    @Override
    public void sendColor(String color) throws IOException {
//        send(ProtocolMessages.getColorMessage(color));
    }

    @Override
    public void sendRainbow(int brightness) throws IOException {

    }

    @Override
    public void sendAllTheSameChanging(int brightness) throws IOException {

    }
}
