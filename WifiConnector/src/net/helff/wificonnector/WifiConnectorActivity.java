package net.helff.wificonnector;

import android.app.Activity;
import android.os.Bundle;
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
    }

    public static WifiConnectorActivity instance() {
              return mInst;
    }

    public void addViewText(String text) {
    	TextView tvValue = (TextView)this.findViewById(R.id.editText1);
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
}