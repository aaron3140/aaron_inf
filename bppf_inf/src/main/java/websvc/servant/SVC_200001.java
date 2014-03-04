package websvc.servant;

import java.util.ArrayList;
import java.util.List;

import common.algorithm.RSACipher;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.bean.SocketConfig;
import common.platform.provider.server.PackageDataSet;
import common.utils.Charset;
import common.utils.Constant;
import common.utils.CurrencyTool;
import common.utils.DateTool;
import common.utils.Merchant;
import common.utils.MerchantCache;
import common.utils.WebSvcTool;
import common.xml.dp.DpPaymentRequest;
import common.xml.dp.DpPaymentResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *	支付接口
 */
public class SVC_200001 {
	
	 public static String svcInfName = "200001";

	public static void main(String[] args) {
		
	}
	
	public static String execute(String in0, String in1) {
		Long pk = null;
		DpPaymentRequest paymentRequest = null;
		DpPaymentResponse resp = new DpPaymentResponse();
		String agentCode = null;
		String cardType = null;
		String areaCode = null;
		String txnChannel = null;
		String payType = null;
		String cardNo = null;
		String cardPwd = null;
		String txnAmount = null;
		String tradeSeq = null;
		String goodsName = null;
		String goodsCode = null;
		String tradeTime = null;
		String keep = null;
		
		String txnAmountYuan = null;
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			paymentRequest = new DpPaymentRequest(in1);

			agentCode = paymentRequest.getAgentCode();//AGENTCODE
			areaCode = paymentRequest.getAreaCode();
			txnChannel = paymentRequest.getTxnChannel();
			payType = paymentRequest.getPayType();
			cardNo = paymentRequest.getCardNo();
			cardPwd = paymentRequest.getCardPwd();
			txnAmount = paymentRequest.getTxnAmount();
			tradeSeq = paymentRequest.getTradeSeq();//TRADESEQ
			goodsName = paymentRequest.getGoodsName();
			goodsCode = paymentRequest.getGoodsCode();
			tradeTime = paymentRequest.getTradeTime();
			cardType = paymentRequest.getCardType();
			keep = paymentRequest.getKeep();
			
			//关联机构验证
			if(!TCumInfoDao.verifyMerIdCustCode(paymentRequest.getAgentCode(), paymentRequest.getMerId())){
				
				if(TCumInfoDao.getMerIdByCustCode(paymentRequest.getAgentCode(),paymentRequest.getMerId()))
				
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			}
				
			txnAmountYuan = CurrencyTool.fen2Yuan(txnAmount);// 交易金额 -以元为单位
			
			//String cardCustCode = TCumInfoDao.getCardCustCode(cardNo);
			
			//私钥解密
			String decryptPwd = RSACipher.decryptByPrivateKey(cardPwd);

			
			//PCD 卡鉴权
			WebSvcTool.callPCD0002(cardNo, decryptPwd, cardType);
			
			//检查payType值
			boolean exist = WebSvcTool.checkTradeSeqAndAgent(tradeSeq, agentCode);
//			得到sp商户信息
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
			//验证ip地址
			//checkIp(spinfo);
    		areaCode = Charset.isEmpty(areaCode) ? spinfo.getAreaCode() : areaCode;

			String custCode = spinfo.getCustCode(); //得到客户编码(txrd)
			if(!exist){
				
				//写日志表
				pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
						svcInfName, "SCS0001", SocketConfig.getSockIp(),
						"TRADESEQ", tradeSeq,"AGENTCODE",agentCode);
				id.setPk(pk);
				String timeStamp3 = DateTool.getCurDate3();// 格式化日期yyyyMMddHHmmss
				String termNo = spinfo.getTermNo(); //商户终端号
				
				IParamGroup g401 = new ParamGroupImpl("401");
				g401.put("4004", custCode);
				g401.put("4005", Constant.BUSI_PAY_ORDER_CODE);
				g401.put("4006", areaCode);
				g401.put("4007", termNo);
				g401.put("4008", timeStamp3);
				g401.put("4012", "支付");
				g401.put("4016", "LG001");
				g401.put("4144", txnChannel);
				g401.put("4017", tradeSeq);
				g401.put("4018", custCode);
				
				g401.put("4284", paymentRequest.getMerId());//机构编码     //20130628 wanght
				g401.endRow();
	
				IParamGroup g402 = new ParamGroupImpl("402");
				g402.put("4021", "0001");
				g402.put("4023", txnAmountYuan);
				g402.put("4024", "0");
				g402.put("4025", txnAmountYuan);
				g402.endRow();
	
				IParamGroup g404 = new ParamGroupImpl("404");
				g404.put("4047", "1");
				g404.put("4049", Constant.DP_PROD_CODE);//
				g404.put("4051", Constant.DP_PAYMENT_ACTION_CODE);//
				g404.put("4052", "");
				g404.put("4053", "1");
				g404.put("4906", "0");
				g404.endRow();
	
				IParamGroup g405 = new ParamGroupImpl("405");
				g405.put("4047", "1");
				g405.put("4021", Constant.DP_PROD_CODE);//
				g405.put("4066", txnAmountYuan);
				g405.put("4067", "0");
				g405.put("4068", txnAmountYuan);
				g405.put("4071", "101");
				g405.endRow();
	
				IParamGroup g407 = new ParamGroupImpl("407");
				if (!Charset.isEmpty(goodsCode)) {
					g407.put("4047", "1");
					g407.put("4051", Constant.DP_PAYMENT_ACTION_CODE);
					g407.put("4087", "SCS_GOODSCODE");
					g407.put("4088", goodsCode);
					g407.put("4080", "0");
					g407.endRow();
				}
				
				if (!Charset.isEmpty(goodsName)) {
					g407.put("4047", "1");
					g407.put("4051", Constant.DP_PAYMENT_ACTION_CODE);
					g407.put("4087", "SCS_GOODSNAME");
					g407.put("4088", goodsName);
					g407.put("4080", "0");
					g407.endRow();
				}
					
				IParamGroup g408 = new ParamGroupImpl("408");
				g408.put("4103", "1");
				g408.put("4097", Constant.PT_DP_PAYMENT_CODE);
				g408.put("4098", Constant.DP_PAYMENT_AGENT_NO);
				g408.put("4101", cardNo);
				g408.put("4102", decryptPwd);
				g408.put("4021", "0001");
				g408.put("4104", txnAmountYuan);
				g408.endRow();
	
	
				
				IServiceCall caller = new ServiceCallImpl();
				PackageDataSet packageDataSet = caller.call("SCS0001", g401, g402,
						g404, g405, g407, g408);// 组成SCS0001交易数据包
				String responseCode = packageDataSet.getByID("0001", "000");// 获取SCS0001接口的000组的0001参数
				// 返回响应码
				String responseContent = packageDataSet.getByID("0002", "000");// 获取SCS0001接口的000组的0002参数
				// 返回响应码描述
	
				// 更新日志表
				TInfDcoperlogDao.update(pk, responseCode, responseContent);
				
				String orderId = packageDataSet.getByID("4002", "401");
				
				//CARDINFOS信息
				ArrayList<String[]> list = getCardInfoList(packageDataSet);			
				
				return resp.toXMLStr("SUCCESS", keep, svcInfName, responseCode, responseContent, orderId, list);
				//return new DpPaymentResponse().toXMLStr(responseCode, responseContent);
			} else if("01".equals(payType)){
				throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
						INFErrorDef.POSSEQNO_CONFLICT_REASON);
			} else if("02".equals(payType)){
				//写日志表
				pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
						"200001", "SCS0013", SocketConfig.getSockIp(),
						"TRADESEQ", tradeSeq,"AGENTCODE",agentCode);
				id.setPk(pk);
				IParamGroup g401 = new ParamGroupImpl("401");
				String orderId = WebSvcTool.getOrderIdByTermSeq(custCode, tradeSeq);
				g401.put("4002", orderId); // order id
				g401.put("4005", Constant.BUSI_PAY_ORDER_CODE);
				g401.endRow();
	
				IParamGroup g404 = new ParamGroupImpl("404");
				g404.put("4047", "1");
				g404.put("4049", Constant.DP_PROD_CODE);
				g404.put("4051", Constant.DP_PAYMENT_ACTION_CODE);
				g404.put("4052", "");
				g404.put("4053", "1");
				g404.put("4906", "0");
				g404.endRow();
	
				IParamGroup g405 = new ParamGroupImpl("405");
				g405.put("4047", "1");
				g405.put("4021", "0001");
				g405.put("4066", txnAmountYuan);
				g405.put("4067", "0");
				g405.put("4068", txnAmountYuan);
				g405.put("4071", "101");
				g405.endRow();
	
				IParamGroup g408 = new ParamGroupImpl("408");
				g408.put("4103", "1");
				g408.put("4097", Constant.PT_DP_PAYMENT_CODE);//
				g408.put("4098", Constant.DP_PAYMENT_AGENT_NO);
				g408.put("4101", cardNo);
				g408.put("4102", decryptPwd);
				g408.put("4021", "0001");
				g408.put("4104", txnAmountYuan);
				g408.endRow();
	
				IServiceCall caller = new ServiceCallImpl();
				PackageDataSet packageDataSet = caller.call("SCS0013", g401, g404,
						g405, g408);// 组成SCS0013交易数据包
				String responseCode = packageDataSet.getByID("0001", "000");// 获取SCS0013接口的000组的0001参数
				// 返回响应码
				String responseContent = packageDataSet.getByID("0002", "000");// 获取SCS0013接口的000组的0002参数
				// 返回响应码描述
	
				// 更新日志表
				TInfDcoperlogDao.update(pk, responseCode, responseContent);
				
				//CARDINFOS信息		
				ArrayList<String[]> list = getCardInfoList(packageDataSet);			
				
				return resp.toXMLStr("SUCCESS", keep, svcInfName, responseCode, responseContent, orderId, list);
			} else {
				return null;
			}
			
		} catch (XmlINFException spe) {
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e), id);
		}
	}
	
	private static ArrayList<String[]> getCardInfoList(PackageDataSet packageDataSet) {
		List<String> returnCardNo = packageDataSet.getParamByID("1533", "153");//卡号
		List<String> balance = packageDataSet.getParamByID("1515", "153");//卡余额
		List<String> txnaMount = packageDataSet.getParamByID("1558", "153");//该卡花费的金额
		
		ArrayList<String[]> list = null;
		String[] strs = null;
		if(null != returnCardNo && 0 != returnCardNo.size()){
			list = new ArrayList<String[]>();
			for (int i=0;i<returnCardNo.size();i++) {
				strs = new String[3];
				strs[0] = returnCardNo.get(i);
				strs[1] = CurrencyTool.yuan2Fen(balance.get(i));
				strs[2] = CurrencyTool.yuan2Fen(txnaMount.get(i));
				list.add(strs);
			}
			list.trimToSize();
		}
		
		return list;
	}

}
