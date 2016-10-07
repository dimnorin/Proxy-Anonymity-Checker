package dim.scrapper.olx.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import dim.scrapper.olx.exception.Responce404Exception;
import dim.scrapper.olx.exception.ResponseCodeException;
import dim.scrapper.olx.proxy.ProxyWrapper;

public class NetUtils {
	/**
	 * Get url connection
	 * @param url to retrieve
	 * @param proxy could be null
	 * @param userAgent could be null
	 * @return connection
	 * @throws IOException
	 */
	public static HttpURLConnection getConnection(String url, ProxyWrapper proxy, String userAgent) throws IOException{
		HttpURLConnection con;
		if(proxy == null)
			con = (HttpURLConnection)new URL(url).openConnection();
		else
			con = (HttpURLConnection)new URL(url).openConnection(proxy.toProxy());

		if(userAgent == null)
			userAgent = Constants.DEFAULT_USER_AGENT;
		
		// CURLOPT_FOLLOWLOCATION
	    con.setInstanceFollowRedirects(true);
    	con.setRequestProperty("User-Agent", userAgent);
	    con.setConnectTimeout(Constants.READ_TIMEOUT);
	    con.setReadTimeout(Constants.READ_TIMEOUT);
	    return con;
	}
	
	public static HttpURLConnection getConnection(String url, String userAgent) throws IOException{
		return getConnection(url, null, userAgent);
	}
	
	public static String getUrlContent(String url, ProxyWrapper proxy, String ua, String referer) throws IOException{
		return getUrlContent(url, proxy, ua, referer, Constants.CHARSET_UTF8);
	}
	
	public static String getUrlContent(String url, ProxyWrapper proxy, String ua) throws IOException{
		return getUrlContent(url, proxy, ua, Constants.REFERER_GOOGLE, Constants.CHARSET_UTF8);
	}
	
	public static String getUrlContent(String url, String referer, String charset) throws IOException{
		return getUrlContent(url, null, Constants.DEFAULT_USER_AGENT, referer, charset);
	}
	/**
	 * Get url content into string.
	 * @param url to retrieve
	 * @param proxy could be null
	 * @param ua could be null
	 * @param referer could be null
	 * @param charset could be null
	 * @return string with url content
	 * @throws IOException when response code not HTTP 200 
	 */
	public static String getUrlContent(String url, ProxyWrapper proxy, String ua, String referer, String charset) throws IOException{
		HttpURLConnection con = getConnection(url, proxy, ua);
		if(referer == null)
			referer = Constants.REFERER_GOOGLE;
		if(charset == null)
			charset = Constants.CHARSET_UTF8;
		
		con.setRequestProperty("Referer", referer);
	    
	    con.setDoInput(true);
	    
	    int status = con.getResponseCode(); // 200 = HTTP_OK
	    if (status >= 200 && status < 300) {
            return CommonUtils.readInput(con.getInputStream(), charset);
        } else {
        	if(status == 404)
        		throw new Responce404Exception("No page: " + status);
            throw new ResponseCodeException("Unexpected response status: " + status);
        }
	}
}
