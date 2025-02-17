package com.order.food.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.order.food.RegisterActivity;
import com.order.food.db.UserDb;
import com.order.food.entity.UserInfo;

/**
 * desc   :数据访问层
 */
public class UserDao {

    private UserDb mUserDb;
    private Context context;


    public UserDao(Context context) {
        mUserDb = UserDb.getInstance(context);
        this.context=context;
    }

    /**
     * 注册
     */
    public int insert(String mobile, String password) {
        if(isMobileExist(mobile)&& updatePassword(mobile,password)){
            Toast.makeText(context, "您的密码已经直接修改", Toast.LENGTH_SHORT).show();
            return 0;
        }
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
    public boolean updatePassword(String mobile, String newPassword) {
        SQLiteDatabase db = mUserDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        String whereClause = "mobile = ?";
        String[] whereArgs = {mobile};
        int rowsAffected = db.update("user_table", values, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    public boolean isMobileExist(String mobile) {
        SQLiteDatabase db = mUserDb.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM user_table WHERE mobile = ?";
        String[] selectionArgs = {mobile};
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        try {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count > 0;
            }
        } finally {
            cursor.close();
        }
        return false;
    }
}
