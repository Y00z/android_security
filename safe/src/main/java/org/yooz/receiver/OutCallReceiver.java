package org.yooz.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.yooz.dao.AddressDao;

public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String number = getResultData();
		String address = AddressDao.getAddress(number);
		Toast.makeText(context, address, Toast.LENGTH_LONG).show();
	}

}
