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
public class LockFragment extends Fragment {

    private ListView list_view;
    private TextView tv_lock;
    private LockAdapter lockAdapter;
    private ArrayList<AppInfo> lockLists;
    private AppLockDao appLockDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock, null);
        list_view = (ListView) view.findViewById(R.id.list_view);
        tv_lock = (TextView) view.findViewById(R.id.tv_lock);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        List<AppInfo> appInfos = AppInfos.getAppInfos(getActivity());
        appLockDao = new AppLockDao(getActivity());
        lockLists = new ArrayList<AppInfo>();
        for(AppInfo appInfo : appInfos) {
            if(appLockDao.find(appInfo.getApkPackage())) {
                lockLists.add(appInfo);
            }
        }

        lockAdapter = new LockAdapter();
        list_view.setAdapter(lockAdapter);
    }

    class LockAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return lockLists.size();
        }

        @Override
        public Object getItem(int position) {
            return lockLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view ;
            ViewHolder holder;
            if(convertView == null){
                view = View.inflate(getActivity(),R.layout.item_fragment_lock,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.iv_unlock = (ImageView) view.findViewById(R.id.iv_unlock);
                view.setTag(holder);
            } else {
                view =  convertView;
                holder = (ViewHolder) view.getTag();
            }
            tv_lock.setText("已加锁(" + lockLists.size() + ")个");

            final AppInfo appInfo = lockLists.get(position);
            holder.iv_icon.setImageDrawable(lockLists.get(position).getIcon());
            holder.tv_name.setText(lockLists.get(position).getApkName());
            holder.iv_unlock.setImageResource(R.drawable.list_button_unlock_default);
            holder.iv_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //动画效果
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    translateAnimation.setDuration(500);
                    view.startAnimation(translateAnimation);

                    new Thread() {
                        public void run() {
                            SystemClock.sleep(500);
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    appLockDao.delete(appInfo.getApkPackage());
                                    lockLists.remove(position);
                                    lockAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }.start();
                    lockAdapter.notifyDataSetChanged();
                }
            });
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_unlock;
    }
}
