package org.yooz.safe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;

import org.yooz.service.AddressService;
import org.yooz.service.CallSafeService;
import org.yooz.service.WatchDagService;
import org.yooz.utils.ServiceStatusUtils;
import org.yooz.view.SettingClickView;
import org.yooz.view.SettingItemView;

public class SettingActivity extends ActionBarActivity {

	private SettingItemView sivUpdate;
	private SettingItemView sivAddress;
	private SettingItemView sivCall;
	private SettingClickView scvAddressStyle;
	private SettingClickView scvAddressLocation;
	private SettingItemView watch_dog;
	private SharedPreferences mPre;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acvitivy_setting);
		mPre = getSharedPreferences("config", MODE_PRIVATE);

		initUpdateView();
		initAddressView();
		initAddressStyle();
		initAddressLocation();
		initBlackCall();
		initWatchDog();
	}


	private void initBlackCall() {
		sivCall = (SettingItemView) findViewById(R.id.set_call_safe);

		// 根据服务开启状态，来更新checkbox
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"org.yooz.service.CallSafeService");
		if (serviceRunning) {
			sivCall.setChecked(true);
		} else {
			sivCall.setChecked(false);
		}

		sivCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivCall.isChecked()) {
					sivCall.setChecked(false);
					stopService(new Intent(SettingActivity.this,
							CallSafeService.class));
				} else {
					sivCall.setChecked(true);
					startService(new Intent(SettingActivity.this,
							CallSafeService.class));
				}
			}
		});
	}

	//看门狗
	private void initWatchDog() {
		watch_dog = (SettingItemView) findViewById(R.id.watch_dog);

		// 根据服务开启状态，来更新checkbox
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"org.yooz.service.WatchDagService");
		if (serviceRunning) {
			watch_dog.setChecked(true);
		} else {
			watch_dog.setChecked(false);
		}

		watch_dog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (watch_dog.isChecked()) {
					watch_dog.setChecked(false);
					stopService(new Intent(SettingActivity.this,
							WatchDagService.class));
				} else {
					watch_dog.setChecked(true);
					startService(new Intent(SettingActivity.this,
							WatchDagService.class));
				}
			}
		});
	}

	// 初始化自动更新设置
	public void initUpdateView() {
		sivUpdate = (SettingItemView) findViewById(R.id.set_view);
		sivUpdate.setTitle("自动更新设置");
		boolean flag = mPre.getBoolean("auto_update", true);
		if (flag) {
			// sivUpdate.setDesc("自动更新已经开启");
			sivUpdate.setChecked(true);
		} else {
			// sivUpdate.setDesc("自动更新已经关闭");
			sivUpdate.setChecked(false);
		}

		sivUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sivUpdate.isChecked()) {
					sivUpdate.setChecked(false);
					// sivUpdate.setDesc("自动更新已经关闭");
					// 更新SharedPreferences
					mPre.edit().putBoolean("auto_update", false).commit();
				} else {
					sivUpdate.setChecked(true);
					// sivUpdate.setDesc("自动更新已经开启");
					mPre.edit().putBoolean("auto_update", true).commit();
				}
			}
		});
	}

	// 初始化归属地
	public void initAddressView() {
		sivAddress = (SettingItemView) findViewById(R.id.set_address);

		// 根据服务开启状态，来更新checkbox
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"org.yooz.service.AddressService");
		if (serviceRunning) {
			sivAddress.setChecked(true);
		} else {
			sivAddress.setChecked(false);
		}

		sivAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivAddress.isChecked()) {
					sivAddress.setChecked(false);
					stopService(new Intent(SettingActivity.this,
							AddressService.class));
				} else {
					sivAddress.setChecked(true);
					startService(new Intent(SettingActivity.this,
							AddressService.class));
				}
			}
		});
	}
	
	
	
	String [] items = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
	
	/*
	 * 初始化归属地提示框风格
	 */
	public void initAddressStyle() {
		scvAddressStyle = (SettingClickView) findViewById(R.id.scv_style);
		int style = mPre.getInt("address_style", 0);
		
		scvAddressStyle.setTitle("归属地提示框风格");
		scvAddressStyle.setDesc(items[style]);

		scvAddressStyle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ShowDialog();
			}
		});
	}
	
	
	//选择风格单选框
	public void ShowDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("归属地提示框风格");
		
		int style = mPre.getInt("address_style", 0);
		
		
		builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPre.edit().putInt("address_style", which).commit();
				dialog.dismiss();
				scvAddressStyle.setDesc(items[which]);
			}
		});
		
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	
	//初始化归属地提示框移动位置
	public void initAddressLocation() {
		scvAddressLocation = (SettingClickView) findViewById(R.id.scv_style_location);
		scvAddressLocation.setTitle("归属地提示框显示位置");
		scvAddressLocation.setDesc("设置归属地提示框显示位置");
		
		scvAddressLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this,DragViewActivity.class));
			}
		});
	}
}
