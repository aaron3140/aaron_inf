package websvc.servant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mortbay.log.Log;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
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
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpCardAccountInfoRequest;
import common.xml.dp.DpCardAccountInfoResponset;
import common.xml.dp.DpInf02004Responset;
import common.xml.dp.DpInf02016Request;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 账户余额查询接口
 * 
 * 
 */
public class INF02004 {
	public static String svcInfName = "02004";

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

		DpInf02004Responset resp = null;
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
			//如果是40就验证IVR权限 
			if(channelCode.equals("40")){
				flag = PayCompetenceManage.getIvrFunc(cardAccountInfoRequest.getStaffCode(), PrivConstant.IVR_MANAGE_IVR_BALANCE);
				if (!flag) {
					throw new Exception("没有IVR账户查询权限");
				}
			}else{
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
			}
			// 写日志
			// pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
			// svcInfName,"INF_01_001", SocketConfig.getSockIp(),
			// "AGENTCODE", agentCode);
			// id.setPk(pk);

			// 判断所选资金管理模式和实际是否相符
			String bankMode = cardAccountInfoRequest.getBankMode();
			TCumInfoDao dao = new TCumInfoDao();
			if (!"BT1001".equals(bankMode)){//不填时当普通卡处理
			String bankModeFromDb = dao.getBankMode(agentCode);
			if ("BT1002".equals(bankMode)) {// 子母卡
				if (!"BT1013".equals(bankModeFromDb) && !"BT1014".equals(bankModeFromDb)) {
					throw new Exception("请输入正确的资金管理模式");
				} else {
					bankMode = bankModeFromDb;
					cardAccountInfoRequest.setBankMode(bankMode);
				}
			} else if (!bankMode.equals(bankModeFromDb)) {
				throw new Exception("请输入正确的资金管理模式");
			}}

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
			System.out.println("payeeCardAcctNbr:" + payeeCardAcctNbr);
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
				// if
				// (accttype==null||accttype.length()<1||(!accttype.equals("0001")))
				// {
				// continue;
				// }
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

			// String responTime=new
			// SimpleDateFormat("yyyyMMddHHmmss").format(new Date());// 查询时间

			// 返回结果
			resp = new DpInf02004Responset();

			IServiceCall callerC = new ServiceCallImpl();

			String DAYLIMIT = "0";
			String DAYTOTAL = "0";

			try {
//				IParamGroup c200 = new ParamGroupImpl("220"); // 包头
//				c200.put("2002", cardAccountInfoRequest.getAgentCode());
//				c200.put("2804", "T08");
//				DataSet = callerC.call("BIS", "CUM0018", c200);
//				resultCode = (String) DataSet.getParamByID("0001", "000").get(0);
//
//				// 返回结果为失败时，抛出异常
//				if (Long.valueOf(resultCode) == 0) {
//					DAYLIMIT = MathTool.yuanToPoint((String) DataSet.getParamByID("2035", "204").get(0));
//					ArrayList list = DataSet.getParamByID("2230", "223");
//					if (list != null && list.size() != 0) {
//						DAYTOTAL = MathTool.yuanToPoint((String) list.get(0));
//					}
//					
//				}
				TCumAcctDao acctDao = new TCumAcctDao();
				String acctCode =acctDao.getAcctCode(cardAccountInfoRequest.getAgentCode(), "ACCT002");
				IParamGroup g002 = new ParamGroupImpl("002"); // 包头
				g002.put("0021", "6001");
				g002.put("0022", acctCode);// 卡户
				PackageDataSet ds = caller.call("EAS", "EBK1008", g002);
				resultCode = (String) ds.getParamByID("0001", "000").get(0);

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
				Log.info(e.getMessage() + "--" + cardAccountInfoRequest.getStaffCode() + " 日交易金额，日累积额默认为0");
			}

			// 如果是子卡 则要返回其母卡的金额
			String MOTHERBOARD = "0";
			if ("BT1014".equals(bankMode)) {
				TCumAcctDao acctDao = new TCumAcctDao();
				String acctCode = acctDao.getParentAcctCode(agentCode);
				MOTHERBOARD = MathTool.yuanToPoint(getParentAcctCountBalance(acctCode));
			}
			String xmlStr = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode1, responseContent, bankMode, DAYLIMIT,
					DAYTOTAL, MOTHERBOARD, accountItems);
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

	/**
	 * 获取母卡账户余额
	 * 
	 * @param dpRequest
	 * @param cardAccNbr
	 * @return
	 * @throws Exception
	 */
	private static String getParentAcctCountBalance(String cardAccNbr) throws Exception {
		String balance = "0";
		IParamGroup e002 = new ParamGroupImpl("002"); // 包头
		e002.put("0021", "6001");
		e002.put("0022", cardAccNbr); // "110650101000001046"

		IServiceCall callerE = new ServiceCallImpl();
		PackageDataSet DataSet = callerE.call("EAS", "EBK0001", e002); // 组成数据包,调用EBK0001接口

		String resultCode = (String) DataSet.getParamByID("0001", "000").get(0);
		// 返回结果为失败时，抛出异常
		if (Long.valueOf(resultCode) != 0) {
			String resultMsg = (String) DataSet.getParamByID("0002", "000").get(0);
			Log.info("查询母卡余额出错 默认返回0 cardAccNbr=" + cardAccNbr);
			return balance;
			// throw new Exception(resultMsg);
		}

		// 返回响应码
		String responseContent = DataSet.getByID("0002", "000"); // 获取接口的000组的0002参数
		int count = DataSet.getParamSetNum("604");

		if (count < 1) {
			Log.info("无此母卡账户信息 默认返回0 cardAccNbr=" + cardAccNbr);
			return balance;
			// throw new Exception("无此母卡账户信息");
		}
		for (int i = 0; i < count; i++) {
			String accttype = (String) DataSet.getParamByID("6421", "604").get(i);
			if ("0007".equals(accttype)) {
				balance = (String) DataSet.getParamByID("6035", "604").get(i); // 账户总余额
				break;
			}
		}
		return balance;
	}

}
