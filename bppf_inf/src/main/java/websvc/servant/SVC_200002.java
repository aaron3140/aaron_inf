package websvc.servant;

import common.dao.TInfDcoperlogDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.bean.SocketConfig;
import common.platform.provider.server.PackageDataSet;
import common.utils.DateTool;
import common.utils.Merchant;
import common.utils.MerchantCache;
import common.utils.WebSvcTool;
import common.xml.CommonRespAbs;
import common.xml.dp.DpPaymentReversalRequest;
import common.xml.dp.DpPaymentReversalResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFException;
import framework.exception.INFErrorDef;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *  支付冲正接口
 */
public class SVC_200002 {

	 public static String svcInfName = "200002";
	
	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		Long pk = null;
		DpPaymentReversalRequest paymentReversalRequest = null;
		String agentCode = null;   //代理商编码
		String cardNo = null;
		String tradeSeq = null;    //交易流水号
		String txnChannel = null; //交易渠道
		String tradeTime = null;   //交易时间
		String oldTradeSeq = null; //原预定交易流水号
		//String oldTradeTime = null;//原预定交易时间
		String keep = null;
		
		DpPaymentReversalResponse resp = new DpPaymentReversalResponse();
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			paymentReversalRequest = new DpPaymentReversalRequest(in1);
			
			agentCode = paymentReversalRequest.getAgentCode();
			cardNo = paymentReversalRequest.getCardNo();
			oldTradeSeq = paymentReversalRequest.getOldTradeSeq();
			tradeSeq = paymentReversalRequest.getTradeSeq();
			tradeTime = paymentReversalRequest.getTradeTime();
			txnChannel = paymentReversalRequest.getTxnChannel();
			keep = paymentReversalRequest.getKeep();
			
//			检查流水号是否存在,存在则抛错
			boolean exist = WebSvcTool.checkTradeSeqAndAgent(tradeSeq, agentCode);
			
			if(exist){
				throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
						INFErrorDef.POSSEQNO_CONFLICT_REASON);
			}
		
			//写日志 
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "SCS0101", SocketConfig.getSockIp(),
					"TRADESEQ", tradeSeq, "AGENTCODE", agentCode);
			id.setPk(pk);
			//得到sp商户信息
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
   
            //得到客户编码
            String termNo = spinfo.getTermNo();
            
			//查询OPER_ORIG
		
			
//			调用PCM0011
			IParamGroup g401 = new ParamGroupImpl("401");// 包头
			
			g401.put("4230", "0001");//操作类型
			g401.put("4007", termNo);//受理终端号
			g401.put("E007", termNo);//需要冲正的交易的受理终端号
			g401.put("E008", DateTool.getCurDate3());//需要冲正的交易的受理时间
			g401.put("4144", txnChannel);//渠道类型编码
			g401.put("4017", tradeSeq);//终端流水号
			g401.put("4146", spinfo.getCustCode());//操作员
			g401.put("E017", oldTradeSeq);//需要冲正的交易的流水号
			
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet packageDataSet = caller.call("SCS0101",g401);// 组成交易数据包,调用PCM0011接口
			
			String responseCode = packageDataSet.getByID("0001", "000");// 获取接口的000组的0001参数
			// 返回响应码
			String responseDesc = packageDataSet.getByID("0002", "000");// 获取接口的000组的0002参数
			
			//更新日志
			TInfDcoperlogDao.update(pk, responseCode, responseDesc);
		
			return resp.toCommonXmlStr(svcInfName, "10", keep, "SUCCESS", responseCode, responseDesc);
			
		} catch (XmlINFException spe) {
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e), id);
		}
		
		
	}
	
}
