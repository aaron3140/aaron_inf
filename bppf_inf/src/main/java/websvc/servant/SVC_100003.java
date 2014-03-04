package websvc.servant;

import common.dao.TInfDcoperlogDao;
import common.dao.TScsQueryRecDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.bean.SocketConfig;
import common.platform.provider.server.PackageDataSet;
import common.utils.Merchant;
import common.utils.MerchantCache;
import common.utils.WebSvcTool;
import common.xml.CommonRespAbs;
import common.xml.dp.DpCardOrderCancelRequest;
import common.xml.dp.DpCardOrderCancelResponse;
import common.xml.dp.DpCardOrderResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFException;
import framework.exception.INFErrorDef;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *  卡取消订购接口
 */
public class SVC_100003 {

	 public static String svcInfName = "100003";

	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		Long pk = null;
		DpCardOrderCancelRequest cardOrderCancelRequest = null;
		String agentCode = null;   //代理商编码
		String tradeSeq = null;    //交易流水号
		//String tradeTime = null;   //交易时间
		String oldTradeSeq = null; //原预定交易流水号
		//String oldTradeTime = null;//原预定交易时间
		String keep = null;
		
		DpCardOrderCancelResponse resp = new DpCardOrderCancelResponse();
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			cardOrderCancelRequest = new DpCardOrderCancelRequest(in1);
			
			agentCode = cardOrderCancelRequest.getAgentCode();
			oldTradeSeq = cardOrderCancelRequest.getOldTradeSeq();
			//oldTradeTime = cardOrderCancelRequest.getOldTradeTime();
			tradeSeq = cardOrderCancelRequest.getTradeSeq();
			//tradeTime = cardOrderCancelRequest.getTradeTime();
			keep = cardOrderCancelRequest.getKeep();
			
			//检查流水号是否存在,存在则抛错
			boolean exist = WebSvcTool.checkTradeSeqAndAgent(tradeSeq, agentCode);
			
			if(exist){
				throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
						INFErrorDef.POSSEQNO_CONFLICT_REASON);
			}
			
			//写日志 
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "PCM0011", SocketConfig.getSockIp(),
					"TRADESEQ", tradeSeq, "AGENTCODE", agentCode);
			id.setPk(pk);
			//得到sp商户信息
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
   
            //得到客户编码
            String custCode = spinfo.getCustCode(); 
            String termNo = spinfo.getTermNo();
            
			//查询OPER_ORIG
            String operOrig = TScsQueryRecDao.getOperOrig(termNo, custCode, oldTradeSeq);
		
			
//			调用PCM0011
			IParamGroup g153 = new ParamGroupImpl("153");// 包头
			
			g153.put("1556", spinfo.getCustCode());//操作员工号
			g153.put("1551", operOrig);//预订流水号
			
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet packageDataSet = caller.call("PCM0011",g153);// 组成交易数据包,调用PCM0011接口
			
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
