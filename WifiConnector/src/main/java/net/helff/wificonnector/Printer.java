package net.helff.wificonnector;

import java.util.Collection;
import java.util.List;

public class Printer {
	public static final String CAPABILITY_SIZE_A3 = "A3";
    public static final String CAPABILITY_SIZE_A4 = "A4";
    
    public static final String CAPABILITY_COLOR = "Color";
    public static final String CAPABILITY_BW = "BW";
    
    public static final String CAPABILITY_COPY = "Copy";

    private String location;
    private String name;
    private List<String> capabilities;

    
    boolean hasCapability(String cap) {
    	return this.capabilities != null && this.capabilities.contains(cap);
    }
    
    boolean hasCapabilities(Collection<String> cap) {
    	return this.capabilities != null && capabilities.containsAll(cap);
    }

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the capabilities
	 */
	public List<String> getCapabilities() {
		return capabilities;
	}

	/**
	 * @param capabilities the capabilities to set
	 */
	public void setCapabilities(List<String> capabilities) {
		this.capabilities = capabilities;
	}
}
