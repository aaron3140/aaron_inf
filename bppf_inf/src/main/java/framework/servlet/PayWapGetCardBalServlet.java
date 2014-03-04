package framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import websvc.DealProcessorService;

import common.algorithm.RSACipher;
import common.utils.Charset;
import common.utils.CurrencyTool;
import common.utils.SpringContextHelper;
import common.utils.WebSvcTool;
import common.xml.dp.DpPaymentResponse;
import framework.exception.ExceptionHandler;
import framework.exception.XmlINFException;

public class PayWapGetCardBalServlet extends HttpServlet {

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
		String xmlStr;
		PrintWriter output = null;
		String balance = "";
		String availBalance = "";
		String responsecontent = "";
		try {
			String cardNo = getAndcheckRequestParam(hReq, "CARDNO");
			String cardType = getAndcheckRequestParam(hReq, "CARDTYPE");
			String cardPwd = getAndcheckRequestParam(hReq, "CARDPWD");
			String decryptPwd = RSACipher.decryptByPrivateKey(cardPwd);
			WebSvcTool.callPCD0002(cardNo, decryptPwd, cardType);
			xmlStr = getXmlStr(hReq);

			DealProcessorService ds = (DealProcessorService) SpringContextHelper
					.getDealProcessorServiceBean();
			String wbSvrCode = "100006";
			
			String out = ds.dispatchCommand(wbSvrCode + "|123456", xmlStr);

			Document document = DocumentHelper.parseText(out);
			Element e = (Element) document.selectSingleNode("//"
					+ "RESPONSECODE");
			String responsecode = e.getTextTrim();
			Element e2 = (Element) document.selectSingleNode("//"
					+ "RESPONSECONTENT");
			if (e2 != null) {
				responsecontent = e2.getTextTrim();
			}

			Element dataSet = (Element) document.selectSingleNode("//"
					+ "CARDINFOS");
			if (dataSet != null) {
				balance = dataSet.attributeValue("BALANCE");
				availBalance = dataSet.attributeValue("AVAILBALANCE");
			}
			hRes.setCharacterEncoding("utf-8");
			output = hRes.getWriter();
			if ("000000".equals(responsecode)) {
				// 输出数据
				output
						.println("卡余额 " + CurrencyTool.fen2Yuan(balance)
								+ "元 可用余额 "
								+ CurrencyTool.fen2Yuan(availBalance) + "元");
			} else {
				output.println(responsecontent);
			}
			output.flush();
			output.close();
		} catch (Exception ex) {
			DpPaymentResponse resp = new DpPaymentResponse();
			String resultXml = ExceptionHandler.toXML(new XmlINFException(resp,
					ex), null);
			Document document2;
			try {
				document2 = DocumentHelper.parseText(resultXml);
				Element e1 = (Element) document2.selectSingleNode("//"
						+ "RESPONSECODE");
				String responsecode = e1.getTextTrim();
				Element e2 = (Element) document2.selectSingleNode("//"
						+ "RESPONSECONTENT");
				String responsecontent2 = "";
				if (e2 != null) {
					responsecontent2 = e2.getTextTrim();
				}
				if (!"000000".equals(responsecode)) {
					hRes.setCharacterEncoding("utf-8");
					PrintWriter out = hRes.getWriter();
					// 输出数据
					out.println(responsecontent2);
				}
				if (output != null) {
					output.close();
				}
			} catch (DocumentException de) {
				if (output != null) {
					output.close();
				}
				hReq.setAttribute("exceptions", de.getMessage());
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

	public String getXmlStr(HttpServletRequest request) throws Exception {
		String agentCode = getAndcheckRequestParam(request, "AGENTCODE");
		String cardNO = getAndcheckRequestParam(request, "CARDNO");
		String requestSeq = getAndcheckRequestParam(request, "REQUESTSEQ");

		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress().toString();// 获得本机IP
		String appFrom = "440000-payWapExecuteServlet-001-" + ip;

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ " <PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"卡余额查询接口\" " + " WEBSVRCODE=\"100006\" "
				+ " APPFROM=\"" + appFrom + "\"" + " 		KEEP=\"" + requestSeq
				+ "\" />" + " 	<PARAMETERS>" + " 		<AGENTCODE>" + agentCode
				+ "</AGENTCODE>" + " <CARDNO>" + cardNO + "</CARDNO>"
				+ " <TEXT1></TEXT1>" + " <TEXT2></TEXT2>" + " <TEXT3></TEXT3>"
				+ " <TEXT4></TEXT4>" + " <TEXT5></TEXT5>";

		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * 获取传递过来的参数值
	 * 
	 * @version: 1.00
	 * @history: 2010-12-27 下午3:56:24 [created]
	 * @author Leyi Tang 唐乐毅
	 * @param request
	 * @param name
	 *            参数名
	 * @return String
	 * @throws Exception
	 * @see
	 */
	private static String getAndcheckRequestParam(HttpServletRequest request,
			String name) throws Exception {
		String value = request.getParameter(name);
		if (Charset.isEmpty(value)) {
			throw new Exception("参数" + name + "不能为空");
		} else {
			return value.trim();
		}
	}
}
