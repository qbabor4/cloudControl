package pl.example.cloud.cloudcontrol;

/**
 * Created by Jakub on 06-Apr-18.
 */

public enum EDefaultData {

    BLUETOOTH_DEVICE_NAME("HC-06"),
    BLUETOOTH_MODULE_UUID("00001101-0000-1000-8000-00805F9B34FB");

    private final String data;

    EDefaultData(String data){
        this.data = data;
    }

    public String getData(){
        return data;
    }

}
