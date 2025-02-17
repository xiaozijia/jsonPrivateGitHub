package com.order.food;

import com.order.food.entity.FoodsInfo;

import java.util.List;

public interface IHomeView {
    void showLoading();
    void hideLoading();
    void updateData(List<FoodsInfo> foodsInfoList);
    void showError(String error);
}
