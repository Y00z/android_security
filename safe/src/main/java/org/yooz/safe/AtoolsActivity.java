package org.yooz.safe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import org.yooz.utils.SmsUtils;

public class AtoolsActivity extends Activity {
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activit_atools);
	}

	public void addressQuery(View v) {
		startActivity(new Intent(AtoolsActivity.this, AddressActivity.class));
	}

	private Handler handler = new Handler(){



		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			boolean flag = (Boolean) msg.obj;
			if(flag){
				pd.dismiss();
				Toast.makeText(AtoolsActivity.this,"备份成功",Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(AtoolsActivity.this,"备份失败",Toast.LENGTH_SHORT).show();
			}
		}
	};

	public void backupsms(View v) {
		pd = new ProgressDialog(AtoolsActivity.this);
		pd.setTitle("正在备份");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(){
			@Override
			public void run() {
				super.run();

				boolean flag = SmsUtils.backUp(AtoolsActivity.this, new SmsUtils.BackUpSmsInterface() {
					@Override
					public void befor(int count) {
						pd.setMax(count);
					}

					@Override
					public void onBackUpSms(int progress) {
						pd.setProgress(progress);
					}
				});
				Message msg = handler.obtainMessage();
				if(flag) {
					msg.obj = flag;
					handler.sendMessage(msg);
				}else {
					msg.obj = flag;
					handler.sendMessage(msg);
				}
			}
		}.start();

	}

	public void applock(View v) {
		startActivity(new Intent(this,AppLockActivity.class));
	}
}
