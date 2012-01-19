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

import java.util.List;

import net.helff.wificonnector.LocationData.Location;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author mhelff
 * 
 */
public class WifiConnectorActivity extends Activity {
    private static WifiConnectorActivity mInst;
    private StatusReceiver statusReceiver = null;

    protected final String L = "WifiConnectorActivity";

    private Button connectButton;
    private Button disconnectButton;
    private ImageView statusImage;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        connectButton = (Button) this.findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                unlockConnection();
            }

        });
        disconnectButton = (Button) this.findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                lockConnection();
            }

        });

        final ImageButton bPos = (ImageButton) this.findViewById(R.id.buttonPositionRefresh);
        bPos.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // just trigger WiFi-Scanning
                bPos.setEnabled(!((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                        .startScan());
            }

        });

        statusImage = (ImageView) this.findViewById(R.id.statusImage);

    }

    public static WifiConnectorActivity instance() {
        return mInst;
    }

    public void setStatus(String status, String detail) {
        Log.i(L, status + "; " + detail);
        TextView largeStatus = (TextView) this.findViewById(R.id.largeStatus);
        largeStatus.setText(status);
        TextView detailStatus = (TextView) this.findViewById(R.id.smallStatus);
        detailStatus.setText(detail);
    }

    @Override
    public void onStart() {
        super.onStart();
        // start status receiver
        statusReceiver = new StatusReceiver();
        IntentFilter intentFilter = new IntentFilter(WifiConnectivityService.INTENT_STATUS_NOTIFICATION);
        registerReceiver(statusReceiver, intentFilter);

        // check wifi
        checkConnection();

        // start a wifi scan for position update
        ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).startScan();
        // immediately show position from old scan, most times thats quite
        // accurate
        updatePositionView();

        mInst = this;
    }

    @Override
    public void onStop() {
        mInst = null;
        // stop status scanner
        unregisterReceiver(statusReceiver);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, Menu.FIRST, 1, R.string.settings).setShortcut('9', 's')
                .setIcon(android.R.drawable.ic_menu_preferences);

        menu.add(0, Menu.FIRST + 1, 1, R.string.info).setShortcut('0', 'i')
                .setIcon(android.R.drawable.ic_menu_info_details);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case Menu.FIRST:
            Intent intent = new Intent();
            intent.setClass(this, WifiConnectorPreferences.class);
            startActivity(intent);
            return true;

        case Menu.FIRST + 1:
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("About");
            alert.setMessage("WifiConnector\nCopyright (C) 2012 Martin Helff\nThis software is distributed under "
                    + "the terms of the GNU General Public License v3\nhttps://github.com/mhelff/wificonnector/");

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // nothing
                }
            });

            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkConnection() {
        triggerService(WifiConnectivityService.COMMAND_CHECK_CONNECTION);
    }

    public void unlockConnection() {
        triggerService(WifiConnectivityService.COMMAND_UNLOCK_CONNECTION);
    }

    public void lockConnection() {
        triggerService(WifiConnectivityService.COMMAND_LOCK_CONNECTION);
    }

    protected void triggerService(int command) {
        Intent intent = new Intent(this, WifiConnectivityService.class);
        intent.putExtra(WifiConnectivityService.INTENT_COMMAND, command);
        startService(intent);
    }

    public void updatePositionView() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        TextView position = (TextView) this.findViewById(R.id.position);
        String posText = "Unknown position";

        List<ScanResult> results = wifiManager.getScanResults();
        if (results != null) {
            ScanResult strongest = null;
            for (ScanResult scanResult : results) {
                // just check if data is in our location database
                if (LocationData.getLocation(scanResult.BSSID) != null) {
                    if (strongest == null || strongest.level < scanResult.level) {
                        strongest = scanResult;
                    }
                }
            }

            if (strongest != null) {
                Location location = LocationData.getLocation(strongest.BSSID);
                if (location != null) {
                    posText = location.getBuilding() + " " + location.getBlock() + location.getFloor() + " "
                            + location.getPosition();
                }
            }
        }

        position.setText(posText);
        final ImageButton bPos = (ImageButton) this.findViewById(R.id.buttonPositionRefresh);
        bPos.setEnabled(true);
    }

    private class StatusReceiver extends BroadcastReceiver {

        public StatusReceiver() {
            // might be used or never :-)
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiConnectivityService.INTENT_STATUS_NOTIFICATION)) {
                Bundle extras = intent.getExtras();
                String mainStatus = extras.getString(WifiConnectivityService.EXTRA_MAIN_STATUS);
                String detailStatus = extras.getString(WifiConnectivityService.EXTRA_DETAIL_STATUS);
                int status = extras.getInt(WifiConnectivityService.EXTRA_STATUS_CODE);

                TextView mainStatusView = (TextView) findViewById(R.id.largeStatus);
                mainStatusView.setText(mainStatus);
                TextView detailStatusView = (TextView) findViewById(R.id.smallStatus);
                detailStatusView.setText(detailStatus);

                switch (status) {

                case WifiConnectivityService.STATUS_LOCKED:
                    connectButton.setEnabled(true);
                    disconnectButton.setEnabled(false);
                    statusImage.setImageResource(R.drawable.btn_check_on_disabled_holo_light);
                    break;

                case WifiConnectivityService.STATUS_UNLOCKED:
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(true);
                    statusImage.setImageResource(R.drawable.btn_check_on_focused_holo_dark);
                    break;

                default:
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(false);
                    statusImage.setImageResource(R.drawable.btn_check_on_disabled_holo_dark);
                    break;
                }
            }
        }
    }

}