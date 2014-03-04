package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
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
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.DateTool;
import common.utils.MathTool;
import common.utils.PrivConstant;
import common.utils.ValueUtil;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf01006Request;
import common.xml.dp.DpInf01006Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 交易查询接口INF_01_006
 */
public class INF_01_006 {

	public static String svcInfName = "01_006";

	private static final Log logger = LogFactory.getLog(INF_01_006.class);

	public static String execute(String in0, String in1) {
		// Long pk = null;
		DpInf01006Request dpRequest = null;
		DpInf01006Response resp = new DpInf01006Response();
		RespInfo respInfo = null; // 返回信息头
		String tmnNum = null;
		String custCode = null;
		String objectCode = null;
		String transCard = null;
		String channelCode = null;
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;

		String responseCode = "";
		String responseDesc = "";

		String tempStr = "";
		String tempVal = "";
		String keep = "";// 获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			respInfo = new RespInfo(in1, "20"); // 返回信息头
			dpRequest = new DpInf01006Request(in1);
			channelCode = dpRequest.getChannelCode();
			tmnNum = dpRequest.getTmnNum();
			custCode = dpRequest.getCustCode();
			objectCode = dpRequest.getObjectCode();
			transCard = dpRequest.getTransCard();
			keep = dpRequest.getKeep();
			ip = dpRequest.getIp();
			String acctype = dpRequest.getAcctType();
			// 账户类型为空默认为企业账户0001
			if (acctype == null || acctype.equals("")) {
				acctype = PrivConstant.ENTER_ACCT_TYPE;
				dpRequest.setAcctType(acctype);
			}
			if (Charset.isEmpty(custCode)) {
				tempStr = "objectCode";
				tempVal = objectCode;
			} else {
				tempStr = "custCode";
				tempVal = custCode;
			}

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum,
					svcCode, "XML", tempStr, tempVal, "transCard", transCard,
					"S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {
				boolean flag = manager.selectTInfOperInLogByKeep(keep);
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
			boolean flag = false;
			List privList;
			TCumInfoDao dao = new TCumInfoDao();

			// 客户号是否代收付类型
			if (TCumInfoDao.isDSFCust(custCode)) {
				String enterpriseCode = dao.getCustCodeFromDSF(tempVal);
				privList = PayCompetenceManage.payFunc(enterpriseCode,
						dpRequest.getChannelCode());
			} else {
				privList = PayCompetenceManage.payFunc(tempVal, dpRequest
						.getChannelCode());
			}

			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
					if (PrivConstant.IPOS_ORDERLIST_QUERY.equals(str)
							|| PrivConstant.CLN_ORDERLIST_QUERY.equals(str)) {
						flag = true;
						break;
					}
				} else {
					if (PrivConstant.WS_ORDERLIST_QUERY.equals(str)) {
						flag = true;
						break;
					}
				}
			}

			if (!flag) {
				throw new Exception("没有交易查询列表权限");
			}

			// 只能够查询同机构号下的交易记录
			if (!isEmpty(dpRequest.getCustCode())) {
				// 取得客户编码的机构号
				String custMerId = dao.getOrgMerIdFromCustCode(dpRequest
						.getCustCode());
				if (!custMerId.equals(dpRequest.getMerId())) {
					throw new Exception("你的客户编码输入有误");
				}
			}
			if (!isEmpty(dpRequest.getStartdate())
					&& !isEmpty(dpRequest.getEnddate())) {
				// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
				String regex = "^\\d{4}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])(([0|1|]\\d)|2[0-3])([0-5]\\d)([0-5]\\d)$";
				if (!(dpRequest.getEnddate().matches(regex) && dpRequest
						.getStartdate().matches(regex))) {
					throw new Exception("输入日期格式不正确");
				}
			} else {
				dpRequest.setStartdate(DateTool.getBerforDate(Calendar.YEAR,
						null));
				dpRequest.setEnddate(DateTool.getStrDate(null, null));
			}

			//组装业务编码
			String actioncode = dpRequest.getActionCode();
			
			if(actioncode.contains("_")){
				
				String [] acs = actioncode.split("_");
				
				if(!"".equals(actioncode)||acs.length>0){
					
					StringBuffer sb = new StringBuffer();
					
					for(int i=0;i<acs.length;i++){
						
						sb.append("'");
						
						sb.append(acs[i]);
						
						sb.append("'");
						
						sb.append(",");
						
					}
					
					actioncode = sb.substring(0, sb.length()-1);
//					actioncode = sb.substring(0, sb.indexOf(","));//多个时,取一个业务编码 (核心支持多业务后再放开)
				}
			}
			//组装产品编码
			String productCode = dpRequest.getProductCode();
			if(productCode.contains("_")){
				String [] acs = productCode.split("_");
				if(!"".equals(productCode)||acs.length>0){
					StringBuffer sb = new StringBuffer();
					for(int i=0;i<acs.length;i++){
						sb.append("'");
						sb.append(acs[i]);
						sb.append("'");
						sb.append(",");
					}
					productCode = sb.substring(0, sb.length()-1);
//					productCode = sb.substring(0, sb.indexOf(","));//多个时,取一个业务编码 (核心支持多业务后再放开)
				}
			}
			
			// 调核心
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();

			PackageDataSet ds = null;
			
			ds = scs0015(dpRequest, actioncode , productCode);

			responseCode = (String) ds.getParamByID("0001", "000").get(0);
			
			responseDesc = (String) ds.getParamByID("0002", "000").get(0);

			if (Long.valueOf(responseCode) == 0) {

				list = unpackSCS0014(ds);

				if (list != null && list.size() > 0) {
					for (Map<String, String> map : list) {
						map.put("ORDERSEQ", ValueUtil.s(map.get("tmnRunNbr")));
						map.put("TRANSSEQ", ValueUtil.s(map.get("odrCode401")));
						map.put("CUSTCODE", ValueUtil.s(map.get("custCode")));
						map.put("CUSTNAME", ValueUtil.s(map.get("custName")));
						map.put("ORDERTYPECODE", ValueUtil
								.s(map.get("odrType")));
						map.put("ORDERTYPENAME", ValueUtil.s(map
								.get("orderTypeName")));
						map.put("AREANAME", ValueUtil.s(map.get("areaName")));
						map.put("TMNNUM", ValueUtil.s(map.get("accTmnNbr")));
						map.put("ORDERTIME", ValueUtil.s(map.get("beginDate")));
						map.put("MEMO", ValueUtil.s(map.get("memo")));
						map.put("ORDERSTAT", ValueUtil
								.s(map.get("odrStatName")));
						map.put("ORDERAMOUNT", MathTool.yuanToPoint(map
								.get("duePaid")));
						map.put("PAYSTAT", ValueUtil.s(map.get("payStatName")));
						map.put("BUSICODE", ValueUtil.s(map.get("busiCode")));
						map.put("BUSINAME", ValueUtil.s(map.get("busiName")));
						map.put("BUSIOBJ", ValueUtil.s(map.get("busiObj")));
						map.put("PRODCODE", ValueUtil.s(map.get("prodCode")));
						map.put("KEEP", ValueUtil.s(map.get("keep")));
					}
				}else{
					responseCode =INFErrorDef.NO_TRANSACTION_RECORD;
					responseDesc = INFErrorDef.NO_TRANSACTION_RECORD_DESC;
				}
			}

			String count = null;
			if (!isEmpty(dpRequest.getCountTotal())
					&& "1".equals(dpRequest.getCountTotal())) {

				count = totalCount(dpRequest, actioncode,productCode);
			}
			
			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep,
					ip, svcCode, responseCode, responseDesc, "S0A");

			resp = new DpInf01006Response();
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dpRequest.getStartdate(),
					dpRequest.getEnddate(), count, list);
		} catch (XmlINFException spe) {
			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, spe.getMessage(),
						"S0A");
			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), id);
		}
	}

	private static PackageDataSet scs0015(DpInf01006Request dpRequest,
			String actionCode,String productCode) throws Exception {

		String includeson = dpRequest.getIncludesoncard();
		if(includeson==null||includeson.equals("")){
			includeson = "0";
		}
		IParamGroup g001 = new ParamGroupImpl("001");
		if (!isEmpty(dpRequest.getStartNum())
				&& !isEmpty(dpRequest.getEndNum())) {
			g001.put("0012", dpRequest.getStartNum()); // 开始记录
			g001.put("0013", dpRequest.getEndNum()); // 结束记录
			g001.endRow();
		} else if (isEmpty(dpRequest.getStartNum())
				&& isEmpty(dpRequest.getEndNum())) {
			g001.endRow();
		} else {
			throw new Exception("查询起始序号和查询结束序号必须同时填写");
		}

		IParamGroup g002 = new ParamGroupImpl("002");
		// 交易卡号
		if (!isEmpty(dpRequest.getTransCard())) {
			g002.put("0021", "4101");
			g002.put("0022", dpRequest.getTransCard());
			g002.endRow();
		}
		// 外部订单号
		if (!isEmpty(dpRequest.getOrderSeq())) {
			g002.put("0021", "4028");
			g002.put("0022", dpRequest.getOrderSeq());
			g002.endRow();
		}
		if(includeson.equals("0")){
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
		}
		// 支付状态
		if (!isEmpty(dpRequest.getOrderstat())) {
			String orderstat = dpRequest.getOrderstat();
			if (orderstat == null) {
				throw new Exception("找不到相对应的订单状态");
			}
			g002.put("0021", "4013");
			g002.put("0022", orderstat);
			g002.endRow();
		}
		// 交易时间
		if (!isEmpty(dpRequest.getStartdate())
				&& !isEmpty(dpRequest.getEnddate())) {
			long long1 = Long.parseLong(dpRequest.getStartdate());
			long long2 = Long.parseLong(dpRequest.getEnddate());
			if (long1 > long2) {
				throw new Exception("交易起始时间不能大于交易结束时间");
			}
			g002.put("0021", "4008A");
			g002.put("0022", dpRequest.getStartdate());
			g002.endRow();

			g002.put("0021", "4008B");
			g002.put("0022", dpRequest.getEnddate());
			g002.endRow();
		} else if (isEmpty(dpRequest.getStartdate())
				&& isEmpty(dpRequest.getEnddate())) {

		} else {
			throw new Exception("交易起始时间和交易结束时间必须同时填写");
		}

		// 交易类型
		if (!isEmpty(actionCode)) {
			g002.put("0021", "4051");
			g002.put("0022", actionCode);
			g002.endRow();
		}
		// 所属区域
		if (!isEmpty(dpRequest.getAreacode())) {
			g002.put("0021", "4006");
			g002.put("0022", dpRequest.getAreacode());
			g002.endRow();
		}

		// 业务对象
		if (!isEmpty(dpRequest.getBusObject())) {
			g002.put("0021", "4052");
			g002.put("0022", dpRequest.getBusObject());
			g002.endRow();
		}

		// 产品编码
		if (!isEmpty(productCode)) {
			g002.put("0021", "4049");
			g002.put("0022", productCode);
			g002.endRow();
		}

			if(includeson.equals("1")){
				TCumInfoDao dao =  new TCumInfoDao();
				String prtnid = dao.getPrtnId(dpRequest.getCustCode());
				//组查询子卡请求
				g002.put("0021", "E006");
				g002.put("0022", "Y");
				g002.endRow();
				
				g002.put("0021", "2011");
				g002.put("0022", prtnid);
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
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SCS", "SCS0015", g001, g002);

		return ds;
	}

	private static String totalCount(DpInf01006Request dpRequest,
			String actionCode,String productCode) throws Exception {

		IParamGroup g002 = new ParamGroupImpl("002");
		// 交易卡号
		if (!isEmpty(dpRequest.getTransCard())) {
			g002.put("0021", "4101");
			g002.put("0022", dpRequest.getTransCard());
			g002.endRow();
		}
		// 外部订单号
		if (!isEmpty(dpRequest.getOrderSeq())) {
			g002.put("0021", "4028");
			g002.put("0022", dpRequest.getOrderSeq());
			g002.endRow();
		}
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

		// 支付状态
		if (!isEmpty(dpRequest.getOrderstat())) {
			String orderstat = dpRequest.getOrderstat();
			if (orderstat == null) {
				throw new Exception("找不到相对应的订单状态");
			}
			g002.put("0021", "4013");
			g002.put("0022", orderstat);
			g002.endRow();
		}
		// 交易时间
		if (!isEmpty(dpRequest.getStartdate())
				&& !isEmpty(dpRequest.getEnddate())) {
			long long1 = Long.parseLong(dpRequest.getStartdate());
			long long2 = Long.parseLong(dpRequest.getEnddate());
			if (long1 > long2) {
				throw new Exception("交易起始时间不能大于交易结束时间");
			}
			g002.put("0021", "4008A");
			g002.put("0022", dpRequest.getStartdate());
			g002.endRow();

			g002.put("0021", "4008B");
			g002.put("0022", dpRequest.getEnddate());
			g002.endRow();
		} else if (isEmpty(dpRequest.getStartdate())
				&& isEmpty(dpRequest.getEnddate())) {

		} else {
			throw new Exception("交易起始时间和交易结束时间必须同时填写");
		}

		// 交易类型
		if (!isEmpty(actionCode)) {
			g002.put("0021", "4051");
			g002.put("0022", actionCode);
			g002.endRow();
		}
		// 所属区域
		if (!isEmpty(dpRequest.getAreacode())) {
			g002.put("0021", "4006");
			g002.put("0022", dpRequest.getAreacode());
			g002.endRow();
		}

		// 业务对象
		if (!isEmpty(dpRequest.getBusObject())) {
			g002.put("0021", "4052");
			g002.put("0022", dpRequest.getBusObject());
			g002.endRow();
		}
		
		// 产品编码
		if (!isEmpty(productCode)) {
			g002.put("0021", "4049");
			g002.put("0022", productCode);
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
		
		String returnTotalnums = "0";

		IServiceCall caller1 = new ServiceCallImpl();
		PackageDataSet ds = caller1.call("SCS", "SCS0016", g002);

		String responseCode = (String) ds.getParamByID("0001", "000").get(0);

		if (Long.valueOf(responseCode) == 0) {

			List<Map<String, String>> list2 = unpackSCS0016(ds);

			if (list2 != null && list2.size() > 0) {
				returnTotalnums = ValueUtil.s(list2.get(0).get("countTotal"));
			} else {
				returnTotalnums = ValueUtil.s("0");
			}
		} else {

			returnTotalnums = ValueUtil.s("0");
		}

		return returnTotalnums;
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

	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> unpackSCS0014(PackageDataSet dataSet) {
		String[] arrayCodes401 = { "401.4002", "401.4008", "401.4004",
				"401.E003", "401.E004", "401.4025", "401.4026", "401.4013",
				"401.4014", "401.4015", "401.4010", "401.E006", "401.4007",
				"401.4028", "401.4005", "401.E005", "401.4000", "401.4012", "401.4017"};
		String[] arrayNames401 = { "odrCode401", "beginDate", "custCode",
				"custType", "custName", "duePaid", "actuallyPaid",
				"odrStatName", "payStatName", "busiStatName", "endDate",
				"areaName", "accTmnNbr", "tmnRunNbr", "odrType",
				"orderTypeName", "odrFileStat", "memo" ,"keep"};
		String[] arrayCodes404 = { "404.4002", "404.4049", "404.E049",
				"404.4051", "404.E051", "404.4052" };
		String[] arrayNames404 = { "odrCode404", "prodCode", "prodName",
				"busiCode", "busiName", "busiObj" };

		List<Map<String, String>> list401 = getCommonList(dataSet,
				arrayCodes401, arrayNames401);
		List<Map<String, String>> list404 = getCommonList(dataSet,
				arrayCodes404, arrayNames404);

		// list401与list404通过odrCode进行关联
		for (int i = 0; i < list401.size(); i++) {
			String odrCode401 = list401.get(i).get("odrCode401");
			for (int j = 0; j < list404.size(); j++) {
				String odrCode404 = list404.get(j).get("odrCode404");
				if (odrCode401.equals(odrCode404)) {
					// System.out.println("busiName==="+list404.get(j).get("busiName")+"===odrCode404====="+odrCode404+"====odrCode401===="+odrCode401);
					list401.get(i).put("busiCode",
							list404.get(j).get("busiCode"));
					list401.get(i)
							.put("busiObj", list404.get(j).get("busiObj"));
					list401.get(i).put("busiName",
							list404.get(j).get("busiName"));
					list401.get(i).put("prodCode",
							list404.get(j).get("prodCode"));
					break;
				}
			}
		}
		return list401;
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> getCommonList(
			PackageDataSet dataSet, String[] arrayCodes, String[] arrayNames) {

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

	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> unpackSCS0016(PackageDataSet dataSet) {
		String[] arrayCodes001 = { "001.0019" };
		String[] arrayNames001 = { "countTotal" };

		List<Map<String, String>> list001 = getCommonList(dataSet,
				arrayCodes001, arrayNames001);

		return list001;
	}

	public static void main(String args[]) {
		System.out.println(validDate("20120515100000"));
	}

	public static String executeForMD5(String in0, String in1) {
		DpInf01006Response resp = new DpInf01006Response();
		RespInfo respInfo = null; // 返回信息头
		String md5Key = null;
//		String tempVal = "";

		try {
			respInfo = new RespInfo(in1, "10");
			DpInf01006Request dpRequest = new DpInf01006Request(in1);
//			String custCode = dpRequest.getCustCode();
//			String objectCode = dpRequest.getObjectCode();
//			if (Charset.isEmpty(custCode)) {
//				tempVal = objectCode;
//			} else {
//				tempVal = custCode;
//			}
			// 客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				 md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(),
							tokenValidTime);
				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);
				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());
			}

			String oldXml = execute(in0, in1);

			return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);
		} catch (Exception e) {
			String oldXml= ExceptionHandler.toXML(new XmlINFException(
					resp, e, respInfo), null);
			
			return ResponseVerifyAdder.pkgForMD5(oldXml, null);
		}

	}
}
