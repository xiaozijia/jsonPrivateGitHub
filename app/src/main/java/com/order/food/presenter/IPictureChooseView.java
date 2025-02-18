package com.order.food.presenter;

import com.order.food.entity.FoodsInfo;
import com.order.food.entity.PictureInfo;

import java.util.List;

public interface IPictureChooseView {
    void showLoading();
    void hideLoading();
    void updateData(List<PictureInfo> pictureList);
    void showError(String error);
}
