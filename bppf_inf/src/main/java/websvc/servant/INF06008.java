package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.mortbay.log.Log;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymCustomDao;
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
import common.utils.Charset;
import common.utils.MathTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf06008Request;
import common.xml.dp.DpInf06008Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 邱亚建<br>
 *         时间：2013-8-27 下午05:23:00<br>
 *         类描述：子卡列表查询接口
 */
public class INF06008 {

	public static String svcInfName = "INF06008";

	private static final Logger log = Logger.getLogger(INF06008.class);

	public static String executeForMD5(String in0, String in1) {

		DpInf06008Response resp = new DpInf06008Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf06008Request dpRequest = new DpInf06008Request(in1);

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

		DpInf06008Request dpRequest = null;
		DpInf06008Response resp = null;
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

			dpRequest = new DpInf06008Request(in1);

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

			String bankMode = cumInfoDao.getBankMode(dpRequest.getCustCode());
			if (!"BT1013".equals(bankMode)) {
				throw new Exception("该客户编码对应的卡不是母卡");
			}

			TCumAcctDao acctDao = new TCumAcctDao();
			String itemNum = acctDao.getChildInfoListCount(dpRequest.getCustCode());
			List<Map<String, String>> cardList = null;
			if (Integer.valueOf(itemNum) == 0) {
				cardList = Collections.EMPTY_LIST;
			} else {
				cardList = getChildCardList(dpRequest);// 子卡的客户编码和对应的卡号
				TSymCustomDao dao = new TSymCustomDao();
				IServiceCall caller = new ServiceCallImpl();
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddhhmmss");
				for (Map<String, String> map : cardList) {
					String custId = String.valueOf(map.get("CUST_ID"));
					// String dateLimit = dao.getTh(String.valueOf(custId), "T08", "TH002");// 查询子卡日交易限额
					String custCode = map.get("CUST_CODE");
					String acctCode = acctDao.getAcctCode(custCode, "ACCT002");// 卡户
					String DAYLIMIT = "0";
					String DAYTOTAL = "0";
					String regDate = cumInfoDao.getRegDateByCustId(custId);
					Date date = sdf1.parse(regDate);
					regDate = sdf2.format(date);
					try {
						// IParamGroup c200 = new ParamGroupImpl("220"); // 包头
						// c200.put("2002", custCode);
						// c200.put("2804", "T08");
						// PackageDataSet ds = caller.call("BIS", "CUM0018", c200);
						IParamGroup g002 = new ParamGroupImpl("002"); // 包头
						g002.put("0021", "6001");
						g002.put("0022", acctCode);// 卡户
						PackageDataSet ds = caller.call("EAS", "EBK1008", g002);
						String resultCode = (String) ds.getParamByID("0001", "000").get(0);

						// 返回结果为失败时，抛出异常
						if (Long.valueOf(resultCode) == 0) {
							ArrayList list6421 = ds.getParamByID("6421", "604");// 账户类型
							ArrayList list6033 = ds.getParamByID("6033", "604");// 日交易限额
							ArrayList listC033 = ds.getParamByID("C033", "604");//日累积金额
							if (list6421 != null && list6421.size() != 0) {

								for (int i = 0; i < list6421.size(); i++) {
									String acctType = (String) list6421.get(i);
									if ("0007".equals(acctType)) {
										DAYLIMIT = MathTool.yuanToPoint((String) list6033.get(i));
										DAYTOTAL = MathTool.yuanToPoint((String) listC033.get(i));
										break;
									}
								}
							}
						}
					} catch (Exception e) {
						log.error(e.getMessage() + "--" + custCode + " 日交易金额，日累积额默认为0");
					}
					map.put("DAYLIMIT", DAYLIMIT);
					map.put("DAYTOTAL", DAYTOTAL);
					map.put("REGDATE", regDate);
					map.put("REMARK", "");
				}
			}
			responseCode = "000000";
			responseDesc = "成功";
			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), dpRequest.getKeep(), dpRequest.getIp(), svcCode, responseCode, responseDesc, "S0A");

			resp = new DpInf06008Response();

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, itemNum, cardList);
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

	/**
	 * 根据母卡客户编码查询子卡客户编码和卡号
	 * 
	 * @param dpRequest
	 * @throws Exception
	 */
	private static List<Map<String, String>> getChildCardList(DpInf06008Request dpRequest) throws Exception {
		TCumAcctDao dao = new TCumAcctDao();
		String startNum = dpRequest.getStartNum();
		String endNum = dpRequest.getEndNum();
		if (!Charset.isEmpty(startNum, true) && !Charset.isEmpty(endNum, true)) {
			if (Long.valueOf(startNum) >= Long.valueOf(endNum)) {
				throw new INFException(INFErrorDef.STARTNUM_EQUAL_ENDNUM, INFErrorDef.STARTNUM_EQUAL_ENDNUM_DESC);
			}
		}
		String childCustCode = dpRequest.getChildCustCode();
		if (Charset.isEmpty(childCustCode, true)) {
			return dao.getChildInfoListMap(dpRequest.getCustCode(), startNum, endNum);
		} else {
			return dao.getChildInfoMap(childCustCode, dpRequest.getCustCode());
		}
	}

}
