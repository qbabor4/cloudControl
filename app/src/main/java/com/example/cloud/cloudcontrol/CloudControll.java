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

    ImageButton btnOnOff;
    ImageView ivHSVCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_controll);

        initCloudDeviceService();
        initComponents();


        setFinalHsvCircleRadius();
        onSeekBarChange(); // to do initComponent

    }

    private void initComponents(){
        setBtnOnOff();
        setIvHSVCircle();
    }

    private void initCloudDeviceService(){
        Intent intent = new Intent(this, ConnectionService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }



//    @Override
//    public void onBackPressed() {
//        // don't let user use back button because when he does and brings app back it goes to bluetooth connecting ( cloud is already paired and can't par again ) // TODO zmienic zeby mozna było
//        // zrobić tak, żeby wywalało bluetooth activity i przechodziło do tego
//    }

    private void setBtnOnOff(){
        btnOnOff = (ImageButton) findViewById(R.id.btnOnOff);
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOnOffClick(v);
            }
        });
    }

    public void onOnOffClick(View view){
        try {
            if (mIsBtnOnOffTurnedOn) {
                mCloudDevice.sendMessage(Colors.BLACK.getColor());
                mIsBtnOnOffTurnedOn = false;
            } else {
                mCloudDevice.sendMessage(changeRGBColorTOHex(mRed, mGreen, mBlue));
                mIsBtnOnOffTurnedOn = true;
            }
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Nie udało się wysłać danych", Toast.LENGTH_SHORT).show();
        }
    }

    private void setIvHSVCircle() {
        ivHSVCircle = (ImageView) findViewById(R.id.hsvCircleImage);
        ivHSVCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return ivHSVCircleOnTouchListener(v, event);
            }
        });
    }

    private boolean ivHSVCircleOnTouchListener(View v, MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            setRgbVariables(x, y);
            changePreviewEllipseColor();
            try {
                mCloudDevice.sendMessage(changeRGBColorTOHex(mRed, mGreen, mBlue)); // send color
            } catch (IOException e){
                Toast.makeText(getApplicationContext(), "Not Sent", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

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


    /**
     * Sets HSVCircle radius. Creates ViewTreeObserver to get width od image with HSV Circle.
     */
    private void setFinalHsvCircleRadius(){
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
     * @param x x value of touch on HSV Circle Image
     * @param y y value of touch on HSV Circle Image
     * @return true if variables were set - if x and y are contained in HSV Circle (without corner spaces) false if not
     */
    private boolean setRgbVariables(int x, int y){
        double saturation = HsvRgbCalculations.getSaturation( HsvRgbCalculations.getDistanceFromCenter(x, y, mHSVCircleRadius), mHSVCircleRadius);
        if (saturation <= 1) {
            mSaturation = saturation;
            mHue = HsvRgbCalculations.getHue(x, y, mHSVCircleRadius);
            int rgbColors[] = HsvRgbCalculations.hsvToRgb( mHue, mSaturation, mValue);
            mRed = rgbColors[0];
            mGreen = rgbColors[1];
            mBlue = rgbColors[2];
            return true;
        }
        return false;
    }

    private void setRgbVariables(){
        if (mSaturation <= 1) {
            int rgbColors[] = HsvRgbCalculations.hsvToRgb(mHue, mSaturation, mValue);
            this.mRed = rgbColors[0];
            this.mGreen = rgbColors[1];
            this.mBlue = rgbColors[2];
        }
    }

    private void changePreviewEllipseColor(){
        ImageView previewEllipse = (ImageView)findViewById(R.id.picked_color_prewiew_ellipse); // to może globalnie ?? TODO
        previewEllipse.setColorFilter(Color.rgb(mRed, mGreen, mBlue));
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
                mValue = progress /100.; // tu dać seter TODO
                setRgbVariables();
                changePreviewEllipseColor();
                try {
                    mCloudDevice.sendMessage(changeRGBColorTOHex(mRed, mGreen, mBlue));

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
