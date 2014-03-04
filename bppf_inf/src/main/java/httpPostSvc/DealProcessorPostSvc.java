package httpPostSvc;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import websvc.DealProcessorService;
import websvc.impl.DealProcessorServiceImpl;
import websvc.servant.oldbak.INF_01_010;

import common.algorithm.MD5;
import common.algorithm.RSACipher;
import common.dao.BaseDao;
import common.dao.TCumInfoDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymSysParamDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.invoker.exception.ServiceInvokeException;
import common.platform.provider.server.PackageDataSet;
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.PasswordUtil;
import common.utils.SagUtils;
import common.utils.SpringContextHelper;
import common.utils.verify.NETCAPKI;

public class DealProcessorPostSvc {

	private static final Log log = LogFactory.getLog(DealProcessorPostSvc.class);

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
	private static String getRequestParam(HttpServletRequest request, String name) {
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

	// SPPayment
	public String dispatchCommand(HttpServletRequest request) throws ServiceInvokeException {
		String xmlStr = getXmlStr(request);
		DealProcessorService ds = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return ds.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	public String getXmlStr(HttpServletRequest request) {
		String productNo = getRequestParam(request, "PRODUCTNO");
		String busiType = getRequestParam(request, "BUSINESSTYPE");
		String proModel = getRequestParam(request, "PRODUCTMODEL");
		String txnType = getRequestParam(request, "TXNTYPE");
		String authCode = getRequestParam(request, "AUTHCODE");
		String txnAmount = getRequestParam(request, "TXNAMOUNT");
		String payOrgCode = getRequestParam(request, "PAYORGCODE");
		String slyOrgCode = getRequestParam(request, "SUPPLYORGCODE");
		String accSeq = getRequestParam(request, "ACCEPTSEQNO");
		String oldSeq = getRequestParam(request, "OLDACCEPTSEQNO");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<PRODUCTNO>"
				+ productNo
				+ "</PRODUCTNO>"
				+ " <BUSINESSTYPE>"
				+ busiType
				+ "</BUSINESSTYPE>"
				+ " <PRODUCTMODEL>"
				+ proModel
				+ "</PRODUCTMODEL>"
				+ " <TXNTYPE>"
				+ txnType
				+ "</TXNTYPE>"
				+ " <AUTHCODE>"
				+ authCode
				+ "</AUTHCODE>"
				+ " <TXNAMOUNT>"
				+ txnAmount
				+ "</TXNAMOUNT>"
				+ " <PAYORGCODE>"
				+ payOrgCode
				+ "</PAYORGCODE>"
				+ " <SUPPLYORGCODE>" + slyOrgCode + "</SUPPLYORGCODE>" + " <ACCEPTSEQNO>" + accSeq + "</ACCEPTSEQNO>";

		if (!Charset.isEmpty(oldSeq)) {
			xmlStr += " <OLDACCEPTSEQNO>" + oldSeq + "</OLDACCEPTSEQNO>";
		}

		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * 订单生成接口组包
	 */
	public String getGenerateOrderXmlStr(HttpServletRequest request) {

		String productNo = request.getParameter("PRODUCTNO");
		String partnerId = request.getParameter("PARTNERID");
		String partnerOrderId = request.getParameter("PARTNERORDERID");
		String txtAmount = request.getParameter("TXNAMOUNT");
		String sig = request.getParameter("SIG");

		String businessType = request.getParameter("BUSINESSTYPE");
		String goodsId = request.getParameter("GOODSID");
		String goodsName = request.getParameter("GOODSNAME");
		String goodsCount = request.getParameter("GOODSCOUNT");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<PRODUCTNO>"
				+ productNo
				+ "</PRODUCTNO>"
				+ " <PARTNERID>"
				+ partnerId
				+ "</PARTNERID>"
				+ " <PARTNERORDERID>"
				+ partnerOrderId
				+ "</PARTNERORDERID>"
				+ " <TXNAMOUNT>"
				+ txtAmount
				+ "</TXNAMOUNT>"
				+ " <SIG>"
				+ sig
				+ "</SIG>"
				+ " <BUSINESSTYPE>"
				+ businessType
				+ "</BUSINESSTYPE>"
				+ " <GOODSID>"
				+ goodsId
				+ "</GOODSID>"
				+ " <GOODSNAME>" + goodsName + "</GOODSNAME>" + " <GOODSCOUNT>" + goodsCount + "</GOODSCOUNT>";

		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * 退款及交易撤销接口组包
	 */
	public String getRefundXmlStr(HttpServletRequest request) {

		String partnerId = request.getParameter("PARTNERID");
		String supplyOrgCode1 = request.getParameter("SUPPLYORGCODE1");
		String supplyOrgCode2 = request.getParameter("SUPPLYORGCODE2");
		String supplyOrgCode3 = request.getParameter("SUPPLYORGCODE3");
		String productNo = request.getParameter("PRODUCTNO");
		String partnerOrderId = request.getParameter("PARTNERORDERID");
		String oldPartnerOrderId = request.getParameter("OLDPARTNERORDERID");
		String sig = request.getParameter("SIG");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<PARTNERID>"
				+ partnerId
				+ "</PARTNERID>"
				+ " 		<PRODUCTNO>"
				+ productNo
				+ "</PRODUCTNO>"
				+ " <OLDPARTNERORDERID>"
				+ oldPartnerOrderId
				+ "</OLDPARTNERORDERID>"
				+ " <PARTNERORDERID>"
				+ partnerOrderId
				+ "</PARTNERORDERID>"
				+ " <SUPPLYORGCODE1>"
				+ supplyOrgCode1
				+ "</SUPPLYORGCODE1>"
				+ " <SUPPLYORGCODE2>"
				+ supplyOrgCode2
				+ "</SUPPLYORGCODE2>" + " <SUPPLYORGCODE3>" + supplyOrgCode3 + "</SUPPLYORGCODE3>" + " <SIG>" + sig + "</SIG>";

		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	// *********************************商户平台********************************************//

	// 加密(测试)
	public String encrypt(HttpServletRequest request) throws ServiceInvokeException {

		String pinkey = request.getParameter("pinkey");
		String cardOrAccountNo = Charset.lpad(request.getParameter("cardOrAccountNo"), 16, "0");
		;
		String orderNo = request.getParameter("orderNo");
		String pubKey = request.getParameter("pubKey");
		String keyMod = request.getParameter("keyMod");

		log.info("---------------pinkey------------:" + pinkey);
		log.info("---------------cardOrAccountNo------------:" + cardOrAccountNo);
		log.info("---------------orderNo------------:" + orderNo);
		log.info("---------------pubKey------------:" + pubKey);
		log.info("---------------keyMod------------:" + keyMod);
		String value = new com.huateng.encrypt.PinkeyEncrypt().encrypt(pinkey, cardOrAccountNo, orderNo, pubKey, keyMod);
		log.info("---------------value------------:" + value);
		return value;
	}

	/**
	 * SVC_100001 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String cardInventory(HttpServletRequest request) throws ServiceInvokeException {

		String xmlStr = getCardInventoryXmlStr(request);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getCardInventoryXmlStr(HttpServletRequest request) {

		String agentCode = request.getParameter("AGENTCODE");
		String cardType = request.getParameter("CARDTYPE");
		String subCardType = request.getParameter("SUBCARDTYPE");
		String cardPrefix = request.getParameter("CARDPREFIX");
		String cardAmt = request.getParameter("CARDAMT");
		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " <CARDTYPE>"
				+ cardType
				+ "</CARDTYPE>"
				+ " <SUBCARDTYPE>"
				+ subCardType
				+ "</SUBCARDTYPE>"
				+ " <CARDPREFIX>"
				+ cardPrefix
				+ "</CARDPREFIX>"
				+ " <CARDAMT>"
				+ cardAmt
				+ "</CARDAMT>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>"
				+ text2
				+ "</TEXT2>"
				+ " <TEXT3>"
				+ text3
				+ "</TEXT3>"
				+ " <TEXT4>" + text4 + "</TEXT4>" + " <TEXT5>" + text5 + "</TEXT5>";

		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_100002 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String cardOrder(HttpServletRequest request) throws ServiceInvokeException {

		String xmlStr = getCardOrderXmlStr(request);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getCardOrderXmlStr(HttpServletRequest request) {

		String agentCode = request.getParameter("AGENTCODE");
		String cardType = request.getParameter("CARDTYPE");
		String subCardType = request.getParameter("SUBCARDTYPE");
		String cardPrefix = request.getParameter("CARDPREFIX");
		String cardAmt = request.getParameter("CARDAMT");
		String orderNum = request.getParameter("ORDERNUM");
		String tradeSeq = request.getParameter("TRADESEQ");
		String tradeTime = request.getParameter("TRADETIME");

		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " <CARDTYPE>"
				+ cardType
				+ "</CARDTYPE>"
				+ " <SUBCARDTYPE>"
				+ subCardType
				+ "</SUBCARDTYPE>"
				+ " <CARDPREFIX>"
				+ cardPrefix
				+ "</CARDPREFIX>"
				+ " <CARDAMT>"
				+ cardAmt
				+ "</CARDAMT>"
				+ " <ORDERNUM>"
				+ orderNum
				+ "</ORDERNUM>"
				+ " <TRADESEQ>"
				+ tradeSeq
				+ "</TRADESEQ>"
				+ " <TRADETIME>"
				+ tradeTime
				+ "</TRADETIME>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>"
				+ text2
				+ "</TEXT2>"
				+ " <TEXT3>"
				+ text3
				+ "</TEXT3>"
				+ " <TEXT4>"
				+ text4
				+ "</TEXT4>" + " <TEXT5>" + text5 + "</TEXT5>";

		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_100003 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String cardOrderCancel(HttpServletRequest request) throws ServiceInvokeException {

		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getCardOrderCancelXmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getCardOrderCancelXmlStr(HttpServletRequest request, String keep) {

		String agentCode = request.getParameter("AGENTCODE");
		String tradeSeq = request.getParameter("TRADESEQ");
		String tradeTime = request.getParameter("TRADETIME");
		String oldTradeTime = request.getParameter("OLDTRADETIME");
		String oldTradeSeq = request.getParameter("OLDTRADESEQ");

		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\" />"
				+ " 	<PARAMETERS>"
				+ " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " <TRADESEQ>"
				+ tradeSeq
				+ "</TRADESEQ>"
				+ " <TRADETIME>"
				+ tradeTime
				+ "</TRADETIME>"
				+ " <OLDTRADETIME>"
				+ oldTradeTime
				+ "</OLDTRADETIME>"
				+ " <OLDTRADESEQ>"
				+ oldTradeSeq
				+ "</OLDTRADESEQ>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>" + text2 + "</TEXT2>" + " <TEXT3>" + text3 + "</TEXT3>" + " <TEXT4>" + text4 + "</TEXT4>" + " <TEXT5>" + text5 + "</TEXT5>";

		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_100004 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String cardOrderConfirm(HttpServletRequest request) throws ServiceInvokeException {

		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getCardOrderConfirmXmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getCardOrderConfirmXmlStr(HttpServletRequest request, String keep) {

		String agentCode = request.getParameter("AGENTCODE");
		String areaCode = request.getParameter("AREACODE");
		String txnChannel = request.getParameter("TXNCHANNEL");
		String tradeSeq = request.getParameter("TRADESEQ");
		String tradeTime = request.getParameter("TRADETIME");
		String oldTradeTime = request.getParameter("OLDTRADETIME");
		String oldTradeSeq = request.getParameter("OLDTRADESEQ");

		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");

		// user info
		String userType = request.getParameter("USERTYPE");
		String orgName = request.getParameter("ORGNAME");
		String userName = request.getParameter("USERNAME");
		String idType = request.getParameter("ID_TYPE");
		String idNo = request.getParameter("ID_NO");
		String address = request.getParameter("ADDRESS");
		String phone = request.getParameter("PHONE");
		String userText1 = request.getParameter("USERTEXT1");
		String userText2 = request.getParameter("USERTEXT2");
		String userText3 = request.getParameter("USERTEXT3");
		String userText4 = request.getParameter("USERTEXT4");
		String userText5 = request.getParameter("USERTEXT5");

		// account info
		String acctName = request.getParameter("ACCTNAME");
		String acctNo = request.getParameter("ACCTNO");
		String money = request.getParameter("MONEY");
		String acctTradeTime = request.getParameter("ACCTTRADETIME");
		String acctText1 = request.getParameter("ACCTTEXT1");
		String acctText2 = request.getParameter("ACCTTEXT2");
		String acctText3 = request.getParameter("ACCTTEXT3");
		String acctText4 = request.getParameter("ACCTTEXT4");
		String acctText5 = request.getParameter("ACCTTEXT5");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\" />"
				+ " 	<PARAMETERS>"
				+ " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " 		<AREACODE>"
				+ areaCode
				+ "</AREACODE>"
				+ " <TXNCHANNEL>"
				+ txnChannel
				+ "</TXNCHANNEL>"
				+ " <USERINFO "
				+ "USERTYPE=\""
				+ userType
				+ "\" ORGNAME=\""
				+ orgName
				+ "\" USERNAME=\""
				+ userName
				+ "\" ID_TYPE=\""
				+ idType
				+ "\" ID_NO=\""
				+ idNo
				+ "\" ADDRESS=\""
				+ address
				+ "\" PHONE=\""
				+ phone
				+ "\" TEXT1=\""
				+ userText1
				+ "\" TEXT2=\""
				+ userText2
				+ "\" TEXT3=\""
				+ userText3
				+ "\" TEXT4=\""
				+ userText4
				+ "\" TEXT5=\""
				+ userText5
				+ "\"  />"
				+ " <ACCTINFO ACCTNAME=\""
				+ acctName
				+ "\" ACCTNO=\""
				+ acctNo
				+ "\" MONEY=\""
				+ money
				+ "\" TRADETIME=\""
				+ acctTradeTime
				+ "\" TEXT1=\""
				+ acctText1
				+ "\" TEXT2=\""
				+ acctText2
				+ "\" TEXT3=\""
				+ acctText3
				+ "\" TEXT4=\""
				+ acctText4
				+ "\" TEXT5=\""
				+ acctText5
				+ "\"  /> "
				+ " <TRADESEQ>"
				+ tradeSeq
				+ "</TRADESEQ>"
				+ " <TRADETIME>"
				+ tradeTime
				+ "</TRADETIME>"
				+ " <OLDTRADETIME>"
				+ oldTradeTime
				+ "</OLDTRADETIME>"
				+ " <OLDTRADESEQ>"
				+ oldTradeSeq
				+ "</OLDTRADESEQ>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>"
				+ text2
				+ "</TEXT2>"
				+ " <TEXT3>"
				+ text3
				+ "</TEXT3>"
				+ " <TEXT4>" + text4 + "</TEXT4>" + " <TEXT5>" + text5 + "</TEXT5>";

		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_100005 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String queryCardWithPwd(HttpServletRequest request) throws ServiceInvokeException {

		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getQueryCardWithXmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getQueryCardWithXmlStr(HttpServletRequest request, String keep) {

		String agentCode = request.getParameter("AGENTCODE");
		String cardType = request.getParameter("CARDTYPE");
		String cardNo = request.getParameter("CARDNO");
		String cardPwd = request.getParameter("CARDPWD");
		String encryptPwd = "";
		try {
			encryptPwd = RSACipher.encryptByPublicKey(cardPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\" />"
				+ " 	<PARAMETERS>"
				+ " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " <CARDTYPE>"
				+ cardType
				+ "</CARDTYPE>"
				+ " <CARDNO>"
				+ cardNo
				+ "</CARDNO>"
				+ " <CARDPWD>"
				+ encryptPwd
				+ "</CARDPWD>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>"
				+ text2
				+ "</TEXT2>"
				+ " <TEXT3>"
				+ text3
				+ "</TEXT3>"
				+ " <TEXT4>"
				+ text4 + "</TEXT4>" + " <TEXT5>" + text5 + "</TEXT5>";
		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_100006 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String cardBalance(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getCardBalanceStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getCardBalanceStr(HttpServletRequest request, String keep) {

		String agentCode = request.getParameter("AGENTCODE");
		String cardNo = request.getParameter("CARDNO");
		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\" />"
				+ " 	<PARAMETERS>"
				+ " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " <CARDNO>"
				+ cardNo
				+ "</CARDNO>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>" + text2 + "</TEXT2>" + " <TEXT3>" + text3 + "</TEXT3>" + " <TEXT4>" + text4 + "</TEXT4>" + " <TEXT5>" + text5 + "</TEXT5>";
		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_100007 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String cardValid(HttpServletRequest request) throws ServiceInvokeException {

		String xmlStr = getCardValidXmlStr(request);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		// String localAddr = request.getRemoteAddr();

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getCardValidXmlStr(HttpServletRequest request) {

		String agentCode = request.getParameter("AGENTCODE");
		String cardType = request.getParameter("CARDTYPE");
		String cardNo = request.getParameter("CARDNO");
		String cardPwd = request.getParameter("CARDPWD");
		String encryptPwd = "";
		try {
			encryptPwd = RSACipher.encryptByPublicKey(cardPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " <CARDTYPE>"
				+ cardType
				+ "</CARDTYPE>"
				+ " <CARDNO>"
				+ cardNo
				+ "</CARDNO>"
				+ " <CARDPWD>"
				+ encryptPwd
				+ "</CARDPWD>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>"
				+ text2
				+ "</TEXT2>"
				+ " <TEXT3>"
				+ text3
				+ "</TEXT3>"
				+ " <TEXT4>"
				+ text4
				+ "</TEXT4>"
				+ " <TEXT5>"
				+ text5
				+ "</TEXT5>";
		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_100008 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String queryCardWithoutPwd(HttpServletRequest request) throws ServiceInvokeException {

		String xmlStr = getQueryCardWithoutPwdStr(request);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		// String localAddr = request.getRemoteAddr();

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getQueryCardWithoutPwdStr(HttpServletRequest request) {

		String agentCode = request.getParameter("AGENTCODE");
		String cardNo = request.getParameter("CARDNO");
		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");
		String cardType = request.getParameter("CARDTYPE");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " <CARDNO>"
				+ cardNo
				+ "</CARDNO>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>"
				+ text2
				+ "</TEXT2>"
				+ " <TEXT3>"
				+ text3
				+ "</TEXT3>" + " <TEXT4>" + text4 + "</TEXT4>" + " <TEXT5>" + text5 + "</TEXT5>" + " <CARDTYPE>" + cardType + "</CARDTYPE>";
		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_100009 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String lossReporting(HttpServletRequest request) throws ServiceInvokeException {

		String xmlStr = getLossReportingStr(request);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		// String localAddr = request.getRemoteAddr();

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getLossReportingStr(HttpServletRequest request) {
		String cardType = request.getParameter("CARDTYPE");
		String cardNo = request.getParameter("CARDNO");
		String cardPwd = request.getParameter("CARDPWD");
		String encryptPwd = "";
		try {
			encryptPwd = RSACipher.encryptByPublicKey(cardPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String repLossWay = request.getParameter("REPLOSSWAY");
		String repLossType = request.getParameter("REPLOSSTYPE");

		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<CARDNO>"
				+ cardNo
				+ "</CARDNO>"
				+ " <CARDPWD>"
				+ encryptPwd
				+ "</CARDPWD>"
				+ " <CARDTYPE>"
				+ cardType
				+ "</CARDTYPE>"
				+ " <REPLOSSWAY>"
				+ repLossWay
				+ "</REPLOSSWAY>"
				+ " <REPLOSSTYPE>"
				+ repLossType
				+ "</REPLOSSTYPE>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>"
				+ text2
				+ "</TEXT2>"
				+ " <TEXT3>"
				+ text3
				+ "</TEXT3>"
				+ " <TEXT4>" + text4 + "</TEXT4>" + " <TEXT5>" + text5 + "</TEXT5>";
		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_100010 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String modifyPwd(HttpServletRequest request) throws ServiceInvokeException {

		String xmlStr = getModifyPwdStr(request);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		// String localAddr = request.getRemoteAddr();

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getModifyPwdStr(HttpServletRequest request) {

		String agentCode = request.getParameter("AGENTCODE");
		String cardType = request.getParameter("CARDTYPE");
		String cardNo = request.getParameter("CARDNO");
		String oldPasswd = request.getParameter("OLDPASSWD");
		String newPasswd = request.getParameter("NEWPASSWD");
		String encryptOldPwd = "";
		String encryptNewPwd = "";
		try {
			encryptOldPwd = RSACipher.encryptByPublicKey(oldPasswd);
			encryptNewPwd = RSACipher.encryptByPublicKey(newPasswd);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " <CARDNO>"
				+ cardNo
				+ "</CARDNO>"
				+ " <CARDTYPE>"
				+ cardType
				+ "</CARDTYPE>"
				+ " <OLDPASSWD>"
				+ encryptOldPwd
				+ "</OLDPASSWD>"
				+ " <NEWPASSWD>"
				+ encryptNewPwd
				+ "</NEWPASSWD>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>"
				+ text2
				+ "</TEXT2>"
				+ " <TEXT3>"
				+ text3
				+ "</TEXT3>"
				+ " <TEXT4>" + text4 + "</TEXT4>" + " <TEXT5>" + text5 + "</TEXT5>";
		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_100010 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String buyPhysicalCard(HttpServletRequest request) throws ServiceInvokeException {

		String xmlStr = getBuyPhysicalCardStr(request);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		// String localAddr = request.getRemoteAddr();

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getBuyPhysicalCardStr(HttpServletRequest request) {

		String agentCode = request.getParameter("AGENTCODE");
		String tradeSeq = request.getParameter("TRADESEQ");
		String tradeTime = request.getParameter("TRADETIME");
		String company = request.getParameter("COMPANY");
		String contract = request.getParameter("CONTACT");
		String tel = request.getParameter("TEL");
		String add = request.getParameter("ADD");
		String email = request.getParameter("EMAIL");
		String cardAmt = request.getParameter("CARDAMT");
		String orderNum = request.getParameter("ORDERNUM");
		String invoice = request.getParameter("INVOICE");
		String payType = request.getParameter("PAYTYPE");
		String isFirstFlag = request.getParameter("ISFIRSTFLAG");

		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " <TRADESEQ>"
				+ tradeSeq
				+ "</TRADESEQ>"
				+ " <TRADETIME>"
				+ tradeTime
				+ "</TRADETIME>"
				+ " <COMPANY>"
				+ company
				+ "</COMPANY>"
				+ " <CONTACT>"
				+ contract
				+ "</CONTACT>"
				+ " <TEL>"
				+ tel
				+ "</TEL>"
				+ " <ADD>"
				+ add
				+ "</ADD>"
				+ " <EMAIL>"
				+ email
				+ "</EMAIL>"
				+ " <CARDAMT>"
				+ cardAmt
				+ "</CARDAMT>"
				+ " <ORDERNUM>"
				+ orderNum
				+ "</ORDERNUM>"
				+ " <INVOICE>"
				+ invoice
				+ "</INVOICE>"
				+ " <PAYTYPE>"
				+ payType
				+ "</PAYTYPE>"
				+ " <ISFIRSTFLAG>"
				+ isFirstFlag
				+ "</ISFIRSTFLAG>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>"
				+ text2
				+ "</TEXT2>"
				+ " <TEXT3>"
				+ text3
				+ "</TEXT3>"
				+ " <TEXT4>"
				+ text4
				+ "</TEXT4>" + " <TEXT5>" + text5 + "</TEXT5>";
		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_200001 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String payment(HttpServletRequest request) throws ServiceInvokeException {

		String xmlStr = getPaymentStr(request);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		// String localAddr = request.getRemoteAddr();

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getPaymentStr(HttpServletRequest request) {

		String agentCode = request.getParameter("AGENTCODE");
		String areaCode = request.getParameter("AREACODE");
		String txnChannel = request.getParameter("TXNCHANNEL");
		String payType = request.getParameter("PAYTYPE");
		String cardType = request.getParameter("CARDTYPE");
		String cardNo = request.getParameter("CARDNO");
		String cardPwd = request.getParameter("CARDPWD");
		String encryptPwd = "";
		try {
			encryptPwd = RSACipher.encryptByPublicKey(cardPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String txnAmount = request.getParameter("TXNAMOUNT");
		String goodsName = request.getParameter("GOODSNAME");
		String goodsCode = request.getParameter("GOODSCODE");
		String tradeSeq = request.getParameter("TRADESEQ");
		String tradeTime = request.getParameter("TRADETIME");

		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " 		<AREACODE>"
				+ areaCode
				+ "</AREACODE>"
				+ " <TXNCHANNEL>"
				+ txnChannel
				+ "</TXNCHANNEL>"
				+ " <PAYTYPE>"
				+ payType
				+ "</PAYTYPE>"
				+ " <CARDTYPE>"
				+ cardType
				+ "</CARDTYPE>"
				+ " <CARDNO>"
				+ cardNo
				+ "</CARDNO>"
				+ " <CARDPWD>"
				+ encryptPwd
				+ "</CARDPWD>"
				+ " <TXNAMOUNT>"
				+ txnAmount
				+ "</TXNAMOUNT>"
				+ " <GOODSNAME>"
				+ goodsName
				+ "</GOODSNAME>"
				+ " <GOODSCODE>"
				+ goodsCode
				+ "</GOODSCODE>"
				+ " <TRADESEQ>"
				+ tradeSeq
				+ "</TRADESEQ>"
				+ " <TRADETIME>"
				+ tradeTime
				+ "</TRADETIME>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>"
				+ text2
				+ "</TEXT2>"
				+ " <TEXT3>"
				+ text3
				+ "</TEXT3>"
				+ " <TEXT4>"
				+ text4 + "</TEXT4>" + " <TEXT5>" + text5 + "</TEXT5>";
		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_200002 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String paymentReversal(HttpServletRequest request) throws ServiceInvokeException {

		String xmlStr = getPaymentReversalXmlStr(request);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		// String localAddr = request.getRemoteAddr();

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getPaymentReversalXmlStr(HttpServletRequest request) {

		String agentCode = request.getParameter("AGENTCODE");
		String cardNo = request.getParameter("CARDNO");
		String txnChannel = request.getParameter("TXNCHANNEL");
		String tradeSeq = request.getParameter("TRADESEQ");
		String tradeTime = request.getParameter("TRADETIME");
		String oldTradeSeq = request.getParameter("OLDTRADESEQ");

		String text1 = request.getParameter("TEXT1");
		String text2 = request.getParameter("TEXT2");
		String text3 = request.getParameter("TEXT3");
		String text4 = request.getParameter("TEXT4");
		String text5 = request.getParameter("TEXT5");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<AGENTCODE>"
				+ agentCode
				+ "</AGENTCODE>"
				+ " <CARDNO>"
				+ cardNo
				+ "</CARDNO>"
				+ " <TXNCHANNEL>"
				+ txnChannel
				+ "</TXNCHANNEL>"
				+ " <OLDTRADESEQ>"
				+ oldTradeSeq
				+ "</OLDTRADESEQ>"
				+ " <TRADESEQ>"
				+ tradeSeq
				+ "</TRADESEQ>"
				+ " <TRADETIME>"
				+ tradeTime
				+ "</TRADETIME>"
				+ " <TEXT1>"
				+ text1
				+ "</TEXT1>"
				+ " <TEXT2>"
				+ text2
				+ "</TEXT2>" + " <TEXT3>" + text3 + "</TEXT3>" + " <TEXT4>" + text4 + "</TEXT4>" + " <TEXT5>" + text5 + "</TEXT5>";

		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * SVC_200003 test
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String paymentInfo(HttpServletRequest request) throws ServiceInvokeException {

		String xmlStr = getPaymentInfoXmlStr(request);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		// String localAddr = request.getRemoteAddr();

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getPaymentInfoXmlStr(HttpServletRequest request) {

		String cardNo = request.getParameter("CARDNO");
		String txnType = request.getParameter("TXNTYPE");
		String txnChannel = request.getParameter("TXNCHANNEL");
		String startTime = request.getParameter("STARTTIME");
		String endTime = request.getParameter("ENDTIME");

		String startRecord = request.getParameter("STARTRECORD");
		String maxRecord = request.getParameter("MAXRECORD");

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\"123456789\" />" + " 	<PARAMETERS>" + " 		<CARDNO>" + cardNo + "</CARDNO>" + " <TXNTYPE>" + txnType + "</TXNTYPE>"
				+ " <TXNCHANNEL>" + txnChannel + "</TXNCHANNEL>" + " <STARTTIME>" + startTime + "</STARTTIME>" + " <ENDTIME>" + endTime + "</ENDTIME>" + " <STARTRECORD>"
				+ startRecord + "</STARTRECORD>" + " <MAXRECORD>" + maxRecord + "</MAXRECORD>";

		xmlStr += " 	</PARAMETERS>" + " </PayPlatRequestParameter> ";

		return xmlStr;
	}

	/**
	 * 交易查询接口
	 * 
	 * @param request
	 * @history: 2012-02-03 下午5:49:42 [created]
	 * @author yangcheng
	 */
	public String transactionInfo(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = get01002XmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String get01002XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String agentCode = request.getParameter("AGENTCODE");
		String searchTime = request.getParameter("SEARCHTIME");
		String startDate = request.getParameter("STARTDATE");
		String endDate = request.getParameter("ENDDATE");
		String orderSeq = request.getParameter("ORDERSEQ");
		String transSeq = request.getParameter("TRANSSEQ");
		String orderType = request.getParameter("ORDERTYPE");
		String orderStat = request.getParameter("ORDERSTAT");
		String areaCode = request.getParameter("AREACODE");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<AGENTCODE>" + agentCode + "</AGENTCODE>" + " 		<SEARCHTIME>" + searchTime + "</SEARCHTIME>" + " 		<STARTDATE>" + startDate
				+ "</STARTDATE>" + " 		<ENDDATE>" + endDate + "</ENDDATE>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<TRANSSEQ>" + transSeq + "</TRANSSEQ>"
				+ " 		<ORDERTYPE>" + orderType + "</ORDERTYPE>" + " 		<ORDERSTAT>" + orderStat + "</ORDERSTAT>" + " 		<AREACODE>" + areaCode + "</AREACODE>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	// 交易与操作记录查询
	public String inf02016(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getInf02016XmlStr(request, keep);
		System.out.println("xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);

	}

	// 交易与操作记录查询
	private String getInf02016XmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");

		String acctType = request.getParameter("ACCTTYPE");

		String income = request.getParameter("INCOME");

		String startdate = request.getParameter("STARTDATE");
		String enddate = request.getParameter("ENDDATE");

		String startNum = request.getParameter("STARTNUM");
		String endNum = request.getParameter("ENDNUM");
		String bankMode = request.getParameter("BANKMODE");
		String transCode = request.getParameter("TRANSCODE");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<ACCTTYPE>" + acctType + "</ACCTTYPE>"
				+ " 		<STARTDATE>" + startdate + "</STARTDATE>" + " 		<ENDDATE>" + enddate + "</ENDDATE>" + " 		<STARTNUM>" + startNum + "</STARTNUM>" + " 		<ENDNUM>" + endNum
				+ "</ENDNUM>" + " 		<TRANSCODE>" + transCode + "</TRANSCODE>" + " 		<INCOME>" + income + "</INCOME>" + " 		<BANKMODE>" + bankMode + "</BANKMODE>"
				+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);
				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 交易回调接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf00001(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf00001XmlStr(request, keep);
		log.info("INF00001:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);

	}
	
	/**
	 * 交易回调接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf00001XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String tranSeq = request.getParameter("TRANSEQ");
		String bussType = request.getParameter("BUSSTYPE");
		String custCode = request.getParameter("CUSTCODE");

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
				+ " 		<TRANSEQ>"
				+ tranSeq
				+ "</TRANSEQ>"
				+ " 		<BUSSTYPE>"
				+ bussType
				+ "</BUSSTYPE>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
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
	 * 3G流量卡充值接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf12021(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf12021XmlStr(request, keep);
		log.info("INF12021:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);

	}

	/**
	 * 3G流量卡充值接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf12021XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");

		String custCode = request.getParameter("CUSTCODE");

		// String tmnNumNo = request.getParameter("TMNNUMNO");
		// String payType = request.getParameter("PAYTYPE");
		//
		// String psamCardNo = request.getParameter("PSAMCARDNO");
		//
		// String eCardNo = request.getParameter("ECARDNO");
		//
		// String passFlag = request.getParameter("PASSFLAG");
		//
		// String staffCode = request.getParameter("STAFFCODE");
		//
		// String payPassword = request.getParameter("PAYPASSWORD");
		//
		// if (!ChannelCode.IPOS_CHANELCODE.equals(channelCode)) {
		//
		// payPassword = PasswordUtil.ConvertPassword(staffCode, payPassword);
		// }

		String rechargeType = request.getParameter("RECHARGETYPE");

		String verify = request.getParameter("VERIFY");

		String phone = request.getParameter("PHONE");

		String rechargeFlow = request.getParameter("RECHARGEFLOW");

		String systemNO = request.getParameter("SYSTEMNO");

		String txnAmount = request.getParameter("TXNAMOUNT");

		String acceptAreaCode = request.getParameter("ACCEPTAREACODE");

		String tradeTime = request.getParameter("TRADETIME");

		String remark1 = request.getParameter("REMARK1");

		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\""
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
				// + " 		<TMNNUMNO>"
				// + tmnNumNo
				// + "</TMNNUMNO>"
				// + " 		<PAYTYPE>"
				// + payType
				// + "</PAYTYPE>"
				// + " 		<PSAMCARDNO>"
				// + psamCardNo
				// + "</PSAMCARDNO>"
				// + " 		<ECARDNO>"
				// + eCardNo
				// + "</ECARDNO>"
				// + " 		<PASSFLAG>"
				// + passFlag
				// + "</PASSFLAG>"
				// + " 		<STAFFCODE>"
				// + staffCode
				// + "</STAFFCODE>"
				// + " 		<PAYPASSWORD>"
				// + payPassword
				// + "</PAYPASSWORD>"
				+ " 		<RECHARGETYPE>" + rechargeType + "</RECHARGETYPE>" + " 		<VERIFY>" + verify + "</VERIFY>" + " 		<PHONE>" + phone + "</PHONE>" + " 		<RECHARGEFLOW>"
				+ rechargeFlow + "</RECHARGEFLOW>" + " 		<SYSTEMNO>" + systemNO + "</SYSTEMNO>" + " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " 		<ACCEPTAREACODE>"
				+ acceptAreaCode + "</ACCEPTAREACODE>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2
				+ "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {

			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 游戏充值接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf12019(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf12019XmlStr(request, keep);
		System.out.println("INF12019:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 游戏充值接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf12019XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");
		// String staffCode = request.getParameter("STAFFCODE");
		// String payPassword = request.getParameter("PAYPASSWORD");
		// String payType = request.getParameter("PAYTYPE");
		String gameCode = request.getParameter("GAMECODE");
		String battleAcct = request.getParameter("BATTLEACCT");
		String gameAcct = request.getParameter("GAMEACCT");
		String rechargeAmount = request.getParameter("RECHARGEAMOUNT");
		String orderaAmount = request.getParameter("ORDERAMOUNT");
		String tradeTime = request.getParameter("TRADETIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		// payPassword = ConvertPassword(staffCode, payPassword);
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\""
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
				// + " 		<STAFFCODE>"
				// + staffCode
				// + "</STAFFCODE>"
				// + " 		<PAYPASSWORD>"
				// + payPassword
				// + "</PAYPASSWORD>"
				// + "   <PAYTYPE>"
				// + payType
				// + "</PAYTYPE>"
				+ " 		<GAMECODE>" + gameCode + "</GAMECODE>" + " 		<BATTLEACCT>" + battleAcct + "</BATTLEACCT>" + " 		<GAMEACCT>" + gameAcct + "</GAMEACCT>"
				+ " 		<RECHARGEAMOUNT>" + rechargeAmount + "</RECHARGEAMOUNT>"

				+ " 		<ORDERAMOUNT>" + orderaAmount + "</ORDERAMOUNT>"

				+ " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
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
	public String inf12018(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf12018XmlStr(request, keep);
		log.info("INF12018:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 电子售卡接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf12018XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");

		String cardAmount = request.getParameter("CARDAMOUNT");

		String custCode = request.getParameter("CUSTCODE");

		// String staffCode = request.getParameter("STAFFCODE");
		//
		// String payPassword = request.getParameter("PAYPASSWORD");
		// String payType = request.getParameter("PAYTYPE");
		//
		// payPassword = ConvertPassword(staffCode, payPassword);

		String cardtypeCode = request.getParameter("CARDTYPECODE");

		String tradeTime = request.getParameter("TRADETIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>"
				+ " 		<ORDERNO>"
				+ orderNo
				+ "</ORDERNO>"
				+ " 		<CARDAMOUNT>"
				+ cardAmount
				+ "</CARDAMOUNT>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				// + " 		<STAFFCODE>"
				// + staffCode
				// + "</STAFFCODE>"
				// + " 		<PAYPASSWORD>"
				// + payPassword
				// + "</PAYPASSWORD>"
				// + " 	<PAYTYPE>"
				// + payType
				// + "</PAYTYPE>"
				+ " 	  <CARDTYPECODE>" + cardtypeCode + "</CARDTYPECODE>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<REMARK1>" + remark1 + "</REMARK1>"
				+ " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {

			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 话费充值接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf12011(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf12011XmlStr(request, keep);
		System.out.println("INF12011:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);

	}

	/**
	 * 话充值接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf12011XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");

		// String tmnNumNo = request.getParameter("TMNNUMNO");
		// String payType = request.getParameter("PAYTYPE");
		//
		// String psamCardNo = request.getParameter("PSAMCARDNO");
		//
		// String eCardNo = request.getParameter("ECARDNO");
		//
		// String passFlag = request.getParameter("PASSFLAG");
		//
		// String staffCode = request.getParameter("STAFFCODE");
		// String payPassword = request.getParameter("PAYPASSWORD");
		String rechargeType = request.getParameter("RECHARGETYPE");
		String phone = request.getParameter("PHONE");
		String rechargeAmount = request.getParameter("RECHARGEAMOUNT");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String tradeTime = request.getParameter("TRADETIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		// if (!ChannelCode.IPOS_CHANELCODE.equals(channelCode)) {
		//
		// payPassword = PasswordUtil.ConvertPassword(staffCode, payPassword);
		// }

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\""
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
				// + " 		<STAFFCODE>"
				// + staffCode
				// + "</STAFFCODE>"
				// + " 		<TMNNUMNO>"
				// + tmnNumNo
				// + "</TMNNUMNO>"
				// + " 		<PAYTYPE>"
				// + payType
				// + "</PAYTYPE>"
				// + " 		<PSAMCARDNO>"
				// + psamCardNo
				// + "</PSAMCARDNO>"
				// + " 		<ECARDNO>"
				// + eCardNo
				// + "</ECARDNO>"
				// + " 		<PASSFLAG>"
				// + passFlag
				// + "</PASSFLAG>"
				// + " 		<PAYPASSWORD>"
				// + payPassword
				// + "</PAYPASSWORD>"
				+ " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " 		<RECHARGETYPE>" + rechargeType + "</RECHARGETYPE>" + " 		<PHONE>" + phone + "</PHONE>"
				+ " 		<RECHARGEAMOUNT>" + rechargeAmount + "</RECHARGEAMOUNT>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<REMARK1>" + remark1 + "</REMARK1>"
				+ " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {

			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 分账方案查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf05105(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf05105XmlStr(request, keep);
		System.out.println("INF05105:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		} else {

			return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
		}
	}

	/**
	 * 分账方案管理接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf05105XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");
		String planCode = request.getParameter("PLANCODE");

		String planName = request.getParameter("PLANNAME");

		String planType = request.getParameter("PLANTYPE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<PLANCODE>" + planCode + "</PLANCODE>" + " 		<PLANNAME>" + planName + "</PLANNAME>" + " 		<PLANTYPE>"
				+ planType + "</PLANTYPE>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);
				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 分账方案管理接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf05104(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf05104XmlStr(request, keep);
		System.out.println("INF05104:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		} else {

			return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
		}
	}

	/**
	 * 分账方案管理接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf05104XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");
		String payPassword = request.getParameter("PAYPASSWORD");

		String operType = request.getParameter("OPERTYPE");
		String planCode = request.getParameter("PLANCODE");

		String planName = request.getParameter("PLANNAME");

		String planDesc = request.getParameter("PLANDESC");

		String planType = request.getParameter("PLANTYPE");

		String planCustCode = request.getParameter("PLANCUSTCODE");

		String planValue = request.getParameter("PLANVALUE");
		String tradeTime = request.getParameter("TRADETIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		if (!ChannelCode.IPOS_CHANELCODE.equals(channelCode)) {

			payPassword = PasswordUtil.ConvertPassword(staffCode, payPassword);
		}

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<CUSTCODE>"
				+ custCode + "</CUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<PAYPASSWORD>" + payPassword + "</PAYPASSWORD>" + " 		<OPERTYPE>" + operType
				+ "</OPERTYPE>" + " 		<PLANCODE>" + planCode + "</PLANCODE>" + " 		<PLANNAME>" + planName + "</PLANNAME>" + " 		<PLANDESC>" + planDesc + "</PLANDESC>"
				+ " 		<PLANTYPE>" + planType + "</PLANTYPE>" + " 		<PLANCUSTCODE>" + planCustCode + "</PLANCUSTCODE>" + " 		<PLANVALUE>" + planValue + "</PLANVALUE>"
				+ " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);
				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 话费充值接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02011(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02011XmlStr(request, keep);
		System.out.println("INF02011:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		} else {

			return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
		}
	}

	/**
	 * 话充值接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02011XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");

		String tmnNumNo = request.getParameter("TMNNUMNO");
		String payType = request.getParameter("PAYTYPE");

		String psamCardNo = request.getParameter("PSAMCARDNO");

		String eCardNo = request.getParameter("ECARDNO");

		String passFlag = request.getParameter("PASSFLAG");

		String staffCode = request.getParameter("STAFFCODE");
		String payPassword = request.getParameter("PAYPASSWORD");
		String rechargeType = request.getParameter("RECHARGETYPE");
		String phone = request.getParameter("PHONE");
		String rechargeAmount = request.getParameter("RECHARGEAMOUNT");
		String txnAmount = request.getParameter("TXNAMOUNT");

		// String premium = request.getParameter("PREMIUM");

		String tradeTime = request.getParameter("TRADETIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		if (!ChannelCode.IPOS_CHANELCODE.equals(channelCode)) {

			payPassword = PasswordUtil.ConvertPassword(staffCode, payPassword);
		}

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<CUSTCODE>"
				+ custCode + "</CUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<PAYTYPE>" + payType + "</PAYTYPE>"
				+ " 		<PSAMCARDNO>" + psamCardNo + "</PSAMCARDNO>" + " 		<ECARDNO>" + eCardNo + "</ECARDNO>" + " 		<PASSFLAG>" + passFlag + "</PASSFLAG>"
				+ " 		<PAYPASSWORD>"
				+ payPassword
				+ "</PAYPASSWORD>"
				+ " 		<TXNAMOUNT>"
				+ txnAmount
				+ "</TXNAMOUNT>"
				// + " 		<PREMIUM>"
				// + premium
				// + "</PREMIUM>"
				+ " 		<RECHARGETYPE>" + rechargeType + "</RECHARGETYPE>" + " 		<PHONE>" + phone + "</PHONE>" + " 		<RECHARGEAMOUNT>" + rechargeAmount + "</RECHARGEAMOUNT>"
				+ " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);
				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 3G流量卡充值接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02021(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02021XmlStr(request, keep);
		log.info("INF02021:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		} else {

			return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
		}

	}

	/**
	 * 3G流量卡充值接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02021XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");

		String custCode = request.getParameter("CUSTCODE");

		String tmnNumNo = request.getParameter("TMNNUMNO");
		String payType = request.getParameter("PAYTYPE");

		String psamCardNo = request.getParameter("PSAMCARDNO");

		String eCardNo = request.getParameter("ECARDNO");

		String passFlag = request.getParameter("PASSFLAG");

		String staffCode = request.getParameter("STAFFCODE");

		String payPassword = request.getParameter("PAYPASSWORD");

		if (!ChannelCode.IPOS_CHANELCODE.equals(channelCode)) {

			payPassword = PasswordUtil.ConvertPassword(staffCode, payPassword);
		}

		String rechargeType = request.getParameter("RECHARGETYPE");

		String verify = request.getParameter("VERIFY");

		String phone = request.getParameter("PHONE");

		String rechargeFlow = request.getParameter("RECHARGEFLOW");

		String systemNO = request.getParameter("SYSTEMNO");

		String txnAmount = request.getParameter("TXNAMOUNT");

		String acceptAreaCode = request.getParameter("ACCEPTAREACODE");

		String tradeTime = request.getParameter("TRADETIME");

		String remark1 = request.getParameter("REMARK1");

		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<CUSTCODE>"
				+ custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<PAYTYPE>" + payType + "</PAYTYPE>" + " 		<PSAMCARDNO>" + psamCardNo
				+ "</PSAMCARDNO>" + " 		<ECARDNO>" + eCardNo + "</ECARDNO>" + " 		<PASSFLAG>" + passFlag + "</PASSFLAG>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>"
				+ " 		<PAYPASSWORD>" + payPassword + "</PAYPASSWORD>" + " 		<RECHARGETYPE>" + rechargeType + "</RECHARGETYPE>" + " 		<VERIFY>" + verify + "</VERIFY>"
				+ " 		<PHONE>" + phone + "</PHONE>" + " 		<RECHARGEFLOW>" + rechargeFlow + "</RECHARGEFLOW>" + " 		<SYSTEMNO>" + systemNO + "</SYSTEMNO>" + " 		<TXNAMOUNT>"
				+ txnAmount + "</TXNAMOUNT>" + " 		<ACCEPTAREACODE>" + acceptAreaCode + "</ACCEPTAREACODE>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<REMARK1>"
				+ remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);
				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	public String inf01006(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getTransactionInfoXmlStr(request, keep);
		System.out.println("xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);

	}

	private String getTransactionInfoXmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String objectCode = request.getParameter("OBJECTCODE");
		String busObject = request.getParameter("BUSOBJECT");
		String dataType = request.getParameter("DATATYPE");
		String acctType = request.getParameter("ACCTTYPE");
		String transCard = request.getParameter("TRANSCARD");
		String startdate = request.getParameter("STARTDATE");
		String enddate = request.getParameter("ENDDATE");
		String countTotal = request.getParameter("COUNTTOTAL");
		String startNum = request.getParameter("STARTNUM");
		String endNum = request.getParameter("ENDNUM");
		String orderSeq = request.getParameter("ORDERSEQ");

		String actionCode = "";// request.getParameter("ACTIONCODE");

		String[] selected = request.getParameterValues("ACTIONCODE");

		if (selected != null) {

			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < selected.length; i++) {

				if (!"".equals(selected[i])) {

					sb.append(selected[i]);

					sb.append("_");
					// actionCode = actionCode+selected[i]+"_";

				}
			}

			if (!"".equals(sb.toString())) {

				actionCode = sb.substring(0, sb.length() - 1);
			}
		}

		String orderstat = request.getParameter("ORDERSTAT");
		String areacode = request.getParameter("AREACODE");
		String productCode = request.getParameter("PRODUCTCODE");
		String includesoncard = request.getParameter("INCLUDESONCARD");
		// String productCode = "";
		// String[] productCodes = request.getParameterValues("PRODUCTCODE");
		// if(productCodes != null){
		// StringBuffer sb = new StringBuffer();
		// for(int i=0; i<productCodes.length; i++){
		// if(!"".equals(productCodes[i])){
		// sb.append(productCodes[i]);
		// sb.append("_");
		// }
		// }
		// if(!"".equals(sb.toString())){
		// productCode = sb.substring(0, sb.length()-1);
		// }
		// }

		// CUSTCODE与OBJECTCODE二选一
		String tempStr = "";
		// String tempVal = "";
		if (!Charset.isEmpty(custCode)) {
			tempStr = " 		<CUSTCODE>" + custCode + "</CUSTCODE>";
			// tempVal = custCode;
		} else {
			tempStr = " 		<OBJECTCODE>" + objectCode + "</OBJECTCODE>";
			// tempVal = objectCode;
		}
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + tempStr + " 		<TRANSCARD>" + transCard + "</TRANSCARD>" + " 		<TMNNUM>" + tmnNum + "</TMNNUM>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>"
				+ " 		<DATATYPE>" + dataType + "</DATATYPE>" + " 		<BUSOBJECT>" + busObject + "</BUSOBJECT>" + " 		<ACCTTYPE>" + acctType + "</ACCTTYPE>" + " 		<STARTDATE>"
				+ startdate + "</STARTDATE>" + " 		<ENDDATE>" + enddate + "</ENDDATE>" + " 		<COUNTTOTAL>" + countTotal + "</COUNTTOTAL>" + " 		<STARTNUM>" + startNum
				+ "</STARTNUM>" + " 		<ENDNUM>" + endNum + "</ENDNUM>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<ACTIONCODE>" + actionCode
				+ "</ACTIONCODE>"
				// + " 		<ORDERTYPE>" + ordertype + "</ORDERTYPE>"
				// + " 		<ACTIONTYPE>" + ordertype + "</ACTIONTYPE>"
				+ " 		<ORDERSTAT>" + orderstat + "</ORDERSTAT>" + " 		<AREACODE>" + areacode + "</AREACODE>" + " 		<PRODUCTCODE>" + productCode + "</PRODUCTCODE>"
				+ " 		<INCLUDESONCARD>" + includesoncard + "</INCLUDESONCARD>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);
				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	public String trade(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getTradeXmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		// if
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
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
		// pay =
		// "<PayPlatRequestParameter> <CTRL-INFO WEBSVRNAME=\"test\" WEBSVRCODE=\"test\" APPFROM=\"1234\" KEEP=\"90000086201209180536517341\" /> <PARAMETERS> <AGENTCODE>ti02@189.com</AGENTCODE> <AREACODE>441000</AREACODE> <ACTIONCODE>1030</ACTIONCODE> <TXNAMOUNT>1</TXNAMOUNT> <PAYEECODE>ti04@189.com</PAYEECODE> <GOODSCODE>ak47</GOODSCODE> <GOODSNAME>枪一支</GOODSNAME> <ORDERSEQ>111111111111111</ORDERSEQ> <TRANSSEQ></TRANSSEQ> <TRADETIME>20120918000000</TRADETIME> <MARK1></MARK1> <MARK2></MARK2> </PARAMETERS> </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * SVC_100001 test
	 * 
	 * @author xiangxin
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02004(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getInf02004XmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf02004XmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String agentCode = request.getParameter("AGENTCODE");

		String staffCode = request.getParameter("STAFFCODE");
		String bankMode = request.getParameter("BANKMODE");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<AGENTCODE>" + agentCode + "</AGENTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<BANKMODE>" + bankMode + "</BANKMODE>"
				+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}


	public String inf02032(HttpServletRequest request)
			throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getINF02032XmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);

	}

	private String getINF02032XmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");

		String txnamount = request.getParameter("TXNAMOUNT");
		String orderSeq = request.getParameter("ORDERSEQ");

		String colleCustCode = request.getParameter("COLLECUSTCODE");

		String tranType = request.getParameter("TRANTYPE");

		String preOrderSeq = request.getParameter("PREORDERSEQ");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String staffCode = request.getParameter("STAFFCODE");

		String password = request.getParameter("PASSWORD");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			password = ConvertPassword(staffCode, password);
		}

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" "
				+ " APPFROM=\"1234\"" + " 		KEEP=\""
				+ keep
				+ "\" />"
				+ " 	<PARAMETERS>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"

				+ " 		<PASSWORD>"
				+ password
				+ "</PASSWORD>"

				+ " 		<COLLECUSTCODE>"
				+ colleCustCode
				+ "</COLLECUSTCODE>"
				+ " 		<TXNAMOUNT>"
				+ txnamount
				+ "</TXNAMOUNT>"
				+ " 		<ORDERSEQ>"
				+ orderSeq
				+ "</ORDERSEQ>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<PASSWORD>"
				+ password
				+ "</PASSWORD>"
				+ " 		<TRANTYPE>"
				+ tranType
				+ "</TRANTYPE>"
				+ " 		<PREORDERSEQ>"
				+ preOrderSeq
				+ "</PREORDERSEQ>"
				+ " 		<REMARK1>"
				+ remark1
				+ "</REMARK1>"
				+ " 		<REMARK2>"
				+ remark2
				+ "</REMARK2>"
				+ " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";

		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode,
						tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay
						.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI
						.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * SVC_02002 test
	 * 
	 * @author xiangxin
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String accountBankInfo(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getAccountBankInfoXmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getAccountBankInfoXmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);
				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * SVC_02003 test
	 * 
	 * @author xiangxin
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String clientVersionManager(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getClientVersionManagerXmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getClientVersionManagerXmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String imsi = request.getParameter("IMSI");
		String curVersion = request.getParameter("CURVERSION");
		String system = request.getParameter("SYSTEM");
		String sysVersion = request.getParameter("SYSVERSION");
		String phone = request.getParameter("PHONE");
		String productNo = request.getParameter("PRODUCTNO");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<IMSI>" + imsi + "</IMSI>" + " 		<CURVERSION>" + curVersion + "</CURVERSION>" + " 		<SYSTEM>" + system + "</SYSTEM>" + " 		<SYSVERSION>"
				+ sysVersion + "</SYSVERSION>" + " 		<PHONE>" + phone + "</PHONE>" + " 		<PRODUCTNO>" + productNo + "</PRODUCTNO>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";

		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

				sign = "";
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * SVC_100001 test
	 * 
	 * @author xiangxin
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String accountInfo(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getCardAccountInfoXmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getCardAccountInfoXmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String agentCode = request.getParameter("AGENTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<AGENTCODE>" + agentCode + "</AGENTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";

		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	private PackageDataSet cum0014() {

		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "CUM_RAND");
		g002.endRow();

		IServiceCall caller = new ServiceCallImpl();

		PackageDataSet dataSet = null;
		try {
			dataSet = caller.call("BIS", "CUM0014", g002);
		} catch (Exception e) {

		}
		return dataSet;
	}

	private String ConvertPassword(String username, String password) {

		String pwd = MD5.MD5Encode(MD5.MD5Encode(username + password + "aienbiei22&*#*(@ieizewbxwerq?"));

		// 获取随机数
		PackageDataSet ds = cum0014();

		String cum = ds.getByID("2174", "217");

		// 组装旧密码
		String[] s = cum.split("_");

		String oldPassword = pwd;

		oldPassword = s[0] + "_" + MD5.MD5Encode(s[0] + oldPassword + s[1]);

		return oldPassword;
	}

	/**
	 * SVC_100004 test
	 * 
	 * @author chenwenchao
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String accountManage(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getAccountMangeXmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getAccountMangeXmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String agentCode = request.getParameter("AGENTCODE");
		String txnchannel = request.getParameter("TXNCHANNEL");
		String opertype = request.getParameter("OPERTYPE");
		String txnamount = request.getParameter("TXNAMOUNT");
		String orderSeq = request.getParameter("ORDERSEQ");

		String transferFlag = request.getParameter("TRANSFERFLAG");
		String colleCustCode = request.getParameter("COLLECUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");
		String password = request.getParameter("PASSWORD");
		String acctType = request.getParameter("ACCTTYPE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		password = ConvertPassword(staffCode, password);

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<AGENTCODE>" + agentCode + "</AGENTCODE>" + " 		<TRANSFERFLAG>" + transferFlag + "</TRANSFERFLAG>" + " 		<COLLECUSTCODE>" + colleCustCode
				+ "</COLLECUSTCODE>" + " 		<TXNCHANNEL>" + txnchannel + "</TXNCHANNEL>" + " 		<OPERTYPE>" + opertype + "</OPERTYPE>" + " 		<TXNAMOUNT>" + txnamount
				+ "</TXNAMOUNT>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<PASSWORD>" + password + "</PASSWORD>"
				+ " 		<ACCTTYPE>" + acctType + "</ACCTTYPE>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";

		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * inf_01_005 test 交易详情查询接口
	 */
	public String transactionDetailInfo(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getTransactionDetailInfoXmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * inf_01_007 test 新交易明细查询接口
	 */
	public String inf01007(HttpServletRequest request) throws ServiceInvokeException {

		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getTransactionDetailInfoXmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getTransactionDetailInfoXmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String transSeq = request.getParameter("TRANSSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String verify = request.getParameter("VERIFY");
		String payPassword = request.getParameter("PAYPASSWORD");

		String staffCode = request.getParameter("STAFFCODE");

		payPassword = ConvertPassword(staffCode, payPassword);

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<TRANSSEQ>" + transSeq + "</TRANSSEQ>" + " 	<CUSTCODE>" + custCode + "</CUSTCODE>" + " 	<VERIFY>" + verify + "</VERIFY>"
				+ " 	<PAYPASSWORD>" + payPassword + "</PAYPASSWORD>" + " 	<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	@SuppressWarnings("unchecked")
	public void initSymSeq(HttpServletRequest request) {
		BaseDao dao = SpringContextHelper.getBaseDaoBean();

		String sql = "select t.area_code, t.area_name from t_sym_area t where t.area_type in ('01','02')";
		List<Map<String, String>> list = dao.queryForList(sql);
		for (Map<String, String> map : list) {
			String areaCode = map.get("AREA_CODE");
			areaCode = areaCode.substring(0, 4) + "001";
			String areaName = map.get("AREA_NAME");

			String insertSql = "insert into t_sym_seq values ('EBK_ACCT_CODE', ?, 100, 99999999, 'N', ?)";
			dao.insert(insertSql, new Object[] { areaCode, areaName });
		}

		System.out.println(list.size());
	}

	/**
	 * 模拟前向商户进行业务网关查询操作，调用网关接口SCS0001
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf01008(HttpServletRequest request) throws ServiceInvokeException {

		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getSag0001XmlStr(request, keep);
		System.out.println("Sag0001XmlStr====" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 获取带鉴权的xml包
	 * 
	 * @param request
	 * @return
	 */
	private String getSag0001XmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String actionCode = request.getParameter("ACTIONCODE");
		String objCode = request.getParameter("OBJCODE");
		String objType = request.getParameter("OBJTYPE");
		String orgCode = request.getParameter("ORGCODE");
		// 拼接鉴权附加信息
		String item1 = request.getParameter("EXTITEM1");
		String item2 = request.getParameter("EXTITEM2");
		String item3 = request.getParameter("EXTITEM3");
		StringBuffer sb = new StringBuffer();
		sb.append("ITEM1").append("=\"").append(item1).append("\" ").append("ITEM2").append("=\"").append(item2).append("\" ").append("ITEM3").append("=\"").append(item3)
				.append("\"");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<ACTIONCODE>" + actionCode + "</ACTIONCODE>" + " 		<OBJCODE>" + objCode + "</OBJCODE>" + " 		<OBJTYPE>" + objType + "</OBJTYPE>"
				+ " 		<ORGCODE>" + orgCode + "</ORGCODE>" + " 		<EXTITEM " + sb.toString() + ">" + "</EXTITEM>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 接受业务处理结果，组成报文调用SAG0006
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf01010(HttpServletRequest request) throws ServiceInvokeException {

		// 获取入参
		String C0_OrderId = request.getParameter("C0_OrderId");
		String C1_SerNum = request.getParameter("C1_SerNum");
		String C2_Code = request.getParameter("C2_Code");
		String C3_InOrderNo = request.getParameter("C3_InOrderNo");
		String C4_Mobile = request.getParameter("C4_Mobile");
		String C5_TxnAmt = request.getParameter("C5_TxnAmt");
		String C6_ReturnCode = request.getParameter("C6_ReturnCode");
		String C7_ResponseCode = request.getParameter("C7_ResponseCode");
		String hmac = request.getParameter("hmac");

		// 本机联调用
		// StringBuffer test = new StringBuffer();
		// test.append(C0_OrderId).append("|").append(C1_SerNum).append("|").append(C2_Code).append("|").append(C3_InOrderNo).append("|")
		// .append(C4_Mobile).append("|").append(C5_TxnAmt).append("|").append(C6_ReturnCode).append("|").append(C7_ResponseCode)
		// .append("|").append("1234567890123456");
		// hmac = MD5.getMD5(test.toString().getBytes());

		StringBuffer sb = new StringBuffer();
		sb.append("C0_OrderId=").append(C0_OrderId).append("&C1_SerNum=").append(C1_SerNum).append("&C2_Code=").append(C2_Code).append("&C3_InOrderNo=").append(C3_InOrderNo)
				.append("&C4_Mobile=").append(C4_Mobile).append("&C5_TxnAmt=").append(C5_TxnAmt).append("&C6_ReturnCode=").append(C6_ReturnCode).append("&C7_ResponseCode=")
				.append(C7_ResponseCode).append("&hmac=").append(hmac);
		String callBackMsg = sb.toString();

		return INF_01_010.execute(C0_OrderId, C1_SerNum, C2_Code, C3_InOrderNo, C4_Mobile, C5_TxnAmt, C6_ReturnCode, C7_ResponseCode, callBackMsg);
	}

	/**
	 * 充值缴费类下单
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf01009(HttpServletRequest request) throws ServiceInvokeException {

		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getScs0001XmlStr(request, keep);
		System.out.println("SCS0001XmlStr====" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 组包充值缴费类下单报文
	 * 
	 * @param request
	 * @return
	 */
	public String getScs0001XmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String actionCode = request.getParameter("ACTIONCODE");
		String termSeq = request.getParameter("TERMSEQ");
		String eventSeq = request.getParameter("EVENTSEQ");
		String objCode = request.getParameter("OBJCODE");
		String objType = request.getParameter("OBJTYPE");
		String payAmount = request.getParameter("PAYAMOUNT");
		String payTime = request.getParameter("PAYTIME");
		String orgCode = request.getParameter("ORGCODE");
		String callBackURL = request.getParameter("CALLBACKURL");
		// 拼接鉴权附加信息
		String item1 = request.getParameter("EXTITEM1");
		String item2 = request.getParameter("EXTITEM2");
		String item3 = request.getParameter("EXTITEM3");
		StringBuffer sb = new StringBuffer();
		sb.append("ITEM1").append("=\"").append(item1).append("\" ").append("ITEM2").append("=\"").append(item2).append("\" ").append("ITEM3").append("=\"").append(item3)
				.append("\"");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<ACTIONCODE>" + actionCode + "</ACTIONCODE>" + " 		<TERMSEQ>" + termSeq + "</TERMSEQ>" + " 		<EVENTSEQ>" + eventSeq + "</EVENTSEQ>"
				+ " 		<OBJCODE>" + objCode + "</OBJCODE>" + " 		<OBJTYPE>" + objType + "</OBJTYPE>" + " 		<PAYAMOUNT>" + payAmount + "</PAYAMOUNT>" + " 		<PAYTIME>" + payTime
				+ "</PAYTIME>" + " 		<ORGCODE>" + orgCode + "</ORGCODE>" + " 		<CALLBACKURL>" + callBackURL + "</CALLBACKURL>" + " 		<EXTITEM " + sb.toString() + ">"
				+ "</EXTITEM>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
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
	public String getCommonXml(String merId, String channelCode, String tmnNum, String sign, String cer, String pay) {
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<Request><VerifyParameter>" + "<MERID>" + merId + "</MERID>" + "<CHANNELCODE>" + channelCode
				+ "</CHANNELCODE>" + "<TMNNUM>" + tmnNum + "</TMNNUM>" + "<SIGN>" + sign + "</SIGN>" + "<CER>" + cer + "</CER>" + "</VerifyParameter>" + pay + "</Request>";
		return xmlStr;
	}

	public String inf02001(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getNewTransactionInfoXmlStr(request, keep);
		System.out.println("xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getNewTransactionInfoXmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String dataType = request.getParameter("DATATYPE");
		String queryMode = request.getParameter("QUERYMODE");
		String objectCode = request.getParameter("OBJECTCODE");
		String orderNo = request.getParameter("ORDERNO");
		String transSeq = request.getParameter("TRANSSEQ");
		String keepNo = request.getParameter("KEEPNO");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		// CUSTCODE与OBJECTCODE二选一
		String tempStr = "";
		if (!Charset.isEmpty(custCode))
			tempStr = " 		<CUSTCODE>" + custCode + "</CUSTCODE>";
		else
			tempStr = " 		<OBJECTCODE>" + objectCode + "</OBJECTCODE>";
		String tempOrderNoXml = "";
		if (!Charset.isEmpty(orderNo)) {
			tempOrderNoXml = " 		<ORDERNO>" + orderNo + "</ORDERNO>";
		}
		String tempKeepNoXml = "";
		if (!Charset.isEmpty(keepNo)) {
			tempKeepNoXml = " 		<KEEPNO>" + keepNo + "</KEEPNO>";
		}
		String tempTransSeqXml = "";
		if (!Charset.isEmpty(transSeq)) {
			tempTransSeqXml = " 		<TRANSSEQ>" + transSeq + "</TRANSSEQ>";
		}
		String queryModeXml = "";
		if (!Charset.isEmpty(queryMode)) {
			queryModeXml = " 		<QUERYMODE>" + queryMode + "</QUERYMODE>";
		}

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + tempStr + tempOrderNoXml + tempKeepNoXml + queryModeXml + tempTransSeqXml + " 		<TMNNUM>" + tmnNum + "</TMNNUM>" + " 		<DATATYPE>" + dataType
				+ "</DATATYPE>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2></PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	public String inf05001(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getInf05001XmlStr(request, keep);
		System.out.println("xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 出票[火车票]接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf12039(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf12039XmlStr(request, keep);
		log.info("INF12039:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 出票[火车票]接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf12039XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String systemNo = request.getParameter("SYSTEMNO");
		String tranAmount = request.getParameter("TRANAMOUNT");
		String acceptDate = request.getParameter("ACCEPTDATE");
		String outCustSign = request.getParameter("OUTCUSTSIGN");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<SYSTEMNO>" + systemNo + "</SYSTEMNO>" + " 		<TRANAMOUNT>" + tranAmount + "</TRANAMOUNT>" + " 		<ACCEPTDATE>"
				+ acceptDate + "</ACCEPTDATE>" +         "<OUTCUSTSIGN>" + outCustSign + "</OUTCUSTSIGN>" + " 		 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 预订[火车票]接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf12038(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf12038XmlStr(request, keep);
		log.info("INF12038:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 预订[火车票]接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf12038XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String phone = request.getParameter("PHONE");
		String searchId = request.getParameter("SEARCHID");
		String trainId = request.getParameter("TRAINID");
		String bookInfo = request.getParameter("BOOKINFO");
		String date = request.getParameter("DATE");

		String systemNo = request.getParameter("SYSTEMNO");

		// String isOut = request.getParameter("ISOUT");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<PHONE>" + phone + "</PHONE>" + " 		<SEARCHID>" + searchId + "</SEARCHID>" + " 		<TRAINID>" + trainId
				+ "</TRAINID>" + " 		<BOOKINFO>" + bookInfo + "</BOOKINFO>" + " 		<DATE>" + date + "</DATE>" + " 		<SYSTEMNO>" + systemNo + "</SYSTEMNO>"
				// + " 		<ISOUT>"
				// + isOut
				// + "</ISOUT>"
				+ " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * QQ下单
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02033(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02033XmlStr(request, keep);
		log.info("INF02033:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		} else {

			return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
		}
	}

	/**
	 * QQ下单
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02033XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String payPassword = request.getParameter("PAYPASSWORD");
		if (!ChannelCode.IPOS_CHANELCODE.equals(channelCode)) {

			payPassword = PasswordUtil.ConvertPassword(staffCode, payPassword);
		}

		String systemNo = request.getParameter("SYSTEMNO");
		String productCode = request.getParameter("PRODUCTCODE");
		String areaCode = request.getParameter("ACCEPTAREACODE");
		String acctCode = request.getParameter("ACCTCODE");
		String tradeTime = request.getParameter("TRADETIME");

		String rechAmount = request.getParameter("RECHARGEAMOUNT");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderNo + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<PAYPASSWORD>" + payPassword + "</PAYPASSWORD>"
				+ " 		<SYSTEMNO>" + systemNo + "</SYSTEMNO>" + " 		<PRODUCTCODE>" + productCode + "</PRODUCTCODE>" + " 		<ACCEPTAREACODE>" + areaCode + "</ACCEPTAREACODE>"
				+ " 		<ACCTCODE>" + acctCode + "</ACCTCODE>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<RECHARGEAMOUNT>" + rechAmount + "</RECHARGEAMOUNT>"
				+ " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);
				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * QQ发货接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02034(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02034XmlStr(request, keep);
		log.info("INF02034:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		} else {

			return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
		}
	}

	/**
	 * QQ发货接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02034XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERSEQ");

		String custCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String payPassword = request.getParameter("PAYPASSWORD");

		payPassword = ConvertPassword(staffCode, payPassword);

		String systemNo = request.getParameter("SYSTEMNO");

		String productCode = request.getParameter("PRODUCTCODE");

		String areaCode = request.getParameter("ACCEPTAREACODE");

		String tradeTime = request.getParameter("TRADETIME");

		String acctCode = request.getParameter("ACCTCODE");

		String txtAmount = request.getParameter("TXNAMOUNT");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderNo + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<PAYPASSWORD>" + payPassword + "</PAYPASSWORD>" + " 	<SYSTEMNO>"
				+ systemNo + "</SYSTEMNO>" + " 	  <PRODUCTCODE>" + productCode + "</PRODUCTCODE>" + " 	  <ACCEPTAREACODE>" + areaCode + "</ACCEPTAREACODE>" + " 		<TRADETIME>"
				+ tradeTime + "</TRADETIME>" + " 		<ACCTCODE>" + acctCode + "</ACCTCODE>" + " 		<TXNAMOUNT>" + txtAmount + "</TXNAMOUNT>" + " 		<REMARK1>" + remark1 + "</REMARK1>"
				+ " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {

			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);
				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * QQ发货接口(纯业务)
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf12034(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf12034XmlStr(request, keep);
		log.info("INF12034:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * QQ发货接口(纯业务)
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf12034XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERSEQ");

		String custCode = request.getParameter("CUSTCODE");

		String systemNo = request.getParameter("SYSTEMNO");

		String productCode = request.getParameter("PRODUCTCODE");

		String areaCode = request.getParameter("ACCEPTAREACODE");

		String tradeTime = request.getParameter("TRADETIME");

		String acctCode = request.getParameter("ACCTCODE");

		String txtAmount = request.getParameter("TXNAMOUNT");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderNo + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<SYSTEMNO>" + systemNo + "</SYSTEMNO>" + " 	  <PRODUCTCODE>" + productCode + "</PRODUCTCODE>"
				+ " 	  <ACCEPTAREACODE>" + areaCode + "</ACCEPTAREACODE>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<ACCTCODE>" + acctCode + "</ACCTCODE>"
				+ " 		<TXNAMOUNT>" + txtAmount + "</TXNAMOUNT>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 资金归档测试
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf01011(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddHHmmss", 4);

		String xmlStr = getInf01011XmlStr(request, keep);
		System.out.println("inf01011xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 短信下发接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf03001(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddHHmmss", 4);

		String xmlStr = getInf03001XmlStr(request, keep);
		System.out.println("xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 加密随机数下发接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf03002(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddHHmmss", 4);

		String xmlStr = getInf03002XmlStr(request, keep);
		System.out.println("xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 收款请求接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf03003(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddHHmmss", 4);

		String xmlStr = getInf03003XmlStr(request, keep);
		System.out.println("xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 收款请求接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf03003XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String staffCode = request.getParameter("STAFFCODE");
		String password = request.getParameter("PASSWORD");
		String agentCode = request.getParameter("AGENTCODE");
		String areaCode = request.getParameter("AREACODE");
		String payeeCode = request.getParameter("PAYEECODE");
		String payeeName = request.getParameter("PAYEENAME");
		String tradeTime = request.getParameter("TRADETIME");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String remark1 = request.getParameter("REMARK1");
		
		password = ConvertPassword(staffCode, password);

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>"
				+ " 		<PASSWORD>" + password + "</PASSWORD>" + " 		<AGENTCODE>" + agentCode + "</AGENTCODE>" + " 		<AREACODE>" + areaCode + "</AREACODE>" + " 		<PAYEECODE>"
				+ payeeCode + "</PAYEECODE>" + " 		<PAYEENAME>" + payeeName + "</PAYEENAME>" + " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " 		<TRADETIME>" + tradeTime
				+ "</TRADETIME>" + " 		<REMARK1>" + remark1 + "</REMARK1></PARAMETERS>" + " </PayPlatRequestParameter>";
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
	 * 鉴权验证接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf01012(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddHHmmss", 4);

		String xmlStr = getInf01012XmlStr(request, keep);
		System.out.println("xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 实时解签接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf01013(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddHHmmss", 4);

		String xmlStr = getInf01013XmlStr(request, keep);
		System.out.println("xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 实时验证接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf01014(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddHHmmss", 4);

		String xmlStr = getInf01014XmlStr(request, keep);
		System.out.println("xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 实时冲正接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf01015(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddHHmmss", 4);

		String xmlStr = getInf01015XmlStr(request, keep);
		System.out.println("xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf01015XmlStr(HttpServletRequest request, String keep) {
		// TODO Auto-generated method stub
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String apTransSeq = request.getParameter("APTRANSSEQ");
		String appKeep = request.getParameter("APPKEEP");
		String bankAcct = request.getParameter("BANKACCT");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<APTRANSSEQ>" + apTransSeq + "</APTRANSSEQ>"
				+ " 		<APPKEEP>" + appKeep + "</APPKEEP>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " 		<REMARK1>" + remark1
				+ "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	private String getInf01014XmlStr(HttpServletRequest request, String keep) {
		// TODO Auto-generated method stub
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String busiType = request.getParameter("BUSITYPE");
		String areaCode = request.getParameter("AREACODE");
		// String branchCode = request.getParameter("BRANCHCODE");

		String bankAcct = request.getParameter("BANKACCT");
		String transAccName = request.getParameter("TRANSACCNAME");
		String bankCode = request.getParameter("BANKCODE");
		String openBank = request.getParameter("OPENBANK");

		String cardFlag = request.getParameter("CARDFLAG");
		String privateFlag = request.getParameter("PRIVATEFLAG");
		String validity = request.getParameter("VALIDITY");
		String cvn2 = request.getParameter("CVN2");
		String certType = request.getParameter("CERTTYPE");
		String certNo = request.getParameter("CERTNO");

		String tel = request.getParameter("TEL");
		// String txnAmount = request.getParameter("TXNAMOUNT");
		String memo = request.getParameter("MEMO");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String xml = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<BUSITYPE>" + busiType + "</BUSITYPE>"
				+ " 		<AREACODE>"
				+ areaCode
				+ "</AREACODE>"
				// + " 		<BRANCHCODE>" + branchCode + "</BRANCHCODE>"
				+ " 		<TRANSACCNAME>" + transAccName + "</TRANSACCNAME>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<OPENBANK>" + openBank + "</OPENBANK>"
				+ " 		<BANKCODE>" + bankCode + "</BANKCODE>"

				+ " 		<PRIVATEFLAG>" + privateFlag + "</PRIVATEFLAG>" + " 		<CARDFLAG>" + cardFlag + "</CARDFLAG>" + " 		<VALIDITY>" + validity + "</VALIDITY>" + " 		<CVN2>"
				+ cvn2 + "</CVN2>" + " 		<CERTTYPE>" + certType + "</CERTTYPE>" + " 		<CERTNO>" + certNo + "</CERTNO>"

				+ " 		<TEL>" + tel
				+ "</TEL>"
				// + " 		<EXTERNALID>" + externalId + "</EXTERNALID>"
				// + " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>"
				+ " 		<MEMO>" + memo + "</MEMO>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(xml.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, xml);
	}

	private String getInf01013XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String contractId = request.getParameter("CONTRACTID");
		String externalId = request.getParameter("EXTERNALID");
		String bankAcct = request.getParameter("BANKACCT");
		String memo = request.getParameter("MEMO");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<CONTRACTID>" + contractId + "</CONTRACTID>"
				+ " 		<EXTERNALID>" + externalId + "</EXTERNALID>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<MEMO>" + memo + "</MEMO>" + " 		<REMARK1>" + remark1
				+ "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	private String getInf01012XmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String staffCode = request.getParameter("STAFFCODE");
		String password = request.getParameter("PASSWORD");
		String verifyType = request.getParameter("VERIFYTYPE");
		String verifyCode = request.getParameter("VERIFYCODE");

		String verifyLevel = request.getParameter("VERIFYLEVEL");

		String imei = request.getParameter("IMEI");
		String imsi = request.getParameter("IMSI");
		String wifimac = request.getParameter("WIFIMAC");
		String bluemac = request.getParameter("BLUEMAC");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		password = ConvertPassword(staffCode, password);

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<PASSWORD>" + password + "</PASSWORD>" + " 		<VERIFYTYPE>" + verifyType + "</VERIFYTYPE>"
				+ " 		<VERIFYCODE>" + verifyCode + "</VERIFYCODE>" + " 		<VERIFYLEVEL>" + verifyLevel + "</VERIFYLEVEL>" + " 		<IMEI>" + imei + "</IMEI>" + " 		<IMSI>" + imsi
				+ "</IMSI>" + " 		<WIFIMAC>" + wifimac + "</WIFIMAC>" + " 		<BLUEMAC>" + bluemac + "</BLUEMAC>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>"
				+ remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	private String getInf03002XmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String verifyType = request.getParameter("VERIFYTYPE");
		String staffCode = request.getParameter("STAFFCODE");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<VERIFYTYPE>" + verifyType + "</VERIFYTYPE>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	private String getInf03001XmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		// String sign = request.getParameter("SIGN");
		// String cer = request.getParameter("CER");

		String requsttime = request.getParameter("REQUESTTIME");
		String staffCode = request.getParameter("STAFFCODE");
		String sendType = request.getParameter("SENDTYPE");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + "<SENDTYPE>" + sendType + "</SENDTYPE>" + "           <REQTIME>" + requsttime + "</REQTIME>"
				+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		return getCommonXml(merId, channelCode, tmnNum, "", "", pay);
	}

	private String getInf01011XmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String externalId = request.getParameter("EXTERNALID");
		// String prtnCode;
		String custCode;
		// if ("0001".equals(contractType)) {
		// prtnCode = request.getParameter("PRTNCODE");
		// "<PRTNCODE>" + prtnCode + "</PRTNCODE>";
		// } else {
		custCode = request.getParameter("CUSTCODE");

		// }
		String province = request.getParameter("PROVINCE");
		String areaCode = request.getParameter("AREACODE");
		String prtnType = request.getParameter("PRTNTYPE");
		String branchProp = request.getParameter("BRANCHPROP");
		String branchName = request.getParameter("BRANCHNAME");
		String bankArea = request.getParameter("BANKAREA");
		String veriType = request.getParameter("VERITYPE");
		String busiType = request.getParameter("BUSITYPE");
		String project = request.getParameter("PROJECT");

		String accName = request.getParameter("ACCNAME");
		String bankAcct = request.getParameter("BANKACCT");
		String bankInfo = request.getParameter("BANKINFO");
		String bankCode = request.getParameter("BANKCODE");
		String branchCode = request.getParameter("BRANCHCODE");

		String cardflag = request.getParameter("CARDFLAG");
		String privateflag = request.getParameter("PRIVATEFLAG");
		String creditvalidtime = request.getParameter("CREDITVALIDTIME");
		String creditvalidcode = request.getParameter("CREDITVALIDCODE");
		String certCode = request.getParameter("CERTCODE");
		String certNo = request.getParameter("CERTNO");
		String contactphone = request.getParameter("CONTACTPHONE");
		String contactAddr = request.getParameter("CONTACTADDR");
		String recvcorp = request.getParameter("RECVCORP");

		String memo = request.getParameter("MEMO");
		String remark = request.getParameter("REMARK");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " <CUSTCODE>" + custCode + "</CUSTCODE>" + "          <PROJECT>" + project + "</PROJECT>"
				+ "          <EXTERNALID>" + externalId + "</EXTERNALID>" + " 		<PROVINCE>" + province + "</PROVINCE>" + "          <AREACODE>" + areaCode + "</AREACODE>"
				+ " 		<PRTNTYPE>" + prtnType + "</PRTNTYPE>" + " 		<BRANCHPROP>" + branchProp + "</BRANCHPROP>" + " 		<BRANCHNAME>" + branchName + "</BRANCHNAME>"
				+ " 		<VERITYPE>" + veriType + "</VERITYPE>" + " 		<BUSITYPE>" + busiType + "</BUSITYPE>" + " 		<CERTCODE>" + certCode + "</CERTCODE>" + " 		<CERTNO>" + certNo
				+ "</CERTNO>" + " 		<ACCNAME>" + accName + "</ACCNAME>" + " 		<BANKAREA>" + bankArea + "</BANKAREA>" + " 		<BANKINFO>" + bankInfo + "</BANKINFO>" + " 		<BANKCODE>"
				+ bankCode + "</BANKCODE>" + " 		<BRANCHCODE>" + branchCode + "</BRANCHCODE>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<CARDFLAG>" + cardflag
				+ "</CARDFLAG>" + " 		<PRIVATEFLAG>" + privateflag + "</PRIVATEFLAG>" + " 		<CONTACTPHONE>" + contactphone + "</CONTACTPHONE>" + " 		<CONTACTADDR>" + contactAddr
				+ "</CONTACTADDR>" + " 		<RECVCORP>" + recvcorp + "</RECVCORP>" + " 		<CREDITVALIDTIME>" + creditvalidtime + "</CREDITVALIDTIME>" + " 		<CREDITVALIDCODE>"
				+ creditvalidcode + "</CREDITVALIDCODE>" + " 		<MEMO>" + memo + "</MEMO>" + " 		<REMARK>" + remark + "</REMARK>" + " 		<REMARK2>" + remark2 + "</REMARK2>"
				+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	private String getInf05001XmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String busiType = request.getParameter("BUSITYPE");

		String transferFlag = request.getParameter("TRANSFERFLAG");
		String colleCustCode = request.getParameter("COLLECUSTCODE");

		String externalId = request.getParameter("EXTERNALID");
		String branchCode = request.getParameter("BRANCHCODE");
		String bankAcct = request.getParameter("BANKACCT");
		String transContractId = request.getParameter("TRANSCONTRACTID");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String memo = request.getParameter("MEMO");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay1 = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<BUSITYPE>" + busiType + "</BUSITYPE>" + " 		<TRANSFERFLAG>" + transferFlag
				+ "</TRANSFERFLAG>" + " 		<COLLECUSTCODE>" + colleCustCode + "</COLLECUSTCODE>" + " 		<BRANCHCODE>" + branchCode + "</BRANCHCODE>" + "          <EXTERNALID>"
				+ externalId + "</EXTERNALID>" + "          <BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<TRANSCONTRACTID>" + transContractId + "</TRANSCONTRACTID>"
				+ " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " 		<MEMO>" + memo + "</MEMO>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2
				+ "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay1.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay1);
	}

	/**
	 * 账户转账收款名单查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String transAcctList(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getTransAcctListXmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getTransAcctListXmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String agentCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<CUSTCODE>" + agentCode + "</CUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";

		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 付款单查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String payBillList(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getPayBillListXmlStr(request, keep);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getPayBillListXmlStr(HttpServletRequest request, String keep) {

		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String agentCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String objCode = request.getParameter("OBJCODE");
		String stat = request.getParameter("STAT");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<CUSTCODE>" + agentCode + "</CUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<OBJCODE>" + objCode + "</OBJCODE>"
				+ " 		<STAT>" + stat + "</STAT>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 付款到银行账户接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf05003(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getInf05003XmlStr(request, keep);
		System.out.println("INF05003:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf05003XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String transCode = request.getParameter("TRANSCODE");
		String recvCode = request.getParameter("RECVCODE");
		String revaccNo = request.getParameter("REVACCNO");
		String revaccName = request.getParameter("REVACCNAME");
		String addr = request.getParameter("ADDR");
		String phone = request.getParameter("PHONE");
		String certId = request.getParameter("CERTID");
		String certCode = request.getParameter("CERTCODE");
		String bankBelong = request.getParameter("BANKBELONG");
		String bankId = request.getParameter("BANKID");
		String bankSubId = request.getParameter("BANKSUBID");
		String bankCardId = request.getParameter("BANKCARDID");
		String bankCardType = request.getParameter("BANKCARDTYPE");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String orderSeq = request.getParameter("ORDERSEQ");
		String tradeTime = request.getParameter("TRADETIME");
		String remark = request.getParameter("REMARK");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<TRANSCODE>" + transCode + "</TRANSCODE>" + " 		<RECVCODE>" + recvCode + "</RECVCODE >" + " 		<REVACCNO>" + revaccNo + "</REVACCNO>"
				+ " 		<REVACCNAME>" + revaccName + "</REVACCNAME>" + " 		<ADDR>" + addr + "</ADDR>" + " 		<PHONE>" + phone + "</PHONE>" + " 		<CERTID>" + certId + "</CERTID>"
				+ " 		<CERTCODE>" + certCode + "</CERTCODE>" + " 		<BANKBELONG>" + bankBelong + "</BANKBELONG>" + " 		<BANKID>" + bankId + "</BANKID>" + " 		<BANKSUBID>"
				+ bankSubId + "</BANKSUBID>" + " 		<BANKCARDID>" + bankCardId + "</BANKCARDID>" + " 		<BANKCARDTYPE>" + bankCardType + "</BANKCARDTYPE>" + " 		<TXNAMOUNT>"
				+ txnAmount + "</TXNAMOUNT>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<REMARK>" + remark + "</REMARK>"
				+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 付款接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf05004(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getInf05004XmlStr(request, keep);
		System.out.println("INF05004:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf05004XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String staffCode = request.getParameter("STAFFCODE");
		String passWord = request.getParameter("PASSWORD");
		String custCode = request.getParameter("CUSTCODE");
		String preOrderSeq = request.getParameter("PREORDERSEQ");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<PASSWORD>" + passWord + "</PASSWORD>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<PREORDERSEQ>" + preOrderSeq + "</PREORDERSEQ>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	// 户名验证代收付接口
	public String inf05102(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getInf05102XmlStr(request, keep);
		System.out.println("INF05102:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf05102XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");

		String transferFlag = request.getParameter("TRANSFERFLAG");
		String colleCustCode = request.getParameter("COLLECUSTCODE");

		String busiType = request.getParameter("BUSITYPE");
		String areaCode = request.getParameter("AREACODE");
		String branchCode = request.getParameter("BRANCHCODE");
		String bankAcct = request.getParameter("BANKACCT");
		String transAccName = request.getParameter("TRANSACCNAME");
		String bankCode = request.getParameter("BANKCODE");

		String cardFlag = request.getParameter("CARDFLAG");
		String validity = request.getParameter("VALIDITY");
		String cvn2 = request.getParameter("CVN2");
		String certType = request.getParameter("CERTTYPE");
		String certNo = request.getParameter("CERTNO");
		String tel = request.getParameter("TEL");
		String openBank = request.getParameter("OPENBANK");
		String privateFlag = request.getParameter("PRIVATEFLAG");
		String externalId = request.getParameter("EXTERNALID");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String memo = request.getParameter("MEMO");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String xml = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<TRANSFERFLAG>" + transferFlag
				+ "</TRANSFERFLAG>" + " 		<COLLECUSTCODE>" + colleCustCode + "</COLLECUSTCODE>" + " 		<BUSITYPE>" + busiType + "</BUSITYPE>" + " 		<AREACODE>" + areaCode
				+ "</AREACODE>" + " 		<BRANCHCODE>" + branchCode + "</BRANCHCODE>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<BANKCODE>" + bankCode + "</BANKCODE>"
				+ " 		<TRANSACCNAME>" + transAccName + "</TRANSACCNAME>" + " 		<CARDFLAG>" + cardFlag + "</CARDFLAG>" + " 		<CERTTYPE>" + certType + "</CERTTYPE>" + " 		<CERTNO>"
				+ certNo + "</CERTNO>" + " 		<TEL>" + tel + "</TEL>" + " 		<OPENBANK>" + openBank + "</OPENBANK>" + " 		<VALIDITY>" + validity + "</VALIDITY>" + " 		<CVN2>" + cvn2
				+ "</CVN2>" + " 		<PRIVATEFLAG>" + privateFlag + "</PRIVATEFLAG>" + " 		<EXTERNALID>" + externalId + "</EXTERNALID>" + " 		<TXNAMOUNT>" + txnAmount
				+ "</TXNAMOUNT>" + " 		<MEMO>" + memo + "</MEMO>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(xml.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, xml);
	}

	// 无验证代收付接口
	public String inf05103(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getInf05103XmlStr(request, keep);
		System.out.println("INF05103:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf05103XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");

		String transferFlag = request.getParameter("TRANSFERFLAG");
		String colleCustCode = request.getParameter("COLLECUSTCODE");

		String busiType = request.getParameter("BUSITYPE");
		String areaCode = request.getParameter("AREACODE");
		String branchCode = request.getParameter("BRANCHCODE");
		String bankAcct = request.getParameter("BANKACCT");
		String transAccName = request.getParameter("TRANSACCNAME");
		String bankCode = request.getParameter("BANKCODE");

		String cardFlag = request.getParameter("CARDFLAG");
		String validity = request.getParameter("VALIDITY");
		String cvn2 = request.getParameter("CVN2");
		String privateFlag = request.getParameter("PRIVATEFLAG");
		String certType = request.getParameter("CERTTYPE");
		String certNo = request.getParameter("CERTNO");
		String tel = request.getParameter("TEL");
		String openBank = request.getParameter("OPENBANK");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String memo = request.getParameter("MEMO");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String xml = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<TRANSFERFLAG>" + transferFlag
				+ "</TRANSFERFLAG>" + " 		<COLLECUSTCODE>" + colleCustCode + "</COLLECUSTCODE>" + " 		<BUSITYPE>" + busiType + "</BUSITYPE>" + " 		<AREACODE>" + areaCode
				+ "</AREACODE>" + " 		<BRANCHCODE>" + branchCode + "</BRANCHCODE>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<BANKCODE>" + bankCode + "</BANKCODE>"
				+ " 		<TRANSACCNAME>" + transAccName + "</TRANSACCNAME>" + " 		<OPENBANK>" + openBank + "</OPENBANK>" + " 		<CARDFLAG>" + cardFlag + "</CARDFLAG>"
				+ " 		<VALIDITY>" + validity + "</VALIDITY>" + " 		<CVN2>" + cvn2 + "</CVN2>" + " 		<PRIVATEFLAG>" + privateFlag + "</PRIVATEFLAG>" + " 		<CERTTYPE>" + certType
				+ "</CERTTYPE>" + " 		<CERTNO>" + certNo + "</CERTNO>" + " 		<TEL>" + tel + "</TEL>" + " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " 		<MEMO>" + memo
				+ "</MEMO>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(xml.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, xml);
	}

	// 签约绑定查询接口
	public String inf01021(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);

		String xmlStr = getInf01021XmlStr(request, keep);
		System.out.println("INF01021:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf01021XmlStr(HttpServletRequest request, String keep) {
		// TODO Auto-generated method stub
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String branchName = request.getParameter("BRANCHNAME");
		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String externalId = request.getParameter("EXTERNALID");
		String bankAcct = request.getParameter("BANKACCT");
		String memo = request.getParameter("MEMO");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		String xml = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\" />"
				+ " 	<PARAMETERS>" + " 		<BRANCHNAME>" + branchName + "</BRANCHNAME>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<EXTERNALID>" + externalId + "</EXTERNALID>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<MEMO>" + memo + "</MEMO>" + " 		<REMARK1>" + remark1
				+ "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(xml.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, xml);
	}

	/**
	 * 车船税账单查询
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06006(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06006XmlStr(request, keep);
		System.out.println("INF06006:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 车船税账单查询
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06006XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");

		String custCode = request.getParameter("CUSTCODE");

		String tmnNumNo = request.getParameter("TMNNUMNO");

		String plateNo = request.getParameter("PLATENO");

		String engineNo = request.getParameter("ENGINENO");

		String acceptDate = request.getParameter("ACCEPTDATE");

		String remark1 = request.getParameter("REMARK1");

		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<PLATENO>" + plateNo + "</PLATENO>" + " 		<ENGINENO>" + engineNo
				+ "</ENGINENO>" + " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>"
				+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 卡户管理接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06007(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06007XmlStr(request, keep);
		System.out.println("INF06007:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 卡户管理接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06007XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");

		String custCode = request.getParameter("CUSTCODE");

		String childCustCode = request.getParameter("CHILDCUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String payPassword = request.getParameter("PAYPASSWORD");

		payPassword = ConvertPassword(staffCode, payPassword);

		String operType = request.getParameter("OPERTYPE");

		String dayLimit = request.getParameter("DAYLIMIT");

		String tradeTime = request.getParameter("TRADETIME");

		String remark1 = request.getParameter("REMARK1");

		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<CUSTCODE>"
				+ custCode + "</CUSTCODE>" + " 		<CHILDCUSTCODE>" + childCustCode + "</CHILDCUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<PAYPASSWORD>"
				+ payPassword + "</PAYPASSWORD>" + " 		<OPERTYPE>" + operType + "</OPERTYPE>" + " 		<DAYLIMIT>" + dayLimit + "</DAYLIMIT>" + " 		<TRADETIME>" + tradeTime
				+ "</TRADETIME>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 子卡列表查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06008(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06008XmlStr(request, keep);
		System.out.println("INF06008:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 子卡列表查询接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06008XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String childCustCode = request.getParameter("CHILDCUSTCODE");

		String acctType = request.getParameter("ACCTTYPE");

		String startNum = request.getParameter("STARTNUM");

		String endNum = request.getParameter("ENDNUM");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<CHILDCUSTCODE>" + childCustCode + "</CHILDCUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<ACCTTYPE>" + acctType + "</ACCTTYPE>"
				+ " 		<STARTNUM>" + startNum + "</STARTNUM>" + " 		<ENDNUM>" + endNum + "</ENDNUM>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		/*
		 * try { sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay .getBytes("UTF-8"))); cer = NETCAPKI.getX509CertificateString(NETCAPKI .getSrvX509Certificate()); } catch
		 * (Exception e) { e.printStackTrace(); }
		 */
		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 水电煤账单查询
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06003(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06003XmlStr(request, keep);
		System.out.println("INF06003:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 水电煤账单查询
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06003XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String tmnNumNo = request.getParameter("TMNNUMNO");

		// String outTmnNumNo = request.getParameter("OUTTMNNUMNO");
		String selectType = request.getParameter("SELECTTYPE");
		String selectValue = request.getParameter("SELECTVALUE");
		String phoneNumber = request.getParameter("PHONENUMBER");
		String billMonth = request.getParameter("BILLMONTH");
		String acceptDate = request.getParameter("ACCEPTDATE");
		String acceptAreaCode = request.getParameter("ACCEPTAREACODE");
		String additem1 = request.getParameter("ADDITEM1");
		String additem2 = request.getParameter("ADDITEM2");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		String remark3 = request.getParameter("REMARK3");
		String remark4 = request.getParameter("REMARK4");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>"
				+ custCode
				+ "</CUSTCODE>"
				+ " 		<STAFFCODE>"
				+ staffCode
				+ "</STAFFCODE>"
				+ " 		<TMNNUMNO>"
				+ tmnNumNo
				+ "</TMNNUMNO>"
				// + " 		<OUTTMNNUMNO>"
				// + outTmnNumNo
				// + "</OUTTMNNUMNO>"
				+ " 		<SELECTTYPE>" + selectType + "</SELECTTYPE>" + " 		<SELECTVALUE>" + selectValue + "</SELECTVALUE>" + " 		<PHONENUMBER>" + phoneNumber + "</PHONENUMBER>"
				+ " 		<BILLMONTH>" + billMonth + "</BILLMONTH>" + " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>" + " 		<ACCEPTAREACODE>" + acceptAreaCode + "</ACCEPTAREACODE>"
				+ " 		<ADDITEM1>" + additem1 + "</ADDITEM1>" + " 		<ADDITEM2>" + additem2 + "</ADDITEM2>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2
				+ "</REMARK2>" + " 		<REMARK3>" + remark3 + "</REMARK3>" + " 		<REMARK4>" + remark4 + "</REMARK4>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 终端签到接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06001(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06001XmlStr(request, keep);
		System.out.println("INF06001:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 终端签到接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06001XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String psamCardNo = request.getParameter("PSAMCARDNO");
		String random = request.getParameter("RANDOM");
		String condition = request.getParameter("CONDITION");
		String encryption = request.getParameter("ENCRYPTION");
		String networkNo = request.getParameter("NETWORKNO");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<PSAMCARDNO>" + psamCardNo + "</PSAMCARDNO>" + " 		<RANDOM>" + random + "</RANDOM>" + " 		<TMNNUMNO>" + tmnNumNo
				+ "</TMNNUMNO>" + " 		<CONDITION>" + condition + "</CONDITION>" + " 		<ENCRYPTION>" + encryption + "</ENCRYPTION>" + " 		<NETWORKNO>" + networkNo + "</NETWORKNO>"
				+ " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 信用卡还款接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06101(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06101XmlStr(request, keep);

		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 信用卡还款接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06101XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String acceptDate = request.getParameter("ACCEPTDATE");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String targetAccount = request.getParameter("TARGETACCOUNT");
		String payType = request.getParameter("PAYTYPE");
		String staffCode = request.getParameter("STAFFCODE");
		String accName = request.getParameter("ACCNAME");
		String bankAcct = request.getParameter("BANKACCT");
		String privateFlag = request.getParameter("PRIVATEFLAG");

		String cardFlag = request.getParameter("CARDFLAG");
		String creditValidTime = request.getParameter("CREDITVALIDTIME");
		String creditValidCode = request.getParameter("CREDITVALIDCODE");
		String bankArea = request.getParameter("BANKAREA");
		String bankCode = request.getParameter("BANKCODE");
		String bankInfo = request.getParameter("BANKINFO");
		String certType = request.getParameter("CERTTYPE");
		String certNo = request.getParameter("CERTNO");
		String contactPhone = request.getParameter("CONTACTPHONE");
		String contactAddr = request.getParameter("CONTACTADDR");
		String operUser = request.getParameter("OPERUSER");
		String operPassword = request.getParameter("OPERPASSWORD");

		String payAccount = request.getParameter("PAYACCOUNT");
		String payPassword = request.getParameter("PAYPASSWORD");
		String psamCardNo = request.getParameter("PSAMCARDNO");
		String trackTwo = request.getParameter("TRACKTWO");
		String trackThree = request.getParameter("TRACKTHREE");
		String networkNo = request.getParameter("NETWORKNO");
		String outCustSign = request.getParameter("OUTCUSTSIGN");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		payPassword = ConvertPassword(staffCode, payPassword);
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>" + " 		<TXNAMOUNT>"
				+ txnAmount + "</TXNAMOUNT>" + " 		<TARGETACCOUNT>" + targetAccount + "</TARGETACCOUNT>" + " 		<PAYTYPE>" + payType + "</PAYTYPE>" + " 		<STAFFCODE>" + staffCode
				+ "</STAFFCODE>" + " 		<ACCNAME>" + accName + "</ACCNAME>" + " 		<BANKAREA>" + bankArea + "</BANKAREA>" + " 		<BANKCODE>" + bankCode + "</BANKCODE>"
				+ " 		<BANKINFO>" + bankInfo + "</BANKINFO>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<PRIVATEFLAG>" + privateFlag + "</PRIVATEFLAG>"
				+ " 		<CREDITVALIDTIME>" + creditValidTime + "</CREDITVALIDTIME>" + " 		<CREDITVALIDCODE>" + creditValidCode + "</CREDITVALIDCODE>" + " 		<CERTTYPE>" + certType
				+ "</CERTTYPE>" + " 		<CERTNO>" + certNo + "</CERTNO>" + " 		<CARDFLAG>" + cardFlag + "</CARDFLAG>" + " 		<CONTACTPHONE>" + contactPhone + "</CONTACTPHONE>"
				+ " 		<CONTACTADDR>" + contactAddr + "</CONTACTADDR>" + " 		<OPERUSER>" + operUser + "</OPERUSER>" + " 		<OPERPASSWORD>" + operPassword + "</OPERPASSWORD>"
				+ " 		<PAYACCOUNT>" + payAccount + "</PAYACCOUNT>" + " 		<PAYPASSWORD>" + payPassword + "</PAYPASSWORD>" + " 		<PSAMCARDNO>" + psamCardNo + "</PSAMCARDNO>"
				+ " 		<TRACKTWO>" + trackTwo + "</TRACKTWO>" + " 		<TRACKTHREE>" + trackThree + "</TRACKTHREE>" + " 		<NETWORKNO>" + networkNo + "</NETWORKNO>" +" 		<OUTCUSTSIGN>" + outCustSign + "</OUTCUSTSIGN>"+ " 		<REMARK1>"
				+ remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		String pay2 = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>" + " 		<TXNAMOUNT>"
				+ txnAmount + "</TXNAMOUNT>" + " 		<TARGETACCOUNT>" + targetAccount + "</TARGETACCOUNT>" + " 		<TARGETACCOUNT>" + targetAccount + "</TARGETACCOUNT>"
				+ " 		<PAYACCOUNT>" + payAccount + "</PAYACCOUNT>" + " 		<PAYPASSWORD>" + "******" + "</PAYPASSWORD>" + " 		<PSAMCARDNO>" + psamCardNo + "</PSAMCARDNO>"
				+ " 		<TRACKTWO>" + "******" + "</TRACKTWO>" + " 		<TRACKTHREE>" + "******" + "</TRACKTHREE>" + " 		<NETWORKNO>" + networkNo + "</NETWORKNO>" + " 		<OUTCUSTSIGN>" + outCustSign + "</OUTCUSTSIGN>" + " 		<REMARK1>"
				+ remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INF06101:xmlStr====>" + getCommonXml(merId, channelCode, tmnNum, sign, cer, pay2));
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 全国多媒体付款接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06200(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06200XmlStr(request, keep);

		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 全国多媒体付款接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06200XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String acceptDate = request.getParameter("ACCEPTDATE");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String busObject = request.getParameter("BUSOBJECT");
		String bankAcct = request.getParameter("BANKACCT");
		String privateFlag = request.getParameter("PRIVATEFLAG");
		String cardFlag = request.getParameter("CARDFLAG");
		String payPassword = request.getParameter("PAYPASSWORD");
		String psamCardNo = request.getParameter("PSAMCARDNO");
		String trackTwo = request.getParameter("TRACKTWO");
		String trackThree = request.getParameter("TRACKTHREE");
		String networkNo = request.getParameter("NETWORKNO");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		// payPassword = ConvertPassword(staffCode, payPassword);
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>" + " 		<TXNAMOUNT>"
				+ txnAmount + "</TXNAMOUNT>" + " 		<BUSOBJECT>" + busObject + "</BUSOBJECT>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<PRIVATEFLAG>" + privateFlag
				+ "</PRIVATEFLAG>" + " 		<CARDFLAG>" + cardFlag + "</CARDFLAG>" + " 		<PAYPASSWORD>" + payPassword + "</PAYPASSWORD>" + " 		<PSAMCARDNO>" + psamCardNo
				+ "</PSAMCARDNO>" + " 		<TRACKTWO>" + trackTwo + "</TRACKTWO>" + " 		<TRACKTHREE>" + trackThree + "</TRACKTHREE>" + " 		<NETWORKNO>" + networkNo + "</NETWORKNO>"
				+ " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 东莞一卡通 签到
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06201(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06201XmlStr(request, keep);
		System.out.println("INF06201:xmlStr====>" + xmlStr);
		// DealProcessorService dp = (DealProcessorService) SpringContextHelper
		// .getDealProcessorServiceBean();
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 东莞一卡通 签到
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06201XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String tradeTime = request.getParameter("TRADETIME");
		String staffCode = request.getParameter("STAFFCODE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<REMARK1>"
				+ remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 东莞一卡通 签退
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06202(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06202XmlStr(request, keep);
		System.out.println("INF06202:xmlStr====>" + xmlStr);
		// DealProcessorService dp = (DealProcessorService) SpringContextHelper
		// .getDealProcessorServiceBean();
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 东莞一卡通 签退
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06202XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String tradeTime = request.getParameter("TRADETIME");
		String staffCode = request.getParameter("STAFFCODE");
		String settDate = request.getParameter("SETTDATE");
		String batchNo = request.getParameter("BATCHNO");
		String totalSvnum = request.getParameter("TOTALSVNUM");
		String totalSvamt = request.getParameter("TOTALSVAMT");
		String totalSaledep = request.getParameter("TOTALSALEDEP");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<SETTDATE>"
				+ settDate + "</SETTDATE>" + " 		<BATCHNO>" + batchNo + "</BATCHNO>" + " 		<TOTALSVNUM>" + totalSvnum + "</TOTALSVNUM>" + " 		<TOTALSVAMT>" + totalSvamt
				+ "</TOTALSVAMT>" + " 		<TOTALSALEDEP>" + totalSaledep + "</TOTALSALEDEP>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>"
				+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 东莞一卡通 卡操作
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06203(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06203XmlStr(request, keep);
		System.out.println("INF06203:xmlStr====>" + xmlStr);
		// DealProcessorService dp = (DealProcessorService) SpringContextHelper
		// .getDealProcessorServiceBean();
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 东莞一卡通 卡操作
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06203XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String tradeTime = request.getParameter("TRADETIME");
		String staffCode = request.getParameter("STAFFCODE");
		String ecardNo = request.getParameter("ECARDNO");
		String psamcardNo = request.getParameter("PSAMCARDNO");
		String passFlag = request.getParameter("PASSFLAG");
		String payPassword = request.getParameter("PAYPASSWORD");
		String orderSeq = request.getParameter("ORDERSEQ");
		String cardoprType = request.getParameter("CARDOPRTYPE");
		String cityCode = request.getParameter("CITYCODE");
		String cardId = request.getParameter("CARDID");
		String cardmknd = request.getParameter("CARDMKND");
		String cardsknd = request.getParameter("CARDSKND");

		String command = request.getParameter("COMMAND");
		String commandlen = request.getParameter("COMMANDLEN");

		String cardModel = request.getParameter("CARDMODEL");
		String transType = request.getParameter("TRANSTYPE");
		String deposit = request.getParameter("DEPOSIT");
		String origamt = request.getParameter("ORIGAMT");
		String cardvalDate = request.getParameter("CARDVALDATE");
		String srcbal = request.getParameter("SRCBAL");
		String cardseq = request.getParameter("CARDSEQ");
		String keyver = request.getParameter("KEYVER");
		String algind = request.getParameter("ALGIND");
		String cardRand = request.getParameter("CARDRAND");
		String mac1 = request.getParameter("MAC1");
		String divData = request.getParameter("DIVDATA");
		String batchno = request.getParameter("BATCHNO");

		/*
		 * String cardcnt = request.getParameter("CARDCNT"); String befbalance = request.getParameter("BEFBALANCE"); String cardverno = request.getParameter("CARDVERNO"); String
		 * txnamt = request.getParameter("TXNAMT"); String saleMode = request.getParameter("SALEMODE");//开卡冲正特有 String handingCharge =
		 * request.getParameter("HANDINGCHARGE");//充值冲正特有 String lastpossvseq = request.getParameter("LASTPOSSVSEQ");//充值冲正特有
		 */

		String keySet = request.getParameter("KEYSET");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<STAFFCODE>"
				+ staffCode + "</STAFFCODE>" + " 		<ECARDNO>" + ecardNo + "</ECARDNO>" + " 		<PSAMCARDNO>" + psamcardNo + "</PSAMCARDNO>" + " 		<PASSFLAG>" + passFlag
				+ "</PASSFLAG>" + " 		<CARDOPRTYPE>" + cardoprType + "</CARDOPRTYPE>" + " 		<CITYCODE>" + cityCode + "</CITYCODE>" + " 		<CARDID>" + cardId + "</CARDID>"
				+ " 		<CARDMKND>" + cardmknd + "</CARDMKND>" + " 		<CARDSKND>" + cardsknd + "</CARDSKND>" + " 		<PAYPASSWORD>" + payPassword + "</PAYPASSWORD>" + " 		<COMMAND>"
				+ command + "</COMMAND>" + " 		<COMMANDLEN>" + commandlen + "</COMMANDLEN>" + " 		<CARDMODEL>" + cardModel + "</CARDMODEL>" + " 		<TRANSTYPE>" + transType
				+ "</TRANSTYPE>" + " 		<DEPOSIT>" + deposit + "</DEPOSIT>" + " 		<ORIGAMT>" + origamt + "</ORIGAMT>" + " 		<CARDVALDATE>" + cardvalDate + "</CARDVALDATE>"
				+ " 		<SRCBAL>" + srcbal + "</SRCBAL>" + " 		<CARDSEQ>" + cardseq + "</CARDSEQ>" + " 		<KEYVER>" + keyver + "</KEYVER>" + " 		<ALGIND>" + algind + "</ALGIND>"
				+ " 		<CARDRAND>" + cardRand + "</CARDRAND>" + " 		<MAC1>" + mac1 + "</MAC1>" + " 		<DIVDATA>" + divData + "</DIVDATA>" + " 		<BATCHNO>" + batchno + "</BATCHNO>"
				/*
				 * +" 		<CARDVERNO>" + cardverno + "</CARDVERNO>" +" 		<BEFBALANCE>" + befbalance + "</BEFBALANCE>" +" 		<TXNAMT>" + txnamt + "</TXNAMT>" +" 		<CARDCNT>" + cardcnt
				 * + "</CARDCNT>" +" 		<SALEMODE>" + saleMode + "</SALEMODE>" +" 		<HANDINGCHARGE>" + handingCharge + "</HANDINGCHARGE>" +" 		<LASTPOSSVSEQ>" + lastpossvseq +
				 * "</LASTPOSSVSEQ>"
				 */
				+ " 		<KEYSET>" + keySet + "</KEYSET>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 东莞一卡通 开卡
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06204(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06204XmlStr(request, keep);
		System.out.println("INF06204:xmlStr====>" + xmlStr);
		// DealProcessorService dp = (DealProcessorService) SpringContextHelper
		// .getDealProcessorServiceBean();
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 东莞一卡通 开卡
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06204XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String operationType = request.getParameter("OPERATIONTYPE");
		String apTransSeq = request.getParameter("APTRANSSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String tradeTime = request.getParameter("TRADETIME");
		String staffCode = request.getParameter("STAFFCODE");
		String edcardId = request.getParameter("EDCARDID");
		String cardId = request.getParameter("CARDID");
		String cardcnt = request.getParameter("CARDCNT");
		String cardmknd = request.getParameter("CARDMKND");
		String cardsknd = request.getParameter("CARDSKND");
		String cardModel = request.getParameter("CARDMODEL");
		String saleMode = request.getParameter("SALEMODE");

		String transType = request.getParameter("TRANSTYPE");
		String systemNo = request.getParameter("SYSTEMNO");
		// String command = request.getParameter("COMMAND");
		// String commandlen = request.getParameter("COMMANDLEN");
		String deposit = request.getParameter("DEPOSIT");
		String befbalance = request.getParameter("BEFBALANCE");
		String txnamt = request.getParameter("TXNAMT");
		String cardvalDate = request.getParameter("CARDVALDATE");
		String cityCode = request.getParameter("CITYCODE");
		String cardverno = request.getParameter("CARDVERNO");
		String batchno = request.getParameter("BATCHNO");
		String authseq = request.getParameter("AUTHSEQ");
		String limitedauthseql = request.getParameter("LIMITEDAUTHSEQL");

		String tac = request.getParameter("TAC");
		String txnDate = request.getParameter("TXNDATE");
		String txnTime = request.getParameter("TXNTIME");

		String keySet = request.getParameter("KEYSET");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<APTRANSSEQ>" + apTransSeq + "</APTRANSSEQ>" + " 		<OPERATIONTYPE>" + operationType + "</OPERATIONTYPE>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<TRANSTYPE>" + transType + "</TRANSTYPE>" + " 	<SYSTEMNO>"
				+ systemNo + "</SYSTEMNO>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<EDCARDID>" + edcardId + "</EDCARDID>" + " 		<CARDID>" + cardId + "</CARDID>"
				+ " 		<CARDCNT>" + cardcnt + "</CARDCNT>" + " 		<CARDMKND>" + cardmknd + "</CARDMKND>" + " 		<CARDSKND>" + cardsknd + "</CARDSKND>" + " 		<CARDMODEL>" + cardModel
				+ "</CARDMODEL>" + " 		<SALEMODE>" + saleMode + "</SALEMODE>"
				/*
				 * +" 		<COMMAND>" + command + "</COMMAND>" +" 		<COMMANDLEN>" + commandlen + "</COMMANDLEN>"
				 */
				+ " 		<DEPOSIT>" + deposit + "</DEPOSIT>" + " 		<BEFBALANCE>" + befbalance + "</BEFBALANCE>" + " 		<TXNAMT>" + txnamt + "</TXNAMT>" + " 		<CARDVALDATE>"
				+ cardvalDate + "</CARDVALDATE>" + " 		<CITYCODE>" + cityCode + "</CITYCODE>" + " 		<CARDVERNO>" + cardverno + "</CARDVERNO>" + " 		<BATCHNO>" + batchno
				+ "</BATCHNO>" + " 		<AUTHSEQ>" + authseq + "</AUTHSEQ>" + " 		<LIMITEDAUTHSEQL>" + limitedauthseql + "</LIMITEDAUTHSEQL>" + " 		<KEYSET>" + keySet + "</KEYSET>"
				+ " 		<TAC>" + tac + "</TAC>" + " 		<TXNDATE>" + txnDate + "</TXNDATE>" + " 		<TXNTIME>" + txnTime + "</TXNTIME>" + " 		<REMARK1>" + remark1 + "</REMARK1>"
				+ " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 东莞一卡通 充值
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06205(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06205XmlStr(request, keep);
		System.out.println("INF06205:xmlStr====>" + xmlStr);
		// DealProcessorService dp = (DealProcessorService) SpringContextHelper
		// .getDealProcessorServiceBean();
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 东莞一卡通 充值
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06205XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String operationType = request.getParameter("OPERATIONTYPE");
		String apTransSeq = request.getParameter("APTRANSSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String tradeTime = request.getParameter("TRADETIME");
		String staffCode = request.getParameter("STAFFCODE");

		String transType = request.getParameter("TRANSTYPE");
		String systemNo = request.getParameter("SYSTEMNO");

		String cardId = request.getParameter("CARDID");
		String cardcnt = request.getParameter("CARDCNT");
		String cardmknd = request.getParameter("CARDMKND");
		String cardsknd = request.getParameter("CARDSKND");
		String cardModel = request.getParameter("CARDMODEL");
		String deposit = request.getParameter("DEPOSIT");
		String befbalance = request.getParameter("BEFBALANCE");
		String origamt = request.getParameter("ORIGAMT");
		String txnamt = request.getParameter("TXNAMT");
		String handingCharge = request.getParameter("HANDINGCHARGE");
		String cardvalDate = request.getParameter("CARDVALDATE");
		String cityCode = request.getParameter("CITYCODE");
		String cardverno = request.getParameter("CARDVERNO");
		String batchno = request.getParameter("BATCHNO");
		String authseq = request.getParameter("AUTHSEQ");
		String limitedauthseql = request.getParameter("LIMITEDAUTHSEQL");
		String lastpossvseq = request.getParameter("LASTPOSSVSEQ");

		String tac = request.getParameter("TAC");
		String txnDate = request.getParameter("TXNDATE");
		String txnTime = request.getParameter("TXNTIME");

		String keySet = request.getParameter("KEYSET");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<APTRANSSEQ>" + apTransSeq + "</APTRANSSEQ>" + " 		<OPERATIONTYPE>" + operationType + "</OPERATIONTYPE>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<TRANSTYPE>" + transType + "</TRANSTYPE>" + " 	<SYSTEMNO>"
				+ systemNo + "</SYSTEMNO>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<ORIGAMT>" + origamt + "</ORIGAMT>" + " 		<CARDID>" + cardId + "</CARDID>"
				+ " 		<CARDCNT>" + cardcnt + "</CARDCNT>" + " 		<CARDMKND>" + cardmknd + "</CARDMKND>" + " 		<CARDSKND>" + cardsknd + "</CARDSKND>" + " 		<CARDMODEL>" + cardModel
				+ "</CARDMODEL>" + " 		<HANDINGCHARGE>" + handingCharge + "</HANDINGCHARGE>" + " 		<LASTPOSSVSEQ>" + lastpossvseq + "</LASTPOSSVSEQ>" + " 		<DEPOSIT>" + deposit
				+ "</DEPOSIT>" + " 		<BEFBALANCE>" + befbalance + "</BEFBALANCE>" + " 		<TXNAMT>" + txnamt + "</TXNAMT>" + " 		<CARDVALDATE>" + cardvalDate + "</CARDVALDATE>"
				+ " 		<CITYCODE>" + cityCode + "</CITYCODE>" + " 		<CARDVERNO>" + cardverno + "</CARDVERNO>" + " 		<BATCHNO>" + batchno + "</BATCHNO>" + " 		<AUTHSEQ>" + authseq
				+ "</AUTHSEQ>" + " 		<LIMITEDAUTHSEQL>" + limitedauthseql + "</LIMITEDAUTHSEQL>" + " 		<KEYSET>" + keySet + "</KEYSET>" + " 		<TAC>" + tac + "</TAC>"
				+ " 		<TXNDATE>" + txnDate + "</TXNDATE>" + " 		<TXNTIME>" + txnTime + "</TXNTIME>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2
				+ "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 东莞一卡通 开卡冲正
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06206(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06206XmlStr(request, keep);
		System.out.println("INF06206:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 东莞一卡通 开卡冲正
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06206XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String tradeTime = request.getParameter("TRADETIME");
		String staffCode = request.getParameter("STAFFCODE");

		String transseq = request.getParameter("TRANSSEQ");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<STAFFCODE>"
				+ staffCode + "</STAFFCODE>" + " 		<TRANSSEQ>" + transseq + "</TRANSSEQ>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>"
				+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 东莞一卡通 充值冲正
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06207(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06207XmlStr(request, keep);
		System.out.println("INF06207:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 东莞一卡通 充值冲正
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06207XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String tradeTime = request.getParameter("TRADETIME");
		String staffCode = request.getParameter("STAFFCODE");

		String transseq = request.getParameter("TRANSSEQ");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<STAFFCODE>"
				+ staffCode + "</STAFFCODE>" + " 		<TRANSSEQ>" + transseq + "</TRANSSEQ>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>"
				+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 车船税账单缴费
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06104(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06104XmlStr(request, keep);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 车船税账单缴费
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06104XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");

		String acceptDate = request.getParameter("ACCEPTDATE");
		String systemNo = request.getParameter("SYSTEMNO");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String plateNo = request.getParameter("PLATENO");
		String payType = request.getParameter("PAYTYPE");

		String accName = request.getParameter("ACCNAME");
		String bankAcct = request.getParameter("BANKACCT");
		String privateFlag = request.getParameter("PRIVATEFLAG");
		String cardFlag = request.getParameter("CARDFLAG");

		String creditValidTime = request.getParameter("CREDITVALIDTIME");
		String creditValidCode = request.getParameter("CREDITVALIDCODE");
		String payPassword = request.getParameter("PAYPASSWORD");
		String psamCardNo = request.getParameter("PSAMCARDNO");
		String eCardNo = request.getParameter("ECARDNO");
		String passFlag = request.getParameter("PASSFLAG");
		String trackTwo = request.getParameter("TRACKTWO");
		String trackThree = request.getParameter("TRACKTHREE");
		String networkNo = request.getParameter("NETWORKNO");
		String contactPhone = request.getParameter("CONTACTPHONE");
		String contactAddr = request.getParameter("CONTACTADDR");
		String operUser = request.getParameter("OPERUSER");
		String operPassword = request.getParameter("OPERPASSWORD");
		if (!ChannelCode.IPOS_CHANELCODE.equals(channelCode)) {

			operPassword = PasswordUtil.ConvertPassword(operUser, operPassword);
		}
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>" + " 		<SYSTEMNO>"
				+ systemNo + "</SYSTEMNO>" + " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " 		<PLATENO>" + plateNo + "</PLATENO>" + " 		<ACCNAME>" + accName + "</ACCNAME>"
				+ " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<PRIVATEFLAG>" + privateFlag + "</PRIVATEFLAG>" + " 		<CARDFLAG>" + cardFlag + "</CARDFLAG>" + " 		<PAYTYPE>"
				+ payType + "</PAYTYPE>" + " 		<CREDITVALIDTIME>" + creditValidTime + "</CREDITVALIDTIME>" + " 		<CREDITVALIDCODE>" + creditValidCode + "</CREDITVALIDCODE>"
				+ " 		<PSAMCARDNO>" + psamCardNo + "</PSAMCARDNO>" + " 		<ECARDNO>" + eCardNo + "</ECARDNO>" + " 		<PASSFLAG>" + passFlag + "</PASSFLAG>" + " 		<PAYPASSWORD>"
				+ payPassword + "</PAYPASSWORD>" + " 		<TRACKTWO>" + trackTwo + "</TRACKTWO>" + " 		<TRACKTHREE>" + trackThree + "</TRACKTHREE>" + " 		<NETWORKNO>" + networkNo
				+ "</NETWORKNO>" + " 		<CONTACTPHONE>" + contactPhone + "</CONTACTPHONE>" + " 		<CONTACTADDR>" + contactAddr + "</CONTACTADDR>" + " 		<OPERUSER>" + operUser
				+ "</OPERUSER>" + " 		<OPERPASSWORD>" + operPassword + "</OPERPASSWORD>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>"
				+ " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		String pay2 = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>" + " 		<SYSTEMNO>"
				+ systemNo + "</SYSTEMNO>" + " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " 		<PLATENO>" + plateNo + "</PLATENO>" + " 		<ACCNAME>" + accName + "</ACCNAME>"
				+ " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<PRIVATEFLAG>" + privateFlag + "</PRIVATEFLAG>" + " 		<CARDFLAG>" + cardFlag + "</CARDFLAG>" + " 		<PAYTYPE>"
				+ payType + "</PAYTYPE>" + " 		<CREDITVALIDTIME>" + creditValidTime + "</CREDITVALIDTIME>" + " 		<CREDITVALIDCODE>" + creditValidCode + "</CREDITVALIDCODE>"
				+ " 		<PSAMCARDNO>" + psamCardNo + "</PSAMCARDNO>" + " 		<ECARDNO>" + eCardNo + "</ECARDNO>" + " 		<PAYPASSWORD>" + "******" + "</PAYPASSWORD>" + " 		<TRACKTWO>"
				+ "******" + "</TRACKTWO>" + " 		<TRACKTHREE>" + "******" + "</TRACKTHREE>" + " 		<NETWORKNO>" + networkNo + "</NETWORKNO>" + " 		<CONTACTPHONE>" + contactPhone
				+ "</CONTACTPHONE>" + " 		<CONTACTADDR>" + contactAddr + "</CONTACTADDR>" + " 		<OPERUSER>" + operUser + "</OPERUSER>" + " 		<OPERPASSWORD>" + "******"
				+ "</OPERPASSWORD>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INF06104:xmlStr====>" + getCommonXml(merId, channelCode, tmnNum, sign, cer, pay2));

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 水电煤账单缴费接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06102(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06102XmlStr(request, keep);
		// DealProcessorService dp = (DealProcessorService)
		// SpringContextHelper.getDealProcessorServiceBean();
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 水电煤账单缴费接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06102XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		// String outTmnNumNo = request.getParameter("OUTTMNNUMNO");
		String acceptDate = request.getParameter("ACCEPTDATE");
		String systemNo = request.getParameter("SYSTEMNO");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String cashType = request.getParameter("CASHTYPE");
		String cashOrder = request.getParameter("CASHORDER");
		String cashNumber = request.getParameter("CASHNUMBER");
		String accName = request.getParameter("ACCNAME");
		String bankAcct = request.getParameter("BANKACCT");
		String privateFlag = request.getParameter("PRIVATEFLAG");
		String cardFlag = request.getParameter("CARDFLAG");
		String payType = request.getParameter("PAYTYPE");
		String creditValidTime = request.getParameter("CREDITVALIDTIME");
		String creditValidCode = request.getParameter("CREDITVALIDCODE");
		String payPassword = request.getParameter("PAYPASSWORD");
		String psamCardNo = request.getParameter("PSAMCARDNO");

		String eCardNo = request.getParameter("ECARDNO");
		String passFlag = request.getParameter("PASSFLAG");

		String trackTwo = request.getParameter("TRACKTWO");
		String trackThree = request.getParameter("TRACKTHREE");
		String networkNo = request.getParameter("NETWORKNO");
		String contactPhone = request.getParameter("CONTACTPHONE");
		String contactAddr = request.getParameter("CONTACTADDR");
		String operUser = request.getParameter("OPERUSER");
		String operPassword = request.getParameter("OPERPASSWORD");
		if (!ChannelCode.IPOS_CHANELCODE.equals(channelCode)) {

			operPassword = PasswordUtil.ConvertPassword(operUser, operPassword);
		}
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\""
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
				+ " 		<TMNNUMNO>"
				+ tmnNumNo
				+ "</TMNNUMNO>"
				// + " 		<OUTTMNNUMNO>"
				// + outTmnNumNo
				// + "</OUTTMNNUMNO>"
				+ " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>" + " 		<SYSTEMNO>" + systemNo + "</SYSTEMNO>" + " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " 		<CASHTYPE>"
				+ cashType + "</CASHTYPE>" + " 		<CASHORDER>" + cashOrder + "</CASHORDER>" + " 		<CASHNUMBER>" + cashNumber + "</CASHNUMBER>" + " 		<ACCNAME>" + accName
				+ "</ACCNAME>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<PRIVATEFLAG>" + privateFlag + "</PRIVATEFLAG>" + " 		<CARDFLAG>" + cardFlag + "</CARDFLAG>"
				+ " 		<PAYTYPE>" + payType + "</PAYTYPE>" + " 		<CREDITVALIDTIME>" + creditValidTime + "</CREDITVALIDTIME>" + " 		<CREDITVALIDCODE>" + creditValidCode
				+ "</CREDITVALIDCODE>" + " 		<PSAMCARDNO>" + psamCardNo + "</PSAMCARDNO>" + " 		<ECARDNO>" + eCardNo + "</ECARDNO>" + " 		<PASSFLAG>" + passFlag + "</PASSFLAG>"
				+ " 		<PAYPASSWORD>" + payPassword + "</PAYPASSWORD>" + " 		<TRACKTWO>" + trackTwo + "</TRACKTWO>" + " 		<TRACKTHREE>" + trackThree + "</TRACKTHREE>"
				+ " 		<NETWORKNO>" + networkNo + "</NETWORKNO>" + " 		<CONTACTPHONE>" + contactPhone + "</CONTACTPHONE>" + " 		<CONTACTADDR>" + contactAddr + "</CONTACTADDR>"
				+ " 		<OPERUSER>" + operUser + "</OPERUSER>" + " 		<OPERPASSWORD>" + operPassword + "</OPERPASSWORD>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>"
				+ remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		String pay2 = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\""
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
				+ " 		<TMNNUMNO>"
				+ tmnNumNo
				+ "</TMNNUMNO>"
				// + " 		<OUTTMNNUMNO>"
				// + outTmnNumNo
				// + "</OUTTMNNUMNO>"
				+ " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>" + " 		<SYSTEMNO>" + systemNo + "</SYSTEMNO>" + " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " 		<CASHTYPE>"
				+ cashType + "</CASHTYPE>" + " 		<CASHORDER>" + cashOrder + "</CASHORDER>" + " 		<ACCNAME>" + accName + "</ACCNAME>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>"
				+ " 		<PRIVATEFLAG>" + privateFlag + "</PRIVATEFLAG>" + " 		<CARDFLAG>" + cardFlag + "</CARDFLAG>" + " 		<PAYTYPE>" + payType + "</PAYTYPE>"
				+ " 		<CREDITVALIDTIME>" + creditValidTime + "</CREDITVALIDTIME>" + " 		<CREDITVALIDCODE>" + creditValidCode + "</CREDITVALIDCODE>" + " 		<PSAMCARDNO>"
				+ psamCardNo + "</PSAMCARDNO>" + " 		<ECARDNO>" + eCardNo + "</ECARDNO>" + " 		<PASSFLAG>" + passFlag + "</PASSFLAG>" + " 		<PAYPASSWORD>" + "******"
				+ "</PAYPASSWORD>" + " 		<TRACKTWO>" + "******" + "</TRACKTWO>" + " 		<TRACKTHREE>" + "******" + "</TRACKTHREE>" + " 		<NETWORKNO>" + networkNo + "</NETWORKNO>"
				+ " 		<CONTACTPHONE>" + contactPhone + "</CONTACTPHONE>" + " 		<CONTACTADDR>" + contactAddr + "</CONTACTADDR>" + " 		<OPERUSER>" + operUser + "</OPERUSER>"
				+ " 		<OPERPASSWORD>" + "******" + "</OPERPASSWORD>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(operUser, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INF06102:xmlStr====>" + getCommonXml(merId, channelCode, tmnNum, sign, cer, pay2));

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 个人账户充值接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06103(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06103XmlStr(request, keep);

		// DealProcessorService dp = (DealProcessorService)
		// SpringContextHelper.getDealProcessorServiceBean();
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 个人账户充值接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06103XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderSeq = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String outTmnNumNo = request.getParameter("OUTTMNNUMNO");
		String acctCode = request.getParameter("ACCTCODE");
		String acceptDate = request.getParameter("ACCEPTDATE");
		String systemNO = request.getParameter("SYSTEMNO");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String accName = request.getParameter("ACCNAME");
		String bankAcct = request.getParameter("BANKACCT");
		String payPassword = request.getParameter("PAYPASSWORD");
		String privateFlag = request.getParameter("PRIVATEFLAG");
		String cardFlag = request.getParameter("CARDFLAG");
		String payType = request.getParameter("PAYTYPE");
		String creditValidTime = request.getParameter("CREDITVALIDTIME");
		String creditValidCode = request.getParameter("CREDITVALIDCODE");
		String psamCardNo = request.getParameter("PSAMCARDNO");
		String trackTwo = request.getParameter("TRACKTWO");
		String trackThree = request.getParameter("TRACKTHREE");
		String networkNo = request.getParameter("NETWORKNO");
		String contactPhone = request.getParameter("CONTACTPHONE");
		String contactAddr = request.getParameter("CONTACTADDR");
		String operUser = request.getParameter("OPERUSER");
		String operPassword = request.getParameter("OPERPASSWORD");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			operPassword = ConvertPassword(operUser, operPassword);
		}

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<OUTTMNNUMNO>" + outTmnNumNo + "</OUTTMNNUMNO>" + " 		<ACCEPTDATE>"
				+ acceptDate + "</ACCEPTDATE>" + " 		<ACCTCODE>" + acctCode + "</ACCTCODE>" + " 		<SYSTEMNO>" + systemNO + "</SYSTEMNO>" + " 		<TXNAMOUNT>" + txnAmount
				+ "</TXNAMOUNT>" + " 		<ACCNAME>" + accName + "</ACCNAME>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<PRIVATEFLAG>" + privateFlag + "</PRIVATEFLAG>"
				+ " 		<CARDFLAG>" + cardFlag + "</CARDFLAG>" + " 		<PAYTYPE>" + payType + "</PAYTYPE>" + " 		<CREDITVALIDTIME>" + creditValidTime + "</CREDITVALIDTIME>"
				+ " 		<CREDITVALIDCODE>" + creditValidCode + "</CREDITVALIDCODE>" + " 		<PSAMCARDNO>" + psamCardNo + "</PSAMCARDNO>" + " 		<PAYPASSWORD>" + payPassword
				+ "</PAYPASSWORD>" + " 		<TRACKTWO>" + trackTwo + "</TRACKTWO>" + " 		<TRACKTHREE>" + trackThree + "</TRACKTHREE>" + " 		<NETWORKNO>" + networkNo + "</NETWORKNO>"
				+ " 		<CONTACTPHONE>" + contactPhone + "</CONTACTPHONE>" + " 		<CONTACTADDR>" + contactAddr + "</CONTACTADDR>" + " 		<OPERUSER>" + operUser + "</OPERUSER>"
				+ " 		<OPERPASSWORD>" + operPassword + "</OPERPASSWORD>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";

		String pay2 = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<OUTTMNNUMNO>" + outTmnNumNo + "</OUTTMNNUMNO>" + " 		<ACCEPTDATE>"
				+ acceptDate + "</ACCEPTDATE>" + " 		<ACCTCODE>" + acctCode + "</ACCTCODE>" + " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " 		<ACCNAME>" + accName
				+ "</ACCNAME>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<PRIVATEFLAG>" + privateFlag + "</PRIVATEFLAG>" + " 		<CARDFLAG>" + cardFlag + "</CARDFLAG>"
				+ " 		<PAYTYPE>" + payType + "</PAYTYPE>" + " 		<CREDITVALIDTIME>" + creditValidTime + "</CREDITVALIDTIME>" + " 		<CREDITVALIDCODE>" + creditValidCode
				+ "</CREDITVALIDCODE>" + " 		<PSAMCARDNO>" + psamCardNo + "</PSAMCARDNO>" + " 		<PAYPASSWORD>" + "******" + "</PAYPASSWORD>" + " 		<TRACKTWO>" + "******"
				+ "</TRACKTWO>" + " 		<TRACKTHREE>" + "******" + "</TRACKTHREE>" + " 		<NETWORKNO>" + networkNo + "</NETWORKNO>" + " 		<CONTACTPHONE>" + contactPhone
				+ "</CONTACTPHONE>" + " 		<CONTACTADDR>" + contactAddr + "</CONTACTADDR>" + " 		<OPERUSER>" + operUser + "</OPERUSER>" + " 		<OPERPASSWORD>" + "******"
				+ "</OPERPASSWORD>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(operUser, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INF06102:xmlStr====>" + getCommonXml(merId, channelCode, tmnNum, sign, cer, pay2));

		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 地市查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06002(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06002XmlStr(request, keep);
		System.out.println("INF06002:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);

	}

	/**
	 * 地市查询接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06002XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String tmnNumNo = request.getParameter("TMNNUMNO");

		String accepTareaCode = request.getParameter("ACCEPTAREACODE");
		String acceptDate = request.getParameter("ACCEPTDATE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<ACCEPTAREACODE>" + accepTareaCode + "</ACCEPTAREACODE>"
				+ " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>"
				+ " </PayPlatRequestParameter>";
		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 充值账户校验接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06004(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06004XmlStr(request, keep);
		System.out.println("INF06004:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");

		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 充值账户校验接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06004XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");

		String staffCode = request.getParameter("STAFFCODE");

		String verify = request.getParameter("VERIFY");

		String tmnNumNo = request.getParameter("TMNNUMNO");
		// String outTmnNumNo = request.getParameter("OUTTMNNUMNO");
		String accepTareaCode = request.getParameter("ACCEPTAREACODE");
		String acctCode = request.getParameter("ACCTCODE");
		String acceptDate = request.getParameter("ACCEPTDATE");
		String acceptTime = request.getParameter("ACCEPTTIME");
		String reamount = request.getParameter("REAMOUNT");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<VERIFY>"
				+ verify
				+ "</VERIFY>"
				+ " 		<TMNNUMNO>"
				+ tmnNumNo
				+ "</TMNNUMNO>"
				// + " 		<OUTTMNNUMNO>"
				// + outTmnNumNo
				// + "</OUTTMNNUMNO>"
				+ " 		<ACCEPTAREACODE>" + accepTareaCode + "</ACCEPTAREACODE>" + " 		<ACCTCODE>" + acctCode + "</ACCTCODE>" + " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>"
				+ " 		<ACCEPTTIME>" + acceptTime + "</ACCEPTTIME>" + " 		<REAMOUNT>" + reamount + "</REAMOUNT>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>"
				+ remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 个人账户余额查询口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf06005(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf06005XmlStr(request, keep);
		System.out.println("INF06005:xmlStr====>" + xmlStr);
		DealProcessorService dp = (DealProcessorService) SpringContextHelper.getDealProcessorServiceBean();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		String channelCode = request.getParameter("CHANNELCODE");

		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {

			return dp.dispatchCommandEXT(wbSvrCode + "|123456", xmlStr);
		}
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 个人账户余额查询口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf06005XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");

		String tmnNumNo = request.getParameter("TMNNUMNO");
		String accepTareaCode = request.getParameter("ACCEPTAREACODE");
		String acctCode = request.getParameter("ACCTCODE");
		String searchDate = request.getParameter("SEARCHDATE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<ACCEPTAREACODE>" + accepTareaCode + "</ACCEPTAREACODE>"
				+ " 		<ACCTCODE>" + acctCode + "</ACCTCODE>" + " 		<SEARCHDATE>" + searchDate + "</SEARCHDATE>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>"
				+ remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(staffCode, tokenValidTime);

				sign = MD5.MD5Encode(pay + "<KEY>" + md5Key + "</KEY>");
				cer = "";
			} else {
				sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
				cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 交易退款接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02014(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02014XmlStr(request, keep);
		log.info("INF02014:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 交易退款接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02014XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String objectCode = request.getParameter("OBJECTCODE");
		String orderSeq = request.getParameter("ORDERSEQ");
		String apptransSeq = request.getParameter("APPTRANSSEQ");
		String txnAmount = request.getParameter("TXNAMOUNT");
		String tradeTime = request.getParameter("TRADETIME");

		String keepNo = request.getParameter("KEEPNO");

		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<OBJECTCODE>" + objectCode + "</OBJECTCODE>" + " 		<KEEPNO>" + keepNo + "</KEEPNO>" + " 		<ORDERSEQ>" + orderSeq + "</ORDERSEQ>" + " 		<APPTRANSSEQ>"
				+ apptransSeq + "</APPTRANSSEQ>" + " 		<TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " 		<TRADETIME>" + tradeTime + "</TRADETIME>" + " 		<REMARK1>" + remark1
				+ "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 账户绑卡验证接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02024(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02024XmlStr(request, keep);
		log.info("INF02024:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 账户绑卡验证接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02024XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String payPassword = request.getParameter("PAYPASSWORD");

		// payPassword = ConvertPassword(staffCode, payPassword);

		String verifyType = request.getParameter("VERIFYTYPE");
		String bankCode = request.getParameter("BANKCODE");
		String openBank = request.getParameter("OPENBANK");
		String areaCode = request.getParameter("AREACODE");
		String bankAcct = request.getParameter("BANKACCT");
		String transAccName = request.getParameter("TRANSACCNAME");
		String cerNo = request.getParameter("CERNO");
		String phone = request.getParameter("PHONE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<CUSTCODE>"
				+ custCode + "</CUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<PAYPASSWORD>" + payPassword + "</PAYPASSWORD>" + " 		<VERIFYTYPE>" + verifyType
				+ "</VERIFYTYPE>" + " 		<BANKCODE>" + bankCode + "</BANKCODE>" + " 		<OPENBANK>" + openBank + "</OPENBANK>" + " 		<AREACODE>" + areaCode + "</AREACODE>"
				+ " 		<BANKACCT>" + bankAcct + "</BANKACCT>" + " 		<TRANSACCNAME>" + transAccName + "</TRANSACCNAME>" + " 		<CERNO>" + cerNo + "</CERNO>" + " 		<PHONE>" + phone
				+ "</PHONE>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 账户绑卡通知接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02030(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02030XmlStr(request, keep);
		log.info("INF02030:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 账户绑卡通知接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02030XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String operType = request.getParameter("OPERTYPE");

		String bankCode = request.getParameter("BANKCODE");
		String openBank = request.getParameter("OPENBANK");
		String areaCode = request.getParameter("AREACODE");
		String bankAcct = request.getParameter("BANKACCT");
		String transAccName = request.getParameter("TRANSACCNAME");
		String cerNo = request.getParameter("CERNO");
		String phone = request.getParameter("PHONE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<CUSTCODE>"
				+ custCode + "</CUSTCODE>" + " 		<STAFFCODE>" + staffCode + "</STAFFCODE>" + " 		<OPERTYPE>" + operType + "</OPERTYPE>" + " 		<BANKCODE>" + bankCode
				+ "</BANKCODE>" + " 		<OPENBANK>" + openBank + "</OPENBANK>" + " 		<AREACODE>" + areaCode + "</AREACODE>" + " 		<BANKACCT>" + bankAcct + "</BANKACCT>"
				+ " 		<TRANSACCNAME>" + transAccName + "</TRANSACCNAME>" + " 		<CERNO>" + cerNo + "</CERNO>" + " 		<PHONE>" + phone + "</PHONE>" + " 		<REMARK1>" + remark1
				+ "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 手机号码验证接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf04001(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf04001XmlStr(request, keep);
		System.out.println("INF04001:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 手机号码验证接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf04001XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String phone = request.getParameter("MOBILE");
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<MOBILE>"
				+ phone + "</MOBILE>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			// sign = "";
			// cer = "";
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 语音操作密码验证接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf04002(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf04002XmlStr(request, keep);
		System.out.println("INF04002:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommandIPOS(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 语音操作密码验证接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf04002XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String CUSTCODE = request.getParameter("CUSTCODE");
		String PASSWORDTPYE = request.getParameter("PASSWORDTPYE");
		String PASSWORD = request.getParameter("PASSWORD");
		String STAFFID = request.getParameter("STAFFID");
		String passwordJm = ConvertPassword(STAFFID, PASSWORD);
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<CUSTCODE>"
				+ CUSTCODE + "</CUSTCODE>" + " 		<PASSWORDTPYE>" + PASSWORDTPYE + "</PASSWORDTPYE>" + " 		<PASSWORD>" + passwordJm + "</PASSWORD>" + " 		<STAFFID>" + STAFFID
				+ "</STAFFID>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			// sign = "";
			// cer = "";
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 广州后付费查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02025(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02025XmlStr(request, keep);
		System.out.println("INF02025:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 广州电信后付费查询接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02025XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String acceptDate = request.getParameter("ACCEPTDATE");
		String payType = request.getParameter("PAYTYPE");
		String phone = request.getParameter("PHONE");
		String busType = request.getParameter("BUSTYPE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<CUSTCODE>"
				+ custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>" + " 		<PAYTYPE>" + payType
				+ "</PAYTYPE>" + " 		<PHONE>" + phone + "</PHONE>" + " 		<BUSTYPE>" + busType + "</BUSTYPE>" + " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2
				+ "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 广州后付费交易接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02026(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02026XmlStr(request, keep);
		System.out.println("INF02026:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 广州后付费交易接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02026XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String payType = request.getParameter("PAYTYPE");
		String userName = request.getParameter("USERNAME");
		String systemo = request.getParameter("SYSTEMNO");
		String txnamount = request.getParameter("TXNAMOUNT");
		String contactPhone = request.getParameter("CONTACTPHONE");
		String contactAddr = request.getParameter("CONTACTADDR");
		String operUser = request.getParameter("OPERUSER");
		String operPassword = request.getParameter("OPERPASSWORD");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<CUSTCODE>"
				+ custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<PAYTYPE>" + payType + "</PAYTYPE>" + " 		<USERNAME>" + userName + "</USERNAME>"
				+ " 		<SYSTEMNO>" + systemo + "</SYSTEMNO>" + " 		<TXNAMOUNT>" + txnamount + "</TXNAMOUNT>" + " 		<CONTACTPHONE>" + contactPhone + "</CONTACTPHONE>"
				+ " 		<CONTACTADDR>" + contactAddr + "</CONTACTADDR>" + " 		<OPERUSER>" + operUser + "</OPERUSER>" + " 		<OPERPASSWORD>" + operPassword + "</OPERPASSWORD>"
				+ " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 有线电视查询接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02027(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02027XmlStr(request, keep);
		System.out.println("INF02027:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 有线电视查询接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02027XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String acceptDate = request.getParameter("ACCEPTDATE");
		String payType = request.getParameter("PAYTYPE");
		String busType = request.getParameter("BUSTYPE");
		String queryType = request.getParameter("QUERYTYPE");
		String queryValue = request.getParameter("QUERYVALUE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<CUSTCODE>"
				+ custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>" + " 		<PAYTYPE>" + payType
				+ "</PAYTYPE>" + " 		<BUSTYPE>" + busType + "</BUSTYPE>" + " 		<QUERYTYPE>" + queryType + "</QUERYTYPE>" + " 		<QUERYVALUE>" + queryValue + "</QUERYVALUE>"
				+ " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 有线电视交易接口
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf02028(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf02028XmlStr(request, keep);
		System.out.println("INF02028:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	/**
	 * 有线电视交易接口
	 * 
	 * @param request
	 * @param keep
	 * @return
	 */
	private String getInf02028XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERNO");
		String custCode = request.getParameter("CUSTCODE");
		String tmnNumNo = request.getParameter("TMNNUMNO");
		String payType = request.getParameter("PAYTYPE");
		String busType = request.getParameter("BUSTYPE");
		String systemo = request.getParameter("SYSTEMNO");
		String txnamount = request.getParameter("TXNAMOUNT");
		String contactPhone = request.getParameter("CONTACTPHONE");
		String contactAddr = request.getParameter("CONTACTADDR");
		String operUser = request.getParameter("OPERUSER");
		String operPassword = request.getParameter("OPERPASSWORD");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<CUSTCODE>"
				+ custCode + "</CUSTCODE>" + " 		<TMNNUMNO>" + tmnNumNo + "</TMNNUMNO>" + " 		<PAYTYPE>" + payType + "</PAYTYPE>" + " 		<BUSTYPE>" + busType + "</BUSTYPE>"
				+ " 		<SYSTEMNO>" + systemo + "</SYSTEMNO>" + " 		<TXNAMOUNT>" + txnamount + "</TXNAMOUNT>" + " 		<CONTACTPHONE>" + contactPhone + "</CONTACTPHONE>"
				+ " 		<CONTACTADDR>" + contactAddr + "</CONTACTADDR>" + " 		<OPERUSER>" + operUser + "</OPERUSER>" + " 		<OPERPASSWORD>" + operPassword + "</OPERPASSWORD>"
				+ " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		<REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 火车票车次查询
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf12035(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf12035XmlStr(request, keep);
		System.out.println("INF12035:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf12035XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String orderNo = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String acceptAreaCode = request.getParameter("ACCEPTAREACODE");
		String fromStation = request.getParameter("FROMSTATION");
		String toStation = request.getParameter("TOSTATION");
		String date = request.getParameter("DATE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 		<ORDERNO>" + orderNo + "</ORDERNO>" + " 		<CUSTCODE>"
				+ custCode + "</CUSTCODE>" + " 		<ACCEPTAREACODE>" + acceptAreaCode + "</ACCEPTAREACODE>" + " 		<FROMSTATION>" + fromStation + "</FROMSTATION>" + "   <TOSTATION>"
				+ toStation + "</TOSTATION>" + " 		<DATE>" + date + "</DATE>"

				+ " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		" + "        <REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 火车票余座查询
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf12036(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf12036XmlStr(request, keep);
		System.out.println("INF12035:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf12036XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		// String orderNo = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		// String staffCode = request.getParameter("STAFFCODE");

		String queryId = request.getParameter("QUERYID");
		String trainId = request.getParameter("TRAINID");
		String acceptDate = request.getParameter("ACCEPTDATE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss")
				+ "\"/>"
				+ " 	<PARAMETERS>"
				// + " 		<ORDERNO>"
				// + orderNo
				// + "</ORDERNO>"
				+ " 		<CUSTCODE>" + custCode + "</CUSTCODE>" + " 		<QUERYID>" + queryId + "</QUERYID>" + " 		<TRAINID>" + trainId + "</TRAINID>" + "   <ACCEPTDATE>" + acceptDate
				+ "</ACCEPTDATE>"

				+ " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		" + "        <REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}

	/**
	 * 火车票订票查询
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvokeException
	 * @throws JSONException
	 */
	public String inf12037(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf12037XmlStr(request, keep);
		System.out.println("INF12035:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf12037XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		// String orderNo = request.getParameter("ORDERSEQ");
		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");

		String systemNo = request.getParameter("SYSTEMNO");
		String keepNo = request.getParameter("KEEPNO");
		String acceptDate = request.getParameter("ACCEPTDATE");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 	<CUSTCODE>" + custCode + "</CUSTCODE>"
				+ " 		<SYSTEMNO>" + systemNo + "</SYSTEMNO>" + " 		<KEEPNO>" + keepNo + "</KEEPNO>" + "   <ACCEPTDATE>" + acceptDate + "</ACCEPTDATE>"

				+ " 		<REMARK1>" + remark1 + "</REMARK1>" + " 		" + "        <REMARK2>" + remark2 + "</REMARK2>" + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	
	public String inf13001(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf3001XmlStr(request, keep);
		System.out.println("INF13001:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf3001XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 	<CUSTCODE>" + getString(custCode) + "</CUSTCODE>"
				+ " 		<STAFFCODE>" + getString(staffCode) + "</STAFFCODE></PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	public String inf13002(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf3002XmlStr(request, keep);
		System.out.println("INF13002:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf3002XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 	<CUSTCODE>" + getString(custCode) + "</CUSTCODE>"
				+ " 		<STAFFCODE>" + getString(staffCode) + "</STAFFCODE></PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	public String inf13003(HttpServletRequest request) throws ServiceInvokeException {
		String tmnNum = request.getParameter("TMNNUM");
		String keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		String xmlStr = getInf3003XmlStr(request, keep);
		System.out.println("INF13003:xmlStr====>" + xmlStr);
		DealProcessorService dp = new DealProcessorServiceImpl();
		String wbSvrCode = getRequestParam(request, "WEBSVRCODE");
		return dp.dispatchCommand(wbSvrCode + "|123456", xmlStr);
	}

	private String getInf3003XmlStr(HttpServletRequest request, String keep) {
		String merId = request.getParameter("MERID");
		String channelCode = request.getParameter("CHANNELCODE");
		String tmnNum = request.getParameter("TMNNUM");
		String sign = request.getParameter("SIGN");
		String cer = request.getParameter("CER");

		String custCode = request.getParameter("CUSTCODE");
		String staffCode = request.getParameter("STAFFCODE");
		String rankListType = request.getParameter("RANKLISTTYPE");
		String searchMonth = request.getParameter("SEARCHMONTH");

		String pay = "<PayPlatRequestParameter>" + " 	<CTRL-INFO " + " WEBSVRNAME=\"test\" " + " WEBSVRCODE=\"test\" " + " APPFROM=\"1234\"" + " 		KEEP=\"" + keep + "\""
				+ "     REQUESTTIME=\"" + DateUtil.formatDate(new Date(), "yyyyMMddhhmmss") + "\"/>" + " 	<PARAMETERS>" + " 	<CUSTCODE>" + getString(custCode) + "</CUSTCODE>"
				+ " 		<STAFFCODE>" + getString(staffCode) + "</STAFFCODE>"
				+ " 		<RANKLISTTYPE>" + getString(rankListType) + "</RANKLISTTYPE>"
				+ " 		<SEARCHMONTH>" + getString(searchMonth) + "</SEARCHMONTH>"+"</PARAMETERS>" + " </PayPlatRequestParameter>";
		try {
			sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay.getBytes("UTF-8")));
			cer = NETCAPKI.getX509CertificateString(NETCAPKI.getSrvX509Certificate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCommonXml(merId, channelCode, tmnNum, sign, cer, pay);
	}
	
	private String getString(String str){
		if(str==null){
			return "";
		}
		return str;
	}

}