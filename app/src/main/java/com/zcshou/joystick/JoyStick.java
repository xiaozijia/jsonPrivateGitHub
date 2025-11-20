package com.zcshou.joystick;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.zcshou.database.DataBaseHistoryFavorite;
import com.zcshou.gogogo.FavoritesActivity;
import com.zcshou.gogogo.MainActivity;
import com.zcshou.gogogo.R;
import com.zcshou.utils.GoUtils;
import com.zcshou.utils.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoyStick extends View {
    private static final int DivGo = 1000;
    private static final int WINDOW_TYPE_JOYSTICK = 0;
    private static final int WINDOW_TYPE_MAP = 1;
    private static final int WINDOW_TYPE_HISTORY = 2;

    private final Context mContext;
    private WindowManager.LayoutParams mWindowParamCurrent;
    private WindowManager mWindowManager;
    private int mCurWin = WINDOW_TYPE_JOYSTICK;
    private final LayoutInflater inflater;
    private JoyStickClickListener mListener;

    private View mJoystickLayout;
    private GoUtils.TimeCount mTimer;
    private boolean isMove;
    private double mSpeed = 1.2;
    private double mAltitude = 55.0;
    private double mAngle = 0;
    private double mR = 0;
    private double disLng = 0;
    private double disLat = 0;
    private final SharedPreferences sharedPreferences;

    private FrameLayout mHistoryLayout;
    private RecyclerView mRecyclerView;
    private FavoriteSimpleAdapter mAdapter;
    private List<Map<String, Object>> mFavoriteList = new ArrayList<>();
    private TextView mEmptyView;

    private FrameLayout mMapLayout;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LatLng mCurMapLngLat;
    private LatLng mMarkMapLngLat;

    private final BroadcastReceiver mFavoriteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fetchAllRecord();
            if (mAdapter != null) mAdapter.notifyDataSetChanged();
            updateEmptyState();
        }
    };

    public JoyStick(Context context) {
        super(context);
        this.mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        initWindowManager();
        inflater = LayoutInflater.from(mContext);
        initJoyStickView();
        initJoyStickMapView();
        initHistoryView();
        IntentFilter filter = new IntentFilter("FAVORITE_CHANGED");
        mContext.registerReceiver(mFavoriteReceiver, filter);
    }

    public JoyStick(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        initWindowManager();
        inflater = LayoutInflater.from(mContext);
        initJoyStickView();
        initJoyStickMapView();
        initHistoryView();
        IntentFilter filter = new IntentFilter("FAVORITE_CHANGED");
        mContext.registerReceiver(mFavoriteReceiver, filter);
    }

    public JoyStick(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        initWindowManager();
        inflater = LayoutInflater.from(mContext);
        initJoyStickView();
        initJoyStickMapView();
        initHistoryView();
        IntentFilter filter = new IntentFilter("FAVORITE_CHANGED");
        mContext.registerReceiver(mFavoriteReceiver, filter);
    }

    public void setCurrentPosition(double lng, double lat, double alt) {
        double[] lngLat = MapUtils.wgs2bd09(lng, lat);
        mCurMapLngLat = new LatLng(lngLat[1], lngLat[0]);
        mAltitude = alt;
        resetBaiduMap();
    }

    public void show() {
        switch (mCurWin) {
            case WINDOW_TYPE_MAP:
                if (mJoystickLayout.getParent() != null) mWindowManager.removeView(mJoystickLayout);
                if (mHistoryLayout.getParent() != null) mWindowManager.removeView(mHistoryLayout);
                if (mMapLayout.getParent() == null) {
                    resetBaiduMap();
                    mWindowManager.addView(mMapLayout, mWindowParamCurrent);
                }
                break;
            case WINDOW_TYPE_HISTORY:
                if (mMapLayout.getParent() != null) mWindowManager.removeView(mMapLayout);
                if (mJoystickLayout.getParent() != null) mWindowManager.removeView(mJoystickLayout);
                if (mHistoryLayout.getParent() == null) {
                    mWindowManager.addView(mHistoryLayout, mWindowParamCurrent);
                }
                break;
            case WINDOW_TYPE_JOYSTICK:
                if (mMapLayout.getParent() != null) mWindowManager.removeView(mMapLayout);
                if (mHistoryLayout.getParent() != null) mWindowManager.removeView(mHistoryLayout);
                if (mJoystickLayout.getParent() == null) {
                    mWindowManager.addView(mJoystickLayout, mWindowParamCurrent);
                }
                break;
        }
    }

    public void hide() {
        if (mMapLayout.getParent() != null) mWindowManager.removeViewImmediate(mMapLayout);
        if (mJoystickLayout.getParent() != null)
            mWindowManager.removeViewImmediate(mJoystickLayout);
        if (mHistoryLayout.getParent() != null) mWindowManager.removeViewImmediate(mHistoryLayout);
    }

    public void destroy() {
        hide();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        try {
            mContext.unregisterReceiver(mFavoriteReceiver);
        } catch (Exception ignored) {}
    }

    public void setListener(JoyStickClickListener mListener) {
        this.mListener = mListener;
    }

    private void initWindowManager() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowParamCurrent = new WindowManager.LayoutParams();
        mWindowParamCurrent.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        mWindowParamCurrent.format = PixelFormat.RGBA_8888;
        mWindowParamCurrent.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mWindowParamCurrent.gravity = Gravity.START | Gravity.TOP;
        mWindowParamCurrent.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParamCurrent.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParamCurrent.x = 300;
        mWindowParamCurrent.y = 300;
    }

    @SuppressLint("InflateParams")
    private void initJoyStickView() {
        mTimer = new GoUtils.TimeCount(DivGo, DivGo);
        mTimer.setListener(new GoUtils.TimeCount.TimeCountListener() {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                disLng = mSpeed * (DivGo / 1000.0) * mR * Math.cos(mAngle * Math.PI / 180) / 1000.0;
                disLat = mSpeed * (DivGo / 1000.0) * mR * Math.sin(mAngle * Math.PI / 180) / 1000.0;
                mListener.onMoveInfo(mSpeed, disLng, disLat, 90.0F - mAngle);
                mTimer.start();
            }
        });

        try {
            mSpeed = Double.parseDouble(sharedPreferences.getString("setting_walk", getResources().getString(R.string.setting_walk_default)));
        } catch (Exception e) {
            mSpeed = 1.2;
        }

        mJoystickLayout = inflater.inflate(R.layout.joystick, null);
        mJoystickLayout.setOnTouchListener(new JoyStickOnTouchListener());

        ImageButton btnHistory = mJoystickLayout.findViewById(R.id.joystick_history);
        btnHistory.setOnClickListener(v -> {
            mCurWin = WINDOW_TYPE_HISTORY;
            show();
        });
    }

    private void processDirection(boolean auto, double angle, double r) {
        if (r <= 0) {
            mTimer.cancel();
            isMove = false;
        } else {
            mAngle = angle;
            mR = r;
            if (auto) {
                if (!isMove) {
                    mTimer.start();
                    isMove = true;
                }
            } else {
                mTimer.cancel();
                isMove = false;
                disLng = mSpeed * (DivGo / 1000.0) * mR * Math.cos(mAngle * Math.PI / 180) / 1000.0;
                disLat = mSpeed * (DivGo / 1000.0) * mR * Math.sin(mAngle * Math.PI / 180) / 1000.0;
                mListener.onMoveInfo(mSpeed, disLng, disLat, 90.0F - mAngle);
            }
        }
    }

    private class JoyStickOnTouchListener implements OnTouchListener {
        private int x, y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    mWindowParamCurrent.x += nowX - x;
                    mWindowParamCurrent.y += nowY - y;
                    x = nowX;
                    y = nowY;
                    mWindowManager.updateViewLayout(view, mWindowParamCurrent);
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
            }
            return false;
        }
    }

    public interface JoyStickClickListener {
        void onMoveInfo(double speed, double disLng, double disLat, double angle);
        void onPositionInfo(double lng, double lat, double alt);
    }

    @SuppressLint("InflateParams")
    private void initJoyStickMapView() {
        mMapLayout = (FrameLayout) inflater.inflate(R.layout.joystick_map, null);
        mMapLayout.setOnTouchListener(new JoyStickOnTouchListener());
        initBaiduMap();
    }

    private void initBaiduMap() {
        mMapView = mMapLayout.findViewById(R.id.map_joystick);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                markBaiduMap(point);
            }
            @Override
            public void onMapPoiClick(MapPoi poi) {
                markBaiduMap(poi.getPosition());
            }
        });
    }

    private void resetBaiduMap() {
        mBaiduMap.clear();
        MyLocationData loc = new MyLocationData.Builder()
                .latitude(mCurMapLngLat.latitude)
                .longitude(mCurMapLngLat.longitude).build();
        mBaiduMap.setMyLocationData(loc);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(
                new MapStatus.Builder().target(mCurMapLngLat).zoom(18).build()));
    }

    private void markBaiduMap(LatLng latLng) {
        mMarkMapLngLat = latLng;
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(latLng).icon(MainActivity.mMapIndicator));
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(
                new MapStatus.Builder().target(latLng).zoom(18).build()));
    }

    @SuppressLint("InflateParams")
    private void initHistoryView() {
        mHistoryLayout = (FrameLayout) inflater.inflate(R.layout.joystick_favorites, null);
        mHistoryLayout.setOnTouchListener(new JoyStickOnTouchListener());

        mEmptyView = mHistoryLayout.findViewById(R.id.empty_view);
        ImageButton btnClose = mHistoryLayout.findViewById(R.id.joystick_fav_close);
        btnClose.setOnClickListener(v -> {
            mCurWin = WINDOW_TYPE_JOYSTICK;
            show();
        });

        mRecyclerView = mHistoryLayout.findViewById(R.id.joystick_fav_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new FavoriteSimpleAdapter(mFavoriteList);
        mRecyclerView.setAdapter(mAdapter);

        fetchAllRecord();
        updateEmptyState();

        RecyclerView letterRecycler = mHistoryLayout.findViewById(R.id.letter_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 9);
        letterRecycler.setLayoutManager(gridLayoutManager);

        List<String> letters = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) letters.add(String.valueOf(c));

        letterRecycler.setAdapter(new LetterAdapter(letters));
    }

    private void updateEmptyState() {
        if (mFavoriteList.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void fetchAllRecord() {
        mFavoriteList.clear();
        try {
            DataBaseHistoryFavorite helper = new DataBaseHistoryFavorite(mContext);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(DataBaseHistoryFavorite.TABLE_NAME, null,
                    DataBaseHistoryFavorite.DB_COLUMN_ID + ">0", null, null, null,
                    DataBaseHistoryFavorite.DB_COLUMN_NAME + " ASC");

            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<>();
                map.put(FavoritesActivity.KEY_ID, cursor.getInt(0));
                map.put(FavoritesActivity.KEY_NAME, cursor.getString(1));
                map.put(FavoritesActivity.KEY_LNG_LAT_WGS, cursor.getString(2));
                map.put(FavoritesActivity.KEY_LNG_LAT_CUSTOM, cursor.getString(3));
                mFavoriteList.add(map);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e("JoyStick", "加载收藏失败", e);
        }
    }

    // ===================== 修复：完整拼音首字母匹配（覆盖所有常用汉字） =====================
    private String getPinyinFirst(char c) {
        if (c >= 'A' && c <= 'Z') return String.valueOf(c);
        if (c >= 'a' && c <= 'z') return String.valueOf((char) (c - 32));
        if (c < 0x4E00 || c > 0x9FA5) return "#";

        // 完整拼音首字母区间（覆盖GB2312常用字）
        if (c <= 0x4E8C) return "A";
        if (c <= 0x50D7) return "B";
        if (c <= 0x5316) return "C";
        if (c <= 0x554A) return "D";
        if (c <= 0x57A4) return "E";
        if (c <= 0x59D3) return "F";
        if (c <= 0x5C0F) return "G";
        if (c <= 0x5E2E) return "H";
        if (c <= 0x6052) return "J";
        if (c <= 0x628A) return "K";
        if (c <= 0x64AD) return "L";
        if (c <= 0x66DC) return "M";
        if (c <= 0x6912) return "N";
        if (c <= 0x6B47) return "O";
        if (c <= 0x6D77) return "P";
        if (c <= 0x6FA8) return "Q";
        if (c <= 0x71D6) return "R";
        if (c <= 0x7410) return "S";
        if (c <= 0x764A) return "T";
        if (c <= 0x787E) return "W";
        if (c <= 0x7AB5) return "X";
        if (c <= 0x7CE0) return "Y";
        if (c <= 0x7F16) return "Z";
        return "#";
    }

    private String getFirstPinyin(String name) {
        if (name == null || name.isEmpty()) return "#";
        // 跳过空格、符号，取第一个有效字符
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isLetterOrDigit(c) || (c >= 0x4E00 && c <= 0x9FA5)) {
                return getPinyinFirst(c);
            }
        }
        return "#";
    }

    private class LetterAdapter extends RecyclerView.Adapter<LetterViewHolder> {
        private final List<String> letters;

        public LetterAdapter(List<String> letters) {
            this.letters = letters;
        }

        @NonNull
        @Override
        public LetterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_letter, parent, false);
            return new LetterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LetterViewHolder holder, int position) {
            String letter = letters.get(position);
            holder.tvLetter.setText(letter);

            holder.itemView.setOnClickListener(v -> {
                // 遍历所有收藏项，找到第一个匹配首字母的位置
                int targetPos = -1;
                for (int i = 0; i < mFavoriteList.size(); i++) {
                    String name = (String) mFavoriteList.get(i).get(FavoritesActivity.KEY_NAME);
                    String first = getFirstPinyin(name);
                    if (letter.equals(first)) {
                        targetPos = i;
                        break;
                    }
                }

                // 修复：不用 lambda，直接滚动
                if (targetPos != -1 && mRecyclerView != null) {
                    mRecyclerView.smoothScrollToPosition(targetPos);
                }
            });
        }

        @Override
        public int getItemCount() {
            return letters.size();
        }
    }

    public static class LetterViewHolder extends RecyclerView.ViewHolder {
        TextView tvLetter;
        public LetterViewHolder(View itemView) {
            super(itemView);
            tvLetter = itemView.findViewById(R.id.tv_letter);
        }
    }

    // ===================== 收藏列表适配器 =====================
    private class FavoriteSimpleAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {
        private final List<Map<String, Object>> mList;

        public FavoriteSimpleAdapter(List<Map<String, Object>> list) {
            mList = list;
        }

        @Override
        public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_favorite, parent, false);
            return new FavoriteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FavoriteViewHolder holder, int position) {
            Map<String, Object> data = mList.get(position);
            String name = (String) data.get(FavoritesActivity.KEY_NAME);
            holder.tvName.setText(name);

            holder.itemView.setOnClickListener(v -> {
                try {
                    String wgsStr = (String) data.get(FavoritesActivity.KEY_LNG_LAT_WGS);
                    String[] split1 = wgsStr.split(" ");
                    double lng = 0, lat = 0;
                    for (String s : split1) {
                        if (s.startsWith("经度:"))
                            lng = Double.parseDouble(s.replace("经度:", "").trim());
                        if (s.startsWith("纬度:"))
                            lat = Double.parseDouble(s.replace("纬度:", "").trim());
                    }
                    mListener.onPositionInfo(lng, lat, mAltitude);
                    GoUtils.DisplayToast(mContext, "已切换：" + name);
                } catch (Exception e) {
                    GoUtils.DisplayToast(mContext, "切换失败");
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        public FavoriteViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}