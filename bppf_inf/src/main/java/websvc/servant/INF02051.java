package websvc.servant;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.socket.caller.SocketServiceCallImpl;
import common.utils.JsonUtil;
import common.utils.OrderConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02051Request;
import common.xml.dp.DpInf02051Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @title INF02051.java
 * @description 理财历史利率查询接口业务处理类
 * @date 2014-02-07 14:57
 * @author lichunan
 * @version 1.0
 */
public class INF02051 {
	private static final Log logger = LogFactory.getLog(INF02051.class);
	public static String svcInfName = "INF02051";

	public static String execute(String in0, String in1) {
		DpInf02051Request dpRequest = null;
		DpInf02051Response resp = new DpInf02051Response();
		RespInfo respInfo = null;
		logger.info("INF02051请求参数：" + in1);
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String svcCode = WebSvcTool.getSvcCode(in0);
		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		try {
			dpRequest = new DpInf02051Request(in1);
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

			JSONObject requestJSONObject = getRequestJsonObject(dpRequest);
			SocketServiceCallImpl serviceCall = new SocketServiceCallImpl();
			JSONObject responseJSONObject = serviceCall.call(requestJSONObject);
			String responseCode = responseJSONObject.getString("retCode");// 默认返回码为失败
			String responseContent = responseJSONObject.getString("retMsg");
			String oXml = "";
			if ("0000".equals(responseCode)) {
				JSONObject dataJSONObject = (JSONObject) responseJSONObject
						.get("data");
				String itemNum = dataJSONObject.getString("count");
				JSONArray resultJSONArray = dataJSONObject.getJSONArray("profits");
				List<Map<String, Object>> list = JsonUtil.getList(resultJSONArray);
				oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseContent, itemNum, list);
			} else {
				oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(),
						respInfo.getRespType(), respInfo.getKeep(), "SUCCESS",
						responseCode, responseContent, "", null);
			}
			return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);
		} catch (Exception e) {
			String oXml = ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), infId);
			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		}
	}

	private static JSONObject getRequestJsonObject(DpInf02051Request dpRequest)
			throws JSONException {
		JSONObject requestJsonObject = new JSONObject();
		requestJsonObject.put("cumCustCode", dpRequest.getCustCode());
		requestJsonObject.put("serviceName", "queryFundProfit");
		requestJsonObject.put("reqNo", dpRequest.getKeep());
		JSONObject dataJSONObject = new JSONObject();
		dataJSONObject.put("fundNo", dpRequest.getFundCode());
		requestJsonObject.put("data", dataJSONObject);
		return requestJsonObject;
	}

}
