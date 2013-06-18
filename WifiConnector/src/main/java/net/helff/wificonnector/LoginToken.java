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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginToken {
    
    public static final String TOKEN_NOT_SET = "not-set";
    
    private static final Pattern TOKEN_MSG_REGEX = Pattern
			.compile("Bitte verwenden sie ([A-Z,a-z,0-9]+) als Ihren Telefonica WLAN Token.");
    
    private String token;
    
    public LoginToken() {
        reset();
    }
    
    public void setToken(String t) {
        token = String.valueOf(t);
    }
    
    public String getToken() {
        return token;
    }
    
    public boolean isTokenSet() {
        return !TOKEN_NOT_SET.equals(token);
    }
    
    public void reset() {
        token = String.valueOf(TOKEN_NOT_SET);
    }
    
    public String extractTokenFromSms(String msg) {
    	String splitString = null;
    	
		if (msg != null) {
			Matcher m = TOKEN_MSG_REGEX.matcher(msg);
			if (m.matches() && m.groupCount() == 1) {
				splitString = m.group(1);
				if (splitString != null) {
					token = splitString;
				}
			}
		}
		return splitString;
	}
}
