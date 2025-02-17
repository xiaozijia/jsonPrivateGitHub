package com.order.food.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.order.food.db.UserDb;
import com.order.food.entity.UserInfo;

/**
 * desc   :数据访问层
 */
public class UserDao {

    private UserDb mUserDb;


    public UserDao(Context context) {
        mUserDb = UserDb.getInstance(context);
    }

    /**
     * 注册
     */
    public int insert(String mobile, String password) {
        SQLiteDatabase db = mUserDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mobile", mobile);
        values.put("password", password);
        String nullColumnHack = "values(null,?,?)";
        //执行
        int insert = (int) db.insert("user_table", nullColumnHack, values);
        db.close();
        return insert;
    }


    @SuppressLint("Range")
    public UserInfo login(String mobile) {
        UserInfo userInfo = null;
        SQLiteDatabase db = mUserDb.getReadableDatabase();
        String sql = "select _id,mobile,password from user_table where mobile=?";
        String[] selectionArgs = {mobile};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        if (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String phone = cursor.getString(cursor.getColumnIndex("mobile"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            userInfo = new UserInfo(_id, phone, password);
        }
        return userInfo;
    }


    /**
     * 修改密码
     */
    public int update(String _id, String password) {
        SQLiteDatabase db = mUserDb.getReadableDatabase();
        // 填充占位符
        ContentValues values = new ContentValues();
        values.put("password", password);
        // 执行SQL
        int update = db.update("user_table", values, " _id=?", new String[]{_id});
        // 关闭数据库连接
        db.close();
        return update;
    }
}
