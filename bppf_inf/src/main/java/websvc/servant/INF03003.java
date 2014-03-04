package websvc.servant;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TOppOrdfeeDao;
import common.dao.TOppPreOrderDao;
import common.dao.TSymStaffRoleDao;
import common.dao.TSymSysParamDao;
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
import common.utils.MathTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf03003Request;
import common.xml.dp.DpInf03003Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 收款请求接口
 **/
public class INF03003 {

	public static String svcInfName = "03003";

	public static String execute(String in0, String in1) {
		DpInf03003Request request = null; // 入参对象
		DpInf03003Response resp = null; // 出参对象
		RespInfo respInfo = null; // 返回信息头
		String tmnNum = null; // 受理终端号

		TransManage manage = new TransManage(); // 业务组件

		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "019999";
		String keep = "";// 获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			respInfo = new RespInfo(in1, "10"); // 返回信息头

			// 获取入参对象
			request = new DpInf03003Request(in1);
			tmnNum = request.getTmnNum();
			keep = request.getKeep();
			ip = request.getIp();
			String channelCode = request.getChannelCode();
			// 出参对象
			resp = new DpInf03003Response();

			String agentCode = request.getAgentCode(); // 商户编码
			String payeeCode = request.getPayeeCode(); // 收款商户编码
			String txnAmount = request.getTxnAmount(); // 交易金额：分为单位

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "agentCode", agentCode, "payeeCode", payeeCode, "S0A");
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

			boolean flag = false;
			List privList = PayCompetenceManage.payFunc(payeeCode, channelCode);
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if ("cln_Collection".equals(str) && ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
					flag = true;
				} else if ("cln_Collection".equals(str)) {
					flag = true;
				}
			}
			if (!flag) {
				throw new Exception("没有客户端-收款请求权限");
			}
			// 判断金额是否为0
			if (Double.parseDouble(txnAmount) <= 0) {
				throw new Exception("交易金额不能为0");
			}
			// 判断收款方和付款方是否是同一个
			if (agentCode.equals(payeeCode)) {
				throw new Exception("收款方和付款方不能相同");
			}

			// 单位转换：分转元
			txnAmount = MathTool.pointToYuan(txnAmount);
			request.setTxnAmount(txnAmount);

			// 格式化得到六位日期
			SimpleDateFormat setDateFormat = new SimpleDateFormat("yyMMdd");
			String time = setDateFormat.format(Calendar.getInstance().getTime());
			DecimalFormat df = new DecimalFormat("000000000"); // sequence位数不足就在前面补0
			// String nineStr=df.format(Integer.parseInt(sqPreorder)); //得到9位流水号
			SimpleDateFormat setDate = new SimpleDateFormat("yyyyMMddHHmmss"); // 时间格式化
			String acctDatestr = setDate.format(Calendar.getInstance().getTime());
			int i = 1;
			// Date acctDate=new Date();
			TOppPreOrderDao dao = new TOppPreOrderDao();
			TSymStaffRoleDao roleDao = new TSymStaffRoleDao();
			boolean isAdminRole = roleDao.adminRole(request.getStaffCode());
			String stat;
			if (isAdminRole) {
				stat = "S0C";
			} else {
				stat = "S0V";
			}
			// 生成预处理标识
			String sqPREID = dao.getSequence();
			String PREID = time + df.format(Integer.parseInt(sqPREID));
			// 生成批次号
			DecimalFormat df0 = new DecimalFormat("0000"); // sequence位数不足就在前面补0
			String size = df0.format(1);
			String BATCHCODE = time + size + df.format(Integer.parseInt(sqPREID));
			// 插入预处理单
			dao.addPreorder(sqPREID, BATCHCODE, acctDatestr + i, request.getStaffCode(), request.getAgentCode(), request.getPayeeCode(), tmnNum, channelCode,
					request.getAreaCode(), request.getRemark1(), stat);
			TOppOrdfeeDao ordfeeDao = new TOppOrdfeeDao();
			ordfeeDao.addOrdfee(sqPREID, txnAmount, txnAmount);

			String resultCode = "0000";
			responseCode = resultCode;

			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, "成功", "S0A");

			// 出参
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, "成功", String.valueOf(Long.parseLong(PREID)),
					txnAmount);

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
		}
	}

	public static String executeForMD5(String in0, String in1) {
		DpInf03003Request request = null; // 入参对象
		DpInf03003Response resp = new DpInf03003Response(); // 出参对象
		RespInfo respInfo = null; // 返回信息头
		String md5Key = null;
		try {
			respInfo = new RespInfo(in1, "10");
			request = new DpInf03003Request(in1);
			// 客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(request.getChannelCode())) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(request.getStaffCode(), tokenValidTime);
				request.verifyByMD5(request.xmlSubString(in1), md5Key);
				TInfLoginLogDao.updateRanduseTimeByStaffCode(request.getStaffCode());
			}

			// String oldXml = execute(in0, in1);
			// return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);
			PackageDataSet dataSet = callCUM1003(request);
			String responseCode = dataSet.getByID("0001", "000");
			if (Long.valueOf(responseCode) == 0) {
				String oldXml = execute(in0, in1);

				return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);
			} else {
				throw new Exception("密码校验失败!");
			}
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), null);
		}
	}

	private static PackageDataSet callCUM1003(DpInf03003Request dpRequest) throws Exception {

		// DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		// String tradeTime = df.format(new Date());
		String staff = dpRequest.getStaffCode();

		String verityType = "0001"; // 支付密码

		String tmnNum = dpRequest.getTmnNum();

		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", staff);
		g200.put("2903", "2007");
		g200.put("2904", dpRequest.getPassword());
		g200.put("2172", "0001");
		g200.put("2173", verityType);
		// g200.put("2025", null);
		g200.endRow();

		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", dpRequest.getChannelCode());
		g211.put("2077", tmnNum);
		g211.put("2078", null);
		g211.put("2085", dpRequest.getIp());
		g211.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM1003", g200, g211);

		return dataSet;
	}
}
