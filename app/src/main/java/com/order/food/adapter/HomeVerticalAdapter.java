package com.order.food.adapter;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.order.food.R;
import com.order.food.entity.FoodsInfo;
import com.order.food.utils.Utils;


public class HomeVerticalAdapter extends BaseQuickAdapter<FoodsInfo, BaseViewHolder> {
    public HomeVerticalAdapter() {
        super(R.layout.home_list_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, FoodsInfo foodsInfo) {
        baseViewHolder.setText(R.id.title, foodsInfo.getTitle());
        baseViewHolder.setText(R.id.introduce, foodsInfo.getDetail());
        baseViewHolder.setText(R.id.price, foodsInfo.getPrice() + "");
        Utils.loadImage(foodsInfo.getImageUrl(), baseViewHolder.getView(R.id.image));
    }

}
