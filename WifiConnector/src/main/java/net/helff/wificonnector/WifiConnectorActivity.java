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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * @author mhelff
 * 
 */
public class WifiConnectorActivity extends ActionBarActivity {

	public final static String TAG = "WifiConnectorActivity";

	private MenuItem refreshButton;
	
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavigationTitles;

	private int status = WifiConnectivityService.STATUS_NOT_CONNECTED;
	
	public static Context ctx;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ctx = getApplicationContext();
		
		LocationData.init();

		if (savedInstanceState != null) {
			status = savedInstanceState.getInt("connectionStatus",
					WifiConnectivityService.STATUS_NOT_CONNECTED);
		}

		setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mNavigationTitles = getResources().getStringArray(R.array.navigation_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavigationTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
        	
            public void onDrawerClosed(View view) {
            	mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
            	mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0); 
        }
		
        /*	
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

		updateConnectButton(status);
		
		*/
        refreshButton = (MenuItem) findViewById(R.id.action_refresh);
	}
	
    private void selectItem(int position) {
        // update the main content by replacing fragments
    	Fragment fragment;
    	if(position == 0) {
    		fragment = new PrinterFragment();
    	} else {
    		fragment = new WifiFragment();
    	}

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        if(position == 0) {
        	setTitle(R.string.printers);
        } else {
        	setTitle(R.string.app_name);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }



	@Override
	public void onStart() {
		super.onStart();
		ctx = getApplicationContext();
		LocationData.init();
		// start status receiver
		//startReceiver();

		// check wifi
		//checkConnection();

		// start a wifi scan for position update
		startScan();
		// immediately show position from old scan, most times thats quite
		// accurate
		//updatePositionView();
	}
	
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
	protected void startScan() {

		WifiManager wifiManager = (WifiManager) getApplicationContext()
				.getSystemService(Context.WIFI_SERVICE);

		if (wifiManager != null && wifiManager.isWifiEnabled()) {
			wifiManager.startScan();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		// trigger update
		ctx = getApplicationContext();
		LocationData.init();
		startScan();
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		// stop status scanner
		//stopReceiver();
		bundle.putInt("connectionStatus", status);
		super.onSaveInstanceState(bundle);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		
		switch (item.getItemId()) {

		case R.id.action_refresh:
			startScan();
			return true;
			
		case R.id.action_settings:
			Intent intentSettings = new Intent();
			intentSettings.setClass(this, WifiConnectorPreferences.class);
			startActivity(intentSettings);
			return true;

		case R.id.action_about:
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

	protected void triggerService(int command) {
		Intent intent = new Intent(this, WifiConnectivityService.class);
		intent.putExtra(WifiConnectivityService.INTENT_COMMAND, command);
		startService(intent);
	}

	private void setStatus(int s) {
		status = s;
	}

}