package websvc.servant;

import common.algorithm.RSACipher;
import common.dao.TInfDcoperlogDao;
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
import common.xml.dp.DpModifyPwdRequest;
import common.xml.dp.DpModifyPwdResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *  密码修改接口
 */
public class SVC_100010 {

	 public static String svcInfName = "100010";
	
	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		Long pk = null;
		DpModifyPwdRequest modifyPwdRequest = null;
		String agentCode = null;   //商户编码
		String cardNo = null;   //卡号
		String oldPasswd = null; //旧密码
		String newPasswd = null; //新密码
		String cardType = null;
		String keep = null;
		
		
		DpModifyPwdResponse resp = new DpModifyPwdResponse();
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			modifyPwdRequest = new DpModifyPwdRequest(in1);
			agentCode = modifyPwdRequest.getAgentCode();
			cardNo = modifyPwdRequest.getCardNo();
			oldPasswd = modifyPwdRequest.getOldPasswd();
			newPasswd = modifyPwdRequest.getNewPasswd();
			cardType = modifyPwdRequest.getCardType();
			keep = modifyPwdRequest.getKeep();
			
			//写日志 
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "PCM0037", SocketConfig.getSockIp(),
					"AGENTCODE", agentCode, "CARDNO", cardNo);
			id.setPk(pk);
//			私钥解密
			String decryptNewPwd = RSACipher.decryptByPrivateKey(newPasswd);
			String decryptOldPwd = RSACipher.decryptByPrivateKey(oldPasswd);
			
//			调用PCM0036		
			IServiceCall caller = new ServiceCallImpl();
			WebSvcTool.callPCM0036(cardNo, decryptOldPwd, cardType);		
			
			
//			得到sp商户信息
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
			String custCode = spinfo.getCustCode(); 
			
			//调用PCM0037
			IParamGroup groupFor37 = new ParamGroupImpl("153");// 包头
			
			groupFor37.put("1530", cardNo); //卡号
			groupFor37.put("1535", decryptNewPwd); //卡重置密码
			groupFor37.put("1556", custCode); //操作员工号
			
		
			PackageDataSet packageDataSet = caller.call("PCM0037",groupFor37);// 组成交易数据包,调用PCM0011接口
			
			
			
			
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
