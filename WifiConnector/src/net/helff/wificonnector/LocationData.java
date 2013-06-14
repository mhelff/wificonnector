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

/**
 * 
 * 2;A252;4153;CM4540;MFP;Farbe;A4
2;C206;4194;M4555fskm;MFP;SW;A4
2;B206;4170;M525;MFP;SW;A4
2;D236;1487;M525;MFP;SW;A4
2;D207;4217;M525;MFP;SW;A4
2;B238;4172;M775z;MFP;Farbe;A3
2;E225;4248;M775z;MFP;Farbe;A3
3;C331;4196;CM4540;MFP;Farbe;A4
3;B345;4175;M4555fskm;MFP;SW;A4
3;A326;4155;M525;MFP;SW;A4
3;E315;4253;M525;MFP;SW;A4
3;C316;4198;M725z;MFP;SW;A3
3;D331;4220;M725z;MFP;SW;A3
3;A345;4159;M775z;MFP;Farbe;A3
3;E336;4252;M775z;MFP;Farbe;A3
4;C418;7516;M4555fskm;MFP;SW;A4
4;C430;4199;M4555fskm;MFP;SW;A4
4;E430;4256;M4555fskm;MFP;SW;A4
4;A414;1355;M525;MFP;SW;A4
4;E408;;M525;MFP;SW;A4
4;A432;4157;M725z;MFP;SW;A3
4;B445;4177;M775z;MFP;Farbe;A3
4;D425;4224;M775z;MFP;Farbe;A3
4;E404;4156;M775z;MFP;Farbe;A3
5;C503;4203;M4555fskm;MFP;SW;A4
5;D506;4228;M4555fskm;MFP;SW;A4
5;E504;4167;M4555fskm;MFP;SW;A4
5;A548;4262;M725z;MFP;SW;A3
5;B504;4430;M725z;MFP;SW;A3
5;A514;4260;M775z;MFP;Farbe;A3
5;D525;;M775z;MFP;Farbe;A3
6;C625;;CLJ5550dn;Drucker;Farbe;A3
6;A626;4317;M4555fskm;MFP;SW;A4
6;C625;4205;M4555fskm;MFP;SW;A4
6;D631;4232;M4555fskm;MFP;SW;A4
6;A645;2111;M525;MFP;SW;A4
6;B621;8491;M525;MFP;SW;A4
6;E633;4204;M725z;MFP;SW;A3
7;A745;7521;CM4540;MFP;Farbe;A4
7;C708;4209;M4555fskm;MFP;SW;A4
7;D732;4236;M4555fskm;MFP;SW;A4
7;E714;4243;M4555fskm;MFP;SW;A4
7;B747;4187;M775z;MFP;Farbe;A3
8;A826;4164;M4555fskm;MFP;SW;A4
8;A858;4165;M525;MFP;SW;A4
8;D821;2080;M775z;MFP;Farbe;A3
 * @author helffm
 *
 */

public class LocationData {

    private static Map<String, Location> locations = new HashMap<String, Location>();

    static {
        InputStream is = LocationData.class.getResourceAsStream("/gbr.locations");
        Gson g = new Gson();
        Type collectionType = new TypeToken<Collection<Location>>(){}.getType();
        Collection<Location> l = g.fromJson(new InputStreamReader(is), collectionType);
        addAllLocations(l);       
    }
    
    private static void addAllLocations(Collection<Location> ls) {
        for(Location l : ls) {
            locations.put(l.getId(), l);
        }
    }
    
    public static SortedMap<Integer, Location> findPrintersAtLocation(Location pos, boolean color, boolean a3, boolean copier) {
    	SortedMap<Integer, Location> result = new TreeMap<Integer, Location>();
    	
    	LocationConnectionMap rm = new LocationConnectionMap();
	    DijkstraEngine de = new DijkstraEngine(rm);
    	
    	Collection<String> neededCapabilities = new HashSet<String>();
    	neededCapabilities.add(color ? Location.CAPABILITY_COLOR : Location.CAPABILITY_BW);
    	neededCapabilities.add(a3 ? Location.CAPABILITY_SIZE_A3 : Location.CAPABILITY_SIZE_A4);
    	if(copier) {
    		neededCapabilities.add(Location.CAPABILITY_COPY);
    	}
    	
    	// first find all matching printers:
    	for(Location l : locations.values()) {
    		if(l.getType() == Location.TYPE_PRINTER && l.hasCapabilities(neededCapabilities)) {
    			// find distance
    			de.execute(pos, l);
    		    result.put(de.getShortestDistance(l), l);
    		}
    	}
    	
    	return result;
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
