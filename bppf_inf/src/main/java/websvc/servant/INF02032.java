package websvc.servant;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TInfOrderBusCfgDao;
import common.dao.TOppPreOrderDao;
import common.dao.TPnmPartnerDao;
import common.dao.TSymSysParamDao;
import common.dao.TbisTanOrderDao;
import common.entity.BankAcctInfo;
import common.entity.SignOrder;
import common.entity.TInfOperInLog;
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
import common.utils.Charset;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.PasswordUtil;
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02032Request;
import common.xml.dp.DpInf02032Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02032 {

	public static String svcInfName = "INF02032";

	private static final Log logger = LogFactory.getLog(INF02032.class);

	public static String executeForMD5(String in0, String in1) {

		DpInf02032Response resp = new DpInf02032Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf02032Request dpRequest = new DpInf02032Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {

				String tokenValidTime = TSymSysParamDao.getTokenValidTime();

				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest
						.getStaffCode(), tokenValidTime);

				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);

				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest
						.getStaffCode());

				// 密码鉴权
				PasswordUtil.callCUM1003(dpRequest, dpRequest.getStaffCode(),
						dpRequest.getPassword(), "2");

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

		DpInf02032Request dbRequest = null;

		RespInfo respInfo = null; // 返回信息头

		DpInf02032Response resp = new DpInf02032Response();

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		boolean isupdate = false;

		// 转账处理标识
		boolean isTran = false;

		boolean isTransFlag = false;

		TbisTanOrderDao tranDao = new TbisTanOrderDao();

		try {
			dbRequest = new DpInf02032Request(in1);

			respInfo = new RespInfo(in1, dbRequest.getChannelCode()); // 返回信息头

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dbRequest.getKeep(),
					dbRequest.getIp(), dbRequest.getTmnNum(), svcCode, "XML",
					"agentCode", dbRequest.getCustCode(), "orderSeq", dbRequest
							.getOrderSeq(), "S0A");

			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {
				boolean flag = manager.selectTInfOperInLogByKeep(dbRequest
						.getKeep());
				// 判断流水号是否可用
				if (flag) {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {

					// 准予通过
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			// 关联机构验证
			if (ChannelCode.WS_CHANELCODE.equals(dbRequest.getChannelCode())&&!TCumInfoDao.verifyMerIdCustCode(dbRequest.getCustCode(),
					dbRequest.getMerId())) {

				if (TCumInfoDao.getMerIdByCustCode(dbRequest.getCustCode(),
						dbRequest.getMerId()))

					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			}

			// 判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(
					dbRequest.getCustCode(), ChannelCode.WS_CHANELCODE);
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.WS_RECH_TRAN.equals(str)) {
					r = true;
					break;
				}

			}

			if (!r) {
				// throw new Exception("你没有充值转账的权限");
			}

			String responseDesc = "";

			// 业务组件
			SignBankManage manage = new SignBankManage();

			// 获取ActionCode
			String actionCode = SignBankManage.AC_AUTH_BANKCARD_RECHARGE;

			// 获取客户ID
			String custId = manage.getCustIdByCode(dbRequest.getCustCode());
			if (custId == null) {
				throw new Exception("该商户号不存在");
			}
			// 获取客户绑定银行卡列表
			List<BankAcctInfo> bankAcctList = manage.getBankAcctList(custId);
			if (bankAcctList == null || bankAcctList.size() == 0) {
				throw new Exception("该商户的签约银行卡不存在");
			}
			if (bankAcctList.size() != 1) {
				throw new Exception("该商户存在多张签约银行卡");
			}

			BankAcctInfo bankAcctInfo = bankAcctList.get(0);

			// 生成授权银行卡充值单据
			SignOrder order = scs0201(dbRequest, actionCode, bankAcctInfo);

			// 获取手机号码
			String mobile = cum003(dbRequest);

			String buscode = TInfOperInLogManager.convertBussCode("1");

			// 订单控制
			isupdate = TInfOperInLogManager.verifyOrder(
					dbRequest.getOrderSeq(), dbRequest.getKeep(), dbRequest
							.getTmnNum(), svcInfName, buscode);

			TOppPreOrderDao dao=null;
			//付款预处理逻辑
			if (ChannelCode.AGENT_CHANELCODE.equals(dbRequest.getChannelCode())&&"1".equals(dbRequest.getTranType())){
				
				dao= new TOppPreOrderDao();
				
				prePay(dao,dbRequest);
			}
			
			if (dbRequest.getColleCustCode().equals(dbRequest.getCustCode())) {

				throw new Exception("收款方客户编码和付款方客户编码不能一样");
			}

			isTransFlag = true;

			// 收款商户关联机构验证
			if (ChannelCode.WS_CHANELCODE.equals(dbRequest.getChannelCode())&&!TCumInfoDao.verifyMerIdCustCode(dbRequest.getColleCustCode(),
					dbRequest.getMerId())) {

				if (TCumInfoDao.getMerIdByCustCode(
						dbRequest.getColleCustCode(), dbRequest.getMerId()))

					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_R_NOT_MATCH_MERG_DESC);
			}

			logger.info("1..转账预处理");
			Hashtable<String, Object> rt = new Hashtable<String, Object>();
			rt.put("KEEP", dbRequest.getKeep());
			rt.put("ORDER_CODE", dbRequest.getOrderSeq());
			rt.put("PAY_CUSTCODE", dbRequest.getCustCode());
			rt.put("COLLE_CUSTCODE", dbRequest.getColleCustCode());
			rt.put("PAY_MONEY", dbRequest.getTxnamount());
			rt.put("RECHARGE_STAT", OrderConstant.S0P);
			rt.put("TRAN_STAT", OrderConstant.S0A);

			tranDao.saveTanOrder(rt);

			// 调用调用充值接口
			PackageDataSet dataSet = manage.Recharge(dbRequest.getCustCode(),
					order, mobile, dbRequest);

			String responseCode = (String) dataSet.getParamByID("0001", "000")
					.get(0);

			// 更新订单控制状态
			TInfOrderBusCfgDao cfgDao = new TInfOrderBusCfgDao();

			if (isupdate && responseCode.equals("0000")) {

				cfgDao.updateTInfOrderStat(dbRequest.getTmnNum(), dbRequest
						.getOrderSeq(), OrderConstant.S0C);
			}

			responseDesc = dataSet.getByID("0002", "000"); // 响应码描述
			String respTransSeq = dataSet.getByID("4002", "401"); // 订单号
			String txnamount = dataSet.getByID("6303", "600"); // 交易金额

			// 单位转换：元转分
			String finalAmount = MathTool.yuanToPoint(order.getFinalAmount());

			txnamount = MathTool.yuanToPoint(txnamount);

			String concession = "";

			if (!Charset.isEmpty(order.getConcession())) {

				concession = MathTool.yuanToPoint(order.getConcession());
			}

			isTran = true;
			logger.info("3..支付成功 更新");
			// 更新充值状态
			tranDao.updateRecSucStat(dbRequest.getKeep(), respTransSeq,
					responseCode, responseDesc);

			// 调用转账接口
			dataSet = transfer(dbRequest, order);

			String tranOrderId = dataSet.getByID("4002", "401");

			logger.info("4..转账成功 更新");
			// 更新更新成功记录
			tranDao.updateTraOrder(dbRequest.getKeep(), tranOrderId);
			
			//付款状态更新
			if (ChannelCode.AGENT_CHANELCODE.equals(dbRequest.getChannelCode())&&"1".equals(dbRequest.getTranType())){
				
				//把取回的orderid,和处理时间 再更新预处理单表										
				dao.update(tranOrderId, dbRequest.getPreOrderSeq());
			}

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dbRequest.getOrderSeq(),
					respTransSeq, txnamount, finalAmount, concession, dbRequest
							.getRemark1(), dbRequest.getRemark2());

		} catch (XmlINFException spe) {
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {

			if (isTran) {

				return ExceptionHandler.toTanXML(new XmlINFException(resp, e,
						respInfo), id, tranDao);
			} else {

				if (e instanceof SocketTimeoutException) {

					return ExceptionHandler.toXML(new XmlINFException(resp,
							new Exception("调用接口超时"), respInfo), id);
				} else {

					return ExceptionHandler.toOutOrderXML(new XmlINFException(
							resp, e, respInfo), id, null, isupdate, dbRequest
							.getTmnNum(), dbRequest.getOrderSeq(), tranDao,
							isTransFlag);
				}
			}

		}
	}

	private static void prePay(TOppPreOrderDao dao,DpInf02032Request dbRequest) throws Exception{
		
		List list= dao.getTOppPreOrder(dbRequest.getPreOrderSeq());
		Map map = new HashMap();
		if(list.size()>0){
		  map=(Map)list.get(0);
		}else{
			throw new Exception("预受理订单号输入不正确");
		}
		 String payeeCode = (String)map.get("OBJ_CODE");					// 收款商户编码
//		 String custCode2 = (String)map.get("CUST_CODE");					// 收款商户编码
//		 String custStat = (String)map.get("CUST_STAT");					// 发起方审核状态
//		 String actionCode = (String)map.get("ACTION_CODE");				// 操作编码
//		 String orderId = (String)map.get("ORDER_ID");	                     // 订单编码
//		 Date acctDate= (Date)map.get("ACCT_DATE");
		 BigDecimal a = (BigDecimal)map.get("PAY_MONEY");                   //交易金额
		 String amount=a.toString();
		 
		 amount = MathTool.yuanToPoint(amount);
		 
		 if(!payeeCode.equals(dbRequest.getColleCustCode())){
			 
			 throw new Exception("你的收款商户编码输入不正确");
		 }
		 if(!dbRequest.getTxnamount().equals(amount)){
			 
			 throw new Exception("你的收款金额输入不正确");
		 }
		 
		 dbRequest.setColleCustCode(payeeCode);
	}
	/**
	 * 调用SCS0001接口转账
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet transfer(DpInf02032Request dpRequest,
			SignOrder order) throws Exception {

		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 

		String acctCodeR = acctDao.getAcctCode(dpRequest.getColleCustCode());// 

		TCumInfoDao infoDao = new TCumInfoDao();
		String area_code = infoDao.getAreaCode(dpRequest.getCustCode());

		logger.info("area_code:::" + area_code);

		String bankCode = "110000";

		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeTime = df.format(new Date());

		String actionCode = "01030001";

		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", area_code);// 所属区域编码 广州地区440100
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 
		g401.put("4018", "0.0.0.0");// 受理终端号
		g401.put("4280", dpRequest.getColleCustCode());// 收款方

		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", order.getAmount());// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", order.getAmount());// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码 // 全国新宽联0007 腾讯QQ 0031 改0039 电子售卡
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", dpRequest.getColleCustCode());//
		g404.put("4053", "1");// 业务数量
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", order.getAmount());// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", order.getAmount());// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0301");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");
		g407.put("4088", acctCodeR);
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");
		g407.put("4088", "0001");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", bankCode);// 
		g408.put("4099", dpRequest.getAcctType());// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", "123456");// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", order.getAmount());// 支付金额

		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}

	private static String cum003(DpInf02032Request dpRequest) throws Exception {

		String mobile = null;
		// 根据客户编码，调用CUM0003查询联系信息
		IParamGroup g0003_200 = new ParamGroupImpl("200"); // 包头
		g0003_200.put("2002", dpRequest.getCustCode());
		g0003_200.endRow();

		IParamGroup g0003_002 = new ParamGroupImpl("002"); // 包头
		g0003_002.put("0011", "207");
		g0003_002.endRow();

		// 组成数据包,调用CUM0003接口
		IServiceCall caller2 = new ServiceCallImpl();
		PackageDataSet dataSet = caller2.call("BIS", "CUM0003", g0003_200,
				g0003_002);// 组成交易数据包,调用CUM0003接口

		// 获取客户编码的手机号
		int count = dataSet.getParamSetNum("202");
		for (int i = 0; i < count; i++) {
			String code = (String) dataSet.getParamByID("2016", "202").get(i);
			if (code.equals("MOB")) {
				mobile = dataSet.getByID("2018", "202");
				break;
			}
		}

		return mobile;
	}

	private static SignOrder scs0201(DpInf02032Request dpRequest,
			String actionCode, BankAcctInfo bankAcctInfo) throws Exception {

		SignOrder order = new SignOrder();
		order.setMerId(dpRequest.getMerId());
		order.setKeep(dpRequest.getKeep());
		order.setChannelCode(dpRequest.getChannelCode());// 交易渠道
		order.setTmnNum(dpRequest.getTmnNum());// 交易终端号
		order.setActionCode(actionCode);// 操作类型：充值
		order.setTransSeq(dpRequest.getOrderSeq());// 交易序列号
		order.setBankAcctInfo(bankAcctInfo);// 客户绑定银行账户详细信息
		if (Double.valueOf(dpRequest.getTxnamount()) < 1) {
			throw new Exception("金额不能少于1分钱");
		}
		// 单位转换：分转元
		String txnamount = MathTool.pointToYuan(dpRequest.getTxnamount());
		String finalAmount = txnamount;
		order.setAmount(txnamount);// 交易金额
		order.setFinalAmount(txnamount);

		TPnmPartnerDao dao = new TPnmPartnerDao();
		Map<String, String> map = dao.getPrntnCodeAndPrntType(dpRequest
				.getCustCode());

		IParamGroup g423 = new ParamGroupImpl("423");
		g423.put("4230", "0001"); // 手续费
		g423.put("2011", dpRequest.getMerId()); // 接入机构的对应的商户编码
		g423.put("4330", map.get("PRTN_CODE")); // 实际做交易的商户编码
		g423.put("4331", map.get("PRTN_TYPE")); // 实际做交易的商户类型
		g423.put("2002", dpRequest.getCustCode()); // 实际做交易的商户编码
		g423.put("4051", actionCode); // 业务编码
		g423.put("4098", bankAcctInfo.getBankCode()); // 银行编码
		g423.put("4006", bankAcctInfo.getAreaCode()); // 区域编码
		g423.put("4144", dpRequest.getChannelCode()); // 渠道号
		g423.endRow();

		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4025", txnamount); // 订单金额
		g402.put("4099", dpRequest.getAcctType()); // 账户类型编码

		// 组成数据包,调用SCS0201接口
		IServiceCall caller = new ServiceCallImpl();

		PackageDataSet dataSet1 = caller.call("SCS", "SCS0201", g423, g402);
		String resCode = dataSet1.getByID("0001", "000");
		if (Long.valueOf(resCode) == 0) {
			String flagStr = dataSet1.getByID("4230", "423");
			if ("0001".equals(flagStr)) {
				String concessionType = dataSet1.getByID("4328", "423");
				String concession = dataSet1.getByID("4329", "423");
				if (concession == null || "".equals(concession)
						|| Double.valueOf(concession) == 0) {
					concession = "0";
				}

				order.setConcession(concession);
				order.setConcessionType(concessionType);
				// finalAmount = txnamount;
				double temp = 0;
				if ("DT003".equals(concessionType)) {
					temp = Double.parseDouble(txnamount)
							+ Double.parseDouble(concession);
					finalAmount = String.valueOf(temp);
				} else {
					temp = Double.parseDouble(txnamount)
							- Double.parseDouble(concession);
					if (temp < 0) {
						throw new Exception("计算手续费金额后为负数");
					}
					txnamount = String.valueOf(temp);
				}
				order.setAmount(txnamount);// 交易金额
				order.setFinalAmount(finalAmount);// 最终交易金额
			}
		}

		return order;

	}

}
