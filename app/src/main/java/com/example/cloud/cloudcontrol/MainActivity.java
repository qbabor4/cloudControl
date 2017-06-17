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
        - skalowac zdjecie ( match_parent? ) do ekranu, zeby było jak najwieksze ( kilka pixeli zeby było z po bokach
        - moze sliderlayout  na ekranie ( mniejsze rozdzielczosci ucinaja
        - zobaczyc czy mozna wywalic casy up i down ze swicha
        - zmiana koloru na prewiew ellipse
        - liczenie w oddzielnej klasie a tu tylko zmiana widgetow
     */

    public int hue = 0; // 0-360
    public double saturation = 0; // 0-1
    public double value = 1; // 0-1

    public int red = 0; // 0-255
    public int green = 0; // 0-255
    public int blue = 0; // 0-255

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
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        setRgbVariables(x, y);
                        tv6.setText(String.valueOf(hue));
                        tv7.setText(String.valueOf(saturation));
                        tv13.setText(String.valueOf(red));
                        tv14.setText(String.valueOf(green));
                        tv15.setText(String.valueOf(blue));
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

    private void onSeekBarChange(){
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
        final ImageView hsvCircleBlackOverlay = (ImageView) findViewById(R.id.hsvCircleBlackOverlay);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final TextView valueTextView = (TextView) findViewById(R.id.textView8);
                valueTextView.setText(String.valueOf(progress));
                float imageAlpha = 1 - (float)progress / 100;
                hsvCircleBlackOverlay.setAlpha(imageAlpha); // osobna funkcja
                value = progress /100.;
                setRgbVariables();
                TextView tv13 = (TextView)findViewById(R.id.textView13);
                TextView tv14 = (TextView)findViewById(R.id.textView14);
                TextView tv15 = (TextView)findViewById(R.id.textView15);
                tv13.setText(String.valueOf(red));
                tv14.setText(String.valueOf(green));
                tv15.setText(String.valueOf(blue));

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
