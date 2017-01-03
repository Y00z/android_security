package org.yooz.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {

	private LocationManager lm;
	private LocationListener listener;
	private SharedPreferences mPre;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mPre = getSharedPreferences("config", MODE_PRIVATE);

		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 获取所有的经纬度提供者
		// List<String> allProviders = lm.getAllProviders();
		// System.out.println(allProviders);
		Criteria criteria = new Criteria();
		// 最佳提供者标准，是否允许付费(3G)
		criteria.setCostAllowed(true);
		// 最佳提供者标准，选择精确度
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// 获取最佳位置提供者
		String bestProvider = lm.getBestProvider(criteria, true);
		listener = new MyLocationListener();
		// 使用gps定位
		// lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
		// listener);
		// 使用最佳位置提供者定位
		lm.requestLocationUpdates(bestProvider, 0, 0, listener);
	}

	class MyLocationListener implements LocationListener {

		// 位置提供者状态发生变化
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		// 用户打开gps
		public void onProviderEnabled(String provider) {
		}

		// 用户关闭gps
		public void onProviderDisabled(String provider) {

		}

		// 位置改变的时候调用
		public void onLocationChanged(Location location) {
			String j = "经度" + location.getLongitude();
			String w = "纬度" + location.getLatitude();
			String accuracy = "精确度" + location.getAccuracy();
			String altitude = "海拔" + location.getAltitude();
			String address = j + "\n" + w + "\n" + accuracy + "\n" + altitude;
			mPre.edit()
					.putString(
							"location",
							"j:" + location.getLongitude() + "\n" + "w:"
									+ location.getLatitude()).commit();
		}
	}
}
