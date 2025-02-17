package com.order.food.api;

import com.order.food.entity.FoodsInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FoodService {
    @GET("xiaozijia/xiaozijia/raw/master/xiaozijia.json")
    Call<List<FoodsInfo>> getFoodsInfo();
}
