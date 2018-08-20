package com.example.cloud.cloudcontrol.com.example.cloud.device;

import java.io.IOException;

public interface ICloudDeviceDao {

    void connect() throws IOException;

    void disconnect() throws IOException;

    /**
     * Sends color command to device
     * @throws IOException
     */
    void sendColor(String color) throws IOException;

    /**
     * Sends rainbow command to device
     * @throws IOException
     */
    void sendRainbow(int brightness) throws IOException;

    void sendAllTheSameChanging(int brightness) throws IOException;

}
