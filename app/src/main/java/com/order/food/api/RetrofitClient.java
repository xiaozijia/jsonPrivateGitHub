package com.order.food.api;

import retrofit2.Retrofit;

import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL ="https://gitee.com/";

    public static FoodService getFoodService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(LenientGsonConverterFactory.create())
                .build();

        return retrofit.create(FoodService.class);
    }
}
