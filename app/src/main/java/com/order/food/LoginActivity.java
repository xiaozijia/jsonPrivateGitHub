package com.order.food;

import android.content.Intent;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.order.food.base.BaseActivity;
import com.order.food.dao.UserDao;
import com.order.food.databinding.ActivityLoginBinding;
import com.order.food.entity.UserInfo;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {
    private UserDao mUserDao;

    @Override
    protected ActivityLoginBinding getViewBinding() {
        return ActivityLoginBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setListener() {

        mBinding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        mBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile =mBinding.mobile.getText().toString().trim();
                String password =mBinding.password.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    showToast("请输入手机号");
                } else if (TextUtils.isEmpty(password)) {
                    showToast("请输入密码");
                } else {
                    login(mobile, password);
                }
            }
        });

    }

    /**
     * 登录
     */
    private void login(String mobile, String password) {
        if (mUserDao==null){
            mUserDao =new UserDao(mContext);
        }
        UserInfo login = mUserDao.login(mobile);
        if (null != login){
            if (login.getPassword().equals(password) && login.getMobile().equals(mobile)) {
                UserInfo.setUserInfo(login);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "手机号或密码错误", Toast.LENGTH_SHORT).show();
            }
        }else {
            showToast("该手机号暂未注册");
        }

    }

    @Override
    protected void initData() {

    }
}