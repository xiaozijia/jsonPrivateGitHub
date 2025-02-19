package com.order.food.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.order.food.FoodsDetailsActivity;
import com.order.food.adapter.HomeHorizontalAdapter;
import com.order.food.presenter.HomePresenter;
import com.order.food.presenter.IHomeView;
import com.order.food.adapter.HomeVerticalAdapter;
import com.order.food.base.BaseFragment;
import com.order.food.databinding.FragmentHomeBinding;
import com.order.food.entity.FoodsInfo;
import com.order.food.utils.DataService;
import java.util.List;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements IHomeView {

    private HomeVerticalAdapter mHOmeVerticalAdapter;
    private HomePresenter mHomePresenter;
    private HomeHorizontalAdapter mHomeHorizontalAdapter;
    private int position = 0;

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
        mHOmeVerticalAdapter = new HomeVerticalAdapter();
        mHomeHorizontalAdapter = new HomeHorizontalAdapter();
        mHOmeVerticalAdapter.setList(DataService.getHomeListData(mContext));
        mBinding.recyclerView.setAdapter(mHOmeVerticalAdapter);
        mBinding.swipeRefreshLayout.setOnChildScrollUpCallback((parent, child) -> {
            if (mBinding.recyclerView != null && mBinding.recyclerView.canScrollVertically(-1)) {
                // 如果 RecyclerView 可以上滑，不触发下拉刷新
                return true;
            }
            return false;
        });
        mBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            if (position == 0) {
                mHomePresenter.loadFoodsData();
            }
            if (position == 1) {
                mHomePresenter.loadChaoShangInfo();
            }
            if (position == 2) {
                mHomePresenter.loadHuNanInfo();
            }
            if (position == 3) {
                mHomePresenter.loadTaiGuoInfo();
            }
            if (position == 4) {
                mHomePresenter.loadGuangDongInfo();
            }
            if (position == 5) {
                mHomePresenter.loadNaiChaInfo();
            }
            if (position == 6) {
                mHomePresenter.loadRiChangInfo();
            }
            mBinding.swipeRefreshLayout.setRefreshing(false);
        });
        // 点击事件
        mHOmeVerticalAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                FoodsInfo foodsInfo = mHOmeVerticalAdapter.getData().get(position);
                Intent intent = new Intent(getActivity(), FoodsDetailsActivity.class);
                intent.putExtra("foodsInfo", foodsInfo);
                startActivity(intent);
            }
        });
        mBinding.horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mHomeHorizontalAdapter.setOnItemClickListener((adapter, view, position) -> showUI(position));
        mBinding.horizontalRecyclerView.setAdapter(mHomeHorizontalAdapter);
    }

    private void showUI(int position) {
        if (position == 0) {
            mHomePresenter.loadFoodsData();
            this.position=0;
        }
        if (position == 1) {
            mHomePresenter.loadChaoShangInfo();
            this.position=1;
        }
        if (position == 2) {
            mHomePresenter.loadHuNanInfo();
            this.position=2;
        }
        if (position == 3) {
            mHomePresenter.loadTaiGuoInfo();
            this.position=3;
        }
        if (position == 4) {
            mHomePresenter.loadGuangDongInfo();
            this.position=4;
        }
        if (position == 5) {
            mHomePresenter.loadNaiChaInfo();
            this.position=5;
        }
        if (position == 6) {
            mHomePresenter.loadRiChangInfo();
            this.position=6;
        }
    }

    @Override
    protected void initData() {
        mHomePresenter.loadFoodsData();
        mHomePresenter.loadCaiNameInfo();
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
    public void updateTitleContent(List<FoodsInfo> foodsInfoList) {
        mHomeHorizontalAdapter.setList(foodsInfoList);
    }

    @Override
    public void updateData(List<FoodsInfo> foodsInfoList) {
        mHOmeVerticalAdapter.setList(foodsInfoList);
    }


    @Override
    public void showError(String error) {
        showToast(error);
    }

}