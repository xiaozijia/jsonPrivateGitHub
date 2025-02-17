package com.order.food.fragment;

import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.order.food.AboutActivity;
import com.order.food.UpdatePwdActivity;
import com.order.food.base.BaseFragment;
import com.order.food.databinding.FragmentMineBinding;
import com.order.food.entity.UserInfo;


public class MineFragment extends BaseFragment<FragmentMineBinding> {


    @Override
    protected FragmentMineBinding getViewBinding() {
        return FragmentMineBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setListener() {
        mBinding.about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), AboutActivity.class), 2000);
            }
        });

        mBinding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), UpdatePwdActivity.class), 2000);
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        UserInfo userInfo = UserInfo.getUserInfo();
        if (null != userInfo) {
            mBinding.mobile.setText(userInfo.getMobile());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2000 && getActivity() != null) {
            getActivity().finish();
        }
    }
}