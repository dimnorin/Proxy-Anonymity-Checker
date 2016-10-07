package dim.scrapper.olx.net;

import java.net.HttpURLConnection;
import java.util.LinkedList;

import dim.scrapper.olx.proxy.ProxyWrapper;
import dim.scrapper.olx.util.CommonUtils;
import dim.scrapper.olx.util.NetUtils;

/**
 * Retrieve proxy list form internet resources 
 */
public class ProxyUrlReader {
	/**
	 * Get HTTP proxies from api.foxtools.ru
	 */
	private static final String URL_FOXTOOLS		= "http://api.foxtools.ru/v2/Proxy.txt?type=HTTP&available=Yes&free=Yes&uptime=90&lang=Auto&cp=UTF-8&details=1";
	/**
	 * Get HTTP proxies from www.freeproxy-list.ru
	 */
	private static final String URL_FREEPROXY_LIST	= "http://www.freeproxy-list.ru/api/proxy?accessibility=90&anonymity=true&token=demo";
	/**
	 * Read all available proxies into list
	 * @return list of proxies read
	 * @throws Exception
	 */
	public static LinkedList<String> readAll() throws Exception{
		LinkedList<String> res = new LinkedList<>();
		res.addAll(readFoxTools());
		res.addAll(readFreeProxyList());
		return res;
	}
	/**
	 * Read proxies from api.foxtools.ru using paging
	 * @return list of proxies read
	 * @throws Exception
	 */
	public static LinkedList<String> readFoxTools() throws Exception{
		LinkedList<String> res = new LinkedList<>();
		int limit = 2; //Read only 2 pages, because early proxies are outdated
		for (int page = 1; page <= limit; page++) {
			LinkedList<String> s = CommonUtils.readString(NetUtils.getUrlContent(URL_FOXTOOLS+"&page="+page, (ProxyWrapper)null, null));
			String[] p = s.removeFirst().split(" ");
			limit = Integer.parseInt(p[1]);
			res.addAll(s);
		}
		return res;
	}
	/**
	 * Read proxies from www.freeproxy-list.ru
	 * @return list of proxies read
	 * @throws Exception
	 */
	public static LinkedList<String> readFreeProxyList() throws Exception{
		HttpURLConnection con = (HttpURLConnection) new java.net.URL(URL_FREEPROXY_LIST).openConnection();
		return CommonUtils.readIS(con.getInputStream());
	}
	
}
