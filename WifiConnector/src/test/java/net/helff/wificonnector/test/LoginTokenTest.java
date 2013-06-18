package net.helff.wificonnector.test;

import junit.framework.TestCase;
import net.helff.wificonnector.LoginToken;

public class LoginTokenTest extends TestCase {

	final String SMS_MSG = "Bitte verwenden sie a1b2c3 als Ihren Telefonica WLAN Token.";
	final String SMS_TOKEN = "a1b2c3";

	public void testExtractToken() throws Exception {

		LoginToken t = new LoginToken();
		String token = t.extractTokenFromSms(SMS_MSG);
		assertEquals(SMS_TOKEN, token);
	}

	public void testExtractTokenNullMessage() throws Exception {

		LoginToken t = new LoginToken();
		String token = t.extractTokenFromSms(null);
		assertNull(token);
	}

	public void testExtractTokenEmptyMessage() throws Exception {

		LoginToken t = new LoginToken();
		String token = t.extractTokenFromSms("");
		assertNull(token);
	}
	
	public void testExtractTokenWrongMessage() throws Exception {

		LoginToken t = new LoginToken();
		String token = t.extractTokenFromSms("Hallo hier ist ihr Token; as34sd");
		assertNull(token);
	}

}
