package com.order.food.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AddressDb extends SQLiteOpenHelper {
    public static AddressDb sAddressDb;
    private static final String DB_NAME = "address.db";
    private static final int VERSION = 1;

    public AddressDb(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public synchronized static AddressDb getInstance(Context context) {
        if (null == sAddressDb) {
            sAddressDb = new AddressDb(context, DB_NAME, null, VERSION);
        }
        return sAddressDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table address_table(_id integer primary key autoincrement, " +
                "mobile text," +
                "address text" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 这里可以处理数据库升级逻辑，目前暂时为空
    }
}