package pl.qbabor.cloud.activities.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import pl.qbabor.cloud.activities.connection.BluetoothConnection;
import pl.qbabor.cloud.activities.connection.ConnectionService;

import com.example.cloud.cloudcontrol.R;

import pl.qbabor.cloud.helpers.ERgbColors;
import pl.qbabor.cloud.helpers.EmptyObject;
import pl.qbabor.cloud.helpers.HsvRgbCalculations;
import pl.qbabor.cloud.activities.settings.Settings;
import pl.qbabor.cloud.device.CloudDeviceActionsImpl;
import pl.qbabor.cloud.device.ICloudDeviceActions;

import java.io.IOException;

import static pl.qbabor.cloud.activities.settings.Settings.PREFS_NAME;
import static pl.qbabor.cloud.activities.settings.Settings.PREF_DARK_THEME;

/**
 * TODO
 * okragłe logo (manifest)
 * języki
 * jak sie klika na splashscrren to moze wysyalić null przy sendColor, bo jeszcze nie pobrał obiektu chmury
 */
public class CloudController extends AppCompatActivity {

    private static int TIME_AFTER_CAN_SHOW_SENDING_FAILURE_MESSAGE_IN_MILLIS = 2000;
    private static int mButtonsLayoutHeight;

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

//    private CloudDevice mCloudDevice = null;
    private ICloudDeviceActions mCloudDeviceActions;

    private ImageButton btnOnOff, btnRainbow, btnAllColorsChanging;
    private ImageView ivHSVCircle, ivPickedColorPreviewEllipse, ivHSVCircleBlackOverlay, ivPickedColorMarker;
    private SeekBar sbValueOfHSV;

    private boolean isDarkTheme = false;
    private boolean useDarkTheme = false;

    private long timeOfLastFailureTOSendMessage = 0L;

    private Resources mStringResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        if(useDarkTheme && !isDarkTheme) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
            isDarkTheme = true;
        }
        setContentView(R.layout.activity_cloud_controller);
        mStringResources = getResources();

        mCloudDeviceActions = new CloudDeviceActionsImpl();
        initCloudDeviceService();
        initComponents();
    }

    @Override
    protected void onResume(){
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        if ( (!isDarkTheme && useDarkTheme) || (isDarkTheme && !useDarkTheme) )  {
            recreate();
        }
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cloud_controll_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.toolbar_options_icon){
            goToSettingsActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToSettingsActivity() {
        Intent intent = new Intent(getApplicationContext(), Settings.class);
        startActivity(intent);
    }

    private void closeAndGoToBluetoothConnectionActivity(){
        finish();
        goToBluetoothConnectionActivity();
    }

    private void goToBluetoothConnectionActivity(){
        Intent intent = new Intent(getApplicationContext(), BluetoothConnection.class);
        startActivity(intent);
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /* We've bound to LocalService, cast the IBinder and get LocalService instance */
            mCloudDeviceActions.setDevice(((ConnectionService.LocalBinder) service).getService().getConnectedCloudDevice());
//            mCloudDevice = ((ConnectionService.LocalBinder) service).getService().getConnectedCloudDevice();
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
        setButtonsLayoutHeight();
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
                mCloudDeviceActions.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
                btnOnOff.setImageResource(R.drawable.button_on_off);
            } else {
                mCloudDeviceActions.sendColor(ERgbColors.BLACK.getColor());
                btnOnOff.setImageResource(R.drawable.button_on_off_pressed);
                setOtherButtonsUnpressed(btnOnOff);
            }
            mIsBtnOnOffPressed = !mIsBtnOnOffPressed;
        } catch (IOException e) {
            e.printStackTrace();
            showFailureWhileSendingMessage();

        }
    }

    private void setOtherButtonsUnpressed(Object pressedButton){
        if (!(pressedButton).equals(btnOnOff)){
            releaseBtnOnOff();
        }
        if (!pressedButton.equals(btnRainbow)){
            releaseBtnRainbow();
        }
        if (!pressedButton.equals(btnAllColorsChanging)){
            releaseBtnAllColorsChanging();
        }
    }

    private void releaseBtnOnOff(){
        mIsBtnOnOffPressed = false;
        btnOnOff.setImageResource(R.drawable.button_on_off);
    }

    private void releaseBtnRainbow(){
        mIsBtnRainbowPressed = false;
        btnRainbow.setImageResource(R.drawable.button_rainbow);
    }

    private void releaseBtnAllColorsChanging(){
        mIsBtnAllColorsChangingPressed = false;
        btnAllColorsChanging.setImageResource(R.drawable.button_all_colors_changing);
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
                mCloudDeviceActions.sendRainbow(HsvRgbCalculations.getBrightness(mValue));
                btnRainbow.setImageResource(R.drawable.button_rainbow_pressed);
                setOtherButtonsUnpressed(btnRainbow);
            } else {
                /* Show previous color */
                mCloudDeviceActions.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
                btnRainbow.setImageResource(R.drawable.button_rainbow);
            }
            mIsBtnRainbowPressed = !mIsBtnRainbowPressed;
        }catch (IOException ex){
            ex.printStackTrace();
            showFailureWhileSendingMessage();
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
                mCloudDeviceActions.sendAllTheSameChanging(HsvRgbCalculations.getBrightness(mValue));
                btnAllColorsChanging.setImageResource(R.drawable.button_all_colors_changing_pressed);
                setOtherButtonsUnpressed(btnAllColorsChanging);
            } else {
                mCloudDeviceActions.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
                btnAllColorsChanging.setImageResource(R.drawable.button_all_colors_changing);
            }
            mIsBtnAllColorsChangingPressed = !mIsBtnAllColorsChangingPressed;
        } catch (IOException ex){
            ex.printStackTrace();
            showFailureWhileSendingMessage();
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
                changePickedColorMarkerPosition((int) event.getRawX(), (int) event.getRawY() );
                changePreviewEllipseColor();
                try {
                    mCloudDeviceActions.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
                } catch (IOException e) {
                    showFailureWhileSendingMessage();
                    e.printStackTrace();
                    closeAndGoToBluetoothConnectionActivity();
                }
            }
        }
        return true;
    }

    private void showFailureWhileSendingMessage(){
        if ( (System.currentTimeMillis() - timeOfLastFailureTOSendMessage) > TIME_AFTER_CAN_SHOW_SENDING_FAILURE_MESSAGE_IN_MILLIS) {
            Toast.makeText(getApplicationContext(), mStringResources.getString(R.string.sending_data_error), Toast.LENGTH_SHORT).show();
            timeOfLastFailureTOSendMessage = System.currentTimeMillis();
        }
    }

    private void setPickedColorPreviewEllipse(){
        ivPickedColorPreviewEllipse = (ImageView) findViewById(R.id.picked_color_preview_ellipse);
    }

    private void changePickedColorMarkerPosition(int x, int y){
        ivPickedColorMarker.setVisibility(View.VISIBLE); // TODO change this to setting start position at first
        ivPickedColorMarker.setX(x -mPickedColorMarkerRadius );
        ivPickedColorMarker.setY(y -mPickedColorMarkerRadius - mButtonsLayoutHeight);
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

    private void setButtonsLayoutHeight(){
        final View buttonsLayout = findViewById(R.id.buttons_layout);
        final ViewTreeObserver vto = buttonsLayout.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mButtonsLayoutHeight = buttonsLayout.getMeasuredHeight();
                buttonsLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
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
                releaseBtnOnOff();
                changeHsvCircleBlackOverlaysAlpha(progress);
                setValue(progress);
                setRgbVariables();
                changePreviewEllipseColor();
                try {
                    sendCommand();
                } catch (IOException e) {
                    showFailureWhileSendingMessage();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void sendCommand() throws IOException {
        if (mIsBtnRainbowPressed) {
            mCloudDeviceActions.sendRainbow(HsvRgbCalculations.getBrightness(mValue));
        } else if (mIsBtnAllColorsChangingPressed) {
            mCloudDeviceActions.sendAllTheSameChanging(HsvRgbCalculations.getBrightness(mValue));
        } else {
            mCloudDeviceActions.sendColor(HsvRgbCalculations.changeRGBColorTOHex(mRed, mGreen, mBlue));
        }
    }

    private void setValue(int progress){
        mValue = progress / 100.;
    }

}
