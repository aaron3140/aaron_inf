/**
 *
 */
package cn.tisson.intf.core;

import cn.tisson.intf.core.svc.BaseService;
import cn.tisson.intf.core.ApplicationContextHolder;

import common.xml.ResponseVerifyAdder;

import framework.exception.ExceptionHandler;
import framework.exception.XmlINFException;

/**
 * @author Foging
 * @description
 */
public class WSCallAgent {

	public String dispatchCommand(String in0,String in1) {
		try {



			String wbSvrCode = in0.substring(0, in0.indexOf("|"));

			String out = null;
			//调用服务分支
			BaseService service = (BaseService) ApplicationContextHolder
			.getBean(wbSvrCode + "SVC");

			 out = (String)service.execute();

			 //返回包添加签名信息
			return ResponseVerifyAdder.pkg(out);

		} catch (Exception e) {
			System.out.println(e.toString());
			return ResponseVerifyAdder.pkg(ExceptionHandler.toXML(new XmlINFException(null, e), null));
		}
	}

	/**
	 * @description
	 * @param args
	 */
	public static void main(String[] args) {
		WSCallAgent callagent = new WSCallAgent();
		callagent.dispatchCommand("INF01006|", "xxx");
		System.out.println("aldfjasuwo");

	}

}
