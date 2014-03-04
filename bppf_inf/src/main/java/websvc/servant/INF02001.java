package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import common.utils.ValueUtil;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf02001Request;
import common.xml.dp.DpInf02001Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 交易查询接口INF02001
 */
public class INF02001 {

	public static String svcInfName = "02001";

	public static String execute(String in0, String in1) {
		DpInf02001Request dpRequest = null;
		DpInf02001Response resp = new DpInf02001Response();
		RespInfo respInfo = null; // 返回信息头
		String tmnNum = null;
		String custCode = null;
		String objectCode = null;
		String transCard = null;

		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";

		String tempStr = "";
		String tempVal = "";
		String keep = "";// 获取流水号
		String ip = "";
		String keepNo = "";// 订单号
		String orderNo = "";// 订单号
		String transSeq = "";// 交易流水号
		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			respInfo = new RespInfo(in1, "20"); // 返回信息头
			dpRequest = new DpInf02001Request(in1);

			tmnNum = dpRequest.getTmnNum();
			custCode = dpRequest.getCustCode();
			objectCode = dpRequest.getObjectCode();
			keep = dpRequest.getKeep();
			orderNo = dpRequest.getOrderNo();
			keepNo = dpRequest.getKeepNo();
			transSeq = dpRequest.getTransSeq();
			ip = dpRequest.getIp();

			if (Charset.isEmpty(custCode)) {
				tempStr = "objectCode";
				tempVal = objectCode;
				if (Charset.isEmpty(tempVal)) {
					throw new Exception("业务号码不能为空");
				}
			} else {
				tempStr = "custCode";
				tempVal = custCode;
				if (Charset.isEmpty(tempVal)) {
					throw new Exception("客户编码不能为空");
				}
			}

			if (Charset.isEmpty(keepNo) && Charset.isEmpty(orderNo) && Charset.isEmpty(transSeq)) {
				throw new Exception("查询条件不足");
			}

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", tempStr, tempVal, "transCard", transCard, "S0A");
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

			// 判断有无交易查询权限[输入为客户编码时]
			boolean flag = false;
			List privList = null;
			TCumInfoDao dao = new TCumInfoDao();

			// 客户号是否代收付类型
			if (TCumInfoDao.isDSFCust(custCode)) {
				String enterpriseCode = dao.getCustCodeFromDSF(tempVal);
				privList = PayCompetenceManage.payFunc(enterpriseCode, dpRequest.getChannelCode());
			} /*else {
				privList = PayCompetenceManage.payFunc(tempVal, dpRequest.getChannelCode());
			}*/
			if(privList != null && privList.size()>0) {
				for (int i = 0, l = privList.size(); i < l; i++) {
					Map map = (Map) privList.get(i);
					String str = map.get("PRIV_URL").toString();
					if ("ws_OrderCompositeQuery".equals(str)) {
						flag = true;
					}
				}
				if (!flag) {
					throw new Exception("没有交易综合查询权限");
				}
			}

			IParamGroup g002 = new ParamGroupImpl("002");
			// 订单号
			if (!isEmpty(dpRequest.getOrderNo())) {
				g002.put("0021", "4028");
				g002.put("0022", dpRequest.getOrderNo());
				g002.endRow();
			}
			// KEEPNO值
			if (!isEmpty(dpRequest.getKeepNo())) {
				g002.put("0021", "4017");
				g002.put("0022", dpRequest.getKeepNo());
				g002.endRow();
			}
			PackageDataSet ds = null;
			try {
				IServiceCall caller = new ServiceCallImpl();
				String queryMode = dpRequest.getQueryMode();
				if("1".equals(queryMode)){
					ds = caller.call("SCS", "SCS0011", g002);//查询预授权，担保交易记录
				}else{
					if (!Charset.isEmpty(dpRequest.getCustCode())) {
						// 客户编码
						g002.put("0021", "4004");
						g002.put("0022", dpRequest.getCustCode());
						g002.endRow();
					} else {
						// 业务号码
						g002.put("0021", "4052");
						g002.put("0022", dpRequest.getObjectCode());
						g002.endRow();
					}
					// 交易流水号
					if (!isEmpty(dpRequest.getTransSeq())) {
						g002.put("0021", "4002");
						g002.put("0022", dpRequest.getTransSeq());
						g002.endRow();
					}
					String dataType = dpRequest.getDataType();
					if("0".equals(dataType)){
						g002.put("0021", "4080");
						g002.put("0022", "QM01");
						g002.endRow();
					}else if("1".equals(dataType)){
						g002.put("0021", "4080");
						g002.put("0022", "QM02");
						g002.endRow();
					}
					
					ds = caller.call("SCS", "SCS0015", g002);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) ds.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}

			responseCode = ds.getByID("0001", "000");// 获取接口的000组的0001参数
			String responseDesc = ds.getByID("0002", "000");// 获取接口的000组的0002参数
			// String startDate = ds.getByID("0012", "001");// 开始时间
			// String endDate = ds.getByID("0013", "001");// 结束时间
			// startDate = "201304191212";// 开始时间
			// endDate = "201304191568";// 结束时间
			
			
			//String operCode=ds.getByID("401", "4060");
			//String operDesc=ds.getByID("401", "4061");
			
			List<Map<String, String>> list = unpackSCS0015(ds);
			
			if (list != null && list.size() > 0) {
				for (Map<String, String> map : list) {
					map.put("ORDERSEQ", ValueUtil.s(map.get("tmnRunNbr")));
					map.put("TRANSSEQ", ValueUtil.s(map.get("odrCode401")));
					map.put("CUSTCODE", ValueUtil.s(map.get("custCode")));
					map.put("CUSTNAME", ValueUtil.s(map.get("custName")));
					map.put("ORDERTYPECODE", ValueUtil.s(map.get("odrType")));
					map.put("ORDERTYPENAME", ValueUtil.s(map.get("orderTypeName")));
					map.put("AREANAME", ValueUtil.s(map.get("areaName")));
					map.put("TMNNUM", ValueUtil.s(map.get("accTmnNbr")));
					map.put("ORDERTIME", ValueUtil.s(map.get("beginDate")));
					map.put("MEMO", ValueUtil.s(map.get("memo")));
					map.put("ORDERSTAT", ValueUtil.s(map.get("odrStatName")));
					map.put("ORDERAMOUNT", MathTool.yuanToPoint(map.get("duePaid")));
					map.put("PAYSTAT", ValueUtil.s(map.get("payStatName")));
					map.put("BUSICODE", ValueUtil.s(map.get("busiCode")));
					map.put("BUSINAME", ValueUtil.s(map.get("busiName")));
					map.put("KEEP", ValueUtil.s(map.get("keep")));
					map.put("OPERCODE", ValueUtil.s(map.get("operCode")));//操作结果编码
					map.put("OPERDESC", ValueUtil.s(map.get("operDesc")));//操作结果说明
					/*if(operCode!=null&&!"".equals(operCode)){
						map.put("OPERCODE", ValueUtil.s(operCode));
					}
					if(operDesc!=null&&!"".equals(operDesc)){
						map.put("OPERDESC", ValueUtil.s(operDesc));
					}*/
				}
			}else{
//				throw new INFException(INFErrorDef.NO_TRANSACTION_RECORD, INFErrorDef.NO_TRANSACTION_RECORD_DESC);
				responseCode =INFErrorDef.NO_TRANSACTION_RECORD;
				responseDesc = INFErrorDef.NO_TRANSACTION_RECORD_DESC;
			}
			
			

			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, responseDesc, "S0A");

			resp = new DpInf02001Response();
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, list);
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

	private static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof String) {
			if (((String) o).trim().length() < 1) {
				return true;
			}
		}
		return false;
	}

	private static String validDate(String d) {
		if (d == null) {
			return "值为空";
		}

		if (d.length() != 14) {
			return "长度不为14";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			Long l = 0l;
			Long l2 = System.currentTimeMillis();
			if (sdf2.format(sdf.parse(d)).equals(d)) {
				l = sdf.parse(d).getTime();
				if (Math.abs(l2 - l) > 1000 * 60 * 60) {
					return "与当前时间相隔超过一小时";
				}
				return "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "格式不为yyyyMMddHHmmss";
	}

	/**
	 * 一个List代表一个OrderItem，Map的key代表OrderItem的属性名，value为对应的值
	 * 
	 * @param dataSet
	 * @return
	 */
	public static List<Map<String, String>> unpackSCS0015(PackageDataSet dataSet) {
		String[] arrayCodes401 = { "401.4002", "401.4008", "401.4004", "401.E003", "401.E004", "401.4025", "401.4026", "401.4013", "401.4014",
				"401.4015", "401.4010", "401.E006", "401.4007", "401.4028", "401.4005", "401.E005", "401.4000", "401.4012", "401.4017","401.4060","401.4061"};
		String[] arrayNames401 = { "odrCode401", "beginDate", "custCode", "custType", "custName", "duePaid", "actuallyPaid", "odrStatName",
				"payStatName", "busiStatName", "endDate", "areaName", "accTmnNbr", "tmnRunNbr", "odrType", "orderTypeName", "odrFileStat", "memo","keep","operCode","operDesc"};
		String[] arrayCodes404 = { "404.4002", "404.4049", "404.E049", "404.4051", "404.E051", "404.4052" };
		String[] arrayNames404 = { "odrCode404", "prodCode", "prodName", "busiCode", "busiName", "busiObj" };

		List<Map<String, String>> list401 = getCommonList(dataSet, arrayCodes401, arrayNames401);
		List<Map<String, String>> list404 = getCommonList(dataSet, arrayCodes404, arrayNames404);

		// list401与list404通过odrCode进行关联
		for (int i = 0; i < list401.size(); i++) {
			String odrCode401 = list401.get(i).get("odrCode401");
			for (int j = 0; j < list404.size(); j++) {
				String odrCode404 = list404.get(j).get("odrCode404");
				if (odrCode401.equals(odrCode404)) {
					list401.get(i).put("busiCode", list404.get(j).get("busiCode"));
					list401.get(i).put("busiObj", list404.get(j).get("busiObj"));
					list401.get(i).put("busiName", list404.get(j).get("busiName"));
					break;
				}
			}
		}
		return list401;
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> getCommonList(PackageDataSet dataSet, String[] arrayCodes, String[] arrayNames) {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String code = "";
		String paramID = "";
		String tableID = "";
		String value = "";
		List<String> arrayValues = null;
		boolean flag = false;

		for (int i = 0; i < arrayCodes.length; i++) {
			code = arrayCodes[i];
			paramID = code.split("\\.")[1];
			tableID = code.split("\\.")[0];
			// 键值对获取相关值
			arrayValues = dataSet.getParamByID(paramID, tableID);

			if (!flag) {
				for (int j = 0; j < arrayValues.size(); j++) {
					Map<String, String> map = new HashMap<String, String>();
					list.add(map);
				}
				flag = true;
			}

			if (null != arrayValues && !arrayValues.isEmpty()) {
				for (int j = 0; j < arrayValues.size(); j++) {
					value = arrayValues.get(j);
					list.get(j).put(arrayNames[i], value);
				}
			}
		}

		return list;
	}

	// 比较器
	static class MapCompare implements Comparator<Map<String, String>> {
		public int compare(Map<String, String> o1, Map<String, String> o2) {
			String value1 = o1.get("beginDate");
			String value2 = o2.get("beginDate");
			// 如果 为null，则将其值设为最大,以保证空值是显示在最后一列
			if (isEmpty(value1))
				value1 = "99991230235959";
			if (isEmpty(value2))
				value2 = "99991230235959";
			return value1.compareTo(value2);
		}
	}

}
