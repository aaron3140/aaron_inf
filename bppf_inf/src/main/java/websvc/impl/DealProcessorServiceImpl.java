package websvc.impl;

import java.lang.reflect.Method;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import websvc.DealProcessorService;
import websvc.InfService;

import common.utils.SpringContextHelper;
import common.xml.ResponseVerifyAdder;

import framework.exception.ExceptionHandler;
import framework.exception.XmlINFException;

@WebService(targetNamespace = "http://websvc/")
public class DealProcessorServiceImpl implements DealProcessorService {
	private static final Log log = LogFactory
			.getLog(DealProcessorServiceImpl.class);

	private static final String PKG_NAME = "websvc.servant";
	private static final String METHODNAME = "execute";
	private static final String METHODNAMEFORMD5 = "executeForMD5";

	InfService service;
	
	/**
	 * 统一接口
	 * 
	 * @version 1.00
	 * @history: 2011-01-18 上午11:40:55 [created]
	 * @param dispatchCommandRequest
	 * @return DispatchCommandResponse
	 * @see
	 */
	public String dispatchCommand(String in0, String in1) {

//		String p = in0.substring(0, 8);
//		if (!("INF06101".equalsIgnoreCase(p) || "INF06102".equalsIgnoreCase(p) || "INF06103"
//				.equalsIgnoreCase(p))) {
//			log.info("请求接口 : " + in0 + " 请求报文 : " + in1);
//		}else
		log.info("请求接口 : " + in0 + " 请求报文 : " + in1);
		String out = null;
		try {
			String wbSvrCode = in0.substring(0, in0.indexOf("|"));

			Class<?> svcClass = null;
			// 调用服务分支
			if (wbSvrCode.contains("INF")) {
				svcClass = Class.forName(PKG_NAME + "." + wbSvrCode);
			} else {
				svcClass = Class.forName(PKG_NAME + ".INF_" + wbSvrCode);
			}
			Object[] args = new Object[] { in0, in1 };
			Class[] argsClass = new Class[2];
			for (int i = 0, j = 2; i < j; i++) {
				argsClass[i] = args[i].getClass();
			}

			Method method = svcClass.getMethod(METHODNAME, argsClass);

			out = (String) method.invoke(null, args);
			// 返回包添加签名信息
			out = ResponseVerifyAdder.pkg(out);
			log.info("返回报文 : " + out);
			return out;

		} catch (Exception e) {
			out = ResponseVerifyAdder.pkg(ExceptionHandler.toXML(
					new XmlINFException(null, e), null));
			log.info("返回报文 : " + out);
			return out;
		}
	}

	/**
	 * 统一接口
	 * 
	 * @version 1.00
	 * @history: 2011-01-18 上午11:40:55 [created]
	 * @param dispatchCommandRequest
	 * @return DispatchCommandResponse
	 * @see
	 */
	public String dispatchCommandEXT(String in0, String in1) {
		log.info("客户端请求接口 : " + in0 + " 客户端请求报文 : " + in1);
		String out = null;
		try {
			String wbSvrCode = in0.substring(0, in0.indexOf("|"));
			Class<?> svcClass = null;
			// 调用服务分支
			if (wbSvrCode.contains("INF")) {
				svcClass = Class.forName(PKG_NAME + "." + wbSvrCode);
			} else {
				svcClass = Class.forName(PKG_NAME + ".INF_" + wbSvrCode);
			}
			Object[] args = new Object[] { in0, in1 };
			Class[] argsClass = new Class[2];
			for (int i = 0, j = 2; i < j; i++) {
				argsClass[i] = args[i].getClass();
			}
			Method method = svcClass.getMethod(METHODNAMEFORMD5, argsClass);
			out = (String) method.invoke(null, args);
			log.info("返回客户端报文 : " + out);
			// 返回包添加签名信息
			return out;
		} catch (Exception e) {
			out = ResponseVerifyAdder.pkg(ExceptionHandler.toXML(
					new XmlINFException(null, e), null));
			log.info("返回客户端报文 : " + out);
			return out;
		}
	}

	/**
	 * 统一接口
	 * 
	 * @version 1.00
	 * @history: 2011-01-18 上午11:40:55 [created]
	 * @param dispatchCommandRequest
	 * @return DispatchCommandResponse
	 * @see
	 */
	public String dispatchCommandIPOS(String in0, String in1) {
		log.info("IPOS客户端请求接口 : " + in0 + " 请求报文 : " + in1);
		String out = null;
		String wbSvrCode ="";
		try {
			  wbSvrCode = in0.substring(0, in0.indexOf("|"));
			Class<?> svcClass = null;
			try {
				svcClass = Class.forName(PKG_NAME + "." + wbSvrCode);
				Method method = svcClass.getMethod(METHODNAME, new Class[]{String.class,String.class});
				out = (String) method.invoke(null, new Object[]{in0,in1});
			} catch (ClassNotFoundException e) {
				String msgPackage="websvc.service";
				log.info("消息接口类接入路径："+msgPackage);
				ApplicationContext context =  SpringContextHelper.context;
				service = context.getBean(wbSvrCode, InfService.class);
				out = service.execute(in1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			out = ResponseVerifyAdder.pkg(ExceptionHandler.toXML(
					new XmlINFException(null, e), null));
		}
		log.info("返回IPOS客户端报文 : " + out);
		return out;
	}
	
	@Override
	public String dispatchCommandJsonLibIPOS(String in0, String in1) {
		log.info("IPOS客户端 请求报文 : " + in1);
		String out = null;
		String wbSvrCode =in0;
		try {
//			AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//			context.scan("websvc.servant");
//			service = context.getBean(wbSvrCode, InfService.class);
			
//			@SuppressWarnings("resource")
//			ApplicationContext context = new ClassPathXmlApplicationContext("/profile/applicationContext-dao.xml");
//			service = context.getBean(wbSvrCode, InfService.class);
			
			ApplicationContext context =  SpringContextHelper.context;
			service = context.getBean(wbSvrCode, InfService.class);
			
			out = service.execute(in1);
		} catch (Exception e) {
			e.printStackTrace();
			out = ResponseVerifyAdder.pkg(ExceptionHandler.toXML(
					new XmlINFException(null, e), null));
		}
		log.info("返回IPOS客户端报文 : " + out);
		return out;
	}

}
