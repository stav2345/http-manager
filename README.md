# Http Manager
Library to manage http connections (for now it is possible to call get requests). In particular, it is possible to configure a proxy if present.

## Proxy configuration
The proxy can be configured in the config/proxyConfig.xml file. In particular, Proxy.Mode defines which is the method to detect the proxy. It can assume the following values:
* NO_PROXY: no proxy is actually used;
* AUTO: the proxy is automatically detected and, if not present, NO_PROXY is used;
* MANUAL: the proxy can be manually set (hostname and port are required in the Proxy.ManualHostName and Proxy.ManualPort fields)

Note that since the project can be wrapped inside an installer (i.e. the application is updated), the program first checks if the proxy configuration are present in the parent (../config/proxyConfig.xml). 
Only if they are not present there it will use the configuration present in config/proxyConfig.xml!