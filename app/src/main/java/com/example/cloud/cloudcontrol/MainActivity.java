package com.example.cloud.cloudcontrol;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
//extends AppCompatActivity
public class MainActivity extends  ListActivity {
    /* TODO
        - skalowac zdjecie ( match_parent? ) do ekranu, zeby było jak najwieksze ( kilka pixeli zeby było z po bokach
        - moze sliderlayout  na ekranie ( mniejsze rozdzielczosci ucinaja
        - Bluetooth low Energy
        - onActivityResult zobaczyc co wybrał przy aktywacji bluetootha
        - bluetooth discovery
        - dac do paczki obie klasy
        - lista divajsów w innym activity i jak wybierze, to wraca do normalnego activity ( finish() i wraca do koła hsv )
        - pokazac liste ze sparowanymi urzadzeniami. Przycik szukaj urządzeń i parowanie
        - przełączyc z jednego layouta do drugiego
        - najpierw sam szuka zparowanych urządzeń
        - potem na guziku szuka nowych urzadzeń
        - dopisac listenery do xml
        - disabled na buttonie search jak szuka
     */

    public int hue = 0; // 0-360
    public double saturation = 0; // 0-1
    public double value = 1; // 0-1

    public int red = 255; // 0-255
    public int green = 255; // 0-255
    public int blue = 255; // 0-255

    private double hsvCircleRadius;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice = null;
    OutputStream mmOutputStream = null;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_connection);
        //setContentView(R.layout.activity_main);
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

        //hsvCircleImageOnClick();
        //onSeekBarChange();
    }

    private void setOnItemClickListenerOnListView(){
        ListView list = (ListView)findViewById(R.id.ListView01);
        //setOnItemClickListener
    }

    private void addPairedDevicesToList() throws IOException{
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



    private void sendData(String msg) throws IOException {
        mmOutputStream.write("1".getBytes());
    }



    private void setFinalHsvCircleRadius(){
        final ImageView hsvCircleImgView = (ImageView) findViewById(R.id.hsvCircleImage);
        ViewTreeObserver vto = hsvCircleImgView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                hsvCircleImgView.getViewTreeObserver().removeOnPreDrawListener(this);
                hsvCircleRadius = hsvCircleImgView.getMeasuredWidth() / 2;

                return true;
            }
        });
    }

    private void hsvCircleImageOnClick(){
        final ImageView hsvCircleImg =  (ImageView) findViewById(R.id.hsvCircleImage);
        hsvCircleImg.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int hsvCircleHeight = (int)hsvCircleRadius * 2; // same as Width

                if ( x > 0 && y > 0 && x < hsvCircleHeight && y < hsvCircleHeight) {
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        setRgbVariables(x, y);
                        changePreviewEllipseColor();
                        try {
                            sendData("lol");
                        } catch (IOException e){
                            Toast.makeText(getApplicationContext(), "Not Send", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                return true;
            }
        });
    }

    private void setRgbVariables(int x, int y){
        double distanceFromCenter = HsvRgbCalculations.getDistanceFromCenter(x, y, hsvCircleRadius);
        double saturation = HsvRgbCalculations.getSaturation( distanceFromCenter, hsvCircleRadius );
        if (saturation <= 1) {
            this.saturation = saturation;
            this.hue = HsvRgbCalculations.getHue(x, y, hsvCircleRadius);
            int rgbColors[] = HsvRgbCalculations.hsvToRgb( this.hue, this.saturation, this.value);
            this.red = rgbColors[0];
            this.green = rgbColors[1];
            this.blue = rgbColors[2];
        }
    }

    private void setRgbVariables(){
        if (saturation <= 1) {
            int rgbColors[] = HsvRgbCalculations.hsvToRgb( hue, saturation, value);
            this.red = rgbColors[0];
            this.green = rgbColors[1];
            this.blue = rgbColors[2];
        }
    }

    private void changePreviewEllipseColor(){
        ImageView previewEllipse = (ImageView)findViewById(R.id.prewiew_ellipse);
        previewEllipse.setColorFilter(Color.rgb(red, green, blue));
    }

    private void changeHsvCircleBlackOverlaysAlpha(int progress){
        final ImageView hsvCircleBlackOverlay = (ImageView) findViewById(R.id.hsvCircleBlackOverlay);
        float imageAlpha = 1 - (float)progress / 100;
        hsvCircleBlackOverlay.setAlpha(imageAlpha);
    }

    private void onSeekBarChange(){
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeHsvCircleBlackOverlaysAlpha(progress);
                value = progress /100.;
                setRgbVariables();
                changePreviewEllipseColor();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
