package websvc.servant;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.tisson.pay.config.BftProperties;
import common.algorithm.MD5;
import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TPnmPartnerDao;
import common.dao.TRegBindCardDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.entity.TRegBindCard;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.invoker.exception.ServiceInvokeException;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02030Request;
import common.xml.dp.DpInf02030Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 邱亚建 2013-11-13 上午10:50:30<br/>
 * 
 * 本类描述:账户绑卡通知接口
 */
public class INF02030 {

	public static String svcInfName = "INF02030";

	private static final Logger log = Logger.getLogger(INF02030.class);

	public static String executeForMD5(String in0, String in1) {

		DpInf02030Response resp = new DpInf02030Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf02030Request dpRequest = new DpInf02030Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {

				String tokenValidTime = TSymSysParamDao.getTokenValidTime();

				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(), tokenValidTime);

				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);

				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());

			}

			String oldXml = execute(in0, in1);

			return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);

		} catch (Exception e) {
			String oldXml = ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), null);

			return ResponseVerifyAdder.pkgForMD5(oldXml, null);
		}

	}

	public static String execute(String in0, String in1) {

		DpInf02030Request dpRequest = null;
		DpInf02030Response resp = null;
		RespInfo respInfo = null; // 返回信息头

		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;

		String keep = "";

		String ip = "";

		String responseCode = "";
		String responseDesc = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {

			dpRequest = new DpInf02030Request(in1);

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());// 返回信息头

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(), dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML", "CUSTCODE", dpRequest.getCustCode(), "",
					"", "S0A");

			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {
				boolean flag = manager.selectTInfOperInLogByKeep(dpRequest.getKeep());
				// 判断流水号是否可用
				if (flag) {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE, INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			String phoneReg = "^[0-9]{11,15}$";
			if (!dpRequest.getCustCode().matches(phoneReg)) {
				throw new Exception("客户编码必须是手机号码");
			}

			// 校验客户编码和用户名是否匹配
			TCumInfoDao cumInfoDao = new TCumInfoDao();
			String custCodeByStaff = cumInfoDao.getCustCodeByStaff(dpRequest.getStaffCode());
			if (!StringUtils.equals(custCodeByStaff, dpRequest.getCustCode())) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST, INFErrorDef.CUSTCODE_NOT_MATCH_STAFF_DESC);
			}

			TRegBindCardDao cardDao = new TRegBindCardDao();

			String operType = dpRequest.getOperType();
			String stat = cardDao.getOrderStat(dpRequest.getOrderNo());
			if (Charset.isEmpty(stat, true)) {
				throw new INFException(INFErrorDef.ORDER_NO_NOT_EXITS, INFErrorDef.ORDER_NO_NOT_EXITS_DESC);
			}
			//订单状态已更新
			if("S0X".equals(stat)){
				responseCode = "000000";
				responseDesc = "订单状态已更新";
				resp = new DpInf02030Response();
				return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, dpRequest.getRemark1(), dpRequest
						.getRemark2());
			}
			if ("0".equals(operType)) {// 未绑卡->绑卡中
				TRegBindCard card = new TRegBindCard();
				card.setBindOrderNo(dpRequest.getOrderNo());
				card.setBankCode(dpRequest.getBankCode());
				card.setBankName(dpRequest.getOpenBank());// ?
				card.setBankOpen(dpRequest.getOpenBank());
				card.setAreaCode(dpRequest.getAreaCode());
				card.setBankAcct(dpRequest.getBankAcct());
				card.setTransAccName(dpRequest.getTransAccName());
				card.setCerNo(dpRequest.getCerNo());
				card.setOpenPhone(dpRequest.getPhone());
				card.setRemark(dpRequest.getRemark1() + "::" + dpRequest.getRemark2());
				cardDao.updateBindStatToS0D(card);
			} else {// 绑卡中->绑卡失败
				Map map = cardDao.getOrderInfoByTransSeq(dpRequest.getOrderNo(),"S0D");
				if(map==null||map.isEmpty()){
					throw new INFException(INFErrorDef.ORDER_NO_NOT_EXITS, INFErrorDef.ORDER_NOT_MATCH_DESC);
				}
				//更新绑卡表
				cardDao.updateBindStateToFail(dpRequest.getOrderNo(), "客户端通知修改");
				
				PackageDataSet ds = callSCS0015(dpRequest.getOrderNo(), dpRequest.getCustCode());
				String resultCode = (String) ds.getParamByID("0001", "000").get(0);
				// 返回结果为失败时，抛出异常
				if (Long.valueOf(resultCode) == 0) {
					responseDesc = (String) ds.getParamByID("0002", "000").get(0);
				}

				String keepOld = ds.getByID("4017", "401");
				String orderNo = ds.getByID("4028", "401");// 外部订单号
				String tumNo = ds.getByID("4007", "401");// 终端号
				//通知核心
				try {
					ds =callSCS0013(dpRequest,keepOld,orderNo,tumNo,(String)map.get("BANK_ACCT"));
				} catch (ServiceInvokeException e) {
					 ds = e.getDataSet();
					 resultCode = (String) ds.getParamByID("0001", "000").get(0);
					 if(!"6000".equals(resultCode)){
						 throw e;
					 }
				}
				resultCode = (String) ds.getParamByID("0001", "000").get(0);
				// 返回结果为失败时，抛出异常
				if (Long.valueOf(resultCode) == 0) {
					responseDesc = (String) ds.getParamByID("0002", "000").get(0);
				}
				
			}
			responseCode = "000000";
			responseDesc = "成功";
			resp = new DpInf02030Response();
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, dpRequest.getRemark1(), dpRequest
					.getRemark2());
		} catch (XmlINFException spe) {
			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), id);
		} finally {
		}
	}

	/**
	 * 查询原订单信息
	 * 
	 * @param transData
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callSCS0015(String orderNo, String custCode) throws Exception {
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "4004");
		g002.put("0022", custCode);
		g002.endRow();
		// 交易流水号
		g002.put("0021", "4002");
		g002.put("0022", orderNo);
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
	private static PackageDataSet callSCS0013(DpInf02030Request dpRequest ,  String keep, String orderNo, String tumNo, String bankAcct) throws Exception {
		String accountType = getAccountType(dpRequest.getCustCode());
		String orderId = dpRequest.getOrderNo(); // 订单编码
		String payeeCode = dpRequest.getCustCode(); // 收款商户编码
		String txnAmount = BftProperties.getOrderAmount(); // 交易金额
		String actionCode = "01010007"; // 操作编码
		String mark1 = dpRequest.getRemark1()+"::"+dpRequest.getRemark2();
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
		g401.put("4142", "OT008");//回调标识
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
		g480.put("4805", "S0F"); // 交易确认
		g480.put("4802", "6000"); // 成功0000  失败6000
		g480.put("4803", "失败"); //成功/失败
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

}
