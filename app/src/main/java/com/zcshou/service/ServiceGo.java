package com.zcshou.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.provider.ProviderProperties;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.elvishew.xlog.XLog;
import com.zcshou.gogogo.MainActivity;
import com.zcshou.gogogo.R;
import com.zcshou.joystick.JoyStick;

public class ServiceGo extends Service {

    public static final double DEFAULT_LAT = 36.667662;
    public static final double DEFAULT_LNG = 117.027707;
    public static final double DEFAULT_ALT = 55.0D;
    public static final float DEFAULT_BEA = 0.0F;

    private double mCurLat = DEFAULT_LAT;
    private double mCurLng = DEFAULT_LNG;
    private double mCurAlt = DEFAULT_ALT;
    private float mCurBea = DEFAULT_BEA;
    private double mSpeed = 1.2;

    // 50ms 注入一次，比原来 100ms 快一倍，接近爱思助手的即时感
    private static final int INJECT_INTERVAL_MS = 16;
    private static final int HEALTH_CHECK_INTERVAL = 60;
    private int mInjectCount = 0;
    private static final int HANDLER_MSG_ID = 0;
    private static final String SERVICE_GO_HANDLER_NAME = "ServiceGoLocation";

    private LocationManager mLocManager;
    private HandlerThread mLocHandlerThread;
    private Handler mLocHandler;
    private boolean isStop = false;

    private static final int SERVICE_GO_NOTE_ID = 1;
    private static final String SERVICE_GO_NOTE_ACTION_JOYSTICK_SHOW = "ShowJoyStick";
    private static final String SERVICE_GO_NOTE_ACTION_JOYSTICK_HIDE = "HideJoyStick";
    private static final String SERVICE_GO_NOTE_CHANNEL_ID = "SERVICE_GO_NOTE";
    private static final String SERVICE_GO_NOTE_CHANNEL_NAME = "SERVICE_GO_NOTE";
    private NoteActionReceiver mActReceiver;

    private JoyStick mJoyStick;
    private final ServiceGoBinder mBinder = new ServiceGoBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        removeTestProviderNetwork();
        addTestProviderNetwork();
        removeTestProviderGPS();
        addTestProviderGPS();
        addTestProviderPassive();
        removeTestProviderPassive();
        addTestProviderPassive();

        initGoLocation();
        initNotification();
        initJoyStick();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mCurLng = intent.getDoubleExtra(MainActivity.LNG_MSG_ID, DEFAULT_LNG);
            mCurLat = intent.getDoubleExtra(MainActivity.LAT_MSG_ID, DEFAULT_LAT);
            mCurAlt = intent.getDoubleExtra(MainActivity.ALT_MSG_ID, DEFAULT_ALT);
            mJoyStick.setCurrentPosition(mCurLng, mCurLat, mCurAlt);
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        isStop = true;
        mLocHandler.removeMessages(HANDLER_MSG_ID);
        mLocHandlerThread.quit();
        mJoyStick.destroy();
        removeTestProviderNetwork();
        removeTestProviderGPS();
        removeTestProviderPassive();
        unregisterReceiver(mActReceiver);
        stopForeground(STOP_FOREGROUND_REMOVE);
        super.onDestroy();
    }

    private void initNotification() {
        mActReceiver = new NoteActionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICE_GO_NOTE_ACTION_JOYSTICK_SHOW);
        filter.addAction(SERVICE_GO_NOTE_ACTION_JOYSTICK_HIDE);
        registerReceiver(mActReceiver, filter);

        NotificationChannel mChannel = new NotificationChannel(
                SERVICE_GO_NOTE_CHANNEL_ID,
                SERVICE_GO_NOTE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent clickIntent = new Intent(this, MainActivity.class);
        PendingIntent clickPI = PendingIntent.getActivity(
                this, 1, clickIntent, PendingIntent.FLAG_IMMUTABLE);
        Intent showIntent = new Intent(SERVICE_GO_NOTE_ACTION_JOYSTICK_SHOW);
        PendingIntent showPendingPI = PendingIntent.getBroadcast(
                this, 0, showIntent, PendingIntent.FLAG_IMMUTABLE);
        Intent hideIntent = new Intent(SERVICE_GO_NOTE_ACTION_JOYSTICK_HIDE);
        PendingIntent hidePendingPI = PendingIntent.getBroadcast(
                this, 0, hideIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, SERVICE_GO_NOTE_CHANNEL_ID)
                .setChannelId(SERVICE_GO_NOTE_CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.app_service_tips))
                .setContentIntent(clickPI)
                .addAction(new NotificationCompat.Action(
                        null, getResources().getString(R.string.note_show), showPendingPI))
                .addAction(new NotificationCompat.Action(
                        null, getResources().getString(R.string.note_hide), hidePendingPI))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        startForeground(SERVICE_GO_NOTE_ID, notification);
    }

    private void initJoyStick() {
        mJoyStick = new JoyStick(this);
        mJoyStick.setListener(new JoyStick.JoyStickClickListener() {
            @Override
            public void onPositionInfo(double lng, double lat, double alt) {
                mCurLng = lng;
                mCurLat = lat;
                mCurAlt = alt;
            }
        });
        mJoyStick.show();
    }

    // 强制完全重置所有 Provider，解决长时间使用或频繁切换后失效的问题
    private void forceResetAllProviders() {
        XLog.w("SERVICEGO: force reset all providers");
        // 先全部移除
        try { mLocManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false); } catch (Exception ignored) {}
        try { mLocManager.removeTestProvider(LocationManager.GPS_PROVIDER); } catch (Exception ignored) {}
        try { mLocManager.setTestProviderEnabled(LocationManager.NETWORK_PROVIDER, false); } catch (Exception ignored) {}
        try { mLocManager.removeTestProvider(LocationManager.NETWORK_PROVIDER); } catch (Exception ignored) {}
        try { mLocManager.setTestProviderEnabled(LocationManager.PASSIVE_PROVIDER, false); } catch (Exception ignored) {}
        try { mLocManager.removeTestProvider(LocationManager.PASSIVE_PROVIDER); } catch (Exception ignored) {}
        try { mLocManager.setTestProviderEnabled(LocationManager.PASSIVE_PROVIDER, false); } catch (Exception ignored) {}
        try { mLocManager.removeTestProvider(LocationManager.PASSIVE_PROVIDER); } catch (Exception ignored) {}
        // 等待系统处理完移除操作
        try { Thread.sleep(30); } catch (Exception ignored) {}
        // 重新添加
        addTestProviderNetwork();
        addTestProviderGPS();
        try { Thread.sleep(10); } catch (Exception ignored) {}
    }

    private void checkAndRebuildProviders() {
        boolean needReset = false;
        // 检测 GPS Provider 是否真的存活且可用
        try {
            boolean gpsEnabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gpsEnabled) {
                needReset = true;
                XLog.w("SERVICEGO: GPS provider not enabled");
            } else {
                // 尝试注入一条数据，捕获真实异常
                Location testLoc = new Location(LocationManager.GPS_PROVIDER);
                testLoc.setLatitude(mCurLat);
                testLoc.setLongitude(mCurLng);
                testLoc.setAltitude(mCurAlt);
                testLoc.setAccuracy(1.0f);
                testLoc.setTime(System.currentTimeMillis());
                testLoc.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                mLocManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, testLoc);
            }
        } catch (Exception e) {
            needReset = true;
            XLog.w("SERVICEGO: GPS inject failed: " + e.getMessage());
        }
        // 检测 Network Provider
        if (!needReset) {
            try {
                boolean netEnabled = mLocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!netEnabled) {
                    needReset = true;
                    XLog.w("SERVICEGO: Network provider not enabled");
                } else {
                    Location testLoc = new Location(LocationManager.NETWORK_PROVIDER);
                    testLoc.setLatitude(mCurLat);
                    testLoc.setLongitude(mCurLng);
                    testLoc.setAccuracy(10.0f);
                    testLoc.setTime(System.currentTimeMillis());
                    testLoc.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                    mLocManager.setTestProviderLocation(LocationManager.NETWORK_PROVIDER, testLoc);
                }
            } catch (Exception e) {
                needReset = true;
                XLog.w("SERVICEGO: Network inject failed: " + e.getMessage());
            }
        }
        if (needReset) {
            forceResetAllProviders();
        }
    }

    private void initGoLocation() {
        mLocHandlerThread = new HandlerThread(
                SERVICE_GO_HANDLER_NAME, Process.THREAD_PRIORITY_FOREGROUND);
        mLocHandlerThread.start();
        mLocHandler = new Handler(mLocHandlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (isStop) return;
                try {
                    mInjectCount++;
                    if (mInjectCount % HEALTH_CHECK_INTERVAL == 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() { checkAndRebuildProviders(); }
                        }).start();
                    }
                    setLocationPassive();
                    setLocationNetwork();
                    setLocationGPS();
                } catch (Exception e) {
                    XLog.e("SERVICEGO: inject error, rebuilding");
                    checkAndRebuildProviders();
                } finally {
                    sendEmptyMessageDelayed(HANDLER_MSG_ID, INJECT_INTERVAL_MS);
                }
            }
        };
        mLocHandler.sendEmptyMessage(HANDLER_MSG_ID);
    }

    private void removeTestProviderGPS() {
        try {
            mLocManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
            mLocManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            XLog.e("SERVICEGO: ERROR - removeTestProviderGPS");
        }
    }

    @SuppressLint("WrongConstant")
    private void addTestProviderGPS() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mLocManager.addTestProvider(LocationManager.GPS_PROVIDER,
                        false, true, false, false,
                        true, true, true,
                        ProviderProperties.POWER_USAGE_HIGH,
                        ProviderProperties.ACCURACY_FINE);
            } else {
                mLocManager.addTestProvider(LocationManager.GPS_PROVIDER,
                        false, true, false, false,
                        true, true, true,
                        Criteria.POWER_HIGH,
                        Criteria.ACCURACY_FINE);
            }
            mLocManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        } catch (Exception e) {
            XLog.e("SERVICEGO: ERROR - addTestProviderGPS");
        }
    }

    private void setLocationGPS() {
        long now = System.currentTimeMillis();
        long elapsedNanos = SystemClock.elapsedRealtimeNanos();
        try {
            Location loc = new Location(LocationManager.GPS_PROVIDER);
            loc.setAccuracy(1.0f);
            loc.setAltitude(mCurAlt);
            loc.setBearing(mCurBea);
            loc.setLatitude(mCurLat);
            loc.setLongitude(mCurLng);
            loc.setTime(now);
            loc.setSpeed((float) mSpeed);
            if (android.os.Build.VERSION.SDK_INT >= 31) {
                loc.setMock(true);
            }
            loc.setElapsedRealtimeNanos(elapsedNanos);
            Bundle bundle = new Bundle();
            bundle.putInt("satellites", 10);
            loc.setExtras(bundle);
            mLocManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, loc);
        } catch (Exception e) {
            XLog.e("SERVICEGO: ERROR - setLocationGPS");
        }
    }

    private void removeTestProviderNetwork() {
        try {
            mLocManager.setTestProviderEnabled(LocationManager.NETWORK_PROVIDER, false);
            mLocManager.removeTestProvider(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            XLog.e("SERVICEGO: ERROR - removeTestProviderNetwork");
        }
    }

    @SuppressLint("WrongConstant")
    private void addTestProviderNetwork() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mLocManager.addTestProvider(LocationManager.NETWORK_PROVIDER,
                        true, false, true, true,
                        true, true, true,
                        ProviderProperties.POWER_USAGE_LOW,
                        ProviderProperties.ACCURACY_COARSE);
            } else {
                mLocManager.addTestProvider(LocationManager.NETWORK_PROVIDER,
                        true, false, true, true,
                        true, true, true,
                        Criteria.POWER_LOW,
                        Criteria.ACCURACY_COARSE);
            }
            mLocManager.setTestProviderEnabled(LocationManager.NETWORK_PROVIDER, true);
        } catch (SecurityException e) {
            XLog.e("SERVICEGO: ERROR - addTestProviderNetwork");
        }
    }

    private void setLocationNetwork() {
        long now = System.currentTimeMillis();
        long elapsedNanos = SystemClock.elapsedRealtimeNanos();
        try {
            Location loc = new Location(LocationManager.NETWORK_PROVIDER);
            loc.setAccuracy(10.0f);
            loc.setAltitude(mCurAlt);
            loc.setBearing(mCurBea);
            loc.setLatitude(mCurLat);
            loc.setLongitude(mCurLng);
            loc.setTime(now);
            loc.setSpeed((float) mSpeed);
            if (android.os.Build.VERSION.SDK_INT >= 31) {
                loc.setMock(true);
            }
            loc.setElapsedRealtimeNanos(elapsedNanos);
            mLocManager.setTestProviderLocation(LocationManager.NETWORK_PROVIDER, loc);
        } catch (Exception e) {
            XLog.e("SERVICEGO: ERROR - setLocationNetwork");
        }
    }


    private void removeTestProviderPassive() {
        try {
            mLocManager.setTestProviderEnabled(LocationManager.PASSIVE_PROVIDER, false);
            mLocManager.removeTestProvider(LocationManager.PASSIVE_PROVIDER);
        } catch (Exception e) {
            XLog.e("SERVICEGO: ERROR - removeTestProviderPassive");
        }
    }

    @SuppressLint("WrongConstant")
    private void addTestProviderPassive() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mLocManager.addTestProvider(LocationManager.PASSIVE_PROVIDER,
                        false, false, false, false,
                        true, true, true,
                        ProviderProperties.POWER_USAGE_LOW,
                        ProviderProperties.ACCURACY_FINE);
            } else {
                mLocManager.addTestProvider(LocationManager.PASSIVE_PROVIDER,
                        false, false, false, false,
                        true, true, true,
                        Criteria.POWER_LOW,
                        Criteria.ACCURACY_FINE);
            }
            mLocManager.setTestProviderEnabled(LocationManager.PASSIVE_PROVIDER, true);
        } catch (Exception e) {
                // passive provider cannot be added as test provider
        }
    }

    private void setLocationPassive() {
        long now = System.currentTimeMillis();
        long elapsedNanos = SystemClock.elapsedRealtimeNanos();
        try {
            Location loc = new Location(LocationManager.PASSIVE_PROVIDER);
            loc.setAccuracy(1.0f);
            loc.setAltitude(mCurAlt);
            loc.setBearing(mCurBea);
            loc.setLatitude(mCurLat);
            loc.setLongitude(mCurLng);
            loc.setTime(now);
            loc.setSpeed((float) mSpeed);
            if (android.os.Build.VERSION.SDK_INT >= 31) {
                loc.setMock(true);
            }
            loc.setElapsedRealtimeNanos(elapsedNanos);
            mLocManager.setTestProviderLocation(LocationManager.PASSIVE_PROVIDER, loc);
        } catch (Exception e) {
            // passive provider cannot be overridden
        }
    }


    public class NoteActionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(SERVICE_GO_NOTE_ACTION_JOYSTICK_SHOW)) {
                    mJoyStick.show();
                }
                if (action.equals(SERVICE_GO_NOTE_ACTION_JOYSTICK_HIDE)) {
                    mJoyStick.hide();
                }
            }
        }
    }

    public class ServiceGoBinder extends Binder {
        private long mLastSetPositionTime = 0;
        private int mRapidSwitchCount = 0;

        public void setPosition(double lng, double lat, double alt) {
            // 立即更新坐标
            mCurLng = lng;
            mCurLat = lat;
            mCurAlt = alt;
            // 立即在注入线程执行一次三个 Provider 注入，消除切换延迟
            mLocHandler.removeMessages(HANDLER_MSG_ID);
            mLocHandler.post(new Runnable() {
                @Override
                public void run() {
                    setLocationPassive();
                    setLocationNetwork();
                    setLocationGPS();
                    // 继续周期性注入
                    mLocHandler.sendEmptyMessageDelayed(HANDLER_MSG_ID, INJECT_INTERVAL_MS);
                }
            });
            // 频繁切换保护
            long now = System.currentTimeMillis();
            if (now - mLastSetPositionTime < 500) {
                mRapidSwitchCount++;
                if (mRapidSwitchCount >= 5) {
                    mRapidSwitchCount = 0;
                    mLocHandler.post(new Runnable() {
                        @Override
                        public void run() { forceResetAllProviders(); }
                    });
                }
            } else {
                mRapidSwitchCount = 0;
            }
            mLastSetPositionTime = now;
            mJoyStick.setCurrentPosition(mCurLng, mCurLat, mCurAlt);
        }
    }
}
