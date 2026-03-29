package com.zcshou.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * 开机自启广播：收到 BOOT_COMPLETED 后不启动服务（服务只在用户主动使用 app 时启动）。
 * 仅将 app 进程唤醒，以便系统重新加载 app 状态。
 * 若需要开机自动启动模拟定位服务，可在此启动 ServiceGo。
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
            case "android.intent.action.LOCKED_BOOT_COMPLETED":
            case "android.intent.action.MY_PACKAGE_REPLACED":
            case "android.intent.action.QUICKBOOT_POWERON":
                Intent serviceIntent = new Intent(context, ServiceGo.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
                break;
        }
    }
}
