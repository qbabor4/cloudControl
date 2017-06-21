package com.example.cloud.cloudcontrol;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnection extends ListActivity {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice = null;
    static OutputStream mmOutputStream = null;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);
        setOnItemClickListenerOnListView();
        //setFinalHsvCircleRadius();

        mBluetoothAdapter = getBluetoothAdapter();

        try {
            // sam szuka zparowanych urzadzen i dodaje do listy
            addPairedDevicesToList();
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Błąd IOException", Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Inny Błąd", Toast.LENGTH_LONG).show();
        }
        // jak kliknie guzik, to skanuje nowe urządzenia i dodaje do listy

         /*try {
            bluetoothConnection();
        } catch (IOException e) {
            Context context = getApplicationContext();
            Toast.makeText(context, "Błąd bluetooth", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } */
    }

    private void setOnItemClickListenerOnListView(){
        //ListView listView = (ListView)findViewBy();
        //setOnItemClickListener
    }

    private void addPairedDevicesToList() throws IOException {
        Toast.makeText(getApplicationContext(), "Szukam", Toast.LENGTH_SHORT).show();
        final int REQUEST_ENABLE_BT = 1;

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
        }

        if (mBluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Toast.makeText(getApplicationContext(), deviceName, Toast.LENGTH_LONG).show();
                    if (device.getName().equals("HC-06")) {
                        mmDevice = device;
                        Toast.makeText(getApplicationContext(), "Device found!!!", Toast.LENGTH_LONG).show();

                    }
                    adapter.add(deviceName);
                }

            } else {
                Toast.makeText(getApplicationContext(), "No paired devices found", Toast.LENGTH_LONG).show();
            }

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            BluetoothSocket mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
        } else{
            Toast.makeText(getApplicationContext(), "Enable Bluetooth to get device", Toast.LENGTH_LONG).show();
        }
    }

    public void searchForBluetoothDevices(View view) throws IOException {
        // skanuje, żeby znaleźć nowe urzadznia

    }

    private BluetoothAdapter getBluetoothAdapter(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null ) {
            // jak bluetootha nie da sie właczyc ( nie ma )
            Context context = getApplicationContext();
            Toast.makeText(context, "Moduł bluetooth nie został wykryty", Toast.LENGTH_LONG).show();
            finish();
        }
        return mBluetoothAdapter;
    }

    void bluetoothConnection() throws IOException {

        final int REQUEST_ENABLE_BT = 1;

        if (!mBluetoothAdapter.isEnabled()) {
            // jak bluetooth nie jest enablied ( nieaktywna ikona )
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
            //onActivityResult(,,enableBluetoothIntent);

        }
        if (mBluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    //String deviceHardwareAddress = device.getAddress(); // MAC address
                    Toast.makeText(getApplicationContext(), deviceName, Toast.LENGTH_LONG).show();
                    if (device.getName().equals("HC-06")) {
                        mmDevice = device;
                        Toast.makeText(getApplicationContext(), "Device found!!!", Toast.LENGTH_LONG).show();
                        break;
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "No paired devices found", Toast.LENGTH_LONG).show();
            }

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            BluetoothSocket mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
        }
    }

    static void sendData(String msg) throws IOException {
        mmOutputStream.write("1".getBytes());
    }
}
