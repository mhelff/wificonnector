package net.helff.wificonnector;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


/**
 * An implementation of Dijkstra's shortest path algorithm. It computes the shortest path (in distance)
 * to all locations in the map. The output of the algorithm is the shortest distance from the start Location 
 * to every other Location, and the shortest path from the start Location to every other.
 * <p>
 * Upon calling
 * {@link #execute(Location, Location)}, 
 * the results of the algorithm are made available by calling
 * {@link #getPredecessor(Location)}
 * and 
 * {@link #getShortestDistance(Location)}.
 * 
 * To get the shortest path between the Location <var>destination</var> and
 * the source Location after running the algorithm, one would do:
 * <pre>
 * ArrayList&lt;Location&gt; l = new ArrayList&lt;Location&gt;();
 *
 * for (Location Location = destination; Location != null; Location = engine.getPredecessor(Location))
 * {
 *     l.add(Location);
 * }
 *
 * Collections.reverse(l);
 *
 * return l;
 * </pre>
 * 
 * @see #execute(Location, Location)
 * 
 * @author Renaud Waldura &lt;renaud+tw@waldura.com&gt;
 */

public class DijkstraEngine
{
    /**
     * Infinity value for distances.
     */
    public static final int INFINITE_DISTANCE = Integer.MAX_VALUE;

    /**
     * Some value to initialize the priority queue with.
     */
    private static final int INITIAL_CAPALocation = 8;
    
    /**
     * This comparator orders locations according to their shortest distances,
     * in ascending fashion. If two locations have the same shortest distance,
     * we compare the cities themselves.
     */
    private final Comparator<Location> shortestDistanceComparator = new Comparator<Location>()
        {
            public int compare(Location left, Location right)
            {
                // note that this trick doesn't work for huge distances, close to Integer.MAX_VALUE
                int result = getShortestDistance(left) - getShortestDistance(right);
                
                return (result == 0) ? left.compareTo(right) : result;
            }
        };
    
    /**
     * The graph.
     */
    private final LocationConnectionMap map;
    
    /**
     * The working set of locations, kept ordered by shortest distance.
     */
    private final PriorityQueue<Location> unsettledNodes = new PriorityQueue<Location>(INITIAL_CAPALocation, shortestDistanceComparator);
    
    /**
     * The set of locations for which the shortest distance to the source
     * has been found.
     */
    private final Set<Location> settledNodes = new HashSet<Location>();
    
    /**
     * The currently known shortest distance for all locations.
     */
    private final Map<Location, Integer> shortestDistances = new HashMap<Location, Integer>();

    /**
     * Predecessors list: maps a Location to its predecessor in the spanning tree of
     * shortest paths.
     */
    private final Map<Location, Location> predecessors = new HashMap<Location, Location>();
    
    /**
     * Constructor.
     */
    public DijkstraEngine(LocationConnectionMap map)
    {
        this.map = map;
    }

    /**
     * Initialize all data structures used by the algorithm.
     * 
     * @param start the source node
     */
    private void init(Location start)
    {
        settledNodes.clear();
        unsettledNodes.clear();
        
        shortestDistances.clear();
        predecessors.clear();
        
        // add source
        setShortestDistance(start, 0);
        //unsettledNodes.add(start);
    }
    
    /**
     * Run Dijkstra's shortest path algorithm on the map.
     * The results of the algorithm are available through
     * {@link #getPredecessor(Location)}
     * and 
     * {@link #getShortestDistance(Location)}
     * upon completion of this method.
     * 
     * @param start the starting Location
     * @param destination the destination Location. If this argument is <code>null</code>, the algorithm is
     * run on the entire graph, instead of being stopped as soon as the destination is reached.
     */
    public void execute(Location start, Location destination)
    {
        init(start);
        
        // the current node
        Location u;
        
        // extract the node with the shortest distance
        while ((u = unsettledNodes.poll()) != null)
        {
            assert !isSettled(u);
            
            // destination reached, stop
            if (u == destination) break;
            
            settledNodes.add(u);
            
            relaxNeighbors(u);
        }
    }

    /**
	 * Compute new shortest distance for neighboring nodes and update if a shorter
	 * distance is found.
	 * 
	 * @param u the node
	 */
    private void relaxNeighbors(Location u)
    {
        for (Location v : map.getDestinations(u))
        {
            // skip node already settled
            if (isSettled(v)) continue;
            
            int shortDist = getShortestDistance(u) + map.getDistance(u, v);
            
            if (shortDist < getShortestDistance(v))
            {
            	// assign new shortest distance and mark unsettled
                setShortestDistance(v, shortDist);
                                
                // assign predecessor in shortest path
                setPredecessor(v, u);
            }
        }        
    }

	/**
	 * Test a node.
	 * 
     * @param v the node to consider
     * 
     * @return whether the node is settled, ie. its shortest distance
     * has been found.
     */
    private boolean isSettled(Location v)
    {
        return settledNodes.contains(v);
    }

    /**
     * @return the shortest distance from the source to the given Location, or
     * {@link DijkstraEngine#INFINITE_DISTANCE} if there is no route to the destination.
     */    
    public int getShortestDistance(Location Location)
    {
        Integer d = shortestDistances.get(Location);
        return (d == null) ? INFINITE_DISTANCE : d;
    }

	/**
	 * Set the new shortest distance for the given node,
	 * and re-balance the queue according to new shortest distances.
	 * 
	 * @param Location the node to set
	 * @param distance new shortest distance value
	 */        
    private void setShortestDistance(Location Location, int distance)
    {
        /*
         * This crucial step ensures no duplicates are created in the queue
         * when an existing unsettled node is updated with a new shortest 
         * distance.
         * 
         * Note: this operation takes linear time. If performance is a concern,
         * consider using a TreeSet instead instead of a PriorityQueue. 
         * TreeSet.remove() performs in logarithmic time, but the PriorityQueue
         * is simpler. (An earlier version of this class used a TreeSet.)
         */
        unsettledNodes.remove(Location);

        /*
         * Update the shortest distance.
         */
        shortestDistances.put(Location, distance);
        
		/*
		 * Re-balance the queue according to the new shortest distance found
		 * (see the comparator the queue was initialized with).
		 */
		unsettledNodes.add(Location);        
    }
    
    private void setPredecessor(Location a, Location b)
    {
        predecessors.put(a, b);
    }

}
