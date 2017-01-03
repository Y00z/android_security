package org.yooz.safe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.yooz.bean.AppInfo;
import org.yooz.utils.AppInfos;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements View.OnClickListener {

    private TextView tv_rom;
    private TextView tv_sd;
    private TextView tv_app;
    private ListView list_view;
    private List<AppInfo> appInfos;
    private ArrayList<AppInfo> systemAppInfo;
    private ArrayList<AppInfo> userAppInfo;
    private PopupWindow popupWindow;
    private AppInfo clickAppInfo;
    private LinearLayout ll_progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initDada();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_run:
                Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(clickAppInfo.getApkPackage());
                startActivity(launchIntentForPackage);
                popupDismiss();
                break;
            case R.id.ll_uninstall:
                Intent uninstall_localIntent = new Intent("android.intent.action.DELETE",
                        Uri.parse("package:" + clickAppInfo.getApkPackage()));
                startActivity(uninstall_localIntent);
                popupDismiss();
                break;
            case R.id.ll_share:
                Intent share_localIntent = new Intent("android.intent.action.SEND");
                share_localIntent.setType("text/plain");
                share_localIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                share_localIntent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + clickAppInfo.getApkName() + "下载地址:" +
                                "https://play.google.com/store/apps/details?id=" + clickAppInfo.getApkPackage());
                startActivity(Intent.createChooser(share_localIntent, "分享"));
                popupDismiss();
                break;
            case R.id.ll_info:
                Intent info_intent = new Intent();
                info_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                info_intent.addCategory(Intent.CATEGORY_DEFAULT);
                info_intent.setData(Uri.parse("package:" + clickAppInfo.getApkPackage()));
                startActivity(info_intent);
                break;
        }
    }

    class AppManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userAppInfo.size() + 1 + systemAppInfo.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userAppInfo.size() + 1) {
                return null;
            }
            //把两个特殊的条目从appInfo中减去
            AppInfo appInfo;
            if (position < userAppInfo.size() + 1) {
                appInfo = userAppInfo.get(position - 1);
            } else {
                int location = userAppInfo.size() + 2;
                appInfo = systemAppInfo.get(position - location);
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //添加两个特殊条目，一个用户程序条目，一个系统程序条目
            if (position == 0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("用户程序(" + userAppInfo.size() + ")");
                return textView;
            } else if (position == userAppInfo.size() + 1) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统程序(" + systemAppInfo.size() + ")");
                return textView;
            }

            //把两个特殊的条目从appInfo中减去
            AppInfo appInfo;
            if (position < userAppInfo.size() + 1) {
                appInfo = userAppInfo.get(position - 1);
            } else {
                int location = userAppInfo.size() + 2;
                appInfo = systemAppInfo.get(position - location);
            }

            View view = null;
            ViewHolder hodler;
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                hodler = (ViewHolder) convertView.getTag();
            } else {
                view = View.inflate(AppManagerActivity.this, R.layout.item_app_manager, null);
                hodler = new ViewHolder();
                hodler.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                hodler.tv_app_size = (TextView) view.findViewById(R.id.tv_app_size);
                hodler.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
                hodler.tv_app_rom = (TextView) view.findViewById(R.id.tv_app_rom);
                view.setTag(hodler);
            }
            hodler.iv_app_icon.setBackground(appInfo.getIcon());
            hodler.tv_app_name.setText(appInfo.getApkName());
            hodler.tv_app_size.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getApkSize()));
            if (appInfo.isRom()) {
                hodler.tv_app_rom.setText("手机内存");
            } else {
                hodler.tv_app_rom.setText("sd卡内存");
            }
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_app_icon;
        TextView tv_app_size;
        TextView tv_app_name;
        TextView tv_app_rom;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AppManagerAdapter adapter = new AppManagerAdapter();
            list_view.setAdapter(adapter);
            ll_progress.setVisibility(View.GONE);
        }
    };

    private void initDada() {
        new Thread() {
            @Override
            public void run() {
                appInfos = AppInfos.getAppInfos(AppManagerActivity.this);
                //拆分appInfos 分别装载在用户集合 和 系统集合
                userAppInfo = new ArrayList<AppInfo>();
                systemAppInfo = new ArrayList<AppInfo>();
                for (AppInfo appinfo : appInfos) {
                    if (appinfo.isUserApp()) {
                        userAppInfo.add(appinfo);
                    } else {
                        systemAppInfo.add(appinfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        setContentView(R.layout.activity_app_manager);
        tv_rom = (TextView) findViewById(R.id.tv_rom);
        tv_sd = (TextView) findViewById(R.id.tv_sd);
        tv_app = (TextView) findViewById(R.id.tv_app);
        list_view = (ListView) findViewById(R.id.list_view);
        ll_progress = (LinearLayout) findViewById(R.id.ll_progress);

        //获取手机自带内存剩余容量
        long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();
        //获取sd卡剩余容量
        long sd_freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        tv_rom.setText("内存可用:" + Formatter.formatFileSize(this, rom_freeSpace));
        tv_sd.setText("sd卡可用:" + Formatter.formatFileSize(this, sd_freeSpace));

        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            /**
             *
             * @param view
             * @param firstVisibleItem 第一个可见的条的位置
             * @param visibleItemCount 一页可以展示多少个条目
             * @param totalItemCount   总共的item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                popupDismiss();
                //滑动的时候系统程序和应用程序不消失
                if (systemAppInfo != null && userAppInfo != null) {
                    if (firstVisibleItem > (userAppInfo.size() + 1)) {
                        tv_app.setText("系统程序(" + systemAppInfo.size() + ")");
                    } else {
                        tv_app.setText("应用程序(" + userAppInfo.size() + ")");
                    }
                }
            }
        });

        //ListView 点击显示popup弹出
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击的条目对象
                Object obj = list_view.getItemAtPosition(position);
                clickAppInfo = (AppInfo) obj;

                if (obj != null && obj instanceof AppInfo) {
                    View inflate = View.inflate(AppManagerActivity.this, R.layout.item_popup, null);

                    LinearLayout ll_uninstall = (LinearLayout) inflate.findViewById(R.id.ll_uninstall);
                    LinearLayout ll_run = (LinearLayout) inflate.findViewById(R.id.ll_run);
                    LinearLayout ll_share = (LinearLayout) inflate.findViewById(R.id.ll_share);
                    LinearLayout ll_info = (LinearLayout) inflate.findViewById(R.id.ll_info);

                    ll_uninstall.setOnClickListener(AppManagerActivity.this);
                    ll_share.setOnClickListener(AppManagerActivity.this);
                    ll_run.setOnClickListener(AppManagerActivity.this);
                    ll_info.setOnClickListener(AppManagerActivity.this);

                    popupDismiss();
                    //-2表示包裹内容
                    popupWindow = new PopupWindow(inflate, -2, -2);
                    //必须设置背景，不然没有动画
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    int[] location = new int[2];
                    view.getLocationInWindow(location);
                    //设置popup显示的位置
                    popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 70, location[1]);

                    //设置popup弹出来时由小变大
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnimation.setDuration(300);
                    inflate.startAnimation(scaleAnimation);
                }
            }


        });
    }


    //隐藏popup
    private void popupDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        popupDismiss();
    }
}
