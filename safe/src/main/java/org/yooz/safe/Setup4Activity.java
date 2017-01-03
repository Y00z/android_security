package org.yooz.safe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class Setup4Activity extends BaseSetupActivity {
	private SharedPreferences mPre;
	private CheckBox cb_protect;
	private TextView tv_protect;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		tv_protect = (TextView) findViewById(R.id.tv_protect);
		cb_protect = (CheckBox) findViewById(R.id.cb_protect);
		mPre = getSharedPreferences("config", MODE_PRIVATE);
		//进入页面的时候查看防盗是否开启的，
		boolean flag = mPre.getBoolean("flag", false);
		if(flag) {
			cb_protect.setChecked(true);
			tv_protect.setText("你已经开启防盗保护");
		} else {
			cb_protect.setChecked(false);
			tv_protect.setText("你还没有开启防盗保护");
		}
		
		cb_protect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked) {
					tv_protect.setText("你已经开启防盗保护");
					mPre.edit().putBoolean("flag", true).commit();
				} else {
					tv_protect.setText("你还没有开启防盗保护");
					mPre.edit().putBoolean("flag", false).commit();
				}
			}
		});
	}

	@Override
	public void showpreviousPage() {
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}

	@Override
	public void shownextPage() {
		mPre.edit().putBoolean("setting", true).commit();
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		finish();
	}
}
