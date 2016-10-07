package dim.scrapper.olx.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * Utility class for LOG, IO, Random operations
 */
public class CommonUtils {
	private static Logger LOG					= Logger.getLogger(CommonUtils.class.getName());
	private static ByteArrayOutputStream OUT 	= new ByteArrayOutputStream();
	private static Handler h;
	
	/**
	 * Init custom formatter
	 */
	static{
		try {
			Formatter f = new CustomFormatter(Constants.STACK_TRACE_DEPTH);
			h = new FileHandler("proxychecker-log.%u.%g.txt",1024 * 1024, 10, true);
			h.setFormatter(new SimpleFormatter());
			LOG.addHandler(h);
			h = new StreamHandler(OUT, f);
			LOG.addHandler(h);
			LOG.addHandler(new ConsoleHandler());
			LOG.setUseParentHandlers(false);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	// ----------------LOG-----------------
	/**
	 * Log string
	 * @param msg
	 */
	public static void log(String msg){
		LOG.info(String.format("%s >>> %s", Thread.currentThread().getName(), msg));
	}
	/**
	 * Log string with exception
	 * @param msg
	 * @param thrown
	 */
	public static void log(String msg, Exception thrown){
		msg = String.format("%s >>> %s", Thread.currentThread().getName(), msg);
		LOG.log(Level.SEVERE, msg, thrown);
	}
	/**
	 * Get application root folder in runtime
	 * @return absolute path to root folder
	 */
	public static String getRootFolder(){
		File f = new File(CommonUtils.class.getProtectionDomain().getCodeSource().getLocation()  
                .getFile());          
        return f.getParent() + File.separator ;
	}
	/**
	 * Read file into list
	 * @param filePath absolute path to file to be read
	 * @return list with file content splited by new line
	 * @throws IOException
	 */
	public static LinkedList<String> readFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
        return read(reader);
    }

	private static LinkedList<String> read(BufferedReader reader) throws IOException{
		LinkedList<String> list = new LinkedList<String>();
		String line;
        while((line=reader.readLine()) != null){
            list.add(line);
        }
        reader.close();
        return list;
	}
	/**
	 * Read input stream into string
	 * @param is input stream to read
	 * @return string with input stream contents
	 * @throws IOException
	 */
	public static String readInput(InputStream is) throws IOException{
		return readInput(is, "UTF-8");
	}
	/**
	 * Read input stream into string with specified charset
	 * @param is input stream to read
	 * @param charset 
	 * @return string with input stream contents
	 * @throws IOException
	 */
	public static String readInput(InputStream is, String charset) throws IOException{
		// read the response
		BufferedReader input = new BufferedReader(new InputStreamReader(is, charset));
	    int c;
	    StringBuilder resultBuf = new StringBuilder();
	    while ( (c = input.read()) != -1) {
	        resultBuf.append((char) c);
	    }
	    input.close();

	    return resultBuf.toString();
	}
	/**
	 * Read input stream into list trimed with new line
	 * @param is input stream to read
	 * @return list with input stream contents
	 * @throws IOException
	 */
	public static LinkedList<String> readIS(InputStream is) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		return read(reader);
	}
	/**
	 * Read string into list trimed with new line
	 * @param s string to be read
	 * @return list with input stream contents
	 * @throws IOException
	 */
	public static LinkedList<String> readString(String s) throws IOException{
		BufferedReader reader = new BufferedReader(
                new StringReader(s));
		return read(reader);
	}
	
	/**
	 * Read input stream into byte array
	 * @param is input stream to read
	 * @return byte array with input stream contents
	 * @throws IOException
	 */
	public static byte[] read(InputStream is) throws IOException{
		// read the response
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] data = new byte[16384];

	    while ((nRead = is.read(data, 0, data.length)) != -1) {
	      buffer.write(data, 0, nRead);
	    }

	    buffer.flush();

	    return buffer.toByteArray();
	}
	/**
	 * Write string to file
	 * @param file destination file
	 * @param s string to be written
	 * @param append if true, append string to existed content, if false - overwrite file
	 * @throws IOException
	 */
	public static void writeFile(File file, String s, boolean append) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
		bw.write(s);
		bw.close();
	}
	/**
	 * Write list to file
	 * @param sfile destination file absolute path
	 * @param a list to be written
	 * @param append if true, append string to existed content, if false - overwrite file
	 * @throws IOException
	 */
	public static void writeFile(String sfile, ArrayList a, boolean append) throws IOException{
		writeFile(new File(sfile), toString(a), append);
	}
	private static String toString(ArrayList a){
		StringBuilder sb = new StringBuilder();
		for(Object s : a){
			sb.append(s.toString()).append(Constants.NL);
		}
		return sb.toString();
	}
	
	/**
	 * Sleep random ms from min to max
	 * @param min in ms
	 * @param max in ms
	 */
	public static void sleepRand(int min,int max){
		sleepRand(min, max, 0);
	}
	/**
	 * Sleep random ms from min+base to max+base
	 * @param min in ms
	 * @param max in ms
	 * @param base in ms
	 */
	public static void sleepRand(int min, int max, int base){
		int rand = getRand(min, max);
		try {
			Thread.sleep(rand + base);
		} catch (Exception e) {		}
	}
	/**
	 * Get random int from min to max
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRand(int min,int max){
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}
}
