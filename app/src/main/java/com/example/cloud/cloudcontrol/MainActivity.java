package com.example.cloud.cloudcontrol;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
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
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/** TODO
    - zrobic z tego bibliotekę i ją importować.
    - skalowac zdjecie ( match_parent? ) do ekranu, zeby było jak najwieksze ( kilka pixeli zeby było z po bokach
    - moze sliderlayout  na ekranie ( mniejsze rozdzielczosci ucinaja. Jak to zrobić jak ktoś ma mniejszy ekran?
    - Bluetooth low Energy
    - dopisac listenery do xml
    - disabled na buttonie search jak szuka
    - okragłe logo (manifest)
    - zobaczyc jak zrobić ruchomy splash screen
    - błąd IOexception wtedy gdy chce sam połaczyc sie z HC-06 jak on nie jest aktywny
    - zobaczyc czy ktos juz sie nie połączył z chmurą z menu górnego androida
    - jak nie sparowane, to albo sparowac albo zobaczyc jak to wyglada w apce
    - jak sie nacisnie "tak" na "czy włączyc bluetooth", to jest czasami "unfortunatelly bluetooth has stoped" (potem jest ok, bo działa)
    - mozna napisac, ze automatycznie łączy sie ze sparowaną chmurą
    - jak po dłuższym czasie sie znowu przywraca okno, to chmura sie crashuje
    - dodać cień do guzików i dodać guziki
    - zmienic kolor prewiew elipse ( tak jak zmieniłem *0.65 overlay ellipse
    - zmiana guzikia włacz/wyłącz na inny jak sie kliknie i powrót jak się dotknie koła albo suwaka
 */
public class MainActivity extends AppCompatActivity {


    private int hue = 0; // 0-360
    private double saturation = 0; // 0-1
    private double value = 1; // 0-1

    private int red = 255; // 0-255
    private int green = 255; // 0-255
    private int blue = 255; // 0-255

    private double hsvCircleRadius;

    private boolean isTurnedOn = true; // zmienic nazwę na guzika nazwę

    private ConnectionService mConnectionService;

    private CloudDevice mCloudDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFinalHsvCircleRadius();
        hsvCircleImageOnClick();
        onSeekBarChange();

        Intent intent = new Intent(this, ConnectionService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


    }

//    @Override
//    public void onBackPressed() {
//        // don't let user use back button because when he does and brings app back it goes to bluetooth connecting ( cloud is already paired and can't par again ) // TODO zmienic zeby mozna było
//        // zrobić tak, żeby wywalało bluetooth activity i przechodziło do tego
//    }

    public void onOnOffClick(View view){
        // zrobić listener tutaj a nie w xml TODO
        try {
            if (isTurnedOn) {
                mCloudDevice.sendMessage(Colors.BLACK.getColor());
                isTurnedOn = false;
            } else {
                mCloudDevice.sendMessage(changeRGBColorTOHex(red, green, blue));
                isTurnedOn = true;
            }
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Not Send", Toast.LENGTH_SHORT).show();
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /* We've bound to LocalService, cast the IBinder and get LocalService instance */
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mConnectionService = binder.getService();
            Log.d("lol12", "lol444444");
            mCloudDevice = mConnectionService.getConnectedCloudDevice();
            Log.d("lol123", mConnectionService.getConnectedCloudDevice() + "");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private void setFinalHsvCircleRadius(){
        final ImageView hsvCircleImgView = (ImageView) findViewById(R.id.hsvCircleImage); // globalnie jakoś TODO
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
                        String hexColor = changeRGBColorTOHex(red, green, blue);
                        try {
                            mCloudDevice.sendMessage(changeRGBColorTOHex(red, green, blue)); // send color
                            Log.d(hexColor, "hexColor");
                            Log.d(String.valueOf(red), "r");
                            Log.d(String.valueOf(green), "g");
                            Log.d(String.valueOf(blue), "b");
                        } catch (IOException e){
                            Toast.makeText(getApplicationContext(), "Not Sent", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
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
        ImageView previewEllipse = (ImageView)findViewById(R.id.prewiew_ellipse); // to może globalnie ?? TODO
        previewEllipse.setColorFilter(Color.rgb(red, green, blue));
    }

    private void changeHsvCircleBlackOverlaysAlpha(int progress){
        final ImageView hsvCircleBlackOverlay = (ImageView) findViewById(R.id.hsvCircleBlackOverlay);
        float imageAlpha = (1 - (float)progress  / 100 ) * (float)0.65 ;
        hsvCircleBlackOverlay.setAlpha(imageAlpha);
    }

    //changes rgb values to hex string, with addition of zeros, when there is only 1 char per one of 3 colors f.e. #ffaff (one a, not a0) // zobaczyc czy tak robi faktycznie
    private String changeRGBColorTOHex(int red, int green, int blue){
        return decToHex(red) + decToHex(green) + decToHex(blue);
    }

    private String decToHex(int decColor){
        String hexColor = "";
        char hexArray[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        hexColor += hexArray[ decColor % 16 ];
        decColor = decColor / 16;
        hexColor = hexArray[ decColor % 16 ] + hexColor;

        return hexColor;
    }

    private void onSeekBarChange(){
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1); // TODO globalnie
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeHsvCircleBlackOverlaysAlpha(progress);
                value = progress /100.; // tu dać seter TODO
                setRgbVariables();
                changePreviewEllipseColor();
                try {
                    mCloudDevice.sendMessage(changeRGBColorTOHex(red, green, blue));

                } catch (IOException e){
                    Toast.makeText(getApplicationContext(), "Sending Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection); // może być problem jak sie bedzie przechodziło do innych activity 
    }
}
