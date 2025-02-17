package com.order.food.fragment;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.order.food.FoodsDetailsActivity;
import com.order.food.adapter.HomeListAdapter;
import com.order.food.base.BaseFragment;
import com.order.food.databinding.FragmentHomeBinding;
import com.order.food.entity.FoodsInfo;
import com.order.food.utils.DataService;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> {
    private HomeListAdapter mHomeListAdapter;


    @Override
    protected FragmentHomeBinding getViewBinding() {
        return FragmentHomeBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setListener() {

        mHomeListAdapter = new HomeListAdapter();
        mBinding.recyclerView.setAdapter(mHomeListAdapter);


        //点击事件
        mHomeListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                FoodsInfo foodsInfo = mHomeListAdapter.getData().get(position);

                Intent intent =new Intent(getActivity(), FoodsDetailsActivity.class);
                intent.putExtra("foodsInfo",foodsInfo);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void initData() {

        //设置数据
        mHomeListAdapter.setList(DataService.getHomeListData());
    }
}