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
        - wszystko na srodku, niezaleznie od ekranu
        - latanie palcem po zdjeciu
        - podlaczyc komorke
        - string resource zamias na sztywno 0 w textView
        - skalowac zdjecie ( match_parent? ) do ekranu, zeby było jak najwieksze
        - moze slider w dół na ekranie ( mniejsze rozdzielczosci ucinaja
        - elipsa rysowana, nie z pliku
        - druga elipsa w rogu, do pokazania koloru wybranego
        - black overlay był taki sam rozmiarowo jak hsv image
     */
    private int num = 0;
    public int hsv = 0;
    public int saturation = 0;
    public int value = 0;

    public byte red = 0;
    public byte green = 0;
    public byte blue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hsvCircleImageOnClick();
        onSeekBarChange();
    }

    private void changeTextViewText(){ // jaki id i na jaki text argumenty
        TextView tv = (TextView)findViewById(R.id.textView6);
        num++;
        tv.setText(String.valueOf(num));
    }

    private void hsvCircleImageOnClick(){
        ImageView hsvCircleImg =  (ImageView) findViewById(R.id.hsvCircleImage);
        hsvCircleImg.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                TextView tv = (TextView)findViewById(R.id.textView6);

                tv.setText(String.valueOf(x) + " " + String.valueOf(y));
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // tu zobaczyc kiedy sie wykonują
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                }
                return false;
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
