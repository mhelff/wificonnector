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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/*
 * @author helffm
 *
 */

public class LocationData {

    private static Map<String, Location> locations;
    private static Map<String, WifiLocation> wifi;
    private static Collection<Printer> printers;

    static {
    	Gson g = new Gson();
    	
        InputStream is = LocationData.class.getResourceAsStream("/locations.txt");
        Type collectionType = new TypeToken<Collection<Location>>(){}.getType();
        Collection<Location> l = g.fromJson(new InputStreamReader(is), collectionType);
        addAllLocations(l, "GBR");
        
        is = LocationData.class.getResourceAsStream("/wifi.txt");
        collectionType = new TypeToken<Collection<WifiLocation>>(){}.getType();
        Collection<WifiLocation> wl = g.fromJson(new InputStreamReader(is), collectionType);
        addAllWifiLocations(wl);
        
        is = LocationData.class.getResourceAsStream("/printers.txt");
        collectionType = new TypeToken<Collection<Printer>>(){}.getType();
        printers = g.fromJson(new InputStreamReader(is), collectionType);
               
    }
    
    private static void addAllLocations(Collection<Location> ls, String building) {
    	locations = new HashMap<String, Location>();
        for(Location l : ls) {
        	l.setBuilding(building);
            locations.put(l.getId(), l);
        }
    }
    
    private static void addAllWifiLocations(Collection<WifiLocation> ls) {
    	wifi = new HashMap<String, WifiLocation>();
        for(WifiLocation l : ls) {
            wifi.put(l.getId(), l);
        }
    }
    
    public static SortedMap<Integer, Printer> findPrintersAtLocation(Location pos, boolean color, boolean a3, boolean copier) {
    	SortedMap<Integer, Printer> result = new TreeMap<Integer, Printer>();
    	
    	LocationConnectionMap rm = new LocationConnectionMap();
	    DijkstraEngine de = new DijkstraEngine(rm);
    	
    	Collection<String> neededCapabilities = new HashSet<String>();
    	neededCapabilities.add(color ? Printer.CAPABILITY_COLOR : Printer.CAPABILITY_BW);
    	neededCapabilities.add(a3 ? Printer.CAPABILITY_SIZE_A3 : Printer.CAPABILITY_SIZE_A4);
    	if(copier) {
    		neededCapabilities.add(Printer.CAPABILITY_COPY);
    	}
    	
    	// first find all matching printers:
    	for(Printer p : printers) {
    		if(p.hasCapabilities(neededCapabilities)) {
    			// find distance
    			Location l = locations.get(p.getLocation());
    			if(l != null) {
    				de.execute(pos, l);
    				result.put(de.getShortestDistance(l), p);
    			}
    		}
    	}
    	
    	return result;
    }
    
    public static Location getLocation(String id) {
        return locations.get(id);
    }
    
    public static WifiLocation getWifiLocation(String bssid) {
        return wifi.get(bssid);
    }
    
    public static Collection<Printer> getPrinters() {
    	return printers;
    }
    
    public static Location getLocation(int floor, String block, String position) {
        for(Location l : locations.values()) {
            if(l.getFloor() == floor && block.equals(l.getBlock()) && l.getPosition() != null && l.getPosition().startsWith(position)) {
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
