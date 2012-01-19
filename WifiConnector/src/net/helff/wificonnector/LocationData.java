/* 
 * Copyright (C) 2012 Martin Helff, Florin Buda
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

import java.util.HashMap;
import java.util.Map;

public class LocationData {

    private static Map<String, Location> locations = new HashMap<String, Location>();

    static {    
        addLocation("00:1a:30:bb:ed:c0", "C", 0, "Meet & Eat");
        addLocation("00:19:a9:cc:f9:b0", "A", 0, "Near C");
        addLocation("00:19:a9:cc:f8:50", "C", 0, "1. from A");
        addLocation("00:19:a9:cc:f9:30", "C", 0, "Poststelle");
        addLocation("00:19:a9:cc:f5:10", "A", 0, "Near E");
        addLocation("00:0f:90:f9:ad:00", "E", 0, "1. from A");
        
        addLocation("00:19:a9:cc:f7:d0", "A", 1, "Near D");
        addLocation("00:19:a9:ce:01:30", "A", 1, "Near C");
        addLocation("00:11:92:f8:9f:40", "A", 1, "Near E");
        addLocation("00:0f:90:72:3c:80", "E", 1, "1. from A");
        addLocation("00:11:92:f4:21:e0", "E", 1, "2. from A");
        
        addLocation("00:12:43:48:10:c0", "E", 2, "2. from A");
        addLocation("00:13:1a:96:12:f0", "E", 2, "1. from A");
        addLocation("00:13:1a:96:21:80", "A", 2, "Near E");
        addLocation("00:19:a9:cd:ff:20", "A", 2, "Near D");
        addLocation("00:19:a9:cc:fb:60", "A", 2, "Near C");
        addLocation("00:0f:90:f9:d0:10", "D", 2, "1. from A");
        addLocation("00:19:a9:cd:e2:20", "D", 2, "2. from A");
        
        addLocation("00:19:a9:cc:f9:f0", "E", 3, "2. from A");
        addLocation("00:19:a9:cd:02:40", "E", 3, "1. from A");
        addLocation("00:19:a9:cd:f8:10", "A", 3, "Near E");
        addLocation("00:19:a9:cd:de:90", "A", 3, "Near D");
        addLocation("00:19:a9:cd:e9:20", "A", 3, "Near C");
        addLocation("00:19:a9:cd:03:30", "D", 3, "1. from A");
        addLocation("00:19:a9:cd:f5:80", "D", 3, "2. from A");
        addLocation("00:19:a9:cd:0a:c0", "C", 3, "1. from A");
        addLocation("00:19:a9:cd:f7:00", "C", 3, "2. from A");
        
        addLocation("00:19:a9:cd:fe:e0", "E", 4, "1. from A");
        addLocation("00:19:a9:cd:01:e0", "E", 4, "2. from A");
        addLocation("00:19:a9:cd:e4:e0", "A", 4, "Near E");
        addLocation("00:19:a9:ce:00:c0", "A", 4, "Near D");
        addLocation("00:19:a9:cd:04:60", "A", 4, "Near C");
        addLocation("00:19:a9:cd:fa:80", "D", 4, "1. from A");
        addLocation("00:19:a9:cc:f2:30", "D", 4, "2. from A");
        addLocation("00:19:a9:cd:15:60", "C", 4, "1. from A");
        addLocation("00:19:a9:cc:f8:c0", "C", 4, "2. from A");
        
        addLocation("00:1d:46:7d:c4:f0", "C", 5, "1. from A");
        addLocation("00:19:a9:cd:02:70", "C", 5, "1. from A");
        addLocation("00:12:43:48:0e:60", "A", 5, "Near C");
        addLocation("00:0f:90:94:68:30", "A", 5, "Near D");
        addLocation("00:11:20:70:4c:60", "A", 5, "Near E");
        addLocation("00:19:a9:cd:02:00", "D", 5, "1. from A");
        addLocation("00:19:a9:cc:f4:d0", "D", 5, "2. from A");
        addLocation("00:19:a9:cd:0d:60", "E", 5, "1. from A");
        addLocation("00:19:a9:cd:07:40", "E", 5, "2. from A");
        
        addLocation("00:19:a9:cc:f5:80", "E", 6, "1. from A");
        addLocation("00:19:a9:cc:f1:f0", "E", 6, "2. from A");
        addLocation("00:0f:90:94:64:30", "A", 6, "Near E");
        addLocation("00:0f:90:94:64:c0", "A", 6, "Near D");
        addLocation("00:19:a9:cc:f3:30", "A", 6, "Near C");
        addLocation("00:19:a9:cd:f0:f0", "C", 6, "1. from A");
        addLocation("00:19:a9:cd:0a:90", "C", 6, "2. from A");
        addLocation("00:19:a9:cc:fa:50", "D", 6, "1. from A");
        addLocation("00:19:a9:cc:f7:20", "D", 6, "2. from A");
        
        addLocation("00:19:a9:cc:f9:50", "E", 7, "2. from A");
        addLocation("00:19:a9:cc:f7:e0", "E", 7, "1. from A");
        addLocation("00:19:a9:cd:fc:60", "A", 7, "Near E");
        addLocation("00:0f:90:f9:b2:20", "A", 7, "Near D");
        addLocation("00:11:5c:1b:a4:f0", "A", 7, "Near C");
        addLocation("00:0f:90:93:ef:e0", "D", 7, "1. from A");
        addLocation("00:0f:90:94:65:30", "D", 7, "2. from A");
        addLocation("00:11:21:e0:b9:20", "C", 7, "1. from A");
        addLocation("00:11:21:e0:bc:00", "C", 7, "2. from A");
        
        addLocation("00:19:a9:cc:f2:d0", "E", 8, "2. from A");
        addLocation("00:19:a9:cc:f5:50", "E", 8, "1. from A");
        addLocation("00:19:a9:cd:fc:20", "A", 8, "Near E");
        addLocation("00:1a:30:bb:ea:80", "A", 8, "Near D");
        addLocation("00:1a:30:bb:ea:e0", "A", 8, "Near C");
        addLocation("00:19:a9:cd:fd:40", "D", 8, "1. from A");
        addLocation("00:19:a9:cc:f6:70", "D", 8, "2. from A");
        addLocation("00:1a:30:bb:97:80", "C", 8, "1. from A");
        addLocation("00:1a:30:bb:96:50", "C", 8, "2. from A");
        
        // helffm home
        addLocation("00:1c:4a:07:a2:6d", "M", 2, "Dahoam");
    }

    private static void addLocation(String bssid, String block, int floor, String position) {
        locations.put(bssid, new Location(bssid, floor, block, position));
    }
    
    public static Location getLocation(String bssid) {
        return locations.get(bssid);
    }

    public static class Location {
        private final String bssid;
        private final int floor;
        private final String block;
        private final String position;

        protected Location(String bssid, int floor, String block, String position) {
            this.bssid = bssid;
            this.floor = floor;
            this.block = block;
            this.position = position;
        }

        public String getBssid() {
            return bssid;
        }

        public int getFloor() {
            return floor;
        }

        public String getBlock() {
            return block;
        }

        public String getPosition() {
            return position;
        }
    }
}
