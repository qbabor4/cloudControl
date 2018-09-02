package pl.example.cloud.device;

import java.io.IOException;

public interface ICloudDeviceActions {

    void setDevice(CloudDevice device);

    /**
     * Connects device
     * @throws IOException
     */
    void connect() throws IOException, NoDeviceException;

    /**
     * Disconnects device
     * @throws IOException
     */
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

    /**
     * Sends rainbow command to device with all leds in the same color
     * @throws IOException
     */
    void sendAllTheSameChanging(int brightness) throws IOException;


}
