package org.yooz.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Yooz on 2016/2/15.
 */
public class AppLockDao {

    private final AppLockOpenHelper helper;

    public AppLockDao(Context context) {
        helper = new AppLockOpenHelper(context);
    }

    //添加程序锁到包名
    public void add(String packageName) {
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("packagename", packageName);
        writableDatabase.insert("info", null, contentValues);
        writableDatabase.close();
    }

    //从程序锁中删除
    public void delete(String packageName) {
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        writableDatabase.delete("info", "packagename = ?", new String[]{packageName});
        writableDatabase.close();
    }

    //查询程序 是否在程序锁里面
    public boolean find(String packageName) {
        boolean result = false;
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        Cursor cursor = readableDatabase.query("info", null, "packagename = ?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        readableDatabase.close();
        return result;
    }

    //查询全部的锁定的包名
    public ArrayList<String> findAll() {
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        Cursor cursor = readableDatabase.query("info", new String[]{"packageName"}, null, null, null, null, null);
        ArrayList<String> packageNameLists = new ArrayList<String>();
        while (cursor.moveToNext()) {
            packageNameLists.add(cursor.getString(0));
        }
        cursor.close();
        readableDatabase.close();
        return packageNameLists;
    }


}
