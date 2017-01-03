package org.yooz.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean flag = sp.getBoolean("flag", false);
		// 判断防盗是否开启，如果开启，就监听sim卡变化
		if (flag) {
			String sim = sp.getString("sim", "");
			// 判断sim序列化是否保存
			if (!TextUtils.isEmpty(sim)) {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				String newsim = tm.getSimSerialNumber();
				if (newsim.equals(sim)) {
					System.out.println("手机安全");
				} else {
					System.out.println("手机sim卡被更换");
				}
			}
		}
	}
}
