package com.order.food.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class CarDb extends SQLiteOpenHelper {
    public static CarDb sCarDb;
    private static final String DB_NAME = "car.db";
    private static final int VERSION = 1;

    public CarDb(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public synchronized static CarDb getInstance(Context context) {
        if (null == sCarDb) {
            sCarDb = new CarDb(context, DB_NAME, null, VERSION);
        }
        return sCarDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table car_table(_id integer primary key autoincrement, " +
                "mobile text," +       //手机号
                "title text," +       //商品名
                "price int," +       //价格
                "image integer," +       //图片
                "food_num int," +       //数量
                "detail text" +    //详情
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}