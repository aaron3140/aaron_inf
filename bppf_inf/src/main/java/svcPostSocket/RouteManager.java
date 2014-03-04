package svcPostSocket;

/**
 * 
 * @version: 1.00
 * @history: 2011-12-7 15:53:17 [created]
 * @author GuoHong Zhao
 * @param args
 * @see
 */
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import svcPostSocket.SocketSrvConfig;

import common.platform.provider.client.SocketSend;

public class RouteManager {

	private String srvIP="";
	private int srvPort = -1;
	
	/**
	 * socket数据包路由选择
	 * 
	 * @version: 1.00
	 * @history: 2011-12-8 上午09:37:28 [created]
	 * @author GuoHong Zhao
	 * @param sendData
	 * @return
	 * @throws Exception
	 * @see
	 */
	@Deprecated
	public String route(String sendData) throws Exception {
		String receiveData = "";
		int start = SocketSrvConfig.getServOffsetStart();
		int end = SocketSrvConfig.getServOffsetEnd();
		String serv = sendData.substring(start,end);
		
		Map<String,String> param = SocketSrvConfig.getParam();
		
		String srvName = param.get(serv);
		srvIP = param.get(srvName+"IP");
		srvIP = srvIP==null?"":srvIP;
		
		String strPort = param.get(srvName+"PORT");
		srvPort =Integer.parseInt(strPort==null?"-1":strPort);
		
		SocketSend ss = new SocketSend(srvIP,srvPort);
		ss.SendData(sendData);
		receiveData = ss.ReceiveData();
		return receiveData;		
		
	}

	/**
	 * 调用者手动选定发送的服务器
	 * @param server 例如配置文件route.properties的CUM0001_BIS=EAS<br>等号的右值
	 * @param sendData 发送的Socket数据包
	 * @return 服务端返回来的Socket数据包
	 * @throws Exception
	 */
	public String route(String server,String sendData)throws Exception {
		String receiveData = "";
		int start = SocketSrvConfig.getServOffsetStart();
		int end = SocketSrvConfig.getServOffsetEnd();
		//String serv = sendData.substring(start,end);
		server = server.toUpperCase();
		Map<String,String> param = SocketSrvConfig.getParam();
		
		//method 1, 从CUM0001+server取得等号的右值
		//String srvName = param.get( serv+server.toUpperCase() );
		
		//method 2,server相当于等号的右值
		String srvName = server;
		
		srvIP = param.get(srvName+"IP");
		srvIP = srvIP==null?"":srvIP;	
		String strPort = param.get(srvName+"PORT");
		srvPort =Integer.parseInt(strPort==null?"-1":strPort);
		
		SocketSend ss = new SocketSend(srvIP,srvPort);
		int receiveTimeOut = Integer.parseInt(param.get("RECEIVETIMEOUT"));
		ss.setReceiveTimeOut( receiveTimeOut );
		ss.SendData(sendData);
		receiveData = ss.ReceiveData();
		return receiveData;
	}
	
	
	@Deprecated
	public String process(String sendData) throws Exception{
		String receiveData = "";
		
		//String srvName = sendData.substring(22,25);
		//String srvFlag = sendData.substring(25, 29);
		String serv = sendData.substring(22,29);
		
		srvIP = loadPerp().get(loadPerp().get(serv)+"IP");
		srvIP = srvIP==null?"":srvIP;
		String strPort = loadPerp().get(loadPerp().get(serv)+"PORT");
		srvPort =Integer.parseInt(strPort==null?"-1":strPort);
		System.out.println("IP is: " + srvIP + "\nPort is: " + srvPort);
		/*if(srvName.equalsIgnoreCase("SCS")) {
			System.out.println("is SCS");
		}else if(srvName.equalsIgnoreCase("CUM")){
			System.out.println("is CUM");
		}else {
			return "";
		}*/
		return "end";
		//SocketSend ss = new SocketSend(srvIP,srvPort);
		//ss.SendData(sendData);
		//receiveData = ss.ReceiveData();
		//return receiveData;
	}
	
	@Deprecated
	public static Map<String,String> loadPerp() throws Exception {
		Map param = new HashMap<String,String>();
		InputStream in = new FileInputStream("F://socketSrv.properties");
		Properties p = new Properties();
		p.load(in);
        Enumeration en = p.propertyNames();
        while (en.hasMoreElements()) {
        	String key = (String) en.nextElement();
            String Property = p.getProperty (key);
            param.put(key, Property);
        }
        return param;
	}
	
	
	public static void main(String[] args) {

		RouteManager rm = new RouteManager();
		try {
			loadPerp();
			rm.process("FFFF585752110000000217EBK000111002001200000000000000000000000000111111110000010200010004400100020042���пۿ�ʧ�ܣ����нӿڷ���ʧ�ܣ����Ժ�����401001054002001511110801542341240040000E00400004101000041180012000017102836");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
