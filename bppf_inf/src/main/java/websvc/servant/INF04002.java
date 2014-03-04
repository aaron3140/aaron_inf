package websvc.servant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import common.utils.OrderConstant;
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf04002Request;
import common.xml.dp.DpInf04002Response;
import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF04002 {

	private static final Log logger = LogFactory.getLog(INF04002.class);

	public static String svcInfName = "INF04002";

	public static String execute(String in0, String in1) {
		DpInf04002Request dpRequest = null;
		DpInf04002Response resp = new DpInf04002Response();
		RespInfo respInfo = null;
		String responseCode = "";
		logger.info("INF04002请求参数：：" + in1);
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String keep = "";// 获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		String tmnNum = null;
		INFLogID infId = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		Long consumId = null;
		try {
			respInfo = new RespInfo(in1, "20");
			dpRequest = new DpInf04002Request(in1);
			boolean ivr=false;
			//00 表示密码操作权限验证 01表示查询权限验证
//			if (dpRequest.getPasswordType().equals("00")) {
//				 ivr = PayCompetenceManage.getIvrFunc(dpRequest.getStaffId(), PrivConstant.IPOS_RECHARGE_IVR_CZ);
//			} else if (dpRequest.getPasswordType().equals("01")) {
//				 ivr = PayCompetenceManage.getIvrFunc(dpRequest.getStaffId(), PrivConstant.IPOS_RECHARGE_IVR_Cx);
//			}
			ivr = PayCompetenceManage.getIvrFunc(dpRequest.getStaffId(), PrivConstant.IVR_MANAGE_IVR_AUTH);
			if (!ivr) {
				throw new Exception("你没有密码鉴权权限");
			}
			// 关联机构验证
//			if (!TCumInfoDao.verifyMerIdCustCode(dpRequest.getCustCode(), dpRequest.getMerId())) {
//
//				if (TCumInfoDao.getMerIdByCustCode(dpRequest.getCustCode(), dpRequest.getMerId()))
//
//					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
//			}
			if (!TCumInfoDao.verifyMerIdCustCode(dpRequest.getCustCode(), dpRequest.getMerId())) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_DESC_3);
			}
			tmnNum = dpRequest.getTmnNum();
			keep = dpRequest.getKeep();
			ip = dpRequest.getIp();
			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "", "", "", "", OrderConstant.S0A);
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
			// 密码验证
			PackageDataSet dataSet = callCUM2005(dpRequest);
			String resCode = dataSet.getByID("0001", "000");
			if (Long.valueOf(resCode) != 0) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.PAY_PWD_FAULT);
			}
			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", "000000", "成功", dpRequest.getOrderNo());
			return ResponseVerifyAdder.pkgForMD5(oXml, "");

		} catch (XmlINFException spe) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), OrderConstant.S0A);
			}
			spe.setRespInfo(respInfo);

			String oXml = ExceptionHandler.toXML(spe, infId);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		} catch (Exception e) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), OrderConstant.S0A);
			}
			String oXml = ExceptionHandler.toConsumeXML(new XmlINFException(resp, e, respInfo), infId, consumId);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		}
	}

	private static PackageDataSet callCUM2005(DpInf04002Request dpRequest) throws Exception {

		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", dpRequest.getStaffId());
		g200.put("2903", "2007");
		g200.put("2904", dpRequest.getPassword());
		g200.put("2172", "0001");
		if (dpRequest.getPasswordType().equals("00")) {
			g200.put("2173", "0003");// 0003：操作密码
		} else if (dpRequest.getPasswordType().equals("01")) {
			g200.put("2173", "0004");// 0004：查询密码
		}
		g200.put("2025", "");
		g200.endRow();
		
		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", dpRequest.getChannelCode());
		g211.put("2077", dpRequest.getTmnNum());
		g211.put("2078", dpRequest.getStaffId());
		g211.put("2085", dpRequest.getStaffId());
		g211.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM1003", g200, g211);

		return dataSet;
	}

}
