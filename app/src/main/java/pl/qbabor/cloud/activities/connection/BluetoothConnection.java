package pl.qbabor.cloud.activities.connection;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import pl.qbabor.cloud.activities.controller.CloudController;
import pl.qbabor.cloud.helpers.EDefaultData;
import com.example.cloud.cloudcontrol.R;
import pl.qbabor.cloud.device.CloudDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static pl.qbabor.cloud.activities.settings.Settings.PREFS_NAME;
import static pl.qbabor.cloud.activities.settings.Settings.PREF_DARK_THEME;

/**
 * TODO:
 * zrobic 2 urzadzenie i sprobowac laczys sie tylko z 1 a nie z 2
 * angielski w opcjach
 * zwiekszyć zasięg łączenia z bluetoothem
 *
 */
public class BluetoothConnection extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    final int REQUEST_ENABLE_BT = 1;

    private ListView listView;
    private ArrayList<CloudDevice> listItems = new ArrayList<>();
    private ArrayAdapter<CloudDevice> mAdapter;

    private Button btnRefreshPairedDevices;

    private ConnectionService mConnectionService;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); /* Splash screen */

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

        setBluetoothAdapter(getBluetoothAdapter());
        setComponents();
        addPairedDevicesToList();
        bindToConnectionService();

    }

    private void connectIfOnlyOneDeviceFound(){
        if (isOnlyOneDevice()){
            try {
                mConnectionService.connectDevice(mAdapter.getItem(0));
                goToCloudControllerActivity();
            } catch (IOException ex){
                ex.printStackTrace();
                Log.d("k12", ex.getMessage());
            }
        }
    }

    private void connectIfOnlyOneDeviceFoundWithProgressDialog(){
        if (isOnlyOneDevice()){
            connectWithProgressDialog(mAdapter.getItem(0));
        }
    }

    private boolean isOnlyOneDevice(){
        return listItems.size() == 1;
    }

    private BluetoothAdapter getBluetoothAdapter() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            /* When there is no bluetooth module */
            Toast.makeText(getApplicationContext(), "Moduł bluetooth nie został wykryty", Toast.LENGTH_LONG).show();
            finish();
        }
        return bluetoothAdapter;
    }

    private void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        mBluetoothAdapter = bluetoothAdapter;
    }

    private void bindToConnectionService() {
        /* Bind to ConnectionService */
        Intent intent = new Intent(this, ConnectionService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void setComponents() {
        setToolbar();
        setListView();
        setBtnRefreshPairedDevices();
    }


    private void setBtnRefreshPairedDevices() {
        btnRefreshPairedDevices = (Button) findViewById(R.id.refresh_paired_devices);
        btnRefreshPairedDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPairedDevicesToList();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_bluetooth_connection_menu, menu);
        return true;
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.bluetooth_connection_toolbar);
        setSupportActionBar(toolbar);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /* We've bound to LocalService, cast the IBinder and get LocalService instance */
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mConnectionService = binder.getService();
            connectIfOnlyOneDeviceFound();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private void setListView() {
        listView = (ListView) findViewById(R.id.listView_devices);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                connectWithProgressDialog(mAdapter.getItem(position));
            }
        });
    }

    private void connectWithProgressDialog(final CloudDevice cloudDevice){
        mProgressDialog = ProgressDialog.show(this, "Proszę czekać", "Łączę z chmurą...", true);
        new Thread() {
            @Override
            public void run() {
                try {
                    mConnectionService.connectDevice(cloudDevice);
                    goToCloudControllerActivity();
                } catch (IOException ex) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Nie udało się połączyć z urządzeniem. Spróbuj podejśc bliżej urządzenia i sprawdz czy jest włączone", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                    }
                });

            }
        }.start();
    }

    private void goToCloudControllerActivity() {
        Intent intent = new Intent(getApplicationContext(), CloudController.class);
        startActivity(intent);
        finish();
    }

    private void addPairedDevicesToList() {
        listItems.clear();
        if (!mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        } else {
            /* when bluetooth is already enabled */
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) { /* If there are paired devices. */
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(EDefaultData.BLUETOOTH_DEVICE_NAME.getData())) {
                        mAdapter.add(new CloudDevice(device));
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Nie znaleziono sparowanych urządzeń. Dodaj je w opcjach bloototh swojego smartfona", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* Check which request we're responding to */
        if (requestCode == REQUEST_ENABLE_BT) {
            /* Make sure the request was successful */
            if (resultCode != RESULT_CANCELED) {
                addPairedDevicesToList();
                if (mConnectionService != null){
                    connectIfOnlyOneDeviceFoundWithProgressDialog();
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lol12", "KONIECCCC!!!");
//        unbindService(mConnection); // to chyba nie ... TODO
    }


}
