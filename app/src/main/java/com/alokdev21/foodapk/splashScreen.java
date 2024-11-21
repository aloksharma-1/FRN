package com.alokdev21.foodapk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.window.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

public class splashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent to move from SplashScreen to MainActivity
                Intent intent = new Intent(splashScreen.this, SignIn.class);
                startActivity(intent);
                finish();  // Finish SplashScreen so it's removed from the back stack
            }
        }, 2000);
    }
}