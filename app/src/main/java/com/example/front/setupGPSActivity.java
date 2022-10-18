package com.example.front;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;

import java.util.ArrayList;

public class setupGPSActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_gpsactivity);
        Button gps_button = (Button) findViewById(R.id.gpsbutton);
        PaperOnboardingPage scr4 = new PaperOnboardingPage("반가워요!",
                "당신의 위치를 찾기 위해\n GPS를 활성화 시켜주세요",
                Color.parseColor("#FFFFFF"), R.drawable.onboarding4, R.drawable.onboarding_gps);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr4);


        //2. 화면을 온보드 프래그먼트에 넣는다.
        PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(elements);

        //3. 프래그먼트 화면설정 완료
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.containers_setupGPS, onBoardingFragment);
        fragmentTransaction.commit();
        //4. 온보드 완료시에 보여줄 프래그먼트 설정
        onBoardingFragment.setOnChangeListener(new PaperOnboardingOnChangeListener() {
            @Override
            public void onPageChanged(int first, int last) {

            }
        });

        gps_button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startActivity(new Intent(setupGPSActivity.this, MainActivity.class));
                }
            }
        });

    }
}