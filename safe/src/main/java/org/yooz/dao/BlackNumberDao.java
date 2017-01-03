package org.yooz.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.yooz.bean.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

public class BlackNumberDao {

	private BlackNumberOpenHelper helper;

	public BlackNumberDao(Context context) {
		helper = new BlackNumberOpenHelper(context);
	}

	public boolean add(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		long rowId = db.insert("blacknumber", null, values);
		if (rowId == -1) {
			return false;
		} else {
			return true;
		}
	}

	public boolean delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int rowNumber = db.delete("blacknumber", "number = ?",
				new String[] { number });
		if (rowNumber == 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean changeMode(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		int rowNumber = db.update("blacknumber", values, "number = ?",
				new String[] { number });
		if (rowNumber == 0) {
			return false;
		} else {
			return true;
		}
	}

	public String findNumber(String number) {
		String mode = "";
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", new String[] { "mode" },
				"number = ?", new String[] { number }, null, null, null);
		if (cursor.moveToNext()) {
			mode = cursor.getString(0);
		}
		db.close();
		cursor.close();
		return mode;
	}
	
	public int getTotal() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blacknumber ", null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		db.close();
		System.out.println("count:"+count);
		return count;
	}

	public List<BlackNumberInfo> findAll() {
		SQLiteDatabase db = helper.getReadableDatabase();
		List<BlackNumberInfo> arrayList = new ArrayList<BlackNumberInfo>();
		Cursor cursor = db
				.query("blacknumber", new String[] { "number", "mode" }, null,
						null, null, null, null);
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setMode(cursor.getString(1));
			blackNumberInfo.setNumber(cursor.getString(0));
			arrayList.add(blackNumberInfo);
		}
		db.close();
		cursor.close();
		return arrayList;
	}

	/*
	 * 分页
	 * pageNumber ： 当前页
	 * pagesize	： 一页共多少数据
	 */ 
	public List<BlackNumberInfo> findPage(int pageNumber, int pagesize) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select number,mode from blacknumber limit ? offset ?",
				new String[] { String.valueOf(pagesize),
						String.valueOf(pagesize * pageNumber) });
		List<BlackNumberInfo> arrayList = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNumber(cursor.getString(0));
			info.setMode(cursor.getString(1));
			arrayList.add(info);
		}
		cursor.close();
		db.close();
		return arrayList;
	}
	
	/**
	 * 分批加载
	 * startIndex：开始的位置
	 * maxCount：	每页展示最多的条目
	 */

	public List<BlackNumberInfo> findPartial(int startIndex, int maxCount) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select number,mode from blacknumber limit ? offset ?",
				new String[] { String.valueOf(maxCount),
						String.valueOf(startIndex) });
		List<BlackNumberInfo> arrayList = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNumber(cursor.getString(0));
			info.setMode(cursor.getString(1));
			arrayList.add(info);
		}
		cursor.close();
		db.close();
		return arrayList;
	}
}
