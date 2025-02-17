package com.order.food.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class UserDb extends SQLiteOpenHelper {
    public static UserDb sUserDb;
    private static final String DB_NAME = "user_info.db";
    private static final int VERSION = 1;

    public UserDb(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public synchronized static UserDb getInstance(Context context) {
        if (null == sUserDb) {
            sUserDb = new UserDb(context, DB_NAME, null, VERSION);
        }
        return sUserDb;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user_table(_id integer primary key autoincrement, " +
                "mobile text," +
                "password text" +
                ")");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}