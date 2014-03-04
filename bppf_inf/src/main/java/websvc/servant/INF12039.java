package websvc.servant;

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
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.utils.MathTool;
import common.utils.PrivConstant;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf12039Request;
import common.xml.dp.DpInf12039Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF12039 {

	public static String svcInfName = "INF12039";

	private static final Log logger = LogFactory.getLog(INF12039.class);

	public static String execute(String in0, String in1) {

		DpInf12039Request dpRequest = null;

		RespInfo respInfo = null; // 返回信息头

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		DpInf12039Response resp = null;

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf12039Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode()); // 返回信息头

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"agentCode", dpRequest.getCustCode(), "", "", "S0A");

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

			// 判断有无交易查询权限
			List<?> privList = PayCompetenceManage.payFunc(dpRequest
					.getCustCode(), "80");
			boolean re = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.WS_EXP_TICKET.equals(str)) {
					re = true;
					break;
				}

			}

			if (!re) {
				throw new Exception("你没有火车票出票权限");
			}

			// 业务组件
			SignBankManage manage = new SignBankManage();

			// 获取客户ID
			String custId = manage.getCustIdByCode(dpRequest.getCustCode());

			if (null == custId || "".equals(custId)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
						INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
			}

			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if (!(dpRequest.getAcceptDate().matches(RegexTool.DATE_FORMAT))) {

				throw new Exception("输入日期格式不正确");
			}

			String responseDesc = "";

			String transSeq = "";

			String systemNo = "";

			String tradeTime = "";

			String ide = "";

			String orderId = "";

			String prouductCode = "0002";

			String actionCode = "01010013";

			PackageDataSet ds = scs0001(dpRequest, actionCode, prouductCode);

			String responseCode = (String) ds.getParamByID("0001", "000")
					.get(0);

			if (Long.valueOf(responseCode) == 0) {

				responseDesc = (String) ds.getParamByID("0002", "000").get(0);

				transSeq = (String) ds.getParamByID("4002", "401").get(0);

				systemNo = (String) ds.getParamByID("4062", "401").get(0);

				tradeTime = (String) ds.getParamByID("4010", "401").get(0);

				ide = (String) ds.getParamByID("6920", "692").get(0);

				orderId = (String) ds.getParamByID("6923", "692").get(0);

			}

			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
					dpRequest.getKeep(), dpRequest.getIp(), svcCode, "",
					"000000", "S0A");

			// 返回结果
			resp = new DpInf12039Response();

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, systemNo, dpRequest.getTxnAmount(),dpRequest
							.getOrderSeq(), transSeq, tradeTime, ide, orderId,
					dpRequest.getRemark1(), dpRequest.getRemark2());

		} catch (XmlINFException spe) {

			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);

		} catch (Exception e) {

			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), id);
		}
	}

	// @SuppressWarnings("unchecked")
	// public static List<List<Map<String, String>>> unpack(PackageDataSet ds) {
	//
	// String count = (String) ds.getParamByID("6900", "690").get(0);
	//
	// ArrayList value6920 = ds.getParamByID("6920", "692");
	// ArrayList value6923 = ds.getParamByID("6923", "692");
	// List<List<Map<String, String>>> data = new ArrayList<List<Map<String,
	// String>>>();
	//
	// int lenght = Integer.parseInt(count);
	// int num = 12;// R**每一个账单有21个参数
	//
	// for (int i = 0; i < lenght; i++) {
	// List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	// data.add(list);
	// int startIndex = i * num;// 本次循环取数据开始的索引【包含】
	// int endIndex = startIndex + num;// 本次循环取数据结束的索引【不包含】
	//
	// for (int j = startIndex; j < endIndex; j++) {
	// String key = (String) value6920.get(j);
	// String value = (String) value6923.get(j);
	//
	// Map<String, String> map = new HashMap<String, String>();
	// list.add(map);
	// map.put("KEY", key);
	// map.put("VALUE", value);
	// }
	// }
	// return data;
	// }

	/**
	 * 调用SCS0001接口充值
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet scs0001(DpInf12039Request dpRequest,
			String actionCode, String proudCode) throws Exception {

		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 银行帐号
		String area_code = TCumInfoDao.getAreaCode(dpRequest.getCustCode());
		
		String txtAmount = MathTool.pointToYuan(dpRequest.getTxnAmount());

		String bankCode = "110000"; // 110000
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", area_code);// 所属区域编码 广州地区440100
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", dpRequest.getAcceptDate());// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 
		g401.put("4018", dpRequest.getTmnNum());// 受理终端号
		g401.put("4012", "火车票出票[纯业务]");// 订单描述
		g401.put("4028", dpRequest.getOrderSeq());// 外部订单号
		g401.put("4029", dpRequest.getOutCustSign());// 外部客户描述
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", txtAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", txtAmount);// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", proudCode);// 产品编码 0013
		g404.put("4051", actionCode);// 业务编码 13010001
		g404.put("4052", "");// 充值号码
		g404.put("4053", "1");// 业务数量
		g404.put("4062", dpRequest.getSystemNo());
		g404.put("4064", "");
		g404.put("4072", "");
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txtAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txtAmount);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_SERVID");
		g407.put("4088", "GOC401");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT2001");// 支付方式编码
		g408.put("4127", bankCode);// 前置支付机构,查卡表的bankcode
		g408.put("4098", bankCode);// 
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", "123456");// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txtAmount);// 支付金额

		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}

}
