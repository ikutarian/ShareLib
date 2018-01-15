package com.owen.library.sharelib.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RxRequestHelper {

    private static final long DEFAULT_TIMEOUT = 30;
    private static RxRequestHelper sInstance;
    private static Retrofit sRetrofit;

    public static RxRequestHelper getInstance() {
        if (sInstance == null) {
            synchronized (RxRequestHelper.class) {
                if (sInstance == null) {
                    sInstance = new RxRequestHelper();
                }
            }
        }
        return sInstance;
    }

    private RxRequestHelper() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        httpClientBuilder
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        sRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.weixin.qq.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }
}
