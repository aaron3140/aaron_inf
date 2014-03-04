package framework.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.algorithm.MD5;
import common.dao.TPayWapDao;
import common.utils.Charset;
import common.utils.URLConnectionTool;
import framework.config.PayWapConfig;

public class PayWapMerchantURLServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  String macToAgent;
	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(arg0, arg1);
	}

	@Override
	protected void doPost(HttpServletRequest hReq, HttpServletResponse hRes)
			throws ServletException, IOException {
		String key = getRequestParam(hReq, "key");
		String orderId = getRequestParam(hReq,"orderId");
		String merchantURL = getRequestParam(hReq,"MERCHANTURL");
		String backMerChanturl = getRequestParam(hReq,
		"BACKMERCHANTURL");
		String responsecode = getRequestParam(hReq,"RESPONSECODE");
		String responsecontent = getRequestParam(hReq,"RESPONSECONTENT");
		String tranTime=getRequestParam(hReq,"TRADETIME");
		String tradeSeq=getRequestParam(hReq,"TRADESEQ");
		String txnAmount=getRequestParam(hReq,"TXNAMOUNT");
		String agentCode=getRequestParam(hReq,"AGENTCODE");
		String tranDate=tranTime.substring(0,8);
		String params=createRetParamUrl(orderId, tranDate, responsecode, responsecontent, tradeSeq, txnAmount, agentCode, key);
		try {
			if("2001209".equals(responsecode)){		
				try{
					String backMerReturn = URLConnectionTool.readContentFromPost(backMerChanturl,params,PayWapConfig.getCONNECT_BACKMERCHANT_TIMELIMIT(), PayWapConfig.getWAIT_BACKMERCHANT_RES_TIMELIMIT());
					if(backMerReturn.indexOf("ORDERID_")!=0){
						TPayWapDao.insert(orderId, tranDate, responsecode, responsecontent, tradeSeq, txnAmount, macToAgent, backMerChanturl);
					}
				}catch (Exception ex) {
					TPayWapDao.insert(orderId, tranDate, responsecode, responsecontent, tradeSeq, txnAmount, macToAgent, backMerChanturl);
				}
			}			
			hRes.sendRedirect(merchantURL+"?"+params);
			//hReq.getRequestDispatcher(merchantURL).forward(hReq,hRes);
			
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

	}


	/**
	 * 获取传递过来的参数值
	 * 
	 * @version: 1.00
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
	
	private  String createRetParamUrl(String orderId,String tranDate,String retnCode,String retnInfo,String tradeSeq,String txnAmount,String agentCode,String key ){
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
		macToAgent = MD5.MD5Encode(
				sb.toString())
				.toUpperCase();
		String params = "ORDERID="+orderId+"&TRANDATE="+tranDate+"&RETNCODE="
		+retnCode+"&RETNINFO="+retnInfo+"&TRADESEQ="+tradeSeq
		+"&TXNAMOUNT="+txnAmount+"&CURTYPE=RMB"
		+"&ENCODETYPE=1&MAC="+macToAgent;
		return params;
	}
}
