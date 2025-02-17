package com.order.food.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.order.food.R;
import com.order.food.entity.OrderInfo;


public class OrderListAdapter extends BaseQuickAdapter<OrderInfo, BaseViewHolder> {
    public OrderListAdapter() {
        super(R.layout.order_list_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, OrderInfo orderInfo) {
        baseViewHolder.setText(R.id.pay_method, orderInfo.getPay_method() + "支付");
        baseViewHolder.setText(R.id.goodsNum, orderInfo.getFood_num() + "");
        baseViewHolder.setText(R.id.order_num, "订单号：" + orderInfo.getOrder_num() + "");
        baseViewHolder.setText(R.id.mobile, "手机号：" + orderInfo.getMobile() + "");
        baseViewHolder.setText(R.id.address, "配送地址：" + orderInfo.getAddress() + "");
        baseViewHolder.setText(R.id.goodsPrice,   orderInfo.getPrice() + "");
        baseViewHolder.setText(R.id.title, orderInfo.getTitle());
        baseViewHolder.setText(R.id.detail, orderInfo.getDetail());
        baseViewHolder.setImageResource(R.id.image, orderInfo.getImage());

    }
}
