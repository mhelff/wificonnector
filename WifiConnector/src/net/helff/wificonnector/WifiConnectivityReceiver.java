package net.helff.wificonnector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class WifiConnectivityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(WifiConnectivityReceiver.class.getSimpleName(), "action: "
                + intent.getAction());

		WifiConnectorActivity a = WifiConnectorActivity.instance();
		if(a != null) {
			a.addViewText(intent.toString());
		}

		NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			if(a != null) {
				a.addViewText(networkInfo.toString());
			}
		}
		   
		
	}

}
