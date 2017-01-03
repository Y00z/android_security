package org.yooz.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Yooz on 2016/1/30.
 */
public class AppInfo {
    private Drawable icon;
    private String apkName;
    private long apkSize;
    private String apkPackage;
    /**
     * true:表示用户应用
     * false:表示系统应用
     */
    private boolean isUserApp;

    /**
     * true:表示应用存储在在自带内存里面
     * false:表示应用存储在sd卡里面
     */
    private boolean isRom;


    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public String getApkPackage() {
        return apkPackage;
    }

    public void setApkPackage(String apkPackage) {
        this.apkPackage = apkPackage;
    }

    public boolean isUserApp() {
        return isUserApp;
    }

    public void setIsUserApp(boolean isUserApp) {
        this.isUserApp = isUserApp;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }
}
