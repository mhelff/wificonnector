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
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class WifiConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("WifiConnectivityReceiver", "action: " + intent.getAction());

        WifiConnectorActivity a = WifiConnectorActivity.instance();
        if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            if (a != null) {
                a.updatePositionView();
            }
        }

        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                boolean autoConnect = prefs.getBoolean("autoConnect", false);

                Log.d("WifiConnectivityReceiver", "triggering WifiConnectivityService with autoConnect=" + autoConnect);
                
                int command = autoConnect ? WifiConnectivityService.COMMAND_UNLOCK_CONNECTION
                        : WifiConnectivityService.COMMAND_CHECK_CONNECTION;
                Intent msgIntent = new Intent(context, WifiConnectivityService.class);
                msgIntent.putExtra(WifiConnectivityService.INTENT_COMMAND, command);
                context.startService(msgIntent);
            }

        }

    }

}
