package net.helff.wificonnector.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import net.helff.wificonnector.DijkstraEngine;
import net.helff.wificonnector.Location;
import net.helff.wificonnector.LocationConnection;
import net.helff.wificonnector.LocationConnectionMap;
import net.helff.wificonnector.LocationData;

import com.google.gson.Gson;

public class LocationDataTest extends TestCase {

	public void testExtractToken() throws Exception {

		Collection<Location> l = LocationData.getLocations();
		assertEquals(79, l.size());
	}
	
	public void testRouting() throws Exception {
	    LocationConnectionMap rm = new LocationConnectionMap();
	    DijkstraEngine de = new DijkstraEngine(rm);
	    de.execute(LocationData.getLocation(6, "A", "West"), LocationData.getLocation(0, "E", "Nord"));
	    assertEquals(330, de.getShortestDistance(LocationData.getLocation(0, "E", "Nord")));
	}
	
	public void testGenerateNeighbors() throws Exception {

	    Set<LocationConnection> conns = new HashSet<LocationConnection>();
	    
        Collection<Location> l = LocationData.getLocations();
        for(int floor = 0; floor <= 8; floor++) {
            for(char block = 'A'; block <= 'E'; block++) {
                if(block == 'A') {
                    Location west = LocationData.getLocation(floor, String.valueOf(block), "West");
                    Location mitte = LocationData.getLocation(floor, String.valueOf(block), "Mitte");
                    Location ost = LocationData.getLocation(floor, String.valueOf(block), "Ost");
                    if(west != null) {
                        if(mitte != null) {
                            conns.add(new LocationConnection(west.getId(), mitte.getId(), 30));
                        }
                        Location cn = LocationData.getLocation(floor, "C", "Nord");
                        if(cn != null) {
                            conns.add(new LocationConnection(west.getId(), cn.getId(), 30));
                        }
                        Location westoben = LocationData.getLocation(floor+1, String.valueOf(block), "West");
                        if(westoben != null) {
                            conns.add(new LocationConnection(west.getId(), westoben.getId(), 40));
                        }
                    }
                    if(mitte != null) {
                        if(ost != null) {
                            conns.add(new LocationConnection(mitte.getId(), ost.getId(), 30));
                        }
                        Location dn = LocationData.getLocation(floor, "D", "Nord");
                        if(dn != null) {
                            conns.add(new LocationConnection(mitte.getId(), dn.getId(), 30));
                        }
                        Location mitteoben = LocationData.getLocation(floor+1, String.valueOf(block), "Mitte");
                        if(mitteoben != null) {
                            conns.add(new LocationConnection(mitte.getId(), mitteoben.getId(), 40));
                        }
                    }
                    if(ost != null) {
                        Location en = LocationData.getLocation(floor, "E", "Nord");
                        if(en != null) {
                            conns.add(new LocationConnection(ost.getId(), en.getId(), 30));
                        }
                        Location ostoben = LocationData.getLocation(floor+1, String.valueOf(block), "Ost");
                        if(ostoben != null) {
                            conns.add(new LocationConnection(ost.getId(), ostoben.getId(), 40));
                        }
                    }
                } else if(block != 'B') {
                    Location nord = LocationData.getLocation(floor, String.valueOf(block), "Nord");
                    Location sued = LocationData.getLocation(floor, String.valueOf(block), "S�d");
                    if(nord != null) {
                        if(sued != null) {
                            conns.add(new LocationConnection(nord.getId(), sued.getId(), 30));
                        }
                        Location nordoben = LocationData.getLocation(floor+1, String.valueOf(block), "Nord");
                        if(nordoben != null) {
                            conns.add(new LocationConnection(nord.getId(), nordoben.getId(), 40));
                        }
                    }
                    if(sued != null) {
                        Location suedoben = LocationData.getLocation(floor+1, String.valueOf(block), "S�d");
                        if(suedoben != null) {
                            conns.add(new LocationConnection(sued.getId(), suedoben.getId(), 40));
                        }
                    }
                }
            }
        }
        
        Gson g = new Gson();
        //System.out.println( g.toJson(conns) );
        
    }

}
