package com.tisson.httpIposSvc;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;

import websvc.DealProcessorService;
import websvc.impl.DealProcessorServiceImpl;

import common.algorithm.MD5;
import common.dao.TInfLoginLogDao;
import common.dao.TSymSysParamDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.invoker.exception.ServiceInvokeException;
import common.platform.provider.server.PackageDataSet;
import common.utils.Charset;
import common.utils.SagUtils;
import common.utils.verify.NETCAPKI;

public class DealProcessIposSvc {

	private static final Log log = LogFactory.getLog(DealProcessIposSvc.class);

	private PackageDataSet cum0014() {

		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "CUM_RAND");
		g002.endRow();

		IServiceCall caller = new ServiceCallImpl();

		PackageDataSet dataSet = null;
		try {
			dataSet = caller.call("BIS", "CUM0014", g002);
		} catch (ServiceInvokeException e) {
			dataSet = e.getDataSet();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return dataSet;
	}

	private String ConvertPassword(String username, String password) {

		String pwd = MD5.MD5Encode(MD5.MD5Encode(username + password
				+ "aienbiei22&*#*(@ieizewbxwerq?"));

		//
		// 获取随机数
		PackageDataSet ds = cum0014();

		String cum = ds.getByID("2174", "217");

		// 组装旧密码
		String[] s = cum.split("_");

		String oldPassword = pwd;

		oldPassword = s[0] + "_" + MD5.MD5Encode(s[0] + oldPassword + s[1]);

		return oldPassword;
	}

	private String ConvertPassword1(String username, String password) {

		String pwd = MD5.MD5Encode(MD5.MD5Encode(username + password
				+ "aienbiei22&*#*(@ieizewbxwerq?"));

		// 获取随机数
		// PackageDataSet ds = cum0014();
		//
		// String cum = ds.getByID("2174", "217");
		//
		// // 组装旧密码
		// String[] s = cum.split("_");
		//
		// String oldPassword = pwd;
		//
		// oldPassword = s[0] + "_"
		// + MD5.MD5Encode(s[0] + oldPassword + s[1]);

		return pwd;
	}

	/**
	 * 商户注册接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02022(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02022XmlStr(request, keep);
		log.info("INF02022:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	
	/**
	 * 支付插件交易接口
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf05002(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String xmlStr = getInf05002XmlStr(request, keep);
		return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
//		String xmlStr = getTradeXmlStr(request, keep);
//		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}
	
	/**
	 * 支付插件交易接口
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf05002XmlStr(HttpServletRequest request, String keep) {
		
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String staffCode = request.getParameter("STAFFCODE");
		String password = request.getParameter("PASSWORD");
		String fundSource = request.getParameter("FUNDSOURCE");
		String verifyCode = request.getParameter("VERIFYCODE");
		password = ConvertPassword(staffCode, password);
		
//		String agentCode = request.getParameter("AGENTCODE");
//		String areaCode = request.getParameter("AREACODE");
//		String actionCode = request.getParameter("ACTIONCODE");
//		String txnAmount = request.getParameter("TXNAMOUNT");
//		String payeeCode = request.getParameter("PAYEECODE");
//		String goodsCode = request.getParameter("GOODSCODE");
//		String goodsName = request.getParameter("GOODSNAME");
//		String orderSeq = request.getParameter("ORDERSEQ");
//		String transSeq = request.getParameter("TRANSSEQ");
//		String tradeTime = request.getParameter("TRADETIME");
//		String mark1 = request.getParameter("MARK1");
//		String mark2 = request.getParameter("MARK2");
		
		//插件调用者 参数明文
//		String requestXml = "<PayPlatRequestParameter>" + "<CTRL-INFO" + " WEBSVRNAME=\"test\"" + " WEBSVRCODE=\"test\"" + " APPFROM=\"1234\"" + " KEEP=\"" + keep + "\"/>"
//						+ "<PARAMETERS>" + "<AGENTCODE>" + agentCode + "</AGENTCODE>" + "<AREACODE>" + areaCode + "</AREACODE>" + "<ACTIONCODE>" + actionCode + "</ACTIONCODE>"
//						+ "<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + "<PAYEECODE>" + payeeCode + "</PAYEECODE>" + "<GOODSCODE>" + goodsCode + "</GOODSCODE>" + "<GOODSNAME>" + goodsName
//						+ "</GOODSNAME>" + "<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + "<TRANSSEQ>" + transSeq + "</TRANSSEQ>" + "<TRADETIME>" + tradeTime + "</TRADETIME>" + "<MARK1>"
//						+ mark1 + "</MARK1>" + "<MARK2>" + mark2 + "</MARK2>" + "</PARAMETERS>" + "</PayPlatRequestParameter>";
//		try {
//			//获取服务器证书
//			//进行签名后得到二进制签名数据,BASE64编码后得到可视的SIGN
//			String signRequestXml = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(requestXml.getBytes("UTF-8")));
//			String cerRequestXml = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
//			requestXml = getCommonXml(merId, channelCode, tmnNum, signRequestXml, cerRequestXml, requestXml);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		String requestXml = "<![CDATA["+getTradeXmlStr(request, keep)+"]]>";
		
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<PASSWORD>"
				+ password
				+ "</PASSWORD>"
				+ " 		<FUNDSOURCE>"
				+ fundSource
				+ "</FUNDSOURCE>"
				+ " 		<VERIFYCODE>"
				+ verifyCode
				+ "</VERIFYCODE>"
				+ " 		<REQUESTXML>"
				+ requestXml
				+ "</REQUESTXML>"
				+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		
		
		try {
			
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);
			
			sign = MD5.MD5Encode(pay.toString() + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	private String getTradeXmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String agentCode = request.getParameter("AGENTCODE");
		String areaCode = request.getParameter("AREACODE");
		String actionCode = request.getParameter("ACTIONCODE");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String payeeCode = request.getParameter("PAYEECODE");
		String goodsCode = request.getParameter("GOODSCODE");
		String goodsName = request.getParameter("GOODSNAME");
		String orderSeq = request.getParameter("ORDERSEQ");
		String transSeq = request.getParameter("TRANSSEQ");
		String tradeTime = request.getParameter("TRADETIME");
		String mark1 = request.getParameter("MARK1");
		String mark2 = request.getParameter("MARK2");

		String pay = "<PayPlatRequestParameter>" + "<CTRL-INFO" + " WEBSVRNAME=\"test\"" + " WEBSVRCODE=\"test\"" + " APPFROM=\"1234\"" + " KEEP=\"" + keep + "\"/>"
				+ "<PARAMETERS>" + "<AGENTCODE>" + agentCode + "</AGENTCODE>" + "<AREACODE>" + areaCode + "</AREACODE>" + "<ACTIONCODE>" + actionCode + "</ACTIONCODE>"
				+ "<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + "<PAYEECODE>" + payeeCode + "</PAYEECODE>" + "<GOODSCODE>" + goodsCode + "</GOODSCODE>" + "<GOODSNAME>" + goodsName
				+ "</GOODSNAME>" + "<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + "<TRANSSEQ>" + transSeq + "</TRANSSEQ>" + "<TRADETIME>" + tradeTime + "</TRADETIME>" + "<MARK1>"
				+ mark1 + "</MARK1>" + "<MARK2>" + mark2 + "</MARK2>" + "</PARAMETERS>" + "</PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	

	/**
	 * 消息详情查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02043(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		HashMap<String, String> reqBody = new HashMap<String, String>();
		String issueId = "ISSUEID";
		String issueType = "ISSUETYPE";
		
		reqBody.put(issueId, request.getParameter(issueId));
		reqBody.put(issueType, request.getParameter(issueType));
		
		String xmlStr = getInfXmlStr(request, keep, reqBody);
		log.info("INF02043:xmlStr====>" + xmlStr);
		
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandJsonLibIPOS(wbSvrCode, xmlStr);
	}
	
	/**
	 * 消息列表查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02042(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		HashMap<String, String> reqBody = new HashMap<String, String>();
		String issueScope = "ISSUESCOPE";
		String issueType = "ISSUETYPE";
		String issueChannel = "ISSUECHANNEL";
		String issueDateEnd = "ISSUEDATEEND";
		String issueDateStart = "ISSUEDATESTART";
		
		reqBody.put(issueType, request.getParameter(issueType));
		reqBody.put(issueChannel, request.getParameter(issueChannel));
		reqBody.put(issueScope, request.getParameter(issueScope));
		reqBody.put(issueDateStart, request.getParameter(issueDateStart));
		reqBody.put(issueDateEnd, request.getParameter(issueDateEnd));
		reqBody.put("start", request.getParameter("start"));
		reqBody.put("page", request.getParameter("page"));
		
		String xmlStr = getInfXmlStr(request, keep, reqBody);
		log.info("INF02042:xmlStr====>" + xmlStr);
		
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandJsonLibIPOS(wbSvrCode, xmlStr);
	}
	
	
	/**
	 * 消息管理已阅和删除接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02041(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		HashMap<String, String> reqBody = new HashMap<String, String>();
		
		reqBody.put("ORDERNO", request.getParameter("ORDERNO"));
		reqBody.put("ISSUEID", request.getParameter("ISSUEID"));
		reqBody.put("ISSUETYPE", request.getParameter("ISSUETYPE"));
		reqBody.put("OPERTYPE", request.getParameter("OPERTYPE"));
		
		
		String xmlStr = getInfXmlStr(request, keep, reqBody);
		log.info("INF02041:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandJsonLibIPOS(wbSvrCode, xmlStr);
	}
	
	
	/**
	 * 消息未阅条数查询
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02040(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		HashMap<String, String> reqBody = new HashMap<String, String>();
		
		String xmlStr = getInfXmlStr(request, keep, reqBody);
		log.info("INF02040:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandJsonLibIPOS(wbSvrCode, xmlStr);
	}
	
	private String getInfXmlStr(HttpServletRequest request, String keep, Map<String, String> reqBody) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");
		
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		
		StringBuilder pay = new StringBuilder();
		pay.append("<PayPlatRequestParameter>").append(
				" 	<CTRL-INFO " ).append( " WEBSVRNAME=\"test\" ").append(
				" WEBSVRCODE=\"test\" ").append(" APPFROM=\"1234\"").append(
				" KEEP=\"" ).append(keep).append( "\" REQUESTTIME=\"").append(
				DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")).append("\"/>" +
				" 	<PARAMETERS>" ).append(" 		<CUSTCODE>" ).append(custCode ).append("</CUSTCODE>").append(
				" 		<STAFFCODE>" ).append( staffCode ).append( "</STAFFCODE>");
		
		for (Object key :  reqBody.keySet().toArray()) {
			String value = reqBody.get(key);
			if (value != null)
				pay.append("<"+key+">").append(value).append("</"+key+">");
		}
				pay.append("</PARAMETERS>" + " </PayPlatRequestParameter>");
		
		try {
			
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);
			
			sign = MD5.MD5Encode(pay.toString() + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay.toString());
	}
	
	
	/**
	 * 短信交易凭证接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02031(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02031XmlStr(request, keep);
		log.info("INF02031:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}
	
	private String getInf02031XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String transSeq = request.getParameter("TRANSSEQ");
		String cardInfo = request.getParameter("CARDINFO");
		String phone	 = request.getParameter("PHONE");
		String cardNo = request.getParameter("CARDNO");
		String password = request.getParameter("PASSWORD");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" +
		" 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " +
				" WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" +
		" 		KEEP=\"" + keep + "\"" + "     REQUESTTIME=\"" +
				DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" +
		" 	<PARAMETERS>" +
				" 		<CUSTCODE>" + custCode + "</CUSTCODE>" +
				" 		<STAFFCODE>" + staffCode + "</STAFFCODE>" +
				" 		<TRANSSEQ>" + transSeq + "</TRANSSEQ>" +
				" 		<CARDINFO>" + cardInfo + "</CARDINFO>" +
				" 		<CARDNO>" + cardNo + "</CARDNO>" +
				" 		<PASSWORD>" + password + "</PASSWORD>" +
				" 		<PHONE>" + phone	+ "</PHONE>" +
		" 		<REMARK1>" + remark1 + "</REMARK1>" +
				" 		<REMARK2>" + remark2 + "</REMARK2>" + 
		" 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();

			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	/**
	 * 商户注册接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02022XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String regType = request.getParameter("REGTYPE");
		String custName = request.getParameter("CUSTNAME");
		String product = request.getParameter("PRODUCTS");
		String enterName = request.getParameter("ENTERNAME");
		String enterAddress = request.getParameter("ENTERADDRESS");
		String areaCode = request.getParameter("AREACODE");
		String agentCode = request.getParameter("AGENTCODE");
		String trade = request.getParameter("TRADE");
		String licence = request.getParameter("BUSINESSLICENCE");
		String applyer = request.getParameter("APPLYER");
		String certNo = request.getParameter("CERTNO");
		String email = request.getParameter("EMAIL");
		String org = request.getParameter("ENTERORGCODE");
		String person = request.getParameter("ENTERPERSON");
		String pcertNo = request.getParameter("ENTERCERNO");
		String reveNbr = request.getParameter("REVENBR");
		String regDate = request.getParameter("REGDATE");
		String verifyCode = request.getParameter("VERIFYCODE");
		String verifyType = request.getParameter("VERIFYTYPE");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<REGTYPE>"
				+ regType
				+ "</REGTYPE>"
				+ " 		<CUSTNAME>"
				+ custName
				+ "</CUSTNAME>"
				+ " 		<PRODUCTS>"
				+ product
				+ "</PRODUCTS>"
				+ " 		<ENTERNAME>"
				+ enterName
				+ "</ENTERNAME>"
				+ " 		<ENTERADDRESS>"
				+ enterAddress
				+ "</ENTERADDRESS>"
				+ " 		<AREACODE>"
				+ areaCode
				+ "</AREACODE>"
				+ " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " 		<TRADE>"
				+ trade
				+ "</TRADE>"
				+ " 		<BUSINESSLICENCE>"
				+ licence
				+ "</BUSINESSLICENCE>"
				+ " 		<APPLYER>"
				+ applyer
				+ "</APPLYER>"
				+ " 		<CERTNO>"
				+ certNo
				+ "</CERTNO>"
				+ " 		<EMAIL>"
				+ email
				+ "</EMAIL>"
				+ " 		<ENTERORGCODE>"
				+ org
				+ "</ENTERORGCODE>"
				+ " 		<ENTERPERSON>"
				+ person
				+ "</ENTERPERSON>"
				+ " 		<ENTERCERNO>"
				+ pcertNo
				+ "</ENTERCERNO>"
				+ " 		<REVENBR>"
				+ reveNbr
				+ "</REVENBR>"
				+ " 		<REGDATE>"
				+ regDate
				+ "</REGDATE>"
				+ " 		<VERIFYCODE>"
				+ verifyCode
				+ "</VERIFYCODE>"
				+ " 		<VERIFYTYPE>"
				+ verifyType
				+ "</VERIFYTYPE>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 代理商列表查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06009(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06009XmlStr(request, keep);
		log.info("INF06009:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 代理商列表查询接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06009XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String areaCode = request.getParameter("AREACODE");

//		String regType = request.getParameter("REGTYPE");

		String pDline = request.getParameter("PDMLINE");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
//				+ " 		<REGTYPE>"
//				+ regType
//				+ "</REGTYPE>"
				+ " 		<AREACODE>"
				+ areaCode
				+ "</AREACODE>"
				+ " 		<PDMLINE>"
				+ pDline
				+ "</PDMLINE>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		// try {
		//
		// String tokenValidTime = TSymSysParamDao.getTokenValidTime();
		//
		// String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
		// tokenValidTime);
		//
		// sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
		// cer = "";
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 快捷交易查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02013(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02013XmlStr(request, keep);
		log.info("INF02013:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 快捷交易查询接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02013XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>"
				+ " 	<PARAMETERS>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<STAFFCODE>" + staffCode + "</STAFFCODE>"
				+ " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>"
				+ remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();

			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 电子售卡接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02018(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02018XmlStr(request, keep);
		log.info("INF02018:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 电子售卡接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02018XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");

		String cardAmount = request.getParameter("CARDAMOUNT");

		String custCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String payPassword = request.getParameter("PAYPASSWORD");
		String payType = request.getParameter("PAYTYPE");

		payPassword = ConvertPassword(staffCode, payPassword);

		String cardtypeCode = request.getParameter("CARDTYPECODE");

		String tradeTime = request.getParameter("TRADETIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<ORDERNO>"
				+ orderNo
				+ "</ORDERNO>"
				+ " 		<CARDAMOUNT>"
				+ cardAmount
				+ "</CARDAMOUNT>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<PAYPASSWORD>"
				+ payPassword
				+ "</PAYPASSWORD>"
				+ " 	<PAYTYPE>"
				+ payType
				+ "</PAYTYPE>"
				+ " 	  <CARDTYPECODE>"
				+ cardtypeCode
				+ "</CARDTYPECODE>"
				+ " 		<TRADETIME>"
				+ tradeTime
				+ "</TRADETIME>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();

			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 快捷交易关闭接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02017(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02017XmlStr(request, keep);
		log.info("INF02017:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 快捷交易关闭接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02017XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String tradeTime = request.getParameter("TRADETIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<ORDERNO>"
				+ orderNo
				+ "</ORDERNO>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<TRADETIME>"
				+ tradeTime
				+ "</TRADETIME>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();

			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 快捷交易设置接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02009(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02009XmlStr(request, keep);
		log.info("INF02009:xmlStr====>" + xmlStr);
		// DealProcessorService dp = (DealProcessorService)
		// SpringContextHelper.getDealProcessorServiceBean();
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 快捷交易设置接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02009XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String payPassword = request.getParameter("PAYPASSWORD");
		String peramount = request.getParameter("PERAMOUNT");
		String allamount = request.getParameter("ALLAMOUNT");
		String tradetime = request.getParameter("TRADETIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		payPassword = ConvertPassword(staffCode, payPassword);

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<ORDERNO>"
				+ orderNo
				+ "</ORDERNO>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<PAYPASSWORD>"
				+ payPassword
				+ "</PAYPASSWORD>"
				+ " 		<PERAMOUNT>"
				+ peramount
				+ "</PERAMOUNT>"
				+ " 		<ALLAMOUNT>"
				+ allamount
				+ "</ALLAMOUNT>"
				+ " 		<TRADETIME>"
				+ tradetime
				+ "</TRADETIME>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();

			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 酬金结转接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02012(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02012XmlStr(request, keep);
		System.out.println("INF02012:xmlStr====>" + xmlStr);
		// DealProcessorService dp = (DealProcessorService)
		// SpringContextHelper.getDealProcessorServiceBean();
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 酬金结转接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02012XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String payPassword = request.getParameter("PAYPASSWORD");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		payPassword = ConvertPassword(staffCode, payPassword);

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<ORDERNO>"
				+ orderNo
				+ "</ORDERNO>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<PAYPASSWORD>"
				+ payPassword
				+ "</PAYPASSWORD>"
				+ " 		<TXNAMOUNT>"
				+ txnAmount
				+ "</TXNAMOUNT>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 溢价查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02044(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02044XmlStr(request, keep);
		System.out.println("INF02044:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 溢价查询接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02044XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String faceAmount = request.getParameter("FACEAMOUNT");
		String phone = request.getParameter("PHONE");
		String actionCode = request.getParameter("ACTIONCODE");
		String prodCode = request.getParameter("PRODCODE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<FACEAMOUNT>"
				+ faceAmount
				+ "</FACEAMOUNT>"
				+ " 		<PHONE>"
				+ phone
				+ "</PHONE>"
				+ " 		<ACTIONCODE>"
				+ actionCode
				+ "</ACTIONCODE>"
				+ " 		<PRODCODE>"
				+ prodCode
				+ "</PRODCODE>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	/**
	 * 理财开户接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02045(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02045XmlStr(request, keep);
		System.out.println("INF02045:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 理财开户接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02045XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String fundCode = request.getParameter("FUNDCODE");
		String accountName = request.getParameter("ACCOUNTNAME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<FUNDCODE>"
				+ fundCode
				+ "</FUNDCODE>"
				+ " 		<ACCOUNTNAME>"
				+ accountName
				+ "</ACCOUNTNAME>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	/**
	 * 理财申购接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02046(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02046XmlStr(request, keep);
		System.out.println("INF02046:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 理财申购接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02046XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String totalAmount = request.getParameter("TOTALAMOUNT");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<TOTALAMOUNT>"
				+ totalAmount
				+ "</TOTALAMOUNT>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	/**
	 * 理财申购支付接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02047(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02047XmlStr(request, keep);
		System.out.println("INF02047:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 理财申购支付接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02047XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");
		
		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String businessOrderNo = request.getParameter("BUSINESSORDERNO");
		String passWord = request.getParameter("PASSWORD");
		passWord = ConvertPassword(staffCode, passWord);
		String totalAmount = request.getParameter("TOTALAMOUNT");
		String fundSourceType = request.getParameter("FUNDSOURCETYPE");
		String tradeTime = request.getParameter("TRADETIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<ORDERSEQ>"
				+ orderSeq
				+ "</ORDERSEQ>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<BUSINESSORDERNO>"
				+ businessOrderNo
				+ "</BUSINESSORDERNO>"
				+ " 		<PASSWORD>"
				+ passWord
				+ "</PASSWORD>"
				+ " 		<TOTALAMOUNT>"
				+ totalAmount
				+ "</TOTALAMOUNT>"
				+ " 		<FUNDSOURCETYPE>"
				+ fundSourceType
				+ "</FUNDSOURCETYPE>"
				+ " 		<TRADETIME>"
				+ tradeTime
				+ "</TRADETIME>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	/**
	 * 理财赎回接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02048(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02048XmlStr(request, keep);
		System.out.println("INF02048:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 理财赎回接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02048XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");
		
		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String passWord = request.getParameter("PASSWORD");
		String ransomType = request.getParameter("RANSOMTYPE");
		String totalAmount = request.getParameter("TOTALAMOUNT");
		String tradeTime = request.getParameter("TRADETIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<ORDERSEQ>"
				+ orderSeq
				+ "</ORDERSEQ>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<PASSWORD>"
				+ passWord
				+ "</PASSWORD>"
				+ " 		<TOTALAMOUNT>"
				+ totalAmount
				+ "</TOTALAMOUNT>"
				+ " 		<RANSOMTYPE>"
				+ ransomType
				+ "</RANSOMTYPE>"
				+ " 		<TRADETIME>"
				+ tradeTime
				+ "</TRADETIME>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	/**
	 * 理财明细列表查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02049(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02049XmlStr(request, keep);
		System.out.println("INF02049:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 理财明细列表查询接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02049XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");
		
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String detailType = request.getParameter("DETAILTYPE");
		String startDate = request.getParameter("STARTDATE");
		String endDate = request.getParameter("ENDDATE");
		String pageNo = request.getParameter("PAGENO");
		String pageSize = request.getParameter("PAGESIZE");
		String sortFlag = request.getParameter("SORTFLAG");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<DETAILTYPE>"
				+ detailType
				+ "</DETAILTYPE>"
				+ " 		<STARTDATE>"
				+ startDate
				+ "</STARTDATE>"
				+ " 		<ENDDATE>"
				+ endDate
				+ "</ENDDATE>"
				+ " 		<PAGENO>"
				+ pageNo
				+ "</PAGENO>"
				+ " 		<PAGESIZE>"
				+ pageSize
				+ "</PAGESIZE>"
				+ " 		<SORTFLAG>"
				+ sortFlag
				+ "</SORTFLAG>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	/**
	 * 理财余额查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02050(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02050XmlStr(request, keep);
		System.out.println("INF02050:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 理财余额查询接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02050XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");
		
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	/**
	 * 理财历史利率查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02051(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02051XmlStr(request, keep);
		System.out.println("INF02051:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 理财历史利率查询接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02051XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");
		
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String fundCode = request.getParameter("FUNDCODE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<FUNDCODE>"
				+ fundCode
				+ "</FUNDCODE>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	/**
	 * Q币充值接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02010(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02010XmlStr(request, keep);
		System.out.println("INF02010:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * Q币充值接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02010XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String payPassword = request.getParameter("PAYPASSWORD");
		String qq = request.getParameter("QQ");
		String rechargeAmount = request.getParameter("RECHARGEAMOUNT");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String tradeTime = request.getParameter("TRADETIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		payPassword = ConvertPassword(staffCode, payPassword);

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<ORDERNO>"
				+ orderNo
				+ "</ORDERNO>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<PAYPASSWORD>"
				+ payPassword
				+ "</PAYPASSWORD>"
				+ " 		<TXNAMOUNT>"
				+ txnAmount
				+ "</TXNAMOUNT>"
				+ " 		<QQ>"
				+ qq
				+ "</QQ>"
				+ " 		<RECHARGEAMOUNT>"
				+ rechargeAmount
				+ "</RECHARGEAMOUNT>"
				+ " 		<TRADETIME>"
				+ tradeTime
				+ "</TRADETIME>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {

			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 密码修改接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02008(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02008XmlStr(request, keep);
		System.out.println("INF02008:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf02008XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String operType = request.getParameter("OPERTYPE");
		String verifyCode = request.getParameter("VERIFYCODE");
		String password = request.getParameter("PASSWORD");
		String newPassword = request.getParameter("NEWPASSWORD");
		String afftPassword = request.getParameter("AFFTPASSWORD");
		String passwordType = request.getParameter("PASSWORDTYPE");

		if ("00".equals(operType)) {
			password = ConvertPassword(staffCode, password);
			newPassword = ConvertPassword1(staffCode, newPassword);
			afftPassword = ConvertPassword1(staffCode, afftPassword);
		}

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<OPERTYPE>"
				+ operType
				+ "</OPERTYPE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<VERIFYCODE>"
				+ verifyCode
				+ "</VERIFYCODE>"
				+ " 		<PASSWORD>"
				+ password
				+ "</PASSWORD>"
				+ " 		<NEWPASSWORD>"
				+ newPassword
				+ "</NEWPASSWORD>"
				+ " 		<AFFTPASSWORD>"
				+ afftPassword
				+ "</AFFTPASSWORD>"
				+ " 		<PASSWORDTYPE>"
				+ passwordType
				+ "</PASSWORDTYPE>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";

		try {
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 获取公共的XML头包
	 * 
	 * @param merId
	 * @param channelCode
	 * @param tmnNum
	 * @param sign
	 * @param cer
	 * @param pay
	 * @return
	 */
	public String getCommonXml(String merId, String channelCode, String tmnNum,
			String sign, String cer, String pay) {
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<Request><VerifyParameter>" + "<MERID>" + merId + "</MERID>"
				+ "<CHANNELCODE>" + channelCode + "</CHANNELCODE>" + "<TMNNUM>"
				+ tmnNum + "</TMNNUM>" + "<SIGN>" + sign + "</SIGN>" + "<CER>"
				+ cer + "</CER>" + "</VerifyParameter>" + pay + "</Request>";
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
	 * @see
	 */
	private static String getRequestParam(HttpServletRequest request,
			String name) {
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String value = request.getParameter(name);
		if (Charset.isEmpty(value)) {
			return "";
		} else {
			return value.trim();
		}
	}

	/**
	 * 游戏充值接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02019(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02019XmlStr(request, keep);
		System.out.println("INF02011:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 游戏充值接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02019XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String payPassword = request.getParameter("PAYPASSWORD");
		String payType = request.getParameter("PAYTYPE");
		String gameCode = request.getParameter("GAMECODE");
		String battleAcct = request.getParameter("BATTLEACCT");
		String gameAcct = request.getParameter("GAMEACCT");
		String rechargeAmount = request.getParameter("RECHARGEAMOUNT");
		String orderaAmount = request.getParameter("ORDERAMOUNT");
		String tradeTime = request.getParameter("TRADETIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		payPassword = ConvertPassword(staffCode, payPassword);
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<ORDERNO>"
				+ orderNo
				+ "</ORDERNO>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<PAYPASSWORD>"
				+ payPassword
				+ "</PAYPASSWORD>"
				+ "   <PAYTYPE>"
				+ payType
				+ "</PAYTYPE>"
				+ " 		<GAMECODE>"
				+ gameCode
				+ "</GAMECODE>"
				+ " 		<BATTLEACCT>"
				+ battleAcct
				+ "</BATTLEACCT>"
				+ " 		<GAMEACCT>"
				+ gameAcct
				+ "</GAMEACCT>"
				+ " 		<RECHARGEAMOUNT>"
				+ rechargeAmount
				+ "</RECHARGEAMOUNT>"

				+ " 		<ORDERAMOUNT>"
				+ orderaAmount
				+ "</ORDERAMOUNT>"

				+ " 		<TRADETIME>"
				+ tradeTime
				+ "</TRADETIME>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 客户信息验证接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02029(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02029XmlStr(request, keep);
		System.out.println("INF02029:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 客户信息验证接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02029XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String regVerifyCode = request.getParameter("REGVERIFYCODE");
		String password = request.getParameter("PASSWORD");
		String contactNo = request.getParameter("CONTACTNO");

		if (!Charset.isEmpty(password, true)) {
			password = ConvertPassword(staffCode, password);
		}

		String certType = request.getParameter("CERTTYPE");
		String cerNo = request.getParameter("CERNO");
		String bankAcct = request.getParameter("BANKACCT");
		String transAccnNme = request.getParameter("TRANSACCNAME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\""
				+ "     REQUESTTIME=\""
				+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<REGVERIFYCODE>"
				+ regVerifyCode
				+ "</REGVERIFYCODE>"
				+ " 		<PASSWORD>"
				+ password
				+ "</PASSWORD>"
				+ " 		<CONTACTNO>"
				+ contactNo
				+ "</CONTACTNO>"
				+ " 		<CERTTYPE>"
				+ certType
				+ "</CERTTYPE>"
				+ " 		<CERNO>"
				+ cerNo
				+ "</CERNO>"
				+ " 		<BANKACCT>"
				+ bankAcct
				+ "</BANKACCT>"
				+ " 		<TRANSACCNAME>"
				+ transAccnNme
				+ "</TRANSACCNAME>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
					tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	/**
	 * 全国固话宽带充值接口
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02035(HttpServletRequest request)
	throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02035XmlStr(request, keep);
		System.out.println("INF02035:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}
	
	private String getInf02035XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String payPassword = request.getParameter("PAYPASSWORD");
		String systemNo=request.getParameter("SYSTEMNO");
		String busiCode = request.getParameter("BUSICODE");
		String accepTareaCode = request.getParameter("ACCEPTAREACODE");
		String acctCode = request.getParameter("ACCTCODE");
		String tradeTime = request.getParameter("TRADETIME");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		payPassword = ConvertPassword(staffCode, payPassword);
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\"" + "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderNo + "</ORDERSEQ>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<PAYPASSWORD>" + payPassword + "</PAYPASSWORD>" + "   <SYSTEMNO>"+ systemNo+ "</SYSTEMNO>"+" 		<BUSICODE>" + busiCode + "</BUSICODE>" + " 		<ACCEPTAREACODE>" + accepTareaCode + "</ACCEPTAREACODE>" + " 		<ACCTCODE>" + acctCode + "</ACCTCODE>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>"

		+ " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>"

		+  " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	/**
	 * 账户绑卡查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02036(HttpServletRequest request)
	throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02036XmlStr(request, keep);
		log.info("INF02036:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}
	
	/**
	 *账户绑卡查询接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02036XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");
		
//		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String queryType = request.getParameter("QUERYTYPE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
		+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
		+ " APPFROM=\"1234\"" + " 		KEEP=\""
		+ keep
		+ "\""
		+ "     REQUESTTIME=\""
		+ DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
		+ "\"/>"
		+ " 	<PARAMETERS>"
		+ " 		<CUSTCODE>"
		+ custCode
		+ "</CUSTCODE>"
		+ " 		<STAFFCODE>"
		+ staffCode
		+ "</STAFFCODE>"
		+ " 		<QUERYTYPE>"
		+ queryType
		+ "</QUERYTYPE>"
		+ " 		<REMARK1>"
		+ remark1
		+ "</REMARK1>"
		+ " 		<REMARK2>"
		+ remark2
		+ "</REMARK2>"
		+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

			sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
			cer = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

}
