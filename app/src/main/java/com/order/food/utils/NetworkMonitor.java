package com.order.food.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import androidx.annotation.RequiresApi;
import org.greenrobot.eventbus.EventBus;

public class NetworkMonitor {

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NetworkMonitor(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        initNetworkCallback();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initNetworkCallback() {
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                // 网络已连接，发布事件
                EventBus.getDefault().post(new NetworkConnectedEvent());
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                // 网络已断开
            }
        };

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void unregisterCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    public static class NetworkConnectedEvent {
        // 可以在这里添加额外的信息，如果需要的话
    }
}