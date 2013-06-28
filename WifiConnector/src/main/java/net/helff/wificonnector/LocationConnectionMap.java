package net.helff.wificonnector;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This map stores routes in a matrix, a nxn array. It is most useful when there
 * are lots of routes, otherwise using a sparse representation is recommended.
 * 
 * @author Renaud Waldura &lt;renaud+tw@waldura.com&gt;
 */

public class LocationConnectionMap {

    private final Map<Location, Map<Location, Integer>> distances;

    public LocationConnectionMap() {
        distances = new HashMap<Location, Map<Location, Integer>>();
        InputStream is = LocationData.class.getResourceAsStream("/distances.txt");
        Gson g = new Gson();
        Type collectionType = new TypeToken<Collection<LocationConnection>>() {
        }.getType();
        Collection<LocationConnection> l = g.fromJson(new InputStreamReader(is), collectionType);
        for (LocationConnection c : l) {
            addDirectRoute(LocationData.getLocation(c.getStart()), LocationData.getLocation(c.getEnd()), c.getDistance());
        }
    }

    /**
     * Link two locations by a direct route with the given distance.
     */
    public void addDirectRoute(Location start, Location end, int distance) {
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
     * @return the distance between the two locations, or 0 if no path exists.
     */
    public int getDistance(Location start, Location end) {
        Map<Location, Integer> distanceMap = distances.get(start);

        return (distanceMap != null && distanceMap.containsKey(end)) ? distanceMap.get(end).intValue() : 0;
    }

    /**
     * @return the list of all valid destinations from the given Location.
     */
    public Set<Location> getDestinations(Location location) {
        return distances.get(location) != null ? distances.get(location).keySet() : new HashSet<Location>();
    }

}
