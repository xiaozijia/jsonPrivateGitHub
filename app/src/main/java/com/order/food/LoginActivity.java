package com.order.food;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.order.food.base.BaseActivity;
import com.order.food.dao.UserDao;
import com.order.food.databinding.ActivityLoginBinding;
import com.order.food.entity.UserInfo;

import java.util.Random;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {
    private UserDao mUserDao;
    private CountDownTimer countDownTimer;
    private String verificationCode;
    private static final int PERMISSION_REQUEST_CODE = 1;

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
        mBinding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = mBinding.mobile.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    showToast("发送验证码之前您的账号不能为空");
                    return;
                }
                // 启动倒计时
                checkPermissionAndSendNotification();

            }
        });

        mBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = mBinding.mobile.getText().toString().trim();
                String password = mBinding.password.getText().toString().trim();
                String verify = mBinding.verifyEdit.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    showToast("请输入手机号");
                } else if (TextUtils.isEmpty(password)) {
                    showToast("请输入密码");
                } else if (TextUtils.isEmpty(verify)) {
                    showToast("您的验证码没有输入，请输入验证码");
                } else if (verificationCode .isEmpty()|| !verificationCode.equals(verify)) {
                    showToast("验证码输入错误，请重新输入");
                } else {
                    login(mobile, password);
                }
            }
        });

    }

    private void showNotification() {
        startCountDownTimer();
        // 生成验证码
        verificationCode = generateRandomCode();
        // 创建通知管理器
        String CHANNEL_ID = "high_priority_channel";

        // 创建通知渠道（Android O及以上）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "High Priority Channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.tupian)
                .setContentTitle("验证码通知")
                .setContentText("您的验证码是: " + verificationCode)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 再次检查权限状态，而不是直接依赖grantResults
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，显示通知
                showNotification();
            } else {
                // 权限被拒绝，提示用户手动开启权限
                showToast("通知权限被拒绝，无法发送通知。请手动开启权限。");
                openAppSettings();
            }
        }
    }

    private void checkPermissionAndSendNotification() {
        // 检查是否已经拥有权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            // 权限已开启，直接显示通知
            showNotification();
        } else {
            // 权限未开启，请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void openAppSettings() {
        // 引导用户手动开启权限
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private void startCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // 创建倒计时器，倒计时60秒
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 每秒更新按钮文字
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                mBinding.verify.setText(secondsRemaining + "s");
                mBinding.verify.setEnabled(false); // 禁用按钮，防止重复点击
            }

            @Override
            public void onFinish() {
                // 倒计时结束，更新按钮文字
                mBinding.verify.setText("输入验证码");
                mBinding.verify.setEnabled(true); // 恢复按钮可用状态
                verificationCode = "";
            }
        }.start();
    }

    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成100000到999999之间的随机数
        return String.valueOf(code);//生成6位数的验证码
    }


    /**
     * 登录
     */
    private void login(String mobile, String password) {
        if (mUserDao == null) {
            mUserDao = new UserDao(mContext);
        }
        UserInfo login = mUserDao.login(mobile);
        if (null != login) {
            if (login.getPassword().equals(password) && login.getMobile().equals(mobile)) {
                UserInfo.setUserInfo(login);
                verificationCode = "";
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                showToast("登录成功");
            } else {
                showToast("手机或者密码错误");
            }
        } else {
            showToast("该手机号暂未注册");
        }

    }

    @Override
    protected void initData() {

    }
}