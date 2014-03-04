package svcPostSocket;

import java.util.HashMap;
import java.util.Map;

public class SocketSrvConfig {

	
	private static Map<String,String> param = null ;

	private static int servOffsetStart = 0;
	private static int servOffsetEnd = 0;
	private static int servLength = 0;
	
	
	public static int getServLength() {
		return servLength;
	}

	public static void setServLength(int servLength) {
		SocketSrvConfig.servLength = servLength;
	}

	public static int getServOffsetEnd() {
		return servOffsetEnd;
	}

	public static void setServOffsetEnd(int servOffsetEnd) {
		SocketSrvConfig.servOffsetEnd = servOffsetEnd;
	}

	public static int getServOffsetStart() {
		return servOffsetStart;
	}

	public static void setServOffsetStart(int servOffsetStart) {
		SocketSrvConfig.servOffsetStart = servOffsetStart;
	}

	public static Map<String, String> getParam() {
		return param;
	}

	public static void setParam(Map<String, String> param) {
		SocketSrvConfig.param = param;
		if(param==null) {
			param = new HashMap<String,String>(0);
		}
		//init();
	}
	
	public static void init() {
		
		try {
			String iOffset = param.get("SERVOFFSET");
			String iLength = param.get("SERVLENGTH");
			servOffsetStart = Integer.parseInt(iOffset);
			servLength = Integer.parseInt(iLength);
			servOffsetEnd = servOffsetStart + servLength;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
