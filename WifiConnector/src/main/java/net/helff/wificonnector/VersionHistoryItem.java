package net.helff.wificonnector;

public class VersionHistoryItem {

	public static final int TYPE_APP = 0;
	public static final int TYPE_PRINTERS = 1;
	public static final int TYPE_LOCATIONS = 2;
	public static final int TYPE_WIFI = 3;
	public static final int TYPE_DISTANCES = 4;
	
	private int type;
	private int builddate;
	private String whatsnew;
	private String url;
	private int version;

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the builddate
	 */
	public int getBuilddate() {
		return builddate;
	}

	/**
	 * @param builddate the builddate to set
	 */
	public void setBuilddate(int builddate) {
		this.builddate = builddate;
	}

	/**
	 * @return the whatsnew
	 */
	public String getWhatsnew() {
		return whatsnew;
	}

	/**
	 * @param whatsnew the whatsnew to set
	 */
	public void setWhatsnew(String whatsnew) {
		this.whatsnew = whatsnew;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
}
