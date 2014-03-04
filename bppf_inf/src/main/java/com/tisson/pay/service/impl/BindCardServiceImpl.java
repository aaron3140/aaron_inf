package com.tisson.pay.service.impl;

import java.util.Map;

import mpi.client.data.TransData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tisson.pay.config.BftProperties;
import com.tisson.pay.config.SysCode;
import com.tisson.pay.service.BindCardService;

import common.algorithm.MD5;
import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TPnmPartnerDao;
import common.dao.TRegBindCardDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.invoker.exception.ServiceInvokeException;
import common.platform.provider.server.PackageDataSet;
import common.utils.Charset;

/**
 * 邱亚建 2013-12-3 上午09:16:09<br/>
 * 
 * 本类描述:绑卡服务类
 */
public class BindCardServiceImpl implements BindCardService {

	private static final Log log = LogFactory.getLog(BindCardServiceImpl.class);
	private TRegBindCardDao dao = new TRegBindCardDao();

	@Override
	public String doBindCard(TransData transdata, String bindType) throws Exception {
		String sysCode = transdata.getSysCode();
		SysCode sysCodeObject = getSysCode(sysCode);
		String payDesc = sysCodeObject.getDesc();

		// sysCode ="P_0000";//测试。。。发布时注释掉
		String payID = transdata.getPayID(); 
		String logTag = payID + "_" + transdata.getUserID() + "  BFT_";
		if ("0".equals(bindType)) {
			logTag += "CALLBACK";
		} else if ("1".equals(bindType)) {
			logTag += "QUERY";
		}

		log.info(logTag + "  帮付通回复 payId=" + payID + "  userId=" + transdata.getUserID() + "支付结果码=" + sysCode + " 描述=" + payDesc);
		String custCode = "";
		// 获取绑卡信息
		Map map = null;
		try {
			map = dao.getOrderInfoByTransSeq(payID);
			if (map == null || map.isEmpty()) {
				log.info(logTag + "报文交互失败,订单号码不存在  T_0014");
				return "T_0014";// 报文交互失败,订单号码不存在
			}
		} catch (Exception e) {
			log.error(logTag + "获取绑卡订单信息出错  bind_orderno=" + payID);
			return "T_0014";// 报文交互失败,订单号码不存在
		}

		
		String bindState = (String) map.get("BIND_STATE");
		custCode = (String) map.get("CUST_CODE");
		if ("S0F".equals(bindState) || "S0C".equals(bindState)) {
			log.info(logTag + "报文交互失败,订单状态已更新  T_0025，绑卡表状态为：" + bindState);
			return "T_0025";// 报文交互失败,订单状态已更新
		}

		/**
		 * 商户业务逻辑处理【开始】
		 */
		// 查询原订单
		// 客户编码
		log.info("============查询预受理订单信息开始============");
		PackageDataSet ds = callSCS0015(transdata, custCode);
		String resultCode = (String) ds.getParamByID("0001", "000").get(0);
		// 返回结果为失败时，抛出异常
		if (Long.valueOf(resultCode) != 0) {
			String resultMsg = (String) ds.getParamByID("0002", "000").get(0);
			log.error(logTag + "调用核心出错  响应码：" + resultCode + "  描述：" + resultMsg);
			throw new Exception(resultMsg);
		}  

		String keep = ds.getByID("4017", "401");
		if (Charset.isEmpty(keep, true)) {
			log.error(logTag + "预受理订单信息：custCode=[" + custCode + "] orderNo=[" + payID + "] ");
			throw new Exception("预受理订单信息不存在");
		}
		String orderNo = ds.getByID("4028", "401");// 外部订单号
		String transSeq = ds.getByID("4002", "401");// 交易流水号
		String tumNo = ds.getByID("4007", "401");// 终端号
		log.info(logTag + "订单信息：keep=[" + keep + "] orderNo=[" + orderNo + "] transSeq=[" + transSeq + "] tumNo=[" + tumNo + "]");
		log.info("============查询预受理订单信息结束============");

		// 判断支付是否成功
		if (!sysCode.equals(SysCode.P_0000.name())) {
			log.info(logTag + " 支付状态码：" + sysCode + "  描述：" + payDesc);
			log.info(logTag + " 支付不成功，终止绑卡操作  订单更新为无效");
			dao.updateBindStateToFail(payID, sysCode + "::" + payDesc);
			// 密码错误时不回调核心
			if (!sysCode.equals(SysCode.P_0055.name())) {
				String desc = getSysCode(transdata.getSysCode()).getDesc();
				try {
					ds = callSCS0013(transdata, custCode, keep, orderNo, tumNo, (String) map.get("BANK_ACCT"), "S0F",desc);
				} catch (ServiceInvokeException e) {
					ds = e.getDataSet();
					resultCode = (String) ds.getParamByID("0001", "000").get(0);
					if (!"6000".equals(resultCode)) {
						throw e;
					}else{
						//核心返回6000时为核心成功修改订单为失败状态
						dao.updateBindStateToFail(transdata.getPayID(), transdata.getSysCode() + "::" + transdata.getDescription() + "::" + desc);
					}
				}
			}
			return "T_0096";// 报文交互失败,其它错误
		} else {
			log.info(logTag + " 支付状态码：" + sysCode + "  描述：" + payDesc);
			log.info(logTag + " 支付成功，继续绑卡操作");
		}

		// 支付
		log.info("============异步订单支付开始============");
		ds = callSCS0013(transdata, custCode, keep, orderNo, tumNo, (String) map.get("BANK_ACCT"), "S0A","");
		resultCode = (String) ds.getParamByID("0001", "000").get(0);
		// 返回结果为失败时，抛出异常
		if (Long.valueOf(resultCode) != 0) {
			String resultMsg = (String) ds.getParamByID("0002", "000").get(0);
			log.error(logTag + "调用核心出错  响应码：" + resultCode + "  描述：" + resultMsg);
			throw new Exception(resultMsg);
		}
		log.info("============异步订单支付结束============");

		// 签约
		log.info("============签约绑卡开始============");
		boolean flag = dao.hasBindCardInfo(custCode);// 是否存在绑卡信息

		if (map != null && !map.isEmpty()) {
			map.put("TMN_NUM", tumNo);
			if (flag) {// 修改绑卡信息
				log.info("-----------------修改绑卡信息-----------------");
				ds = callCUM0005(transdata, map);
				resultCode = (String) ds.getParamByID("0001", "000").get(0);
				if (Long.valueOf(resultCode) != 0) {
					String resultMsg = (String) ds.getParamByID("0002", "000").get(0);
					log.error(logTag + "调用核心出错  响应码：" + resultCode + "  描述：" + resultMsg);
					throw new Exception(resultMsg);
				}
				// 修改旧的绑卡信息为无效
				dao.updateStateToS0X(custCode);
			} else {// 第一次绑卡
				log.info("-----------------第一次绑卡-----------------");
				ds = callCUM0012(transdata, map);
				resultCode = (String) ds.getParamByID("0001", "000").get(0);
				if (Long.valueOf(resultCode) != 0) {
					String resultMsg = (String) ds.getParamByID("0002", "000").get(0);
					log.error(logTag + "调用核心出错  响应码：" + resultCode + "  描述：" + resultMsg);
					throw new Exception(resultMsg);
				}
				String desc = ds.getByID("0002", "000");
				log.info(logTag + "签约反馈信息：" + desc);
				log.info(logTag + "签约绑卡成功  签约ID=[" + ds.getByID("2149", "207") + "]  生效时间=[" + ds.getByID("2040", "207") + "]");
			}
			// 更新绑卡订单状态
			dao.updateBindStateToSuccess(custCode, payID);
		} else {
			log.error(logTag + "没有找到对应的订单信息...custCode=[" + transdata.getUserID() + "] orderNo=[" + payID + "]");
			throw new Exception("签约时没有找到对应的订单信息");
		}

		log.info("============签约绑卡结束============");
		return "T_0000";// 报文交互成功
	}

	/**
	 * 修改银行卡信息
	 * 
	 * @param acctCode
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callCUM0005(TransData transData, Map map) throws Exception {
		IParamGroup g200 = new ParamGroupImpl("200");
		String custCode = (String) map.get("CUST_CODE");
		g200.put("2002", custCode); // 客户编码
		g200.endRow();

		IParamGroup g207 = new ParamGroupImpl("207");
		g207.put("2048", "ACCT001"); // 账户类型编码
		g207.put("4097", "PT1004");// 支付方式--银行代收（无磁无密）
		g207.put("2049", (String) map.get("BANK_ACCT"));// 账号
		g207.put("2050", (String) map.get("BANK_CODE"));// 银行账户所属银行代码
		g207.put("2051", (String) map.get("TRANSACC_NAME"));// 开户行名称
		g207.put("2052", "1");// 对公/对私标识
		g207.put("2068", (String) map.get("BANK_OPEN"));// 开户行信息
		g207.endRow();

		IParamGroup g216 = new ParamGroupImpl("216");
		g216.put("2069", "2566"); // 证件类型
		g216.put("2071", "00");
		g216.endRow();

		g216.put("2069", "2567"); // 证件号码
		g216.put("2071", (String) map.get("CER_NO"));
		g216.endRow();

		g216.put("2069", "2568"); // 银行所在区域
		g216.put("2071", (String) map.get("AREA_CODE"));
		g216.endRow();

		g216.put("2069", "2569"); // 开户行名称
		g216.put("2071", (String) map.get("BANK_OPEN"));
		g216.endRow();

		g216.put("2069", "2814"); // 手机号码
		g216.put("2071", (String) map.get("OPEN_PHONE"));
		g216.endRow();

		g216.put("2069", "8038"); // 联系地址
		g216.put("2071", "-");
		g216.endRow();

		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", "80"); // 渠道类型编码
		g211.put("2077", (String) map.get("TMN_NUM")); // 操作来源（终端号）
		g211.put("2078", custCode); // 操作执行者 客户网站注册时，填为客户编码；后台为商户注册时，填为操作员工工号。
		g211.endRow();

		IServiceCall serCall = new ServiceCallImpl();
		PackageDataSet dataSet = serCall.call("BIS", "CUM0005", g200, g207, g216, g211);
		return dataSet;
	}

	/**
	 * 签约
	 * 
	 * @param map
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static PackageDataSet callCUM0012(TransData transData, Map map) throws Exception {

		TCumInfoDao dao = new TCumInfoDao();
		String custCode = (String) map.get("CUST_CODE");
		String prtnCode = dao.getPrtnCode(custCode);
		String custName = TCumInfoDao.getCustName(custCode);
		// String areaCode = TCumInfoDao.getAreaCode(custCode);// 客户编码所在的区域编码

		IParamGroup g218 = new ParamGroupImpl("218");
		g218.put("2011", prtnCode);
		g218.put("2002", custCode); // 客户编码
		g218.put("2004", "C03");// 客户类型编码
		g218.put("2091", custName);// 收款单位名称
		// g218.put("2181", areaCode);
		g218.put("2181", "00");// 省代码 若不确定省代码，默认填00 2位的省代码

		g218.put("2182", "0");// 若不确定企业类型，默认填0
		g218.put("2183", "000");// 若不确定项目，默认项目000
		g218.put("2185", "0002"); // 签约方式
		g218.put("2184", "0003"); // 无验证签约
		g218.endRow();

		IParamGroup g207 = new ParamGroupImpl("207");
		g207.put("2009", "00");// 证件类型编码
		g207.put("2010", (String) map.get("CER_NO"));// 证件号码
		g207.put("2050", (String) map.get("BANK_CODE"));// 银行账户所属银行代码
		g207.put("2051", (String) map.get("BANK_OPEN"));// 开户行信息
		g207.put("2158", (String) map.get("TRANSACC_NAME"));// 银行账户户名
		g207.put("2150", (String) map.get("AREA_CODE"));// 账号归属地
		g207.put("2151", "1");// 卡折标识
		g207.put("2152", "1");// 对公/对私标识
		g207.put("2154", (String) map.get("OPEN_PHONE"));// 联系号码
		g207.put("2155", "-");// 联系地址
		g207.put("2156", "");// 信用卡有效期
		g207.put("2157", "");// 信用卡验证码
		g207.put("2159", (String) map.get("BANK_ACCT"));// 银行账户
		g207.put("4097", "PT1004");// 支付方式--银行代收（无磁无密）
		g207.endRow();

		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", "80");
		g211.put("2077", (String) map.get("TMN_NUM"));
		g211.put("2078", custCode);
		g211.endRow();

		// 组成数据包,调用CUM0012接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM0012", g218, g207, g211);

		// 返回结果
		return dataSet;
	}

	/**
	 * 查询原订单信息
	 * 
	 * @param transData
	 * @return
	 * @throws Exception
	 */
	private PackageDataSet callSCS0015(TransData transData, String custCode) throws Exception {
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "4004");
		g002.put("0022", custCode);
		g002.endRow();
		// 交易流水号
		g002.put("0021", "4002");
		g002.put("0022", transData.getPayID());
		g002.endRow();
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SCS", "SCS0015", g002);
		return ds;
	}

	/**
	 * 异步订单支付
	 * 
	 * @param transData
	 * @param tumNo
	 * @param orderNo
	 * @param keep
	 * @param string
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callSCS0013(TransData transData, String custCode, String keep, String orderNo, String tumNo, String bankAcct, String stat,String failDesc) throws Exception {
		String accountType = getAccountType(custCode);
		String orderId = transData.getPayID(); // 订单编码
		String payeeCode = custCode; // 收款商户编码
		String txnAmount = BftProperties.getOrderAmount(); // 交易金额
		String actionCode = "01010007"; // 操作编码
		String mark1 = transData.getDescription();
		TCumAcctDao dao = new TCumAcctDao();
		String payeeCardAcctNbr = dao.getAcctCode(payeeCode, "ACCT002");// 收款人资金帐户
		/**
		 * 调用SCS0013,完成交易确认/交易取消操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4002", orderId);// 订单编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4144", "80");// 交易渠道
		g401.put("4017", keep);// 终端流水号
		g401.put("4018", tumNo);// 操作原始来源
		g401.put("4028", orderNo);// 外部订单号
		g401.put("4142", "OT008");// 回调标识
		g401.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码//////////////////
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", payeeCode);// 业务对象//////////////////
		g404.put("4053", "1");// 业务数量
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", "0301");// 属性值1///////////////////////
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", payeeCardAcctNbr);// 属性值1///////////////////////
		g407.put("4080", "1");// 控制标识
		g407.endRow();
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
		g407.put("4088", accountType);// 属性值1/////////////////////////
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		if (!Charset.trim(mark1).equals("")) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_MARK1");// 业务属性编码
			g407.put("4088", mark1);// 属性值1/////////////////////////
			g407.put("4080", "0");// 控制标识
			g407.endRow();
		}

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT1006");// 支付方式编码///////////////
		g408.put("4098", "110003");// 支付机构编码
		g408.put("4099", accountType);// 账户类型编码/////////////////
		g408.put("4101", bankAcct);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.endRow();

		IParamGroup g480 = new ParamGroupImpl("480");
		log.info("调用SCS0013 480组状态值为：" + stat);
		g480.put("4805", stat); // 交易确认
		if (stat.equals("S0A")) {
			g480.put("4802", "0000"); // 成功0000 失败6000
			g480.put("4803", "成功"); // 成功/失败
		} else {
			g480.put("4802", "6000"); // 成功0000 失败6000
			g480.put("4803", failDesc); // 成功/失败
		}

		g480.put("4804", "ST001"); // 交易确认
		g480.endRow();

		// 组成数据包,调用SCS0013接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0013", g401, g404, g405, g407, g408, g480);

		// 返回结果
		return dataSet;
	}

	/**
	 * 获取入账账户类型，0001资金帐户，0007交费易帐号
	 * 
	 * @param custCode
	 * @return
	 * @throws Exception
	 */
	private static String getAccountType(String custCode) throws Exception {
		String type = "0001";// 资金帐户
		TPnmPartnerDao dao = new TPnmPartnerDao();
		String plineId = dao.getPlineIdByCusCode(custCode);
		if (plineId.contains("104")) {
			type = "0007";
		}
		log.info("产品线编码：" + plineId + "  入账账户类型：" + type);
		return type;
	}

	private SysCode getSysCode(String sysCode) {
		if ("P_0000".equals(sysCode)) {
			return SysCode.P_0000;
		} else if ("P_0025".equals(sysCode)) {
			return SysCode.P_0025;
		} else if ("P_0051".equals(sysCode)) {
			return SysCode.P_0051;
		} else if ("P_0055".equals(sysCode)) {
			return SysCode.P_0055;
		} else if ("P_0061".equals(sysCode)) {
			return SysCode.P_0061;
		} else if ("P_0063".equals(sysCode)) {
			return SysCode.P_0063;
		} else if ("P_0064".equals(sysCode)) {
			return SysCode.P_0064;
		} else if ("P_0075".equals(sysCode)) {
			return SysCode.P_0075;
		} else if ("P_0080".equals(sysCode)) {
			return SysCode.P_0080;
		} else if ("P_0081".equals(sysCode)) {
			return SysCode.P_0081;
		} else if ("P_0082".equals(sysCode)) {
			return SysCode.P_0082;
		} else if ("P_0086".equals(sysCode)) {
			return SysCode.P_0086;
		} else if ("P_0089".equals(sysCode)) {
			return SysCode.P_0089;
		}else if ("P_00AA".equals(sysCode)) {
			return SysCode.P_00AA;
		} else if ("P_00DD".equals(sysCode)) {
			return SysCode.P_00DD;
		} else if ("P_00PP".equals(sysCode)) {
			return SysCode.P_00PP;
		} else if ("P_00J2".equals(sysCode)) {
			return SysCode.P_00J2;
		} else if ("P_00NN".equals(sysCode)) {
			return SysCode.P_00NN;
		} else if ("P_00B1".equals(sysCode)) {
			return SysCode.P_00B2;
		} else if ("P_00B2".equals(sysCode)) {
			return SysCode.P_00B2;
		} else if ("P_00B3".equals(sysCode)) {
			return SysCode.P_00B3;
		} else if ("P_00B4".equals(sysCode)) {
			return SysCode.P_00B4;
		} else if ("P_00B5".equals(sysCode)) {
			return SysCode.P_00B5;
		} else if ("P_00B6".equals(sysCode)) {
			return SysCode.P_00B6;
		} else if ("P_00B7".equals(sysCode)) {
			return SysCode.P_00B7;
		} else if ("P_00X1".equals(sysCode)) {
			return SysCode.P_00X1;
		} else if ("P_00X2".equals(sysCode)) {
			return SysCode.P_00X2;
		} else if ("P_00X3".equals(sysCode)) {
			return SysCode.P_00X3;
		} else if ("P_00X5".equals(sysCode)) {
			return SysCode.P_00X5;
		} else if ("P_00X6".equals(sysCode)) {
			return SysCode.P_00X6;
		} else if ("P_0096".equals(sysCode)) {
			return SysCode.P_0096;
		}
		return SysCode.P_0096;
	}

}
