package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.btr.proxy.search.ProxySearch;
import com.btr.proxy.search.ProxySearch.Strategy;

import config.ProxyConfig;
import proxy.ProxyConfigException;

public class HttpManager {

	private static final Logger LOGGER = LogManager.getLogger(HttpManager.class);
	
	/**
	 * Open an http connection with the endpoint
	 * @param endpoint
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection openConnection(String endpoint) throws IOException {
		
	    URL url = new URL(endpoint);
	    Proxy proxy = HttpManager.getProxy();
	    
	    // use proxy if present, otherwise no
	    HttpURLConnection con = null;
	    if (proxy == null) {
	    	con = (HttpURLConnection) url.openConnection();
	    }
	    else {
	    	con = (HttpURLConnection) url.openConnection(proxy);
	    }
	    
	    return con;
	}

	/**
	 * Perform a get request
	 * @param endpoint
	 * @return
	 * @throws IOException
	 */
	public static String get(String endpoint) throws IOException {
		
	    // use proxy if present, otherwise no
	    HttpURLConnection con = openConnection(endpoint);
	    
	    con.setRequestMethod("GET");
	    
	    // add support for github
	    con.setRequestProperty("Accept", "application/vnd.github.v3+json");
	    
	    int status = con.getResponseCode();
	    
	    LOGGER.info("Response code=" + status);
	    
	    if (status != HttpURLConnection.HTTP_OK) {
	    	return null;
	    }
	    
	    BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    
	    StringBuilder result = new StringBuilder();
	    String line;
	    while ((line = rd.readLine()) != null) {
	    	result.append(line);
	    }
	    rd.close();
	    
	    con.disconnect();
	    
	    LOGGER.debug("Response contents=" + result.toString());
	    
	    return result.toString();
	}
	
	/**
	 * Get the system proxy
	 * @return
	 * @throws ProxyConfigException 
	 */
	public static Proxy getProxy() throws ProxyConfigException {
		
		ProxyConfig config = new ProxyConfig();
		
		Proxy proxy = null;
		switch(config.getProxyMode()) {
		case NO_PROXY:
			proxy = null;
			break;
		case AUTO:
			proxy = detectProxy();
			break;
		case MANUAL:
			proxy = getManualProxy();
			break;
		}
		
		return proxy;
	}
	
	public static Proxy getManualProxy() throws ProxyConfigException {
		
		ProxyConfig config = new ProxyConfig();
		
		String hostname = config.getProxyHostname();
		String port = config.getProxyPort();
		
		// automatically detect proxy if empty port or hostname
		if ((hostname == null || hostname.isEmpty()) || (port == null || port.isEmpty())) {
			LOGGER.error("Wrong configuration for the proxy hostname or port. Found hostname=" + hostname + ", port=" + port);
			throw new ProxyConfigException("hostname: " + hostname + ", port: " + port);
		}
		
		try {
			int portInt = Integer.valueOf(port);
			
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, portInt));
			
			return proxy;
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			LOGGER.error("Wrong configuration for the proxy port. Expected number, found port=" + port, e);
			throw new ProxyConfigException("hostname: " + hostname + ", port: " + port);
		}
	}
	
	/**
	 * Automatically detect a proxy of the system
	 * @return
	 */
	public static Proxy detectProxy() {
		
		LOGGER.info("Automatic detection of proxy...");
		
		System.setProperty("java.net.useSystemProxies","true");
		
		ProxySearch proxySearch = new ProxySearch();
		proxySearch.addStrategy(Strategy.OS_DEFAULT);
		proxySearch.addStrategy(Strategy.JAVA);
		proxySearch.addStrategy(Strategy.BROWSER);
		ProxySelector proxySelector = proxySearch.getProxySelector();
		
		// if no proxy can be found (due to no connection)
		if (proxySelector == null) {
			return null;
		}
		
		ProxySelector.setDefault(proxySelector);
		
		URI testUri = null;
		try {
			testUri = new URI("http://java.sun.com/");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			LOGGER.error("Wrong test URI for searching automatically proxies", e);
		}
		
		if (testUri == null) {
			return null;
		}
		
		List<Proxy> l = proxySelector.select(testUri);
		
		for (Iterator<Proxy> iter = l.iterator(); iter.hasNext();) {

            Proxy proxy = iter.next();

            LOGGER.debug("Proxy hostname : " + proxy.type());

            InetSocketAddress addr = (InetSocketAddress)proxy.address();

            if(addr == null) {
            	LOGGER.info("No Proxy found");
            } else {

            	// proxy found
            	LOGGER.info("Proxy hostname : " + addr.getHostName());
            	LOGGER.info("Proxy port : " + addr.getPort());

                return proxy;
            }
        }
		
		return null;
	}
}
