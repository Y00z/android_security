package org.yooz.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import java.util.List;

/**
 * 服务运行状态获取
 * @author Yooz
 *
 */
public class ServiceStatusUtils {
	public static boolean isServiceRunning(Context ctx,String ServiceName) {
		//获取系统所有的服务
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			String className = runningServiceInfo.service.getClassName();
			System.out.println(className);
			if(className.equals(ServiceName)){
				return true;
			}
		}
		return false;
	}

}
