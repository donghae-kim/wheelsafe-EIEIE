package com.example.front;

import android.graphics.LinearGradient;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface initRegisterApi {
    //@통신 방식("통신 API명")
    @POST("/users/register")
    Call<String> getRegisterResponse(@Body LoginRequest loginRequest );
}