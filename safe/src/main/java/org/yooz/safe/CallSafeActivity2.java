package org.yooz.safe;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.yooz.bean.BlackNumberInfo;
import org.yooz.dao.BlackNumberDao;

import java.util.List;

public class CallSafeActivity2 extends ActionBarActivity {
	private ListView lvBlack;
	private CallSafeAdapter adapter;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_safe);
		initUI();
		initData();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			adapter = new CallSafeAdapter();
			lvBlack.setAdapter(adapter);
		};
	};

	// 初始化数据
	private void initData() {

		new Thread() {
			public void run() {
				dao = new BlackNumberDao(CallSafeActivity2.this);
				infos = dao.findAll();
				handler.sendEmptyMessage(0);
			};
		}.start();

	}

	// 初始化UI
	private void initUI() {
		lvBlack = (ListView) findViewById(R.id.lv_black);
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
				convertView = View.inflate(CallSafeActivity2.this,
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
					//从数据库中删除
					boolean delete = dao.delete(number);
					if(delete) {
						Toast.makeText(CallSafeActivity2.this, "删除成功", Toast.LENGTH_SHORT).show();
						//从集合中删除
						infos.remove(info);
						//更新数据
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(CallSafeActivity2.this, "删除失败", Toast.LENGTH_SHORT).show();
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
