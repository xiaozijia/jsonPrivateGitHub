package com.order.food.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class PictureDb extends SQLiteOpenHelper {
    private static PictureDb sPictureDb;
    private static final String DB_NAME = "picture_info.db"; // 数据库文件名
    private static final int VERSION = 2; // 数据库版本，从 1 增加到 2

    public PictureDb(@Nullable Context context, @Nullable String name,
                     @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public synchronized static PictureDb getInstance(Context context) {
        if (sPictureDb == null) {
            sPictureDb = new PictureDb(context, DB_NAME, null, VERSION);
        }
        return sPictureDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建 picture_table 表，用于存储图片 ID 和 URL
        db.execSQL("CREATE TABLE picture_table (" +
                "id TEXT PRIMARY KEY," + // 图片 ID 作为主键
                "imageUrl TEXT" +       // 图片 URL
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果需要升级数据库，可以在这里处理
        db.execSQL("DROP TABLE IF EXISTS picture_table");
        onCreate(db);
    }
}
