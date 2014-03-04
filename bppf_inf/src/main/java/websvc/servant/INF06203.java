package websvc.servant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.dao.TCumAcctDao;
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
import common.service.TransManage;
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.DateTime;
import common.utils.MathTool;
import common.utils.PasswordUtil;
import common.utils.PrivConstant;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf06203Request;
import common.xml.dp.DpInf06203Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 2014年1月15日 下午1:55:57<br>
 * 
 *         本来描述：[东莞公交]卡操作接口
 */
public class INF06203 {
	private static final Log logger = LogFactory.getLog(INF06203.class);

	public static String svcInfName = "INF06203";

	public static String execute(String in0, String in1) {
		DpInf06203Request dpRequest = null;

		DpInf06203Response resp = new DpInf06203Response();

		logger.info("请求参数：：" + in1);

		RespInfo respInfo = null;

		String responseCode = "";

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String keep = "";// 获取流水号

		String ip = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf06203Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			tmnNum = dpRequest.getTmnNum();

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "", "", "", "", "S0A");
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

			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if (!(dpRequest.getTradeTime().matches(RegexTool.DATE_FORMAT))) {
				throw new Exception("输入日期格式不正确");
			}

			TransManage transManage = new TransManage();
			// IPOS处理
			if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())) {

				Map<String, String> map = transManage.getCustCodeByExtTermNumNo(dpRequest.getTmnNumNo());
				if (map != null && map.size() != 0) {
					String custCode = map.get("CUST_CODE");
					String tmnNumNo = map.get("TERM_CODE");
					dpRequest.setCustCode(custCode);
					dpRequest.setTmnNumNo(tmnNumNo);
				} else {
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_DESC);
				}
			} else {
				if (dpRequest.getTmnNumNo().length() < 12) {
					// dpRequest.setTmnNumNo(transManage.getTermNumNoByExt(dpRequest.getTmnNumNo()));
					String tmnNumNo = transManage.getTermNumNoByExt(dpRequest.getTmnNumNo(), dpRequest.getCustCode());
					if (tmnNumNo != null && !"".equals(tmnNumNo)) {
						dpRequest.setTmnNumNo(tmnNumNo);
					} else {
						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.TMNNUMNO_NOT_MATCH_DESC);
					}
				}
			}
			// 判断有无权限
			List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode());
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if (PrivConstant.EPOS_DG_BUS.equals(str)) {
					r = true;
					break;
				}
			}
			if (!r) {
				throw new Exception("你没有东莞公交操作权限");
			}

			// 关联机构验证
			// if (!TCumInfoDao.verifyMerIdCustCode(dpRequest.getCustCode(), dpRequest.getMerId())) {
			// if (TCumInfoDao.getMerIdByCustCode(dpRequest.getCustCode(), dpRequest.getMerId())) {
			// throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			// }
			// }

			// 密码鉴权
			PasswordUtil.AuthenticationPassWord(dpRequest, dpRequest.getStaffCode(), dpRequest.getPayPassword(), dpRequest.getEcardNo(), dpRequest.getPsamcardNo(),
					dpRequest.getPassFlag());

			// 明细项内容
			String detailContent = "";

			// 调网关
			PackageDataSet ds = sag0002(dpRequest);
			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			String responseDesc = null;
			// 调网关返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}
			detailContent = (String) ds.getParamByID("6923", "692").get(0);
			Map<String, String> map = parserXml(detailContent);// 解析xml

			String cardoprType = dpRequest.getCardoprType();// 2062 联机公用电子钱包充值 2063 联机售卡
			String transSeq = "";
			String systemNo = ds.getByID("6901", "690");
			if ("2062".equals(cardoprType) || "2063".equals(cardoprType)) {

				// 调核心
				ds = callSCS0001OpenOrRechargeCard(dpRequest, systemNo);
				resultCode = (String) ds.getParamByID("0001", "000").get(0);
				responseCode = resultCode;
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
				transSeq = ds.getByID("4002", "401");
				map.put("TRANSSEQ", transSeq);
				// 扣费结果判断
				if (Long.valueOf(resultCode) != 0) {// 扣费失败调冲正
					Map<String, String> mapRoll = parserXmlToRoll(detailContent);
					ds = rollback_sag(dpRequest, mapRoll, systemNo);
				}

			}

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, dpRequest.getStaffCode(), map,
					dpRequest.getTmnNumNo(), systemNo, dpRequest.getRemark1(), dpRequest.getRemark2());
		} catch (XmlINFException spe) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), infId);

		}
	}

	/**
	 * 解析业务网关返回的xml，获取返回数据
	 * 
	 * @param detailContent
	 * @return
	 * @throws Exception
	 */
	private static Map<String, String> parserXml(String detailContent) throws Exception {
		Document doc = DocumentHelper.parseText(detailContent);
		Map<String, String> map = new HashMap<String, String>();
		String[] keys = { "SAMID", "EDCARDID", "CITYCODE", "CARDID", "CARDMKND", "CARDSKND", "CARDMODEL", "AUTHSEQ", "DICMAC", "LIMITEAUTHSEQL", "MAC2", "SYSDATETIME","MESSAGEDATETIME"};
		Element root = doc.getRootElement();
		for (String key : keys) {
			String value = root.elementTextTrim(key.toLowerCase());
			if (Charset.isEmpty(value, true)) {
				value = "";
			}
			map.put(key, value);
		}
		return map;
	}

	/**
	 * 卡操作返回的xml中获取冲正参数
	 * 
	 * @param detailContent
	 * @return
	 * @throws Exception
	 */
	private static Map<String, String> parserXmlToRoll(String detailContent) throws Exception {
		Document doc = DocumentHelper.parseText(detailContent);
		Map<String, String> map = new HashMap<String, String>();
		String[] keys = { "EDCARDID", "AUTHSEQ", "LIMITEAUTHSEQL", "POSSEQUENCE", "RELOADBAL" ,"TAC","SYSDATETIME"};
		Element root = doc.getRootElement();
		for (String key : keys) {
			String value = root.elementTextTrim(key.toLowerCase());
			if (Charset.isEmpty(value, true)) {
				value = "";
			}
			map.put(key, value);
		}
		return map;
	}

	/**
	 * 调用SCS0001 完成开卡或充值操作
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callSCS0001OpenOrRechargeCard(DpInf06203Request dpRequest, String systemNo) throws Exception {
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 通过客户编码查区域编码
		String areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());
		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 支付帐号
		// 2062 充值
		// 2063 开卡
		String cardoprType = dpRequest.getCardoprType();
		String actionCode = "";// 业务编码
		String orderDesc = "";// 订单描述
		String sagSerCode = "";// 业务网关服务编码
		String origamtYuan = MathTool.pointToYuan(dpRequest.getOrigamt());// 单位分转元
		if ("2062".equals(cardoprType)) {
			actionCode = "12010004";
			orderDesc = "东莞通-充值";
			sagSerCode = "COC402";
		} else if ("2063".equals(cardoprType)) {
			actionCode = "12010006";
			orderDesc = "东莞通-开卡";
			sagSerCode = "COC401";
			origamtYuan = "20";// 开卡收20块钱工本费
		}

		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", dpRequest.getTradeTime());// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode60To20());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 终端流水号
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		g401.put("4012", orderDesc);// 订单描述，目前是硬编码
		g401.put("4146", dpRequest.getStaffCode());// 操作员
		g401.put("4028", dpRequest.getOrderSeq());// 外部订单号
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", origamtYuan);// 订单原始金额(元)
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", origamtYuan);// 订单应付金额（元）
		g402.put("4030", "0");//
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0012");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4064", dpRequest.getCustCode());
		g404.put("4052", dpRequest.getCardId());// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", systemNo);
		g404.put("4072", "");// 可选业务编码
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", origamtYuan);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", origamtYuan);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0200");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_USERTYPE");
		g407.put("4088", "2");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_SERVID");
		g407.put("4088", sagSerCode);
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099", "0007");// 账户类型编码
		g408.put("4101", acctCode);// 支付账号
		g408.put("4102", dpRequest.getPayPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", origamtYuan);// 支付金额
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}

	/**
	 * 卡操作
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet sag0002(DpInf06203Request dpRequest) throws Exception {
		// 通过客户编码查区域编码
		String areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());
		// 押金处理
		String cardoprType = dpRequest.getCardoprType();
		String deposit = dpRequest.getDeposit();
		String origamt = dpRequest.getOrigamt();
		String reloadbal =origamt;
		if ("2063".equals(cardoprType)) {
			reloadbal = "0";
		}
		// 包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "COC203");// 服务编码
		g675.put("6752", dpRequest.getChannelCode());// 渠道号
		g675.put("6753", dpRequest.getKeep());// 流水号
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码
		g675.endRow();

		// 业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", "12010001");// 业务编码
		g676.put("6762", "0012");// 产品编码
		g676.put("6763", areaCode);// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();

		String commandlen = dpRequest.getCommandlen();
		if (commandlen.matches("^0+$")) {
			commandlen = "0";
		}

		// 查询信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", commandlen);
		g680.put("6802", "");
		g680.put("6803", "");
		g680.put("6804", "");
		g680.put("6805", "");
		g680.put("6806", "");
		g680.put("6807", "");
		g680.put("6808", dpRequest.getTradeTime());// 20130508151906
		g680.endRow();

		// 查询项
		IParamGroup g682 = new ParamGroupImpl("682");
		g682.put("6820", "X001");
		g682.put("6821", "");// 订单项名称
		g682.put("6822", "01");

		
		String params = "<PARAMS>" + //
				"<POSID>" + dpRequest.getTmnNumNo() + "</POSID>" + //
				"<POSSEQUENCE>" + dpRequest.getOrderSeq() + "</POSSEQUENCE>" + //
				"<TERMID>" + dpRequest.getTmnNumNo() + "</TERMID>" + //
				"<OPERID>" + dpRequest.getTmnNumNo() + "</OPERID>" + //
				"<CARDID>" + dpRequest.getCardId() + "</CARDID>" + //
				"<CARDOPRTYPE>" + dpRequest.getCardoprType() + "</CARDOPRTYPE>" + //
				"<CITYCODE>" + dpRequest.getCityCode() + "</CITYCODE>" + //
				"<CARDMKND>" + dpRequest.getCardmknd() + "</CARDMKND>" + //
				"<CARDSKND>" + dpRequest.getCardsknd() + "</CARDSKND>" + //
				"<CARDMODEL>" + dpRequest.getCardModel() + "</CARDMODEL>" + //
				"<TRANSTYPE>" + dpRequest.getTransType() + "</TRANSTYPE>" + //
				"<DEPOSIT>" + deposit + "</DEPOSIT>" + //
				"<ORIGAMT>" + origamt + "</ORIGAMT>" + //
				"<RELOADBAL>" + reloadbal + "</RELOADBAL>" + //
				"<CARDVALDATE>" + dpRequest.getCardvalDate() + "</CARDVALDATE>" + //
				"<SRCBAL>" + dpRequest.getSrcbal() + "</SRCBAL>" + //
				"<CARDSEQ>" + dpRequest.getCardseq() + "</CARDSEQ>" + //
				"<KEYVER>" + dpRequest.getKeyver() + "</KEYVER>" + //
				"<ALGIND>" + dpRequest.getAlgind() + "</ALGIND>" + //
				"<CARDRAND>" + dpRequest.getCardRand() + "</CARDRAND>" + //
				"<MAC1>" + dpRequest.getMac1() + "</MAC1>" + //
				"<DIVDATA>" + dpRequest.getDivData() + "</DIVDATA>" + //
				"<BATCHNO>" + dpRequest.getBatchNo() + "</BATCHNO>" + //
				"<KEYSET>" + dpRequest.getKeySet() + "</KEYSET>" + //
				"<COMMANDLEN>" + commandlen + "</COMMANDLEN>" + "<RESERVED>" + dpRequest.getDivData() + "0000" + "</RESERVED>";

		if ("0".equals(commandlen)) {
			params += "</PARAMS>";
		} else {
			params += "<COMMAND>" + dpRequest.getCommand() + "</COMMAND></PARAMS>";
		}

		g682.put("6823", params);
		g682.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SAG", "SAG0002", g675, g676, g680, g682);

		return ds;
	}

	/**
	 * 冲正
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet rollback_sag(DpInf06203Request dpRequest, Map<String, String> map, String systemNo) throws Exception {
		// 通过客户编码查区域编码
		String areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());
		// 押金处理
		String cardoprType = dpRequest.getCardoprType();
		String deposit = dpRequest.getDeposit();
		String serverCode = "";
		String orderDesc = "";
		String XmlContent = "";
		String txnDate="";
		String txnTime="";
		
		String sysdatetime=map.get("SYSDATETIME");
		if(!"".equals(sysdatetime)&&sysdatetime!=null){
			txnDate=sysdatetime.substring(0, 8);//日期部分
		    txnTime=sysdatetime.substring(8,sysdatetime.length());//时间部分
		}
		
		if ("2063".equals(cardoprType)) {// 开卡
			orderDesc = "开卡冲正参数";
			deposit = "0";
			serverCode = "COC902";
			XmlContent = "<PARAMS>"
				+ "<POSID>" + dpRequest.getTmnNumNo() + "</POSID>" + 
				"<POSSEQUENCE>" + dpRequest.getOrderSeq() + "</POSSEQUENCE>" +
				"<TERMID>"+ dpRequest.getTmnNumNo() + "</TERMID>" +
				"<OPERID>" + dpRequest.getTmnNumNo() + "</OPERID>" + 
				"<EDCARDID>" + map.get("EDCARDID") + "</EDCARDID>" + 
				"<CARDID>"+ dpRequest.getCardId() + "</CARDID>" +
				"<CARDCNT>" + "0" + "</CARDCNT>" + 
				"<CARDMKND>" + dpRequest.getCardmknd() +"</CARDMKND>" +
				"<CARDSKND>"+ dpRequest.getCardsknd() + "</CARDSKND>" + 
				"<CARDMODEL>" + dpRequest.getCardModel() + "</CARDMODEL>" + 
				"<SALEMODE>" + "80" + "</SALEMODE>" +
				"<DEPOSIT>"+ deposit + "</DEPOSIT>" + 
				"<BEFBALANCE>" + dpRequest.getSrcbal() + "</BEFBALANCE>" + 
				"<TXNAMT>" + map.get("RELOADBAL") + "</TXNAMT>" +
				"<TRANSTYPE>" + dpRequest.getTransType() + "</TRANSTYPE>" + 
				"<CARDVALDATE>"+ dpRequest.getCardvalDate() + "</CARDVALDATE>" + 
				"<CITYCODE>" + dpRequest.getCityCode() + "</CITYCODE>" + 
				"<CARDVERNO>" + "0" + "</CARDVERNO>" +
				"<BATCHNO>"+ dpRequest.getBatchNo() + "</BATCHNO>" +
				"<AUTHSEQ>" + map.get("AUTHSEQ") +"</AUTHSEQ>" +
				"<LIMITEDAUTHSEQL>" + map.get("LIMITEAUTHSEQL")+ "</LIMITEDAUTHSEQL>" +
				"<TAC>" + map.get("TAC") + "</TAC>" + 
				"<TXNDATE>" + txnDate + "</TXNDATE>" + 
				"<TXNTIME>" + txnTime + "</TXNTIME>" + 
				"<KEYSET>" + dpRequest.getKeySet() + "</KEYSET>" + 
				"</PARAMS>";
		} else {// 充值
			orderDesc = "充值冲正参数";
			serverCode = "COC901";
			XmlContent = "<PARAMS>" 
				+ "<POSID>" + dpRequest.getTmnNumNo() + "</POSID>" +
				"<POSSEQUENCE>" + dpRequest.getOrderSeq() + "</POSSEQUENCE>" + 
				"<TERMID>"+ dpRequest.getTmnNumNo() + "</TERMID>" + 
				"<OPERID>" + dpRequest.getTmnNumNo() + "</OPERID>" +
				"<CARDID>" + dpRequest.getCardId() + "</CARDID>" + 
				"<CARDCNT>"+ "0" + "</CARDCNT>" + 
				"<CARDMKND>" + dpRequest.getCardmknd() + "</CARDMKND>" +
				"<CARDSKND>" + dpRequest.getCardsknd() + "</CARDSKND>" +
				"<CARDMODEL>"+ dpRequest.getCardModel() + "</CARDMODEL>" + 
				"<BEFBALANCE>" + dpRequest.getSrcbal() + "</BEFBALANCE>" + 
				"<ORIGAMT>" + dpRequest.getOrigamt() + "</ORIGAMT>"+
				"<TXNAMT>" + map.get("RELOADBAL") + "</TXNAMT>" +
				"<TRANSTYPE>" + dpRequest.getTransType() + "</TRANSTYPE>" + 
				"<HANDINGCHARGE>" + "0" + "</HANDINGCHARGE>" +
				"<DEPOSIT>" + deposit + "</DEPOSIT>" + 
				"<CARDVALDATE>"+ dpRequest.getCardvalDate() + "</CARDVALDATE>" + 
				"<CITYCODE>" + dpRequest.getCityCode() + "</CITYCODE>" +
				"<CARDVERNO>" + "0" + "</CARDVERNO>" +
				"<BATCHNO>"+ dpRequest.getBatchNo() + "</BATCHNO>" +
				"<AUTHSEQ>" + map.get("AUTHSEQ") + "</AUTHSEQ>" + 
				"<LIMITEDAUTHSEQL>" + map.get("LIMITEAUTHSEQL")+"</LIMITEDAUTHSEQL>" +
				"<LASTPOSSVSEQ>" + map.get("POSSEQUENCE") + "</LASTPOSSVSEQ>" + 
				"<TAC>" + map.get("TAC") + "</TAC>" + 
				"<TXNDATE>" + txnDate + "</TXNDATE>" + 
				"<TXNTIME>" + txnTime + "</TXNTIME>" + 
				"<KEYSET>" + dpRequest.getKeySet() + "</KEYSET>" +
				"</PARAMS>";
		}
		// 包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", serverCode);// 服务编码
		g675.put("6752", dpRequest.getChannelCode());// 渠道号
		g675.put("6753", dpRequest.getKeep() + "2");// 这里流水号码=第一次调用网关卡操作的流水号+2
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码
		g675.endRow();

		// 业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", "12010004");// 业务编码
		g676.put("6762", "0012");// 产品编码
		g676.put("6763", areaCode);// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();

		// 查询信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", "");
		g680.put("6802", "");
		g680.put("6803", "");
		g680.put("6804", "");
		g680.put("6805", "");
		g680.put("6806", "");
		g680.put("6807", "");
		g680.put("6808", dpRequest.getTradeTime());// 20130508151906
		g680.put("6809", systemNo);
		g680.endRow();

		// 查询项
		IParamGroup g682 = new ParamGroupImpl("682");
		g682.put("6820", "X001");
		g682.put("6821", orderDesc);// 订单项名称
		g682.put("6822", "01");
		g682.put("6823", XmlContent);

		g682.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SAG", "SAG0009", g675, g676, g680, g682);

		return ds;
	}

}
