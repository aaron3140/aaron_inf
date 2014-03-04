package common.utils;

public class DownloadTool {	
	public static  String DOWNLOADPATH = "/u1/upload";
	public static  String DOWNLOADJSPURL = "https://enterprise.bestpay.com.cn/bppf_inf/fileDownLoadServlet.do?fileId=";
	
	public static String getCommonUrl(String fileId) {
		return DOWNLOADJSPURL + fileId;
	}
	
	public static String getAppvfUrl(String fileId, String appvfId) {
		return DOWNLOADJSPURL + fileId + "&appvfId=" + appvfId;
	}
	
	public static String getClientUrl(String fileId, String clientId) {
		return DOWNLOADJSPURL + fileId + "&clientId=" + clientId;
	}
	public static String getClientUrl(String fileName) {
		return DOWNLOADJSPURL + fileName;
	}
}
