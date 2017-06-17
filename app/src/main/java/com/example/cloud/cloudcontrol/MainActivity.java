package com.example.cloud.cloudcontrol;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    /* TODO
        - skalowac zdjecie ( match_parent? ) do ekranu, zeby było jak najwieksze
        - moze sliderlayout  na ekranie ( mniejsze rozdzielczosci ucinaja
        - zamiana hsv na rgb
        - zmiana x i y na hsv
        - zobaczyc czy mozna wywalic casy up i down ze swicha
        = zobaczyc czy da sie wywalic globalne hue i saturation
        - liczenie w oddzielnej klasie a tu tylko widgety
     */

    public int hue = 0; // 0-360
    public double saturation = 0; // 0-1
    public double value = 1; // 0-1

    public int red = 0;
    public int green = 0;
    public int blue = 0;

    private double hsvCircleRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hsvCircleImageOnClick();
        onSeekBarChange();
        setHsvCircleRadius();
    }

    private void setHsvCircleRadius(){
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

    private double getDistanceFromCenter(int x, int y){
        double middleXY = hsvCircleRadius;
        double triangleBase = Math.abs(middleXY - x);
        double triangleHeight = Math.abs(middleXY - y);

        return Math.sqrt(triangleBase * triangleBase + triangleHeight * triangleHeight); //triangle diagonal (pitagoras)
    }

    private double getSaturation(double distanceFromCenter){
        return distanceFromCenter / hsvCircleRadius;
    }

    private int getHue(int x, int y){
        double angle = Math.abs (Math.atan2( (y - hsvCircleRadius), ( hsvCircleRadius - x) ) * 180 / 3.14 - 180);
        angle = (angle + 90) % 360;

        return (int)angle;
    }


    /// Convert HSV to RGB
    /// h is from 0-360
    /// s,v values are 0-1
    /// r,g,b values are 0-255
    public int[] hsvToRgb(int H, double S, double V) {
        double red, green, blue;

        if (V == 0) {
            red = green = blue = 0;
        }
        else {
            double hf = H / 60.0;
            int i = (int) Math.floor( hf );
            double f = hf - i;
            double pv = V * (1 - S);
            double qv = V * (1 - S * f);
            double tv = V * (1 - S * (1 - f));

            switch (i) {
                // Red is the dominant color
                case 0:
                    red = V;
                    green = tv;
                    blue = pv;
                    break;
                // Green is the dominant color
                case 1:
                    red = qv;
                    green = V;
                    blue = pv;
                    break;
                case 2:
                    red = pv;
                    green = V;
                    blue = tv;
                    break;
                // Blue is the dominant color
                case 3:
                    red = pv;
                    green = qv;
                    blue = V;
                    break;
                case 4:
                    red = tv;
                    green = pv;
                    blue = V;
                    break;
                // Red is the dominant color
                case 5:
                    red = V;
                    green = pv;
                    blue = qv;
                    break;
                // Just in case we overshoot on our math by a little, we put these here. Since its a switch it won't slow us down at all to put these here.
                case 6:
                    red = V;
                    green = tv;
                    blue = pv;
                    break;
                case -1:
                    red = V;
                    green = pv;
                    blue = qv;
                    break;
                // The color is not defined, we should throw an error.
                default:
                    //LFATAL("i Value error in Pixel conversion, Value is %d", i);
                    red = green = blue = V; // Just pretend its black/white
                    break;
            }
        }
        int rgbArray[] = {(int)(red * 255.0), (int)(green * 255.0), (int)(blue * 255.0)};
        return rgbArray;
    }

    private void hsvCircleImageOnClick(){
        final ImageView hsvCircleImg =  (ImageView) findViewById(R.id.hsvCircleImage);
        hsvCircleImg.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int hsvCircleHeight = (int)hsvCircleRadius * 2;
                int hsvCircleWidth = hsvCircleHeight;
                TextView tv6 = (TextView)findViewById(R.id.textView6);
                TextView tv7 = (TextView)findViewById(R.id.textView7);
                TextView tv13 = (TextView)findViewById(R.id.textView13);
                TextView tv14 = (TextView)findViewById(R.id.textView14);
                TextView tv15 = (TextView)findViewById(R.id.textView15);
                if ( x > 0 && y > 0 && x < hsvCircleWidth && y < hsvCircleHeight) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {

                            setRgbVariables(x, y);
                            tv6.setText(String.valueOf(hue));
                            tv7.setText(String.valueOf(saturation));
                            tv13.setText(String.valueOf(red));
                            tv14.setText(String.valueOf(green));
                            tv15.setText(String.valueOf(blue));
                            Log.d("rgb", "r: " + red + " g: " + green + " b: " + blue);
                            break;
                        }
                    }
                }

                return true;
            }
        });
    }

    private void setRgbVariables(int x, int y){
        double distanceFromCenter = getDistanceFromCenter(x, y);
        double saturation = getSaturation( distanceFromCenter );
        if (saturation <= 1) {
            this.saturation = saturation;
            this.hue = getHue(x, y);
            int rgbColors[] = hsvToRgb( this.hue, this.saturation, this.value);
            this.red = rgbColors[0];
            this.green = rgbColors[1];
            this.blue = rgbColors[2];
        }
    }

    private void onSeekBarChange(){
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
        final ImageView hsvCircleBlackOverlay = (ImageView) findViewById(R.id.hsvCircleBlackOverlay);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final TextView valueTextView = (TextView) findViewById(R.id.textView8);
                valueTextView.setText(String.valueOf(progress));
                float imageAlpha = 1 - (float)progress / 100;
                hsvCircleBlackOverlay.setAlpha(imageAlpha);
                value = progress /100.;
                Log.d("value", String.valueOf(imageAlpha));

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
