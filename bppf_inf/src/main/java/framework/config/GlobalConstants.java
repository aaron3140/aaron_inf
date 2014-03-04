package framework.config;

public class GlobalConstants {

	// 配置文件所在包，以"/"开头，将包名的"."替换成"/"
//	public static final String CONFIG_FILE_PACKAGE = System.getenv("CONFIG_HOME")+"/profile/";
	
	public static final String CONFIG_FILE_PACKAGE = System.getenv("INFCONF_HOME")+"/profile/"; //工作空间路径
	
	//SP商户提供公钥文件所在包，将包名的"."替换成"/"
	public static final String SPKEY_FILE_PACKAGE = "spkey/";

	public static final String LOG4J_CONF_NAME = CONFIG_FILE_PACKAGE+"log4j.properties";

	// socket配置文件
	public static final String SOCKET_CONF = CONFIG_FILE_PACKAGE
			+ "socket.properties";
	
	// socketclientconfig配置文件
	public static final String SOCKET_CLIENT_CONFIG = CONFIG_FILE_PACKAGE + "socketclientconfig.properties";
	
	// 多Socket服务器配置文件
	public static final String ROUTE_CONF = CONFIG_FILE_PACKAGE
			+ "route.properties";
	
	// ftp配置文件
	public static final String FTP_CONF = CONFIG_FILE_PACKAGE
			+ "ftp.properties";

	// paywap配置文件
	public static final String PAYWAP_CONF = CONFIG_FILE_PACKAGE
			+ "paywap.properties";
	//业务编码,产品编码配置
	public static final String ACTIONINFO_CONF = CONFIG_FILE_PACKAGE
	+ "actioninfo.properties";
	
	// download配置文件
	public static final String DOWNLOAD_CONF = CONFIG_FILE_PACKAGE
			+ "download.properties";
	
	//手机号码验证配置
	public static final String VALIDATE_CONF = CONFIG_FILE_PACKAGE
	+ "validate.properties";
	
	//积分商城短信下发接口配置
	public static final String SMS_CONF = CONFIG_FILE_PACKAGE
	+ "sms.properties";
	//帮付通配置文件路径
	public static final String BFT_CONF = CONFIG_FILE_PACKAGE
	+ "MPI.properties";
	public static final String BFT_BANK_CODE_CONF = CONFIG_FILE_PACKAGE
	+ "bftbankcode.properties";
	//定时任务配置文件
	public static final String TASK_CONF = CONFIG_FILE_PACKAGE+"timerTask.properties";
	
	//证书配置文件
	public static final String CER_CONF = CONFIG_FILE_PACKAGE+"cert.properties";
	//短信模板配置文件
	public static final String MSG_CONF = CONFIG_FILE_PACKAGE+"msgtemplet.properties";
	/**
	 * 场景字段
	 */
	public static String APP_ENV_SCENE = "";
	public static String APP_ENV_SCENE_CONFIG = CONFIG_FILE_PACKAGE+"env.properties";
	
	//默认机构ID
	public static final String MERID_CONF = CONFIG_FILE_PACKAGE+"merId.properties";
	
	//T_SYM_ERROR表的MODULE_CODE
	public static final String MODULE_CODE_JF = "JF";
	
	public static final String MODULE_CODE_EP = "EP";
	
	public static final String MODULE_CODE_HB = "HB";
	
	public static final String MODULE_CODE_MEPF = "MEPF";
	
	public static final String MODULE_CODE_UDB = "UDB";
}
