package com.order.food;

import android.util.Log;

import com.order.food.api.FoodService;
import com.order.food.api.RetrofitClient;
import com.order.food.entity.FoodsInfo;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter {
    private IHomeView mView;
    private FoodService mFoodService;

    public HomePresenter(IHomeView view) {
        this.mView = view;
        this.mFoodService = RetrofitClient.getFoodService();
    }

    public void loadFoodsData() {
        mView.showLoading();
        mFoodService.getFoodsInfo().enqueue(new Callback<List<FoodsInfo>>() {
            @Override
            public void onResponse(Call<List<FoodsInfo>> call, Response<List<FoodsInfo>> response) {
                mView.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    mView.updateData(response.body());
                } else {
                    mView.showError("Data loading failed");
                }
            }

            @Override
            public void onFailure(Call<List<FoodsInfo>> call, Throwable t) {
                mView.hideLoading();
                mView.showError(t.getMessage());
            }
        });
    }
}