package org.yooz.safe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_phone;
	private SharedPreferences mPre;
	private String phone;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);

		et_phone = (EditText) findViewById(R.id.et_phone);

		mPre = getSharedPreferences("config", MODE_PRIVATE);

		phone = mPre.getString("phone", "");
		et_phone.setText(phone);
	}

	// 跳转到联系人
	public void selectContact(View v) {
		Intent intent = new Intent(this, ContactListActivity.class);
		startActivityForResult(intent, 1);
	}

	// 获取用户选择联系人的号码
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			phone = data.getStringExtra("phone");
			et_phone.setText(phone);
		}
	}

	@Override
	public void showpreviousPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}

	@Override
	public void shownextPage() {
		startActivity(new Intent(this, Setup4Activity.class));
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		// 用户下一步的时候保存安全号码
		phone = et_phone.getText().toString();
		mPre.edit().putString("phone", phone).commit();
		finish();
	}
}
