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
     */
    private int num = 0;
    public int hsv = 0;
    public int saturation = 0;
    public int value = 0;

    public byte red = 0;
    public byte green = 0;
    public byte blue = 0;

    public int hsvCircleWidth = 0; // chyba zawsze to samo co height
    public int hsvCircleHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hsvCircleImageOnClick();
        onSeekBarChange();
        setHsvCircleSize();

    }

    private void setHsvCircleSize(){
        final ImageView hsvCircleImgView = (ImageView) findViewById(R.id.hsvCircleImage);
        ViewTreeObserver vto = hsvCircleImgView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                hsvCircleImgView.getViewTreeObserver().removeOnPreDrawListener(this);
                hsvCircleWidth = hsvCircleImgView.getMeasuredWidth();
                hsvCircleHeight = hsvCircleImgView.getMeasuredHeight();

                return true;
            }
        });
    }

    private void hsvCircleImageOnClick(){
        final ImageView hsvCircleImg =  (ImageView) findViewById(R.id.hsvCircleImage);
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
