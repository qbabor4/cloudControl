package pl.qbabor.cloud.device;

import java.io.IOException;

public interface ICloudDeviceActions {

    void setDevice(CloudDevice device);

    /**
     * Connects to device.
     * Sets socket and gets outputStream.
     *
     * @throws IOException when connection error occurs
     * @throws NoDeviceException when device is not set and is null
     */
    void connect() throws IOException, NoDeviceException;

    /**
     * Disconnects from device.
     * Closes outputstream and socket.
     * @throws IOException when error occurs while disconnecting
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
