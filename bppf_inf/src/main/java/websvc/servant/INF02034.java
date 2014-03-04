package websvc.servant;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfConsumeDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TPnmPartnerDao;
import common.dao.TSymSysParamDao;
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
import common.utils.ChannelCode;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.PrivConstant;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.utils.WebSvcUtil;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02034Request;
import common.xml.dp.DpInf02034Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02034 {

	public static String svcInfName = "INF02034";

	private static final Log logger = LogFactory.getLog(INF02034.class);
	
	public static String executeForMD5(String in0, String in1) {

		DpInf02034Response resp = new DpInf02034Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf02034Request dpRequest = new DpInf02034Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {

				String tokenValidTime = TSymSysParamDao.getTokenValidTime();

				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest
						.getStaffCode(), tokenValidTime);

				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);

				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest
						.getStaffCode());
				
				//密码鉴权
//				PasswordUtil.AuthenticationPassWord3(dpRequest, dpRequest.getStaffCode(), dpRequest.getPayPassword());

			}

			String oldXml = execute(in0, in1);

			return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);

		} catch (Exception e) {
			String oldXml = ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), null);

			return ResponseVerifyAdder.pkgForMD5(oldXml, null);
		}

	}

	public static String execute(String in0, String in1) {

		DpInf02034Request dpRequest = null;

		RespInfo respInfo = null; // 返回信息头

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		DpInf02034Response resp = null;

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		Long consumId = null;

		try {

			dpRequest = new DpInf02034Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode()); // 返回信息头

			// 判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(
					dpRequest.getCustCode(), ChannelCode.AGENT_CHANELCODE);
			boolean re = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.CLN_QQ_SHIP.equals(str)) {
					re = true;
					break;
				}

			}

			if (!re) {
				throw new Exception("你没有QQ发货的权限");
			}

			TInfOperInLogManager manager = new TInfOperInLogManager();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"agentCode", dpRequest.getCustCode(), "", "", "S0A");

			// 判断插入是否成功
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

			// 业务组件
			SignBankManage manage = new SignBankManage();

			// 获取客户ID
			String custId = manage.getCustIdByCode(dpRequest.getCustCode());

			if (null == custId || "".equals(custId)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
						INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
			}

			if (Double.valueOf(dpRequest.getTxtAmount()) < 1) {
				throw new Exception("金额不能少于1分钱");
			}
			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if (!(dpRequest.getTradeTime().matches(RegexTool.DATE_FORMAT))) {

				throw new Exception("输入日期格式不正确");
			}

			String actionCode = "07010002";
			
			String channelCode=dpRequest.getChannelCode();
			
			Hashtable ht=null;
			
			//-----------------------------------------------------------------
			TCumInfoDao infoDao = new TCumInfoDao();
			
			String areaCode = infoDao.getAreaCode(dpRequest.getCustCode());
			
			//计算差额
			String concession = scs0201(dpRequest,actionCode,areaCode);
			
			DecimalFormat df = new DecimalFormat("#.00");//保留两位小数
			String payMoney=df.format(Double.parseDouble(dpRequest.getTxtAmount())-Double.parseDouble(concession));//订单应付金额=原始金额-差额
			//-------------------------------------------------------------------
			
			if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				
				 ht = process20(dpRequest, actionCode, custId,payMoney,concession,areaCode);
				 
			}else {
				 ht = process80(dpRequest, actionCode,payMoney,concession,areaCode);
			}
			
			String responseCode = (String) ht.get("ResultCode");

			String responseDesc = (String) ht.get("ResponseDesc");

			consumId = (Long) ht.get("ConsumId");

			PackageDataSet ds = (PackageDataSet) ht.get("DateSet");

			String transSeq = (String) ds.getParamByID("4002", "401").get(0);

			String txtMount = dpRequest.getTxtAmount();// (String)
														// ds.getParamByID("6303",
														// "600").get(0);

			// 单位转换：元转分
			txtMount = MathTool.yuanToPoint(txtMount);

			// 插入信息到出站日志表
			// sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
			// dpRequest.getKeep(), dpRequest.getIp(), svcCode, "",
			// "000000", "S0A");

			// 返回结果
			resp = new DpInf02034Response();

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dpRequest.getOrderSeq(),
					transSeq, dpRequest.getSystemNo(), txtMount, dpRequest
							.getRemark1(), dpRequest.getRemark2());

			return oXml;

		} catch (XmlINFException spe) {

			spe.setRespInfo(respInfo);

			String oXml = ExceptionHandler.toXML(spe, id);

			return oXml;

		} catch (Exception e) {

			String oXml = ExceptionHandler.toConsumeXML(new XmlINFException(
					resp, e, respInfo), id, consumId);

			return oXml;
		}
	}
	
	/**
	 * 20端口调用
	 * @param dpRequest
	 * @param actionCode
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	private static Hashtable<String, Object> process20(
			DpInf02034Request dpRequest, String actionCode, String custId,String payMoney,String concession,String areaCode)
			throws Exception {

		Long consumId = null;

		// 快捷交易验证
		VerifyConsumeEntity entity = new VerifyConsumeEntity(custId, dpRequest
				.getStaffCode(), payMoney, dpRequest
				.getPassword(), dpRequest.getChannelCode(), dpRequest
				.getTmnNum(), dpRequest.getIp());

		WebSvcUtil websvcutil = new WebSvcUtil();

		boolean flag = websvcutil.VerifyConsume(entity);

		TInfConsumeDao dao = new TInfConsumeDao();

		consumId = dao.getConsumeId();

		String responseDesc = "";

		String resultCode = "";

		// 调用核心接口之前将消费记录插入到消费表
		addConsume(dpRequest, consumId, dao, custId, websvcutil.getSum_stat(),
				actionCode,payMoney);
		
		// 单位转换：分转元
		String txtAmount = MathTool.pointToYuan(dpRequest.getTxtAmount());

		dpRequest.setTxtAmount(txtAmount);
		
		// 通过客户编码查区域编码
		String bankCode = "110000";
		
		// 充值
		PackageDataSet ds = scs0001(dpRequest, actionCode,areaCode,bankCode,payMoney,concession);

		resultCode = (String) ds.getParamByID("0001", "000").get(0);

		// 返回结果为失败时，获取结果描述
		if (Long.valueOf(resultCode) == 0) {

			responseDesc = (String) ds.getParamByID("0002", "000").get(0);

			dao.updateOrderStat(consumId, OrderConstant.S0C);

			if (flag) {

				dao.updateSumStat(custId);
			}
		}

		Hashtable<String, Object> map = new Hashtable<String, Object>();

		map.put("ResultCode", resultCode);

		map.put("ResponseDesc", responseDesc);

		map.put("ConsumId", consumId);

		map.put("DateSet", ds);

		return map;

	}
	
	/**
	 * 80和其他端口调用
	 * @param dpRequest
	 * @param actionCode
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	private static Hashtable<String, Object> process80(
			DpInf02034Request dpRequest, String actionCode,String payMoney,String concession,String areaCode)
			throws Exception {

		Long consumId = new Long(0);

		String responseDesc = "";

		String resultCode = "";
		
		String txtAmount = dpRequest.getTxtAmount();
		
		// 单位转换：分转元
		dpRequest.setTxtAmount(MathTool.pointToYuan(txtAmount));
		// 通过客户编码查区域编码
		String bankCode = "110000";
		
		// 充值
		PackageDataSet ds = scs0001(dpRequest, actionCode,areaCode,bankCode,payMoney,concession);

		resultCode = (String) ds.getParamByID("0001", "000").get(0);

		// 返回结果为失败时，获取结果描述
		if (Long.valueOf(resultCode) == 0) {

			responseDesc = (String) ds.getParamByID("0002", "000").get(0);

		}

		Hashtable<String, Object> map = new Hashtable<String, Object>();

		map.put("ResultCode", resultCode);

		map.put("ResponseDesc", responseDesc);

		map.put("ConsumId", consumId);

		map.put("DateSet", ds);

		return map;

	}
	
	private static void addConsume(DpInf02034Request dpRequest, Long consumId,
			TInfConsumeDao dao, String custId, String stat, String actionCode,String payAmount) {

		TInfConsume c = new TInfConsume();

		c.setConsumeId(consumId);

		c.setCustId(Long.valueOf(custId));

		c.setOrderNo(dpRequest.getOrderSeq());

		c.setAcctType("0007");

		c.setKeep(dpRequest.getKeep());

		c.setChannelType(dpRequest.getChannelCode());

		c.setTermId(dpRequest.getTmnNum());

		c.setActionCode(actionCode);

		c.setPdLineId(String.valueOf(dao.getPdlineId()));

		c.setAmount(payAmount);

		c.setStat(OrderConstant.S0A);

		c.setAcctDate(new Date());

		c.setRemark(dpRequest.getRemark1() + "::" + dpRequest.getRemark2());

		c.setSum_stat(stat);

		dao.insert(c);
	}

	/**
	 * 调用SCS0001接口充值
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static String scs0201(DpInf02034Request dpRequest,
			String actionCode,String areaCode) throws Exception {
		
		// 单位转换：分转元
		String amount = MathTool.pointToYuan(dpRequest.getTxtAmount());
		
		TPnmPartnerDao dao = new TPnmPartnerDao();
		Map<String, String> map = dao.getPrntnCodeAndPrntType(dpRequest
				.getCustCode());
		
		IParamGroup g423 = new ParamGroupImpl("423");
		g423.put("4230", "0002"); 	//手续费
		g423.put("2011", dpRequest.getMerId());	//接入机构的对应的商户编码
		g423.put("4330", map.get("PRTN_CODE"));	//实际做交易的商户编码
		g423.put("4331", map.get("PRTN_TYPE"));	//实际做交易的商户类型
		g423.put("2002", dpRequest.getCustCode());	//实际做交易的商户编码
		g423.put("4051", actionCode);	//业务编码
		g423.put("4049", dpRequest.getProductCode());	//产品编码
		g423.put("4098", "110000");  //银行编码
		g423.put("4006", areaCode);  //区域编码
		g423.put("4144", dpRequest.getChannelCode());  //渠道号
		g423.endRow();
		
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4025", amount);	//订单金额
		
		g402.put("4099", "0007");	//账户类型编码
		
		String concession ="0";
		
		try{
			// 组成数据包,调用SCS0201接口
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet dataSet = caller.call("SCS", "SCS0201", g423, g402);
			
			String resCode = dataSet.getByID("0001", "000");
			
			if (Long.valueOf(resCode) == 0) {
				String flagStr = dataSet.getByID("4230", "423");
				if ("0002".equals(flagStr)) {
					concession = dataSet.getByID("4329", "423");
					if (concession == null || "".equals(concession)
							|| Double.valueOf(concession) == 0) {
						concession = "0";
					}
				}
			}
		}catch(Exception e){}
		
		// 返回结果  单位转换：元转分
		return MathTool.yuanToPoint(concession);

	}
	
	/**
	 * 调用SCS0001接口充值
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet scs0001(DpInf02034Request dpRequest,
			String actionCode,String areaCode,String bankCode,String payMoney,String concession) throws Exception {

		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 企业帐号
		
		payMoney = MathTool.pointToYuan(payMoney);
		
		concession = MathTool.pointToYuan(concession);

		// String actionCode="07010002";
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码 广州地区440100
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", dpRequest.getTradeTime());// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 
		g401.put("4018", dpRequest.getTmnNum());// 受理终端号
		g401.put("4012", "腾讯QQ直连");// 订单描述
		g401.put("4028", dpRequest.getOrderSeq());// 外部订单号
		g401.endRow();
		
//		DecimalFormat df = new DecimalFormat("#.00");//保留两位小数
//		String payMoney=df.format(Double.parseDouble(dpRequest.getTxtAmount())-Double.parseDouble(concession));//订单应付金额=原始金额-差额
		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", dpRequest.getTxtAmount());// 订单原始金额
		g402.put("4024", concession);// 订单优惠金额/差额
		g402.put("4025", payMoney);// 订单应付金额
//		g402.put("4030", 0);//溢价金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", dpRequest.getProductCode());// 产品编码 // 全国新宽联0007 腾讯QQ
														// 0031 改0039 电子售卡
		g404.put("4051", actionCode);// 业务编码
//		g404.put("4052", dpRequest.getCustCode());//12121
		g404.put("4052", dpRequest.getAcctCode());//业务对象 QQ账号
		g404.put("4053", "1");// 业务数量
		g404.put("4062", dpRequest.getSystemNo());// 
		g404.put("4072", actionCode);
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", dpRequest.getTxtAmount());// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", dpRequest.getTxtAmount());// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0200");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_USERTYPE");
		g407.put("4088", "2");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE1");
		g407.put("4088", "Q001");
		g407.put("4089", "支付账号");
		g407.put("4091", "01");
		g407.put("4093", acctCode);
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE2");
		g407.put("4088", "Q002");
		g407.put("4089", "扣费结果状态");
		g407.put("4091", "11");
		g407.put("4093", "2000"); //
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE3");
		g407.put("4088", "Q003");
		g407.put("4089", "货物代码");
		g407.put("4091", "01");
		g407.put("4093", "0088");//0076改0088
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_SERVID");
		g407.put("4088", "PQC402");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4127", bankCode);// 前置支付机构,查卡表的bankcode
		g408.put("4098", bankCode);// 
	    g408.put("4099", "0007");// 账户类型编码
//		if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {
//			g408.put("4099", "0007");// 账户类型编码
//		}else{
//			g408.put("4099", "0001");// 账户类型编码
//		}
		
		g408.put("4101", acctCode);// 账号
		g408.put("4102", dpRequest.getPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", payMoney);// 支付金额
		g408.put("4109", "0003");// 国际网络号
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}
}
