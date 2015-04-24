package net.helff.wificonnector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PrinterFragment extends Fragment {

	private TextView positionView;
	private ToggleButton colorCheck;
	private ToggleButton a3Check;
	private ToggleButton copyCheck;
	private ListView printerList;

	private LocationStatusReceiver locationStatusReceiver;

	public PrinterFragment() {
		// Empty constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.printer_layout, container, false);

		positionView = (TextView) rootView.findViewById(R.id.position);
		colorCheck = (ToggleButton) rootView.findViewById(R.id.colorSwitch);
		printerList = (ListView) rootView.findViewById(R.id.printerList);

		colorCheck.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// printersView.setText(R.string.printer_notfound);
				// just trigger WiFi-Scanning
				updatePositionView();
				((WifiConnectorActivity) getActivity()).startScan();
			}

		});
		a3Check = (ToggleButton) rootView.findViewById(R.id.sizeSwitch);
		a3Check.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// printersView.setText(R.string.printer_notfound);
				updatePositionView();
				// just trigger WiFi-Scanning
				((WifiConnectorActivity) getActivity()).startScan();
			}

		});

		copyCheck = (ToggleButton) rootView.findViewById(R.id.copySwitch);
		copyCheck.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// printersView.setText(R.string.printer_notfound);
				// just trigger WiFi-Scanning
				updatePositionView();
				((WifiConnectorActivity) getActivity()).startScan();
			}

		});

		return rootView;
	}

	// TODO: fill list view in background thread
	public void updatePositionView() {
		String posText = getResources().getString(R.string.position_default);

		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.printer_list_item,
				Arrays.asList(getResources().getString(R.string.printer_notfound)));

		List<ScanResult> results = null;
		WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
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
				WifiLocation wifilocation = LocationData.getWifiLocation(strongest.BSSID);

				if (wifilocation != null) {
					Location location = LocationData.getLocation(wifilocation.getLocation());
					String posTranslated = (String) getResources().getText(
							getResources().getIdentifier("WIFI_LOC_" + location.getPosition(), "string",
									"net.helff.wificonnector"));

					posText = location.getBuilding() + " " + location.getBlock() + location.getFloor() + " "
							+ posTranslated + " " + strongest.BSSID;

					Map<Integer, Printer> printers = LocationData.findPrintersAtLocation(location,
							colorCheck.isChecked(), a3Check.isChecked(), copyCheck.isChecked());
					if (!printers.isEmpty()) {
						List<String> pList = new ArrayList<String>();
						int i = 0;
						for (Integer distance : printers.keySet()) {
							if (i++ > 10) {
								break;
							}
							Printer printer = printers.get(distance);
							Location ploc = LocationData.getLocation(printer.getLocation());
							pList.add(printer.getName() + " " + getResources().getString(R.string.printer_location)
									+ " " + ploc.getPosition() + " "
									+ getResources().getString(R.string.printer_distance) + " " + (distance) + "m");
						}
						listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.printer_list_item, pList);
					}
				}
			}
		}

		positionView.setText(posText);
		printerList.setAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onStart() {
		super.onStart();

		startReceiver();
		updatePositionView();
	}

	@Override
	public void onStop() {
		stopReceiver();

		super.onStop();
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
		updatePositionView();
		super.onResume();
	}

	private void startReceiver() {
		if (locationStatusReceiver == null) {
			locationStatusReceiver = new LocationStatusReceiver();
			IntentFilter intentFilter = new IntentFilter(LocationIntent.INTENT_LOCATION_NOTIFICATION);
			getActivity().registerReceiver(locationStatusReceiver, intentFilter);
		}
	}

	private void stopReceiver() {
		if (locationStatusReceiver != null) {
			getActivity().unregisterReceiver(locationStatusReceiver);
			locationStatusReceiver = null;
		}
	}

	private class LocationStatusReceiver extends BroadcastReceiver {

		public LocationStatusReceiver() {
			// might be used or never :-)
		}

		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(LocationIntent.INTENT_LOCATION_NOTIFICATION)) {
				updatePositionView();
			}
		}
	}

}
