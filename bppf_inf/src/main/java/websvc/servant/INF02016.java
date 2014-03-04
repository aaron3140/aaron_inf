package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
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
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.ChannelCode;
import common.utils.DateTool;
import common.utils.MathTool;
import common.utils.PaymentTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf01006Request;
import common.xml.dp.DpInf01006Response;
import common.xml.dp.DpInf02016Request;
import common.xml.dp.DpInf02016Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02016 {

	public static String svcInfName = "INF02016";

	private static final Log logger = LogFactory.getLog(INF02016.class);

	public static String execute(String in0, String in1) {

		DpInf02016Request dpRequest = null;
		DpInf02016Response resp = null;
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

			dpRequest = new DpInf02016Request(in1);

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());// 返回信息头

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"CUSTCODE", dpRequest.getCustCode(), "", "", "S0A");

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
			// boolean flag = false;
			// List privList;
//			TCumInfoDao dao = new TCumInfoDao();
			//			
			// //客户号是否代收付类型
			// if (TCumInfoDao.isDSFCust(custCode)) {
			// String enterpriseCode = dao.getCustCodeFromDSF(tempVal);
			// privList = PayCompetenceManage.payFunc(enterpriseCode,
			// dpRequest.getChannelCode());
			// }else{
			// privList = PayCompetenceManage.payFunc(tempVal,
			// dpRequest.getChannelCode());
			// }
			//			
			// for (int i = 0; i < privList.size(); i++) {
			// Map map = (Map)privList.get(i);
			// String str = map.get("PRIV_URL").toString();
			//
			// if(ChannelCode.AGENT_CHANELCODE.equals(channelCode)){
			// if(PrivConstant.IPOS_ORDERLIST_QUERY.equals(str)||PrivConstant.CLN_ORDERLIST_QUERY.equals(str)){
			// flag = true;
			// break;
			// }
			// }else{
			// if(PrivConstant.WS_ORDERLIST_QUERY.equals(str)){
			// flag = true;
			// break;
			// }
			// }
			// }
			//			
			// if (!flag) {
			// throw new Exception("没有交易查询列表权限");
			// }

			// 判断所选资金管理模式和实际是否相符
			String bankMode = dpRequest.getBankMode();
			TCumInfoDao dao = new TCumInfoDao();
			String bankModeFromDb = dao.getBankMode(dpRequest.getCustCode());
			logger.info("实际资金管理模式为:"+bankModeFromDb + " 接收到的为:"+bankMode);
			if ("BT1002".equals(bankMode)) {// 子母卡
				if (!"BT1013".equals(bankModeFromDb) && !"BT1014".equals(bankModeFromDb)) {
					throw new Exception("请输入正确的资金管理模式");
				} else {
					bankMode = bankModeFromDb;
					dpRequest.setBankMode(bankMode);
				}
			} else if (!bankMode.equals(bankModeFromDb)) {
				throw new Exception("请输入正确的资金管理模式");
			}
			
			String cardAccNbr = PaymentTool.getTissonCardAcct(dpRequest.getCustCode()); //custCode对应的天讯账户号
			
			if(cardAccNbr==null){
				
				throw new INFException(INFErrorDef.BANKACCT_NOT_cardAccNbr,
						INFErrorDef.BANKACCT_NOT_cardAccNbr_DESC);
			}
			
			
			if (isEmpty(dpRequest.getStartNum())
					&& !isEmpty(dpRequest.getEndNum())
					|| !isEmpty(dpRequest.getStartNum())
					&& isEmpty(dpRequest.getEndNum())) {
				throw new Exception("查询起始序号和查询结束序号必须同时填写");
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

			// 调核心

			String totalCount = "0";

			List<Map<String, String>> list = new ArrayList<Map<String, String>>();

			PackageDataSet ds = ebk0002(dpRequest,cardAccNbr);

			responseCode = (String) ds.getParamByID("0001", "000").get(0);

			if (Long.valueOf(responseCode) == 0) {

				responseDesc = (String) ds.getParamByID("0002", "000").get(0);

				totalCount = (String) ds.getParamByID("0019", "001").get(0);

				list = unpackEBK0002(ds);
			}

			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
					dpRequest.getKeep(), dpRequest.getIp(), svcCode,
					responseCode, responseDesc, "S0A");

			resp = new DpInf02016Response();
			
			//如果是母卡将增加返回所有子卡的交易
			List acctCodelist  = null;
			if ("BT1013".equals(bankMode)) {
				TCumAcctDao acctDao = new TCumAcctDao();
				acctCodelist = acctDao.getChildAcctCodeList(dpRequest.getCustCode());
				if(acctCodelist != null  && acctCodelist.size()!=0){
					logger.info("该母卡共有["+acctCodelist.size()+"]个子卡");
					StringBuffer sb = new StringBuffer();
					for (Object object : acctCodelist) {
						sb.append(object).append(",");
					}
					if(sb.length()>1){
						sb.deleteCharAt(sb.length() -1);
					}
					try {
						ds = ebk0002(dpRequest, sb.toString());
						List<Map<String,String>> list2 = unpackEBK0002(ds);
						logger.info("根据母卡查询子卡交易记录: 共有["+list2.size()+"]条");
						list.addAll(list2);
					} catch (Exception e) {
						logger.info("根据母卡查询子卡交易记录时出错");
						logger.info(e.getMessage());
					}
				}
			}
			
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dpRequest.getStartdate(),
					dpRequest.getEnddate(), totalCount, list);
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

	private static PackageDataSet ebk0002(DpInf02016Request dpRequest,String cardAccNbr)
			throws Exception {

		IParamGroup g001 = new ParamGroupImpl("001");
		g001.put("0011", "630");// 630：交易记录 631：操作记录
		if (!isEmpty(dpRequest.getStartNum())
				&& !isEmpty(dpRequest.getEndNum())) {
			g001.put("0012", dpRequest.getStartNum()); // 开始记录
			g001.put("0013", dpRequest.getEndNum()); // 结束记录
		}
		g001.endRow();

		IParamGroup g002 = new ParamGroupImpl("002");
		
		//收支标记
		 if (!isEmpty(dpRequest.getIncome())) {
			 g002.put("0021","6414");
			 g002.put("0022",dpRequest.getIncome());
			 g002.endRow();
		 }

		 //卡户号
		 if (!isEmpty(cardAccNbr)) {
			 g002.put("0021","6001");
			 g002.put("0022",cardAccNbr);
			 g002.endRow();
		 }
		// 账户类型编码
		if (!isEmpty(dpRequest.getAcctType())) {
			g002.put("0021", "6421");
			g002.put("0022", dpRequest.getAcctType());
			g002.endRow();
		}
		// 交易代码
		if (!isEmpty(dpRequest.getTransCode())) {
			g002.put("0021", "6411");
			g002.put("0022", dpRequest.getTransCode());
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
			g002.put("0021", "6311A");
			g002.put("0022", dpRequest.getStartdate());
			g002.endRow();

			g002.put("0021", "6311B");
			g002.put("0022", dpRequest.getEnddate());
			g002.endRow();
		} else if (isEmpty(dpRequest.getStartdate())
				&& isEmpty(dpRequest.getEnddate())) {

		} else {
			throw new Exception("交易起始时间和交易结束时间必须同时填写");
		}
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SCS", "EBK0002", g001, g002);

		return ds;
	}

	/**
	 * 接口EBK0002解包
	 * 
	 * @param dataSet
	 * @return
	 */
	public static List<Map<String, String>> unpackEBK0002(PackageDataSet dataSet) {

		String[] codes630 = { "630.6302", "630.6001", "630.6421", "630.6030",
				"630.6411", "630.6412", "630.6414", "630.6303", "630.6304",
				"630.6306", "630.6309", "630.6310", "630.6311", "630.6312",
				"630.G312", "630.6314", "630.4002", "630.6307" };
		String[] names630 = { "SYSTEMNO", "CARDCODE", "ACCTTYPE", "ACCTCODE",
				"TRANSCODE", "TRANSNAME", "INCOME", "TRANSAMOUNT",
				"TRANSBALANCE", "TRANSSOURCE", "TRANSDATE", "TRANSTIME",
				"TRANSNOTESDATE", "RESULTCODE", "RESULTCODEDEC", "REMARK",
				"ORDERNO", "EXECUTION" };

		List<Map<String, String>> listItem = new ArrayList<Map<String, String>>();

		// 获取630组信息
		List<Map<String, String>> list630 = getCommonList(dataSet, codes630,
				names630);

		// 对集合list根据时间进行排序
		// Collections.sort(listItem,new MapCompare());
		
		if (list630 != null && list630.size() > 0) {
			for (Map<String, String> map : list630) {
				
				if(!"".equals(map.get("TRANSAMOUNT"))){
					
					map.put("TRANSAMOUNT", MathTool.yuanToPoint(map
							.get("TRANSAMOUNT")));
				}
				
			}
		}

		if (list630.size() > 0) {

			listItem.removeAll(list630);
			listItem.addAll(list630);
		}

		return listItem;
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
			String oXml =ExceptionHandler.toXML(new XmlINFException(
					resp, e, respInfo), null);
			
			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		}

	}
}
