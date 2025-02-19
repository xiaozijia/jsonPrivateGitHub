package com.order.food.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;

import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL =" https://raw.githubusercontent.com/";
    public static FoodService getFoodService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(LenientGsonConverterFactory.create())
                .build();

        return retrofit.create(FoodService.class);
    }
}