package org.yooz.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import org.yooz.service.KillProcesWidgetService;

/**
 * Created by Yooz on 2016/2/10.
 */
public class MyAppWidget extends AppWidgetProvider {
    //创建小部件的时候调用
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        System.out.println("onEnabled");
        Intent intent = new Intent(context,KillProcesWidgetService.class);
        context.startService(intent);
    }
    //删除小控件的时候调用
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context,KillProcesWidgetService.class);
        context.stopService(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        System.out.println("onUpdate");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        System.out.println("onDeleted");
    }
}
