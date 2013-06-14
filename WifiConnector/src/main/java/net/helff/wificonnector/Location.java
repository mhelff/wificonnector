package net.helff.wificonnector;

import java.util.Collection;
import java.util.List;


public class Location implements Comparable<Location> {
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_PRINTER = 2;
    
    public static final String CAPABILITY_SIZE_A3 = "A3";
    public static final String CAPABILITY_SIZE_A4 = "A4";
    
    public static final String CAPABILITY_COLOR = "Color";
    public static final String CAPABILITY_BW = "BW";
    
    public static final String CAPABILITY_COPY = "Copy";

    private String id;
    private int type;
    private int floor;
    private String block;
    private String position;
    private String building;
    private String name;
    private List<String> capabilities;
    
    protected Location(String id, int type, int floor, String block, String position, String name, List<String> capabilities) {
        this.id = id;
        this.type = type;
        this.floor = floor;
        this.block = block;
        this.position = position;
        this.building = "GBR";
        this.name = name;
        this.capabilities = capabilities;
    }

    public String getId() {
        return id;
    }
    
    public int getType() {
        return type;
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
    
    public String getBuilding() {
        return building;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean equals(Object o)
    {
        return this == o || equals((Location) o);
    }
    
    private boolean equals(Location c)
    {
        return this.id == c.id;
    }

    /**
     * Compare two cities by name.
     * 
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Location c)
    {
        return this.id.compareTo(c.getId());
    }
    
    boolean hasCapability(String cap) {
    	return this.capabilities != null && this.capabilities.contains(cap);
    }
    
    boolean hasCapabilities(Collection<String> cap) {
    	return this.capabilities != null && capabilities.containsAll(cap);
    }

}