package net.helff.wificonnector;


public class LocationConnection {

    private String a;
    private String b;
    private int d;
    
    public LocationConnection(String a, String b, int d) {
        this.a = a;
        this.b = b;
        this.d = d;
    }

    public String getA() {
        return a;
    }
    
    public String getB() {
        return b;
    }

    public int getD() {
        return d;
    }

}