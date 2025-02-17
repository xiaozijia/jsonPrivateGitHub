package com.order.food.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.order.food.FoodsDetailsActivity;
import com.order.food.HomePresenter;
import com.order.food.IHomeView;
import com.order.food.adapter.HomeListAdapter;
import com.order.food.api.FoodService;
import com.order.food.api.RetrofitClient;
import com.order.food.base.BaseFragment;
import com.order.food.databinding.FragmentHomeBinding;
import com.order.food.entity.FoodsInfo;
import com.order.food.utils.DataService;

import java.util.List;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements IHomeView {


    private HomeListAdapter mHomeListAdapter;
    private HomePresenter mHomePresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomePresenter = new HomePresenter(this);
    }

    @Override
    protected FragmentHomeBinding getViewBinding() {
        return FragmentHomeBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setListener() {
        mHomeListAdapter = new HomeListAdapter();
        mHomeListAdapter.setList(DataService.getHomeListData());
       mBinding.recyclerView.setAdapter(mHomeListAdapter);

        // 点击事件
        mHomeListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                FoodsInfo foodsInfo = mHomeListAdapter.getData().get(position);
                Intent intent = new Intent(getActivity(), FoodsDetailsActivity.class);
                intent.putExtra("foodsInfo", foodsInfo);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        mHomePresenter.loadFoodsData();
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
    public void updateData(List<FoodsInfo> foodsInfoList) {
        mHomeListAdapter.setList(foodsInfoList);
    }


    @Override
    public void showError(String error) {
        showToast(error);
    }

}