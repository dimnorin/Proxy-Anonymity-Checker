package com.checker.proxy.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import com.checker.proxy.util.Constants;
/**
 * Proxy wrapper over java.net.Proxy
 */
public class ProxyWrapper {
	/**
	 * Proxy host
	 */
	private String host = null;
	/**
	 * Proxy port
	 */
	private int port = 0;
	/**
	 * Proxy type, e.g. HTTP, SOCKS
	 */
	private Proxy.Type type = Type.HTTP;
	
	public ProxyWrapper(String host, int port, Type type) {
		this.host = host;
		this.port = port;
		this.type = type;
	}
	
	public ProxyWrapper(String host, String port, String type)  throws IOException{
		this(host+Constants.TAB+port+Constants.TAB+type);
	}
	/**
	 * Construct proxy from strings like host:port:type or host\tport\ttype
	 * @param host_port_type strings like host:port:type or host\tport\ttype
	 * @throws IOException
	 */
	public ProxyWrapper(String host_port_type) throws IOException{
		super();
		prepareDirty(host_port_type);
	}
	

	@Override
	public String toString(){
		return host+Constants.TAB+port+Constants.TAB+type;
	}
	/**
	 * Get the proxy object
	 * @return proxy object constructed from wrapper
	 */
	public Proxy toProxy(){
		return new Proxy(type, new InetSocketAddress(host, port));
	}
	
	//--------GETTERS-------
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public Proxy.Type getType() {
		return type;
	}	
	/**
	 * Parse income string and get host, port, type to construct a proxy
	 * Supported delimeters: , ; : \t any number of whitespaces
	 * @param sProxy strings like host:port:type or host\tport\ttype or host,port,type
	 * @throws IOException
	 */
	private void prepareDirty(String sProxy) throws IOException{
		int i = 0;
		//Use regexp to divide income string into parts using supported delimeters
		String[] parts = sProxy.split(",|;|:|\\t|\\s+|\\xA0"); 
		for(String part : parts){
			if (i > 2) break;
			// Use regexp to find ip
			if(part.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")){
				this.host = part;
				i++;
			}else if(part.toUpperCase().contains("HTTP")){ 
				this.type = Proxy.Type.HTTP;
				i++;
			}else if(part.toUpperCase().contains("SOCKS")){
				this.type = Proxy.Type.SOCKS;
				i++;
			}else{
				if(this.host != null && this.port == 0)
					try {
						this.port = Integer.parseInt(part);
						i++;
					} catch (Exception e) {}
			}
		}
	}
	
	@Override
	public boolean equals(Object o){
		ProxyWrapper p = (ProxyWrapper) o;
		return this.host.equals(p.host) && this.port == p.port;
	}
}
