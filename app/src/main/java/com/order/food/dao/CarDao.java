package com.order.food.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.order.food.db.CarDb;
import com.order.food.db.UserDb;
import com.order.food.entity.CarInfo;
import com.order.food.entity.UserInfo;

import java.time.format.SignStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * desc   :
 */
public class CarDao {

    private CarDb mCarDb;


    public CarDao(Context context) {
        mCarDb = CarDb.getInstance(context);
    }

    /**
     * 添加
     */
    public int insert(String mobile, String title, int price, int image, int food_num, String detail) {
        SQLiteDatabase db = mCarDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mobile", mobile);
        values.put("title", title);
        values.put("price", price);
        values.put("image", image);
        values.put("food_num", food_num);
        values.put("detail", detail);
        String nullColumnHack = "values(null,?,?,?,?,?,?)";
        //执行
        int insert = (int) db.insert("car_table", nullColumnHack, values);
        db.close();
        return insert;
    }


    @SuppressLint("Range")
    public List<CarInfo> queryCarList(String mobile) {
        List<CarInfo> list = new ArrayList<>();
        SQLiteDatabase db = mCarDb.getReadableDatabase();
        String sql = "select _id,mobile,title,price,image,food_num,detail from car_table where mobile=?";
        String[] selectionArgs = {mobile};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String phone = cursor.getString(cursor.getColumnIndex("mobile"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            int price = cursor.getInt(cursor.getColumnIndex("price"));
            int image = cursor.getInt(cursor.getColumnIndex("image"));
            int food_num = cursor.getInt(cursor.getColumnIndex("food_num"));
            String detail = cursor.getString(cursor.getColumnIndex("detail"));
            list.add(new CarInfo(_id, phone, title, price, image, food_num, detail));
        }
        return list;
    }
    public int delete(String _id) {
        SQLiteDatabase db = mCarDb.getWritableDatabase();
        int delete = db.delete("car_table", " _id=?", new String[]{_id});
        db.close();
        return delete;
    }


    /***
     * 删除表数据
     */
    public void clear() {
        SQLiteDatabase db = mCarDb.getWritableDatabase();
        db.execSQL("DELETE FROM car_table");
    }

}
