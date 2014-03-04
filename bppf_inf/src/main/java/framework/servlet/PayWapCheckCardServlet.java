package framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.algorithm.RSACipher;
import common.utils.Charset;
import common.utils.WebSvcTool;
import common.xml.dp.DpPaymentResponse;

import framework.exception.ExceptionHandler;
import framework.exception.XmlINFException;

public class PayWapCheckCardServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(arg0, arg1);
	}

	@Override
	protected void doPost(HttpServletRequest hReq, HttpServletResponse hRes)
			throws ServletException, IOException {
		String cardNo = getRequestParam(hReq, "CARDNO");
		String cardType = getRequestParam(hReq, "CARDTYPE");
		String cardPwd = getRequestParam(hReq, "CARDPWD");
		try {
			String decryptPwd = RSACipher.decryptByPrivateKey(cardPwd);
			WebSvcTool.callPCD0002(cardNo, decryptPwd, cardType);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			DpPaymentResponse resp = new DpPaymentResponse();
			String resultXml = ExceptionHandler.toXML(new XmlINFException(
					resp, e), null);
			Document document;
			try {
				document = DocumentHelper.parseText(resultXml);
				Element e1 = (Element) document.selectSingleNode("//"
						+ "RESPONSECODE");
				String responsecode = e1.getTextTrim();
				Element e2 = (Element) document.selectSingleNode("//"
						+ "RESPONSECONTENT");
				String responsecontent = "";
				if (e2 != null) {
					responsecontent = e2.getTextTrim();
				}
				if(!"000000".equals(responsecode)){
					hRes.setCharacterEncoding("utf-8");
					PrintWriter out = hRes.getWriter();
//					输出数据
					out.println(responsecontent);
					out.flush();
					out.close();
				}
			} catch (DocumentException e3) {
				hReq.setAttribute("exceptions", e3.getMessage());
				hReq.getRequestDispatcher("/pay/payWapError.jsp").forward(hReq,
						hRes);
			}
			
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	/**
	 * 获取传递过来的参数值
	 * 
	 * @version: 1.00
	 * @history: 2010-12-27 下午3:56:24 [created]
	 * @author wenhg
	 * @param request
	 * @param name
	 *            参数名
	 * @return String
	 * @throws Exception
	 * @see
	 */
	private static String getRequestParam(HttpServletRequest request,
			String name) {
		String value = request.getParameter(name);
		if (Charset.isEmpty(value)) {
			return "";
		} else {
			return value.trim();
		}
	}
}
