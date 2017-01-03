package org.yooz.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

import org.yooz.dao.AppLockDao;
import org.yooz.safe.LockPwdActivity;

import java.util.List;

public class WatchDagService extends Service {


    private ActivityManager activitymanager;
    private AppLockDao appLockDao;
    private boolean flag = false;
    private String stopDogPackageName;
    private WatchDogReceiver receiver;
//    private ArrayList<String> appLockInfos;

    private class WatchDogReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("org.yooz.mobile.stopWatchDog")) {
                stopDogPackageName = intent.getStringExtra("packageName");
                //如果接收到锁屏广播，就停止看门狗
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                stopDogPackageName = null;
                flag = false;
                //如果接收到开屏广播，就开始看门狗
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (flag == false) {
                    startWatchDog();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appLockDao = new AppLockDao(this);
        //        appLockInfos = appLockDao.findAll();


        receiver = new WatchDogReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //注册锁屏 开屏  以及 看门狗停止保护广播。
        intentFilter.addAction("org.yooz.mobile.stopWatchDog");
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, intentFilter);

        activitymanager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);


        startWatchDog();
    }

    private void startWatchDog() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                flag = true;
                while (flag) {
                    //运行中的程序
                    List<ActivityManager.RunningTaskInfo> runningTasks = activitymanager.getRunningTasks(1);
                    //获取最上面的程序
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    //获取到最顶端的应用程序
                    String packageName = runningTaskInfo.topActivity.getPackageName();
                    //让狗休息一会
                    SystemClock.sleep(30);
                    if (appLockDao.find(packageName)) {
//                    if (appLockInfos.contains(packageName)) {

                        if(!packageName.equals(stopDogPackageName)) {
                            System.out.println(packageName + "在锁里面");
                            Intent intent = new Intent(WatchDagService.this, LockPwdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                        }

                    } else {
                        System.out.println(packageName + "没有在锁里面");
                    }
                }
            }
        }.start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(receiver);
        receiver = null;
    }
}
