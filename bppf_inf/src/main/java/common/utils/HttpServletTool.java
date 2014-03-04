package common.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpServletTool {
	
	private static final Log log = LogFactory.getLog(HttpServletTool.class);
	
	private static final String HEADER_NAME = "x-up-calling-line-id";
	
	public static void printHeaders(HttpServletRequest req) {
		CommonLogger.info("=============HttpHeader=====================");
		Enumeration headerNames = req.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();
			CommonLogger.info(headerName + ":" + req.getHeader(headerName));
		}

		CommonLogger.info("============================================");
	}

	public static void out(HttpServletResponse response, String msg)
			throws IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.println(msg);
		out.flush();
		out.close();
	}
	
	/**
	 * 从http头中获取手机号码
	 * @return
	 */
	public static String getPhonefromHead(HttpServletRequest request)  {  
		
		String mobile;
		
		/*Enumeration enu2=request.getHeaderNames();  
		while(enu2.hasMoreElements()){        
			String names=(String)enu2.nextElement();         
			if(log.isInfoEnabled()){
				log.info("RequestHeadName:<" + names+"> value:<"+request.getHeader(names)+">");
			}   
		}  */
		
		try{   
			mobile = request.getHeader(HEADER_NAME);          
		}catch(Exception e){    
			e.printStackTrace();    
			mobile = "";      
		}   
		
		if(mobile == null)   
			mobile = ""; 
		
		//长度大于11位取后11位
		if(!Charset.isEmpty(mobile) && mobile.length() > 11){
			int len = mobile.length();
			mobile = mobile.substring(len-11);
		}
		
		if(log.isInfoEnabled()){
			log.info("**************************************************************");
			log.info("发送手机号码:"+mobile);
			log.info("***************************************************************");
		}   
		return mobile;     
	}  

}
