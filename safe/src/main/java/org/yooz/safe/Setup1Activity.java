package org.yooz.safe;

import android.content.Intent;
import android.os.Bundle;

public class Setup1Activity extends BaseSetupActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	
	//展示上一页
	public void showpreviousPage() {
		
	}
	//展示下一页
	public void shownextPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		finish();
	}
}
