package net.helff.wificonnector;


public class LocationConnection {

    private String start;
    private String end;
    private int distance;
    
    public LocationConnection(String start, String end, int distance) {
        this.start = start;
        this.end = end;
        this.distance = distance;
    }

    public String getStart() {
        return start;
    }
    
    public String getEnd() {
        return end;
    }

    public int getDistance() {
        return distance;
    }

}