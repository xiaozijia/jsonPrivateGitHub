package com.order.food.base;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

public abstract class BaseFragment <T extends ViewBinding> extends Fragment {
    protected T mBinding;
    protected Context mContext;
    private ProgressDialog mProgressDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = getViewBinding();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListener();

        initData();
    }

    protected abstract T getViewBinding();

    protected abstract void setListener();

    protected abstract void initData();


    protected void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }


    protected void  showProgressDialog(){
        if (mProgressDialog==null){
            mProgressDialog =new ProgressDialog(mContext);
        }
        mProgressDialog.setMessage("正在加载中......");
        mProgressDialog.show();

    }
    protected void dismissProgressDialog(){
        if (mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }
}