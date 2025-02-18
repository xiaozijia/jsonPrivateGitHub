package com.order.food.presenter;

import android.util.Log;

import com.order.food.api.FoodService;
import com.order.food.api.RetrofitClient;
import com.order.food.entity.FoodsInfo;
import com.order.food.entity.PictureInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PictureChoosePresenter {
    private IPictureChooseView mView;
    private FoodService mFoodService;

    public PictureChoosePresenter(IPictureChooseView view) {
        this.mView = view;
        this.mFoodService = RetrofitClient.getFoodService();
    }

    public void loadPictureData() {
        mView.showLoading();
        mFoodService.getPicturesInfo().enqueue(new Callback<List<PictureInfo>>() {
            @Override
            public void onResponse(Call<List<PictureInfo>> call, Response<List<PictureInfo>> response) {
                mView.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    mView.updateData(response.body());
                } else {
                    mView.showError("加载数据失败，请检查网络和服务器");
                }
            }

            @Override
            public void onFailure(Call<List<PictureInfo>> call, Throwable t) {
                mView.hideLoading();
                mView.showError(t.getMessage());
            }
        });
    }
}
