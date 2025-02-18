package com.order.food.fragment;

import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.order.food.AboutActivity;
import com.order.food.PictureChooseActivity;
import com.order.food.UpdatePwdActivity;
import com.order.food.base.BaseFragment;
import com.order.food.dao.PictureDao;
import com.order.food.dao.UserDao;
import com.order.food.databinding.FragmentMineBinding;
import com.order.food.entity.PictureInfo;
import com.order.food.entity.UserInfo;
import com.order.food.utils.Utils;


public class MineFragment extends BaseFragment<FragmentMineBinding> {
    PictureDao mPictureDao;

    @Override
    protected FragmentMineBinding getViewBinding() {
        mPictureDao=new PictureDao(getContext());
        return FragmentMineBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setListener() {
        mBinding.imageIcon.setOnClickListener(v -> startActivity(new Intent(getActivity(), PictureChooseActivity.class)));
        mBinding.about.setOnClickListener(view -> startActivityForResult(new Intent(getActivity(), AboutActivity.class), 2000));
        mBinding.edit.setOnClickListener(view -> startActivityForResult(new Intent(getActivity(), UpdatePwdActivity.class), 2000));
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            Utils.loadImage(mPictureDao.queryById(PictureInfo.getMobile()),mBinding.imageIcon);
        }
       catch (Exception e){
            e.printStackTrace();
       }
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