package websvc.servant;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TPnmPartnerDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.service.TransManage;
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.MathTool;
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf06200Request;
import common.xml.dp.DpInf06200Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 邱亚建 2013-9-4 下午02:48:13<br/>
 * 
 * 本类描述:全国多媒体付款接口
 */
public class INF06200 {
	private static final Log logger = LogFactory.getLog(INF06200.class);

	public static String svcInfName = "INF06200";

	public static String executeForMD5(String in0, String in1) {
		String oXml = execute(in0, in1);
		return ResponseVerifyAdder.pkgForMD5(oXml, null);
	}

	public static String execute(String in0, String in1) {
		DpInf06200Request dpRequest = null;

		DpInf06200Response resp = new DpInf06200Response();

		RespInfo respInfo = null;

		String responseCode = "";

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String keep = "";// 获取流水号

		String ip = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf06200Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			tmnNum = dpRequest.getTmnNum();

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "", "", "", "", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {

				boolean flag = manager.selectTInfOperInLogByKeep(keep);
				// 判断流水号是否可用
				if (flag) {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);

					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE, INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			// 判断有无权限

			boolean r = false;
			List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode(), ChannelCode.CHANELCODE_10);
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.MULTIMEDIA_PAYMENT.equals(str)) {
					r = true;
					break;
				}

			}

			if (!r) {
				throw new Exception("你没有全国多媒体付款的权限");
			}

			TransManage transManage = new TransManage();

			// IPOS处理
			if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())) {

				Map<String, String> map = transManage.getCustCodeByExtTermNumNo(dpRequest.getTmnNumNo());
				if (map != null && map.size() != 0) {
					String custCode = map.get("CUST_CODE");
					String tmnNumNo = map.get("TERM_CODE");
					dpRequest.setCustCode(custCode);
					dpRequest.setTmnNumNo(tmnNumNo);
				} else {
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_DESC);
				}
			} else {
				if (dpRequest.getTmnNumNo().length() < 12) {
					String tmnNumNo = transManage.getTermNumNoByExt(dpRequest.getTmnNumNo(), dpRequest.getCustCode());
					if (tmnNumNo != null && !"".equals(tmnNumNo)) {
						dpRequest.setTmnNumNo(tmnNumNo);
					} else {
						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.TMNNUMNO_NOT_MATCH_DESC);
					}
				}
				//内部终端是否与cust_code匹配
				boolean flag = transManage.getTermNumNoByCustCode(dpRequest.getCustCode(),dpRequest.getTmnNumNo());
				if(!flag){
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.TMNNUMNO_NOT_MATCH_DESC_2);
				}
			}

			// 关联机构验证
			if (!TCumInfoDao.verifyMerIdCustCode(dpRequest.getCustCode(), dpRequest.getMerId())) {

				if (TCumInfoDao.getMerIdByCustCode(dpRequest.getCustCode(), dpRequest.getMerId()))
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			}

			PackageDataSet ds = trade(dpRequest);// 交易
			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			String responseDesc = null;
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}

			String transSeq = ds.getByID("4002", "401");

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, transSeq, dpRequest.getOrderSeq(),
					dpRequest.getTmnNumNo(), dpRequest.getRemark1(), dpRequest.getRemark2());
		} catch (XmlINFException spe) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), infId);

		}
	}

	/**
	 * 调用SCS0001 进行交易
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet trade(DpInf06200Request dpRequest) throws Exception {

		TCumAcctDao acctDao = new TCumAcctDao();
		String bankCode = acctDao.getBankCode(dpRequest.getBankAcct());// 银行编码
		String areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());// 区域编码

		PackageDataSet dataSet2 = null;
		TPnmPartnerDao dao = new TPnmPartnerDao();
		String patnercode = dao.getPrtnCode301ByCustCode(dpRequest.getCustCode());
		try {
			// 一次路由
			TransManage tm = new TransManage();
			dataSet2 = tm.firstRoute(dpRequest.getCustCode(), areaCode, dpRequest.getChannelCode(), "17010001", patnercode, dpRequest.getTmnNum(), dpRequest.getTxnAmount(),
					"PT1003", bankCode);
		} catch (Exception e) {
			throw new INFException(INFErrorDef.CAN_NOT_FIND_ROUTE, INFErrorDef.CAN_NOT_FIND_ROUTE_DESC);
		}
		String newOrgCode = dataSet2.getByID("4098", "423");
		String newActionCode = dataSet2.getByID("4051", "423");
		String txnAmount = MathTool.pointToYuan(dpRequest.getTxnAmount());// 分转元
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", dpRequest.getTmnNumNo());// 受理终端号
		g401.put("4008", dpRequest.getAcceptDate());// 受理时间
		g401.put("4012", "多媒体[纯支付业务]");// 订单描述，目前是硬编码
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 终端流水号
		g401.put("4018", dpRequest.getTmnNumNo());// 操作原始来源
		g401.put("4042", "OT001");// 
		g401.put("4028", dpRequest.getOrderSeq());// 
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", txnAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", txnAmount);// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4064", dpRequest.getCustCode());
		g404.put("4049", "0002");// 产品编码
		g404.put("4051", newActionCode);// 业务编码
//		g404.put("4052", dpRequest.getBankAcct());// 业务对象
		g404.put("4052", dpRequest.getBusObject());// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");
		g404.put("4064", "");
		g404.put("4072", newActionCode);
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		// 控制标识说明:0 核心添加到数据库 1 不添加
		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_TRADETYPE");
		g407.put("4088", "002201");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_PINDATA");
		g407.put("4088", dpRequest.getPayPassword());
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_PCICTRL");
		g407.put("4088", dpRequest.getPsamCardNo());
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_CARDTYPE");
		g407.put("4088", dpRequest.getCardFlag());// ????
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDTYPE");
		g407.put("4088", dpRequest.getPrivateFlag());
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT1003");// 支付方式编码
		g408.put("4098", newOrgCode);// 支付机构编码
		g408.put("4099", "0007");// 账户类型编码
		g408.put("4101", dpRequest.getBankAcct());// 账号
		g408.put("4102", dpRequest.getPayPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.put("4107", dpRequest.getTrackTwo());// 磁道2信息
		g408.put("4108", dpRequest.getTrackThree());// 磁道3信息
		g408.put("4109", dpRequest.getNetworkNo());
		g408.put("4119", "");
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}

	private static boolean isEmpty(String str) {
		return Charset.isEmpty(str, true);
	}
}
