package com.example.cloud.cloudcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    /* TODO
        - latanie palcem po zdjeciu
        - skalowac zdjecie ( match_parent? ) do ekranu, zeby było jak najwieksze
        - moze slider w dół na ekranie ( mniejsze rozdzielczosci ucinaja
        - zamiana hsv na rgb
        - zmiana x i y na hsv
        - jak x albo y jest na minusie ( albo jest wieksze od szerokosci  albo wysokosci ( raczej to samo )), to wyjezdza sie palecem za hsvCircle
        - nie ustawia dobrze imagewidth i height
     */
    private int num = 0;
    public int hsv = 0;
    public int saturation = 0;
    public int value = 0;

    public byte red = 0;
    public byte green = 0;
    public byte blue = 0;

    public int hsvCircleWidth;
    public int hsvCircleHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hsvCircleImageOnClick();
        onSeekBarChange();
        setHsvCircleWidth();
        setHsvCircleHeight();
    }

    private void setHsvCircleWidth(){
        ImageView hsvCircleImg =  (ImageView) findViewById(R.id.hsvCircleImage);
        this.hsvCircleWidth = hsvCircleImg.getWidth();
    }

    private void setHsvCircleHeight(){
        ImageView hsvCircleImg =  (ImageView) findViewById(R.id.hsvCircleImage);
        hsvCircleHeight = hsvCircleImg.getHeight();
    }

    private void hsvCircleImageOnClick(){
        final ImageView hsvCircleImg =  (ImageView) findViewById(R.id.hsvCircleImage);
        this.hsvCircleHeight = hsvCircleImg.getHeight();
        TextView tv13 = (TextView)findViewById(R.id.textView13);
        TextView tv14 = (TextView)findViewById(R.id.textView14);
        tv13.setText(String.valueOf(hsvCircleHeight));
        tv14.setText(String.valueOf(hsvCircleWidth));
        hsvCircleImg.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                TextView tv = (TextView)findViewById(R.id.textView6);
                TextView tv2 = (TextView)findViewById(R.id.textView7);
                //  && x < hsvCircleWidth && y < hsvCircleHeight
                if ( x > 0 && y > 0 ) {
                    Log.d("hsvCirclWidth", String.valueOf(hsvCircleWidth));
                    Log.d("hsvCircleHeight", String.valueOf(hsvCircleHeight));
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
                            break;
                        }
                    }
                }

                return true;
            }
        });
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
