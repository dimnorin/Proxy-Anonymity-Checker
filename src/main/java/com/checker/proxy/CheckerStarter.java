package com.checker.proxy;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.checker.proxy.net.ProxyUrlReader;
import com.checker.proxy.proxy.ProxyWrapper;
import com.checker.proxy.util.CommonUtils;
import com.checker.proxy.util.Constants;
import com.checker.proxy.util.NetUtils;
/**
 * Main class to test proxies and find High Anonimus
 */
public class CheckerStarter {
	/**
	 * Properties file name
	 */
	private static final String PROP_FILE	= "./app.properties";
	/**
	 * Proxies list to be checked
	 */
	private String PROXY_SRC_FILE 			= "./proxy_src.txt";
	/**
	 * High Anonimus and Elite Proxies will be placed here
	 */
	private String PROXY_DST_HIGH_FILE 	= "./proxy_dst_high.txt";
	/**
	 * Low Anonimus Proxies will be here
	 */
	private String PROXY_DST_LOW_FILE 		= "./proxy_dst_low.txt";
	/**
	 * Remote server url to show headers of our request through selected proxy.<br>
	 * Please check headers.php in /src/main/php
	 */
	private String HEADERS_TEST_URL		= "";

	private ArrayList<ProxyWrapper> proxyHight = new ArrayList<>();
	private ArrayList<ProxyWrapper> proxyLow = new ArrayList<>();
	private ExecutorService poolProxyCheck = Executors.newFixedThreadPool(Constants.PROXY_POOL_MAX_SIZE);

	public static void main(String[] args) {
		CheckerStarter starter;
		try {
			starter = new CheckerStarter(PROP_FILE);
			starter.doFileChecker();
//			starter.doNetChecker();
		} catch (IOException e) {
			CommonUtils.log("", e);
		}
	}
	
	public CheckerStarter(String filePropertiesName) throws IOException{
		Properties prop = new Properties();
		// load a properties file
		prop.load(new FileInputStream(filePropertiesName));
		
		PROXY_SRC_FILE = prop.getProperty("PROXY_SRC_FILE");
		PROXY_DST_HIGH_FILE = prop.getProperty("PROXY_DST_HIGH_FILE");
		PROXY_DST_LOW_FILE = prop.getProperty("PROXY_DST_LOW_FILE");;
		HEADERS_TEST_URL = prop.getProperty("HEADERS_TEST_URL");;
	}
	/**
	 * Check proxy list reade from PROXY_SRC_FILE
	 */
	public void doFileChecker() {
		try {
			doCheck(CommonUtils.readFile(PROXY_SRC_FILE));
		} catch (IOException e) {
			CommonUtils.log("", e);
		}
	}
	/**
	 * Check proxies list read from internet resources 
	 */
	public void doNetChecker() {
		try{
			doCheck(ProxyUrlReader.readAll());
		} catch (Exception e) {
			CommonUtils.log("", e);
		}
	}
	/**
	 * Main check method.
	 * <p>Result will be in <pre>PROXY_DST_HIGH_FILE</pre> and <pre>PROXY_DST_LOW_FILE</pre>
	 * @param proxies list for check
	 */
	private void doCheck(LinkedList<String> proxies){
		try {
			System.out.println("Proxies submited: "+proxies.size());
			
			ArrayList<Callable<Void>> todo = new ArrayList<Callable<Void>>();
			for (String sProxy : proxies) {
				Callable<Void> c = new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						ProxyWrapper proxy = null;
						try {
							proxy = new ProxyWrapper(sProxy);
							CommonUtils.sleepRand(500, 1000); // to avoid many
																// request in
																// one time and
																// block from
																// provider
							String content = NetUtils.getUrlContent(HEADERS_TEST_URL, proxy, Constants.DEFAULT_USER_AGENT);
							if (isHighAnonimous(content, proxy))
								proxyHight.add(proxy);
							else
								proxyLow.add(proxy);
						} catch (Exception e) {
							CommonUtils.log(e.getMessage()+", Proxy: "+proxy);
						}
						return null;
					}
				};
				todo.add(c);
			}
			try {
				poolProxyCheck.invokeAll(todo);
			} catch (InterruptedException e) {}
			
			System.out.println(String.format("High proxies: %d, Low proxies: %d, Total: %d", proxyHight.size(), proxyLow.size(), (proxyHight.size()+proxyLow.size())));
			
			CommonUtils.writeFile(PROXY_DST_HIGH_FILE, proxyHight, false);
			CommonUtils.writeFile(PROXY_DST_LOW_FILE, proxyLow, false);
			
			System.exit(0);
		} catch (Exception e) {
			CommonUtils.log("", e);
		}
	}
	/**
	 * Determine if proxy is high anonimus.
	 * <p>
	 * Find in headers keys: X-Forwarded-For, X-Real-IP, Via<br>
	 * If one of key found, proxy is low anonimus
	 * @param sHeaders headers from access headers.php through this proxy
	 * @param proxy  given proxy
	 * @return true if high anonimus, false - if low
	 */
	private boolean isHighAnonimous(String sHeaders, ProxyWrapper proxy) {
		String[] headers = sHeaders.split("<br>");
		for (String header : headers) {
			String[] a = header.split(":");
			switch (a[0].trim()) {
			case "X-Forwarded-For":
			case "X-Real-IP":
				if (!a[1].trim().equals(proxy.getHost()))
					return false;
				break;

			case "Via":
				return false;
			}
		}
		return true;
	}

}
