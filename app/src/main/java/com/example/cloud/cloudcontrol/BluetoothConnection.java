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

    final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice = null;
    static OutputStream mmOutputStream = null;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);
        setOnItemClickListenerOnListView();
        //setFinalHsvCircleRadius();

        mBluetoothAdapter = getBluetoothAdapter();

        try {
            addPairedDevicesToList(); // looks for paired devices
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
        // jak kliknie na coś z listy
    }

    private void addPairedDevicesToList() throws IOException {
        Toast.makeText(getApplicationContext(), "Szukam", Toast.LENGTH_SHORT).show();


        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
            // zobaczyc czy aktywował bluetooth
        } else {
            // when bluetooth is already enabled
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    //Toast.makeText(getApplicationContext(), deviceName, Toast.LENGTH_LONG).show();

                    if (device.getName().equals("HC-06")) {
                        adapter.add(deviceName);
                        mmDevice = device;
                        //Toast.makeText(getApplicationContext(), "Device found!!!", Toast.LENGTH_LONG).show();
                        // próbuje połączyc z tym ( moze byc zparowane ale nie włączone )
                        try {
                            connectToCloud();
                            Toast.makeText(getApplicationContext(), "Urządzenie aktywne, połączono", Toast.LENGTH_LONG).show();
                            // przejsc do koła hsv
                        } catch (IOException e){
                            // bluetooth module in cloud is not on
                            Toast.makeText(getApplicationContext(), "Urządzenie nieaktywne", Toast.LENGTH_LONG).show();
                            //e.printStackTrace();

                        }
                    }

                }

            } else {
                Toast.makeText(getApplicationContext(), "No paired devices found", Toast.LENGTH_LONG).show();
            }

        }

    }

    private void connectToCloud() throws IOException{
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        BluetoothSocket mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // jak tu wejdzie przez szukanie, to rozróznić ( patrzec na liste ze sparowanymi?)

        // Check which request we're responding to
        if ( requestCode == REQUEST_ENABLE_BT){
            // Make sure the request was successful
            if(resultCode != RESULT_CANCELED){ // -1
            //if (resultCode == RESULT_OK){
                Toast.makeText(getApplicationContext(), "Nie canceled"+ resultCode, Toast.LENGTH_LONG).show();
                // szuakać paired devices, a jak nie połączy z chmurą, to szukać aktywnych
            }
            else { // 0
                Toast.makeText(getApplicationContext(), "canceled" + resultCode, Toast.LENGTH_LONG).show();
                // nic nie robić
            }
        }
    }

    private void searchForPairedDevices(){

    }

    public void searchForBluetoothDevices(View view) throws IOException {
        // skanuje, żeby znaleźć nowe urzadznia
        // sprawdzic czy bluetooth jest aktywny

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
