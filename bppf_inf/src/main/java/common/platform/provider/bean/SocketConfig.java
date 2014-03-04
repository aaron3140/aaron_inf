/**
 * payeasy
 * tables
 * SocketConfig.java
 * @author Khaos
 * @date Jul 11, 2008
 */
package common.platform.provider.bean;

/**
 * @author Khaos
 *
 */
public class SocketConfig {
	
	private static String SOCKIP = "";
	private static int SOCKPORT = -1;
	
	private static String GWMIP = "";
	private static int GWMPORT = -1;
	
	private static String ISPIP = "";
	private static int ISPPORT = -1;
	
	private static int CONNECTTIMEOUT = 15000;
	private static int SENDTIMEOUT = 70000;	
	
	private static String BACKUPIP = "";
	private static int BACKUPPORT = 8555;
	
	private static boolean USEBACKUP = false;
	
	public static String getISPIP() {
		return ISPIP;
	}

	public static void setISPIP(String ispip) {
		ISPIP = ispip;
	}

	public static int getISPPORT() {
		return ISPPORT;
	}

	public static void setISPPORT(int ispport) {
		ISPPORT = ispport;
	}

	public static void setGwmIp(String gwmIp) {
		GWMIP = gwmIp;
	}
	
	public static String getGwmIp() {
		return GWMIP;
	}
	
	public static void setGwmPort(int gwmPort) {
		GWMPORT = gwmPort;
	}
	
	public static int getGwmPort() {
		return GWMPORT;
	}
	
	public static void setConnectTimeout(int connecttimeout) {
		CONNECTTIMEOUT = connecttimeout;
	}
	
	public static int getConnectTimeout() {
		return CONNECTTIMEOUT;
	}
	
	public static void setSendTimeout(int sendtimeout) {
		SENDTIMEOUT = sendtimeout;
	}
	
	public static int getSendTimeout() {
		return SENDTIMEOUT;
	}
	
	/**
	 * @return the sockIp
	 */
	static public String getSockIp() {
		return SOCKIP;
	}
	/**
	 * @param SOCKIP the sockIp to set
	 */
	static public void setSockIp(String sIp) {
		SOCKIP = sIp;
	}
	/**
	 * @return the sockPort
	 */
	static public int getSockPort() {
		return SOCKPORT;
	}
	/**
	 * @param SOCKPORT the sockPort to set
	 */
	static public void setSockPort(int sPort) {
		SOCKPORT = sPort;
	}

	public static String getBackUpIp() {
		return BACKUPIP;
	}

	public static void setBackUpIp(String backUpIp) {
		BACKUPIP = backUpIp;
	}

	public static int getBackUpPort() {
		return BACKUPPORT;
	}

	public static void setBackUpPort(int backUpPort) {
		BACKUPPORT = backUpPort;
	}

	public static boolean isUseBackUp() {
		return USEBACKUP;
	}

	public static void setUseBackUp(boolean useBackUp) {
		USEBACKUP = useBackUp;
	}
	
	
}
