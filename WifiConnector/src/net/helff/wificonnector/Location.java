package net.helff.wificonnector;


public class Location implements Comparable<Location> {
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_PRINTER = 2;

    private String id;
    private int type;
    private int floor;
    private String block;
    private String position;
    private String building;
    
    protected Location(String id, int type, int floor, String block, String position) {
        this.id = id;
        this.type = type;
        this.floor = floor;
        this.block = block;
        this.position = position;
        this.building = "GBR";
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

}