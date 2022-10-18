package com.example.front;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

public class onboardingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        Button start_button = (Button) findViewById(R.id.startbutton);


        PaperOnboardingPage scr1 = new PaperOnboardingPage("안전한 길찾기",
                "휠체어가 다니기\n안전한 길을 찾아드려요",
                Color.parseColor("#FFFFFF"), R.drawable.onboarding1, R.drawable.onboarding_bottom1);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("충전소",
                "전동휠체어의\n충전소 위치를 알려드려요",
                Color.parseColor("#FFFFFF"), R.drawable.onboarding2, R.drawable.onboarding_bottom2);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("제보",
                "안전하지 않은 길을 제보해주세요\n 다른 사용자들에게 공유해요",
                Color.parseColor("#FFFFFF"), R.drawable.onboarding3, R.drawable.onboarding_bottom3);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);

        //2. 화면을 온보드 프래그먼트에 넣는다.
        PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(elements);

        //3. 프래그먼트 화면설정 완료
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.containers, onBoardingFragment);
        fragmentTransaction.commit();
        //4. 온보드 완료시에 보여줄 프래그먼트 설정
        onBoardingFragment.setOnChangeListener(new PaperOnboardingOnChangeListener() {
            @Override
            public void onPageChanged(int first, int last) {
                if(first==1 && last==2)
                    start_button.setVisibility(View.VISIBLE);
                else{
                    start_button.setVisibility(View.INVISIBLE);
                }
            }
        });

        start_button.setOnClickListener(new Button.OnClickListener(){
        @Override
            public void onClick(View view) {
                startActivity(new Intent(onboardingActivity.this, setupGPSActivity.class));
            }
        });
    }

}
