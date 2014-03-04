package framework.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.tisson.pay.config.BftProperties;
import com.tisson.pay.config.MsgProperties;

import svcPostSocket.SocketSrvConfig;

import common.algorithm.RSA;
import common.algorithm.RSACipher;
import common.algorithm.RSAConfig;
import common.dao.TInfErrorCodeDao;
import common.platform.provider.bean.SocketConfig;
import common.socket.bean.SocketClientConfig;
import common.utils.Charset;
import common.utils.CommonLogger;
import common.utils.DownloadTool;
import common.utils.NetcapkiUtil;
import common.utils.TInfErrorCodeUtil;
import common.utils.verify.UtilProperties;

public class ConfigReader {

	private static final Logger LOG = Logger.getLogger(ConfigReader.class);
	
	private ServletContext context;

	private String realAppPath;

	private String delim;

	private String classesPath;
	
    public static String CONNECT_BACKMERCHANT_TIMELIMIT;
    
    public static String WAIT_BACKMERCHANT_RES_TIMELIMIT;
    
    public static String PAYWAP_TIMELIMIT;
	
	public ConfigReader() {
		// TODO Auto-generated constructor stub
	}

	public ConfigReader(ServletContext application) {
		// TODO Auto-generated constructor stub
		context = application;
		realAppPath = application.getRealPath("/");
		delim = System.getProperty("file.separator");
		classesPath = realAppPath + "WEB-INF" + delim + "classes" + delim;
		
	}

	private void initCommonLogger() {
//		CommonLogger.init(classesPath + GlobalConstants.LOG4J_CONF_NAME);
		CommonLogger.init(GlobalConstants.LOG4J_CONF_NAME);
		LOG.info("初始化CommonLogger...完成".intern());
	}
	
	public final void initPcpfRsaKey() {
		try {
			RSACipher.setPublicKey(classesPath + "pcpf_publickey.der"); // RSA证书
			RSACipher.setPrivateKey(classesPath + "pcpf_privatekey.der");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		LOG.info("初始化PCPF的RSA证书...完成");
	}
	

	/**
	 * @see 返回propFile的配置
	 * @author lyz
	 * @param propFile
	 * @return
	 */
	public static Properties readConfigbak(String propFile) {
		Properties p = new Properties();
		try {
			p.load(ConfigReader.class.getResourceAsStream(propFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
	/**
	 * @see 返回propFile的配置
	 * @author lyz
	 * @param propFile
	 * @return
	 */
	public static Properties readConfig(String propFile) {
		Properties p = new Properties();
		FileInputStream input = null;
		try {
			input = new FileInputStream(propFile);
			p.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(input != null){
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return p;
	}
	
	/**
	 * @see 返回propFile的配置
	 * @author GuoHong Zhao
	 * @param propFile
	 * @return
	 */
	public final void initSocketSrv() {
		try {
			Map param = new HashMap<String,String>();
			
			Properties p = ConfigReader.readConfig(GlobalConstants.ROUTE_CONF);
	        Enumeration en = p.propertyNames();
	        while (en.hasMoreElements()) {
	        	String key = (String) en.nextElement();
	            String Property = p.getProperty (key);
	            param.put(key, Property);
	            LOG.info("key:"+key+" value:"+Property);
	        }
	        SocketSrvConfig.setParam(param);
		}catch (Exception e) {
			e.printStackTrace();
		}
		LOG.info("初始化Route配置...完成");
	}

	public final void initSocket() {
		try {
			Properties p = ConfigReader.readConfig(GlobalConstants.SOCKET_CONF);
			String connTimeout = p.getProperty("CONNECTTIMEOUT");
			String sndTimeout = p.getProperty("SENDTIMEOUT");
			SocketConfig.setConnectTimeout(Integer.parseInt(connTimeout));
			SocketConfig.setSendTimeout(Integer.parseInt(sndTimeout));
			LOG.info(">>> CONNECTTIMEOUT: " + connTimeout);
			LOG.info(">>> SENDTIMEOUT: " + sndTimeout);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		LOG.info("初始化Socket配置...完成");
	}
	
	public final void initSocketClientConfig(){
		try {
			Properties p = ConfigReader.readConfig(GlobalConstants.SOCKET_CLIENT_CONFIG);
			/**
			 * 获取配置文件中的值
			 */
			String socketServerIp = p.getProperty("SOCKETSERVERIP");
			SocketClientConfig.setSocketServerIp(socketServerIp);
			int socketServerPort = Integer.valueOf(p.getProperty("SOCKETSERVERPORT"));
			SocketClientConfig.setSocketServerPort(socketServerPort);
			int connectTimeOut = Integer.valueOf(p.getProperty("CONNECTTIMEOUT"));
			SocketClientConfig.setConnectTimeOut(connectTimeOut);
			int soTimeout = Integer.valueOf(p.getProperty("SOTIMEOUT"));
			SocketClientConfig.setSoTimeout(soTimeout);
			int soLinger = Integer.valueOf(p.getProperty("SOLINGER"));
			SocketClientConfig.setSoLinger(soLinger);
			int recBufferSize = Integer.valueOf(p.getProperty("RECBUFFERSIZE"));
			SocketClientConfig.setRecBufferSize(recBufferSize);
			int sendBufferSize = Integer.valueOf(p.getProperty("SENDBUFFERSIZE"));
			SocketClientConfig.setSendBufferSize(sendBufferSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.info("初始化SocketClientConfig...完成");
	}
	
	public final void initCert() {
		try {
			String keyFile = classesPath + "pubkey.der"; // RSA证书
			byte[] bArr = RSA.loadKeyFile(keyFile);
			String keyHex = Charset.bytes2hex(bArr);
			RSAConfig.setKeyHex(keyHex);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		LOG.info("初始化交易平台的RSA证书...完成");
	}
	
	public final void initDownloadTool() {
		try {
			Properties p = ConfigReader.readConfig(GlobalConstants.DOWNLOAD_CONF);
			String url = p.getProperty("DOWNLOADJSPURL");
			
			DownloadTool.DOWNLOADJSPURL = url;
			//
			LOG.info(">>> DOWNLOADJSPURL: " + url);
			
			Properties p1 = ConfigReader.readConfig(GlobalConstants.DOWNLOAD_CONF);
			String documentPath = p1.getProperty("DOWNLOADPATH");
			
			DownloadTool.DOWNLOADPATH = documentPath;
			//
			LOG.info(">>> DOWNLOADPATH: " + documentPath);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public final void initPayWap(){
		
		try {
			Properties p = ConfigReader.readConfig(GlobalConstants.PAYWAP_CONF);
			String ct = p.getProperty("CONNECT_BACKMERCHANT_TIMELIMIT");
			String wt = p.getProperty("WAIT_BACKMERCHANT_RES_TIMELIMIT");
			String pt = p.getProperty("PAYWAP_TIMELIMIT");
			String account = p.getProperty("PAYWAP_ACCOUNT");
			String password = p.getProperty("PAYWAP_PASSWORD");
			String url= p.getProperty("PAYWAP_URL");
			String pageparampath=p.getProperty("PAGE_PARAM_PATH");
			
			PayWapConfig.setCONNECT_BACKMERCHANT_TIMELIMIT(ct);
			PayWapConfig.setWAIT_BACKMERCHANT_RES_TIMELIMIT(wt);
			PayWapConfig.setPAYWAP_TIMELIMIT(pt);
			PayWapConfig.setPAYWAP_ACCOUNT(account);
			PayWapConfig.setPAYWAP_PASSWORD(password);
			PayWapConfig.setPAYWAP_URL(url);
			PayWapConfig.setPAGE_PARAM_PATH(pageparampath);
			
			LOG.info(">>> CONNECT_BACKMERCHANT_TIMELIMIT: " + ct);
			LOG.info(">>> WAIT_BACKMERCHANT_RES_TIMELIMIT: " + wt);
			LOG.info(">>> PAYWAP_TIMELIMIT: " + pt);
			LOG.info(">>> PAGE_PARAM_PATH: " + pageparampath);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		LOG.info("初始化PayWap配置...完成");
	}
      public final void initActionInfo(){
		
		try {
			Properties p = ConfigReader.readConfig(GlobalConstants.ACTIONINFO_CONF);
			String INF02010_PRO_ID = p.getProperty("INF02010_PRO_ID");
			ActionInfoConfig.setINF02010_PRO_ID(INF02010_PRO_ID);
			LOG.info(">>> INF02010_PRO_ID: " + INF02010_PRO_ID);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		LOG.info("初始化ActionInfo配置...完成");
	}
	
    //错误码转义写入内存
    public final void initError(){
    	TInfErrorCodeDao dao = new TInfErrorCodeDao();
		TInfErrorCodeUtil.map=dao.getErrorMap();
    }
    /**
     * 帮付通配置文件初始化
     */
    public final void initBftConfig(){
    	try {
			Properties p = ConfigReader.readConfig(GlobalConstants.BFT_CONF);
			String keyStore = p.getProperty("KeyStore");
			String hostURL = p.getProperty("HostURL");
			String merId = p.getProperty("MerId");
			String merName = p.getProperty("MerName");
			String merHomeUrl = p.getProperty("MerHomeUrl");
			String merBackUrl = p.getProperty("MerBackUrl");
			String orderAmount = p.getProperty("OrderAmount");
			BftProperties.setKeyStore(keyStore);
			BftProperties.setHostURL(hostURL);
			BftProperties.setMerId(merId);
			BftProperties.setMerName(merName);
			BftProperties.setMerHomeUrl(merHomeUrl);
			BftProperties.setMerBackUrl(merBackUrl);
			BftProperties.setOrderAmount(orderAmount);
			LOG.info(">>> keyStore: " + keyStore);
			LOG.info(">>> hostURL: " + hostURL);
			LOG.info(">>> merId: " + merId);
			LOG.info(">>> merName: " + merName);
			LOG.info(">>> merHomeUrl: " + merHomeUrl);
			LOG.info(">>> merBackUrl: " + merBackUrl);
			LOG.info(">>> orderAmount: " + orderAmount);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		LOG.info("初始化BftProperties配置...完成");
    }
    /**
     * 初始化证书
     * @throws Exception 
     */
    public final void initNETCAPKI() {
    	try {
			String srvKeyStore = UtilProperties.getKeyStore();
			String srvKeyStorePwd = UtilProperties.getKeyStorePwd();
			String srvAlias = UtilProperties.getAlias();
			if (srvKeyStore.length() <= 0) {
				throw new Exception("配置文件中keystore 未配置");
			}

			if (srvAlias.length() <= 0) {
				throw new Exception("配置文件中alias 未配置");
			}
			if (srvKeyStorePwd.length() <= 0) {
				throw new Exception("配置文件中password 未配置");
			}

			File file = new File(srvKeyStore);
			if (!file.exists()) {
				throw new Exception("配置文件中配置的服务器证书不存在");
			}
			java.security.KeyStore keystore = java.security.KeyStore
					.getInstance("JKS");
			InputStream fileinputstream = new FileInputStream(file);
			keystore.load(fileinputstream, srvKeyStorePwd.toCharArray());
			fileinputstream.close();
			NetcapkiUtil.x509Certificate=(X509Certificate) keystore.getCertificate(srvAlias);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * 短信模板配置文件初始化
     */
    public final void initMsgConfig(){
    	try {
			Properties p = ConfigReader.readConfig(GlobalConstants.MSG_CONF);
			String VCODE_LOGIN=p.getProperty("VCODE_LOGIN");
			String VCODE_REG=p.getProperty("VCODE_REG");
			String VCODE_PASS_R_PAY=p.getProperty("VCODE_PASS_R_PAY");
			String VCODE_PASS_E_PAY=p.getProperty("VCODE_PASS_E_PAY");
		    String VCODE_PASS_E_LOGIN=p.getProperty("VCODE_PASS_E_LOGIN");
			String VCODE_PASS_R_LOGIN=p.getProperty("VCODE_PASS_R_LOGIN");
			String MSG_REG=p.getProperty("MSG_REG");
			String MSG_PASS_E_PAY=p.getProperty("MSG_PASS_E_PAY");
			String MSG_PASS_R_PAY=p.getProperty("MSG_PASS_R_PAY");
			String MSG_PASS_E_LOGIN=p.getProperty("MSG_PASS_E_LOGIN");
			String MSG_PASS_R_LOGIN=p.getProperty("MSG_PASS_R_LOGIN");
			MsgProperties.setMSG_PASS_E_LOGIN(MSG_PASS_E_LOGIN);
			MsgProperties.setMSG_PASS_E_PAY(MSG_PASS_E_PAY);
			MsgProperties.setMSG_PASS_R_PAY(MSG_PASS_R_PAY);
			MsgProperties.setMSG_PASS_R_LOGIN(MSG_PASS_R_LOGIN);
			MsgProperties.setMSG_REG(MSG_REG);
			MsgProperties.setVCODE_LOGIN(VCODE_LOGIN);
			MsgProperties.setVCODE_PASS_E_LOGIN(VCODE_PASS_E_LOGIN);
			MsgProperties.setVCODE_PASS_E_PAY(VCODE_PASS_E_PAY);
			MsgProperties.setVCODE_PASS_R_LOGIN(VCODE_PASS_R_LOGIN);
			MsgProperties.setVCODE_PASS_R_PAY(VCODE_PASS_R_PAY);
			MsgProperties.setVCODE_REG(VCODE_REG);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		LOG.info("初始化msgtemplet.properties配置...完成");
    }
    /**
     * 短信模板配置文件初始化
     */
    public final void initConstantApp(){
    	try {
			Properties p = ConfigReader.readConfig(GlobalConstants.APP_ENV_SCENE_CONFIG);
			String APP_ENV_SCENE=p.getProperty("env.scene");
			GlobalConstants.APP_ENV_SCENE=APP_ENV_SCENE;
			LOG.info(">>> APP_ENV_SCENE: " + APP_ENV_SCENE);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		LOG.info("初始化APP_ENV_SCENE_CONFIG配置...完成");
    }
	public final void initStartUp() {
		initConstantApp();
		initCommonLogger();
		initDownloadTool();
		initSocketSrv();
		initSocket();
		initPayWap();
		initActionInfo();
		initBftConfig();
		initMsgConfig();
		initPcpfRsaKey();
		initNETCAPKI();
		initCert();
		initError();
	//	initSocketClientConfig();
	}

}
