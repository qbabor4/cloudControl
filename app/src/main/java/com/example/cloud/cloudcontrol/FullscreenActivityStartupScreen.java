package com.example.cloud.cloudcontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class FullscreenActivityStartupScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_startup_screen);

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(2000);  //Delay of 10 seconds
                } catch (Exception e) {

                } finally {

                    Intent bluetoothConnectionIntent = new Intent(FullscreenActivityStartupScreen.this, BluetoothConnection.class);
                    startActivity( bluetoothConnectionIntent  );

                    finish();
                }
            }
        };
        welcomeThread.start();
    }

}
