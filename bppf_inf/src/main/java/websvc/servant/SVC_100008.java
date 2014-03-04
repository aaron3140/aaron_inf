package websvc.servant;

import common.dao.TInfDcoperlogDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.bean.SocketConfig;
import common.platform.provider.server.PackageDataSet;
import common.utils.Charset;
import common.utils.CurrencyTool;
import common.utils.DictTranslationUtils;
import common.utils.Merchant;
import common.utils.MerchantCache;
import common.xml.dp.DpCardWithoutPwdRequest;
import common.xml.dp.DpCardWithoutPwdResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *	无密卡信息查询接口
 */
public class SVC_100008 {

	 public static String svcInfName = "100008";
	
	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		Long pk = null;
		DpCardWithoutPwdRequest cardWithOutPwdRequest = null;
		String cardNo = null;
		String agentCode = null;
		String keep = null;
		String cardType = null;
		DpCardWithoutPwdResponse resp = null;
		INFLogID logId = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			cardWithOutPwdRequest = new DpCardWithoutPwdRequest(in1);
			
			cardNo = cardWithOutPwdRequest.getCardNo();
			agentCode = cardWithOutPwdRequest.getAgentCode();
			cardType = cardWithOutPwdRequest.getCardType();
			keep = cardWithOutPwdRequest.getKeep();
			
			//写日志 
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "PCM0006", SocketConfig.getSockIp(),
					"AGENTCODE", agentCode,"CARDNO",cardNo);
			logId.setPk(pk);
			
			//	得到sp商户信息
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
			
			//调用PCM0006
			IParamGroup g153 = new ParamGroupImpl("153");// 包头
			
			g153.put("1530", cardNo);// 卡号
			
			IServiceCall caller = new ServiceCallImpl();
			
			
			PackageDataSet packageDataSet = null;
			//天翼支付卡
			if(null != cardType && "1".equals(cardType.trim())){
				packageDataSet = caller.call("PCM0006",g153);// 组成交易数据包,调用PCM0006接口
			}else{//11888卡
				packageDataSet = caller.call("PCM0021",g153);// 组成交易数据包,调用PCM0021接口
			}
			
			String responseCode = packageDataSet.getByID("0001", "000");// 获取接口的000组的0001参数
			// 返回响应码
			String responseContent = packageDataSet.getByID("0002", "000");// 获取接口的000组的0002参数
			
			String cardSeq = packageDataSet.getByID("", "153");//TODO 卡序号 
			String cardAmtFen = packageDataSet.getByID("1539", "153");//卡面值
			String returnCardNo = packageDataSet.getByID("1530", "153");//卡号
			String balance = packageDataSet.getByID("1567", "153");//卡余额
			String availBalance = packageDataSet.getByID("1568", "153");//卡号可用余额
			String frozenBal = packageDataSet.getByID("1569", "153");//已冻结金额
			String serviceFee = packageDataSet.getByID("1570", "153");//手续费
			String cardStat = packageDataSet.getByID("1534", "153");//卡状态
			String cardTypeName = packageDataSet.getByID("1520", "153");//卡类型名称
			//String cardType = packageDataSet.getByID("1564", "153");//卡类型
			String cardHolderName = packageDataSet.getByID("1590", "153");//持卡人姓名
			String expDate= packageDataSet.getByID("1537", "153");//失效期
			expDate = Charset.isEmpty(expDate) ? expDate : expDate.replaceAll("-|:|\\s", "");
			String makeCardDate = packageDataSet.getByID("1571", "153");//制卡日期
			makeCardDate = Charset.isEmpty(makeCardDate) ? makeCardDate : makeCardDate.replaceAll("-|:|\\s", "");
			String frozenDate = packageDataSet.getByID("1565", "153");//冻结日期
			frozenDate = Charset.isEmpty(frozenDate) ? frozenDate : frozenDate.replaceAll("-|:|\\s", "");
			String unFrozenDate = packageDataSet.getByID("1566", "153");//解冻日期
			unFrozenDate = Charset.isEmpty(unFrozenDate) ? unFrozenDate : unFrozenDate.replaceAll("-|:|\\s", "");
			String lastDate = packageDataSet.getByID("1576", "153");//最后交易日期
			lastDate = Charset.isEmpty(lastDate) ? lastDate : lastDate.replaceAll("-|:|\\s", "");
			String cardHolderIdType = packageDataSet.getByID("1591", "153");//持卡人身份证件类型
			String id = packageDataSet.getByID("1592", "153");//身份证件号码
			String phone = packageDataSet.getByID("1593", "153");//手机号码
			
			String cardAmtRe = "";
			if(cardAmtFen != null &&!cardAmtFen.equals("")){
				String cardAmtYuan = CurrencyTool.fen2Yuan(cardAmtFen, 0);
				cardAmtRe = DictTranslationUtils.dictReverseTranslation(cardAmtYuan, "INF_CARDAMT");
			}
			
			resp = new DpCardWithoutPwdResponse();
			
			//更新日志
			TInfDcoperlogDao.update(pk, responseCode, responseContent);
			
			return resp.toXMLStr("SUCCESS", keep,svcInfName, responseCode, responseContent, 
					cardSeq, cardType, cardAmtRe, returnCardNo, balance, availBalance, expDate,
					frozenBal, serviceFee, cardStat, cardTypeName, cardHolderName, makeCardDate,
					frozenDate, unFrozenDate, lastDate, cardHolderIdType, id, phone);
			
		} catch (XmlINFException spe) {
			return ExceptionHandler.toXML(spe, logId);
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e), logId);
		}
		
		
	}
	
}
