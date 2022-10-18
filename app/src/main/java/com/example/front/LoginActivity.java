package com.example.front;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText emailText, passText;
    Button submit;
    boolean isLogin = true;

    private RetrofitClient retrofitClient;
    private initMyApi initMyApi;
    private  initRegisterApi initRegisterApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.email);
        passText = findViewById(R.id.password);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                String password = passText.getText().toString();
                if(email.equals("") || password.equals("")){
                    Toast.makeText(getApplicationContext(),"사용자 이름과 비밀번호를 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isLogin) { // 로그인
                    LoginResponse();
                }
                else{ // 회원가입
                    RegisterResponse();
                }
            }
        });

        //toggle
        TextView signUp = (TextView) findViewById(R.id.signup);
        TextView logIn = (TextView) findViewById(R.id.login);
        Button submit = (Button) findViewById(R.id.submit);
        View signUpUnderLine = (View) findViewById(R.id.signupunderline);
        View logInUnderLine = (View) findViewById(R.id.loginunderline);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp.setTextColor(getResources().getColorStateList(R.color.black));
                logIn.setTextColor(getResources().getColorStateList(R.color.gray));
                submit.setText("Sign Up");
                signUpUnderLine.setVisibility(View.VISIBLE);
                logInUnderLine.setVisibility(View.GONE);
                isLogin = false;
            }
        });
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp.setTextColor(getResources().getColorStateList(R.color.gray));
                logIn.setTextColor(getResources().getColorStateList(R.color.black));
                submit.setText("Next");
                signUpUnderLine.setVisibility(View.GONE);
                logInUnderLine.setVisibility(View.VISIBLE);
                isLogin = true;
            }
        });
    }


    public void LoginResponse() {
        String email = emailText.getText().toString().trim();
        String password = passText.getText().toString().trim();

        //loginRequest에 사용자가 입력한 email과 pw를 저장
        LoginRequest loginRequest = new LoginRequest(email,password);

        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance();
        initMyApi = RetrofitClient.getRetrofitInterface();
        Call<String> call = initMyApi.getLoginResponse(loginRequest);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("isSuccessful", String.valueOf(response.isSuccessful()));
                Log.d("body", String.valueOf(response.body()));
                Log.d("response", String.valueOf(response.code()));
                //통신 성공
                if (response.isSuccessful() && response.body() != null) {
                    //response.body()를 result에 저장
//                    String result = response.body();
                    isLogin = true;
                    if (isLogin) {
                        String email = emailText.getText().toString();
                        String password = passText.getText().toString();
                        Toast.makeText(LoginActivity.this, email+"님 환영합니다.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        intent.putExtra("isLogin", true);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("알림")
                            .setMessage("로그인 실패.\n")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                t.printStackTrace();
                builder.setTitle("알림")
                        .setMessage("예기치 못한 오류가 발생하였습니다.\n 고객센터에 문의바랍니다.")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            }


        });
    }

    public void RegisterResponse() {
        String email = emailText.getText().toString().trim();
        String password = passText.getText().toString().trim();

        //loginRequest에 사용자가 입력한 email과 pw를 저장
        LoginRequest loginRequest = new LoginRequest(email,password);

        //retrofit 생성
        retrofitClient = RetrofitClient.getInstance();
        initRegisterApi = RetrofitClient.getRegisterInterface();
        Call<String> call = initRegisterApi.getRegisterResponse(loginRequest);
        
        //loginRequest에 저장된 데이터와 함께 init에서 정의한 getLoginResponse 함수를 실행한 후 응답을 받음
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("isSuccessful", String.valueOf(response.isSuccessful()));
                Log.d("body", String.valueOf(response.body()));
                Log.d("response", String.valueOf(response.code()));

                //통신 성공
                    //response.body()를 result에 저장
                if(response.isSuccessful() == true)
                    Toast.makeText(LoginActivity.this, email+"님 환영합니다.", Toast.LENGTH_LONG).show();
                else{

                    Toast.makeText(LoginActivity.this, "회원가입 실패", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                t.printStackTrace();
                builder.setTitle("알림")
                        .setMessage("예기치 못한 오류가 발생하였습니다.\n 고객센터에 문의바랍니다.")
                        .setPositiveButton("확인", null)
                        .create()
                        .show();
            }
        });
    }

//    //데이터를 내부 저장소에 저장하기
//    public void setPreference(String key, String value){
//        SharedPreferences pref = getSharedPreferences(DATA_STORE, MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString(key, value);
//        editor.apply();
//    }
//
//    //내부 저장소에 저장된 데이터 가져오기
//    public String getPreferenceString(String key) {
//        SharedPreferences pref = getSharedPreferences(DATA_STORE, MODE_PRIVATE);
//        return pref.getString(key, "");
//    }

}
