package websvc.servant;

import common.dao.TInfDcoperlogDao;
import common.dao.TScsQueryDtlDao;
import common.dao.TScsQueryRecDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.bean.SocketConfig;
import common.platform.provider.server.PackageDataSet;
import common.utils.Charset;
import common.utils.CurrencyTool;
import common.utils.DateTool;
import common.utils.DictTranslationUtils;
import common.utils.Merchant;
import common.utils.MerchantCache;
import common.utils.SeqTool;
import common.utils.WebSvcTool;
import common.xml.dp.DpCardOrderRequest;
import common.xml.dp.DpCardOrderResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *  卡预定接口
 */
public class SVC_100002 {

	 public static String svcInfName = "100002";
	
	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		Long pk = null;
		DpCardOrderRequest cardOrderRequest = null;
		String agentCode = null;   //代理商编码
		String cardType = null;    //卡类别
		String cardAmt = null;     //卡面值
		String orderNum = null;    //购买数量
		String tradeSeq = null;    //交易流水号
		String subCardType = null; //卡子类型
		String cardPrefix = null;  //卡前缀
		String keep = null;
		
		DpCardOrderResponse resp = new DpCardOrderResponse();
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			cardOrderRequest = new DpCardOrderRequest(in1);
			
			agentCode = cardOrderRequest.getAgentCode();
			cardType = cardOrderRequest.getCardType();
			cardAmt = cardOrderRequest.getCardAmt();
			orderNum = cardOrderRequest.getOrderNum();
			tradeSeq = cardOrderRequest.getTradeSeq();
			subCardType = cardOrderRequest.getSubCardType();
			cardPrefix = cardOrderRequest.getCardPrefix();
			keep = cardOrderRequest.getKeep();
			
			//检查流水号是否存在,存在则抛错
			boolean exist = WebSvcTool.checkTradeSeqAndAgent(tradeSeq, agentCode);
			
			if(exist){
				throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
						INFErrorDef.POSSEQNO_CONFLICT_REASON);
			}
			
			//写日志 
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "PCM0010", SocketConfig.getSockIp(),
					"TRADESEQ", tradeSeq,"AGENTCODE",agentCode);
			id.setPk(pk);
			//得到sp商户信息
			Merchant spinfo = MerchantCache.getSpInfo(agentCode);
			
			//得到预订的总金额
			long cardAmtValue = 0L;
			String cardAmtYuan = DictTranslationUtils.dictTranslation(cardAmt,"INF_CARDAMT");
			
			if(cardAmtYuan != null){
				cardAmtValue = Long.parseLong(cardAmtYuan);
			}
			String cardAmtFen = "";
			if(cardAmtYuan != null &&!cardAmtYuan.equals("")){
				cardAmtFen = CurrencyTool.yuan2Fen(cardAmtYuan);
			}
			
			long amount = cardAmtValue*Long.parseLong(orderNum);
			
			
			//得到卡预订流水号
			String SQ_INF_PKTSEQ = SeqTool.getSqInfPktseq();
			String acceptseqNo = Charset.lpad(SQ_INF_PKTSEQ, 6, "0"); 
			String timeStamp1 = DateTool.getCurDate1();
            String cardOrderSeq= timeStamp1 + acceptseqNo;

            //商户终端号
            String termNo = spinfo.getTermNo();
            
//          得到客户编码
            String custCode = spinfo.getCustCode(); 
            
			
//			调用PCM0010
			IParamGroup g153 = new ParamGroupImpl("153");// 包头
			
			g153.put("1501", orderNum);//购买数量
			g153.put("1533", subCardType);//卡类型
			g153.put("1589", cardType);//卡类别
			g153.put("1587", cardPrefix);//卡前缀
			g153.put("1539", cardAmtFen);//卡面值
			g153.put("1556", spinfo.getCustCode());//操作员工号
			g153.put("1562", agentCode);//代理商编号
			g153.put("1551", cardOrderSeq);//预订流水号
			
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet packageDataSet = caller.call("PCM0010",g153);// 组成交易数据包,调用PCM0010接口
			
			String responseCode = packageDataSet.getByID("0001", "000");// 获取接口的000组的0001参数
			// 返回响应码
			String responseDesc = packageDataSet.getByID("0002", "000");// 获取接口的000组的0002参数
			
			String cardOrderSeqRe = packageDataSet.getByID("1551", "153");//预订流水号

			//更新日志
			TInfDcoperlogDao.update(pk, responseCode, responseDesc);
			
			//插入Query rec记录
			long recPk = TScsQueryRecDao.insert(amount, tradeSeq, termNo, custCode, agentCode, cardOrderSeqRe);

			//更新日志
			TInfDcoperlogDao.update(pk, responseCode, responseDesc);
			
			//插入Query dtl记录
			TScsQueryDtlDao.insert(recPk, 1, "Y001", "卡类型", "01", cardType);
			
			TScsQueryDtlDao.insert(recPk, 2, "Y002", "卡面值", "11", String.valueOf(cardAmtValue));
			
			TScsQueryDtlDao.insert(recPk, 3, "Y003", "购买数量", "01", orderNum);
			
			TScsQueryDtlDao.insert(recPk, 4, "Y004", "卡子类型", "01", subCardType);
			
		
			return resp.toCommonXmlStr(svcInfName, "10", keep, "SUCCESS", responseCode, responseDesc);
			
		} catch (XmlINFException spe) {
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e), id);
		}
		
		
	}
	
}
