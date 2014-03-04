package websvc.servant;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.Charset;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.PrivConstant;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf02011Response;
import common.xml.dp.DpInf12011Request;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author wanght 2013-12-05 <br>
 *         TODO 话费充值(纯业务)
 * 
 */
public class INF12011 {

	private static final Log logger = LogFactory.getLog(INF12011.class);

	public static String svcInfName = "INF12011";

	@SuppressWarnings("unchecked")
	public static String execute(String in0, String in1) {

		DpInf12011Request dpRequest = null;

		DpInf02011Response resp = new DpInf02011Response();

		RespInfo respInfo = null;

		logger.info("INF12011请求参数：：" + in1);

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf12011Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					"", dpRequest.getTmnNum(), svcCode, "XML", "", "", "", "",
					OrderConstant.S0A);

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

			if (Double.valueOf(dpRequest.getTxnAmount()) < 1) {
				throw new Exception("金额不能少于1分钱");
			}
			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if (!(dpRequest.getTradeTime().matches(RegexTool.DATE_FORMAT))) {

				throw new Exception("输入日期格式不正确");
			}
			// 判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(
					dpRequest.getCustCode(), "80");
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.WS_PHONE_REC.equals(str)) {
					r = true;
					break;
				}
			}

			if (!r) {
				throw new Exception("你没有纯业务话费充值的权限");
			}

			PackageDataSet ds = null;

			String resultCode = "";

			String responseDesc = "";

			Hashtable<String, Object> m = process2(dpRequest);

			resultCode = (String) m.get("ResultCode");

			responseDesc = (String) m.get("ResponseDesc");

			ds = (PackageDataSet) m.get("DateSet");

			String transSeq = ds.getByID("4002", "401");

			String txanAmount = "";

			ArrayList list = ds.getParamByID("6303", "600");

			if (list != null && list.size() != 0) {

				String txanAmounts = (String) list.get(0);// 交易金额
				// 单位转换：元转分
				txanAmount = MathTool.yuanToPoint(txanAmounts);
			} else {

				txanAmount = MathTool.yuanToPoint(dpRequest.getTxnAmount());
			}

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS", resultCode,
					responseDesc, dpRequest.getOrderNo(), dpRequest
							.getRemark1(), dpRequest.getRemark2(), transSeq,
					txanAmount,null);

			return oXml;

		} catch (XmlINFException spe) {

			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), infId);

		}
	}

	private static Hashtable<String, Object> process2(
			DpInf12011Request dpRequest) throws Exception {

		PackageDataSet ds = null;

		String responseDesc = "";

		String resultCode = "";

		// 单位转换：分转元
		String reAmount = MathTool.pointToYuan(dpRequest.getRechargeAmount());

		String txAmount = MathTool.pointToYuan(dpRequest.getTxnAmount());

		dpRequest.setRechargeAmount(reAmount);

		dpRequest.setTxnAmount(txAmount);
		// 充值
		ds = recharge(dpRequest);

		resultCode = (String) ds.getParamByID("0001", "000").get(0);

		// 返回结果为失败时，获取结果描述
		if (Long.valueOf(resultCode) == 0) {

			responseDesc = (String) ds.getParamByID("0002", "000").get(0);
		}

		Hashtable<String, Object> map = new Hashtable<String, Object>();

		map.put("ResultCode", resultCode);

		map.put("ResponseDesc", responseDesc);

		map.put("DateSet", ds);

		return map;

	}

	/**
	 * 调用SCS0001接口充值
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet recharge(DpInf12011Request dpRequest)
			throws Exception {

		// String areaCode =
		// TCumInfoDaoTemp.queryAreacodeByCustCode(dpRequest.getCustCode());//
		// 所属区域编码
		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 银行帐号
		String bankCode = acctDao.getBankCode(acctCode); // 银行编码[通过银行帐号查询]
		String rechargeType = dpRequest.getRechargeType();// 业务编码
		// String proCode = getProductCode(rechargeType);//产品编码

		// 通过客户编码查区域编码
		String area_code = TCumInfoDao.getAreaCode(dpRequest.getCustCode());
		String provinceCode = TCumInfoDao.getProvinceCodeByPhoneNum(dpRequest
				.getPhone());// 手机号对应的省份编码
		if (Charset.isEmpty(provinceCode, true)) {// 查不到，用企业账户的区域编码
			provinceCode = area_code;
		}

		bankCode = "110000";
		// 一次路由
		// TransManage tm = new TransManage();
		// PackageDataSet dataSet2 = tm.firstRoute(dpRequest.getCustCode(),
		// areaCode, dpRequest.getChannelCode(), "07010002",
		// dpRequest.getMerId(),
		// dpRequest.getTmnNum(), dpRequest.getTxnAmount(), "PT1004", bankCode);

		// String newActionCode = dataSet2.getByID("4051", "423");
		// String newActionCode = "07010002";
		String newActionCode = rechargeType;
		// String newOrgCode = dataSet2.getByID("4098", "423");
		// DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		// String tradeTime = df.format(new Date());

		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		// g401.put("4006", areaCode);// 所属区域编码
		g401.put("4006", area_code);// 所属区域编码 杭州330100
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", dpRequest.getTradeTime());// 受理时间
		g401.put("4012", convertCodeToChar(dpRequest.getRechargeType()));// 订单描述
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode60To20());// 渠道类型编码
		// g401.put("4017", dpRequest.getOrderNo());// 订单号？？？
		g401.put("4017", dpRequest.getKeep());// 订单号？？？
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		g401.put("4028", dpRequest.getOrderNo());

		g401.put("4284", dpRequest.getMerId());// 机构编码 //20130628 wanght
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
		g404.put("4049", getProductCode(rechargeType));// 产品编码
		g404.put("4051", newActionCode);// 业务编码
		g404.put("4052", dpRequest.getPhone());// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 当该值与不为空时，已该值作为actlist的系统参考号。否则由核心交易平台平台生成
		g404.put("4072", rechargeType);
		// g404.put("4064", dpRequest.getCustCode());
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

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0200");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_USERTYPE");
		g407.put("4088", "1");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1"); // 业务单序号
		g407.put("4051", newActionCode); // 业务编码
		g407.put("4087", "SCS_DISCID"); // 产品属性编码
		g407.put("4088", newActionCode + provinceCode); // 附加项数据类型
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0"); // 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_SERVID");
		g407.put("4088", get4088Param(rechargeType));
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT2001");// 支付方式编码

		g408.put("4127", bankCode);// 前置支付机构,查卡表的bankcode
		g408.put("4098", bankCode);// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", "123456");// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", dpRequest.getTxnAmount());// 支付金额
		g408.put("4109", "0003");// 国际网络号
		g408.put("4119", "");
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}

	/**
	 * 获取SCS_SERVID 属性对应的属性值
	 * 
	 * @param rechargeType
	 * @return
	 */
	private static String get4088Param(String rechargeType) {
		if ("03010008".equals(rechargeType)) {
			return "GMC405";
		}
		if ("05010005".equals(rechargeType)) {
			return "GMC403";
		}
		if ("04010003".equals(rechargeType)) {
			return "GMC404";
		}
		return "";
	}

	/**
	 * 获取产品编码
	 * 
	 * @param rechargeType
	 * @return
	 */
	private static String getProductCode(String rechargeType) {
		if ("03010008".equals(rechargeType)) {// 电信
			return "0003";
		}
		if ("05010005".equals(rechargeType)) {// 移动
			return "0005";
		}
		if ("04010003".equals(rechargeType)) {// 联通
			return "0004";
		}
		return "";
	}

	/**
	 * 获取充值业务类型说明
	 * 
	 * @param rechargeType
	 * @return
	 */
	private static String convertCodeToChar(String rechargeType) {
		if ("03010008".equals(rechargeType)) {
			return "全国电信直充-纯业务";
		} else if ("05010005".equals(rechargeType)) {
			return "全国移动直充-纯业务";
		} else if ("04010003".equals(rechargeType)) {
			return "全国联通直充-纯业务";
		} else {
			return "";
		}

	}
}
