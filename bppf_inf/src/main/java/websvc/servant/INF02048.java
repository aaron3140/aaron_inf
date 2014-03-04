package websvc.servant;

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
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02048Request;
import common.xml.dp.DpInf02048Response;

import framework.config.ActionInfoConfig;
import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @title INF02048.java
 * @description 理财赎回业务处理类
 * @date 2014-02-11 09:27
 * @author lichunan
 * @version 1.0
 */
public class INF02048 {

	private static final Log logger = LogFactory.getLog(INF02048.class);
	public static String svcInfName = "INF02048";

	public static String execute(String in0, String in1) {
		DpInf02048Request dpRequest = null;
		DpInf02048Response resp = new DpInf02048Response();
		RespInfo respInfo = null;
		logger.info("INF02048请求参数：" + in1);
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String svcCode = WebSvcTool.getSvcCode(in0);
		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);
		String responseCode = "";
		String responseContent = "";
		try {
			dpRequest = new DpInf02048Request(in1);
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

			TCumAcctDao acctDao = new TCumAcctDao();
			String acctCode = "";
			String payAgencyCode = "";
			String actionCode = "";
			if ("0".equals(dpRequest.getRansomType())) {// 赎回到授权银行卡
				acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 银行帐号
				payAgencyCode = acctDao.getBankCode(acctCode); // 支付机构编码
				actionCode = "20010003";
			} else {// 赎回到交费易账户
				acctCode = acctDao.getAcctCode(dpRequest.getCustCode(),
						"ACCT002");// 交费易帐号
				payAgencyCode = "110000"; // 支付机构编码
				actionCode = "20010002";
			}
			PackageDataSet ds = null;
			String oXml = "";
			ds = ransom(dpRequest, acctCode, payAgencyCode, actionCode);
			responseCode = (String) ds.getParamByID("0001", "000").get(0);
			responseContent = (String) ds.getParamByID("0002", "000").get(0);
			if (Long.valueOf(responseCode) != 0) {
				oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(),
						respInfo.getRespType(), respInfo.getKeep(), "SUCCESS",
						responseCode, responseContent, dpRequest.getOrderSeq(),
						"", dpRequest.getRansomType(),
						MathTool.yuanToPoint(dpRequest.getTotalAmount()),
						dpRequest.getTradeTime(), dpRequest.getRemark1(),
						dpRequest.getRemark2());
			}
			String transSeq = ds.getByID("4002", "401");
			oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(),
					respInfo.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseContent, dpRequest.getOrderSeq(),
					transSeq, dpRequest.getRansomType(),
					MathTool.yuanToPoint(dpRequest.getTotalAmount()),
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
	 * 调用OMM0001接口实现理财赎回功能
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet ransom(DpInf02048Request dpRequest,
			String acctCode, String payAgencyCode, String actionCode)
			throws Exception {
		TCumInfoDao infoDao = new TCumInfoDao();
		String areaCode = infoDao.getAreaCode(dpRequest.getCustCode());// 通过客户编码查区域编码
		/**
		 * 调用OMM0001,完成理财赎回操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", DateTool.getStrDate(new Date(), "yyyyMMddHHmmss"));// 受理时间
		g401.put("4012", "理财赎回");// 订单描述
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		g401.put("4028", dpRequest.getOrderSeq());// 外部订单号
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

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "OMM_USERTYPE");
		g407.put("4088", "0");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "OMM_SPID");
		g407.put("4088", "10002001");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "OMM_ATTRLINE1");
		g407.put("4088", "H003");
		g407.put("4089", "理财赎回ID");
		g407.put("4091", "01");
		g407.put("4093", ActionInfoConfig.INF02010_PRO_ID); // 测试2030 生产 5178
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "OMM_ATTRLINE2");
		g407.put("4088", "H007");
		g407.put("4089", "其它参数");
		g407.put("4091", "01");
		g407.put("4093", "1");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "OMM_ATTRLINE3");
		g407.put("4088", "H016");
		g407.put("4089", "理财赎回");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getCustCode());
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "OMM_ATTRLINE4");
		g407.put("4088", "H017");
		g407.put("4089", "确认理财赎回");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getCustCode());
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "OMM_ATTRLINE5");
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
		g407.put("4087", "OMM_SERVID");
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
