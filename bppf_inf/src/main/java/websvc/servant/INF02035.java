package websvc.servant;

import java.util.Date;
import java.util.List;
import java.util.Map;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfConsumeDao;
import common.dao.TInfDcoperlogDao;
import common.entity.TInfConsume;
import common.entity.TInfOperInLog;
import common.entity.VerifyConsumeEntity;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.PrivConstant;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.utils.WebSvcUtil;
import common.xml.RespInfo;
import common.xml.dp.DpInf02035Response;
import common.xml.dp.DpInf02035Request;
import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02035 {
	
	public static String svcInfName = "INF02035";
	
	public static String execute(String in0, String in1) throws Exception {
			
			return rechargeGHKD(in0,in1);
			
	}
	
	private static String rechargeGHKD(String in0, String in1) throws Exception{
		
		DpInf02035Request dpRequest = null;
		
		DpInf02035Response dpResponse = null;
		
		RespInfo respInfo = null;
		
		TInfOperInLog tInfOperInLog = null;
		
		SagManager sagManager = new SagManager();
		
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		
		Long consumId = null;
		
		String oXml = null;
		try{
		
		dpRequest = new DpInf02035Request(in1);
			
		//返回信息头
		respInfo = new RespInfo(in1, dpRequest.getChannelCode());
			
		TInfOperInLogManager manager = new TInfOperInLogManager();
			
		if (tInfOperInLog != null) {
			
			boolean flag = manager.selectTInfOperInLogByKeep(dpRequest
					.getKeep());
			
			// 判断流水号是否可用
			if (flag) {
				
				flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
				
				throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
						INFErrorDef.POSSEQNO_CONFLICT_REASON);
			} else {
				
				flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				
			}
		}
			
		// 判断有无交易查询权限
		List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode(),dpRequest.getChannelCode());
			
		boolean r = false;
			
		for (int i = 0; i < privList.size(); i++) {
			
			Map map = (Map) privList.get(i);
			
			String str = map.get("PRIV_URL").toString();

			if (PrivConstant.CLN_FIX_REC.equals(str)) {
				r = true;
				break;
			}

		}

		if (!r) {
			throw new Exception("你没有固话宽带充值的权限");
		}
			
		// 插入信息到入站日志表
		tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
				dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
				"agentCode", dpRequest.getCustCode(), "", "", "S0A");
			
		if (Double.valueOf(dpRequest.getTxnAmount()) < 1) {
			throw new Exception("金额不能少于1分钱");
		}
			
		// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
		if (!(dpRequest.getTradeTime().matches(RegexTool.DATE_FORMAT))) {

			throw new Exception("输入日期格式不正确");
		}
			
		//业务编码
//		String actionCode =dpRequest.getBusICode();
		
		PackageDataSet ds = null;
		
		String responseDesc = "";
		
		String resultCode = "";
		
		if(Integer.valueOf(dpRequest.getTxnAmount())%100!=0){
			
			throw new INFException(INFErrorDef. NO_TAX_OF_100, INFErrorDef.NO_TAX_OF_100_DESC);
		}
		
//		boolean flag = checkPassword(dpRequest, consumId);
		

		
		TInfConsumeDao dao = new TInfConsumeDao();
	
		// 业务组件
		SignBankManage manage = new SignBankManage();
		
		consumId = dao.getConsumeId();

		// 获取客户ID
		String custId = manage.getCustIdByCode(dpRequest.getCustCode());
		
		if (null == custId || "".equals(custId)) {
			throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
					INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
		}
		
		// 快捷交易验证
		VerifyConsumeEntity entity = new VerifyConsumeEntity(custId, dpRequest
				.getStaffCode(), dpRequest.getTxnAmount(), dpRequest
				.getPayPassWord(), dpRequest.getChannelCode(), dpRequest
				.getTmnNum(), dpRequest.getIp());
		
		WebSvcUtil websvcutil = new WebSvcUtil();
		
		boolean flag = websvcutil.VerifyConsume(entity);
		
		// 调用核心接口之前将消费记录插入到消费表
		TInfConsume c = new TInfConsume();

		consumId = dao.getConsumeId();
		c.setConsumeId(consumId);
		c.setCustId(Long.valueOf(custId));
		c.setOrderNo(dpRequest.getOrderSeq());
		c.setAcctType("0007");
		c.setKeep(dpRequest.getKeep());
		c.setChannelType(dpRequest.getChannelCode());
		c.setTermId(dpRequest.getTmnNum());
		c.setActionCode("16010001");
		c.setPdLineId(String.valueOf(dao.getPdlineId()));
		c.setAmount(dpRequest.getTxnAmount());
		c.setStat(OrderConstant.S0A);
		c.setAcctDate(new Date());
		c.setRemark(dpRequest.getRemark1() + "::" + dpRequest.getRemark2());
		c.setSum_stat(websvcutil.getSum_stat());
		System.out.println("INF02010===="+websvcutil.getSum_stat());
		dao.insert(c);
		
		// 单位转换：分转元
		dpRequest.setTxnAmount(MathTool.pointToYuan(dpRequest.getTxnAmount()));
		
		ds = scs0001(dpRequest);
			
		resultCode = (String) ds.getParamByID("0001", "000").get(0);
		
		if (Long.valueOf(resultCode) == 0) {

			responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			dao.updateOrderStat(consumId, OrderConstant.S0C);
			
			if(flag){
				dao.updateSumStat(custId);
			}
		}
		
		String transSeq = ds.getByID("4002", "401");
		
		// 插入信息到出站日志表
		sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
				dpRequest.getKeep(), dpRequest.getIp(), svcCode, "",
				"000000", "S0A");
		
		// 返回结果
		dpResponse = new DpInf02035Response();
		
		// 单位转换：元转分
		dpRequest.setTxnAmount(MathTool.yuanToPoint(dpRequest.getTxnAmount()));
		
		oXml = dpResponse.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
				.getRespType(), respInfo.getKeep(), "SUCCESS", resultCode,
				responseDesc, dpRequest.getOrderSeq(), transSeq, dpRequest.getSystemNo()
						, dpRequest.getTxnAmount(),dpRequest.getRemark1(), dpRequest.getRemark2());
		}catch (XmlINFException spe) {
			
			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, id);
		}catch(Exception e){
			
			return ExceptionHandler.toConsumeXML(new XmlINFException(dpResponse, e,
					respInfo), id, consumId);
		}
		return oXml;
	}
	
	/**
	 * 调用SCS0001,完成交易操作
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet scs0001(DpInf02035Request dpRequest) throws Exception {
		
		String areacode = dpRequest.getAcceptAreaCode();
		
		if(areacode==null||areacode.equals("")){
			TCumInfoDao infoDao = new TCumInfoDao();
			areacode = infoDao.getAreaCode(dpRequest.getCustCode());
		}
		
		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode()); // 银行编码[通过银行帐号查询]
		String bankCode = acctDao.getBankCode(acctCode); // 银行编码[通过银行帐号查询]
		
		IParamGroup g401 = new ParamGroupImpl("401");
		// 客户编码
		g401.put("4004", dpRequest.getCustCode());
		// 订单类型编码：业务类订单	
		g401.put("4005", "OT001");
		// 所属区域编码
		g401.put("4006", areacode);
		// 受理终端号
		g401.put("4007", dpRequest.getTmnNum());
		// 受理时间
		g401.put("4008", dpRequest.getTradeTime());
		//订单备注
		g401.put("4012", "全国固话宽带");
		// 客户登录认证方式编码：用户名
		g401.put("4016", "LG001");
		// 渠道类型编码
		g401.put("4144", dpRequest.getChannelCode());
		// 终端流水号
		g401.put("4017", dpRequest.getKeep());
		// 操作原始来源
		g401.put("4018", dpRequest.getTmnNum());
		// 外部订单号
		g401.put("4028", dpRequest.getOrderSeq());
		// 机构编码
		g401.put("4284", dpRequest.getMerId());
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		// 币种编码
		g402.put("4021", "0001");
		// 订单原始金额
		g402.put("4023", dpRequest.getTxnAmount());
		// 订单优惠金额
		g402.put("4024", "0");
		// 订单应付金额
		g402.put("4025", dpRequest.getTxnAmount());
		g402.endRow();
		
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");
		g404.put("4049", "0003");
		// 业务编码
		g404.put("4051", dpRequest.getBusICode());
		// 业务对象
		g404.put("4052", dpRequest.getAcctCode());
		// 业务数量
		g404.put("4053", "1");
		g404.put("4062", dpRequest.getSystemNo());
		g404.put("4072", dpRequest.getBusICode());
		g404.endRow();
		
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");
		g405.put("4021", "0001");
		g405.put("4066", dpRequest.getTxnAmount());
		g405.put("4067", "0");
		g405.put("4068", dpRequest.getTxnAmount());
		g405.put("4071", "103");
		g405.endRow();
		
		// 业务单信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");
		g407.put("4051", dpRequest.getBusICode());
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0200");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");
		g407.endRow();
		
		g407.put("4047", "1");
		g407.put("4051", dpRequest.getBusICode());
		g407.put("4087", "SCS_USERTYPE");
		g407.put("4088", "1");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");
		g407.endRow();
		
		g407.put("4047", "1");
		g407.put("4051", dpRequest.getBusICode());
		g407.put("4087", "SCS_SERVID");
		g407.put("4088", "GTC406");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");
		g407.endRow();
		
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");
		g408.put("4097", "PT0004");
		g408.put("4127", bankCode);
		g408.put("4098", "110000"); 
		g408.put("4099", "0007");
		g408.put("4101", acctCode);
		g408.put("4102", dpRequest.getPayPassWord());
		g408.put("4021", "0001");
		// 支付金额
		g408.put("4104", dpRequest.getTxnAmount()); 
		g408.put("4109", "0003"); 
		g408.put("4119", "");
		g408.endRow();
		
		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
		
		return dataSet;
		
	}
	
//	private static boolean checkPassword(DpInf02035Request dpRequest, Long consumId) throws Exception{
//		
//		TInfConsumeDao dao = new TInfConsumeDao();
//	
//		// 业务组件
//		SignBankManage manage = new SignBankManage();
//		
//		consumId = dao.getConsumeId();
//
//		// 获取客户ID
//		String custId = manage.getCustIdByCode(dpRequest.getCustCode());
//		
//		if (null == custId || "".equals(custId)) {
//			throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
//					INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
//		}
//		
//		// 快捷交易验证
//		VerifyConsumeEntity entity = new VerifyConsumeEntity(custId, dpRequest
//				.getStaffCode(), dpRequest.getTxnAmount(), dpRequest
//				.getPayPassWord(), dpRequest.getChannelCode(), dpRequest
//				.getTmnNum(), dpRequest.getIp());
//		
//		WebSvcUtil websvcutil = new WebSvcUtil();
//		
//		boolean flag = websvcutil.VerifyConsume(entity);
//		
//		// 调用核心接口之前将消费记录插入到消费表
////		TInfConsume tinfConsume = new TInfConsume(dao, custId, dpRequest.getOrderSeq(), dpRequest.getKeep(), dpRequest.getChannelCode(), dpRequest.getTmnNum(), 
////				dpRequest.getBusICode(), dpRequest.getTxnAmount(), dpRequest.getRemark1(), dpRequest.getRemark2(), websvcutil.getSum_stat());
////		
////		tinfConsume.addConsume(tinfConsume,dao);
//		
//		TInfConsume c = new TInfConsume();
//
//		consumId = dao.getConsumeId();
//		c.setConsumeId(consumId);
//		c.setCustId(Long.valueOf(custId));
//		c.setOrderNo(dpRequest.getOrderSeq());
//		c.setAcctType("0007");
//		c.setKeep(dpRequest.getKeep());
//		c.setChannelType(dpRequest.getChannelCode());
//		c.setTermId(dpRequest.getTmnNum());
//		c.setActionCode("16010001");
//		c.setPdLineId(String.valueOf(dao.getPdlineId()));
//		c.setAmount(dpRequest.getTxnAmount());
//		c.setStat(OrderConstant.S0A);
//		c.setAcctDate(new Date());
//		c.setRemark(dpRequest.getRemark1() + "::" + dpRequest.getRemark2());
//		c.setSum_stat(websvcutil.getSum_stat());
//		System.out.println("INF02010===="+websvcutil.getSum_stat());
//		dao.insert(c);
//		
////		if(flag){
////			
////			dao.updateSumStat(custId);
////			
////		}
//		return flag;
//	}
}
