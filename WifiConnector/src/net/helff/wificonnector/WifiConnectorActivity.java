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

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author mhelff
 * 
 */
public class WifiConnectorActivity extends Activity {
    private static WifiConnectorActivity mInst;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button b = (Button)this.findViewById(R.id.connectButton);
        b.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                new LoginTask().execute("+491791004418");
            }
            
        });
        Button b1 = (Button)this.findViewById(R.id.disconnectButton);
        b1.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                new LogoutTask().execute("+491791004418");
            }
            
        });
    }

    public static WifiConnectorActivity instance() {
        return mInst;
    }

    public void addViewText(String text) {
        TextView tvValue = (TextView) this.findViewById(R.id.editText1);
        tvValue.append(text + "\n");
    }

    @Override
    public void onStart() {
        super.onStart();
        mInst = this;
    }

    @Override
    public void onStop() {
        mInst = null;
        super.onStop();
    }

    private class LoginTask extends AsyncTask<String, String, String> {

        private Context applicationContext;
        private WifiManager wifiManager;
        private HttpClient httpClient = new DefaultHttpClient();
        private HttpContext localContext = new BasicHttpContext();
        private LoginToken loginToken = new LoginToken();

        @Override
        protected void onPreExecute() {
            applicationContext = getApplicationContext();
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }

        protected String doInBackground(String... mobileNumber) {
            // check if WiFi is ours
            checkWifi(wifiManager);
            if (isCancelled())
                return null;

            // check if network is already unlocked
            checkConnectivity(httpClient, true);
            if (isCancelled())
                return null;

            // post mobile-number to login page
            submitMSISDN(httpClient, mobileNumber[0]);
            if (isCancelled())
                return null;

            // wait for SMS to arrive and parse token
            waitForSMS();
            if (isCancelled())
                return null;
            
            // post token to confirmation page
            submitToken(httpClient, loginToken);
            if (isCancelled())
                return null;
            
            //try {
			//	Thread.sleep(1000);
			//} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
            // check connectivity to google or other page
            checkConnectivity(httpClient, false);
            return loginToken.getToken();
        }

        protected void onProgressUpdate(String... progress) {
            addViewText(progress[0]);
        }

        protected void onPostExecute(String result) {
            if (result != null)
                addViewText(result);
        }
        
        @Override
        protected void onCancelled() {
            // remove BroadcastReceiver if left over
        }

        protected void checkWifi(WifiManager wifiManager) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo == null || !"TelefonicaPublic".equals(wifiInfo.getSSID())) {
                // post error
                publishProgress("Failure, not connected to TelefonicaPublic");
                cancel(false);      
            } else {
                publishProgress("Connected to TelefonicaPublic");
            }
        }

        protected void checkConnectivity(HttpClient httpClient, boolean beforeUnlock) {
            try {
                HttpGet httpGet = new HttpGet("http://www.helff.net");
                HttpResponse response = httpClient.execute(httpGet, localContext);
                String result = "";

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result += line + "\n";
                }
                if (result.contains("<title>helff.net</title>")) {
                    if(beforeUnlock) {
                        publishProgress("Network is already unlocked, start surfing!");
                    cancel(false);
                    } else {
                        publishProgress("Network is now unlocked, start surfing!");
                    }
                } else {
                    if(!beforeUnlock) {
                        publishProgress("Network is not unlocked, something failed!");
                    }
                }
                reader.close();
                response.getEntity().consumeContent();
            } catch (ClientProtocolException e) {
                publishProgress("Error checking network status, please retry");
                Log.e(WifiConnectorActivity.class.getName(), "Network check failed1", e);
                cancel(false);
            } catch (IOException e) {
                publishProgress("Error checking network status, please retry");
                Log.e(WifiConnectorActivity.class.getName(), "Network check failed2", e);
                cancel(false);
            }
        }

        protected void submitMSISDN(HttpClient httpClient, String msisdn) {
            try {
                // post mobile-number to login page
                HttpPost httpPost = new HttpPost("http://wlan.de.telefonica:8001/login.php?l=de");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("handynr", msisdn));
                nameValuePairs.add(new BasicNameValuePair("login", "Token per SMS zusenden &gt;"));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpClient.execute(httpPost, localContext);
                response.getEntity().consumeContent();
                publishProgress("Submitted MSISDN: " + msisdn);
            } catch (ClientProtocolException e) {
                publishProgress("Could not submit sign-in form, please retry");
                cancel(false);
            } catch (IOException e) {
                publishProgress("Could not submit sign-in form, please retry");
                cancel(false);
            }
        }
        
        protected void waitForSMS() {
            
            // set up broadcast receiver
            SMSReceiver receiver = new SMSReceiver(loginToken);
            applicationContext.registerReceiver(receiver, new IntentFilter(SMSReceiver.ACTION));
            
            int iterations = 1;
            // loop for 10 seconds and wait for SMS arriving
            while(iterations < 20 && !this.isCancelled()) {
                if(loginToken.isTokenSet()) {
                    break;
                }
                
                // sleep a second
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // just finish then
                    break;
                }
                
                iterations++;
            }
            
            // remove broadcast receiver
            applicationContext.unregisterReceiver(receiver);
        }
        
        protected void submitToken(HttpClient httpClient, LoginToken token) {
            try {
                // post mobile-number to login page
                HttpPost httpPost = new HttpPost("http://wlan.de.telefonica:8001/token.php?l=de");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("token", token.getToken()));
                nameValuePairs.add(new BasicNameValuePair("submit", "Lossurfen &gt;"));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                httpClient.execute(httpPost, localContext);
                HttpResponse response = httpClient.execute(httpPost, localContext);
                response.getEntity().consumeContent();
                publishProgress("Submitted token: " + token.getToken());
            } catch (ClientProtocolException e) {
                publishProgress("Could not submit sign-in form, please retry");
                cancel(false);
            } catch (IOException e) {
                publishProgress("Could not submit sign-in form, please retry");
                cancel(false);
            }
        }
    }
    
    private class LogoutTask extends AsyncTask<String, String, String> {

        private Context applicationContext;
        private WifiManager wifiManager;
        private HttpClient httpClient = new DefaultHttpClient();
        private HttpContext localContext = new BasicHttpContext();

        @Override
        protected void onPreExecute() {
            applicationContext = getApplicationContext();
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }

        protected String doInBackground(String... mobileNumber) {
            // check if WiFi is ours
            checkWifi(wifiManager);
            if (isCancelled())
                return null;

            // check connectivity to google or other page
            logout(httpClient, "");
            return "Disconnected";
        }

        protected void onProgressUpdate(String... progress) {
            addViewText(progress[0]);
        }

        protected void onPostExecute(String result) {
            if (result != null)
                addViewText(result);
        }
        
        @Override
        protected void onCancelled() {
            // remove BroadcastReceiver if left over
        }

        protected void checkWifi(WifiManager wifiManager) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo == null || !"TelefonicaPublic".equals(wifiInfo.getSSID())) {
                // post error
                publishProgress("Failure, not connected to TelefonicaPublic");
                cancel(false);      
            } else {
                publishProgress("Connected to TelefonicaPublic");
            }
        }

        protected void logout(HttpClient httpClient, String msisdn) {
            try {
                // post mobile-number to login page
                HttpPost httpPost = new HttpPost("http://wlan.de.telefonica:8001/index.php?l=de");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("exit", "Ja, diese Sitzung jetzt beenden &gt;"));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpClient.execute(httpPost, localContext);
                response.getEntity().consumeContent();
            } catch (ClientProtocolException e) {
                publishProgress("Could not submit sign-in form, please retry");
                cancel(false);
            } catch (IOException e) {
                publishProgress("Could not submit sign-in form, please retry");
                cancel(false);
            }
        }        
   }
}