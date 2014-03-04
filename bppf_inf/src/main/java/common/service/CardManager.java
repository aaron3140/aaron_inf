package common.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import common.dao.TPcmFailCountDao;
import common.dao.TPcmInfoDao;
import common.dao.TPcmVcInfoDao;
import common.dao.TPdmIncludeDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.utils.DateTool;
import framework.exception.INFException;
import framework.exception.INFErrorDef;

/**
 * @author Tisson
 *
 */
public class CardManager {
	
	private static String CARD_STAT_LOCK = "20F";
	
	public static void cardValidCatch(String cardNo, String cardType) throws Exception{
		int limit = 0;
		String limitStr = TPdmIncludeDao.getTimesForWrongPwdLimit();
		if(limitStr != null &&limitStr != ""){
			limit = Integer.parseInt(limitStr);
		}
		List failCountList = TPcmFailCountDao.getFailCount(cardNo);
		int failCount = 0;
		//一卡仅一条failCout记录
		if(failCountList != null && failCountList.size() == 1){
			Map failCountMap = (Map)failCountList.get(0);
			failCount = Integer.parseInt(failCountMap.get("fail_count").toString());	
			Date countDate = (Date)failCountMap.get("count_date");
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
			String countDateStr = simpleDateFormat.format(countDate);
			if(DateTool.getCurDate1().equals(countDateStr)){
				TPcmFailCountDao.update(cardNo, ++failCount);
			}else{
				TPcmFailCountDao.update(cardNo, 1);
				failCount = 1;
			}
			
			
		}else if(failCountList == null || failCountList.isEmpty()){
			TPcmFailCountDao.insert(cardNo, 1);
			failCount = 1;
		}
		
		if(failCount > limit){
			String cardStat = getCardStat(cardNo, cardType);
			
			if (!CARD_STAT_LOCK.equals(cardStat)) {
				//调用PCM0010
				IParamGroup g = new ParamGroupImpl("153");// 包头
				g.put("1530", cardNo);// 卡号
				g.put("1556", "PCPF_INF");// 员工号
				g.put("1542", cardStat);//TODO 卡状态标识 
				g.put("1505", "密码当天连续错误次数超过" +limit +"次，卡自动锁定");// 锁卡原因
				g.put("1559", "1");// 锁卡原因
				g.endRow();
				IServiceCall svcCall = new ServiceCallImpl();
				svcCall.call("PCM0013", g);
			}
			
			throw new INFException(INFErrorDef.CARD_PWD_VALID_FAIL, "卡密码当天连续错误次数超过" + limit + "次，卡已被锁定");
		}
		
		throw new INFException(INFErrorDef.CARD_PWD_VALID_FAIL, "卡密码输入错误，当天连续错误次数为" + failCount + "，超过" + limit + "次则进行锁卡");
		
	}
	
	public static String getCardStat(String cardNo, String cardType) {
		String cardStat = "";
		if ("1".equals(cardType)) {
			//查询原卡状态
			cardStat = TPcmInfoDao.getCardState(cardNo);
		} else if ("2".equals(cardType)) {
			cardStat = TPcmVcInfoDao.getCardState(cardNo);
		} else {
			cardStat = TPcmInfoDao.getCardState(cardNo);
		}
		
		return cardStat;
	}
	
	public static boolean isCardExist(String cardNo, String cardType) {
		if ("1".equals(cardType)) {
			return TPcmInfoDao.isCardExist(cardNo) ;
		} else if ("2".equals(cardType)) {
			return TPcmVcInfoDao.isCardExist(cardNo);
		} else {
			return TPcmInfoDao.isCardExist(cardNo)  || TPcmVcInfoDao.isCardExist(cardNo);
		}
	}


}
