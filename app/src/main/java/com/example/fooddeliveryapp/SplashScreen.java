package com.example.fooddeliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        TextView loaderText = findViewById(R.id.loaderAppText);
        ImageView loaderImage = findViewById(R.id.loaderImage);

        loaderImage.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent switchToAuth = new Intent(SplashScreen.this, Welcome_Page.class);
                startActivity(switchToAuth);
                finish();
            }
        }, 1500);
    }
}