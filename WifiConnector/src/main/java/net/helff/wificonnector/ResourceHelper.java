package net.helff.wificonnector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ResourceHelper {
	
	private static Gson g = new Gson();
	
	public static Collection<Location> getLocations() {
		InputStream is = getResourceInputStream("locations.txt");
		
        Type collectionType = new TypeToken<Collection<Location>>(){}.getType();
        return g.fromJson(new InputStreamReader(is), collectionType);
	}
	
	public static Collection<WifiLocation> getWifiLocations() {
		InputStream is = getResourceInputStream("wifi.txt");
		
        Type collectionType = new TypeToken<Collection<WifiLocation>>(){}.getType();
        return g.fromJson(new InputStreamReader(is), collectionType);
	}
	
	public static Collection<Printer> getPrinters() {
		InputStream is = getResourceInputStream("printers.txt");
		
        Type collectionType = new TypeToken<Collection<Printer>>(){}.getType();
        return g.fromJson(new InputStreamReader(is), collectionType);
	}
	
	public static Collection<LocationConnection> getRoutes() {
		InputStream is = getResourceInputStream("distances.txt");
		
        Type collectionType = new TypeToken<Collection<LocationConnection>>(){}.getType();
        return g.fromJson(new InputStreamReader(is), collectionType);
	}
	
	protected static InputStream getResourceInputStream(String resource) {
		
		if(WifiConnectorActivity.ctx != null) {
			try {
				return WifiConnectorActivity.ctx.openFileInput(resource);
			} catch (FileNotFoundException e) {
				// ignore, no update file saved
			}
		}
		
		return ResourceHelper.class.getResourceAsStream("/" + resource);
	}
	
	public Collection<LatestVersion> getLatestVersions() {
		InputStream is = getResourceInputStream("latestversions.txt");
		
        Type collectionType = new TypeToken<Collection<LatestVersion>>(){}.getType();
        return g.fromJson(new InputStreamReader(is), collectionType);	
	}
	
	public Collection<VersionHistoryItem> updateVersionHistory() {
		BufferedReader reader = null;
        
		HttpClient httpClient = new DefaultHttpClient();

        try {
            HttpGet httpGet = new HttpGet("http://www.helff.net/wificonnector/versions.txt");
            HttpResponse response = httpClient.execute(httpGet, new BasicHttpContext());
            String result = "";

            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line + "\n";
            }
            reader.close();
            response.getEntity().consumeContent();
        } catch (Exception e) {
           // just log the error - next time next try...
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("", "Could not close reader", e);
                }
            }
        }
        return null;
	}
 
}
