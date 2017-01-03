package org.yooz.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.yooz.bean.AppInfo;
import org.yooz.dao.AppLockDao;
import org.yooz.safe.R;
import org.yooz.utils.AppInfos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yooz on 2016/2/14.
 */
public class UnLockFragment extends Fragment {

    private View view;
    private List<AppInfo> appInfos;
    private ListView list_view;
    private UnLockAdapter unLockAdapter;
    private TextView tv_unlock;
    private AppLockDao appLockDao;
    private ArrayList<AppInfo> unLockLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_unlock, null);
        list_view = (ListView) view.findViewById(R.id.list_view);
        tv_unlock = (TextView) view.findViewById(R.id.tv_unlock1);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appInfos = AppInfos.getAppInfos(getActivity());
        //初始化一个没有加锁的程序集合,里面存放没有加锁的程序
        unLockLists = new ArrayList<AppInfo>();
        appLockDao = new AppLockDao(getActivity());
        //判断引用是否在程序里面
        for (AppInfo appinfo : this.appInfos) {
            if (appLockDao.find(appinfo.getApkPackage())) {

            } else {
                unLockLists.add(appinfo);
            }
        }


        unLockAdapter = new UnLockAdapter();
        list_view.setAdapter(unLockAdapter);
    }

    public class UnLockAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return unLockLists.size();
        }

        @Override
        public Object getItem(int position) {
            return unLockLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final View view;
            if (convertView == null) {
                view = View.inflate(getActivity(), R.layout.item_fragment_unlock, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.iv_lock = (ImageView) view.findViewById(R.id.iv_lock);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            tv_unlock.setText("未加锁(" + unLockLists.size() + ")个");
            //获取当前点击的条目对象
            final AppInfo appInfo = unLockLists.get(position);
            holder.iv_icon.setImageDrawable(unLockLists.get(position).getIcon());
            holder.tv_name.setText(unLockLists.get(position).getApkName());
            holder.iv_lock.setImageResource(R.drawable.list_button_lock_default);

            //把程序添加到程序锁
            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //动画效果
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    translateAnimation.setDuration(500);
                    view.startAnimation(translateAnimation);

                    new Thread() {
                        public void run() {
                            SystemClock.sleep(500);
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    appLockDao.add(appInfo.getApkPackage());
                                    unLockLists.remove(position);
                                    unLockAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }.start();

                }
            });

            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }
}
