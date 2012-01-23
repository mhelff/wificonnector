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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class WifiConnectivityService extends IntentService {

    public static final String INTENT_COMMAND = "intent-command";

    public static final String TAG = "WifiConnetivityService";

    public static final int COMMAND_SEND_STATUS = 1;
    public static final int COMMAND_REFRESH_STATUS = 2;
    public static final int COMMAND_CHECK_CONNECTION = 3;
    public static final int COMMAND_UNLOCK_CONNECTION = 4;
    public static final int COMMAND_AUTO_UNLOCK_CONNECTION = 5;
    public static final int COMMAND_LOCK_CONNECTION = 6;

    public static final int STATUS_CONFIG_ERROR = -1;
    public static final int STATUS_NOT_CONNECTED = 0;
    public static final int STATUS_LOCKED = 1;
    public static final int STATUS_UNLOCKING = 2;
    public static final int STATUS_UNLOCKED = 3;

    private String mainStatus = "Initializing";
    private String detailStatus = "Please wait";
    private int statusCode;

    private String mobileNumber;

    private HttpClient httpClient;
    private HttpContext localContext;
    private WifiManager wifiManager;
    
    private boolean notify = false;

    public WifiConnectivityService() {
        super(WifiConnectivityService.class.getName());
    }

    public WifiConnectivityService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        // autoConnect = prefs.getBoolean("autoConnect", false);
        mobileNumber = prefs.getString("mobileNumber", "");

        httpClient = new DefaultHttpClient();
        localContext = new BasicHttpContext();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int cmd = intent.getIntExtra(INTENT_COMMAND, COMMAND_REFRESH_STATUS);
        // Always send status first for fast gui response
        sendStatusIntent();

        notify = false;
        
        switch (cmd) {

        case COMMAND_SEND_STATUS:
        case COMMAND_REFRESH_STATUS:
            sendStatusIntent();
            break;

        case COMMAND_CHECK_CONNECTION:
            try {
                // check if network is already unlocked
                checkConnectivity();
            } catch (ConnectionWorkflowException e) {
                if (e.getCause() != null) {
                    Log.e("WifiConnectivityService", e.getMessage(), e.getCause());
                } else {
                    Log.d("WifiConnectivityService", e.getMessage());
                }
            }
            break;

        case COMMAND_AUTO_UNLOCK_CONNECTION:
            notify = true;
            
        case COMMAND_UNLOCK_CONNECTION:
            try {
                // check if network is already unlocked
                if (checkConnectivity()) {
                    break;
                }

                // post mobile-number to login page
                submitMSISDN(httpClient, localContext, mobileNumber);

                // wait for SMS to arrive and parse token
                LoginToken loginToken = waitForToken();

                // post token to confirmation page
                submitToken(httpClient, localContext, loginToken);

                // check connectivity to web page
                checkInternetAccess(httpClient, localContext);
            } catch (ConnectionWorkflowException e) {
                if (e.getCause() != null) {
                    Log.e("WifiConnectivityService", e.getMessage(), e.getCause());
                } else {
                    Log.d("WifiConnectivityService", e.getMessage());
                }
            }
            break;

        case COMMAND_LOCK_CONNECTION:
            try {
                checkWifi();

                // check connectivity to google or other page
                logout(httpClient);
            } catch (ConnectionWorkflowException e) {
                if (e.getCause() != null) {
                    Log.e("WifiConnectivityService", e.getMessage(), e.getCause());
                } else {
                    Log.d("WifiConnectivityService", e.getMessage());
                }
            }
            break;
        }
    }

    protected void sendStatusIntent() {
        Intent statusIntent = new StatusIntent(mainStatus, detailStatus, statusCode);

        this.sendBroadcast(statusIntent);
    }

    protected void publishProgress(String main, String detail, int status) {
        this.mainStatus = main;
        this.detailStatus = detail;
        this.statusCode = status;

        // should be Log.d?
        Log.i("WifiConnectivityService", detailStatus);

        sendStatusIntent();
        if(notify) {
            sendNotification(detail);
        }
    }

    protected void sendNotification(String msg) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(/* your notification */);
                    
        Intent intent = new Intent(this, WifiConnectorActivity.class); 
        intent.setAction("android.intent.action.MAIN"); 
        intent.addCategory("android.intent.category.LAUNCHER"); 
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, 
        intent, 0);
        
        notification.setLatestEventInfo(this.getApplicationContext(), "WifiConnector", msg, pendingIntent);
        notificationManager.notify(1, notification);
    
    }

    protected boolean checkConnectivity() throws ConnectionWorkflowException {
        boolean unlocked = false;

        // check if mobile number is set
        checkMsisdn(mobileNumber);

        // check if WiFi is ours
        checkWifi();

        // check if network is already unlocked
        unlocked = checkInternetAccess(httpClient, localContext);

        return unlocked;
    }

    protected void checkMsisdn(String msisdn) throws ConnectionWorkflowException {
        if (msisdn == null || msisdn.trim().length() == 0) {
            // post error
            publishProgress("Check app settings", "Mobile phone number not set", STATUS_CONFIG_ERROR);
            throw new ConnectionWorkflowException("mobileNumber not set");
        }
    }

    protected void checkWifi() throws ConnectionWorkflowException {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null || !"TelefonicaPublic".equals(wifiInfo.getSSID())) {
            // post error
            publishProgress("Not connected", "Not connected to TelefonicaPublic", STATUS_NOT_CONNECTED);
            throw new ConnectionWorkflowException("No connection to TelefonicaPublic");
        }
    }

    protected boolean checkInternetAccess(HttpClient httpClient, HttpContext localContext)
            throws ConnectionWorkflowException {

        BufferedReader reader = null;
        boolean unlocked = false;

        try {
            HttpGet httpGet = new HttpGet("http://www.helff.net");
            HttpResponse response = httpClient.execute(httpGet, localContext);
            String result = "";

            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line + "\n";
            }
            if (result.contains("<title>helff.net</title>")) {
                unlocked = true;
                publishProgress("WiFi ready", "Internet access is unlocked", STATUS_UNLOCKED);
            } else {
                publishProgress("TelefonicaPublic locked", "Press connect to unlock", STATUS_LOCKED);
            }
            reader.close();
            response.getEntity().consumeContent();
        } catch (ClientProtocolException e) {
            publishProgress(mainStatus, "Error checking network status", STATUS_LOCKED);
            throw new ConnectionWorkflowException("error checking connectivity", e);
        } catch (IOException e) {
            publishProgress(mainStatus, "Error checking network status", STATUS_LOCKED);
            throw new ConnectionWorkflowException("error checking connectivity", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close reader", e);
                }
            }
        }

        return unlocked;
    }

    protected void submitMSISDN(HttpClient httpClient, HttpContext localContext, String msisdn)
            throws ConnectionWorkflowException {
        try {
            // post mobile-number to login page
            publishProgress("Unlocking WiFi", "Submitting mobile phone number " + mobileNumber, STATUS_UNLOCKING);
            HttpPost httpPost = new HttpPost("http://wlan.de.telefonica:8001/login.php?l=de");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("handynr", msisdn));
            nameValuePairs.add(new BasicNameValuePair("login", "Token per SMS zusenden &gt;"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httpPost, localContext);
            // TODO: check response for success or "not registered"
            response.getEntity().consumeContent();
            publishProgress("Unlocking WiFi", "Submitted MSISDN: " + msisdn, STATUS_UNLOCKING);
        } catch (ClientProtocolException e) {
            publishProgress("Unlocking WiFi", "Mobile phone number not registered", STATUS_LOCKED);
            throw new ConnectionWorkflowException("error submitting msisdn form", e);
        } catch (IOException e) {
            publishProgress("Unlocking WiFi", "Could not submit mobile phone number", STATUS_LOCKED);
            throw new ConnectionWorkflowException("error submitting msisdn form", e);
        }
    }

    protected LoginToken waitForToken() throws ConnectionWorkflowException {

        LoginToken loginToken = new LoginToken();

        // set up broadcast receiver
        publishProgress("Unlocking WiFi", "Waiting for login token", STATUS_UNLOCKING);
        SMSReceiver receiver = new SMSReceiver(loginToken);
        IntentFilter intentFilter = new IntentFilter(SMSReceiver.ACTION);
        intentFilter.setPriority(100);
        registerReceiver(receiver, intentFilter);

        int iterations = 1;
        // loop for 10 seconds and wait for SMS arriving
        while (iterations < 20 && !loginToken.isTokenSet()) {

            // just wait, therefore sleep a half second
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // just finish then
                break;
            }

            iterations++;
        }

        // remove broadcast receiver
        unregisterReceiver(receiver);

        if (loginToken.isTokenSet()) {
            publishProgress("Unlocking WiFi", "Received token: " + loginToken.getToken(), STATUS_UNLOCKING);
        } else {
            publishProgress("Unlocking WiFi", "Token did not arrive within 10 seconds, please retry!", STATUS_LOCKED);
            throw new ConnectionWorkflowException("no token received within 10 seconds");
        }

        return loginToken;
    }

    protected void submitToken(HttpClient httpClient, HttpContext localContext, LoginToken loginToken)
            throws ConnectionWorkflowException {
        try {
            // post mobile-number to login page
            publishProgress("Unlocking WiFi", "Submitting token: " + loginToken.getToken(), STATUS_UNLOCKING);
            HttpPost httpPost = new HttpPost("http://wlan.de.telefonica:8001/token.php?l=de");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("token", loginToken.getToken()));
            nameValuePairs.add(new BasicNameValuePair("submit", "Lossurfen &gt;"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            httpClient.execute(httpPost, localContext);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            response.getEntity().consumeContent();
            publishProgress("Unlocking WiFi", "Submitted token: " + loginToken.getToken(), STATUS_UNLOCKING);
        } catch (ClientProtocolException e) {
            publishProgress("Unlocking WiFi", "Could not submit token, please retry", STATUS_LOCKED);
            throw new ConnectionWorkflowException("Error submitting token " + loginToken.getToken(), e);
        } catch (IOException e) {
            publishProgress("Unlocking WiFi", "Could not submit token, please retry", STATUS_LOCKED);
            throw new ConnectionWorkflowException("Error submitting token " + loginToken.getToken(), e);
        }
    }

    protected void logout(HttpClient httpClient) throws ConnectionWorkflowException {
        try {
            publishProgress("Logging off", "Logging off Telefonica WiFi internet access", STATUS_UNLOCKING);
            // post mobile-number to login page
            HttpPost httpPost = new HttpPost("http://wlan.de.telefonica:8001/index.php?l=de");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("exit", "Ja, diese Sitzung jetzt beenden &gt;"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httpPost, localContext);
            // TODO: check response
            response.getEntity().consumeContent();
            publishProgress("TelefonicaPublic locked", "Press connect to unlock internet access", STATUS_LOCKED);
        } catch (ClientProtocolException e) {
            publishProgress("Logging off", "Could not log off, please retry", STATUS_UNLOCKED);
            throw new ConnectionWorkflowException("Error locking session ", e);
        } catch (IOException e) {
            publishProgress("Logging off", "Could not log off, please retry", STATUS_UNLOCKED);
            throw new ConnectionWorkflowException("Error locking session ", e);
        }
    }
}
