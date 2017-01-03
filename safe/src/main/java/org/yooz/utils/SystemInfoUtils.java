package org.yooz.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Yooz on 2016/2/4.
 */
public class SystemInfoUtils {
    //获取进程个数
    public static int getProcessCount(Context context) {
        //进程管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //运行中的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        //后台进程个数
        int size = runningAppProcesses.size();
        return size;
    }

    /**
     * 获取剩余内存
     * @param context
     * @return
     */
    public static long getAvailMem(Context context) {
        //进程管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //运行中的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获取内存基本信息
        activityManager.getMemoryInfo(memoryInfo);
        //剩余剩余内存
        long availMem = memoryInfo.availMem;
        return availMem;
    }

    /**
     * 获取总共内存
     * @param context
     * @return
     */
    public static long getTotalMem(Context context) {
        //进程管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //运行中的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获取内存基本信息
        activityManager.getMemoryInfo(memoryInfo);
        //获取全部内存
        long totalMem = memoryInfo.totalMem;
        return totalMem;
    }
}
