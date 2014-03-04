package com.tisson.httpIposSvc;

import framework.exception.ExceptionHandler;
import framework.exception.INFException;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.utils.Charset;
import common.utils.HttpServletTool;
import common.utils.JSONTool;

public class HttpIposDispatcher {

	public static String PKG_NAME = HttpIposDispatcher.class.getPackage().getName();

	public static String CLASS_NAME = "DealProcessIposSvc";
	
	private static final Log log = LogFactory.getLog(HttpIposDispatcher.class);
	
	/**
	 * 分派ipos调用请求
	 * 
	 * @version: 1.00
	 * @history: 2010-12-23 下午3:46:22 [created]
	 * @author He WenFeng 和文锋
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @see
	 */
	public static void dispatch(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		HttpServletRequest httpReq = (HttpServletRequest) request;
		String reqMethod = httpReq.getParameter("method");
		reqMethod = Charset.isEmpty(reqMethod) ?  httpReq.getParameter("METHOD") : reqMethod ;
		
		try {

			if(Charset.isEmpty(reqMethod))
				throw new Exception("传入method不能为空");
			
			reqMethod = Charset.getFuncName(reqMethod,"\\.");
		
			Class<?> svcClass = Class.forName(PKG_NAME + "." + CLASS_NAME);
			// request.getClass() 不返回 HttpServletRequest.class	
			
			Method method = svcClass.getMethod(reqMethod,
					HttpServletRequest.class);
			String json = (String) method.invoke(svcClass.newInstance(),
					request);
					
			HttpServletTool.out(response, json);
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			HttpServletTool.out(response, JSONTool.createStandardErrJsonStr(ExceptionHandler.GWM_MTP_ERRCODE, reqMethod+"接口不存在" , ""));
		}catch (INFException e) {
			e.printStackTrace();
			HttpServletTool.out(response, JSONTool.createStandardErrJsonStr(e.getErrCode(), e.getErrReason() ,""));
		}catch(Exception e){
			e.printStackTrace();
			HttpServletTool.out(response, JSONTool.createStandardErrJsonStr(ExceptionHandler.GWM_MTP_ERRCODE, e.getMessage(),""));
		}
	}
}
