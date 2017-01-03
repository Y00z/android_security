package org.yooz.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Yooz on 2016/2/11.
 */
public class AntivirusDao {
    //查询md5是否在病毒库里面
    public static String checkFileVirus(String md6) {
        String desc = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase("data/data/org.yooz.safe/files/antivirus.db", null,
                SQLiteDatabase.OPEN_READWRITE);
        //查询程序md5是否在病毒数据库里面
        Cursor cursor = db.rawQuery("select desc from datable where md5 =?", new String[]{md6});
        if(cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        return  desc;
    }

    //添加信息到病毒库
    public static void addVirus(String md5 , String desc){
        SQLiteDatabase db = SQLiteDatabase.openDatabase("data/data/org.yooz.safe/files/antivirus.db", null,
                SQLiteDatabase.OPEN_READWRITE);
        ContentValues contentValues = new ContentValues();
        contentValues.put("md5" , md5);
        contentValues.put("desc", desc);
        contentValues.put("type",6);
        contentValues.put("name","病毒木马");
        db.insert("datable",null,contentValues);
        db.close();
    }
}
