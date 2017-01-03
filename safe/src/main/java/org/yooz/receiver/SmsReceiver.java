package org.yooz.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import org.yooz.safe.R;

public class SmsReceiver extends BroadcastReceiver {

	private SharedPreferences mPre;

	@Override
	public void onReceive(Context context, Intent intent) {
		//获取设备管理器
		DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		
		mPre = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		Bundle bundle = intent.getExtras();
		Object objects [] = (Object[]) bundle.get("pdus");
		for(Object obj : objects) {
			SmsMessage sms = SmsMessage.createFromPdu( (byte[]) obj);
			String address = sms.getOriginatingAddress();
			String body = sms.getMessageBody();
			//接收到该短信
			if("#*alarm*#".equals(body)){
				MediaPlayer play = MediaPlayer.create(context, R.raw.lovebgm);
				play.setVolume(1f, 1f);
				play.setLooping(true);
				play.start();
				abortBroadcast();
				//接收到该短信，就获取位置信息，并且发送给发送短信者
			} else if ("#*location*#".equals(body)) {
				String location = mPre.getString("location", "noting location");
				SmsManager sm = SmsManager.getDefault();
				sm.sendTextMessage(address, null, location, null, null);
				System.out.println(location);
				abortBroadcast();
				//接收到短信，锁屏
			} else if ("#*lockscreen*#".equals(body)) {
				dpm.resetPassword("1234", 0);
				abortBroadcast();
				//接收到短信出厂化
			} else if ("#*wipedata*#".equals(body)) {
				dpm.wipeData(0);
				abortBroadcast();
			}
		}
	}
}
