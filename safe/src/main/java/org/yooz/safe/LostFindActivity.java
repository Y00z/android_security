package org.yooz.safe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends ActionBarActivity {
	private SharedPreferences mPre;
	private SharedPreferences mPref;
	private ImageView iv_lock;
	private TextView tv_phone;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//判断是否设置过防盗功能，如果没有就自动跳转到防盗设置
		mPre = getSharedPreferences("config", MODE_PRIVATE);
		boolean setting = mPre.getBoolean("setting", false);
		if (setting) {
			setContentView(R.layout.activity_lost_find);
			
			//更新锁图片
			iv_lock = (ImageView) findViewById(R.id.iv_safelock);
			mPref = getSharedPreferences("config", MODE_PRIVATE);
			boolean flag = mPref.getBoolean("flag", false);
			if(flag) {
				iv_lock.setImageResource(R.drawable.lock);
			} else {
				iv_lock.setImageResource(R.drawable.unlock);
			}
			
			//更新安全号码
			tv_phone = (TextView) findViewById(R.id.tv_safephone);
			mPref = getSharedPreferences("config", MODE_PRIVATE);
			String phone = mPref.getString("phone", "");
			if(!TextUtils.isEmpty(phone)) {
				tv_phone.setText(phone);
			}
		} else {
			startActivity(new Intent(this, Setup1Activity.class));
			finish();
		}
	}
	
	public void reset(View v) {
		startActivity(new Intent(this,Setup1Activity.class));
	}
}
