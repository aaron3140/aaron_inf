package common.utils.verify;

import java.net.URLDecoder;
import java.util.*;

import common.utils.Charset;

import framework.config.ConfigReader;
import framework.config.GlobalConstants;

public class UtilProperties {
  
	public static 	String keyStore ="";
	public static 	String alias ="";
	public static 	String password ="";
  	private static String getProperty(String propertyName) {
	    try {
	    	Properties prop = ConfigReader.readConfig(GlobalConstants.CER_CONF);
		    return prop.getProperty(propertyName);
	    }
	    catch (Exception e) {
	      return null;
	    }
	  }

  public static String getKeyStore() throws Exception {
	  if(Charset.isEmpty(keyStore, true)){
		  keyStore = GlobalConstants.CONFIG_FILE_PACKAGE + getProperty("keystore");
		  keyStore = URLDecoder.decode(keyStore, "UTF-8");
	  }
    return keyStore;
  }

  public static String getAlias() {
	  if(Charset.isEmpty(alias, true)){
		  alias = getProperty("alias");
	  }
    return alias;
  }

  public static String getKeyStorePwd() {
	  if(Charset.isEmpty(password, true)){
		  password = getProperty("password");
	  }
    return password;
  }

  public static void main(String arg[]) {
    //System.out.println(getPropertiesPath());
	 String classDir = UtilProperties.class.getResource("/").getPath();
	System.out.println(classDir);
    String keystorefile = getProperty("keystore");
    System.out.println(keystorefile);
  }

}
