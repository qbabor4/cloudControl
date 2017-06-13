package com.example.cloud.cloudcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonClick();
    }

    private void changeTextViewText(){ // jaki id i na jaki text
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
}
