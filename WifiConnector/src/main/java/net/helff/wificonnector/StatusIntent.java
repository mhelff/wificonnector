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

import android.content.Intent;

public class StatusIntent extends Intent {

    public static final String INTENT_STATUS_NOTIFICATION = "net.helff.wificonnector.CONNECTION_STATUS";
    
    public static final String EXTRA_MAIN_STATUS = "mainStatus";
    public static final String EXTRA_DETAIL_STATUS = "detailStatus";
    public static final String EXTRA_STATUS_CODE = "statusCode";
    
    public StatusIntent(String mainStatus, String detailStatus, int statusCode) {
        setAction(INTENT_STATUS_NOTIFICATION);
        putExtra(EXTRA_MAIN_STATUS, mainStatus);
        putExtra(EXTRA_DETAIL_STATUS, detailStatus);
        putExtra(EXTRA_STATUS_CODE, statusCode);
    }
}
