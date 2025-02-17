package com.order.food;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.order.food.base.BaseActivity;
import com.order.food.dao.UserDao;
import com.order.food.databinding.ActivityRegisterBinding;


public class RegisterActivity extends BaseActivity<ActivityRegisterBinding> {
    private UserDao mUserDao;

    @Override
    protected ActivityRegisterBinding getViewBinding() {
        return ActivityRegisterBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setListener() {

        //注册
        mBinding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = mBinding.mobile.getText().toString().trim();
                String password = mBinding.password.getText().toString().trim();
                String cim_password = mBinding.newPassword.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    showToast("请输入手机号");
                } else if (TextUtils.isEmpty(password)) {
                    showToast("请输入密码");
                } else if (TextUtils.isEmpty(cim_password)) {
                    showToast("请输确认密码");
                } else if (!password.equals(cim_password)) {
                    showToast("密码不一致");
                } else {
                    if (mUserDao == null) {
                        mUserDao = new UserDao(mContext);
                    }
                    int row = mUserDao.insert(mobile, password);
                    if (row > 0) {
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            }
        });
    }

    @Override
    protected void initData() {

    }
}