package org.yooz.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import org.yooz.dao.AddressDao;
import org.yooz.safe.R;

/*
 * 来电显示归属地
 */
public class AddressService extends Service {

	private TelephonyManager tm;
	private Mylisten listener;
	private OutCallReceiver receiver;
	private WindowManager mWM;
	private View view;
	private int startX;
	private int startY;
	private SharedPreferences mPre;
	private WindowManager.LayoutParams params;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		listener = new Mylisten();

		mPre = getSharedPreferences("config", MODE_PRIVATE);

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		receiver = new OutCallReceiver();

		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);
	}

	class Mylisten extends PhoneStateListener {

		// 来显显示归属地
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("电话铃响了");
				String address = AddressDao.getAddress(incomingNumber);
				// Toast.makeText(AddressService.this, address,
				// Toast.LENGTH_LONG)
				// .show();
				showToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:// 电话空闲状态
				clearToast();
			default:
				break;
			}
		}
	}

	// 去电显示归属地 动态注册广播 拨打电话
	class OutCallReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			String number = getResultData();
			String address = AddressDao.getAddress(number);
			// Toast.makeText(context, address, Toast.LENGTH_LONG).show();
			showToast(address);
		}
	}

	// 服务销毁的时候 取消监听，和取消广播绑定
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(receiver);
	}

	// 清除浮窗
	public void clearToast() {
		if (mWM != null && view != null) {
			mWM.removeView(view);
			view = null;
		}
	}

	// 自定义浮窗
	public void showToast(String address) {
		mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		final int winWidth = mWM.getDefaultDisplay().getWidth();
		final int winHeight = mWM.getDefaultDisplay().getHeight();

		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		// params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.setTitle("Toast");
		// 将重心位置设置左上方，也就是(0，0从左上方开始，而不是默认的中心位置)
		params.gravity = Gravity.LEFT + Gravity.TOP;
		// view = new TextView(this);
		// view.setTextColor(Color.RED);
		// view.setText(text);

		int lastX = mPre.getInt("lastX", 0);
		int lastY = mPre.getInt("lastY", 0);
		// 设置浮床的位置。
		params.x = lastX;
		params.y = lastY;

		int style = mPre.getInt("address_style", 0);
		System.out.println("style:" + style);
		int[] bgs = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };

		view = View.inflate(this, R.layout.toast_address, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_number);
		view.setBackgroundResource(bgs[style]);
		tv.setText(address);
		// 把组件显示在窗口上
		mWM.addView(view, params);

		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// 计算偏移量
					int dx = endX - startX;
					int dy = endY - startY;
					
					params.x += dx ; 
					params.y += dy;
					
					mWM.updateViewLayout(view, params);
					// 重新初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					//防止通话时归属地浮窗坐标偏移屏幕
					if(params.x < 0 ) {
						params.x = 0 ;
					}
					if(params.y <0) {
						params.y = 0;
					}
					if(params.x > winWidth - view.getWidth()) {
						params.x = winWidth - view.getWidth();
					}
					if(params.y > winHeight - view.getHeight()) {
						params.y =  winHeight - view.getHeight();
					}
					break;
				case MotionEvent.ACTION_UP:
					// 保存归属地坐标
					Editor edit = mPre.edit();
					edit.putInt("lastX", params.x);
					edit.putInt("lastY", params.y);
					edit.commit();
					break;

				default:
					break;
				}
				return true;
			}
		});
	}
}
