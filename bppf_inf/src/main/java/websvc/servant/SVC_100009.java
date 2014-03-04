package websvc.servant;

import common.algorithm.RSACipher;
import common.dao.TInfDcoperlogDao;
import common.dao.TPcmInfoDao;
import common.dao.TPcmVcInfoDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.bean.SocketConfig;
import common.platform.provider.server.PackageDataSet;
import common.service.CardManager;
import common.utils.WebSvcTool;
import common.xml.CommonRespAbs;
import common.xml.dp.DpLossReportingRequest;
import common.xml.dp.DpLossReportingResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *  卡挂失接口
 */
public class SVC_100009 {

	 public static String svcInfName = "100009";
	
	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		Long pk = null;
		DpLossReportingRequest lossReportingRequest = null;
		String cardNo = null;   //卡号
		String cardPwd = null;    //卡密
		String repLossWay = null; //挂失方式
		String repLossType = null; //挂失类型
		String cardType = null;
		String keep = null;
		
		
		DpLossReportingResponse resp = new DpLossReportingResponse();
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			lossReportingRequest = new DpLossReportingRequest(in1);
			
			cardNo = lossReportingRequest.getCardNo();
			cardPwd = lossReportingRequest.getCardPwd();
			repLossWay = lossReportingRequest.getRepLossWay();
			repLossType = lossReportingRequest.getRepLossType();
			cardType = lossReportingRequest.getCardType();
			keep = lossReportingRequest.getKeep();
			
			//写日志 
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "PCM0023", SocketConfig.getSockIp(),
					"CARDNO", cardNo);
			id.setPk(pk);
			
//			调用PCM0036
			
			//私钥解密
			String decryptPwd = RSACipher.decryptByPrivateKey(cardPwd);
			
			IServiceCall caller = new ServiceCallImpl();
			WebSvcTool.callPCM0036(cardNo, decryptPwd, cardType);		
			
			String cardStat = CardManager.getCardStat(cardNo, cardType);
			
			//调用PCM0023
			IParamGroup groupFor23 = new ParamGroupImpl("150");// 包头		
			groupFor23.put("1530", cardNo); //卡号
			groupFor23.put("1597", repLossWay); //挂失方式
			groupFor23.put("1598", repLossType); //挂失类型
			groupFor23.put("1556", "PCPF_INF"); //挂失操作员工号
			
			groupFor23.put("1542", cardStat); //原卡状态
		
			PackageDataSet packageDataSet = caller.call("PCM0023",groupFor23);// 组成交易数据包,调用PCM0011接口				
			
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
