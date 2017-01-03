package org.yooz;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Yooz on 2016/3/6.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        Log.e("Application-----", "-----Create");
        Toast.makeText(getApplicationContext()," 激光推送" ,Toast.LENGTH_SHORT).show();
    }
}
