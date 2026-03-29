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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final int WINDOW_TYPE_JOYSTICK = 0;
    private static final int WINDOW_TYPE_MAP = 1;
    private static final int WINDOW_TYPE_HISTORY = 2;
    private static final String TAG = "JoyStick_DEBUG";
    private final Context mContext;
    private WindowManager.LayoutParams mWindowParamCurrent;
    private WindowManager mWindowManager;
    private int mCurWin = WINDOW_TYPE_JOYSTICK;
    private final LayoutInflater inflater;
    private JoyStickClickListener mListener;
    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_ITEM = 1;
    private View mJoystickLayout;
    private double mAltitude = 55.0;
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
            mAdapter.notifyDataSetChanged();
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
                    // WINDOW_TYPE_HISTORY 分支里
                    mWindowParamCurrent.width = dp2px(300);
                    mWindowParamCurrent.height = dp2px(500);
                    mWindowManager.addView(mHistoryLayout, mWindowParamCurrent);
                }
                break;
            case WINDOW_TYPE_JOYSTICK:
                if (mMapLayout.getParent() != null) mWindowManager.removeView(mMapLayout);
                if (mHistoryLayout.getParent() != null) mWindowManager.removeView(mHistoryLayout);
                if (mJoystickLayout.getParent() == null) {
                    mWindowParamCurrent.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    mWindowParamCurrent.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    mWindowManager.addView(mJoystickLayout, mWindowParamCurrent);
                }
                break;
        }
    }

    private int dp2px(int dp) {
        return (int) (dp * mContext.getResources().getDisplayMetrics().density + 0.5f);
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
        } catch (Exception ignored) {
        }
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
        mJoystickLayout = inflater.inflate(R.layout.joystick, null);
        mJoystickLayout.setOnTouchListener(new JoyStickOnTouchListener(mJoystickLayout));

        ImageButton btnHistory = mJoystickLayout.findViewById(R.id.joystick_history);
        btnHistory.setOnClickListener(v -> {
            mCurWin = WINDOW_TYPE_HISTORY;
            show();
        });
    }

    

    private class JoyStickOnTouchListener implements OnTouchListener {
        private final View mRootView;
        private int x, y;

        JoyStickOnTouchListener(View rootView) { this.mRootView = rootView; }

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
                    try {
                        mWindowManager.updateViewLayout(mRootView, mWindowParamCurrent);
                    } catch (IllegalArgumentException ignored) {}
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
            }
            return false;
        }
    }

    public interface JoyStickClickListener {
        

        void onPositionInfo(double lng, double lat, double alt);
    }

    @SuppressLint("InflateParams")
    private void initJoyStickMapView() {
        mMapLayout = (FrameLayout) inflater.inflate(R.layout.joystick_map, null);
        mMapLayout.setOnTouchListener(new JoyStickOnTouchListener(mMapLayout));
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
        mHistoryLayout.setOnTouchListener(new JoyStickOnTouchListener(mHistoryLayout));

        mEmptyView = mHistoryLayout.findViewById(R.id.empty_view);
        ImageButton btnClose = mHistoryLayout.findViewById(R.id.joystick_fav_close);
        Button btnReset = mHistoryLayout.findViewById(R.id.joystick_fav_reset);
        btnReset.setOnClickListener(v -> {
            fetchAllRecord();
        });
        btnClose.setOnClickListener(v -> {
            mCurWin = WINDOW_TYPE_JOYSTICK;
            show();
        });

        mRecyclerView = mHistoryLayout.findViewById(R.id.joystick_fav_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        // ✅ 先创建适配器
        mAdapter = new FavoriteSimpleAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);
        // ✅ 再加载数据
        fetchAllRecord();

        RecyclerView letterRecycler = mHistoryLayout.findViewById(R.id.letter_recycler);
// QWERTY 键盘每行10列，用""补位对齐
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 10);
        letterRecycler.setLayoutManager(gridLayoutManager);
// 第1行：Q W E R T Y U I O P（10键，无需补位）
// 第2行：_ A S D F G H J K L _（9键，左右各补半格，用""补1格左，共10=1+9）
// 第3行：_ _ Z X C V B N M _ _（7键，左补2格，共10=2+7+1）
        List<String> letters = new ArrayList<>();
        String[] row1 = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
        String[] row2 = {"", "A", "S", "D", "F", "G", "H", "J", "K", "L"};  // 左补1格
        String[] row3 = {"", "", "Z", "X", "C", "V", "B", "N", "M", ""};    // 左补2格，右补1格
        for (String s : row1) letters.add(s);
        for (String s : row2) letters.add(s);
        for (String s : row3) letters.add(s);
        letterRecycler.setAdapter(new LetterAdapter(letters));
    }

    private void updateEmptyState() {
        int size = mAdapter.mList.size();
        Log.d(TAG, "【空布局判断】当前列表数量：" + size);

        if (size == 0) {
            Toast.makeText(mContext, "没有地址数据", Toast.LENGTH_SHORT).show();
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            Log.d(TAG, "【空布局】显示列表");
        }
    }

    private void fetchAllRecord() {
        Log.d(TAG, "========== 开始加载数据 ==========");

        // 1. 清空【适配器自己的列表】，不是外面的！
        mAdapter.mList.clear();
        mFavoriteList.clear();
        Log.d(TAG, "【清空】适配器内部列表已清空");

        try {
            DataBaseHistoryFavorite helper = new DataBaseHistoryFavorite(mContext);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(DataBaseHistoryFavorite.TABLE_NAME, null,
                    DataBaseHistoryFavorite.DB_COLUMN_ID + ">0", null, null, null,
                    DataBaseHistoryFavorite.DB_COLUMN_NAME + " ASC");

            Log.d(TAG, "【数据库】查到总条数：" + cursor.getCount());
            String lastLetter = "";
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<>();
                map.put(FavoritesActivity.KEY_ID, cursor.getInt(0));
                String name = cursor.getString(1);
                map.put(FavoritesActivity.KEY_NAME, name);
                map.put(FavoritesActivity.KEY_LNG_LAT_WGS, "经度:" + cursor.getString(2) + " 纬度:" + cursor.getString(3));
                map.put(FavoritesActivity.KEY_LNG_LAT_CUSTOM, "经度:" + cursor.getString(5) + " 纬度:" + cursor.getString(6));
                String firstLetter = "";
                // 读取首字母
                if (cursor.getColumnCount() >= 7) {
                    firstLetter = cursor.getString(7);
                    if (firstLetter == null) firstLetter = "#";
                    map.put(FavoritesActivity.KEY_First_letter, firstLetter);
                    Log.d(TAG, "【读取】" + name + " → 首字母：" + firstLetter);
                }

                if (!firstLetter.equalsIgnoreCase(lastLetter)) {
                    Map<String, Object> header = new HashMap<>();
                    header.put("__type__", "header");
                    header.put("__letter__", firstLetter.toUpperCase());
                    mAdapter.mList.add(header);
                    mFavoriteList.add(header);  // 注意：header 也要加入 mFavoriteList 以便筛选时能重建
                    lastLetter = firstLetter;
                }

                // ✅ 关键：直接加到【适配器内部列表】
                mAdapter.mList.add(map);
                mFavoriteList.add(map);
            }

            Log.d(TAG, "【最终】适配器内数据总数：" + mAdapter.mList.size());
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "【错误】加载失败", e);
        }

        // 2. 刷新适配器 + 刷新空布局
        mAdapter.notifyDataSetChanged();
        updateEmptyState();
        Log.d(TAG, "【刷新】列表已通知刷新");
        Log.d(TAG, "========== 加载完成 ==========");
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
            if (letter.isEmpty()) {
                // 空占位格：不可点击，透明
                holder.itemView.setOnClickListener(null);
                holder.itemView.setAlpha(0f);
                return;
            }
            holder.itemView.setAlpha(1f);

            holder.itemView.setOnClickListener(v -> {
                try {
                    mAdapter.mList.clear();
                    // 重新插入 header + 该字母的条目
                    boolean hasResult = false;
                    for (Map<String, Object> item : mFavoriteList) {
                        if ("header".equals(item.get("__type__"))) continue; // 跳过旧 header
                        String first = (String) item.get(FavoritesActivity.KEY_First_letter);
                        if (letter.equalsIgnoreCase(first)) {
                            if (!hasResult) {
                                // 插一个 header
                                Map<String, Object> header = new HashMap<>();
                                header.put("__type__", "header");
                                header.put("__letter__", letter.toUpperCase());
                                mAdapter.mList.add(header);
                                hasResult = true;
                            }
                            mAdapter.mList.add(item);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    updateEmptyState();
                } catch (Exception e) {
                    Log.e("JoyStick", "筛选失败", e);
                }
            });
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "【列表询问】当前数据数量 = " + letters.size());
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

    private class FavoriteSimpleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public List<Map<String, Object>> mList;

        public FavoriteSimpleAdapter(List<Map<String, Object>> list) {
            mList = list;
        }

        @Override
        public int getItemViewType(int position) {
            Map<String, Object> item = mList.get(position);
            return "header".equals(item.get("__type__")) ? ITEM_TYPE_HEADER : ITEM_TYPE_ITEM;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_HEADER) {
                // 标题行：直接用一个 TextView 即可，也可新建 item_letter_header.xml
                TextView tv = new TextView(mContext);
                tv.setPadding(16, 8, 16, 4);
                tv.setTextSize(12);
                tv.setTextColor(0xFF888888);
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tv.setLayoutParams(lp);
                return new HeaderViewHolder(tv);
            } else {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_favorite, parent, false);
                return new FavoriteViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Map<String, Object> data = mList.get(position);
            if (holder instanceof HeaderViewHolder) {
                ((HeaderViewHolder) holder).tvHeader.setText((String) data.get("__letter__"));
            } else {
                FavoriteViewHolder fHolder = (FavoriteViewHolder) holder;
                String name = (String) data.get(FavoritesActivity.KEY_NAME);
                fHolder.tvName.setText(name);
                fHolder.itemView.setOnClickListener(v -> {
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
                        // ✅ 加这一行：BD-09 → WGS-84，和 doGoLocation 保持一致
                        double[] wgs = MapUtils.bd2wgs(lng, lat);
                        mListener.onPositionInfo(wgs[0], wgs[1], mAltitude);
                        GoUtils.DisplayToast(mContext, "已切换：" + name);
                    } catch (Exception e) {
                        GoUtils.DisplayToast(mContext, "切换失败");
                    }
                    mCurWin = WINDOW_TYPE_JOYSTICK;
                    show();
                });
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    // 新增 HeaderViewHolder
    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        public HeaderViewHolder(TextView tv) {
            super(tv);
            tvHeader = tv;
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