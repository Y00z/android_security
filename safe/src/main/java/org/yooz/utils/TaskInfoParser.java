package org.yooz.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import org.yooz.bean.TaskInfo;
import org.yooz.safe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yooz on 2016/2/4.
 */
public class TaskInfoParser {
    /**
     * 获取运行中的进程信息
     * @param context
     * @return
     */
    public static List<TaskInfo> getTaskInfos(Context context){
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        //包管理器
        PackageManager packageManager = context.getPackageManager();
        //进程管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //所有运行中的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo  :runningAppProcesses) {
            TaskInfo taskInfo = new TaskInfo();
            //当前应用占用多少内存
            Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            int totalPrivateDirty = processMemoryInfo[0].getTotalPrivateDirty() * 1024;
            taskInfo.setMemorySize(totalPrivateDirty);

            //运行中进程包名
            String processName = runningAppProcessInfo.processName;
            taskInfo.setPackageName(processName);
            try { //通过运行中的进程包名，获取到进程的其他详细信息
                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
                taskInfo.setIcon(appIcon);
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                taskInfo.setAppName(appName);
                //判断运行中的程序是否系统应用货用户应用
                int flags = packageInfo.applicationInfo.flags;
                if((flags & ApplicationInfo.FLAG_SYSTEM)!=0){
                    //表示系统app
                    taskInfo.setIsUserApp(false);
                } else {
                    //表示用户app
                    taskInfo.setIsUserApp(true);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                //系统核心程序里面有一些是没有图标的，给没有图标的设置默认图标
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
                taskInfo.setAppName("系统程序");
            }

            taskInfos.add(taskInfo);
        }

        return taskInfos;
    }
}
