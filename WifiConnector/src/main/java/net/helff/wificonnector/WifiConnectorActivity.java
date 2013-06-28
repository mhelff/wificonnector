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
import java.util.Map;

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
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author mhelff
 * 
 */
public class WifiConnectorActivity extends Activity {
	private StatusReceiver statusReceiver = null;

	public final static String TAG = "WifiConnectorActivity";

	private ImageView connectButton;
	private ImageView statusImage;
	private TextView positionView;
	private TextView printersView;
	private CheckBox colorCheck;
	private CheckBox a3Check;
	private CheckBox copyCheck;

	private int status = WifiConnectivityService.STATUS_NOT_CONNECTED;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			status = savedInstanceState.getInt("connectionStatus",
					WifiConnectivityService.STATUS_NOT_CONNECTED);
		}

		setContentView(R.layout.main);
		connectButton = (ImageView) this.findViewById(R.id.connectButton);
		connectButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				connectButton.setEnabled(false);
				flipConnection();
			}

		});

		Button signupButton = (Button) findViewById(R.id.signupButton);
		signupButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { "martin@helff.net" });
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"WifiConnector signup");
				emailIntent
						.putExtra(android.content.Intent.EXTRA_TEXT,
								"Please send me the latest news and versions of WifiConnector.");
				startActivity(Intent.createChooser(emailIntent,
						getString(R.string.send)));
			}

		});

		final ImageButton bPos = (ImageButton) this
				.findViewById(R.id.buttonPositionRefresh);
		bPos.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// just trigger WiFi-Scanning
				WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				if (wifiManager != null) {
					bPos.setEnabled(!wifiManager.startScan());
				}
			}

		});

		statusImage = (ImageView) this.findViewById(R.id.statusImage);
		positionView = (TextView) this.findViewById(R.id.position);
		printersView = (TextView) this.findViewById(R.id.smallPrinters);
		colorCheck = (CheckBox) this.findViewById(R.id.colorSwitch);
		colorCheck.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// printersView.setText(R.string.printer_notfound);
				// just trigger WiFi-Scanning
				startScan();
			}

		});
		a3Check = (CheckBox) this.findViewById(R.id.sizeSwitch);
		a3Check.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// printersView.setText(R.string.printer_notfound);
				// just trigger WiFi-Scanning
				startScan();
			}

		});

		copyCheck = (CheckBox) this.findViewById(R.id.copySwitch);
		copyCheck.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// printersView.setText(R.string.printer_notfound);
				// just trigger WiFi-Scanning
				startScan();
			}

		});

		updateConnectButton(status);
	}

	public void setStatus(String status, String detail) {
		Log.i(TAG, status + "; " + detail);
		TextView largeStatus = (TextView) this.findViewById(R.id.largeStatus);
		largeStatus.setText(status);
		TextView detailStatus = (TextView) this.findViewById(R.id.smallStatus);
		detailStatus.setText(detail);
	}

	@Override
	public void onStart() {
		super.onStart();
		// start status receiver
		startReceiver();

		// check wifi
		checkConnection();

		// start a wifi scan for position update
		startScan();
		// immediately show position from old scan, most times thats quite
		// accurate
		updatePositionView();
	}

	private void startScan() {

		WifiManager wifiManager = (WifiManager) getApplicationContext()
				.getSystemService(Context.WIFI_SERVICE);

		if (wifiManager != null && wifiManager.isWifiEnabled()) {
			wifiManager.startScan();
		}
	}

	@Override
	public void onPause() {
		// stop status scanner
		stopReceiver();

		super.onPause();
	}

	@Override
	public void onResume() {
		startReceiver();
		// trigger update
		startScan();
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		// stop status scanner
		stopReceiver();
		bundle.putInt("connectionStatus", status);
		super.onSaveInstanceState(bundle);
	}

	private void startReceiver() {
		if (statusReceiver == null) {
			statusReceiver = new StatusReceiver();
			IntentFilter intentFilter = new IntentFilter(
					StatusIntent.INTENT_STATUS_NOTIFICATION);
			intentFilter.addAction(LocationIntent.INTENT_LOCATION_NOTIFICATION);
			registerReceiver(statusReceiver, intentFilter);
		}
	}

	private void stopReceiver() {
		if (statusReceiver != null) {
			unregisterReceiver(statusReceiver);
			statusReceiver = null;
		}
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

			alert.setTitle(R.string.about);
			alert.setMessage(R.string.about_detail);

			alert.setPositiveButton(R.string.about_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// nothing
						}
					});

			alert.show();
		}
		return super.onOptionsItemSelected(item);
	}

	private void checkConnection() {
		triggerService(WifiConnectivityService.COMMAND_CHECK_CONNECTION);
	}

	private void flipConnection() {
		if (status == WifiConnectivityService.STATUS_LOCKED) {
			triggerService(WifiConnectivityService.COMMAND_UNLOCK_CONNECTION);
		} else if (status == WifiConnectivityService.STATUS_UNLOCKED) {
			triggerService(WifiConnectivityService.COMMAND_LOCK_CONNECTION);
		}
	}

	protected void triggerService(int command) {
		Intent intent = new Intent(this, WifiConnectivityService.class);
		intent.putExtra(WifiConnectivityService.INTENT_COMMAND, command);
		startService(intent);
	}

	public void updatePositionView() {
		String posText = getResources().getString(R.string.position_default);
		String printerText = getResources()
				.getString(R.string.printer_notfound);

		List<ScanResult> results = null;
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null) {
			results = wifiManager.getScanResults();
		}
		if (results != null) {
			ScanResult strongest = null;
			for (ScanResult scanResult : results) {
				// just check if data is in our location database
				if (LocationData.getWifiLocation(scanResult.BSSID) != null) {
					if (strongest == null || strongest.level < scanResult.level) {
						strongest = scanResult;
					}
				}
			}

			if (strongest != null) {
				WifiLocation wifilocation = LocationData
						.getWifiLocation(strongest.BSSID);

				if (wifilocation != null) {
					Location location = LocationData.getLocation(wifilocation
							.getLocation());
					posText = location.getBuilding() + " "
							+ location.getBlock() + location.getFloor() + " "
							+ location.getPosition() + " " + strongest.BSSID;

					Map<Integer, Printer> printers = LocationData
							.findPrintersAtLocation(location,
									colorCheck.isChecked(),
									a3Check.isChecked(), copyCheck.isChecked());
					if (!printers.isEmpty()) {
						printerText = getResources().getString(
								R.string.printer_found);
						int i = 0;
						for (Integer distance : printers.keySet()) {
							if (i++ > 3) {
								break;
							}
							Printer printer = printers.get(distance);
							Location ploc = LocationData.getLocation(printer
									.getLocation());
							printerText = printerText
									+ "\n"
									+ printer.getName()
									+ " "
									+ getResources().getString(
											R.string.printer_location)
									+ " "
									+ ploc.getPosition()
									+ " "
									+ getResources().getString(
											R.string.printer_distance) + " "
									+ (distance/100) + "m";
						}
					}
				}
			}
		}

		positionView.setText(posText);
		printersView.setText(printerText);
		final ImageButton bPos = (ImageButton) this
				.findViewById(R.id.buttonPositionRefresh);
		bPos.setEnabled(true);
	}

	private void updateConnectButton(int status) {
		switch (status) {

		case WifiConnectivityService.STATUS_LOCKED:
			connectButton.setEnabled(true);
			connectButton.setImageResource(R.drawable.connect);
			statusImage.setImageResource(R.drawable.wifi_off);
			connectButton.setVisibility(View.VISIBLE);
			statusImage.setVisibility(View.VISIBLE);
			break;

		case WifiConnectivityService.STATUS_UNLOCKED:
			connectButton.setEnabled(true);
			connectButton.setImageResource(R.drawable.disconnect);
			statusImage.setImageResource(R.drawable.wifi_on);
			connectButton.setVisibility(View.VISIBLE);
			statusImage.setVisibility(View.VISIBLE);
			break;

		case WifiConnectivityService.STATUS_WORKING:
			connectButton.setVisibility(View.GONE);
			statusImage.setVisibility(View.INVISIBLE);
			break;

		default:
			connectButton.setEnabled(false);
			connectButton.setImageResource(R.drawable.connect);
			statusImage.setImageResource(R.drawable.wifi_off);
			break;
		}
	}

	private void setStatus(int s) {
		status = s;
	}

	private class StatusReceiver extends BroadcastReceiver {

		public StatusReceiver() {
			// might be used or never :-)
		}

		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					StatusIntent.INTENT_STATUS_NOTIFICATION)) {
				Bundle extras = intent.getExtras();
				String mainStatus = extras
						.getString(StatusIntent.EXTRA_MAIN_STATUS);
				String detailStatus = extras
						.getString(StatusIntent.EXTRA_DETAIL_STATUS);
				int status = extras.getInt(StatusIntent.EXTRA_STATUS_CODE);

				TextView mainStatusView = (TextView) findViewById(R.id.largeStatus);
				mainStatusView.setText(mainStatus);
				TextView detailStatusView = (TextView) findViewById(R.id.smallStatus);
				detailStatusView.setText(detailStatus);

				setStatus(status);
				updateConnectButton(status);
			}

			if (intent.getAction().equals(
					LocationIntent.INTENT_LOCATION_NOTIFICATION)) {
				updatePositionView();
			}
		}
	}

}