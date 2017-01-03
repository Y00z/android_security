package org.yooz.safe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.yooz.bean.BlackNumberInfo;
import org.yooz.dao.BlackNumberDao;

import java.util.List;

public class CallSafeActivity extends ActionBarActivity {
	private ListView lvBlack;
	private CallSafeAdapter adapter;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao dao;
	// private TextView tvPage;
	// private int mCurrentPage = 0;
	// private int mPageSize = 20;
//	private int totalPage;

	private int mStartIndex = 0;
	private int maxCount = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_call_safe);
		setContentView(R.layout.activity_call_safe2);
		initUI();
		initData();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(adapter == null) {
				adapter = new CallSafeAdapter();
				lvBlack.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
			// tvPage.setText((mCurrentPage +1) +"/"+totalPage);
		};
	};
	private int total;

	// private EditText etPage;

	// 初始化数据
	private void initData() {
		dao = new BlackNumberDao(CallSafeActivity.this);
		total = dao.getTotal();
		new Thread() {
			public void run() {
				// totalPage = dao.getTotal() / mPageSize;
				// 一次查询出所有数据
				// infos = dao.findAll();
				// 分页查询
				// infos = dao.findPage(mCurrentPage, mPageSize);
				// 分批加载
				if (infos == null) {
					// 如果是空，就取数据
					infos = dao.findPartial(mStartIndex, maxCount);
				} else {
					// 如果不是空，那么就追加数据
					infos.addAll(dao.findPartial(mStartIndex, maxCount));
				}

				handler.sendEmptyMessage(0);
			};
		}.start();

	}

	/*
	 * public void pre(View v) { if(mCurrentPage <= 0) { Toast.makeText(this,
	 * "已经是第一页了", Toast.LENGTH_SHORT).show(); return; } mCurrentPage--;
	 * initData(); }
	 * 
	 * public void next(View v) { if(mCurrentPage >= totalPage-1) {
	 * Toast.makeText(this, "已经最后一页了", Toast.LENGTH_SHORT).show(); return ; }
	 * 
	 * mCurrentPage++; initData(); }
	 * 
	 * public void jump(View v) { String number =
	 * etPage.getText().toString().trim(); if(TextUtils.isEmpty(number)) {
	 * Toast.makeText(this, "请输入页码", Toast.LENGTH_SHORT).show(); return; } else
	 * { int parseInt = Integer.parseInt(number); if(parseInt >= 0 && parseInt
	 * <= (totalPage-1)) { mCurrentPage = parseInt - 1; initData(); } else {
	 * Toast.makeText(this, "请输入正确页面", Toast.LENGTH_SHORT).show(); } } }
	 */

	// 初始化UI
	private void initUI() {
		lvBlack = (ListView) findViewById(R.id.lv_black);
		// tvPage = (TextView) findViewById(R.id.tv_page);
		// etPage = (EditText) findViewById(R.id.et_page);
		// 设置listview的滚动监听
		// OnScrollListener.SCROLL_STATE_FLING ：惯性
		// OnScrollListener.SCROLL_STATE_IDLE :闲置状态
		// OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ：手指触屏的时候状态
		lvBlack.setOnScrollListener(new OnScrollListener() {
			// 开始滚动和停止滚动时调用
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 获取到listView最后一条数据
					int lastVisiblePosition = lvBlack.getLastVisiblePosition();
					mStartIndex += maxCount;
					// 判断最后一页是不是最后面的数据
					if (lastVisiblePosition >= total) {
						Toast.makeText(CallSafeActivity.this, "已经没有数据了",
								Toast.LENGTH_LONG).show();
						return;
					}
					initData();
					break;
				}
			}

			// 滚动时调用
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	// 添加黑名单
	public void add(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_call_safe, null);
		final EditText etNumber = (EditText) view
				.findViewById(R.id.et_safe_number);

		Button btOk = (Button) view.findViewById(R.id.bt_ok);
		Button btCancel = (Button) view.findViewById(R.id.bt_cancel);

		final CheckBox cbPhone = (CheckBox) view.findViewById(R.id.cb_phone);
		final CheckBox cbSms = (CheckBox) view.findViewById(R.id.cb_sms);

		btCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String number = etNumber.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(CallSafeActivity.this, "请输入号码",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String mode = "";
				if (cbPhone.isChecked() && cbSms.isChecked()) {
					mode = "1";
				} else if (cbPhone.isChecked()) {
					mode = "3";
				} else if (cbSms.isChecked()) {
					mode = "2";
				} else {
					Toast.makeText(CallSafeActivity.this, "请勾选拦截模式",
							Toast.LENGTH_SHORT).show();
					return;
				}
				//数据添加到集合
				BlackNumberInfo info = new BlackNumberInfo();
				info.setNumber(number);
				info.setMode(mode);
				infos.add(0, info);
				//数据添加到数据库
				dao.add(number, mode);
				
				dialog.dismiss();
				//添加数据后刷新界面
				if(adapter == null) {
					adapter = new CallSafeAdapter();
					lvBlack.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
			}
		});
		dialog.setView(view);
		dialog.show();
	}

	class CallSafeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(CallSafeActivity.this,
						R.layout.item_call_safe, null);
				holder = new ViewHolder();
				holder.tvNumber = (TextView) convertView
						.findViewById(R.id.tv_number);
				holder.tvMode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				holder.ivDelete = (ImageView) convertView
						.findViewById(R.id.iv_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tvNumber.setText(infos.get(position).getNumber());
			String type = infos.get(position).getMode();
			if (type.equals("1")) {
				holder.tvMode.setText("拦截短信和电话");
			} else if (type.equals("2")) {
				holder.tvMode.setText("拦截短信");
			} else if (type.equals("3")) {
				holder.tvMode.setText("拦截电话");
			}
			final BlackNumberInfo info = infos.get(position);
			holder.ivDelete.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String number = info.getNumber();
					// 从数据库中删除
					boolean delete = dao.delete(number);
					if (delete) {
						Toast.makeText(CallSafeActivity.this, "删除成功",
								Toast.LENGTH_SHORT).show();
						// 从集合中删除
						infos.remove(info);
						// 更新数据
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(CallSafeActivity.this, "删除失败",
								Toast.LENGTH_SHORT).show();
					}
				}
			});

			return convertView;
		}
	}

	static class ViewHolder {
		TextView tvMode;
		TextView tvNumber;
		ImageView ivDelete;
	}
}
