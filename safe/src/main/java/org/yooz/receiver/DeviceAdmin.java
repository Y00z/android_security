package org.yooz.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import org.yooz.safe.HomeActivity;

/**
 * 激活设备管理器，设置锁屏
 * @author Yooz
 *
 */
public class DeviceAdmin extends ActionBarActivity {
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	private SharedPreferences mPre;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);
		mPre = getSharedPreferences("config", MODE_PRIVATE);
		boolean device = mPre.getBoolean("device", false);
		activeAdmin();
		if (device) {
			startActivity(new Intent(this, HomeActivity.class));
		}
		mPre.edit().putBoolean("device", true).commit();
		System.out.println("aaa");
		finish();
	}

	// 激活设备管理器
	public void activeAdmin() {
		System.out.println("jihuo");
		Toast.makeText(this, "请先激活程序", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"哈哈哈, 我们有了超级设备管理器, 好NB!");
		startActivity(intent);

	}

	// 锁屏
	public void lock() {
		// 判断是不是已经激活了设备管理器
		mDPM.resetPassword("013579", 0);
		mDPM.lockNow();
	}

	// 数据清除
	public void clearData() {
		if (mDPM.isAdminActive(mDeviceAdminSample)) {
			mDPM.wipeData(0);
		} else {
			Toast.makeText(this, "请先激活", Toast.LENGTH_SHORT).show();
		}
	}

	// 卸载
	public void unInstall() {
		// 取消激活
		mDPM.removeActiveAdmin(mDeviceAdminSample);

		// 卸载程序
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT); // 输入要卸载程序的包名
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivity(intent);

	}
}
