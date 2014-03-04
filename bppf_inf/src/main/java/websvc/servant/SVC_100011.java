package websvc.servant;

import common.dao.TInfDcoperlogDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.bean.SocketConfig;
import common.platform.provider.server.PackageDataSet;
import common.utils.CurrencyTool;
import common.utils.DictTranslationUtils;
import common.utils.Merchant;
import common.utils.MerchantCache;
import common.utils.WebSvcTool;
import common.xml.dp.DpBuyPhysicalCardRequest;
import common.xml.dp.DpBuyPhysicalCardResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *  在线售卡(实体卡)-提交预订单接口
 */
public class SVC_100011 {

	 public static String svcInfName = "100011";
	
	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		Long pk = null;
		DpBuyPhysicalCardRequest buyPhysicalCardRequest = null;
		
		String agentCode = null;
		String tradeSeq = null;
		String tradeTime = null;
		String company = null;
		String contact = null;
		String tel = null;
		String add = null;
		String email = null;
		String cardAmt = null;
		String orderNum = null;
		String invoice = null;
		String payType = null;
		String isFirstFlag = null;
		
		String keep = null;
		DpBuyPhysicalCardResponse resp = null;
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			buyPhysicalCardRequest = new DpBuyPhysicalCardRequest(in1);
			
			agentCode = buyPhysicalCardRequest.getAgentCode();
			tradeSeq = buyPhysicalCardRequest.getTradeSeq();
			tradeTime = buyPhysicalCardRequest.getTradeTime();
			company = buyPhysicalCardRequest.getCompany();
			contact = buyPhysicalCardRequest.getContact();
			tel = buyPhysicalCardRequest.getTel();
			add = buyPhysicalCardRequest.getAdd();
			email = buyPhysicalCardRequest.getEmail();
			cardAmt = buyPhysicalCardRequest.getCardAmt();
			orderNum = buyPhysicalCardRequest.getOrderNum();
			invoice = buyPhysicalCardRequest.getInvoice();
			payType = buyPhysicalCardRequest.getPayType();
			isFirstFlag = buyPhysicalCardRequest.getIsFirstFlag();

			keep = buyPhysicalCardRequest.getKeep();
			
//			验证商户平台流水号唯一性
			boolean exist = WebSvcTool.checkTradeSeqAndAgent(tradeSeq, agentCode);
			
			if(exist){
				throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
						INFErrorDef.POSSEQNO_CONFLICT_REASON);
			}
			
			//写日志 
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "PCM0033", SocketConfig.getSockIp(),
					"TRADESEQ", tradeSeq,"AGENTCODE",agentCode);
			id.setPk(pk);
			
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
			String custCode = spinfo.getCustCode();
			
			String cardAmtFen = "";
			if(cardAmt != null &&!cardAmt.equals("")){
				String cardAmtYuan = DictTranslationUtils.dictTranslation(cardAmt,"INF_CARDAMT");
				cardAmtFen = CurrencyTool.yuan2Fen(cardAmtYuan);
			}
			
			String payTypeValue = DictTranslationUtils.dictTranslation(payType, "INF_PCARDBOOKPAYTYPE");
			String isFirstTime = DictTranslationUtils.dictTranslation(isFirstFlag, "INF_PCARDISFIRSTFLAG");
			

			//调用PCM0033
			IParamGroup g153 = new ParamGroupImpl("153");// 包头
			
			g153.put("1603", company);// 公司名称
			g153.put("1590", contact);// 联系人
			g153.put("1593", tel);// 联系电话	
			g153.put("1613", add);// 地址
			g153.put("1610", email);// Email
			g153.put("1539", cardAmtFen);// 卡面值
			g153.put("1501", orderNum);// 张数
			g153.put("1611", invoice);// 发票抬头
			g153.put("1612", payTypeValue);// 付款方式
			g153.put("1604", isFirstTime);// 公司是否初次购买标识
			g153.put("1556", custCode);// 操作员工号
			
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet packageDataSet = caller.call("PCM0033",g153);// 组成交易数据包,调用PCM0006接口
			
			String responseCode = packageDataSet.getByID("0001", "000");// 获取接口的000组的0001参数
			// 返回响应码
			String responseContent = packageDataSet.getByID("0002", "000");// 获取接口的000组的0002参数
			
			String orderId = packageDataSet.getByID("1582", "153");//订单号
			
			resp = new DpBuyPhysicalCardResponse();
			
			//更新日志
			TInfDcoperlogDao.update(pk, responseCode, responseContent);
			
			return resp.toXMLStr("SUCCESS", keep,svcInfName, responseCode, responseContent, orderId);
			
		} catch (XmlINFException spe) {
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e), id);
		}
		
		
	}
	
}
