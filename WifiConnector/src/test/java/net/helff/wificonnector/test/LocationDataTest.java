package net.helff.wificonnector.test;

import java.util.Collection;

import junit.framework.TestCase;
import net.helff.wificonnector.DijkstraEngine;
import net.helff.wificonnector.Location;
import net.helff.wificonnector.LocationConnectionMap;
import net.helff.wificonnector.LocationData;
import net.helff.wificonnector.Printer;
import net.helff.wificonnector.WifiLocation;

public class LocationDataTest extends TestCase {

	public void testLocationAmount() throws Exception {

		Collection<Location> l = LocationData.getLocations();
		assertEquals(256, l.size());
	}
	
	public void testRouting() throws Exception {
	    LocationConnectionMap rm = new LocationConnectionMap();
	    DijkstraEngine de = new DijkstraEngine(rm);
	    de.execute(LocationData.getLocation(6, "A", "WA1"), LocationData.getLocation(0, "E", "WE1"));
	    assertEquals(216, de.getShortestDistance(LocationData.getLocation(0, "E", "WE1")));
	}
	
	public void testPrinterLocations() throws Exception {
	    Collection<Printer> printers = LocationData.getPrinters();
	    for(Printer printer : printers) {
	    	assertNotNull("Printer location: " + printer.getLocation() + " does not exist!", LocationData.getLocation(printer.getLocation()));
	    }
	}
	
	public void testWifiLocations() throws Exception {
	    Collection<WifiLocation> wifis = LocationData.getWifiLocations();
	    for(WifiLocation wifi : wifis) {
	    	assertNotNull("Wifi location: " + wifi.getLocation() + " does not exist!", LocationData.getLocation(wifi.getLocation()));
	    }
	}
	
	public void testWifiLocationsComplete() throws Exception {
	    Collection<Location> locs = LocationData.getLocations();
	    for(Location loc : locs) {
	    	if(loc.getPosition().startsWith("W")) {
	    		// now search in wifi list for matching wifi information
	    		Collection<WifiLocation> wifis = LocationData.getWifiLocations();
	    		boolean found = false;
	    	    for(WifiLocation wifi : wifis) {
	    	    	if(wifi.getLocation().equals(loc.getId())) {
	    	    		found = true;
	    	    	}
	    	    }
	    	    if(!found) {
	    	    	System.out.println("Wifi location " + loc.getId() + " has no BSSID");
	    	    }
	    	}
	    	
	    }
	}

}
