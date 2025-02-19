package com.order.food.presenter;

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
                Log.d("xiaozijia", "onResponse: "+response);
                if (response.isSuccessful() && response.body() != null) {
                    mView.updateData(response.body());
                } else {
                    mView.showError("国内流量可能不支持浏览");
                }
            }

            @Override
            public void onFailure(Call<List<FoodsInfo>> call, Throwable t) {
                mView.hideLoading();
                mView.showError(t.toString());
            }
        });
    }

    public void loadChaoShangInfo() {
        mView.showLoading();
        mFoodService.getChaoShangInfo().enqueue(new Callback<List<FoodsInfo>>() {
            @Override
            public void onResponse(Call<List<FoodsInfo>> call, Response<List<FoodsInfo>> response) {
                mView.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    mView.updateData(response.body());
                } else {
                    mView.showError("国内流量可能不支持浏览");
                }
            }

            @Override
            public void onFailure(Call<List<FoodsInfo>> call, Throwable t) {
                mView.hideLoading();
                mView.showError("国内流量可能不支持浏览");
            }
        });
    }

    public void loadHuNanInfo() {
        mView.showLoading();
        mFoodService.getHuNanCaiInfo().enqueue(new Callback<List<FoodsInfo>>() {
            @Override
            public void onResponse(Call<List<FoodsInfo>> call, Response<List<FoodsInfo>> response) {
                mView.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    mView.updateData(response.body());
                } else {
                    mView.showError("国内流量可能不支持浏览");
                }
            }

            @Override
            public void onFailure(Call<List<FoodsInfo>> call, Throwable t) {
                mView.hideLoading();
                mView.showError("国内流量可能不支持浏览");
            }
        });
    }

    public void loadGuangDongInfo() {
        mView.showLoading();
        mFoodService.getGuanDongInfo().enqueue(new Callback<List<FoodsInfo>>() {
            @Override
            public void onResponse(Call<List<FoodsInfo>> call, Response<List<FoodsInfo>> response) {
                mView.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    mView.updateData(response.body());
                } else {
                    mView.showError("国内流量可能不支持浏览");
                }
            }

            @Override
            public void onFailure(Call<List<FoodsInfo>> call, Throwable t) {
                mView.hideLoading();
                mView.showError("国内流量可能不支持浏览");
            }
        });
    }

    public void loadNaiChaInfo() {
        mView.showLoading();
        mFoodService.getNaiChaInfo().enqueue(new Callback<List<FoodsInfo>>() {
            @Override
            public void onResponse(Call<List<FoodsInfo>> call, Response<List<FoodsInfo>> response) {
                mView.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    mView.updateData(response.body());
                } else {
                    mView.showError("请检查您的网络设置");
                }
            }

            @Override
            public void onFailure(Call<List<FoodsInfo>> call, Throwable t) {
                mView.hideLoading();
                mView.showError("请检查您的网络设置");
            }
        });
    }

    public void loadTaiGuoInfo() {
        mView.showLoading();
        mFoodService.getTaiGuoCaiInfo().enqueue(new Callback<List<FoodsInfo>>() {
            @Override
            public void onResponse(Call<List<FoodsInfo>> call, Response<List<FoodsInfo>> response) {
                mView.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    mView.updateData(response.body());
                } else {
                    mView.showError("请检查您的网络设置");
                }
            }

            @Override
            public void onFailure(Call<List<FoodsInfo>> call, Throwable t) {
                mView.hideLoading();
                mView.showError("请检查您的网络设置");
            }
        });
    }

    public void loadRiChangInfo() {
        mView.showLoading();
        mFoodService.getRiChangYongPingInfo().enqueue(new Callback<List<FoodsInfo>>() {
            @Override
            public void onResponse(Call<List<FoodsInfo>> call, Response<List<FoodsInfo>> response) {
                mView.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    mView.updateData(response.body());
                } else {
                    mView.showError("请检查您的网络设置");
                }
            }

            @Override
            public void onFailure(Call<List<FoodsInfo>> call, Throwable t) {
                mView.hideLoading();
                mView.showError("请检查您的网络设置");
            }
        });
    }

    public void loadCaiNameInfo() {
        mView.showLoading();
        mFoodService.getCaiNameInfo().enqueue(new Callback<List<FoodsInfo>>() {
            @Override
            public void onResponse(Call<List<FoodsInfo>> call, Response<List<FoodsInfo>> response) {
                mView.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    mView.updateTitleContent(response.body());
                } else {
                    mView.showError("请检查您的网络设置");
                }
            }

            @Override
            public void onFailure(Call<List<FoodsInfo>> call, Throwable t) {
                mView.hideLoading();
                mView.showError("请检查您的网络设置");
            }
        });
    }


}