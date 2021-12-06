package proxy;


//import java.net.Authenticator.RequestorType;
import java.net.PasswordAuthentication;
import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.Properties;

import proxy.ProxyLoginForm;

import java.net.Proxy;
import java.net.URL;
import java.net.HttpURLConnection;

import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthCacheValue;

//properties proxyUser, proxyPass, dcfUser, dcfPass, serverList
@SuppressWarnings("restriction")
public class GenericAuthenticator extends Authenticator {
	
	public GenericAuthenticator() {
		Authenticator.setDefault(this);
		System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
		// reset the cache of the authentication
		AuthCacheValue.setAuthCache(new AuthCacheImpl());
	}
	
	private static final Properties props = new Properties();
		
	public static String setProperty(String key, String value) {
		checkKey(key);
		return (String) props.setProperty(key, value);
	}
	
	public static String getProperty(String key, String def) {
		checkKey(key);
		return props.getProperty(key, def);
	}
	
	private static void checkKey(String key) {
        if (key == null) {
            throw new NullPointerException("key can't be null");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("key can't be empty");
        }
    }
	
	public void setProxyDetails(String hostname, String port) {
		setProperty("proxyHost", hostname);
		setProperty("proxyPort", port);
		
		int portInt = Integer.valueOf(port);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, portInt));
		
		//detect if proxy authentication is needed
		try {
			URL url = new URL("https://www.google.com");
			HttpURLConnection con = null;
			con = (HttpURLConnection) url.openConnection(proxy);
			int status = con.getResponseCode();
			//check for 407 (Proxy Authentication Required)"
			if (status==407) {
				ProxyLoginForm plf = new ProxyLoginForm();
				setProperty("proxyUser", plf.proxyUser);
				setProperty("proxyPass", plf.proxyPass);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@Override
    protected PasswordAuthentication getPasswordAuthentication() {
		
        if (getRequestorType() == RequestorType.PROXY) {
            //String prot = getRequestingProtocol().toLowerCase();
            String host = getProperty("proxyHost", "");
            String port = getProperty("proxyPort", "0");
            String user = getProperty("proxyUser", "");
            String password = getProperty("proxyPass", "");
            if ( (getRequestingHost().equalsIgnoreCase(host)) &&
                 (Integer.parseInt(port) == getRequestingPort()) &&
                 !user.isEmpty()){
                    return new PasswordAuthentication(user, password.toCharArray());
            }
            return null;
        }
        else {
        	String prot = getRequestingProtocol().toLowerCase();
            String user = getProperty("dcfUser", "");
            String password = getProperty("dcfPass", "");
            String reqHost = getRequestingHost();
        	if (prot.equals("https") && reqHost.startsWith("dcf-") 
        			&& reqHost.endsWith(".efsa.europa.eu")) {
                    return new PasswordAuthentication(user, password.toCharArray());
            }
        	return null;
        }
    }
	
}
