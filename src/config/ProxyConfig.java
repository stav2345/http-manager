package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import proxy.ProxyMode;

/**
 * Configuration of the application
 * @author avonva
 *
 */
public class ProxyConfig {

	private String PROXY_CONFIG_PATH = "config" + System.getProperty("file.separator") 
										+ "proxyConfig.xml";
	
	private String PARENT_PROXY_CONFIG_PATH = ".." + System.getProperty("file.separator") 
										+ PROXY_CONFIG_PATH;
	
	public static final String PROXY_HOST_NAME = "Proxy.ManualHostName";
	public static final String PROXY_PORT = "Proxy.ManualPort";
	public static final String PROXY_MODE = "Proxy.Mode";

	/**
	 * Set the path where the config folder is present, by the default is current directory
	 * @param basePath
	 */
	public ProxyConfig(String basePath) {
		this.PROXY_CONFIG_PATH = basePath + PROXY_CONFIG_PATH;
	}

	public ProxyConfig() {}
	
	public String getConfigPath() {
		return this.PROXY_CONFIG_PATH;
	}
	
	private String getFromParent(String key) {
		return getValue(PARENT_PROXY_CONFIG_PATH, key);
	}
	
	/**
	 * Get customized proxy hostname
	 * @return
	 */
	public String getProxyHostname() {
		
		String name = getValue(PROXY_CONFIG_PATH, PROXY_HOST_NAME);
		
		if (name == null)
			return getFromParent(PROXY_HOST_NAME);
		
		return name;
	}
	
	/**
	 * Get customized proxy port
	 * @return
	 */
	public String getProxyPort() {
		
		String port = getValue(PROXY_CONFIG_PATH, PROXY_PORT);
		
		if (port == null)
			return getFromParent(PROXY_PORT);
		
		return port;
	}
	
	public ProxyMode getProxyMode() {
		
		String mode = getValue(PROXY_CONFIG_PATH, PROXY_MODE);
		
		if (mode == null)
			mode = getFromParent(PROXY_MODE);

		return ProxyMode.fromString(mode);
	}
	
	/**
	 * Read the application properties from the xml file
	 * @return
	 */
	public Properties getProperties(String filename) {
		
		Properties properties = new Properties();

		try(InputStream stream = new FileInputStream(filename)) {
			properties.loadFromXML(stream);
		}
		catch (IOException e) {
			System.err.println("The " + filename + " file was not found. Please check!");
		}

		return properties;
	}
	
	private String getValue(String propertiesFilename, String property) {
		
		Properties prop = getProperties(propertiesFilename);
		
		if (prop == null)
			return null;
		
		String value = prop.getProperty(property);
		
		return value;
	}
}
