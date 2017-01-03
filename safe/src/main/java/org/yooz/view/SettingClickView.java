package org.yooz.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.yooz.safe.R;


/*
 *设置自定义组合控件
 *（设置页面中  右边有箭头的选线） 
 */

public class SettingClickView extends RelativeLayout {

	private TextView tv_title;
	private TextView tv_desc;

	public SettingClickView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context);
		initView();
	}

	public SettingClickView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	// 自动调用这个构造方法。
	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public SettingClickView(Context context) {
		super(context);
		initView();
	}

	// 初始化布局
	public void initView() {
		// 把指定的布局文件，设置给当前本布局文件对象,
		// 也就是本布局文件文件现在是下面的布局文件的父类
		View.inflate(getContext(), R.layout.view_setting_click, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
	}

	// 设置标题
	public void setTitle(String title) {
		tv_title.setText(title);
	}

	public void setDesc(String desc) {
		tv_desc.setText(desc);
	}

}
