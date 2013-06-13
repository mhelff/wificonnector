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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LocationData {

    private static Map<String, Location> locations = new HashMap<String, Location>();

    static {
        InputStream is = LocationData.class.getResourceAsStream("/gbr.locations");
        Gson g = new Gson();
        Type collectionType = new TypeToken<Collection<Location>>(){}.getType();
        Collection<Location> l = g.fromJson(new InputStreamReader(is), collectionType);
        addAllLocations(l);
        /*addLocation("00:1a:30:bb:ed:c0", "C", 0, "Meet & Eat");
        addLocation("00:19:a9:cc:f9:b0", "A", 0, "West");
        addLocation("00:19:a9:cc:f8:50", "C", 0, "Nord");
        addLocation("00:19:a9:cc:f9:30", "C", 0, "Poststelle");
        addLocation("00:19:a9:cc:f5:10", "A", 0, "Ost");
        addLocation("00:0f:90:f9:ad:00", "E", 0, "Nord");
        
        addLocation("00:19:a9:cc:f7:d0", "A", 0, "Empfang"); // this is 1. Floor Near D
        addLocation("00:19:a9:ce:01:30", "A", 1, "West");
        addLocation("00:11:92:f8:9f:40", "A", 1, "Ost");
        addLocation("00:0f:90:72:3c:80", "E", 1, "Nord");
        addLocation("00:11:92:f4:21:e0", "E", 1, "SŸd");
        addLocation("00:0f:90:93:ee:a0", "D", 1, "Nord");
        addLocation("00:0f:90:94:67:90", "D", 1, "SŸd");
        addLocation("00:19:a9:cc:f7:90", "C", 1, "Nord");
        addLocation("00:19:a9:cd:03:80", "C", 1, "SŸd");
        
        addLocation("00:12:43:48:10:c0", "E", 2, "SŸd");
        addLocation("00:13:1a:96:12:f0", "E", 2, "Nord");
        addLocation("00:13:1a:96:21:80", "A", 2, "Ost");
        addLocation("00:19:a9:cd:ff:20", "A", 2, "Mitte");
        addLocation("00:19:a9:cc:fb:60", "A", 2, "West");
        addLocation("00:0f:90:f9:d0:10", "D", 2, "Nord");
        addLocation("00:19:a9:cd:e2:20", "D", 2, "SŸd");
        
        addLocation("00:19:a9:cc:f9:f0", "E", 3, "SŸd");
        addLocation("00:19:a9:cd:02:40", "E", 3, "Nord");
        addLocation("00:19:a9:cd:f8:10", "A", 3, "Ost");
        addLocation("00:19:a9:cd:de:90", "A", 3, "Mitte");
        addLocation("00:19:a9:cd:e9:20", "A", 3, "West");
        addLocation("00:19:a9:cd:03:30", "D", 3, "Nord");
        addLocation("00:19:a9:cd:f5:80", "D", 3, "SŸd");
        addLocation("00:19:a9:cd:0a:c0", "C", 3, "Nord");
        addLocation("00:19:a9:cd:f7:00", "C", 3, "SŸd");
        
        addLocation("00:19:a9:cd:fe:e0", "E", 4, "Nord");
        addLocation("00:19:a9:cd:01:e0", "E", 4, "SŸd");
        addLocation("00:19:a9:cd:e4:e0", "A", 4, "Ost");
        addLocation("00:19:a9:ce:00:c0", "A", 4, "Mitte");
        addLocation("00:19:a9:cd:04:60", "A", 4, "West");
        addLocation("00:19:a9:cd:fa:80", "D", 4, "Nord");
        addLocation("00:19:a9:cc:f2:30", "D", 4, "SŸd");
        addLocation("00:19:a9:cd:15:60", "C", 4, "Nord");
        addLocation("00:19:a9:cc:f8:c0", "C", 4, "SŸd");
        
        addLocation("00:1d:46:7d:c4:f0", "C", 5, "Nord");
        addLocation("00:19:a9:cd:02:70", "C", 5, "Nord");
        addLocation("00:12:43:48:0e:60", "A", 5, "West");
        addLocation("00:0f:90:94:68:30", "A", 5, "Mitte");
        addLocation("00:11:20:70:4c:60", "A", 5, "Ost");
        addLocation("00:19:a9:cd:02:00", "D", 5, "Nord");
        addLocation("00:19:a9:cc:f4:d0", "D", 5, "SŸd");
        addLocation("00:19:a9:cd:0d:60", "E", 5, "Nord");
        addLocation("00:19:a9:cd:07:40", "E", 5, "SŸd");
        
        addLocation("00:19:a9:cc:f5:80", "E", 6, "Nord");
        addLocation("00:19:a9:cc:f1:f0", "E", 6, "SŸd");
        addLocation("00:0f:90:94:64:30", "A", 6, "Ost");
        addLocation("00:0f:90:94:64:c0", "A", 6, "Mitte");
        addLocation("00:19:a9:cc:f3:30", "A", 6, "West");
        addLocation("00:19:a9:cd:f0:f0", "C", 6, "Nord");
        addLocation("00:19:a9:cd:0a:90", "C", 6, "SŸd");
        addLocation("00:19:a9:cc:fa:50", "D", 6, "Nord");
        addLocation("00:19:a9:cc:f7:20", "D", 6, "SŸd");
        
        addLocation("00:19:a9:cc:f9:50", "E", 7, "SŸd");
        addLocation("00:19:a9:cc:f7:e0", "E", 7, "Nord");
        addLocation("00:19:a9:cd:fc:60", "A", 7, "Ost");
        addLocation("00:0f:90:f9:b2:20", "A", 7, "Mitte");
        addLocation("00:11:5c:1b:a4:f0", "A", 7, "West");
        addLocation("00:0f:90:93:ef:e0", "D", 7, "Nord");
        addLocation("00:0f:90:94:65:30", "D", 7, "SŸd");
        addLocation("00:11:21:e0:b9:20", "C", 7, "Nord");
        addLocation("00:11:21:e0:bc:00", "C", 7, "SŸd");
        
        addLocation("00:19:a9:cc:f2:d0", "E", 8, "SŸd");
        addLocation("00:19:a9:cc:f5:50", "E", 8, "Nord");
        addLocation("00:19:a9:cd:fc:20", "A", 8, "Ost");
        addLocation("00:1a:30:bb:ea:80", "A", 8, "Mitte");
        addLocation("00:1a:30:bb:ea:e0", "A", 8, "West");
        addLocation("00:19:a9:cd:fd:40", "D", 8, "Nord");
        addLocation("00:19:a9:cc:f6:70", "D", 8, "SŸd");
        addLocation("00:1a:30:bb:97:80", "C", 8, "Nord");
        addLocation("00:1a:30:bb:96:50", "C", 8, "SŸd"); */
        
    }
 /*[{"id":"00:0f:90:94:64:30","type":1,"floor":6,"block":"A","position":"Ost","building":"GBR"},
   {"id":"00:19:a9:cc:f5:80","type":1,"floor":6,"block":"E","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cd:f5:80","type":1,"floor":3,"block":"D","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cc:f4:d0","type":1,"floor":5,"block":"D","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cc:f1:f0","type":1,"floor":6,"block":"E","position":"SŸd","building":"GBR"},
   {"id":"00:11:21:e0:bc:00","type":1,"floor":7,"block":"C","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cd:04:60","type":1,"floor":4,"block":"A","position":"West","building":"GBR"},
   {"id":"00:19:a9:cd:fc:20","type":1,"floor":8,"block":"A","position":"Ost","building":"GBR"},
   {"id":"00:11:5c:1b:a4:f0","type":1,"floor":7,"block":"A","position":"West","building":"GBR"},
   {"id":"00:12:43:48:0e:60","type":1,"floor":5,"block":"A","position":"West","building":"GBR"},
   {"id":"00:1a:30:bb:ea:e0","type":1,"floor":8,"block":"A","position":"West","building":"GBR"},
   {"id":"00:0f:90:93:ef:e0","type":1,"floor":7,"block":"D","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cd:0a:c0","type":1,"floor":3,"block":"C","position":"Nord","building":"GBR"},
   {"id":"00:1d:46:7d:c4:f0","type":1,"floor":5,"block":"C","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cc:fb:60","type":1,"floor":2,"block":"A","position":"West","building":"GBR"},
   {"id":"00:19:a9:cc:f9:50","type":1,"floor":7,"block":"E","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cc:f2:30","type":1,"floor":4,"block":"D","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cd:0d:60","type":1,"floor":5,"block":"E","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cd:ff:20","type":1,"floor":2,"block":"A","position":"Mitte","building":"GBR"},
   {"id":"00:0f:90:72:3c:80","type":1,"floor":1,"block":"E","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cc:f3:30","type":1,"floor":6,"block":"A","position":"West","building":"GBR"},
   {"id":"00:19:a9:cd:fc:60","type":1,"floor":7,"block":"A","position":"Ost","building":"GBR"},
   {"id":"00:19:a9:ce:00:c0","type":1,"floor":4,"block":"A","position":"Mitte","building":"GBR"},
   {"id":"00:11:92:f8:9f:40","type":1,"floor":1,"block":"A","position":"Ost","building":"GBR"},
   {"id":"00:19:a9:cc:f7:d0","type":1,"floor":0,"block":"A","position":"Empfang","building":"GBR"},
   {"id":"00:19:a9:cd:07:40","type":1,"floor":5,"block":"E","position":"SŸd","building":"GBR"},
   {"id":"00:11:21:e0:b9:20","type":1,"floor":7,"block":"C","position":"Nord","building":"GBR"},
   {"id":"00:1a:30:bb:96:50","type":1,"floor":8,"block":"C","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cc:f5:50","type":1,"floor":8,"block":"E","position":"Nord","building":"GBR"},
   {"id":"00:1a:30:bb:97:80","type":1,"floor":8,"block":"C","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cd:e9:20","type":1,"floor":3,"block":"A","position":"West","building":"GBR"},
   {"id":"00:19:a9:cd:f7:00","type":1,"floor":3,"block":"C","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cd:01:e0","type":1,"floor":4,"block":"E","position":"SŸd","building":"GBR"},
   {"id":"00:13:1a:96:12:f0","type":1,"floor":2,"block":"E","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cc:f7:e0","type":1,"floor":7,"block":"E","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cd:02:70","type":1,"floor":5,"block":"C","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cc:f6:70","type":1,"floor":8,"block":"D","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cd:e2:20","type":1,"floor":2,"block":"D","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cd:15:60","type":1,"floor":4,"block":"C","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cc:f5:10","type":1,"floor":0,"block":"A","position":"Ost","building":"GBR"},
   {"id":"00:0f:90:94:65:30","type":1,"floor":7,"block":"D","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:ce:01:30","type":1,"floor":1,"block":"A","position":"West","building":"GBR"},
   {"id":"00:19:a9:cc:f7:90","type":1,"floor":1,"block":"C","position":"Nord","building":"GBR"},
   {"id":"00:1a:30:bb:ea:80","type":1,"floor":8,"block":"A","position":"Mitte","building":"GBR"},
   {"id":"00:19:a9:cc:f8:c0","type":1,"floor":4,"block":"C","position":"SŸd","building":"GBR"},
   {"id":"00:0f:90:94:64:c0","type":1,"floor":6,"block":"A","position":"Mitte","building":"GBR"},
   {"id":"00:13:1a:96:21:80","type":1,"floor":2,"block":"A","position":"Ost","building":"GBR"},
   {"id":"00:19:a9:cc:f9:b0","type":1,"floor":0,"block":"A","position":"West","building":"GBR"},
   {"id":"00:19:a9:cd:fd:40","type":1,"floor":8,"block":"D","position":"Nord","building":"GBR"},
   {"id":"00:12:43:48:10:c0","type":1,"floor":2,"block":"E","position":"SŸd","building":"GBR"},
   {"id":"00:11:20:70:4c:60","type":1,"floor":5,"block":"A","position":"Ost","building":"GBR"},
   {"id":"00:19:a9:cc:f9:30","type":1,"floor":0,"block":"C","position":"Poststelle","building":"GBR"},
   {"id":"00:19:a9:cc:f9:f0","type":1,"floor":3,"block":"E","position":"SŸd","building":"GBR"},
   {"id":"00:11:92:f4:21:e0","type":1,"floor":1,"block":"E","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cd:02:40","type":1,"floor":3,"block":"E","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cd:03:80","type":1,"floor":1,"block":"C","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cd:f0:f0","type":1,"floor":6,"block":"C","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cd:03:30","type":1,"floor":3,"block":"D","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cc:f2:d0","type":1,"floor":8,"block":"E","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cd:f8:10","type":1,"floor":3,"block":"A","position":"Ost","building":"GBR"},
   {"id":"00:19:a9:cd:de:90","type":1,"floor":3,"block":"A","position":"Mitte","building":"GBR"},
   {"id":"00:19:a9:cc:f7:20","type":1,"floor":6,"block":"D","position":"SŸd","building":"GBR"},
   {"id":"00:1a:30:bb:ed:c0","type":1,"floor":0,"block":"C","position":"Meet \u0026 Eat","building":"GBR"},
   {"id":"00:19:a9:cc:fa:50","type":1,"floor":6,"block":"D","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cd:0a:90","type":1,"floor":6,"block":"C","position":"SŸd","building":"GBR"},
   {"id":"00:19:a9:cd:02:00","type":1,"floor":5,"block":"D","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cd:fe:e0","type":1,"floor":4,"block":"E","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cc:f8:50","type":1,"floor":0,"block":"C","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cd:fa:80","type":1,"floor":4,"block":"D","position":"Nord","building":"GBR"},
   {"id":"00:0f:90:94:67:90","type":1,"floor":1,"block":"D","position":"SŸd","building":"GBR"},
   {"id":"00:0f:90:f9:ad:00","type":1,"floor":0,"block":"E","position":"Nord","building":"GBR"},
   {"id":"00:0f:90:93:ee:a0","type":1,"floor":1,"block":"D","position":"Nord","building":"GBR"},
   {"id":"00:0f:90:94:68:30","type":1,"floor":5,"block":"A","position":"Mitte","building":"GBR"},
   {"id":"00:0f:90:f9:d0:10","type":1,"floor":2,"block":"D","position":"Nord","building":"GBR"},
   {"id":"00:19:a9:cd:e4:e0","type":1,"floor":4,"block":"A","position":"Ost","building":"GBR"},
   {"id":"00:0f:90:f9:b2:20","type":1,"floor":7,"block":"A","position":"Mitte","building":"GBR"}]

*/
    private static void addLocation(String bssid, String block, int floor, String position) {
        locations.put(bssid, new Location(bssid, Location.TYPE_WIFI, floor, block, position));
    }
    
    private static void addAllLocations(Collection<Location> ls) {
        for(Location l : ls) {
            locations.put(l.getId(), l);
        }
    }
    
    public static Location getLocation(String bssid) {
        return locations.get(bssid);
    }
    
    public static Location getLocation(int floor, String block, String position) {
        for(Location l : locations.values()) {
            if(l.getFloor() == floor && l.getBlock().equals(block) && l.getPosition().startsWith(position)) {
                return l;
            }
        }
        
        return null;
    }
    
    public static Collection<Location> getLocations() {
        return locations.values();
    }
    
    public static String getJson() {
        Gson g = new Gson();
        return g.toJson(locations.values());
    }
}
