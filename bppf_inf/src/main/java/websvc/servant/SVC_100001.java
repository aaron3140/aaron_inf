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
import common.xml.dp.DpCardInventoryRequest;
import common.xml.dp.DpCardInventoryResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *	卡库存查询接口
 */
public class SVC_100001 {

    public static String svcInfName = "100001";
	
	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		Long pk = null;
		DpCardInventoryRequest cardInventoryRequest = null;
		
		String agentCode = null;   //代理商编码
		String cardType = null;    //卡类型
		String subCardType = null; //卡子类型
		String cardPrefix = null;  //卡号前缀
		String cardAmt = null;     //卡面值
		
		String keep = null;
		
		DpCardInventoryResponse resp = null;
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			cardInventoryRequest = new DpCardInventoryRequest(in1);
			
			agentCode = cardInventoryRequest.getAgentCode();
			cardType = cardInventoryRequest.getCardType();
			subCardType = cardInventoryRequest.getSubCardType();
			cardPrefix = cardInventoryRequest.getCardPrefix();
			cardAmt = cardInventoryRequest.getCardAmt();
			keep = cardInventoryRequest.getKeep();
			
			//写日志 
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "PCM0009", SocketConfig.getSockIp(),
					"AGENTCODE", agentCode,"CARDTYPE",cardType);
			id.setPk(pk);
			
			//得到sp商户信息
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
			
			String cardAmtFen = "";
			if(cardAmt != null &&!cardAmt.equals("")){
				String cardAmtYuan = DictTranslationUtils.dictTranslation(cardAmt,"INF_CARDAMT");
				cardAmtFen = CurrencyTool.yuan2Fen(cardAmtYuan);
			}
			
			//调用PCM0009
			IParamGroup g153 = new ParamGroupImpl("153");// 包头
			
			g153.put("1589", cardType);
			g153.put("1533", subCardType);
			g153.put("1539", cardAmtFen);
			g153.put("1562", agentCode);
			g153.put("1587", cardPrefix);
			
			
			
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet packageDataSet = caller.call("PCM0009",g153);// 组成交易数据包,调用PCM0009接口
			
			String responseCode = packageDataSet.getByID("0001", "000");// 获取接口的000组的0001参数
			// 返回响应码
			String responseDesc = packageDataSet.getByID("0002", "000");// 获取接口的000组的0002参数
			
			String returnCardType = packageDataSet.getByID("1589", "153");//卡类别
			String returnSubCardType = packageDataSet.getByID("1533", "153");//卡类型
			String returnCardAmt = packageDataSet.getByID("1539", "153");//卡面值
			String stockNum = packageDataSet.getByID("1502", "153");//剩余数量
			
			//更新日志
			TInfDcoperlogDao.update(pk, responseCode, responseDesc);
			
			resp = new DpCardInventoryResponse();
			return resp.toXMLStr("SUCCESS", keep,svcInfName, responseCode, responseDesc, 
					returnCardType,returnSubCardType, returnCardAmt, stockNum);
			
		} catch (XmlINFException spe) {
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e), id);
		}
		
		
	}
	
}
