package com.order.food.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.order.food.R;
import com.order.food.entity.FoodsInfo;


public class HomeListAdapter extends BaseQuickAdapter<FoodsInfo, BaseViewHolder> {
    public HomeListAdapter() {
        super(R.layout.home_list_item);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, FoodsInfo foodsInfo) {
        baseViewHolder.setText(R.id.title, foodsInfo.getTitle());
        baseViewHolder.setText(R.id.introduce, foodsInfo.getDetail());
        baseViewHolder.setText(R.id.price, foodsInfo.getPrice() + "");

        baseViewHolder.setImageResource(R.id.image, foodsInfo.getImage());
    }
}
