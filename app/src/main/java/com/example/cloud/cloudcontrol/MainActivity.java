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
        - dodac activity do manifestu
        - dodac activity inaczej https://developer.android.com/training/basics/firstapp/starting-activity.html
        - zmienic activity intentem
        - wyswietlić logo jak sie będzie włączać apka
        - najpierw logo jako main. potem przechodzi na
        - portrait w kazdym activity czy tylko w 1 miejscu w manifescie?
        - zmiana nazwy tego pliku i xml tu i w manifescie na control_screen
        - okragłe logo (manifest)
        - zrobic ten splash ze stylem w drawable na poczatku a tak jak jest teraz z wątkiem
        - dopisac tam do bitmapy obrazek
        - jak mam kilka startActivityForResult to jak zrobić do nich osobne onActivityResult onActivityResult()?
        - sprawdzic jak mozna inaczej niż po nazwie zobaczyc czy to moje urządzenie
        - błąd IOexception chyba wtedy gdy chce sam połaczyc sie z HC-06 jak on nie jest aktywny
        - zobaczyc czy ktos juz sie nie połączył z chmurą z menu górnego androida
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



        // pokazac activity logo na poczatku.
        // przejsc z niego od razu do bluetootha
        // stamtad do hsv jak ok

        //hsvCircleImageOnClick();
        //onSeekBarChange();
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
                            BluetoothConnection.sendData("lol");
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
