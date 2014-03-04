package websvc.servant;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.utils.DateTool;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02047Request;
import common.xml.dp.DpInf02047Response;

import framework.config.ActionInfoConfig;
import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @title INF02047.java
 * @description 理财申购支付接口业务处理类
 * @date 2014-02-11 09:27
 * @author lichunan
 * @version 1.0
 */
public class INF02047 {

	private static final Log logger = LogFactory.getLog(INF02047.class);
	public static String svcInfName = "INF02047";

	public static String execute(String in0, String in1) {
		DpInf02047Request dpRequest = null;
		DpInf02047Response resp = new DpInf02047Response();
		RespInfo respInfo = null;
		logger.info("INF02047请求参数：" + in1);
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String svcCode = WebSvcTool.getSvcCode(in0);
		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);
		String responseCode = "";
		String responseContent = "";
		try {
			dpRequest = new DpInf02047Request(in1);
			respInfo = new RespInfo(in1, dpRequest.getChannelCode());
			// 客户端MD5校验--------------------------------------------
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(
					dpRequest.getStaffCode(), tokenValidTime);
			dpRequest.verifyByMD5(md5Key);
			TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest
					.getStaffCode());
			// -------------------------------------------------------------------

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"", "", "", "", OrderConstant.S0A);
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

			SignBankManage manage = new SignBankManage();
			String custId = manage.getCustIdByCode(dpRequest.getCustCode());
			if (null == custId || "".equals(custId)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
						INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
			}
			
			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if(!(dpRequest.getTradeTime().matches(RegexTool.DATE_FORMAT))){
				throw new INFException(INFErrorDef.INPUT_DATE_FORMAT_ERROR,
						INFErrorDef.INPUT_DATE_FORMAT_ERROR_DESC);
			}
			
			PackageDataSet ds = null;
			/**
			 * 1、 如果资金源类型为授权银行卡就先进行充值操作
			 */
			if (!"1".equals(dpRequest.getFundSourceType())) {
				ds = reCharge(dpRequest);
				responseCode = (String) ds.getParamByID("0001", "000").get(0);
				if (Long.valueOf(responseCode) != 0) {
					responseContent = (String) ds.getParamByID("0002", "000")
							.get(0);
					String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(),
							respInfo.getRespType(), respInfo.getKeep(),
							"SUCCESS", responseCode, responseContent,
							dpRequest.getOrderSeq(), "",
							dpRequest.getBusinessOrderNo(),
							MathTool.yuanToPoint(dpRequest.getTotalAmount()),
							dpRequest.getTradeTime(), dpRequest.getRemark1(),
							dpRequest.getRemark2());
					return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);
				}
			}

			/**
			 * 2、进行申购支付操作
			 */
			ds = orderPayment(dpRequest);
			responseCode = (String) ds.getParamByID("0001", "000").get(0);
			responseContent = (String) ds.getParamByID("0002", "000").get(0);
			if (Long.valueOf(responseCode) != 0) {
				String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(),
						respInfo.getRespType(), respInfo.getKeep(), "SUCCESS",
						responseCode, responseContent, dpRequest.getOrderSeq(),
						"", dpRequest.getBusinessOrderNo(),
						MathTool.yuanToPoint(dpRequest.getTotalAmount()),
						dpRequest.getTradeTime(), dpRequest.getRemark1(),
						dpRequest.getRemark2());
				return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);
			}
			String transSeq = ds.getByID("4002", "401");
			String txanAmount = "";
			ArrayList<?> list = ds.getParamByID("6303", "600");
			if (list != null && list.size() != 0) {
				String txanAmounts = (String) list.get(0);// 交易金额
				// 单位转换：元转分
				txanAmount = MathTool.yuanToPoint(txanAmounts);
			} else {
				txanAmount = MathTool.yuanToPoint(dpRequest.getTotalAmount());
			}
			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(),
					respInfo.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseContent, dpRequest.getOrderSeq(),
					transSeq, dpRequest.getBusinessOrderNo(), txanAmount,
					dpRequest.getTradeTime(), dpRequest.getRemark1(),
					dpRequest.getRemark2());
			return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);
		} catch (Exception e) {
			String oXml = ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), infId);
			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		}
	}

	/**
	 * 调用SCS0001接口实现交费易充值功能
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet reCharge(DpInf02047Request dpRequest)
			throws Exception {
		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 银行帐号
		String bankCode = acctDao.getBankCode(acctCode); // 银行编码[通过银行帐号查询]

		TCumInfoDao infoDao = new TCumInfoDao();
		String areaCode = infoDao.getAreaCode(dpRequest.getCustCode());// 通过客户编码查区域编码
		String actionCode = "01010001";// 授权银行卡充值
		/**
		 * 调用SCS0001,完成充值操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", DateTool.getStrDate(new Date(), "yyyyMMddHHmmss"));// 受理时间
		g401.put("4012", "银行卡授权充值");// 订单描述
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		g401.put("4028", dpRequest.getBusinessOrderNo());// 外部订单号
		g401.put("4284", dpRequest.getMerId());// 机构编码
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", dpRequest.getTotalAmount());// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", dpRequest.getTotalAmount());// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0039");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", dpRequest.getCustCode());// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 当该值与不为空时，已该值作为actlist的系统参考号。否则由核心交易平台平台生成
		g404.put("4072", actionCode);
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", dpRequest.getTotalAmount());// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", dpRequest.getTotalAmount());// 业务单应付金额
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
		g407.put("4088", "0");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_SPID");
		g407.put("4088", "10002001");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE1");
		g407.put("4088", "H003");
		g407.put("4089", "交费易充值ID");
		g407.put("4091", "01");
		g407.put("4093", ActionInfoConfig.INF02010_PRO_ID); // 测试2030 生产 5178
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE2");
		g407.put("4088", "H007");
		g407.put("4089", "其它参数");
		g407.put("4091", "01");
		g407.put("4093", "1");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE3");
		g407.put("4088", "H016");
		g407.put("4089", "充值账户");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getCustCode());
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE4");
		g407.put("4088", "H017");
		g407.put("4089", "确认充值账户");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getCustCode());
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE5");
		g407.put("4088", "H018");
		g407.put("4089", "交费易名称(代码)");
		g407.put("4091", "01");
		g407.put("4093", "987654");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE6");
		g407.put("4088", "H019");
		g407.put("4089", "服务器名称(代码)");
		g407.put("4091", "01");
		g407.put("4093", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_SERVID");
		g407.put("4088", "GGC401");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", bankCode);// 支付机构编码
		g408.put("4099", "0007");// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", dpRequest.getPassWord());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", dpRequest.getTotalAmount());// 支付金额
		g408.put("4127", bankCode);// 前置支付机构,查卡表的bankcode
		g408.put("4109", "0003");// 国际网络号
		g408.put("4119", "");
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g407, g408);
		return dataSet;
	}

	/**
	 * 调用OMM0001接口实现申购支付功能
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet orderPayment(DpInf02047Request dpRequest)
			throws Exception {
		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode(),
				"ACCT002");// 交费易帐号
		String payAgencyCode = "110000"; // 支付机构编码

		String areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());// 通过客户编码查区域编码
		String actionCode = "20010001";// 申购支付
		/**
		 * 调用OMM0001,完成申购支付
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", DateTool.getStrDate(new Date(), "yyyyMMddHHmmss"));// 受理时间
		g401.put("4012", "理财申购支付");// 订单描述
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		g401.put("4028", dpRequest.getBusinessOrderNo());// 外部订单号
		g401.put("4284", dpRequest.getMerId());// 机构编码
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", dpRequest.getTotalAmount());// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", dpRequest.getTotalAmount());// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0039");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", dpRequest.getCustCode());// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 当该值与不为空时，已该值作为actlist的系统参考号。否则由核心交易平台平台生成
		g404.put("4072", actionCode);
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", dpRequest.getTotalAmount());// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", dpRequest.getTotalAmount());// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "OMM_DEALTYPE");
		g407.put("4088", "0200");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", payAgencyCode);// 支付机构编码
		g408.put("4099", "0007");// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", dpRequest.getPassWord());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", dpRequest.getTotalAmount());// 支付金额
		g408.put("4127", payAgencyCode);// 前置支付机构,查卡表的bankcode
		g408.put("4109", "0003");// 国际网络号
		g408.put("4119", "");
		g408.endRow();

		// 组成数据包,调用OMM0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("OMM", "OMM0001", g401, g402,
				g404, g405, g407, g408);
		return dataSet;
	}

}
