package com.candela.wecan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import java.io.IOException;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        Handler handler = new Handler();
        try {
            Process clear_process = Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(splash_screen.this, StartupActivity.class));
                finish();
            }
        },1000);
    }
}