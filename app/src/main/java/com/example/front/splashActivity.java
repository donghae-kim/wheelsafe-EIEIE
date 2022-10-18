package com.example.front;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class splashActivity extends AppCompatActivity {
    Animation anim;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        linearLayout = (LinearLayout) findViewById(R.id.activity_splash);
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(splashActivity.this, onboardingActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        linearLayout.startAnimation(anim);
    }
}