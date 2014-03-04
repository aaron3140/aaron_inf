package websvc.servant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import common.dao.TInfDcoperlogDao;
import common.dao.TScsOrderDao;
import common.platform.provider.bean.SocketConfig;
import common.service.CardManager;
import common.utils.Charset;
import common.utils.CurrencyTool;
import common.utils.DictTranslationUtils;
import common.xml.dp.DpPaymentInfoQueryRequest;
import common.xml.dp.DpPaymentInfoQueryResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson
 *	支付明细查询接口
 */
public class SVC_200003 {
	
	 public static String svcInfName = "200003";

	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		Long pk = null;
		DpPaymentInfoQueryRequest paymentInfoQueryRequest = null;
		String cardNo = null;
		String txnType = null;
		String txnChannel = null;
		String startTime = null;
		String endTime = null;
		String actionCode = null;
		String startRecord = null;
		String maxRecord = null;
		
		String keep = null;
		DpPaymentInfoQueryResponse resp = new DpPaymentInfoQueryResponse();
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			paymentInfoQueryRequest = new DpPaymentInfoQueryRequest(in1);
			
			cardNo = paymentInfoQueryRequest.getCardNo();
			txnType = paymentInfoQueryRequest.getTxnType();
			txnChannel = paymentInfoQueryRequest.getTxnChannel();
			startTime = paymentInfoQueryRequest.getStartTime();
			endTime = paymentInfoQueryRequest.getEndTime();
			startRecord = paymentInfoQueryRequest.getStartRecord();
			maxRecord = paymentInfoQueryRequest.getMaxRecord();
			
			keep = paymentInfoQueryRequest.getKeep();
			
			List<String> merchantNameList = new ArrayList<String>();
			//List<String> goodsCodeList = new ArrayList<String>();
			List<String> goodsNameList = new ArrayList<String>();
			List<String> txnChannelList = new ArrayList<String>();
			List<String> txnTypeList = new ArrayList<String>();
			List<String> orderIdList = new ArrayList<String>();
			List<String> tradeSeqList = new ArrayList<String>();
			List<String> tradeTimeList = new ArrayList<String>();
			List<String> txnAmountList = new ArrayList<String>();
			List<String> payStatList = new ArrayList<String>();
			
			
			//写日志 
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "DB", SocketConfig.getSockIp(),"CARDNO", cardNo);
			id.setPk(pk);
			
			
			if  (!Charset.isEmpty(cardNo) && !CardManager.isCardExist(cardNo, null) ) {
				throw new INFException(INFErrorDef.INF_CARD_NOT_EXIST, "卡号为" + cardNo + "的卡不存在");
			}
			
			if(txnType != null){
				actionCode = DictTranslationUtils.dictTranslation(txnType, "INF_CONVACTIONCODE");
			}
			
			//查询支付明细
			List paymentInfos = 
				TScsOrderDao.getOrderForPaymentInfo(cardNo, actionCode, txnChannel,  startTime, endTime, startRecord, maxRecord);
			
			if(paymentInfos !=null &&paymentInfos.size() > 0){
				
				
				Iterator it = paymentInfos.iterator();
				while(it.hasNext()) {
					Map map = (Map) it.next();
				
					merchantNameList.add(map.get("merchantName") == null?"":(String)map.get("merchantName"));
					//goodsCodeList.add((String)map.get("goodsCode"));
					goodsNameList.add(map.get("goodsName") == null?"":(String)map.get("goodsName"));
					txnChannelList.add(map.get("txnChannel") == null?"":(String)map.get("txnChannel"));
					String returnTxnType = "";
					if(map.get("actionCode") != null){
						returnTxnType = 
							DictTranslationUtils.dictReverseTranslation(
									(String)map.get("actionCode"), "INF_CONVACTIONCODE");
					}
					txnTypeList.add(returnTxnType);
					orderIdList.add(map.get("orderId") == null?"":(String)map.get("orderId"));
					tradeSeqList.add(map.get("tradeSeq") == null?"":(String)map.get("tradeSeq"));
					tradeTimeList.add(map.get("tradeTime") == null?"":(String)map.get("tradeTime"));
					//元转分
					String amountFen = CurrencyTool.yuan2Fen(map.get("txnAmount") == null?"":map.get("txnAmount").toString());
					txnAmountList.add(amountFen);
					payStatList.add(map.get("payStat") == null?"":(String)map.get("payStat"));
				}
			}
			
			//更新日志
			TInfDcoperlogDao.update(pk, "000000", "成功");

			return resp.toXMLStr("SUCCESS", keep,svcInfName, "000000", "支付明细查询成功", 
					merchantNameList, goodsNameList, txnChannelList, txnTypeList, orderIdList,
					tradeSeqList, tradeTimeList, txnAmountList, payStatList);
			
		} catch (XmlINFException spe) {
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e), id);
		}
		
		
	}
	
}
