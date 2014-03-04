package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.springframework.util.LinkedCaseInsensitiveMap;

import common.dao.BaseDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymSysParamDao;
import common.entity.Account;
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
import common.utils.MathTool;
import common.utils.SpringContextHelper;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpCardAccountInfoRequest;
import common.xml.dp.DpCardAccountInfoResponset;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 账户信息查询接口
 * 
 * 
 */
public class INF_01_001 {
	public static String svcInfName = "01_001";
	public static String[] keys = { "ORGCODE", "BUSSINESSLIC", "LEGALREPRESENTATIVE", "CONTACTER", "CONTACTPHONE", "EMAIL", "REGCERT", "FINANCIALCONTACTER", "FINANCIALPHONE",
			"FINANCIALEMAIL", "ADDR" };
	public static String[] attrIds = { "2577", "2582", "2579", "2589", "2590", "2591", "2592", "2580", "2585", "2586", "2581" };
	public static Map<String, String> dict = new HashMap<String, String>();

	static {
		for (int i = 0; i < attrIds.length; i++) {
			dict.put(attrIds[i], keys[i]);
		}
	}

	public static String execute(String in0, String in1) {
		// Long pk = null;
		DpCardAccountInfoRequest cardAccountInfoRequest = null;
		RespInfo respInfo = null; // 返回信息头

		String agentCode = null; // 客户编码
		String tmnNum = null; // 受理终端号
		String channelCode = "";
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode1 = "";
		String keep = "";// 获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);

		DpCardAccountInfoResponset resp = null;
		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			respInfo = new RespInfo(in1, "20"); // 返回信息头
			cardAccountInfoRequest = new DpCardAccountInfoRequest(in1);
			channelCode = cardAccountInfoRequest.getChannelCode();
			agentCode = cardAccountInfoRequest.getAgentCode();
			tmnNum = cardAccountInfoRequest.getTmnNum();
			keep = cardAccountInfoRequest.getKeep();
			ip = cardAccountInfoRequest.getIp();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "agentCode", agentCode, "", "", "S0A");
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
			List privList = PayCompetenceManage.payFunc(cardAccountInfoRequest.getAgentCode(), channelCode);
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if (("cln_AcctQuery".equals(str) || "ipos_AcctQuery".equals(str)) && ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
					flag = true;
				} else if ("ws_AcctQuery".equals(str)) {
					flag = true;
				}
			}
			if (!flag) {
				throw new Exception("没有账户查询权限");
			}

			// 写日志
			// pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
			// svcInfName,"INF_01_001", SocketConfig.getSockIp(),
			// "AGENTCODE", agentCode);
			// id.setPk(pk);

			// 根据客户编码，调用CUM0003查询联系信息
			IParamGroup g0003_200 = new ParamGroupImpl("200"); // 包头
			g0003_200.put("2002", agentCode);
			g0003_200.endRow();

			IParamGroup g0003_002 = new ParamGroupImpl("002"); // 包头
			g0003_002.put("0011", "207");
			g0003_002.endRow();

			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet DataSet = caller.call("BIS", "CUM0003", g0003_200, g0003_002);// 组成交易数据包,调用CUM0003接口

			// 获取207组卡户号
			int count = DataSet.getParamSetNum("207");
			String payeeCardAcctNbr = null;
			for (int i = 0; i < count; i++) {
				// 获取卡户类型
				String cardAcctType = (String) DataSet.getParamByID("2048", "207").get(i);
				// 获取天讯卡户号
				if ("ACCT002".equals(cardAcctType)) {
					payeeCardAcctNbr = (String) DataSet.getParamByID("2049", "207").get(i);
				}
			}

			// 调用EBK0001
			IParamGroup e002 = new ParamGroupImpl("002"); // 包头
			e002.put("0021", "6001");
			e002.put("0022", payeeCardAcctNbr); // "110650101000001046"

			IServiceCall callerE = new ServiceCallImpl();
			DataSet = callerE.call("EAS", "EBK0001", e002); // 组成数据包,调用EBK0001接口

			String resultCode = (String) DataSet.getParamByID("0001", "000").get(0);
			responseCode1 = resultCode;
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) DataSet.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}

			responseCode1 = DataSet.getByID("0001", "000"); // 获取接口的000组的0001参数

			// 返回响应码
			String responseContent = DataSet.getByID("0002", "000"); // 获取接口的000组的0002参数
			count = DataSet.getParamSetNum("604");
			if (count < 1) {
				throw new Exception("无账户信息");
			}
			List<Account> accountItems = new ArrayList<Account>();
			String accttype = "";
			for (int i = 0; i < count; i++) {
				accttype = (String) DataSet.getParamByID("6421", "604").get(i);
				if (accttype == null || accttype.length() < 1 || (!accttype.equals("0001"))) {
					continue;
				}
				Account account = new Account();
				account.setAcctType(accttype); // 账户类型
				String acctStat = (String) DataSet.getParamByID("6038", "604").get(i);// 账户状态
				if ("S0A".equals(acctStat)) {
					acctStat = "2"; // 2
				} else if ("S0N".equals(acctStat)) {
					acctStat = "1"; // 1
				} else if ("S0X".equals(acctStat)) {
					acctStat = "3"; // 3
				}
				account.setAcctStat(acctStat); // 账户状态
				String Monetaryunit = (String) DataSet.getParamByID("6404", "604").get(i); // 货币单位
				String balance = (String) DataSet.getParamByID("6035", "604").get(i); // 账户总余额
				String activeBalance = (String) DataSet.getParamByID("6036", "604").get(i); // 账户可用余额
				String frozenBalance = (String) DataSet.getParamByID("6037", "604").get(i); // 账户冻结余额
				if ("元".equals(Monetaryunit)) {
					balance = MathTool.yuanToPoint(balance);
					activeBalance = MathTool.yuanToPoint(activeBalance);
					frozenBalance = MathTool.yuanToPoint(frozenBalance);
				}
				account.setBalance(balance); // 账户总余额
				account.setActiveBalance(activeBalance);// 账户可用余额
				account.setFrozenBalance(frozenBalance);// 账户冻结余额
				accountItems.add(account);
			}
			if (accountItems.size() < 1) {
				throw new Exception("无账户信息");
			}

			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode1, responseContent, "S0A");

			// 更新日志
			// TInfDcoperlogDao.update(pk, responseCode, responseContent);

			String responTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());// 查询时间

			Hashtable<String, String> dataMap = new Hashtable<String, String>();
			dataMap.put("AGENTCODE", agentCode);
			dataMap.put("TIME", responTime);

//			if (!setCumInfo(dataMap, agentCode)) {
//				throw new Exception("获取账户信息出错");
//			}
			
			try {
				setCumInfo(dataMap, agentCode);
			} catch (Exception e) {
				throw new Exception("获取账户信息出错");
			}
			
			// 返回结果
			resp = new DpCardAccountInfoResponset();
			String xmlStr = resp
					.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode1, responseContent, dataMap, accountItems);
			if (xmlStr == null || xmlStr.length() < 1) {
				throw new Exception("获取账户信息出错");
			}
			return xmlStr;
		} catch (XmlINFException spe) {
			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode1, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode1, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), id);
		}
	}

	private static boolean setCumInfo(Hashtable<String, String> ht, String agentCode) throws Exception {

		BaseDao DAO = SpringContextHelper.getBaseDaoBean();
		String sql = "";

		sql = "select cust_id from t_cum_info a where a.cust_code= ? ";
		String custId = (String) DAO.queryForObject(sql, new Object[] { agentCode }, String.class);
		if (custId == null || custId.length() < 1) {
			return false;
		}

		// 组织机构代码,业执照注册号,企业法人,业务联系人,联系手机,电子邮箱,税务登记证,财务联系人,财务联系手机,财务电子邮箱
		sql = "select value1, attr_id from T_CUM_ATTR a where a.cust_id=" + custId + " and a.attr_id in ('2577','2582','2579',"
				+ "'2589','2590','2591','2592','2580','2585','2586','2581')";
		List list = DAO.queryForList(sql);
		for (int j = 0, k = list.size(); j < k; j++) {
			Map map = (Map) list.get(j);
			String attrid = String.valueOf(map.get("attr_id"));
			String attrval = String.valueOf(map.get("value1"));
			String dictName = dict.get(attrid);
			ht.put(dictName, attrval);
		}

		// 所属行业
		sql = "select * from (select b.dict_name from t_cum_attr a,t_sym_dict b" + " where a.value1=b.dict_id and b.dict_typeid='CUM_INDUSTRY'" + " and a.cust_id=" + custId
				+ " and a.attr_id=2588 and a.value1<>'12'" + " union" + " select a.value1 from t_cum_attr a where a.cust_id=" + custId + " and a.attr_id=2604"
				+ " and exists (select 1 from t_cum_attr where cust_id=" + custId + " and attr_id=2588 and value1='12')) a where rownum<=1";
		String range = (String) DAO.queryForObject(sql, String.class);
		if (range == null) {
			range = "";
		}
		ht.put("RANGE", range);

		ht.put("CONTACTADDR", getAllAreaName(agentCode));

		// 企业名称
		// sql="select max(b.prtn_name) prtn_name from t_cum_info a,t_pnm_partner b where a.prtn_id=b.prtn_id and a.cust_id="+custId;
		// String agentname=(String)DAO.queryForObject(sql,String.class);
		// if (agentname==null) {
		// agentname="";
		// }
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "2002");
		g002.put("0022", agentCode);
		g002.endRow();
		IServiceCall callerE = new ServiceCallImpl();

		PackageDataSet dataSet = callerE.call("BIS", "CUM0002", g002);
		String agentname = dataSet.getByID("2003", "201");
		ht.put("AGENTNAME", agentname);
		String regDate = dataSet.getByID("2012", "201");
		ht.put("REGDATE", regDate);

		return true;
	}

	private static String getAllAreaName(String custCode) {
		String sql = "select area_name from t_sym_area where stat = 'S0A'"
				+ " connect by area_code = prior parent_area start with area_code = (select max(area_code) from t_cum_info where cust_code='" + custCode + "')";
		//
		BaseDao DAO = SpringContextHelper.getBaseDaoBean();
		List<LinkedCaseInsensitiveMap> list = DAO.queryForList(sql);
		String result = "";
		int size;
		if (list != null && (size = list.size()) > 0) {
			for (int i = size - 1; i >= 0;) {
				result += list.get(i).get("AREA_NAME");
				if (i > 0) {
					result += "-";
				}
				i--;
			}
		}
		return result;
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

	public static String executeForMD5(String in0, String in1) {
		DpCardAccountInfoResponset resp = new DpCardAccountInfoResponset();
		RespInfo respInfo = null; // 返回信息头
		String md5Key = null;
		try {
			respInfo = new RespInfo(in1, "10");
			DpCardAccountInfoRequest dpRequest = new DpCardAccountInfoRequest(in1);
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
}
