package com.order.food;

import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.order.food.base.BaseActivity;
import com.order.food.dao.CarDao;
import com.order.food.databinding.ActivityFoodsDetailsBinding;
import com.order.food.entity.FoodsInfo;
import com.order.food.entity.UserInfo;
import com.order.food.utils.Utils;

import okhttp3.internal.Util;


public class FoodsDetailsActivity extends BaseActivity<ActivityFoodsDetailsBinding> {
    private FoodsInfo foodsInfo;
    private int order_number = 1;
    private CarDao mCarDao;


    @Override
    protected ActivityFoodsDetailsBinding getViewBinding() {
        return ActivityFoodsDetailsBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setListener() {

        mBinding.addCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = mBinding.carOrderNum.getText().toString().trim();
                order_number = Integer.parseInt(number);
                if (order_number <= 0) {
                    showToast("购买数量错误，购买梳理必须大于0");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("操作");
                    builder.setMessage("确定要加入到购物车吗？");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (mCarDao == null) {
                                mCarDao = new CarDao(FoodsDetailsActivity.this);
                            }
                            int row = mCarDao.insert(UserInfo.getUserInfo().getMobile(), foodsInfo.getTitle(), foodsInfo.getPrice(), foodsInfo.getImageUrl(), order_number, foodsInfo.getDetail());
                            if (row > 0) {
                                showToast("添加成功");
                            } else {
                                showToast("添加失败");
                            }
                            finish();
                        }
                    });
                    builder.show();
                }
            }
        });

    }

    @Override
    protected void initData() {
        foodsInfo = (FoodsInfo) getIntent().getSerializableExtra("foodsInfo");
        if (null != foodsInfo) {
            mBinding.goodName.setText(foodsInfo.getTitle());
            mBinding.goodPrice.setText("商品单价￥：  " + foodsInfo.getPrice());
            mBinding.detail.setText(foodsInfo.getDetail());
            Utils.loadImage(foodsInfo.getImageUrl(),mBinding.image);
        }
    }
}