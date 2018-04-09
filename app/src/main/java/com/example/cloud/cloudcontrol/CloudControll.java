package com.example.cloud.cloudcontrol;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
 */
public class CloudControll extends AppCompatActivity {


    private int mHue = 0; // 0-360
    private double mSaturation = 0; // 0-1
    private double mValue = 1; // 0-1

    private int mRed = 255; // 0-255
    private int mGreen = 255; // 0-255
    private int mBlue = 255; // 0-255

    private double mHSVCircleRadius;

    private boolean mIsBtnOnOffTurnedOn = true;

    private CloudDevice mCloudDevice = null;

    private ImageButton btnOnOff;
    private ImageView ivHSVCircle, ivPickedColorPreviewEllipse, ivHSVCircleBlackOverlay;
    private SeekBar sbValueOfHSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_controll);

        initCloudDeviceService();
        initComponents();
        setFinalHsvCircleRadius();
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
        setIvHSVCircle();
        setPickedColorPreviewEllipse();
        setIvHSVCircleBlackOverlay();
        setSbValueOfHSV();
    }


    private void setBtnOnOff() {
        btnOnOff = (ImageButton) findViewById(R.id.btn_on_off);
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOnOffClick(v);
            }
        });
    }

    public void onOnOffClick(View view) {
        try {
            if (mIsBtnOnOffTurnedOn) {
                mCloudDevice.sendMessage(Colors.BLACK.getColor());
                mIsBtnOnOffTurnedOn = false;
            } else {
                mCloudDevice.sendMessage(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
                mIsBtnOnOffTurnedOn = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
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
    }

    private boolean ivHSVCircleOnTouchListener(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if(setRgbVariables(x, y));
            {
                changePreviewEllipseColor();
                try {
                    mCloudDevice.sendMessage(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue)); // send color
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Not Sent", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
        return true; // to to zmienia? TODO
    }

    private void setPickedColorPreviewEllipse(){
        ivPickedColorPreviewEllipse = (ImageView) findViewById(R.id.picked_color_preview_ellipse);
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
                    mCloudDevice.sendMessage(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
                } catch (IOException e) {
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

    private void setValue(int progress){
        mValue = progress / 100.;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection); // może być problem jak sie bedzie przechodziło do innych activity 
    }
}
