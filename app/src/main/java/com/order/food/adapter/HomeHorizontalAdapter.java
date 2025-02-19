package com.order.food.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.order.food.R;
import com.order.food.entity.FoodsInfo;

public class HomeHorizontalAdapter extends BaseQuickAdapter<FoodsInfo, BaseViewHolder> {
    public HomeHorizontalAdapter() {
        super(R.layout.fragment_hotizental_data_list);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, FoodsInfo foodsInfo) {
        baseViewHolder.setText(R.id.button,foodsInfo.getTitle());
    }

}
