package org.yooz.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import org.yooz.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class CallSafeService extends Service {

	private BlackNumberDao dao;
	private TelephonyManager tm;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dao = new BlackNumberDao(this);
		// 监听电话状态
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		MyPhoneStateListener listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 广播监听短信
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		InnerReceiver receiver = new InnerReceiver();
		registerReceiver(receiver, filter);
	}

	//监听电话状态
	class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String mode = dao.findNumber(incomingNumber);
				if (mode.equals("1") || mode.equals("3")) {
					Uri uri = Uri.parse("content://call_log/calls");

					getContentResolver()
							.registerContentObserver(
									uri,
									true,
									new MyContentObserver(new Handler(),
											incomingNumber));

					endCall();
				}
				break;
			}
		}
	}

	//挂断电话
	private void endCall() {

		try {
			// 通过类加载器加载ServiceManager
			Class<?> clazz = getClassLoader().loadClass(
					"android.os.ServiceManager");
			// 通过反射得到当前的方法
			Method method = clazz.getDeclaredMethod("getService", String.class);

			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

			ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);

			iTelephony.endCall();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class MyContentObserver extends ContentObserver {
		String incomingNumber;

		public MyContentObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}
	}

	class InnerReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			Object objects[] = (Object[]) bundle.get("pdus");
			for (Object obj : objects) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
				String address = sms.getOriginatingAddress();
				String body = sms.getMessageBody();
				String mode = dao.findNumber(address);
				if (mode.equals("1") || mode.equals("2")) {
					abortBroadcast();
				}
			}
		}
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
