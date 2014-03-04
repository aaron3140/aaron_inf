/**
 * 
 */
package common.algorithm;

/**
 * @author lyz
 *
 */
public class RSAConfig {

	private static String keyHex = "";
	
	private static String switcher = "on";

	public static String getKeyHex() {
		return keyHex;
	}

	public static void setKeyHex(String keyHex) {
		RSAConfig.keyHex = keyHex;
	}

	public static String getSwitcher() {
		return switcher;
	}

	public static void setSwitcher(String switcher) {
		RSAConfig.switcher = switcher;
	}
	
}
