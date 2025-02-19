package com.order.food.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.order.food.db.AddressDb;

import java.util.ArrayList;
import java.util.List;

public class AddressDao {
    private AddressDb mAddressDb;

    public AddressDao(Context context) {
        mAddressDb = AddressDb.getInstance(context);
    }

    /**
     * 添加地址，插入前检查是否已有相同地址
     */
    public long insert(String mobile, String address) {
        SQLiteDatabase db = mAddressDb.getWritableDatabase();
        String sql = "SELECT COUNT(*) FROM address_table WHERE mobile = ? AND address = ?";
        String[] selectionArgs = {mobile, address};
        Cursor cursor = null;
        long insertResult = -1;

        try {
            cursor = db.rawQuery(sql, selectionArgs);
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                if (count == 0) { // 如果地址不存在，则插入
                    ContentValues values = new ContentValues();
                    values.put("mobile", mobile);
                    values.put("address", address);
                    insertResult = db.insert("address_table", null, values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return insertResult;
    }

    /**
     * 根据手机号查询所有地址
     */
    @NonNull
    public List<String> queryAddressesByMobile(String mobile) {
        List<String> addressList = new ArrayList<>();
        SQLiteDatabase db = mAddressDb.getReadableDatabase();
        String sql = "select address from address_table where mobile=?";
        String[] selectionArgs = {mobile};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        int addressColumnIndex = cursor.getColumnIndex("address");
        if (addressColumnIndex != -1) {
            while (cursor.moveToNext()) {
                String address = cursor.getString(addressColumnIndex);
                addressList.add(address);
            }
        }
        cursor.close();
        db.close();
        return addressList;
    }

    /**
     * 删除指定手机号和地址的记录
     */
    public int delete(String mobile, String address) {
        SQLiteDatabase db = mAddressDb.getWritableDatabase();
        int deleteCount = db.delete("address_table", "mobile=? AND address=?", new String[]{mobile, address});
        db.close();
        return deleteCount;
    }

    /**
     * 删除指定手机号的所有地址记录
     */
    public int deleteAllAddressesByMobile(String mobile) {
        SQLiteDatabase db = mAddressDb.getWritableDatabase();
        int deleteCount = db.delete("address_table", "mobile=?", new String[]{mobile});
        db.close();
        return deleteCount;
    }



    public int update(String mobile, String oldAddress, String newAddress) {
        SQLiteDatabase db = mAddressDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("address", newAddress);

        String whereClause = "mobile = ? AND address = ?";
        String[] whereArgs = {mobile, oldAddress};

        int updateCount = db.update("address_table", values, whereClause, whereArgs);
        db.close();
        return updateCount;
    }
}
