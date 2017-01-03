package org.yooz.safe;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DragViewActivity extends ActionBarActivity {

	private TextView tvTop;
	private TextView tvBottom;
	private ImageView iv_drag;
	private int startX;
	private int startY;
	private SharedPreferences mPre;
	long[] mHits = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);

		mPre = getSharedPreferences("config", MODE_PRIVATE);

		tvTop = (TextView) findViewById(R.id.tvTop);
		tvBottom = (TextView) findViewById(R.id.tvBottom);
		iv_drag = (ImageView) findViewById(R.id.iv_drag);

		final int winWidth = getWindowManager().getDefaultDisplay().getWidth();
		final int winHeight = getWindowManager().getDefaultDisplay()
				.getHeight();

		int lastX = mPre.getInt("lastX", 0);
		int lastY = mPre.getInt("lastY", 0);

		// 如果归属地比屏幕一般的要高，就因隐身上面，显示下面
		if (lastY > winHeight / 2) {
			tvTop.setVisibility(View.VISIBLE);
			tvBottom.setVisibility(View.INVISIBLE);
		} else {
			tvTop.setVisibility(View.INVISIBLE);
			tvBottom.setVisibility(View.VISIBLE);
		}

		// onMeasure(测量view), onLayout(安放位置), onDraw(绘制)
		// iv_drag.layout(lastX, lastY, lastX + iv_drag.getWidth(), lastY
		// + iv_drag.getHeight());//不能用这个方法,因为还没有测量完成,就不能安放位置

		// 获取布局对象
		RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) iv_drag
				.getLayoutParams();
		layoutParams.leftMargin = lastX;// 设置布局左边距
		layoutParams.topMargin = lastY;// 设置布局顶边距

		iv_drag.setLayoutParams(layoutParams);// 重新设置位置

		iv_drag.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// 计算偏移量
					int dx = endX - startX;
					int dy = endY - startY;

					// 更新左上右下距离
					int l = iv_drag.getLeft() + dx;
					int r = iv_drag.getRight() + dx;
					int t = iv_drag.getTop() + dy;
					int b = iv_drag.getBottom() + dy;

					// 判断归属地显示是否超出屏幕
					if (l < 0 || t < 0 || r > winWidth || b > winHeight - 20) {
						break;
					}

					// 如果归属地比屏幕一般的要高，就因隐身上面，显示下面
					if (t > winHeight / 2) {
						tvTop.setVisibility(View.VISIBLE);
						tvBottom.setVisibility(View.INVISIBLE);
					} else {
						tvTop.setVisibility(View.INVISIBLE);
						tvBottom.setVisibility(View.VISIBLE);
					}

					// 更新界面
					iv_drag.layout(l, t, r, b);

					// 重新初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					// 保存归属地坐标
					Editor edit = mPre.edit();
					edit.putInt("lastX", iv_drag.getLeft());
					edit.putInt("lastY", iv_drag.getTop());
					edit.commit();

					break;
				default:
					break;
				}
				return false; //让事件能够往下传递
			}
		});

		//双击事件
		iv_drag.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				System.out.println("单击了");
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后开始计算的时间
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					System.out.println("双击了");
					iv_drag.layout(winWidth / 2 - iv_drag.getWidth()/2,
							iv_drag.getTop(),
							winWidth / 2 + iv_drag.getWidth()/2,
							iv_drag.getBottom());
				}
			}
		});
	}
}
