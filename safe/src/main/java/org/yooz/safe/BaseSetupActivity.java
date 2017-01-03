package org.yooz.safe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

//设置引导页的父类,不需要注册，因为不需要界面展示
public abstract class BaseSetupActivity extends ActionBarActivity {
	private GestureDetector mGest;
	public SharedPreferences mPre;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGest = new GestureDetector(this, new SimpleOnGestureListener() {
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				
				if (Math.abs(velocityX) < 100) {
					Toast.makeText(BaseSetupActivity.this, "滑动太慢哦",
							Toast.LENGTH_SHORT).show();
					return true;
				}
				// 屏蔽纵向滑动
				if (Math.abs(e2.getRawY() - e1.getRawY()) > 200
						|| Math.asin(e1.getRawY() - e2.getRawY()) > 200) {
					Toast.makeText(BaseSetupActivity.this, "手势不对哦",
							Toast.LENGTH_SHORT).show();
					return true;
				}
				// 滑动上一页
				if (e2.getRawX() - e1.getRawX() > 200) {
					showpreviousPage();
					return true;
				}
				// 滑动下一页
				if (e1.getRawX() - e2.getRawX() > 200) {
					shownextPage();
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	// 展示上一页
	public abstract void showpreviousPage();

	// 展示下一页
	public abstract void shownextPage();

	public void next(View v) {
		shownextPage();
	}

	public void previous(View v) {
		showpreviousPage();
	}

	public boolean onTouchEvent(MotionEvent event) {
		mGest.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
