package framework.servlet;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import websvc.DealProcessorService;

import common.algorithm.MD5;
import common.dao.TPayWapDao;
import common.entity.PayWap;
import common.utils.Charset;
import common.utils.CurrencyTool;
import common.utils.SpringContextHelper;
import common.utils.URLConnectionTool;
import framework.config.PayWapConfig;

public class PayWapExecuteServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PayWap paywap;
	private String macToAgent;
	
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
		String cardNO = hReq.getParameter("CARDNO");
		String paywapBeginTime = hReq.getParameter("paywapBeginTime");
		try {
			//判断支付超时
			Date paywapEndTime=new Date();
			if( paywapEndTime.getTime()-Long.parseLong(paywapBeginTime)>Long.parseLong(PayWapConfig.getPAYWAP_TIMELIMIT())){
				throw new Exception("支付超时");
			}
			
			String cards = getRequestParam(hReq, "CARDLIST");
			String key = getRequestParam(hReq, "key");
			String enCodeType = getAndcheckRequestParam(hReq, "ENCODETYPE");
			String macFromAgent = getAndcheckRequestParam(hReq, "MAC");
			xmlStr = getXmlStr(hReq);
			
			DealProcessorService ds = (DealProcessorService) SpringContextHelper
					.getDealProcessorServiceBean();
			String wbSvrCode = "200001";

			String out = ds.dispatchCommand(wbSvrCode + "|123456", xmlStr);
			
			Document document = DocumentHelper.parseText(out);
			Element e = (Element) document.selectSingleNode("//"
					+ "RESPONSECODE");
			String responsecode = e.getTextTrim();
			Element e2 = (Element) document.selectSingleNode("//"
					+ "RESPONSECONTENT");
			String responsecontent = "";
			if (e2 != null) {
				responsecontent = e2.getTextTrim();
			}
			Element e3 = (Element) document.selectSingleNode("//" + "ORDERID");
			String orderId = "";
			if (e3 != null) {
				orderId = e3.getTextTrim();
			}
			List<String> cardList = new ArrayList<String>();
			if (cards.equals("")) {

			} else {
				String str[] = (cards.substring(1, cards.length()-1)).split(",");
				for (int i = 0; i < str.length; i++) {
					cardList.add(str[i].trim());
				}
			}
			if (!"2001209".equals(responsecode)) {
				String tranDate = paywap.getTradTime().substring(0, 8);
				String params = createRetParamUrl(orderId, tranDate, responsecode,
						responsecontent, paywap.getTradeSeq(), paywap
								.getTxnAmount(), paywap.getAgentCode(),
								key);
				String backMerReturn ="";
				try{
					backMerReturn = URLConnectionTool.readContentFromPost(paywap.getBackMerChanturl(),params,PayWapConfig.getCONNECT_BACKMERCHANT_TIMELIMIT(), PayWapConfig.getWAIT_BACKMERCHANT_RES_TIMELIMIT());
					if(backMerReturn.indexOf("ORDERID_")!=0){
						TPayWapDao.insert(orderId, tranDate, responsecode, responsecontent, paywap.getTradeSeq(), paywap.getTxnAmount(), macToAgent,paywap.getBackMerChanturl());
					}
				}catch (Exception ex) {
					TPayWapDao.insert(orderId, tranDate, responsecode, responsecontent, paywap.getTradeSeq(), paywap.getTxnAmount(), macToAgent,paywap.getBackMerChanturl());
				}

			}else{
				cardList.add(cardNO);
				hReq.setAttribute("cardList", cardList);
			}

			hReq.setAttribute("responsecode", responsecode);
			hReq.setAttribute("responsecontent", responsecontent);
			hReq.setAttribute("paywap", paywap);
			hReq.setAttribute("orderId", orderId);
			hReq.setAttribute("enCodeType", enCodeType);
			hReq.setAttribute("key", key);
			hReq.setAttribute("MAC", macFromAgent);
			hReq.getRequestDispatcher("/pay/payWapResult.jsp").forward(hReq,
					hRes);
		} catch (Exception e) {
			hReq.setAttribute("exceptions", e.getMessage());
			hReq.getRequestDispatcher("/pay/payWapError.jsp").forward(hReq,
					hRes);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(MD5.MD5Encode("ORDERID=120113006798721&AGENTCODE=000020700000&TRADESEQ=66620120113101318123&TXNAMOUNT=100&RETNCODE=000000&TRANDATE =20120113&KEY=123456"));
	}

	public String getXmlStr(HttpServletRequest request) throws Exception {
		String agentCode = getAndcheckRequestParam(request, "AGENTCODE");
		String areaCode = getRequestParam(request, "AREACODE");
		String txnChannel = getAndcheckRequestParam(request, "TXNCHANNEL");
		String requestSeq = getAndcheckRequestParam(request, "REQUESTSEQ");
		String payType = getAndcheckRequestParam(request, "PAYTYPE");
		String cardNO = getAndcheckRequestParam(request, "CARDNO");
		String cardType = getAndcheckRequestParam(request, "CARDTYPE");
		String cardPwd = getAndcheckRequestParam(request, "CARDPWD");
		String txnAmount = CurrencyTool.yuan2Fen(getAndcheckRequestParam(request, "TXNAMOUNT"));
		String goodsCode = getRequestParam(request, "GOODSCODE");
		String goodsName = getRequestParam(request, "GOODSNAME");
		String tradeSeq = getAndcheckRequestParam(request, "TRADESEQ");
		String tradTime = getAndcheckRequestParam(request, "TRADETIME");
		String backMerChanturl = getAndcheckRequestParam(request,
				"BACKMERCHANTURL");
		String merChanturl = getAndcheckRequestParam(request, "MERCHANTURL");
		String agentName = getAndcheckRequestParam(request, "AGENTNAME");

		paywap = new PayWap(agentCode, areaCode, txnChannel, payType,
				txnAmount, merChanturl, backMerChanturl, goodsCode, goodsName,
				tradeSeq, requestSeq, tradTime, agentName);

		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress().toString();// 获得本机IP
		String appFrom = "440000-payWapExecuteServlet-001-" + ip;

		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ " <PayPlatRequestParameter>" + " 	<CTRL-INFO "
				+ " WEBSVRNAME=\"支付接口\" " + " WEBSVRCODE=\"200001\" "
				+ " APPFROM=\"" + appFrom + "\"" + " 		KEEP=\"" + requestSeq
				+ "\" />" + " 	<PARAMETERS>" + " 		<AGENTCODE>" + agentCode
				+ "</AGENTCODE>" + " <TXNCHANNEL>" + txnChannel
				+ "</TXNCHANNEL>" + " <PAYTYPE>" + payType + "</PAYTYPE>"
				+ " <CARDNO>" + cardNO + "</CARDNO>" + " <CARDTYPE>" + cardType
				+ "</CARDTYPE>" + " <CARDPWD>" + cardPwd + "</CARDPWD>"
				+ " <TXNAMOUNT>" + txnAmount + "</TXNAMOUNT>" + " <TRADESEQ>"
				+ tradeSeq + "</TRADESEQ>" + " <TRADETIME>" + tradTime
				+ "</TRADETIME>";

		if (!Charset.isEmpty(areaCode)) {
			xmlStr += " <AREACODE>" + areaCode + "</AREACODE>";
		}
		if (!Charset.isEmpty(goodsCode)) {
			xmlStr += " <GOODSCODE>" + goodsCode + "</GOODSCODE>";
		}
		if (!Charset.isEmpty(goodsName)) {
			xmlStr += " <GOODSNAME>" + goodsName + "</GOODSNAME>";
		}

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

	private String createRetParamUrl(String orderId, String tranDate,
			String retnCode, String retnInfo, String tradeSeq,
			String txnAmount, String agentCode, String key) {
		StringBuilder sb = new StringBuilder();
		sb.append("ORDERID=");
		sb.append(orderId);
		sb.append("&AGENTCODE=");
		sb.append(agentCode);
		sb.append("&TRADESEQ=");
		sb.append(tradeSeq);
		sb.append("&TXNAMOUNT=");
		sb.append(txnAmount);
		sb.append("&RETNCODE=");
		sb.append(retnCode);
		sb.append("&TRANDATE=");
		sb.append(tranDate);
		sb.append("&KEY=");
		sb.append(key);
		
		//System.out.println("进行MD5的明文：" + sb.toString());
		
		macToAgent = MD5.MD5Encode(sb.toString()).toUpperCase();
		
		//System.out.println("MD5结果：" + macToAgent);
		
		String params = "ORDERID=" + orderId + "&TRANDATE=" + tranDate
				+ "&RETNCODE=" + retnCode + "&RETNINFO=" + retnInfo
				+ "&TRADESEQ=" + tradeSeq + "&TXNAMOUNT=" + txnAmount
				+ "&CURTYPE=RMB" + "&ENCODETYPE=1&MAC=" + macToAgent;
		return params;
	}
}
