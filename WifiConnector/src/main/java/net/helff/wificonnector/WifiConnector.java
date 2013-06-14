package net.helff.wificonnector;

import net.helff.wificonnector.R;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "", mailTo = "martin@helff.net", mode = ReportingInteractionMode.DIALOG, resDialogText = R.string.crash_toast_text)
public class WifiConnector extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		// The following line triggers the initialization of ACRA
		ACRA.init(this);
	}
}