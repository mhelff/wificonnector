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

	public void testLocationAmount() throws Exception {

		Collection<Location> l = LocationData.getLocations();
		assertEquals(243, l.size());
	}
	
	public void testRouting() throws Exception {
	    LocationConnectionMap rm = new LocationConnectionMap();
	    DijkstraEngine de = new DijkstraEngine(rm);
	    de.execute(LocationData.getLocation(6, "A", "West"), LocationData.getLocation(0, "E", "Nord"));
	    assertEquals(11370, de.getShortestDistance(LocationData.getLocation(0, "E", "Nord")));
	}

}
