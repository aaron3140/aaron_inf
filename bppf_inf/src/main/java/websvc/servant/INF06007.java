package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

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
import common.utils.MathTool;
import common.utils.PasswordUtil;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf06007Request;
import common.xml.dp.DpInf06007Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 邱亚建<br>
 *         时间：2013-8-27 下午05:22:50<br>
 *         类描述：卡户管理接口
 */
public class INF06007 {

	public static String svcInfName = "INF06007";

	private static final Logger LOG = Logger.getLogger(INF06007.class);

	public static String executeForMD5(String in0, String in1) {

		DpInf06007Response resp = new DpInf06007Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf06007Request dpRequest = new DpInf06007Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

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

	public static String execute(String in0, String in1) {

		DpInf06007Request dpRequest = null;
		DpInf06007Response resp = null;
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

			dpRequest = new DpInf06007Request(in1);

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());// 返回信息头

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(), dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML", "CUSTCODE", dpRequest.getCustCode(), "",
					"", "S0A");

			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {
				boolean flag = manager.selectTInfOperInLogByKeep(dpRequest.getKeep());
				// 判断流水号是否可用
				if (flag) {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE, INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			// 校验客户编码和用户名是否匹配
			TCumInfoDao cumInfoDao = new TCumInfoDao();
			String custCodeByStaff = cumInfoDao.getCustCodeByStaff(dpRequest.getStaffCode());
			if (!StringUtils.equals(custCodeByStaff, dpRequest.getCustCode())) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST, INFErrorDef.CUSTCODE_NOT_MATCH_STAFF_DESC);
			}

			// 校验操作密码(支付密码)
			PackageDataSet ds = PasswordUtil.callCUM1003(dpRequest, dpRequest.getStaffCode(), dpRequest.getPayPassword(), "0001");// 0001：支付密码
			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}else{
				String num = ds.getByID("6047", "601");
				throw new INFException(INFErrorDef.PAY_PWD_FAULT,INFErrorDef.PAY_PWD_FAULT_DESC+num+"次");
			}

			TCumAcctDao dao = new TCumAcctDao();
			boolean flag = dao.isChildAndParentCard(dpRequest.getChildCustCode(), dpRequest.getCustCode());
			if (!flag) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.ERROR_CHILD_CARD);
			}

			String acctCode = dao.getAcctCode(dpRequest.getChildCustCode(), "ACCT002");// 卡户号

			ds = callEBK0001(acctCode);
			resultCode = (String) ds.getParamByID("0001", "000").get(0);
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}

			ArrayList typeList = ds.getParamByID("6421", "604");
			ArrayList acctCodeList = ds.getParamByID("6030", "604");
			String iposAcctCode = "";// 账户号
			if (typeList != null && typeList.size() != 0) {
				for (int i = 0; i < typeList.size(); i++) {
					String type = (String) typeList.get(i);
					if ("0007".equals(type)) {
						iposAcctCode = (String) acctCodeList.get(i);
						break;
					}
				}
			}

			if ("100".equals(dpRequest.getOperType())) {
//				ds = callCUM0005(dpRequest);
				boolean f = false;
				try {
					f = Integer.valueOf(dpRequest.getDayLimit())%100!=0;
				} catch (Exception e) {
//					e.printStackTrace();
					throw new INFException(INFErrorDef. NO_POWER_OF_100, INFErrorDef.NO_POWER_OF_100_DESC);
				}
				if(f){
					throw new INFException(INFErrorDef. NO_POWER_OF_100, INFErrorDef.NO_POWER_OF_100_DESC);
				}
				 ds = callEBK1005(dpRequest, iposAcctCode, acctCode);
			} else {
				// ds = callEBK1005(dpRequest, iposAcctCode, acctCode);
				ds = callCUM1006(dpRequest);
			}
			resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}

			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), dpRequest.getKeep(), dpRequest.getIp(), svcCode, responseCode, responseDesc, "S0A");

			String dayLimit = "";// 当操作类型为100时必填
			if ("100".equals(dpRequest.getOperType())) {
				dayLimit = dpRequest.getDayLimit();
			}
			resp = new DpInf06007Response();

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, dpRequest.getOrderNo(), dayLimit,
					dpRequest.getRemark1(), dpRequest.getRemark2());
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
		} finally {
		}
	}

	/**
	 * 设置日交易限额
	 * 
	 * @param acctCode
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callCUM0005(DpInf06007Request dpRequest) throws Exception {
		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2002", dpRequest.getChildCustCode()); // 客户编码
		g200.endRow();

		IParamGroup g204 = new ParamGroupImpl("204");
		g204.put("2031", "T08"); // 日消费阀值
		g204.put("2044", "0006"); // 定制产品编码
		g204.put("2055", "06010005"); // 定制业务编码
		g204.put("2033", dpRequest.getChildCustCode()); // 定制对象标识
		g204.put("2034", "TH002"); // 阀值类型编码,TH001: 电子钱包余额；TH002: 支付金额；
		g204.put("2035", MathTool.pointToYuan(dpRequest.getDayLimit())); // 提醒阀值
		g204.put("2036", "+"); // 运算符
		g204.put("2037", "PR005"); // 周期类型编码,PR005: 每日；
		g204.put("2016", "MSG"); // 联系对象类型
		g204.put("2018", dpRequest.getCustCode()); // 联系对象
		g204.put("2040", DateFormatUtils.format(new Date(), "yyyyMMddhhmmss")); // 定制生效日期
		g204.put("2040", ""); // 定制失效日期
		g204.endRow();

		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", "80"); // 渠道类型编码
		g211.put("2077", dpRequest.getTmnNum()); // 操作来源（终端号）
		g211.put("2078", dpRequest.getChildCustCode()); // 操作执行者 客户网站注册时，填为客户编码；后台为商户注册时，填为操作员工工号。
		g211.endRow();

		IServiceCall serCall = new ServiceCallImpl();
		PackageDataSet dataSet = serCall.call("BIS", "CUM0005", g200, g204, g211);
		return dataSet;
	}

	/**
	 * 新冻结结冻
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callCUM1006(DpInf06007Request dpRequest) throws Exception {
		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2002");
//		String staffCode = TCumInfoDao.getDefaultStaffCodeByCustCode(dpRequest.getChildCustCode());
		g200.put("2902", dpRequest.getChildCustCode());
		String operType = dpRequest.getOperType();
		if ("200".equals(operType)) {
			g200.put("2903", "0001"); // 冻结
		} else if ("201".equals(operType)) {
			g200.put("2903", "0002");// 解冻
		}
		g200.endRow();

		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", dpRequest.getChannelCode());
		g211.put("2077", dpRequest.getTmnNum());
		g211.put("2078", dpRequest.getCustCode());
		g211.put("2085", dpRequest.getIp());
		g211.endRow();

		IServiceCall serCall = new ServiceCallImpl();
		PackageDataSet dataSet = serCall.call("BIS", "CUM1006", g200, g211);
		return dataSet;
	}

	/**
	 * 根据卡户号查询账户号
	 * 
	 * @param acctCode
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callEBK0001(String acctCode) throws Exception {
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "6001"); // 6001 按企业账户条件查询
		g002.put("0022", acctCode); // 0022 查询条件值 资金账户号
		g002.endRow();

		IServiceCall serCall = new ServiceCallImpl();
		PackageDataSet dataSet = serCall.call("EAS", "EBK0001", g002);
		return dataSet;
	}

	/**
	 * 调用EBK1005
	 * 
	 * @param dpRequest
	 * @param iposAcctCode
	 *            //账户号
	 * @param acctCode
	 *            //开户号
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callEBK1005(DpInf06007Request dpRequest, String iposAcctCode, String acctCode) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		Date tradeTime = sdf.parse(dpRequest.getTradeTime());
		sdf = new SimpleDateFormat("MMdd");
		String date = sdf.format(tradeTime);
		sdf = new SimpleDateFormat("hhmmss");
		String time = sdf.format(tradeTime);
		String areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());

		// 包头控制信息
		IParamGroup g600 = new ParamGroupImpl("600");
		String operCode = convertOperCode(dpRequest.getOperType());
		g600.put("6316", operCode);// 操作类型编码
		g600.put("6001", acctCode);// 卡户号 T_EBK_CARDACCT CAAC_CODE除开户外其他不须传
		g600.put("6030", iposAcctCode);// 账户号 除开户外其他必须传
		// 6032 账户消费次限额 除开户和限额设定外其他不须传
		// 6033 账户消费日限额 除开户和限额设定外其他不须传
		// 6034 账户消费总限额 除开户和限额设定外其他不须传
		if ("0404".equals(operCode)) {
			// g600.put("6032", "5");// 账户消费日限额
			g600.put("6033", MathTool.pointToYuan(dpRequest.getDayLimit()));// 账户消费日限额
			// g600.put("6034", "1000");// 账户消费日限额
		}
		g600.put("6306", dpRequest.getTmnNum());// 操作来源
		g600.put("6307", dpRequest.getCustCode());// 操作执行者
		// g600.put("6308", dpRequest.getMerId());// 执行机构标识
		g600.put("6308", "1234");// 执行机构标识
		g600.put("6309", date);// 操作日期MMDD
		g600.put("6310", time);// 操作时间HH24MISS
		g600.put("2008", areaCode);// 客户所属区域
		g600.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("EAS", "EBK1005", g600);

		return ds;
	}

	/**
	 * 将前置的操作编码转为核心对应的操作编码
	 * 
	 * @param infOperCode
	 */
	private static String convertOperCode(String infOperCode) {
		// 前置：
		// 100:设置日交易限额
		// 200：冻结卡户
		// 201：解冻卡户
		// 核心：
		// 0400：开户
		// 0401：销户
		// 0403：密码重置
		// 0404：消费限额设定
		// 0410：控制
		// 0411：解控
		// 0420：冻结
		// 0421：解冻
		// 0412：停用
		// 0413：启用
		if ("100".equals(infOperCode)) {
			return "0404";
		}
		if ("200".equals(infOperCode)) {
			return "0420";
		}
		if ("201".equals(infOperCode)) {
			return "0421";
		}
		return "";

	}
}
