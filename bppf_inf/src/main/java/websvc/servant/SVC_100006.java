package websvc.servant;

import common.dao.TInfDcoperlogDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.bean.SocketConfig;
import common.platform.provider.server.PackageDataSet;
import common.utils.Merchant;
import common.utils.MerchantCache;
import common.xml.dp.DpCardBalanceRequest;
import common.xml.dp.DpCardBalanceResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *  卡余额查询接口
 */
public class SVC_100006 {

	 public static String svcInfName = "100006";
	
	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		Long pk = null;
		DpCardBalanceRequest cardBalanceRequest = null;
		String cardNo = null;
		String agentCode = null;
		String keep = null;
		DpCardBalanceResponse resp = null;
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			cardBalanceRequest = new DpCardBalanceRequest(in1 );
			
			cardNo = cardBalanceRequest.getCardNo();
			agentCode = cardBalanceRequest.getAgentCode();
			keep = cardBalanceRequest.getKeep();
			
			//写日志 
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "PCM0006", SocketConfig.getSockIp(),
					"AGENTCODE", agentCode,"CARDNO",cardNo);
			id.setPk(pk);
			
			//得到sp商户信息
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
			
			//调用PCM0006
			IParamGroup g153 = new ParamGroupImpl("153");// 包头
			
			g153.put("1530", cardNo);// 卡号
			
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet packageDataSet = caller.call("PCM0006",g153);// 组成交易数据包,调用PCM0006接口
			
			String responseCode = packageDataSet.getByID("0001", "000");// 获取接口的000组的0001参数
			// 返回响应码
			String responseContent = packageDataSet.getByID("0002", "000");// 获取接口的000组的0002参数
			
			String balance = packageDataSet.getByID("1567", "153");//TODO 卡余额
			
			String availBalance = packageDataSet.getByID("1568", "153");//卡号可用余额
			
			resp = new DpCardBalanceResponse();
			
			//更新日志
			TInfDcoperlogDao.update(pk, responseCode, responseContent);
			
			return resp.toXMLStr("SUCCESS", keep,svcInfName, responseCode, responseContent, balance, availBalance);
			
		} catch (XmlINFException spe) {
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e), id);
		}
		
		
	}
	
}
