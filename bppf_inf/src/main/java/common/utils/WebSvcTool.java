package common.utils;

import java.text.SimpleDateFormat;

import common.dao.TCumInfoDaoTemp;
import common.dao.TInfDcoperlogDao;
import common.dao.TPcmFailCountDao;
import common.dao.TScsOrderDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.invoker.exception.ServiceInvokeException;
import common.platform.provider.server.PackageDataSet;
import common.service.CardManager;

import framework.exception.INFErrorDef;
import framework.exception.INFException;

public class WebSvcTool {
	
	/*
	 * 获取服务编码
	 */
	public static String getSvcCode(String in0){
		String[] svcCodes = in0.split("\\|");
		if(svcCodes.length>0)
			return svcCodes[0];
		else
			return "";
		
	}
	
	/**
	 * 根据partnerId 转换成custCode 查询不到 则传递partnerId
	 */
	public static String convertPartnerId(String partnerId) {
		
		String custCode = TCumInfoDaoTemp.getCustCodeByPartnerId(partnerId);
		
		return Charset.isEmpty(custCode) ? "" : custCode ;
	}
	
	public static void checkStartEndDate(String startDate, String endDate, String format) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			df.parse(startDate);
			df.parse(endDate);
		} catch (Exception e) {
			throw new INFException(INFErrorDef.INF_DateFormatWrong_ERRCODE,"日期不符合格式" + format);
		}
		
		if (startDate.length() != format.length() || endDate.length() != format.length()) {
			throw new INFException(INFErrorDef.INF_DateFormatWrong_ERRCODE,"日期不符合格式" + format); 
		}
		
		int s = Integer.valueOf(startDate);
		int e = Integer.valueOf(endDate);
		if (s > e) throw new INFException(INFErrorDef.INF_FromdateBiggerThanTodate_ERRCODE,"开始日期不能大于结束日期");
	}
	
	
	public static void checkPosSeqNo(String seq) throws INFException {
		// 检查POSSEQNO是否重复
		if (TInfDcoperlogDao.isPosSeqNoExists(seq)) {
			throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
					INFErrorDef.POSSEQNO_CONFLICT_REASON);
		}
	}
	//检查商户订单号
	public static void checkPartnerOrderId(String partnerOrderId,String partnerId) throws INFException {
		if (TInfDcoperlogDao.isPartnerOrderIdExists(partnerOrderId, partnerId)) {
			throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
					INFErrorDef.POSSEQNO_CONFLICT_REASON);
		}
	}
	
	//检查代理商工作流
	public static boolean checkTradeSeqAndAgent(String tradeSeq, String agentCode) throws INFException {
		return TInfDcoperlogDao.isTradeSeqExists(tradeSeq, agentCode);
		
	}
	
	public static boolean checkRequestSeqAndAgent(String tradeSeq, String agentCode) throws INFException {
		return TInfDcoperlogDao.isRequestSeqExists(tradeSeq, agentCode);
		
	}
	
	
	//检查水电煤的id是否重复
	public static void checkWegId(String seq) throws INFException {
		if (TInfDcoperlogDao.isWegIdExists(seq)) {
			throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
					INFErrorDef.POSSEQNO_CONFLICT_REASON);
		}
	}
	
	//检查客户端流水号唯一性
	public static void checkSeqNo(String seq) throws INFException {
		if (TInfDcoperlogDao.isSeqNoExists(seq)) {
			throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
					INFErrorDef.POSSEQNO_CONFLICT_REASON);
		}
	}
	
	public static PackageDataSet callPCD0002(String cardNo, String cardPwd, String cardType)
			throws Exception {
		if  (!CardManager.isCardExist(cardNo, cardType) ) {
			throw new INFException(INFErrorDef.INF_CARD_NOT_EXIST, "卡号为" + cardNo + "的卡不存在");
		}
		
		PackageDataSet packageDataSet = null;
		IParamGroup g153 = new ParamGroupImpl("153");// 包头
		
		g153.put("1530", cardNo);// 卡号
		g153.put("1535", cardPwd);// 卡密码
		g153.put("1564", cardType);//卡类别 
		
		g153.endRow();// 把当前参数对封装为一行

		IServiceCall caller = new ServiceCallImpl();
		try{
			packageDataSet = caller.call( "PCD0002", g153);// 组成PCD0002平台数据包
		}catch (ServiceInvokeException  e) {
			handleCardPwdFailExep(e, cardNo, cardType);				
			throw e;
		}
		//重置错误次数为0
		TPcmFailCountDao.update(cardNo, 0);
		return packageDataSet;
	}
	
	private static void handleCardPwdFailExep(ServiceInvokeException sie, String cardNo, String cardType) 
		throws Exception {
		if ("1".equals(cardType)) {//天翼预付卡时才进行卡密码错误次数限制
			String code = "";
			if (sie.getDataSet() != null) {
				code = sie.getDataSet().getByID("0001", "000");
			}
			
			if("1530".equals(code)){
				CardManager.cardValidCatch(cardNo, cardType);
			}
		}
	}
	
	public static void callPCM0036(String cardNo, String cardPwd, String cardType)
			throws Exception {
		if  (!CardManager.isCardExist(cardNo, cardType) ) {
			throw new INFException(INFErrorDef.INF_CARD_NOT_EXIST, "卡号为" + cardNo + "的卡不存在");
		}
		
		IParamGroup g153 = new ParamGroupImpl("153");// 包头
		
		g153.put("1530", cardNo);// 卡号
		g153.put("1535", cardPwd);// 卡密码
		g153.put("1564", cardType);//卡类别 
		
		g153.endRow();// 把当前参数对封装为一行
		
		IServiceCall caller = new ServiceCallImpl();
		try{
			caller.call( "PCM0036", g153);// 组成PCD0002平台数据包
		}catch (ServiceInvokeException  e) {
			handleCardPwdFailExep(e, cardNo, cardType);	
			throw e;
		}
		//重置错误次数为0
		TPcmFailCountDao.update(cardNo, 0);
	}
	
	
	public static String getOrderIdByTermSeq(String custCode, String tradeSeq){
		return TScsOrderDao.getOrderIdByTermSeq(custCode, tradeSeq);
		
	}
}
