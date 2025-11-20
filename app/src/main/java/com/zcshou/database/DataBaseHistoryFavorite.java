package com.zcshou.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.elvishew.xlog.XLog;

public class DataBaseHistoryFavorite extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "FavoriteLocation";
    public static final String DB_COLUMN_ID = "DB_COLUMN_ID";

    public static final String DB_COLUMN_FIRST_LETTER = "DB_COLUMN_FIRST_LETTER";
    public static final String DB_COLUMN_NAME = "DB_COLUMN_NAME";
    public static final String DB_COLUMN_LONGITUDE_WGS84 = "DB_COLUMN_LONGITUDE_WGS84";
    public static final String DB_COLUMN_LATITUDE_WGS84 = "DB_COLUMN_LATITUDE_WGS84";
    public static final String DB_COLUMN_TIMESTAMP = "DB_COLUMN_TIMESTAMP";
    public static final String DB_COLUMN_LONGITUDE_CUSTOM = "DB_COLUMN_LONGITUDE_CUSTOM";
    public static final String DB_COLUMN_LATITUDE_CUSTOM = "DB_COLUMN_LATITUDE_CUSTOM";

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "FavoriteLocation.db";
    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME +
            " (DB_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, DB_COLUMN_NAME TEXT, " +
            "DB_COLUMN_LONGITUDE_WGS84 TEXT NOT NULL, DB_COLUMN_LATITUDE_WGS84 TEXT NOT NULL, " +
            "DB_COLUMN_TIMESTAMP BIGINT NOT NULL, DB_COLUMN_LONGITUDE_CUSTOM TEXT NOT NULL, DB_COLUMN_LATITUDE_CUSTOM TEXT NOT NULL, " +
            DB_COLUMN_FIRST_LETTER + " TEXT)";  // 👈 只加这一段

    public DataBaseHistoryFavorite(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * 保存收藏位置（按坐标去重，重复则更新时间戳和名称）
     */
    public static void saveFavorite(SQLiteDatabase db, ContentValues contentValues) {
        try {
            String lngWgs = contentValues.getAsString(DB_COLUMN_LONGITUDE_WGS84);
            String latWgs = contentValues.getAsString(DB_COLUMN_LATITUDE_WGS84);
            // 先删除同坐标旧记录再插入，实现 upsert
            db.delete(TABLE_NAME,
                    DB_COLUMN_LONGITUDE_WGS84 + " = ? AND " + DB_COLUMN_LATITUDE_WGS84 + " = ?",
                    new String[]{lngWgs, latWgs});
            db.insert(TABLE_NAME, null, contentValues);
        } catch (Exception e) {
            XLog.e("DATABASE: favorite insert error");
        }
    }

    /**
     * 修改收藏名称
     */
    public static void updateFavoriteName(SQLiteDatabase db, String id, String name) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(DB_COLUMN_NAME, name);
            db.update(TABLE_NAME, cv, DB_COLUMN_ID + " = ?", new String[]{id});
        } catch (Exception e) {
            XLog.e("DATABASE: favorite update error");
        }
    }
}
