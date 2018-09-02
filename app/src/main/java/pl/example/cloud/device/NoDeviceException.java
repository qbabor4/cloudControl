package pl.example.cloud.device;

class NoDeviceException extends RuntimeException {

    public NoDeviceException(String message) {
        super(message);
    }
}
