package com.example.front;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static RetrofitClient instance = null;
    private static initMyApi initMyApi;
    private static initRegisterApi initRegisterApi;
    private static initLogoutApi initLogoutApi;
    private static initReportApi initReportApi;
    //사용하고 있는 서버 BASE 주소
    private static String baseUrl = "http://18.207.245.34:3000/";


    private RetrofitClient() {
        //로그를 보기 위한 Interceptor
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Gson gson = new GsonBuilder().setLenient().create();

        //retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client) //로그 기능 추가
                .build();

        initMyApi = retrofit.create(initMyApi.class);
        initRegisterApi = retrofit.create((initRegisterApi.class));
        initLogoutApi = retrofit.create((initLogoutApi.class));
        initReportApi = retrofit.create((initReportApi.class));
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public static initMyApi getRetrofitInterface() {
        return initMyApi;
    }
    public static initRegisterApi getRegisterInterface() {
        return initRegisterApi;
    }
    public static initLogoutApi getLogoutInterface() {
        return initLogoutApi;
    }
    public static initReportApi getReportInterface() {
        return initReportApi;
    }

}