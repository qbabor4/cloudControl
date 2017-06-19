package com.example.cloud.cloudcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    /* TODO
        - skalowac zdjecie ( match_parent? ) do ekranu, zeby było jak najwieksze ( kilka pixeli zeby było z po bokach
        - moze sliderlayout  na ekranie ( mniejsze rozdzielczosci ucinaja
        - Bluetooth low Energy
        - onActivityResult zobaczyc co wybrał przy aktywacji bluetootha
        - bluetooth discovery
        - dac do paczki obie klasy
        - lista divajsów w innym activity i jak wybierze, to wraca do normalnego activity ( finish() i wraca do koła hsv )
     */

    public int hue = 0; // 0-360
    public double saturation = 0; // 0-1
    public double value = 1; // 0-1

    public int red = 255; // 0-255
    public int green = 255; // 0-255
    public int blue = 255; // 0-255

    private double hsvCircleRadius;

    BluetoothDevice mmDevice = null;
    OutputStream mmOutputStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFinalHsvCircleRadius();
        Context context = getApplicationContext();

        try {
            bluetoothConnection(context);
        } catch (IOException e) {
            Toast.makeText(context, "Nie działa bluetooth", Toast.LENGTH_LONG).show();
        }

        hsvCircleImageOnClick();
        onSeekBarChange();
    }



    void bluetoothConnection(Context context) throws IOException {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final int REQUEST_ENABLE_BT = 1;

        if (mBluetoothAdapter == null ){
            // jak bluetootha nie da sie właczyc ( nie ma )
            CharSequence text = "Bluetooth not available";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            finish();

        } else {

            if (!mBluetoothAdapter.isEnabled()) {
                // jak bluetooth nie jest enablied ( nieaktywna ikona )
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
                //onActivityResult(,,enableBluetoothIntent);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0){
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    //String deviceHardwareAddress = device.getAddress(); // MAC address
                    Toast.makeText(context, deviceName, Toast.LENGTH_LONG).show();
                    if(device.getName().equals("HC-06"))
                    {
                        mmDevice = device;
                        Toast.makeText(context, "Device found!!!", Toast.LENGTH_LONG).show();
                        break;
                    }
                }

            }
            else {
                Toast.makeText(context, "No paired devices found", Toast.LENGTH_LONG).show();
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
