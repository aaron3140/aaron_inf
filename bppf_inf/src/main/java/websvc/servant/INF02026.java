package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf02026Request;
import common.xml.dp.DpInf02026Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02026 {

	public static String svcInfName = "INF02026";

	public static String execute(String in0, String in1) {

		DpInf02026Request dpRequest = null;

		RespInfo respInfo = null; // 返回信息头

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		DpInf02026Response resp = null;

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf02026Request(in1);

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

			// 判断有无交易查询权限
			List privList = PayCompetenceManage
					.payFunc(dpRequest.getCustCode(),"80");
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.WS_PAY_TRAN.equals(str)) {
					r = true;
					break;
				}

			}

			if (!r) {
				throw new Exception("你没有广州后付费交易权限");
			}

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"agentCode", dpRequest.getCustCode(), "", "", "S0A");

			if (Double.valueOf(dpRequest.getTxnamount()) < 1) {
				throw new Exception("金额不能少于1分钱");
			}

			if (dpRequest.getPayType().equals("0")) {
				throw new Exception("目前只支持现金支付");
			}
			String actionCode = "03010002";
			String proudCode = "0003";
			String resultCode = "";
			String responseDesc = "";
			String transSeq = "";
			Hashtable<String, Object> m = process1(dpRequest, actionCode,
					proudCode);
			resultCode = (String) m.get("ResultCode");
			responseDesc = (String) m.get("ResponseDesc");
			transSeq = (String) m.get("TransSeq");

			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
					dpRequest.getKeep(), dpRequest.getIp(), svcCode, "",
					"000000", "S0A");
			// 返回结果
			resp = new DpInf02026Response();
			// 单位转换：元转分
			String txanAmount = MathTool.yuanToPoint(dpRequest.getTxnamount());
			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS", resultCode,
					responseDesc, dpRequest.getTmnNumNo(), dpRequest
							.getOrderNo(), transSeq, dpRequest.getSystemo(),
					txanAmount, dpRequest.getRemark1(), dpRequest.getRemark2());
			return oXml;

		} catch (XmlINFException spe) {
			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, id);

		} catch (Exception e) {

			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), id);

		}

	}

	private static Hashtable<String, Object> process1(
			DpInf02026Request dpRequest, String actionCode, String proudCode)
			throws Exception {

		// 业务组件
		SignBankManage manage = new SignBankManage();
		// 获取客户ID
		String custId = manage.getCustIdByCode(dpRequest.getCustCode());
		if (null == custId || "".equals(custId)) {
			throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
					INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
		}

		PackageDataSet ds = null;
		String responseDesc = "";
		String resultCode = "";

		// 单位转换：分转元
		String txnAmount = MathTool.pointToYuan(dpRequest.getTxnamount());
		dpRequest.setTxnamount(txnAmount);
		// 充值
		ds = recharge(dpRequest, actionCode, proudCode);
		resultCode = (String) ds.getParamByID("0001", "000").get(0);
		// 返回结果为失败时，获取结果描述
		if (Long.valueOf(resultCode) == 0) {
			responseDesc = (String) ds.getParamByID("0002", "000").get(0);

		}
		String transSeq = ds.getByID("4002", "401");
		Hashtable<String, Object> map = new Hashtable<String, Object>();
		map.put("ResultCode", resultCode);
		map.put("ResponseDesc", responseDesc);
		map.put("TransSeq", transSeq);

		return map;

	}

	/**
	 * 调用SCS0001接口充值
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet recharge(DpInf02026Request dpRequest,
			String actionCode, String proudCode) throws Exception {
		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 银行帐号
		String area_code = TCumInfoDao.getAreaCode(dpRequest.getCustCode());

		String bankCode = "110000";
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", area_code);// 所属区域编码
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		g401.put("4008", sdf.format(new Date())); // 受理时间
		g401.put("4012", "广州电信后付费缴费");// 订单描述
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode60To20());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 
		g401.put("4018", dpRequest.getTmnNum());// 受理终端号
		g401.put("4028", dpRequest.getOrderNo());// 
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", dpRequest.getTxnamount());// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", dpRequest.getTxnamount());// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", proudCode);// 产品编码 0003
		g404.put("4051", actionCode);// 业务编码 03010002
		g404.put("4052", dpRequest.getContactPhone());// 充值号码
		g404.put("4053", "1");// 业务数量
		g404.put("4062", dpRequest.getSystemo());// 业务系统参考号
		g404.put("4072", actionCode);
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", dpRequest.getTxnamount());// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", dpRequest.getTxnamount());// 业务单应付金额
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
		g407.put("4088", "6");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE1");
		g407.put("4088", "J006");
		g407.put("4089", "缴费类型");
		g407.put("4091", "01");
		g407.put("4093", "1"); //
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE2");
		g407.put("4088", "J029");
		g407.put("4089", "银行代码");
		g407.put("4091", "01");
		g407.put("4093", bankCode); //
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE3");
		g407.put("4088", "J030");
		g407.put("4089", "银行账号");
		g407.put("4091", "01");
		g407.put("4093", acctCode);
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE4");
		g407.put("4088", "J009");
		g407.put("4089", "原终端号");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getTmnNumNo());
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE5");
		g407.put("4088", "J010");
		g407.put("4089", "终端号流水");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getKeep());
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE6");
		g407.put("4088", "J040");
		g407.put("4089", "业务代码");
		g407.put("4091", "01");
		g407.put("4093", "0241");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE7");
		g407.put("4088", "J021");
		g407.put("4089", "合同号");
		g407.put("4091", "01");
		g407.put("4093", "123456");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_SERVID");
		g407.put("4088", "PTC403");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		if (dpRequest.getPayType().equals("9")) {
			g408.put("4097", "PT2001");// 支付方式编码
		} else {
			g408.put("4097", "PT0004");// 支付方式编码
		}
		g408.put("4127", bankCode);// 前置支付机构,查卡表的bankcode
		g408.put("4098", bankCode);// 
		g408.put("4099", "0007");// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", dpRequest.getOperPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", dpRequest.getTxnamount());// 支付金额
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

}
