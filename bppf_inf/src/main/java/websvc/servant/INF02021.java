package websvc.servant;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfConsumeDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
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
import common.service.TransManage;
import common.utils.ChannelCode;
import common.utils.DateTime;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.PasswordUtil;
import common.utils.PrivConstant;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.utils.WebSvcUtil;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02021Request;
import common.xml.dp.DpInf02021Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02021 {

	public static String svcInfName = "INF02021";

	public static String executeForMD5(String in0, String in1) {

		DpInf02021Response resp = new DpInf02021Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf02021Request dpRequest = new DpInf02021Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {

				String tokenValidTime = TSymSysParamDao.getTokenValidTime();

				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest
						.getStaffCode(), tokenValidTime);

				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);

				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest
						.getStaffCode());

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

		DpInf02021Request dpRequest = null;

		RespInfo respInfo = null; // 返回信息头

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		DpInf02021Response resp = null;

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		Long consumId = null;

		try {

			dpRequest = new DpInf02021Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode()); // 返回信息头

			TInfOperInLogManager manager = new TInfOperInLogManager();
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

			TransManage transManage = new TransManage();

			// IPOS处理
			if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())) {
				
				//密码鉴权
				PasswordUtil.AuthenticationPassWord1(dpRequest, dpRequest.getStaffCode(), dpRequest.getPayPassword(), dpRequest.getECardNo(), dpRequest.getPsamCardNo(), dpRequest.getPassFlag());
				
				Map<String, String> map = transManage
						.getCustCodeByExtTermNumNo(dpRequest.getTmnNumNo());
				if (map != null && map.size() != 0) {
					String custCode = map.get("CUST_CODE");
					String tmnNumNo = map.get("TERM_CODE");
					dpRequest.setCustCode(custCode);
					dpRequest.setTmnNum(tmnNumNo);
				} else {
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_DESC);
				}
			}

			// 判断有无交易查询权限
			List privList = PayCompetenceManage
					.payFunc(dpRequest.getCustCode());
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.IPOS_RECHARGE_3G.equals(str)) {
					r = true;
					break;
				}

			}

			if (!r) {
				throw new Exception("你没有3G流量卡充值的权限");
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

			// 业务编码
			String actionCode = "03010020";

			// 产品编码
			String proudCode = "0042";
			
			String resultCode = "";

			String responseDesc = "";

			String transSeq = "";

			if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())||"9".equals(dpRequest.getPayType())) {

				Hashtable<String,Object> m = process2(dpRequest,actionCode,proudCode);

				resultCode = (String) m.get("ResultCode");

				responseDesc = (String) m.get("ResponseDesc");

				transSeq = (String) m.get("TransSeq");

			} else {

				Hashtable<String,Object> m = process1(dpRequest,actionCode,proudCode);

				resultCode = (String) m.get("ResultCode");

				responseDesc = (String) m.get("ResponseDesc");

				transSeq = (String) m.get("TransSeq");
				
				consumId = (Long) m.get("ConsumId");

			}

			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
					dpRequest.getKeep(), dpRequest.getIp(), svcCode, "",
					"000000", "S0A");

			// 返回结果
			resp = new DpInf02021Response();

			// 单位转换：元转分
			String txanAmount = MathTool.yuanToPoint(dpRequest.getTxnAmount());

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS", resultCode,
					responseDesc, dpRequest.getOrderNo(), transSeq, dpRequest
							.getPhone(), dpRequest.getRechargeFlow(),
					txanAmount, dpRequest.getRemark1(), dpRequest.getRemark2());

			return oXml;

		} catch (XmlINFException spe) {
			// if (tInfOperInLog != null) {
			// // 插入信息到出站日志表
			// sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
			// dpRequest.getKeep(), dpRequest.getIp(), svcCode, "",
			// spe.getMessage(), "S0A");
			// }

			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, id);

		} catch (Exception e) {
			// if (tInfOperInLog != null) {
			// // 插入信息到出站日志表
			// sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
			// dpRequest.getKeep(), dpRequest.getIp(), svcCode, "", e
			// .getMessage(), "S0A");
			// }
			return ExceptionHandler.toConsumeXML(new XmlINFException(resp, e,
					respInfo), id, consumId);

		}

	}

	private static Hashtable<String,Object> process1(DpInf02021Request dpRequest,String actionCode,String proudCode) throws Exception {

		Long consumId = null;
		// 业务组件
		SignBankManage manage = new SignBankManage();

		// 获取客户ID
		String custId = manage.getCustIdByCode(dpRequest.getCustCode());

		if (null == custId || "".equals(custId)) {
			throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
					INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
		}

		// 快捷交易验证
		VerifyConsumeEntity entity = new VerifyConsumeEntity(custId, dpRequest
				.getStaffCode(), dpRequest.getTxnAmount(), dpRequest
				.getPayPassword(), dpRequest.getChannelCode(), dpRequest
				.getTmnNum(), dpRequest.getIp());

		WebSvcUtil websvcutil = new WebSvcUtil();

		boolean flag = websvcutil.VerifyConsume(entity);

		TInfConsumeDao dao = new TInfConsumeDao();

		consumId = dao.getConsumeId();

		PackageDataSet ds = null;

		// 验证手机号码
		if ("00".equals(dpRequest.getVerify())) {

			ds = sag0001(dpRequest, actionCode, proudCode);
		}

		String responseDesc = "";

		String resultCode = "";

		// 调用核心接口之前将消费记录插入到消费表
		addConsume(dpRequest, consumId, dao, custId, websvcutil.getSum_stat(),
				actionCode);

		// 单位转换：分转元
		String txnAmount = MathTool.pointToYuan(dpRequest.getTxnAmount());

		dpRequest.setTxnAmount(txnAmount);

		// 充值
		ds = recharge(dpRequest, actionCode, proudCode);

		resultCode = (String) ds.getParamByID("0001", "000").get(0);

		// 返回结果为失败时，获取结果描述
		if (Long.valueOf(resultCode) == 0) {

			responseDesc = (String) ds.getParamByID("0002", "000").get(0);

			dao.updateOrderStat(consumId, OrderConstant.S0C);

			if (flag) {

				dao.updateSumStat(custId);
			}
		}

		String transSeq = ds.getByID("4002", "401");

		Hashtable<String,Object> map = new Hashtable<String,Object>();

		map.put("ResultCode", resultCode);
		map.put("ResponseDesc", responseDesc);
		map.put("TransSeq", transSeq);
		map.put("consumId", consumId);

		return map;

	}

	private static Hashtable<String,Object> process2(DpInf02021Request dpRequest,String actionCode,String proudCode) throws Exception {

		PackageDataSet ds = null;

		// 验证手机号码
		if ("00".equals(dpRequest.getVerify())) {

			ds = sag0001(dpRequest, actionCode, proudCode);
		}

		String responseDesc = "";

		String resultCode = "";

		// 单位转换：分转元
		String txnAmount = MathTool.pointToYuan(dpRequest.getTxnAmount());

		dpRequest.setTxnAmount(txnAmount);

		// 充值
		ds = recharge(dpRequest, actionCode, proudCode);

		resultCode = (String) ds.getParamByID("0001", "000").get(0);

		// 返回结果为失败时，获取结果描述
		if (Long.valueOf(resultCode) == 0) {

			responseDesc = (String) ds.getParamByID("0002", "000").get(0);

		}

		String transSeq = ds.getByID("4002", "401");

		Hashtable<String,Object> map = new Hashtable<String,Object>();

		map.put("ResultCode", resultCode);
		
		map.put("ResponseDesc", responseDesc);
		
		map.put("TransSeq", transSeq);

		return map;

	}

	private static void addConsume(DpInf02021Request dpRequest, Long consumId,
			TInfConsumeDao dao, String custId, String stat, String actionCode) {

		TInfConsume c = new TInfConsume();

		c.setConsumeId(consumId);

		c.setCustId(Long.valueOf(custId));

		c.setOrderNo(dpRequest.getOrderNo());

		c.setAcctType("0007");

		c.setKeep(dpRequest.getKeep());

		c.setChannelType(dpRequest.getChannelCode());

		c.setTermId(dpRequest.getTmnNum());

		c.setActionCode(actionCode);

		c.setPdLineId(String.valueOf(dao.getPdlineId()));

		c.setAmount(dpRequest.getTxnAmount());

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
	private static PackageDataSet sag0001(DpInf02021Request dpRequest,
			String actionCode, String proudCode) throws Exception {

		/**
		 * 调用SAG0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "GTC201");// 
		g675.put("6752", dpRequest.getChannelCode60To20());// 

//		g675.put("6753", dpRequest.getTmnNum()
//				+ SagUtils.getSeqNbr("yyyyMMddhhmmss", 4));// 
		g675.put("6753", (dpRequest.getKeep()+"0"));// 方便日志查询
		g675.put("6754", DateTime.nowDate8Bit());// 
		g675.put("6755", DateTime.nowTime6Bit());// 
		g675.put("6756", "INF");// 

		g675.endRow();

		// 订单费用信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", actionCode);// 
		g676.put("6762", proudCode);// 
		g676.put("6763", "000000");// 
		g676.put("6764", dpRequest.getMerId());// 
		g676.put("6765", dpRequest.getTmnNum());// 

		g676.endRow();

		// 业务单信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", dpRequest.getPhone());// 

		g680.put("6802", "0");// 

		g680.put("6803", "");// 

		g680.endRow();

		// 业务单费用信息
		IParamGroup g682 = new ParamGroupImpl("682");

		g682.put("6820", "G002");//
		g682.put("6821", "卡类型");// 
		g682.put("6822", "01");// 
		g682.put("6823", dpRequest.getRechargeType());// 

		g682.endRow();

		// 组成数据包,调用SAG0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SAG", "SAG0001", g675, g676,
				g680, g682);

		// 返回结果
		return dataSet;

	}

	/**
	 * 调用SCS0001接口充值
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet recharge(DpInf02021Request dpRequest,
			String actionCode, String proudCode) throws Exception {

		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 银行帐号
		// String bankCode = acctDao.getBankCode(acctCode); // 银行编码[通过银行帐号查询]

		String areacode = dpRequest.getAcceptAreaCode();
		if(areacode==null||areacode.equals("")){
			TCumInfoDao infoDao = new TCumInfoDao();
			areacode = infoDao.getAreaCode(dpRequest.getCustCode());
		}

		// area_code = "440100";

		String bankCode = "110000";

		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单

		g401.put("4006", areacode);// 所属区域编码 广州地区440100
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", dpRequest.getTradeTime());// 受理时间
		
		if(dpRequest.getPayType().equals("9")){
			g401.put("4012", "现金支付-3G流量卡充值");// 订单描述
		}else{
			g401.put("4012", "3G流量卡充值");// 订单描述
		}
		
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode60To20());// 渠道类型编码

		g401.put("4017", dpRequest.getKeep());// 
		g401.put("4018", dpRequest.getTmnNum());// 受理终端号
		g401.put("4028", dpRequest.getOrderNo());// 

		// g401.put("4284", dpRequest.getMerId());//机构编码 //20130628 wanght
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", dpRequest.getTxnAmount());// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", dpRequest.getTxnAmount());// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号

		g404.put("4049", proudCode);// 产品编码 // 全国新宽联0007 腾讯QQ 0031 改0039 电子售卡
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", dpRequest.getPhone());// 充值号码
		g404.put("4062", dpRequest.getSystemNO());// 业务系统参考号
		g404.put("4053", "1");// 业务数量
		g404.put("4072", actionCode);
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", dpRequest.getTxnAmount());// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", dpRequest.getTxnAmount());// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		// 业务单费用信息
		IParamGroup g406 = new ParamGroupImpl("406");
		g406.put("4047", "1");// 业务单序号
		g406.put("4049", proudCode);// 产品编码
		g406.put("4077", "SCS_QUERYAREACODE");// 
		g406.put("4078", areacode);//

		g406.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_USERTYPE");
		g407.put("4088", "0");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE1");
		g407.put("4088", "G001");
		g407.put("4089", "充值流量");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getRechargeFlow());
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE2");
		g407.put("4088", "G002");
		g407.put("4089", "卡类型");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getRechargeType()); //
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE3");
		g407.put("4088", "G003");
		g407.put("4089", "充值卡密");
		g407.put("4091", "01");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0200");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		if("9".equals(dpRequest.getPayType())){
			g408.put("4097", "PT2001");// 支付方式编码
		}else{
			g408.put("4097", "PT0004");// 支付方式编码
		}
		g408.put("4127", bankCode);// 前置支付机构,查卡表的bankcode
		g408.put("4098", bankCode);// 
		g408.put("4099", "0007");// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", dpRequest.getPayPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", dpRequest.getTxnAmount());// 支付金额

		// g408.put("4109", "0003");// 国际网络号

		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g406, g407, g408);

		// 返回结果
		return dataSet;

	}

}
