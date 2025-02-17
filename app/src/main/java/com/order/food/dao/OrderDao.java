package com.order.food.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.order.food.db.CarDb;
import com.order.food.db.OrderDb;
import com.order.food.entity.CarInfo;
import com.order.food.entity.OrderInfo;
import com.order.food.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * desc   :
 */
public class OrderDao {

    private OrderDb mOrderDb;


    public OrderDao(Context context) {
        mOrderDb = OrderDb.getInstance(context);
    }

    /**
     *向数据库中添加数据
     */
    public int insert(String mobile, String title, int price, int image, int food_num, String detail,String pay_method,String address) {
        SQLiteDatabase db = mOrderDb.getWritableDatabase();//以写的形式打开数据库调用OderDao.Db构造方法配置的信息 来创建 person_info.db 数据库只执行一次onCreate方法
        ContentValues values = new ContentValues();
        values.put("mobile", mobile);
        values.put("title", title);
        values.put("price", price);
        values.put("image", image);
        values.put("food_num", food_num);
        values.put("detail", detail);
        values.put("pay_method", pay_method);
        values.put("address", address);
        values.put("order_num", Utils.generateOrderNumber());
        String nullColumnHack = "values(null,?,?,?,?,?,?,?,?,?)";
        //执行
        int insert = (int) db.insert("order_table", nullColumnHack, values);
        db.close();
        return insert;
    }


    @SuppressLint("Range")
    public List<OrderInfo> queryOrderList(String mobile) {
        List<OrderInfo> list = new ArrayList<>();
        SQLiteDatabase db = mOrderDb.getReadableDatabase();
        String sql = "select _id,mobile,title,price,image,food_num,detail,pay_method,address,order_num from order_table where mobile=?";
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
            String pay_method = cursor.getString(cursor.getColumnIndex("pay_method"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String order_num = cursor.getString(cursor.getColumnIndex("order_num"));
            list.add(new OrderInfo(_id, phone, title, price, image, food_num, detail,pay_method,address,order_num));
        }
        return list;
    }
    public int delete(String _id) {
        SQLiteDatabase db = mOrderDb.getWritableDatabase();
        int delete = db.delete("order_table", " _id=?", new String[]{_id});
        db.close();
        return delete;
    }

}
