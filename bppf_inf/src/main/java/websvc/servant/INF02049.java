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
import common.utils.DateTool;
import common.utils.JsonUtil;
import common.utils.OrderConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02049Request;
import common.xml.dp.DpInf02049Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @title INF02049.java
 * @description 理财明细列表查询接口业务处理类
 * @date 2014-02-07 14:57
 * @author lichunan
 * @version 1.0
 */
public class INF02049 {
	private static final Log logger = LogFactory.getLog(INF02049.class);
	public static String svcInfName = "INF02049";

	public static String execute(String in0, String in1) {
		DpInf02049Request dpRequest = null;
		DpInf02049Response resp = new DpInf02049Response();
		RespInfo respInfo = null;
		logger.info("INF02049请求参数：" + in1);
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String svcCode = WebSvcTool.getSvcCode(in0);
		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		try {
			dpRequest = new DpInf02049Request(in1);
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
				String itemNum = dataJSONObject.getString("totalSize");
				JSONArray resultJSONArray = dataJSONObject.getJSONArray("resultList");
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

	private static JSONObject getRequestJsonObject(DpInf02049Request dpRequest)
			throws JSONException {
		JSONObject requestJSONObject = new JSONObject();
		requestJSONObject.put("cumCustCode", dpRequest.getCustCode());
		requestJSONObject.put("serviceName", "queryBalanceChangeDtl");
		requestJSONObject.put("reqNo", dpRequest.getKeep());
		JSONObject dataJSONObject = new JSONObject();
		dataJSONObject.put("startDate", DateTool.strToDateFormatStr(
				dpRequest.getStartDate(), "yyyy-MM-dd"));
		dataJSONObject.put("endDate", DateTool.strToDateFormatStr(
				dpRequest.getEndDate(), "yyyy-MM-dd"));
		dataJSONObject.put("pageNo", dpRequest.getPageNo());
		dataJSONObject.put("pageSize", dpRequest.getPageSize());
		dataJSONObject.put("sortFlag", sortFlagConvert(dpRequest.getSortFlag()));
		dataJSONObject.put("operType", operTypeConvert(dpRequest.getDetailType()));
		requestJSONObject.put("data", dataJSONObject);
		return requestJSONObject;
	}
	
	private static String operTypeConvert(String beforeOperType){
		String afterOperType = "";
		if("01".equals(beforeOperType)){
			afterOperType = "";
		}else if("02".equals(beforeOperType)){
			afterOperType = "1";
		}else if("03".equals(beforeOperType)){
			afterOperType = "2";
		}else if("04".equals(beforeOperType)){
			afterOperType = "3";
		}else if("05".equals(beforeOperType)){
			afterOperType = "4";
		}
		return afterOperType;
	}
	
	private static String sortFlagConvert(String beforeSortFlag){
		String afterSortFlag = "";
		if("0".equals(beforeSortFlag)){
			afterSortFlag = "2";
		}else if("1".equals(beforeSortFlag)){
			afterSortFlag = "1";
		}
		return afterSortFlag;
	}
	
}
