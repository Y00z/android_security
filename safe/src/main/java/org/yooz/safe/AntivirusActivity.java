package org.yooz.safe;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.yooz.dao.AntivirusDao;
import org.yooz.utils.MD5Utils;

import java.util.List;

public class AntivirusActivity extends ActionBarActivity {

    private Message message;
    // 扫描开始
    protected static final int BEGING = 1;
    // 扫描中
    protected static final int SCANING = 2;
    // 扫描结束
    protected static final int FINISH = 3;
    private TextView tv_beging;
    private ProgressBar pb_antivirus;
    private ImageView iv_scanning;
    private LinearLayout ll_antivirus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BEGING:
                    tv_beging.setText("初始化八核杀毒引擎");
                    break;
                case SCANING:
                    TextView textView = new TextView(AntivirusActivity.this);
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    if(scanInfo.desc) {
                        textView.setText(scanInfo.appName + "发现病毒");
                        textView.setTextColor(Color.RED);
                    } else {
                        textView.setText(scanInfo.appName + "扫描安全");
                        textView.setTextColor(Color.GREEN);
                    }
                    ll_antivirus.addView(textView);
                    break;
                case FINISH:
                    iv_scanning.clearAnimation();
                    break;
            }
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                message = handler.obtainMessage();
                message.what = BEGING;
                PackageManager packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                //设置最大值
                int size = installedPackages.size();
                pb_antivirus.setMax(size);
                int progress = 0;
                for (PackageInfo packageInfo : installedPackages) {
                    ScanInfo scanInfo = new ScanInfo();

                    //获取到所有应用程序的名字
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    String packageName = packageInfo.applicationInfo.packageName;
                    scanInfo.packageName = packageName;
                    scanInfo.appName = appName;
                    //获取应用程序目录
                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    //获取程序md5值
                    String md5 = MD5Utils.getFileMD5(sourceDir);
                    //查询md5值是否病毒
                    String desc = AntivirusDao.checkFileVirus(md5);

                    //如果desc是null表示没有病毒
                    if (desc == null) {
                        scanInfo.desc = false;
                    } else {
                        scanInfo.desc = true;
                    }
                    //设置当前进度条的值
                    progress++;
                    pb_antivirus.setProgress(progress);
                    message = handler.obtainMessage();
                    message.what = SCANING;
                    message.obj = scanInfo;
                    handler.sendMessage(message);
                }

                message = handler.obtainMessage();
                message.what = FINISH;
                handler.sendMessage(message);
            }
        }.start();
    }

    //定义bean对象
    static class ScanInfo {
        boolean desc;
        String appName;
        String packageName;
    }

    private void initUI() {
        setContentView(R.layout.activity_antivirus);
        tv_beging = (TextView) findViewById(R.id.tv_beging);
        pb_antivirus = (ProgressBar) findViewById(R.id.pb_antivirus);
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        ll_antivirus = (LinearLayout) findViewById(R.id.ll_antivirus);
        /**
         * 设置动画转圈
         */
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //设置动画时间
        rotateAnimation.setDuration(3000);
        //无限循环动画
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        iv_scanning.startAnimation(rotateAnimation);

    }


}
