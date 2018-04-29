package com.example.cloud.cloudcontrol;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Point;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;

/**
 * TODO
 * - zrobic z tego bibliotekę i ją importować.
 * - skalowac zdjecie ( match_parent? ) do ekranu, zeby było jak najwieksze ( kilka pixeli zeby było z po bokach
 * - moze sliderlayout  na ekranie ( mniejsze rozdzielczosci ucinaja. Jak to zrobić jak ktoś ma mniejszy ekran?
 * - Bluetooth low Energy
 * - dopisac listenery do xml
 * - disabled na buttonie search jak szuka
 * - okragłe logo (manifest)
 * - zobaczyc jak zrobić ruchomy splash screen
 * - błąd IOexception wtedy gdy chce sam połaczyc sie z HC-06 jak on nie jest aktywny
 * - zobaczyc czy ktos juz sie nie połączył z chmurą z menu górnego androida
 * - jak nie sparowane, to albo sparowac albo zobaczyc jak to wyglada w apce
 * - jak sie nacisnie "tak" na "czy włączyc bluetooth", to jest czasami "unfortunatelly bluetooth has stoped" (potem jest ok, bo działa)
 * - mozna napisac, ze automatycznie łączy sie ze sparowaną chmurą
 * - jak po dłuższym czasie sie znowu przywraca okno, to chmura sie crashuje
 * - dodać cień do guzików i dodać guziki
 * - zmienic kolor prewiew elipse ( tak jak zmieniłem *0.65 overlay ellipse
 * - zmiana guzikia włacz/wyłącz na inny jak sie kliknie i powrót jak się dotknie koła albo suwaka
 * zobaczyc czy wysyła jak sie kliknie na białe pole na zdjęciu
 * wraca do bluetootha jak sie wróci klawiszem wracającym i przywróci
 * jak tylko dotknę to nie zmienia sie kolor i chyba nie wysyła
 * jak sie klika na rainbow i onOFF to sie pokazuje ostatni kolor a nie gasi (przy klikaniu na rainbow trzeba ustawić jako ON chyba
 * setowac w obiekcie mHSVCircleRadius, zeby nie podawac cały czas
 * komunikat jak nic nie znajdzie, żeby sparował
 * Jak sie nie połączyc, to żeby podszedł bliżej
 * jak kliknałem najpierw on / off to potem nie reagował... (moze to przez serial print)
 * jak sie wybierze kolor na kole, to ma sie odznaczyc przycisk wylaczenia
 * na suwaku ma patrzec czy jest raibow, czy nie
 * jak sie zmienia kolor, to ma odznaczyc guziki
 * moze jakies opacity na wszystko jak sie wyłącza..
 */
public class CloudControll extends AppCompatActivity {


    private int mHue = 0; // 0-360
    private double mSaturation = 0; // 0-1
    private double mValue = 1; // 0-1

    private int mRed = 255; // 0-255
    private int mGreen = 255; // 0-255
    private int mBlue = 255; // 0-255

    private double mHSVCircleRadius;
    private int mPickedColorMarkerRadius;
    private int mDistanceFromLeftToIvHSVCircle;

    private boolean mIsBtnOnOffTurnedOn = true;
    private boolean mIsBtnRainbowTurnedOn = false;
    private boolean mIsBtnAllColorsChangingTurnedOn = false;

    private CloudDevice mCloudDevice = null;

    private ImageButton btnOnOff, btnRainbow, btnAllColorsChanging;
    private ImageView ivHSVCircle, ivPickedColorPreviewEllipse, ivHSVCircleBlackOverlay, ivPickedColorMarker;
    private SeekBar sbValueOfHSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_controll);

        initCloudDeviceService();
        initComponents();

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /* We've bound to LocalService, cast the IBinder and get LocalService instance */
            mCloudDevice = ((ConnectionService.LocalBinder) service).getService().getConnectedCloudDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void initCloudDeviceService() {
        Intent intent = new Intent(this, ConnectionService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void initComponents() {
        setBtnOnOff();
        setBtnRainbow();
        setBtnAllColorsChanging();
        setIvHSVCircle();
        setPickedColorPreviewEllipse();
        setIvHSVCircleBlackOverlay();
        setIvPickedColorMarker();
        setSbValueOfHSV();
    }


    private void setBtnOnOff() {
        btnOnOff = (ImageButton) findViewById(R.id.btn_on_off);
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnOnOffClick(v);
            }
        });
    }

    public void onBtnOnOffClick(View view) { // zmiana image na wyłączony TODO
        try {
            if (mIsBtnOnOffTurnedOn) {
                mCloudDevice.sendColor(Colors.BLACK.getColor());
                btnOnOff.setImageResource(R.drawable.button_01_pressed);
            } else {
                mCloudDevice.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
                btnOnOff.setImageResource(R.drawable.button_01);
            }
            mIsBtnOnOffTurnedOn = !mIsBtnOnOffTurnedOn;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Nie udało się wysłać danych", Toast.LENGTH_SHORT).show();
        }
    }

    private void setOtherButtonsOff(ImageButton pressedButton){
        // odznaczac pozostałe jak kliknie pressedButton TODO
        //zmienne i zdjecia
    }

    private void setBtnRainbow(){
        btnRainbow = (ImageButton) findViewById(R.id.btn_rainbow);
        btnRainbow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnRainbowClick(v);
            }
        });
    }

    private void onBtnRainbowClick(View v){
        try{
            if (!mIsBtnRainbowTurnedOn) {
                mCloudDevice.sendRainbow();
                btnRainbow.setImageResource(R.drawable.button_03_pressed);
            } else {
                /* go back to previous color */
                mCloudDevice.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
                btnRainbow.setImageResource(R.drawable.button_03);
            }
            mIsBtnRainbowTurnedOn = !mIsBtnRainbowTurnedOn;
        }catch (IOException ex){
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), "Nie udało się wysłać danych", Toast.LENGTH_SHORT).show();
        }
    }

    private void setBtnAllColorsChanging(){
        btnAllColorsChanging = (ImageButton) findViewById(R.id.btn_all_colors_changing);
        btnAllColorsChanging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnBtnAllColorsChangingClick(v);
            }
        });
    }

    private void OnBtnAllColorsChangingClick(View v){
        if(!mIsBtnAllColorsChangingTurnedOn){
            // wyslij polecenie ze zmianą
            btnAllColorsChanging.setImageResource(R.drawable.button_02_pressed);
        } else {
            btnAllColorsChanging.setImageResource(R.drawable.button_02);
        }
        mIsBtnAllColorsChangingTurnedOn = !mIsBtnAllColorsChangingTurnedOn;
    }

    private void setIvHSVCircle() {
        ivHSVCircle = (ImageView) findViewById(R.id.HSV_circle);
        ivHSVCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return ivHSVCircleOnTouchListener(v, event);
            }
        });
        setFinalHsvCircleRadius();
    }

    private boolean ivHSVCircleOnTouchListener(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        // jest coś zrąbane z pokazywaniem markera na ekranie ... TODO


        Log.d("k1", x + " OUT " + y);
        // ustawic na sr
        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
            if(setRgbVariables(x, y))  {
                Log.d("k1", x + " " + y);
                changePickedColorMarkerPosition(x, y);
                changePreviewEllipseColor();
                try {
                    mCloudDevice.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue)); // send color
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Nie udało się wysłać danych", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private void setPickedColorPreviewEllipse(){
        ivPickedColorPreviewEllipse = (ImageView) findViewById(R.id.picked_color_preview_ellipse);
    }

    private void changePickedColorMarkerPosition(int x, int y){
        // margines koła HSV trzeba dodać TODO
        Log.d("k1", mDistanceFromLeftToIvHSVCircle + "");
        ivPickedColorMarker.setX(x -mPickedColorMarkerRadius + mDistanceFromLeftToIvHSVCircle/2);
        ivPickedColorMarker.setY(y -mPickedColorMarkerRadius);
    }

    private void changePreviewEllipseColor() {
        ivPickedColorPreviewEllipse.setColorFilter(Color.rgb(mRed, mGreen, mBlue));
    }

    /**
     * Sets HSVCircle radius. Creates ViewTreeObserver to get width od image with HSV Circle.
     */
    private void setFinalHsvCircleRadius() {
        final ViewTreeObserver vto = ivHSVCircle.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mHSVCircleRadius = ivHSVCircle.getMeasuredWidth() / 2;
                Log.d("k2", mHSVCircleRadius + "");

                // tu chyba nie widzi jeszcze width main_layout. moze dac do zmiennych i na koncu ustawiac mDistanceFromLeftToIvHSVCircle TODO
                // obliczył 1020 a nie 1080 i moze przez to sie chrzani marker .

                mDistanceFromLeftToIvHSVCircle = getDistanceFromLeftToIvHSVCircle(ivHSVCircle.getMeasuredWidth());
                ivHSVCircle.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    /**
     * Sets RGB variables if touch coordinates were on HSV Circle.
     *
     * @param x x value of touch on HSV Circle Image
     * @param y y value of touch on HSV Circle Image
     * @return true if variables were set - if x and y are contained in HSV Circle (without corner spaces) false if not
     */
    private boolean setRgbVariables(int x, int y) {
        double saturation = HsvRgbCalculations.getSaturation(HsvRgbCalculations.getDistanceFromCenter(x, y, mHSVCircleRadius), mHSVCircleRadius);
        // chyba źle liczy saturację, bo marker nie dochodzi po konca prawej krawedzi (do lewej dochodzi do konca)
        Log.d("k3", saturation + "");
        if (saturation <= 1) {
            mSaturation = saturation;
            mHue = HsvRgbCalculations.getHue(x, y, mHSVCircleRadius);
            int rgbColors[] = HsvRgbCalculations.hsvToRgb(mHue, mSaturation, mValue);
            mRed = rgbColors[0];
            mGreen = rgbColors[1];
            mBlue = rgbColors[2];
            Log.d("k3", "true");
            return true;
        }
        Log.d("k3", "false");
        // chyba źle zwraca
        return false;
    }

    private void setRgbVariables() {
            int rgbColors[] = HsvRgbCalculations.hsvToRgb(mHue, mSaturation, mValue);
            this.mRed = rgbColors[0];
            this.mGreen = rgbColors[1];
            this.mBlue = rgbColors[2];
    }

    private void setIvHSVCircleBlackOverlay(){
        ivHSVCircleBlackOverlay = (ImageView) findViewById(R.id.HSV_circle_black_overlay);
    }

    private void setIvPickedColorMarker(){
        ivPickedColorMarker = (ImageView) findViewById(R.id.picked_color_marker);
        setPickedColorMarkerRadius();
    }

    private void setPickedColorMarkerRadius(){
        final ViewTreeObserver vto = ivPickedColorMarker.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mPickedColorMarkerRadius = ivPickedColorMarker.getMeasuredWidth() / 2;
                ivPickedColorMarker.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    private int getDistanceFromLeftToIvHSVCircle(int ivHSVCircleWidth){
        getDisplayWidth();
        Log.d("k2", lol + "");
        return (lol - ivHSVCircleWidth) /2;
    }

    private int lol;

    private LinearLayout myLayout;

    private void getDisplayWidth(){
        myLayout = (LinearLayout) findViewById(R.id.main_layout);
        myLayout.post(new Runnable()
        {

            @Override
            public void run()
            {
                lol = myLayout.getWidth();
                Log.i("TEST", "Layout width : "+ myLayout.getWidth());

            }
        });


//    }
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        return size.x;
    }

    /**
     * Sets Alpha of balck overlay on top od HSV circle. (HSV circle on screen is more black)
     * @param progress progress of seekBar (0-100)
     */
    private void changeHsvCircleBlackOverlaysAlpha(int progress) {
        float imageAlpha = (1 - (float) progress / 100) * (float) 0.65;
        ivHSVCircleBlackOverlay.setAlpha(imageAlpha);
    }


    private void setSbValueOfHSV(){
        sbValueOfHSV = (SeekBar) findViewById(R.id.value_of_HSV);
        sbValueOfHSV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeHsvCircleBlackOverlaysAlpha(progress);
                setValue(progress);
                setRgbVariables();
                changePreviewEllipseColor();
                try {
                    mCloudDevice.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Nie udało się wysłać wiadomości (seekbar)", Toast.LENGTH_SHORT).show();
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

    private void setValue(int progress){
        mValue = progress / 100.;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection); // może być problem jak sie bedzie przechodziło do innych activity 
    }
}
