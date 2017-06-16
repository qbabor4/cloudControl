package com.example.cloud.cloudcontrol;

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
     */

    public int hue = 0;
    public double saturation = 0; // 0-1
    public double value = 0; // 0-1

    public byte red = 0;
    public byte green = 0;
    public byte blue = 0;

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


    private void hsvCircleImageOnClick(){
        final ImageView hsvCircleImg =  (ImageView) findViewById(R.id.hsvCircleImage);
        hsvCircleImg.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int hsvCircleHeight = (int)hsvCircleRadius * 2;
                int hsvCircleWidth = hsvCircleHeight;
                TextView tv = (TextView)findViewById(R.id.textView6);
                TextView tv2 = (TextView)findViewById(R.id.textView7);
                if ( x > 0 && y > 0 && x < hsvCircleWidth && y < hsvCircleHeight) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            tv.setText(String.valueOf(x));
                            tv2.setText(String.valueOf(y));
                            changeHsvVariables(x, y);
                            break;
                        }
                    }
                }

                return true;
            }
        });
    }

    private void changeHsvVariables(int x, int y){
        double distanceFromCenter = getDistanceFromCenter(x, y);
        double saturation = getSaturation( distanceFromCenter );
        if (saturation <= 1) {
            this.saturation = saturation;
            this.hue = getHue(x, y);
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
