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
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    /* TODO
        - skalowac zdjecie ( match_parent? ) do ekranu, zeby było jak najwieksze ( kilka pixeli zeby było z po bokach
        - moze sliderlayout  na ekranie ( mniejsze rozdzielczosci ucinaja
        - Bluetooth low Energy
        - bluetooth discovery
        - przełączyc z jednego layouta do drugiego
        - dopisac listenery do xml
        - disabled na buttonie search jak szuka
        - dodac activity inaczej https://developer.android.com/training/basics/firstapp/starting-activity.html
        - zmienic activity intentem
        - zmiana nazwy tego pliku i xml tu i w manifescie na control_screen
        - okragłe logo (manifest)
        - zobaczyc jak zrobić ruchomy splash screen
        - jak mam kilka startActivityForResult to jak zrobić do nich osobne onActivityResult onActivityResult()?
        - sprawdzic jak mozna inaczej niż po nazwie zobaczyc czy to moje urządzenie
        - błąd IOexception wtedy gdy chce sam połaczyc sie z HC-06 jak on nie jest aktywny
        - zobaczyc czy ktos juz sie nie połączył z chmurą z menu górnego androida
        - jak nie sparowane, to albo sparowac albo zobaczyc jak to wyglada w apce
        - jak sie nacisnie "tak" na "czy włączyc bluetooth", to jest czasami "unfortunatelly bluetooth has stoped" (potem jest ok, bo działa)
        - mozna napisac, ze automatycznie łączy sie ze sparowaną chmurą
        - zmianic connectToCloud() na takie jak w android bluetooth
        - zobaczyc jaki jest mac address moich hc-06
        - jak szuka to animacja ładowania ( obracajace sie kółko )
        - nie szuaka dobrze urządzeń...
        - wysylac ze znacznikim koncowym
        - przeniesc pliki arduino do folderu apki androida i dac do githuba
        - dodawac zera jak jest tylko 1 znak w hexStringu ( moze byc długosc 5 albo nawet 3
        - jak po dłuższym czasie sie znowu przywraca okno, to chmura sie crashuje
        - zamiana rgb na hex z dopisanymi zerami
     */

    public int hue = 0; // 0-360
    public double saturation = 0; // 0-1
    public double value = 1; // 0-1

    public int red = 255; // 0-255
    public int green = 255; // 0-255
    public int blue = 255; // 0-255

    private double hsvCircleRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFinalHsvCircleRadius();
        hsvCircleImageOnClick();
        onSeekBarChange();
    }

    @Override
    public void onBackPressed() {
        // don't let user use back button because when he does and brings app back it goes to bluetooth connecting ( cloud is already paired and can't par again )
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
                        String hexColor = changeRGBColorTOHex(red, green, blue);
                        try {
                            BluetoothConnection.sendData( hexColor + '>'); // send color; > is end marker
                            Log.d(hexColor, "hexColor");
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

    //changes rgb values to hex string, with addition of zeros, when there is only 1 char per one of 3 colors f.e. #ffaff (one a, not a0)
    private String changeRGBColorTOHex(int red, int green, int blue){
        String hexR = Integer.toHexString(red);
        hexR = checkHexData(hexR);
        String hexG = Integer.toHexString(green);
        hexG = checkHexData(hexG);
        String hexB = Integer.toHexString(blue);
        hexB = checkHexData(hexB);

        return hexR + hexG + hexB;
    }

    private String checkHexData(String hexColor){
        if (hexColor.length() == 1){
            hexColor+= "0";
        }

        return hexColor;
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
                String hexColor = changeRGBColorTOHex(red, green, blue);
                try {
                    BluetoothConnection.sendData( hexColor + '>'); // send color; > is end marker
                    Log.d(hexColor, "hexColor");
                } catch (IOException e){
                    Toast.makeText(getApplicationContext(), "Not Send", Toast.LENGTH_SHORT).show();
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
}
