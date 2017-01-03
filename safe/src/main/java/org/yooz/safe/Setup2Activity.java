package org.yooz.safe;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import org.yooz.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView siv;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		siv = (SettingItemView) findViewById(R.id.siv);
		mPre = getSharedPreferences("config", MODE_PRIVATE);
		
		//查看sim是否绑定，如果绑定就默认勾选状态
		String sim = mPre.getString("sim", "");
		if(!TextUtils.isEmpty(sim)) {
			siv.setChecked(true);
		} else {
			siv.setChecked(false);
		}
		
		//绑定sim卡勾选框
		siv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (siv.isChecked()) {
					siv.setChecked(false);
					mPre.edit().putString("sim", "").commit();
				} else {
					siv.setChecked(true);
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = tm.getSimSerialNumber();
					System.out.println(simSerialNumber);
					mPre.edit().putString("sim", simSerialNumber).commit();
				}
			}
		});
	}

	@Override
	public void showpreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}

	@Override
	public void shownextPage() {
		startActivity(new Intent(this, Setup3Activity.class));
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		finish();
	}

}
