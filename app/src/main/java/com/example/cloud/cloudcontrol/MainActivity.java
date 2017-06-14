package com.example.cloud.cloudcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    /* TODO
        - wszystko na srodku, niezaleznie od ekranu
        - klikanie na zdjecie
        - string resource zamias na sztywno 0 w textView
        - skalowac zdjecie ( match_parent? ) do ekranu, zeby było jak najwieksze
     */
    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonClick();
        hsvCircleImageOnClick();
    }

    private void changeTextViewText(){ // jaki id i na jaki text argumenty
        TextView tv = (TextView)findViewById(R.id.textView6);
        num++;
        tv.setText(String.valueOf(num));
    }

    private void buttonClick(){
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        changeTextViewText();
                    }
                }
        );
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
}
