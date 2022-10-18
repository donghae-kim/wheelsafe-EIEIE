package com.example.front;

import android.graphics.LinearGradient;
import android.util.Log;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
// 로그인
public interface initMyApi {
    //@통신 방식("통신 API명")
    @POST("/users/login")
    Call<String> getLoginResponse(@Body LoginRequest loginRequest);
}