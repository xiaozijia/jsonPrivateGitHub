package com.order.food.api;

import com.order.food.entity.FoodsInfo;
import com.order.food.entity.PictureInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FoodService {
    @GET("xiaozijia/xiaozijia/raw/master/zonghe.json")
    Call<List<FoodsInfo>> getFoodsInfo();

    @GET("xiaozijia/xiaozijia/raw/master/tupian.json")
    Call<List<PictureInfo>> getPicturesInfo();
}
