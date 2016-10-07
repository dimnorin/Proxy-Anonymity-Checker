package dim.scrapper.olx.util;

public class Constants {
	public static final String NL 						= System.getProperty("line.separator");
	public static final String TAB 						= "\t";

	public static final String DEFAULT_USER_AGENT 		= "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
	public static final String REFERER_GOOGLE 			= "http://google.com";

	public static final int READ_TIMEOUT				= 20 * 1000; // 30 sec

	public static final String CHARSET_UTF8				= "UTF-8";
	public static final String CHARSET_CP1251			= "CP1251";

	public static final int PROXY_POOL_MAX_SIZE		= 30;
	public static final int STACK_TRACE_DEPTH			= 2;
}
