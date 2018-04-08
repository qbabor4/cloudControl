package com.example.cloud.cloudcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * TODO: zrobić nie statyczne wysyłanie, tylko z tworzeniem obietku
 * jak tylko 1 urzadzenie, to od razu połącz
 * dodać navigation drawer i moze toolbar
 * zrobic 2 urzadzenie i sprobowac laczys sie tylko z 1 a nie z 2
 * zmianic nazwę mainActivity
 * zrobić jakąś animację łączenia może.. TODO
 */
public class BluetoothConnection  extends AppCompatActivity {

    final int REQUEST_ENABLE_BT = 1; // po co ? TODO

    private ConnectionService mConnectionService;
    private boolean mBound = false;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice = null;
//    static OutputStream mmOutputStream = null;

    ArrayList<CloudDevice> listItems = new ArrayList<>();

    ArrayAdapter<CloudDevice> adapter; // moze sie da nie globalnie TODO

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
//        setListAdapter(adapter);
        setListView();

        mBluetoothAdapter = getBluetoothAdapter(); // moze nie globalnie, tylko przekazywac

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

        /* Bind to ConnectionService */
        Intent intent = new Intent(this, ConnectionService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d("lol12", "bind " + mBound);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /* We've bound to LocalService, cast the IBinder and get LocalService instance */
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mConnectionService = binder.getService();
            Log.d("lol12", "lol");
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private void setListView(){
        listView = (ListView) findViewById(R.id.listView_devices);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), position + " "  + parent + "", Toast.LENGTH_LONG).show();

                CloudDevice cloudDevice = adapter.getItem(position);
                Toast.makeText(getApplicationContext(), cloudDevice + "", Toast.LENGTH_LONG).show();

                Log.d("lol12", mConnectionService.getRandomNumber() + " " + mBound);

                try{
                    mConnectionService.connectDevice(cloudDevice);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (IOException ex){
                    Toast.makeText(getApplicationContext(), "Nie udało się połączyć z urządzeniem", Toast.LENGTH_LONG).show();
                    Log.d("error", ex.toString());
                }
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

            // zobaczyć bonded state jak jest sparowane i nie jest TODO

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    //Toast.makeText(getApplicationContext(), deviceName, Toast.LENGTH_LONG).show();

                    if (device.getName().equals(EDefaultData.BLUETOOTH_DEVICE_NAME.getData())) { // bedzie zmienione na moją nazwę
                        Log.d("mac", device.getAddress() + "\n" + device.getBondState() );

                        // jak sie zmieni adress w ardiuno, to powino sie dac tutaj odczytac i po tym identyfikować

                        adapter.add(new CloudDevice(device));  // moze sie da dodawac do listy, a nie adaptera TODO
//                        mmDevice = device;

                        // zapisac cos tutaj w service i odczytac w kole hsv TODO

                        //Toast.makeText(getApplicationContext(), "Device found!!!", Toast.LENGTH_LONG).show();
                        // próbuje połączyc z tym ( moze byc sparowane ale nie włączone )
//                        try {
//                            connectToCloud();
//                            Toast.makeText(getApplicationContext(), "Urządzenie aktywne, połączono", Toast.LENGTH_LONG).show();
//                            // changes activity to main
//
//                            Intent mainIntent = new Intent(this, MainActivity.class);
//                            startActivity(mainIntent);
//                            finish();
//
//                        } catch (IOException e){
//                            // bluetooth module in cloud is not on
//                            Toast.makeText(getApplicationContext(), "Urządzenie nieaktywne", Toast.LENGTH_LONG).show();
//                            //e.printStackTrace();
//                        }
                    }
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "No paired devices found", Toast.LENGTH_LONG).show();
            }
        }
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
//                adapter.add(deviceName);
                adapter.notifyDataSetChanged(); // to zobaczyc czy nie trzeba przy parowaniu
                if (device.getName().equals(EDefaultData.BLUETOOTH_DEVICE_NAME.getData())) {
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
//        unbindService(mConnection); // to chyba nie ... TODO
//        mBound = false;
        // unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }

    public void searchForBluetoothDevices(){
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

//    private static void sendData(String msg) throws IOException {
//        mmOutputStream.write(msg.getBytes());
//    }
//
//    /**
//     * Sends message with protocol known to cloud code on arduino
//     * @param message
//     * @throws IOException
//     */
//    public static void sendMessage(String message) throws IOException { // zrobić z stx i ext
//        sendData('#' + message + '>');
//    }
//
//    public static void sendStartRainbow() throws  IOException {
////        sendMessage();
//    }
}
