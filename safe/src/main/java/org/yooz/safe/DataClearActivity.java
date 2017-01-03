package org.yooz.safe;

import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DataClearActivity extends ActionBarActivity {

    private ListView list_view;
    PackageManager packageManager;
    private List<CacheInfo> cacheInfos;
    private DataClearAdapter adapter;
    private LinearLayout ll_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private void initData() {

    }

    private void initUI() {
        setContentView(R.layout.activity_data_clear);
        list_view = (ListView) findViewById(R.id.list_view);
        ll_progress = (LinearLayout) findViewById(R.id.ll_progress);
        packageManager = getPackageManager();
        cacheInfos = new ArrayList<CacheInfo>();

        new Thread() {
            @Override
            public void run() {
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                for (PackageInfo packageInfo : installedPackages) {
                    getCacheSize(packageInfo);
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter = new DataClearAdapter();
            list_view.setAdapter(adapter);
        }
    };

    private class DataClearAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            System.out.println(cacheInfos.size());
            return cacheInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return cacheInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                view = View.inflate(DataClearActivity.this, R.layout.item_data_clear, null);
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_cache = (TextView) view.findViewById(R.id.tv_cache);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            holder.iv_icon.setImageDrawable(cacheInfos.get(position).icon);
            holder.tv_cache.setText("缓存大小" + Formatter.formatFileSize(DataClearActivity.this, cacheInfos.get(position).cachesize));
            holder.tv_name.setText(cacheInfos.get(position).appName);
            ll_progress.setVisibility(View.GONE);
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_cache;
    }


    private void getCacheSize(PackageInfo packageInfo) {
        try {
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method.invoke(packageManager, packageInfo.applicationInfo.packageName, new MyIPackageStatsObserver(packageInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {

        private PackageInfo packageInfo;

        public MyIPackageStatsObserver(PackageInfo packageInfo) {
            this.packageInfo = packageInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long cacheSize = pStats.cacheSize;
            if (cacheSize > 0) {
                System.out.println(packageInfo.applicationInfo.loadLabel(packageManager) + "的缓存是" + Formatter.formatFileSize(DataClearActivity.this, cacheSize));
                Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                CacheInfo cacheInfo = new CacheInfo();
                cacheInfo.icon = drawable;
                cacheInfo.cachesize = cacheSize;
                cacheInfo.appName = appName;
                cacheInfos.add(cacheInfo);
            }
        }
    }


    static class CacheInfo {
        Drawable icon;
        long cachesize;
        String appName;
    }


    public void clearAll(View v) {
        Method[] methods = PackageManager.class.getMethods();
        for(Method method : methods) {
            if(method.getName().equals("freeStorageAndNotify")) {
                try {
                    method.invoke(packageManager,Integer.MAX_VALUE,new MyIPackageDataObserver());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Toast.makeText(DataClearActivity.this,"全部清理完毕",Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    private class MyIPackageDataObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }

}
