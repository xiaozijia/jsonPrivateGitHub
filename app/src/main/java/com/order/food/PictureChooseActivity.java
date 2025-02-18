package com.order.food;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.order.food.adapter.PictureListAdapter;
import com.order.food.base.BaseActivity;
import com.order.food.dao.PictureDao;
import com.order.food.databinding.ActivityPictureChooseBinding;
import com.order.food.entity.FoodsInfo;
import com.order.food.entity.PictureInfo;
import com.order.food.presenter.IPictureChooseView;
import com.order.food.presenter.PictureChoosePresenter;

import java.util.List;

public class PictureChooseActivity extends BaseActivity<ActivityPictureChooseBinding> implements IPictureChooseView ,reBackToActivity {
    private PictureChoosePresenter mPictureChoosePresenter;
    private PictureListAdapter mProductListAdapter;

    @Override
    protected ActivityPictureChooseBinding getViewBinding() {
        return ActivityPictureChooseBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setListener() {
        mPictureChoosePresenter = new PictureChoosePresenter(this);
        mProductListAdapter = new PictureListAdapter(this);
        mProductListAdapter.setReBackToActivityListener(this);
        mProductListAdapter.setList(null);
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        // 获取边距值，这里假设在 dimens.xml 中定义了 item_spacing
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.size_6dp);
        mBinding.recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                // 为每个子项的四个方向都设置相同的边距
                outRect.left = spacingInPixels;
                outRect.right = spacingInPixels;
                outRect.bottom = spacingInPixels;
                // 如果是第一个子项，设置顶部边距
                outRect.top = spacingInPixels;
            }
        });
        mBinding.recyclerView.setAdapter(mProductListAdapter);

    }

    @Override
    protected void initData() {
        mPictureChoosePresenter.loadPictureData();
    }

    @Override
    public void showLoading() {
        showProgressDialog();
    }

    @Override
    public void hideLoading() {
        dismissProgressDialog();
    }

    @Override
    public void updateData(List<PictureInfo> pictureList) {
        mProductListAdapter.setList(pictureList);
    }


    @Override
    public void showError(String error) {
        showToast(error);
    }

    @Override
    public void reBack() {
        finish();
    }
}