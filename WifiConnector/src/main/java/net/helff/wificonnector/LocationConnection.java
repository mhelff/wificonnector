package net.helff.wificonnector;


public class LocationConnection {

    private String start;
    private String end;
    private double distance;
    
    public LocationConnection(String start, String end, double distance) {
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

    public double getDistance() {
        return distance;
    }

}