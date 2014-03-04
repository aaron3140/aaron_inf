package websvc.servant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import common.dao.TCumAttrDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymStaffLogDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.socket.caller.SocketServiceCallImpl;
import common.utils.OrderConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02045Request;
import common.xml.dp.DpInf02045Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @title INF02045.java
 * @description 理财开户接口业务处理类
 * @date 2014-02-07 14:57
 * @author lichunan
 * @version 1.0
 */
public class INF02045 {
	private static final Log logger = LogFactory.getLog(INF02045.class);
	public static String svcInfName = "INF02045";

	public static String execute(String in0, String in1) {
		DpInf02045Request dpRequest = null;
		DpInf02045Response resp = new DpInf02045Response();
		RespInfo respInfo = null;
		logger.info("INF02045请求参数：" + in1);
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String svcCode = WebSvcTool.getSvcCode(in0);
		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		try {
			dpRequest = new DpInf02045Request(in1);
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

			/**
			 * 实名验证
			 */
			String custName = TCumInfoDao.getCustName(dpRequest.getCustCode());
			if (!dpRequest.getAccountName().equals(custName)) {
				throw new INFException(
						INFErrorDef.CUSTCODE_NOT_MATCH_ACCOUNTNAME,
						INFErrorDef.CUSTCODE_NOT_MATCH_ACCOUNTNAME_DESC);
			}
			JSONObject requestJSONObject = getRequestJsonObject(dpRequest,
					custName);
			SocketServiceCallImpl serviceCall = new SocketServiceCallImpl();
			JSONObject responseJSONObject = serviceCall.call(requestJSONObject);
			String responseCode = responseJSONObject.getString("retCode");// 默认返回码为失败
			String responseContent = responseJSONObject.getString("retMsg");
			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(),
					respInfo.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseContent, dpRequest.getCustCode(),
					dpRequest.getFundCode(), dpRequest.getRemark1(),
					dpRequest.getRemark2());
			return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);
		} catch (Exception e) {
			String oXml = ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), infId);
			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		}
	}

	private static JSONObject getRequestJsonObject(DpInf02045Request dpRequest,
			String custName) throws JSONException {
		JSONObject requestJsonObject = new JSONObject();
		requestJsonObject.put("cumCustCode", dpRequest.getCustCode());
		requestJsonObject.put("serviceName", "openAccount");
		requestJsonObject.put("reqNo", dpRequest.getKeep());
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("buyerName", custName);
		String buyerCertType = "1";// 暂时只支持身份证类型
		dataJsonObject.put("buyerCertType", buyerCertType);
		String buyerCertNo = TCumAttrDao.getCertNo(dpRequest.getCustCode());
		dataJsonObject.put("buyerCertNo", buyerCertNo);
		String mobileNumber = TCumInfoDao.getMobileByStaffCode(dpRequest
				.getStaffCode());
		dataJsonObject.put("mobileNumber", mobileNumber);
		String callNumber = TSymStaffLogDao.getPhone(dpRequest.getStaffCode());
		dataJsonObject.put("callNumber", callNumber);
		String address = TCumAttrDao.getAddr(dpRequest.getCustCode());
		dataJsonObject.put("address", address);
		String areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());
		dataJsonObject.put("areaCode", areaCode);
		String email = TSymStaffLogDao.getEmail(dpRequest.getStaffCode());
		dataJsonObject.put("email", email);
		dataJsonObject.put("fundNo", dpRequest.getFundCode());
		requestJsonObject.put("data", dataJsonObject);
		return requestJsonObject;
	}

}
