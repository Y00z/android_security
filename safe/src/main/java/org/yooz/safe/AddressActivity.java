package org.yooz.safe;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import org.yooz.dao.AddressDao;

public class AddressActivity extends ActionBarActivity {

	EditText et_number;
	TextView tv_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		et_number = (EditText) findViewById(R.id.et_number);
		tv_result = (TextView) findViewById(R.id.tv_result);

		// 监听editText变化
		et_number.addTextChangedListener(new TextWatcher() {
			// 文本发生变化
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String number = s.toString();
				if (!TextUtils.isEmpty(number)) {
					String address = AddressDao.getAddress(number);
					tv_result.setText(address);
				}
			}

			// 变化前
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			// 变化后
			public void afterTextChanged(Editable s) {
			}
		});
	}

	public void query(View v) {
		String number = et_number.getText().toString().trim();
		if (!TextUtils.isEmpty(number)) {
			String address = AddressDao.getAddress(number);
			tv_result.setText(address);
		} else {
			Animation shake = AnimationUtils.loadAnimation(this	, R.anim.shake);
			et_number.startAnimation(shake);
			vibrate();
		}
	}
	
	
	//手机震动
	public void vibrate() {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		//震动两秒
		vibrator.vibrate(2000);
		//先等待1秒 再震动2秒 再等待3秒， 再震动4秒，    
		//-1表示不循环， 0 表示循环
		vibrator.vibrate(new long [] {1000,2000,3000,4000,}, -1);
	}
}
