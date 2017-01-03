package org.yooz.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	// 该路径必须是data/data目录，否则数据库访问不到
	private static final String path = "data/data/org.yooz.safe/files/address.db";

	public static String getAddress(String number) {
		String address = "未知号码";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		String sql = "select location from data2 where id=(select outkey from data1 where id = ?)";

		// 匹配手机号码
		if (number.matches("^1[3-8]\\d{9}$")) {
			Cursor cursor = db.rawQuery(sql,
					new String[] { number.substring(0, 7) });
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			} // 匹配长途电话
		} else if (number.startsWith("0") && number.length() > 10) {
			// 有些区号是4位,有些区号是3位(包括0)

			// 先查询4位区号
			Cursor cursor = db.rawQuery(
					"select location from data2 where area =?",
					new String[] { number.substring(1, 4) });

			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			} else {
				cursor.close();

				// 查询3位区号
				cursor = db.rawQuery(
						"select location from data2 where area =?",
						new String[] { number.substring(1, 3) });

				if (cursor.moveToNext()) {
					address = cursor.getString(0);
				}

				cursor.close();
			}
		}

		return address;
	}
	
}
