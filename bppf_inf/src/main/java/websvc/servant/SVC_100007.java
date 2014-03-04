package websvc.servant;

import common.algorithm.RSACipher;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.provider.bean.SocketConfig;
import common.platform.provider.server.PackageDataSet;
import common.utils.Merchant;
import common.utils.MerchantCache;
import common.utils.WebSvcTool;
import common.xml.dp.DpCardValidRequest;
import common.xml.dp.DpCardValidResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *	卡鉴权接口
 */
public class SVC_100007 {

	 public static String svcInfName = "100007";

	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		Long pk = null;
		DpCardValidRequest cardValidRequest = null;
		DpCardValidResponse resp = new DpCardValidResponse();
		String cardNo = null;
		String cardPwd = null;
		String agentCode = null;
		String cardType = null;
		String keep = null;
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			cardValidRequest = new DpCardValidRequest(in1 );
			
			cardNo = cardValidRequest.getCardNo();
			cardPwd = cardValidRequest.getCardPwd();
			agentCode = cardValidRequest.getAgentCode();
			cardType = cardValidRequest.getCardType();
			keep = cardValidRequest.getKeep();
			
			//关联机构验证
			if(!TCumInfoDao.verifyMerIdCustCode(cardValidRequest.getAgentCode(), cardValidRequest.getMerId())){
				
				if(TCumInfoDao.getMerIdByCustCode(cardValidRequest.getAgentCode(),cardValidRequest.getMerId()))
				
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			}
			
//			写日志 
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "PCD0002", SocketConfig.getSockIp(),
					"CARDNO",cardNo);
			id.setPk(pk);
			//得到sp商户信息
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
			
			//私钥解密
			String decryptPwd = RSACipher.decryptByPrivateKey(cardPwd);

			IParamGroup g153 = new ParamGroupImpl("153");// 包头
			
			g153.put("1530", cardNo);// 卡号
			g153.put("1535", decryptPwd);// 卡密码
			g153.put("1564", cardType);//卡类别
			g153.endRow();// 把当前参数对封装为一行
			
			PackageDataSet packageDataSet = WebSvcTool.callPCD0002(cardNo, decryptPwd, cardType);
			
			String responseCode = packageDataSet.getByID("0001", "000");// 获取SCS0001接口的000组的0001参数
			// 返回响应码
			String responseContent = packageDataSet.getByID("0002", "000");// 获取SCS0001接口的000组的0002参数
			
			//更新日志
			TInfDcoperlogDao.update(pk, responseCode, responseContent);
			
			return resp.toCommonXmlStr(svcInfName, "10", keep, "SUCCESS", responseCode, responseContent);
			
		} catch (XmlINFException spe) {
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e), id);
		}
		
		
	}
	
}
