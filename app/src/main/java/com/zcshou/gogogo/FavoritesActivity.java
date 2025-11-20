package com.zcshou.gogogo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

import com.zcshou.database.DataBaseHistoryFavorite;
import com.zcshou.utils.GoUtils;
import com.zcshou.utils.PinyinUtils;
import java.util.Collections;
import java.util.Comparator;

public class FavoritesActivity extends BaseActivity {
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_TIME = "KEY_TIME";
    public static final String KEY_LNG_LAT_WGS = "KEY_LNG_LAT_WGS";
    public static final String KEY_LNG_LAT_CUSTOM = "KEY_LNG_LAT_CUSTOM";

    public static final String KEY_First_letter = "first_letter";

    private ListView mListView;
    private TextView mNoDataText;
    private LinearLayout mSearchLayout;
    private SQLiteDatabase mFavoriteDB;
    private List<Map<String, Object>> mAllFavorites;
    private List<Map<String, Object>> mFilteredFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        setContentView(R.layout.activity_favorites);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initDatabase();
        initSearchView();
        initListView();
    }

    @Override
    protected void onDestroy() {
        if (mFavoriteDB != null) mFavoriteDB.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_delete_all_favorites) {
            new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("确定要清空全部收藏吗?")
                    .setPositiveButton("确定", (dialog, which) -> {
                        try {
                            mFavoriteDB.delete(DataBaseHistoryFavorite.TABLE_NAME, null, null);
                            GoUtils.DisplayToast(this, "已清空收藏");
                            android.content.Intent intent = new android.content.Intent("FAVORITE_CHANGED");
                            sendBroadcast(intent);
                            updateList();
                        } catch (Exception e) {
                            Log.e("FavoritesActivity", "delete all error");
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDatabase() {
        try {
            DataBaseHistoryFavorite dbHelper = new DataBaseHistoryFavorite(getApplicationContext());
            mFavoriteDB = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e("FavoritesActivity", "initDatabase error");
        }
    }

    private List<Map<String, Object>> fetchAll() {
        List<Map<String, Object>> data = new ArrayList<>();
        try {
            Cursor cursor = mFavoriteDB.query(
                    DataBaseHistoryFavorite.TABLE_NAME,
                    new String[]{
                            DataBaseHistoryFavorite.DB_COLUMN_ID,
                            DataBaseHistoryFavorite.DB_COLUMN_NAME,
                            DataBaseHistoryFavorite.DB_COLUMN_LONGITUDE_WGS84,
                            DataBaseHistoryFavorite.DB_COLUMN_LATITUDE_WGS84,
                            DataBaseHistoryFavorite.DB_COLUMN_TIMESTAMP,
                            DataBaseHistoryFavorite.DB_COLUMN_LONGITUDE_CUSTOM,
                            DataBaseHistoryFavorite.DB_COLUMN_LATITUDE_CUSTOM,
                            DataBaseHistoryFavorite.DB_COLUMN_FIRST_LETTER
                    },
                    DataBaseHistoryFavorite.DB_COLUMN_ID + " > ?",
                    new String[]{"0"},
                    null, null,
                    DataBaseHistoryFavorite.DB_COLUMN_TIMESTAMP + " DESC",
                    null
            );

            while (cursor.moveToNext()) {
                Map<String, Object> item = new HashMap<>();
                int id         = cursor.getInt(0);
                String name    = cursor.getString(1);
                String lngWgs  = cursor.getString(2);
                String latWgs  = cursor.getString(3);
                long ts        = cursor.getLong(4);
                String lngBd   = cursor.getString(5);
                String latBd   = cursor.getString(6);
                String firstLetter = cursor.getString(7);

                // ====================== 【日志输出】 ======================

                double dLngWgs = new BigDecimal(lngWgs).setScale(8, RoundingMode.HALF_UP).doubleValue();
                double dLatWgs = new BigDecimal(latWgs).setScale(8, RoundingMode.HALF_UP).doubleValue();
                double dLngBd  = new BigDecimal(lngBd).setScale(8, RoundingMode.HALF_UP).doubleValue();
                double dLatBd  = new BigDecimal(latBd).setScale(8, RoundingMode.HALF_UP).doubleValue();

                item.put(KEY_ID, String.valueOf(id));
                item.put(KEY_NAME, name != null ? name : "收藏位置");
                item.put(KEY_TIME, GoUtils.timeStamp2Date(String.valueOf(ts)));
                item.put(KEY_LNG_LAT_WGS, "[经度:" + dLngWgs + " 纬度:" + dLatWgs + "]");
                item.put(KEY_LNG_LAT_CUSTOM, "[经度:" + dLngBd + " 纬度:" + dLatBd + "]");
                item.put(KEY_First_letter, firstLetter);
                data.add(item);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("FavoritesActivity", "fetchAll error", e);
        }
        return data;
    }

    private void initSearchView() {
        SearchView sv = findViewById(R.id.searchViewFavorites);
        sv.onActionViewExpanded();
        sv.setSubmitButtonEnabled(false);
        sv.setFocusable(false);
        sv.clearFocus();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    setAdapter(mAllFavorites);
                } else {
                    List<Map<String, Object>> filtered = new ArrayList<>();
                    for (Map<String, Object> m : mAllFavorites) {
                        if (m.toString().contains(newText)) filtered.add(m);
                    }
                    setAdapter(filtered.isEmpty() ? mAllFavorites : filtered);
                }
                return false;
            }
        });
    }

    private void setAdapter(List<Map<String, Object>> data) {
        SimpleAdapter adapter = new SimpleAdapter(
                this, data, R.layout.history_item,
                new String[]{KEY_ID, KEY_NAME, KEY_TIME, KEY_LNG_LAT_WGS, KEY_LNG_LAT_CUSTOM},
                new int[]{R.id.LocationID, R.id.LocationText, R.id.TimeText, R.id.WGSLatLngText, R.id.BDLatLngText});
        mListView.setAdapter(adapter);
    }

    private void initListView() {
        mNoDataText  = findViewById(R.id.favorites_no_textview);
        mSearchLayout = findViewById(R.id.search_linear_favorites);
        mListView    = findViewById(R.id.favorites_list_view);

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            String name = ((TextView) view.findViewById(R.id.LocationText)).getText().toString();
            String bdLatLng = ((TextView) view.findViewById(R.id.BDLatLngText)).getText().toString();
            bdLatLng = bdLatLng.substring(bdLatLng.indexOf('[') + 1, bdLatLng.indexOf(']'));
            String[] parts = bdLatLng.split(" ");
            String lng = parts[0].substring(parts[0].indexOf(':') + 1);
            String lat = parts[1].substring(parts[1].indexOf(':') + 1);

            if (!MainActivity.showLocation(name, lng, lat)) {
                GoUtils.DisplayToast(this, getResources().getString(R.string.history_error_location));
            }
            finish();
        });

        mListView.setOnItemLongClickListener((parent, view, position, id) -> {
            PopupMenu popup = new PopupMenu(this, view);
            popup.setGravity(Gravity.END | Gravity.BOTTOM);
            popup.getMenu().add("编辑名称");
            popup.getMenu().add("删除");
            popup.setOnMenuItemClickListener(item -> {
                String locId = ((TextView) view.findViewById(R.id.LocationID)).getText().toString();
                String name  = ((TextView) view.findViewById(R.id.LocationText)).getText().toString();
                switch (item.getTitle().toString()) {
                    case "编辑名称":
                        showEditDialog(locId, name);
                        return true;
                    case "删除":
                        showDeleteDialog(locId);
                        return true;
                    default:
                        return false;
                }
            });
            popup.show();
            return true;
        });

        updateList();
    }

    private void updateList() {
        mAllFavorites = fetchAll();
        if (mAllFavorites.isEmpty()) {
            mListView.setVisibility(View.GONE);
            mSearchLayout.setVisibility(View.GONE);
            mNoDataText.setVisibility(View.VISIBLE);
        } else {
            mNoDataText.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mSearchLayout.setVisibility(View.VISIBLE);
            setAdapter(mAllFavorites);
        }
    }

    private void showEditDialog(String id, String currentName) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentName);
        new AlertDialog.Builder(this)
                .setTitle("编辑名称")
                .setView(input)
                .setPositiveButton("确认", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(newName)) {
                        DataBaseHistoryFavorite.updateFavoriteName(mFavoriteDB, id, newName);
                        updateList();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showDeleteDialog(String id) {
        new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("确定要删除该收藏吗?")
                .setPositiveButton("确定", (dialog, which) -> {
                    try {
                        mFavoriteDB.delete(DataBaseHistoryFavorite.TABLE_NAME,
                                DataBaseHistoryFavorite.DB_COLUMN_ID + " = ?", new String[]{id});
                        GoUtils.DisplayToast(this, "删除成功");
                        android.content.Intent intent = new android.content.Intent("FAVORITE_CHANGED");
                        sendBroadcast(intent);
                        updateList();
                    } catch (Exception e) {
                        Log.e("FavoritesActivity", "delete error");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
