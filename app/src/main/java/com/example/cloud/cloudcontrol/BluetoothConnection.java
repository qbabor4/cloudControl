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
 * za piewszym razem moze pokazywac uzytkownikowi tę chmurę z którą chce sparować , a potem automatycznie
 */
public class BluetoothConnection  extends AppCompatActivity {

    final int REQUEST_ENABLE_BT = 1;

    private ConnectionService mConnectionService;

    BluetoothAdapter mBluetoothAdapter;

    ArrayList<CloudDevice> listItems = new ArrayList<>();

    ArrayAdapter<CloudDevice> adapter; // moze sie da nie globalnie TODO

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

        setListView();
        mBluetoothAdapter = getBluetoothAdapter(); // moze nie globalnie, tylko przekazywac

        registerBluetoothReceiver();

        try {
            addPairedDevicesToList(); // looks for paired devices // TODO niech zwraca listę może
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Błąd IOException", Toast.LENGTH_LONG).show();
        }

        /* Bind to ConnectionService */
        Intent intent = new Intent(this, ConnectionService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    /**
     * Register for broadcasts when a device is discovered.
     */
    private void registerBluetoothReceiver(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /* We've bound to LocalService, cast the IBinder and get LocalService instance */
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mConnectionService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void setListView(){
        listView = (ListView) findViewById(R.id.listView_devices);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    mConnectionService.connectDevice(adapter.getItem(position));
                    goToDeviceControllActivity();
                } catch (IOException ex){
                    Toast.makeText(getApplicationContext(), "Nie udało się połączyć z urządzeniem", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goToDeviceControllActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addPairedDevicesToList() throws IOException {

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
            // zobaczyc czy aktywował bluetooth TODO może
        } else {
            /* when bluetooth is already enabled */
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                /* There are paired devices. */
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(EDefaultData.BLUETOOTH_DEVICE_NAME.getData())) { // bedzie zmienione na moją nazwę
                        // jak sie zmieni adress w ardiuno, to powino sie dac tutaj odczytac i po tym identyfikować

                        adapter.add(new CloudDevice(device));  // moze sie da dodawac do listy, a nie adaptera TODO
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
        /* Check which request we're responding to */
        if ( requestCode == REQUEST_ENABLE_BT){
            /* Make sure the request was successful */
            if(resultCode != RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Yes"+ resultCode, Toast.LENGTH_LONG).show();
                // szuakać paired devices, a jak nie połączy z chmurą, to szukać aktywnych TODO
            }
            else { // 0
                Toast.makeText(getApplicationContext(), "canceled" + resultCode, Toast.LENGTH_LONG).show();
                // Do nothing
            }
        }
    }

    private void searchForPairedDevices(){

    }

    // nie wiem po co to TODO
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
        Log.d("lol12", "KONIECCCC!!!");
//        unbindService(mConnection); // to chyba nie ... TODO
        // unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver); // zobaczyc co to robi TODO
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
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null ) {
            /* When there is no bluetooth module */
            Toast.makeText( getApplicationContext(), "Moduł bluetooth nie został wykryty", Toast.LENGTH_LONG).show();
            finish();
        }
        return bluetoothAdapter;
    }

}
