package framework.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.algorithm.MD5;
import common.dao.TInfDcoperlogDao;
import common.entity.PayWap;
import common.utils.Charset;
import common.utils.CurrencyTool;
import common.utils.Merchant;
import common.utils.MerchantCache;
import common.utils.WebSvcTool;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class PayWapAcceptServlet extends HttpServlet {

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
		// TODO Auto-generated method stub
		INFLogID id = new INFLogID("PAYWAPACCEPT",
				TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			PayWap pw = payWap(hReq, id);
			hReq.setAttribute("payWap", pw);
			Date paywapBeginTime=new Date();
			hReq.setAttribute("paywapBeginTime", paywapBeginTime.getTime());
			hReq.getRequestDispatcher("/pay/payWapAccept.jsp").forward(hReq,
					hRes);
		} catch (Exception e) {
			// e.printStackTrace();
			ExceptionHandler.toXML(new XmlINFException(null, e), id);
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

	public PayWap payWap(HttpServletRequest request, INFLogID id)
			throws Exception {
		Long pk = null;
		String agentCode = getAndcheckRequestParam(request, "AGENTCODE");
		String areaCode = getRequestParam(request, "AREACODE");
		String txnChannel = getAndcheckRequestParam(request, "TXNCHANNEL");
		String payType = "02";
		String txnAmount = getAndcheckRequestParam(request, "TXNAMOUNT");
		String merChanturl = getAndcheckRequestParam(request, "MERCHANTURL");
		String backMerChanturl = getAndcheckRequestParam(request,
				"BACKMERCHANTURL");
		String goodsCode = getRequestParam(request, "GOODSCODE");
		String goodsName = getRequestParam(request, "GOODSNAME");
		String cards = getRequestParam(request, "CARDLIST");
		String tradeSeq = getAndcheckRequestParam(request, "TRADESEQ");
		String requestSeq = getAndcheckRequestParam(request, "REQUESTSEQ");
		String tradTime = getAndcheckRequestParam(request, "TRADETIME");
		String enCodeType = getAndcheckRequestParam(request, "ENCODETYPE");
		String MAC = getAndcheckRequestParam(request, "MAC");
		
		request.setAttribute("enCodeType", enCodeType);
		request.setAttribute("MAC", MAC);
		
		String agentName = "";
		
		if(cards==null || cards.equals("")){
			boolean exist = WebSvcTool.checkRequestSeqAndAgent(tradeSeq,
					agentCode);

			if (exist) {
				throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
						INFErrorDef.POSSEQNO_CONFLICT_REASON);
			}
			
			// 写日志表
			pk = TInfDcoperlogDao.insert(id.getPartyGroup(), id.getSvcInfName(),
					"", "", "REQUESTSEQ", requestSeq, "AGENTCODE", agentCode);
			
			// 得到sp商户信息
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
			agentName = spinfo.getPrtnName();
			String key = spinfo.getWapKey();

			if ("1".equals(enCodeType)) {
				StringBuilder sb = new StringBuilder();
				sb.append("AGENTCODE=");
				sb.append(agentCode);
				sb.append("&TRADESEQ=");
				sb.append(tradeSeq);
				sb.append("&TRADETIME=");
				sb.append(tradTime);
				sb.append("&TXNAMOUNT=");
				sb.append(txnAmount);
				sb.append("&KEY=");
				sb.append(key);
				String sign = MD5.MD5Encode(sb.toString()).toUpperCase();
				if (MAC.equals(sign)) {

				} else {
					throw new Exception("MAC校验错误");
				}
			}
			request.setAttribute("key", key);
			
			TInfDcoperlogDao.update(pk, "000000", "成功");
		}else{
			// 得到sp商户信息
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
			agentName = spinfo.getPrtnName();
			String cardsStr="";
			List<String> cardList = new ArrayList<String>();
			String str[]=(cards.substring(1, cards.length()-1)).split(",");
			for(int i=0;i<str.length;i++){
				cardList.add(str[i].trim());
				cardsStr+=str[i].trim();
				if(i!=(str.length-1)){
					cardsStr+=",";
				}
			}
			request.setAttribute("cardList", cardList);
			request.setAttribute("cards", cardsStr);
		}

		id.setPk(pk);

		// MTPaymentService mtp = SpringContextHelper.getMTPaymentServiceBean();
		PayWap pw = new PayWap(agentCode, areaCode, txnChannel, payType,
				CurrencyTool.fen2Yuan(txnAmount), merChanturl, backMerChanturl, goodsCode, goodsName,
				tradeSeq, requestSeq, tradTime, agentName);

		return pw;
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
	 * @author wenhg
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
