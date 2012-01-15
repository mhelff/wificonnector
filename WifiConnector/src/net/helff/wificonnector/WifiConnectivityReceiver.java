/* 
 * Copyright (C) 2012 Martin Helff
 * 
 * This file is part of WifiConnector.
 * 
 * WifiConnector is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * WifiConnector is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with WifiConnector.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
