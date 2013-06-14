package net.helff.wificonnector.test;

import junit.framework.TestCase;
import net.helff.wificonnector.SMSReceiver;

public class SMSReceiverTest extends TestCase {

	final String SMS_MSG = "Bitte verwenden sie a1b2c3 als Ihren Telefonica WLAN Token.";
	final String SMS_TOKEN = "a1b2c3";

	public void testExtractToken() throws Exception {

		SMSReceiver r = new SMSReceiver(null);
		String token = r.extractToken(SMS_MSG);
		assertEquals(SMS_TOKEN, token);
	}

	public void testExtractTokenNullMessage() throws Exception {

		SMSReceiver r = new SMSReceiver(null);
		String token = r.extractToken(null);
		assertNull(token);
	}

	public void testExtractTokenEmptyMessage() throws Exception {

		SMSReceiver r = new SMSReceiver(null);
		String token = r.extractToken("");
		assertNull(token);
	}
	
	public void testExtractTokenWrongMessage() throws Exception {

		SMSReceiver r = new SMSReceiver(null);
		String token = r.extractToken("Hallo hier ist ihr Token; as34sd");
		assertNull(token);
	}

}
