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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

    public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private LoginToken token;
    
    public SMSReceiver(LoginToken t) {
        token = t;
    }
    
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Bundle bundle = intent.getExtras();
            
            Object messages[] = (Object[]) bundle.get("pdus");
            for (int n = 0; n < messages.length; n++) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) messages[n]);
                // TODO: check from address
                // now parse message and try to set token
                token.setToken(smsMessage.getMessageBody());
            }
        }
    }
}
