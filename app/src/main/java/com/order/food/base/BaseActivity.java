package com.order.food.base;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import com.order.food.R;

import java.util.ArrayList;


public abstract class BaseActivity <T extends ViewBinding> extends AppCompatActivity {
    protected Context mContext;
    protected Toolbar toolbar;
    protected T mBinding;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mBinding = getViewBinding();
        setContentView(mBinding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        if (null != toolbar) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        setListener();

        initData();

//        requestPermission();

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
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("正在识别中......");
        mProgressDialog.show();

    }
    protected void dismissProgressDialog(){
        if (mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    /**
     * Android6.0之后需要动态申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> permissionsList = new ArrayList<>();
            String[] permissions = {
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.WRITE_SETTINGS,
            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (!permissionsList.isEmpty()) {
                String[] strings = new String[permissionsList.size()];
                requestPermissions(permissionsList.toArray(strings), 0);
            }
        }
    }
}