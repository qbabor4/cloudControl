package com.example.cloud.cloudcontrol;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * TODO: zrobić nie statyczne wysyłanie, tylko z tworzeniem obietku
 */
public class BluetoothConnection  extends AppCompatActivity {

    final int REQUEST_ENABLE_BT = 1;

    private static final String deviceBluetoothName = "HC-06";

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice = null;
    static OutputStream mmOutputStream = null;

    /*LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS */
    ArrayList<String> listItems = new ArrayList<>();

    ArrayAdapter<String> adapter;

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
//        setListAdapter(adapter);
        setListView();

        mBluetoothAdapter = getBluetoothAdapter();

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        Log.d("start", "1");

        try {
            addPairedDevicesToList(); // looks for paired devices
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Błąd IOException", Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Inny Błąd", Toast.LENGTH_LONG).show();
        }

    }

    private void setListView(){
        listView = (ListView) findViewById(R.id.listView_devices);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), position + " "  + parent + "", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addPairedDevicesToList() throws IOException {

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
            // zobaczyc czy aktywował bluetooth
        } else {
            Toast.makeText(getApplicationContext(), "Szukam", Toast.LENGTH_SHORT).show();
            // when bluetooth is already enabled
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    //Toast.makeText(getApplicationContext(), deviceName, Toast.LENGTH_LONG).show();

                    if (device.getName().equals(deviceBluetoothName)) { // bedzie zmienione na moją nazwę
                        adapter.add(deviceName);  // moze sie da dodawac do listy, a nie adaptera TODO
                        mmDevice = device;

                        // tu bedzie nowy obiekt chmury TODO

                        //Toast.makeText(getApplicationContext(), "Device found!!!", Toast.LENGTH_LONG).show();
                        // próbuje połączyc z tym ( moze byc zparowane ale nie włączone )
                        try {
                            connectToCloud();
                            Toast.makeText(getApplicationContext(), "Urządzenie aktywne, połączono", Toast.LENGTH_LONG).show();
                            // changes activity to main

                            Intent mainIntent = new Intent(this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();

                        } catch (IOException e){
                            // bluetooth module in cloud is not on
                            Toast.makeText(getApplicationContext(), "Urządzenie nieaktywne", Toast.LENGTH_LONG).show();
                            //e.printStackTrace();
                        }
                    }
                }
            }
            else {
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
                Toast.makeText(getApplicationContext(), "Yes"+ resultCode, Toast.LENGTH_LONG).show();
                // szuakać paired devices, a jak nie połączy z chmurą, to szukać aktywnych
            }
            else { // 0
                Toast.makeText(getApplicationContext(), "canceled" + resultCode, Toast.LENGTH_LONG).show();
                // Do nothing
            }
        }
    }

    private void searchForPairedDevices(){

    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "onRecive", Toast.LENGTH_LONG).show();
            Log.d("szukam", "33454");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(getApplicationContext(), "jest jakiś", Toast.LENGTH_LONG).show();
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                adapter.add(deviceName);
                adapter.notifyDataSetChanged();
                if (device.getName().equals("HC-06")) {
                    //adapter.add(deviceName);
                    Toast.makeText(getApplicationContext(), "znalazłem!", Toast.LENGTH_LONG).show();
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }

    public void searchForBluetoothDevices(View view){
        // skanuje, żeby znaleźć nowe urzadznia
        // sprawdzic czy bluetooth jest aktywny
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT); // tu moze inny kod requesta ( inny int np. 2) i to potem sprawdzac w onActivityResult
            // zobaczyc czy aktywował bluetooth
            // jak tak to skanuje zeby znależć nowe urządzenia
        } else {
            Toast.makeText(getApplicationContext(), "Szukam nowych urządzeń", Toast.LENGTH_LONG).show();
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            mBluetoothAdapter.startDiscovery();
        }
    }

    private BluetoothAdapter getBluetoothAdapter(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null ) {
            // When there is no bluetooth module
            //Context context = getApplicationContext();
            Toast.makeText( getApplicationContext(), "Moduł bluetooth nie został wykryty", Toast.LENGTH_LONG).show();
            finish();
        }
        return mBluetoothAdapter;
    }

    private static void sendData(String msg) throws IOException {
        mmOutputStream.write(msg.getBytes());
    }

    /**
     * Sends message with protocol known to cloud code on arduino
     * @param message
     * @throws IOException
     */
    public static void sendMessage(String message) throws IOException {
        sendData('#' + message + '>');
    }

    public static void sendStartRainbow() throws  IOException {
//        sendMessage();
    }
}
