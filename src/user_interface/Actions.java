package user_interface;

import java.io.IOException;

import config.ProxyConfig;
import http.HttpManager;
import proxy.ProxyMode;

public class Actions {

	/**
	 * Test the connection with proxy settings
	 * @param url
	 * @return
	 */
	public boolean testConnection(String url) {
		boolean success;
		try {
			HttpManager.get(url);
			success = true;
		} catch (IOException e) {
			success = false;
		}
		
		return success;
	}
	
	/**
	 * Save the configuration
	 * @param mode
	 * @param hostname
	 * @param port
	 */
	public void save(ProxyMode mode, String hostname, String port) {
		ProxyConfig config = new ProxyConfig();
		config.setProxyMode(mode);
		config.setProxyHostname(hostname);
		config.setProxyPort(port);
	}
}
