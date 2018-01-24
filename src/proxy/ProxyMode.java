package proxy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public enum ProxyMode {
	
	NO_PROXY("NO_PROXY"),
	AUTO("AUTO"),
	MANUAL("MANUAL");
	
	private static final Logger LOGGER = LogManager.getLogger(ProxyMode.class);
	private String keyword;
	
	ProxyMode(String keyword) {
		this.keyword = keyword;
	}
	
	public static ProxyMode fromString(String keyword) {
		for (ProxyMode mode : ProxyMode.values()) {
			if (mode.keyword.equalsIgnoreCase(keyword)) {
				return mode;
			}
		}
		
		LOGGER.warn("No valid proxy mode found, using NO_PROXY");
		return NO_PROXY;
	}
}
