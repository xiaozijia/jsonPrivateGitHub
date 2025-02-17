package com.order.food.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class OrderDb extends SQLiteOpenHelper {
    public static OrderDb sOrderDb;
    private static final String DB_NAME = "order.db";
    private static final int VERSION = 1;

    public OrderDb(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public synchronized static OrderDb getInstance(Context context) {
        if (null == sOrderDb) {
            sOrderDb = new OrderDb(context, DB_NAME, null, VERSION);
        }
        return sOrderDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table order_table(_id integer primary key autoincrement, " +
                "mobile text," +       //手机号
                "title text," +       //商品名
                "price int," +       //价格
                "image integer," +       //图片
                "food_num int," +       //数量
                "detail text," +    //详情
                "pay_method text," +    //支付方式
                "address text," +    //配送地址
                "order_num text" +    //订单号
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}