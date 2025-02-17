package com.order.food.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.order.food.R;
import com.order.food.entity.CarInfo;


public class CarListAdapter extends BaseQuickAdapter<CarInfo, BaseViewHolder> {
    public CarListAdapter() {
        super(R.layout.car_list_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, CarInfo carInfo) {
        baseViewHolder.setImageResource(R.id.image,carInfo.getImage());
        baseViewHolder.setText(R.id.title,carInfo.getTitle());
        baseViewHolder.setText(R.id.price,carInfo.getPrice()+"");
        baseViewHolder.setText(R.id.food_num,carInfo.getFood_num()+"");
        baseViewHolder.setText(R.id.detail,carInfo.getDetail());


        baseViewHolder.getView(R.id.del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mDeleteListener) {
                    mDeleteListener.delOnClick(carInfo, getItemPosition(carInfo));
                }
            }
        });

        //减
        baseViewHolder.getView(R.id.subtract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null!=mDeleteListener){
                    mDeleteListener.subtract(getData().get(getItemPosition(carInfo)),getItemPosition(carInfo));
                }

            }
        });
        //加
        baseViewHolder.getView(R.id.plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null!=mDeleteListener){
                    mDeleteListener.plus(getData().get(getItemPosition(carInfo)),getItemPosition(carInfo));
                }
            }
        });

    }

    public DeleteListener mDeleteListener;

    public void setDeleteListener(DeleteListener deleteListener) {
        mDeleteListener = deleteListener;
    }

    public interface DeleteListener {
        void delOnClick(CarInfo carInfo, int position);
        void subtract(CarInfo carInfo,int position);
        void plus(CarInfo carInfo,int position);
    }
}
