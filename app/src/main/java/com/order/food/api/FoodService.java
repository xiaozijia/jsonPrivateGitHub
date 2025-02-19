package com.order.food.api;

import com.order.food.entity.FoodsInfo;
import com.order.food.entity.PictureInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FoodService {
    @GET("xiaozijia/jsonPrivateGitHub/refs/heads/main/zonghe.json")
    Call<List<FoodsInfo>> getFoodsInfo();

    @GET("xiaozijia/jsonPrivateGitHub/refs/heads/main/tupian.json")
    Call<List<PictureInfo>> getPicturesInfo();

    @GET("xiaozijia/jsonPrivateGitHub/refs/heads/main/chaoshangmeishi.json")
    Call<List<FoodsInfo>> getChaoShangInfo();

    @GET("xiaozijia/jsonPrivateGitHub/refs/heads/main/guangdongcai.json")
    Call<List<FoodsInfo>> getGuanDongInfo();

    @GET("xiaozijia/jsonPrivateGitHub/refs/heads/main/taiguocai.json")
    Call<List<FoodsInfo>> getTaiGuoCaiInfo();

    @GET("xiaozijia/jsonPrivateGitHub/refs/heads/main/richangyongping.json")
    Call<List<FoodsInfo>> getRiChangYongPingInfo();


    @GET("xiaozijia/jsonPrivateGitHub/refs/heads/main/naicha.json")
    Call<List<FoodsInfo>> getNaiChaInfo();

    @GET("xiaozijia/jsonPrivateGitHub/refs/heads/main/hunancai.json")
    Call<List<FoodsInfo>> getHuNanCaiInfo();

    @GET("xiaozijia/jsonPrivateGitHub/refs/heads/main/local.json")
    Call<List<FoodsInfo>> getCaiNameInfo();
}
