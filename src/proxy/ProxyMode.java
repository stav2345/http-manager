package proxy;

public enum ProxyMode {
	NO_PROXY("NO_PROXY"),
	AUTO("AUTO"),
	MANUAL("MANUAL");
	
	String keyword;
	ProxyMode(String keyword) {
		this.keyword = keyword;
	}
	
	public static ProxyMode fromString(String keyword) {
		for (ProxyMode mode : ProxyMode.values()) {
			if (mode.keyword.equalsIgnoreCase(keyword)) {
				return mode;
			}
		}
		
		System.err.println("No valid proxy mode found, using NO_PROXY");
		return NO_PROXY;
	}
}
