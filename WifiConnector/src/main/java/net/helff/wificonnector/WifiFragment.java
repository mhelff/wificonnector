package net.helff.wificonnector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class WifiFragment extends Fragment {

	private TextView mobileNumber;
	private CheckBox autoConnect;
	private TextView mainStatusView;
	private Button connectButton;
	private StatusReceiver statusReceiver;
	private int status;

	public WifiFragment() {
		// Empty constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.wifi_layout, container, false);

		mobileNumber = (TextView) rootView.findViewById(R.id.mobileNumber);
		autoConnect = (CheckBox) rootView.findViewById(R.id.autoConnect);
		connectButton = (Button) rootView.findViewById(R.id.connectButton);
		mainStatusView = (TextView) rootView.findViewById(R.id.statustext);

		autoConnect.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// printersView.setText(R.string.printer_notfound);
				// just trigger WiFi-Scanning
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				Editor e = prefs.edit();
				e.putBoolean("autoConnect", autoConnect.isChecked());
				e.commit();
				((WifiConnectorActivity) getActivity()).startScan();
			}

		});
		
		mobileNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				Editor e = prefs.edit();
				e.putString("mobileNumber", s.toString());
				e.commit();
				((WifiConnectorActivity) getActivity()).startScan();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		connectButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				connectButton.setEnabled(false);
				flipConnection();
			}

		});

		updatePrefValues();
		
		return rootView;
	}
	
	private void updatePrefValues() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mobileNumber.setText(prefs.getString("mobileNumber", ""));
		autoConnect.setChecked(prefs.getBoolean("autoConnect", false));
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
		Intent intent = new Intent(getActivity(), WifiConnectivityService.class);
		intent.putExtra(WifiConnectivityService.INTENT_COMMAND, command);
		getActivity().startService(intent);
	}

	@Override
	public void onStart() {
		super.onStart();
		updatePrefValues();
		startReceiver();
		checkConnection();
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
		updatePrefValues();
		startReceiver();
		// trigger update
		checkConnection();
		super.onResume();
	}
	
	private void startReceiver() {
		if (statusReceiver == null) {
			statusReceiver = new StatusReceiver();
			IntentFilter intentFilter = new IntentFilter(
					StatusIntent.INTENT_STATUS_NOTIFICATION);
			getActivity().registerReceiver(statusReceiver, intentFilter);
		}
	}

	private void stopReceiver() {
		if (statusReceiver != null) {
			getActivity().unregisterReceiver(statusReceiver);
			statusReceiver = null;
		}
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

				
				mainStatusView.setText(mainStatus);
				
				updateConnectButton(status);
			}
		}
		
		private void updateConnectButton(int status) {
			switch(status) {
			case WifiConnectivityService.STATUS_LOCKED:
				connectButton.setEnabled(true);
				connectButton.setText(R.string.connect);
				break;

			case WifiConnectivityService.STATUS_UNLOCKED:
				connectButton.setEnabled(true);
				connectButton.setText(R.string.disconnect);
				break;

			case WifiConnectivityService.STATUS_WORKING:
				connectButton.setEnabled(false);
				connectButton.setText(R.string.connecting);
				break;

			default:
				connectButton.setEnabled(false);
				connectButton.setText(R.string.connect);
				break;
			}
		}
	}

}
