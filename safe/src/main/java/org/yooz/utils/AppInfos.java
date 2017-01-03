package org.yooz.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import org.yooz.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yooz on 2016/1/31.
 */
public class AppInfos {

    /**
     * 获取已经安装的所有应用
     * @param context
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context) {
        List<AppInfo> appInfos = new ArrayList<AppInfo>();

        //获取包的管理器
        PackageManager packageManager = context.getPackageManager();
        //获取所有已安装应用的包
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo installedPackage : installedPackages) {

            AppInfo appInfo = new AppInfo();

            //应用图标
            Drawable icon = installedPackage.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(icon);

            //应用名称
            String apkName = installedPackage.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setApkName(apkName);

            //应用包名
            String apkpackage = installedPackage.packageName;
            appInfo.setApkPackage(apkpackage);

            //应用安装路径
            String sourceDir = installedPackage.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            long apkSize = file.length();
            appInfo.setApkSize(apkSize);

            int flags = installedPackage.applicationInfo.flags;
            if((flags & ApplicationInfo.FLAG_SYSTEM)!=0){
                //表示系统app
                appInfo.setIsUserApp(false);
            } else {
                //表示用户app
                appInfo.setIsUserApp(true);
            }

            if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0) {
                //表示储存在sd卡
                appInfo.setIsRom(false);
            } else {
                //表示存储在手机内存
                appInfo.setIsRom(true);
            }

            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
