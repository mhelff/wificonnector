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

import android.content.Context;

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
    private static Map<Location, Map<Location, Integer>> distances;
    
    public static void init() {
    	Collection<Location> l = ResourceHelper.getLocations();
    	addAllLocations(l, "GBR");
    	
    	Collection<WifiLocation> wl = ResourceHelper.getWifiLocations();
    	addAllWifiLocations(wl);
    	
    	printers = ResourceHelper.getPrinters();
    	
    	distances = new HashMap<Location, Map<Location, Integer>>();
    	Collection<LocationConnection> lc = ResourceHelper.getRoutes();
        for (LocationConnection c : lc) {
            addDirectRoute(LocationData.getLocation(c.getStart()), LocationData.getLocation(c.getEnd()), c.getDistance());
        }
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
    	
    	LocationData rm = new LocationData();
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
    
    /**
     * @return the distance between the two locations, or 0 if no path exists.
     */
    public int getDistance(Location start, Location end) {
        Map<Location, Integer> distanceMap = distances.get(start);

        return (distanceMap != null && distanceMap.containsKey(end)) ? distanceMap.get(end).intValue() : 0;
    }
    
    /**
     * Link two locations by a direct route with the given distance.
     */
    private static void addDirectRoute(Location start, Location end, int distance) {
        Map<Location, Integer> distanceMap = distances.get(start);
        if (distanceMap == null) {
            distanceMap = new HashMap<Location, Integer>();
            distances.put(start, distanceMap);
        }
        distanceMap.put(end, Integer.valueOf(distance));
        // now vice versa
        distanceMap = distances.get(end);
        if (distanceMap == null) {
            distanceMap = new HashMap<Location, Integer>();
            distances.put(end, distanceMap);
        }
        distanceMap.put(start, Integer.valueOf(distance));
    }

    /**
     * @return the list of all valid destinations from the given Location.
     */
    public Set<Location> getDestinations(Location location) {
        return distances.get(location) != null ? distances.get(location).keySet() : new HashSet<Location>();
    }
    
    public static Location getLocation(String id) {
        return locations.get(id);
    }
    
    public static WifiLocation getWifiLocation(String bssid) {
        return wifi.get(bssid);
    }
    
    public static Collection<WifiLocation> getWifiLocations() {
        return wifi.values();
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
