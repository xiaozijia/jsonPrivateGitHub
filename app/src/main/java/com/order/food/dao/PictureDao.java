package com.order.food.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.order.food.db.OrderDb;
import com.order.food.db.PictureDb;

public class PictureDao {
    private PictureDb mPictureDb; // 使用 PictureDb 管理数据库

    public PictureDao(Context context) {
        mPictureDb = PictureDb.getInstance(context);
    }

    /**
     * 插入或更新图片信息
     * @param id 图片ID
     * @param imageUrl 图片URL
     */
    public void insertOrUpdate(String id, String imageUrl) {
        SQLiteDatabase db = mPictureDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("imageUrl", imageUrl);
        // 使用 insertWithOnConflict，当 id 存在时替换，不存在时插入
        db.insertWithOnConflict("picture_table", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    /**
     * 通过 ID 查询图片信息
     * @param id 图片ID
     * @return 查询到的图片信息，如果不存在则返回 null
     */
    @SuppressLint("Range")
    public String queryById(String id) {
        String imageUrl = null;
        SQLiteDatabase db = mPictureDb.getReadableDatabase();
        String sql = "SELECT id, imageUrl FROM picture_table WHERE id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{id});
        if (cursor.moveToNext()) {
            String imageId = cursor.getString(cursor.getColumnIndex("id"));
            imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
        }
        cursor.close();
        db.close();
        return imageUrl;
    }
}
