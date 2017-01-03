package org.yooz.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.yooz.safe.R;

//设置自定义组合控件

public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/org.yooz.safe";
	private TextView tv_title;
	private TextView tv_desc;
	private CheckBox cb_status;
	private String mTitle;
	private String mDesc_on;
	private String mDesc_off;

	public SettingItemView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context);
		initView();
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	// 自动调用这个构造方法。
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();

		int count = attrs.getAttributeCount();

		for (int i = 0; i < count; i++) {
			String attributeName = attrs.getAttributeName(i);
			String attributeValue = attrs.getAttributeValue(i);
			System.out.println(attributeName + ":" + attributeValue);
		}
		
		//获取到自定义的textView
		mTitle = attrs.getAttributeValue(NAMESPACE, "Title");
		mDesc_on = attrs.getAttributeValue(NAMESPACE, "desc_on");
		mDesc_off = attrs.getAttributeValue(NAMESPACE, "desc_off");

		setTitle(mTitle);
	}

	public SettingItemView(Context context) {
		super(context);
		initView();
	}

	// 初始化布局
	public void initView() {
		// 把指定的布局文件，设置给当前本布局文件对象,
		// 也就是本布局文件文件现在是下面的布局文件的父类
		View.inflate(getContext(), R.layout.view_setting_item, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		cb_status = (CheckBox) findViewById(R.id.cb_status);

	}

	// 设置标题
	public void setTitle(String title) {
		tv_title.setText(title);
	}

	public void setDesc(String desc) {
		tv_desc.setText(desc);
	}

	// 获取复选框状态
	public boolean isChecked() {
		return cb_status.isChecked();
	}

	// 设置复选框
	public void setChecked(boolean check) {
		cb_status.setChecked(check);
		
		//根据选择的复选框更新状态。
		if (check) {
			setDesc(mDesc_on);
		} else {
			setDesc(mDesc_off);
		}
	}

}
