package org.yooz.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import org.yooz.receiver.MyAppWidget;
import org.yooz.safe.R;
import org.yooz.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 桌面小控件一键清理
 */
public class KillProcesWidgetService extends Service {

    private AppWidgetManager widgetManager;

    public KillProcesWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //桌面控件管理器
        widgetManager = AppWidgetManager.getInstance(this);

        //每隔5秒钟更新一次空间
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask(){
            @Override
            public void run() {
                System.out.println("balabala");

                //第二个参数表示哪一个广播来处理这个小控件
                ComponentName componentName = new ComponentName(getApplicationContext(), MyAppWidget.class);
                //要把哪些布局文件现在在控件中
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
                //设置运行中的程序
                int processCount = SystemInfoUtils.getProcessCount(getApplicationContext());
                remoteViews.setTextViewText(R.id.process_count,"正在运行程序:"+String.valueOf(processCount));
                //设置可用内存
                long availMem = SystemInfoUtils.getAvailMem(getApplicationContext());
                remoteViews.setTextViewText(R.id.process_memory, "可用内存:"+Formatter.formatFileSize(getApplicationContext(),availMem));


                //设置一键清理，自动调用该意图的广播
                //发生隐式意图
                Intent intent = new Intent();
                intent.setAction("org.yooz.killProcesWidget");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                remoteViews.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);

                //更新小控件
                widgetManager.updateAppWidget(componentName, remoteViews);
            }
        };
        //每隔5秒钟更新
        timer.schedule(timerTask,0,5000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
