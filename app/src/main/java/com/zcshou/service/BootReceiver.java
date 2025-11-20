package com.zcshou.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 开机自启广播：收到 BOOT_COMPLETED 后不启动服务（服务只在用户主动使用 app 时启动）。
 * 仅将 app 进程唤醒，以便系统重新加载 app 状态。
 * 若需要开机自动启动模拟定位服务，可在此启动 ServiceGo。
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // 目前仅唤醒进程，不自动启动定位服务
            // 如需开机自启定位服务，取消下面注释：
            // Intent serviceIntent = new Intent(context, ServiceGo.class);
            // context.startForegroundService(serviceIntent);
        }
    }
}
