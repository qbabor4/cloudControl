package com.example.cloud.cloudcontrol;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;

/**
 * TODO
 * okragłe logo (manifest)
 * animacja łączenia jak łączy z chmurą (bo sie zawiesza) nie odpowiada aplikacja czasami
 * zobaczyc czy ktos juz sie nie połączył z chmurą z menu górnego androida
 * mozna napisac, ze automatycznie łączy sie ze sparowaną chmurą w instrukcji
 *
 * jak sie bedzie dało wysłać, to można próbować znowu łączyć z tą chmurą
 * języki, ciemny mode, przejscie do łączenia (jak bedzie chcial inna chmure połączyć) w ustawieniach
 * zapisac wybory uzytkownika czy dark theme chce (sharedpreferences)
 * toolbar koloru drewna (i moze tekstura też)
 *
 * TODO IFTIME:
 * zrobic z tego bibliotekę i ją importować.
 */
public class CloudController extends AppCompatActivity {

    private int mHue = 0; // 0-360
    private double mSaturation = 0; // 0-1
    private double mValue = 1; // 0-1

    private int mRed = 255; // 0-255
    private int mGreen = 255; // 0-255
    private int mBlue = 255; // 0-255

    private double mHSVCircleRadius;
    private int mPickedColorMarkerRadius;

    private boolean mIsBtnOnOffPressed = false;
    private boolean mIsBtnRainbowPressed = false;
    private boolean mIsBtnAllColorsChangingPressed = false;

    private CloudDevice mCloudDevice = null;

    private ImageButton btnOnOff, btnRainbow, btnAllColorsChanging;
    private ImageView ivHSVCircle, ivPickedColorPreviewEllipse, ivHSVCircleBlackOverlay, ivPickedColorMarker;
    private SeekBar sbValueOfHSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_controller);

        initCloudDeviceService();
        initComponents();

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_cloud_controll_menu, menu);
        return true;
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
        setToolbar();
        setBtnOnOff();
        setBtnRainbow();
        setBtnAllColorsChanging();
        setIvHSVCircle();
        setPickedColorPreviewEllipse();
        setIvHSVCircleBlackOverlay();
        setIvPickedColorMarker();
        setSbValueOfHSV();
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.cloud_controller_toolbar);
        setSupportActionBar(toolbar);
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

    public void onBtnOnOffClick(View view) {
        try {
            if (mIsBtnOnOffPressed) {
                mCloudDevice.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
                btnOnOff.setImageResource(R.drawable.button_01);
            } else {
                mCloudDevice.sendColor(Colors.BLACK.getColor());
                btnOnOff.setImageResource(R.drawable.button_01_pressed);
                setOtherButtonsUnpressed(btnOnOff);
            }
            mIsBtnOnOffPressed = !mIsBtnOnOffPressed;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Nie udało się wysłać danych", Toast.LENGTH_SHORT).show();
        }
    }

    private void setOtherButtonsUnpressed(Object pressedButton){
        if (!(pressedButton).equals(btnOnOff)){
            unpressBtnOnOff();
        }
        if (!pressedButton.equals(btnRainbow)){
            unpressBtnRainbow();
        }
        if (!pressedButton.equals(btnAllColorsChanging)){
            unpressBtnAllColorsChanging();
        }
    }

    private void unpressBtnOnOff(){
        mIsBtnOnOffPressed = false;
        btnOnOff.setImageResource(R.drawable.button_01);
    }

    private void unpressBtnRainbow(){
        mIsBtnRainbowPressed = false;
        btnRainbow.setImageResource(R.drawable.button_03);
    }

    private void unpressBtnAllColorsChanging(){
        mIsBtnAllColorsChangingPressed = false;
        btnAllColorsChanging.setImageResource(R.drawable.button_02);
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
            if (!mIsBtnRainbowPressed) {
                mCloudDevice.sendRainbow(HsvRgbCalculations.getBrightness(mValue));
                btnRainbow.setImageResource(R.drawable.button_03_pressed);
                setOtherButtonsUnpressed(btnRainbow);
            } else {
                /* Show previous color */
                mCloudDevice.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
                btnRainbow.setImageResource(R.drawable.button_03);
            }
            mIsBtnRainbowPressed = !mIsBtnRainbowPressed;
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
        try{
            if(!mIsBtnAllColorsChangingPressed){
                mCloudDevice.sendAllTheSameChanging(HsvRgbCalculations.getBrightness(mValue));
                btnAllColorsChanging.setImageResource(R.drawable.button_02_pressed);
                setOtherButtonsUnpressed(btnAllColorsChanging);
            } else {
                mCloudDevice.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
                btnAllColorsChanging.setImageResource(R.drawable.button_02);
            }
            mIsBtnAllColorsChangingPressed = !mIsBtnAllColorsChangingPressed;
        } catch (IOException ex){
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), "Nie udało się wysłać danych", Toast.LENGTH_SHORT).show();
        }
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

        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
            if(setRgbVariables(x, y))  {
                setOtherButtonsUnpressed(EmptyObject.emptyObject);
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
        ivPickedColorMarker.setX(x -mPickedColorMarkerRadius );
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
        Log.d("k3", saturation + "");
        if (saturation <= 1) {
            mSaturation = saturation;
            mHue = HsvRgbCalculations.getHue(x, y, mHSVCircleRadius);
            int rgbColors[] = HsvRgbCalculations.hsvToRgb(mHue, mSaturation, mValue);
            mRed = rgbColors[0];
            mGreen = rgbColors[1];
            mBlue = rgbColors[2];
            return true;
        }
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
                unpressBtnOnOff();
                changeHsvCircleBlackOverlaysAlpha(progress);
                setValue(progress);
                setRgbVariables();
                changePreviewEllipseColor();
                try {
                    sendCommand();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Nie udało się wysłać wiadomości (seekbar)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void sendCommand() throws IOException {
        if (mIsBtnRainbowPressed){
            mCloudDevice.sendRainbow(HsvRgbCalculations.getBrightness(mValue));
        } else if (mIsBtnAllColorsChangingPressed){
            mCloudDevice.sendAllTheSameChanging(HsvRgbCalculations.getBrightness(mValue));
        } else {
            mCloudDevice.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
        }
    }

    private void setValue(int progress){
        mValue = progress / 100.;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mCloudDevice.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unbindService(mConnection); // może być problem jak sie bedzie przechodziło do innych activity 
    }
}
