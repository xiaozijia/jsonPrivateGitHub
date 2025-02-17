package com.order.food;

import android.text.TextUtils;
import android.view.View;

import com.order.food.base.BaseActivity;
import com.order.food.dao.UserDao;
import com.order.food.databinding.ActivityUpdatePwdBinding;
import com.order.food.entity.UserInfo;


public class UpdatePwdActivity extends BaseActivity<ActivityUpdatePwdBinding> {
    private UserDao mUserDao;

    @Override
    protected ActivityUpdatePwdBinding getViewBinding() {
        return ActivityUpdatePwdBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setListener() {
        mBinding.pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = mBinding.password.getText().toString();
                String new_password = mBinding.newPassword.getText().toString();

                if (TextUtils.isEmpty(password)) {
                    showToast("请输入密码");
                } else if (TextUtils.isEmpty(new_password)) {
                    showToast("请输入确认密码");
                } else if (!password.equals(new_password)) {
                    showToast("密码和确认密码不一致");
                } else {
                    edit(password);
                }

            }
        });
    }

    private void edit(String password) {
        if (mUserDao == null) {
            mUserDao = new UserDao(this);
        }

        if (mUserDao.updatePassword(UserInfo.getUserInfo().getMobile() + "", password)) {
            showToast("修改成功,请重新登录");
            setResult(2000);
            finish();
        } else {
            showToast("修改失败");
        }
    }

    @Override
    protected void initData() {

    }
}