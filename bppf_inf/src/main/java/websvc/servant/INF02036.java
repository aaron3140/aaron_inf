package websvc.servant;

import java.util.Map;

import mpi.client.data.TransData;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.tisson.pay.config.BftProperties;
import com.tisson.pay.config.RetCode;
import com.tisson.pay.service.BindCardService;
import com.tisson.pay.service.impl.BftPayResultImpl;
import com.tisson.pay.service.impl.BindCardServiceImpl;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TRegBindCardDao;
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
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02036Request;
import common.xml.dp.DpInf02036Response;
import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 邱亚建 2013-11-28 上午10:14:55<br/>
 * 
 * 本类描述:账户绑卡查询接口
 */
public class INF02036 {

	public static String svcInfName = "INF02036";

	private static final Logger log = Logger.getLogger(INF02036.class);

	public static String executeForMD5(String in0, String in1) {

		DpInf02036Response resp = new DpInf02036Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf02036Request dpRequest = new DpInf02036Request(in1);

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

		DpInf02036Request dpRequest = null;
		DpInf02036Response resp = null;
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

			dpRequest = new DpInf02036Request(in1);

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

			String phoneReg = "^[0-9]{11,15}$";
			if (!dpRequest.getCustCode().matches(phoneReg)) {
				throw new Exception("客户编码必须是手机号码");
			}

			// 校验客户编码和用户名是否匹配
			TCumInfoDao cumInfoDao = new TCumInfoDao();
			String custCodeByStaff = cumInfoDao.getCustCodeByStaff(dpRequest.getStaffCode());
			if (!StringUtils.equals(custCodeByStaff, dpRequest.getCustCode())) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST, INFErrorDef.CUSTCODE_NOT_MATCH_STAFF_DESC);
			}
			boolean isBind = callCUM0003(dpRequest);// 查询核心是否有绑卡信息
			String bindState = "";
			if (!isBind) {  
				log.info("核心查询结果为未绑卡，前往查询绑卡表 cust_code=" + dpRequest.getCustCode());
				TRegBindCardDao cardDao = new TRegBindCardDao();
				bindState = cardDao.getBindStateByCustCode(dpRequest.getCustCode());
				log.info("查询帮卡表结果为：[" + bindState + "] cust_code=" + dpRequest.getCustCode());
				if (Charset.isEmpty(bindState, true)) {
					bindState = "S00";// 如果没有查到对应的记录，则为未绑卡
					log.info("查询帮卡表结果为空或出错，默认为未绑卡：[" + bindState + "] cust_code=" + dpRequest.getCustCode());
				}
				// 如果绑卡表中的状态是S0A或S0D则去帮付通查询，否则直接返回表中的状态
				String queryType = dpRequest.getQueryType();
				log.info("查询类型 queryType=" + queryType);
				if ("1".equals(queryType)) {
					if ("S0A".equals(bindState) || "S0D".equals(bindState)) {
						log.info(dpRequest.getCustCode() + "绑卡状态为" + bindState);
						Map map = cardDao.getOrderInfoByCustCode(dpRequest.getCustCode());
						if(map!=null&&!map.isEmpty()){
							String orderNo = (String) map.get("BIND_ORDERNO");
							String openPhone = (String) map.get("OPEN_PHONE");
							log.info("查到的 orderNo="+orderNo +"   openPhone="+openPhone);
							bindState = queryBft(openPhone, orderNo, bindState);
						}
					}
				}
			} else {
				log.info("核心查询结果为绑卡成功 cust_code=" + dpRequest.getCustCode());
				bindState = "S0C";
			}

			// 0未绑卡 1绑卡成功 2绑卡中
			log.info("转换前----绑卡状态：bindState=" + bindState);
			if ("S0C".equals(bindState)) {
				bindState = "1";
			} else if ("S0D".equals(bindState)) {
				bindState = "2";
			} else {
				bindState = "0";
			}
			log.info("转换后----绑卡状态：bindState=" + bindState);

			responseCode = "000000";
			responseDesc = "成功";
			resp = new DpInf02036Response();
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, bindState, dpRequest.getRemark1(),
					dpRequest.getRemark2());
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
	 * 是否绑卡
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static boolean callCUM0003(DpInf02036Request dpRequest) throws Exception {
		// 根据客户编码，调用CUM0003查询联系信息
		IParamGroup g0003_200 = new ParamGroupImpl("200"); // 包头
		TCumInfoDao dao = new TCumInfoDao();
		String custCode = dao.getCustCodeByStaff(dpRequest.getStaffCode());
		g0003_200.put("2002", custCode);
		g0003_200.endRow();

		IParamGroup g0003_002 = new ParamGroupImpl("002"); // 包头
		g0003_002.put("0011", "207");
		g0003_002.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet DataSet = caller.call("BIS", "CUM0003", g0003_200, g0003_002);// 组成交易数据包,调用CUM0003接口

		// 获取207组卡户号
		int count = DataSet.getParamSetNum("207");
		String bankAcctNbr = "";
		for (int i = 0; i < count; i++) {
			// 获取卡户类型
			String cardAcctType = (String) DataSet.getParamByID("2048", "207").get(i);
			// 获取天讯卡户号
			if ("ACCT001".equals(cardAcctType)) {
				bankAcctNbr = (String) DataSet.getParamByID("2049", "207").get(i);
				break;
			}
		}
		if (!Charset.isEmpty(bankAcctNbr, true)) {
			return true;
		}
		return false;
	}

	/**
	 * 查询帮付通
	 * 
	 * @param orderNo
	 * @param bindState
	 */
	private static String queryBft(String openPhone, String orderNo, String bindState) {

		// 接收"结果查询报文"数据元
		String userid = "";
		String payid = "";
		String orderamount = "";
		String RetCode = "";
		String TransDate = "";
		String TransTime = "";
		String SystemSSN = "";
		String ylSystemSSN = "";
		String SysCode = "";
		String BankCard = "";
		String BankCode = "";
		String TransType = "";

		TransData transdataRep = new TransData();// 定义应答数据包
		TransData transdataReq = new TransData();// 定义请求数据包

		transdataReq.setMerID(BftProperties.getMerId());// ,填帮付通分配商户编号
		transdataReq.setUserID(openPhone);// 用户手机号
		transdataReq.setPayID(orderNo);// 商户订单号
		transdataReq.setOrderAmount(BftProperties.getOrderAmount());// 订单金额
		// transdataReq.setTransType("3001");// 交易类型
		transdataReq.setTransType("2003");// 交易类型 

		BftPayResultImpl payresult = new BftPayResultImpl();// 声明"结果查询报文"类对像
		transdataRep = payresult.sendPayFindRequest(transdataReq);// 调用"结果查询请求"方法,同时取应答

		// 从应答数据包取数据元
		userid = transdataRep.getUserID();
		payid = transdataRep.getPayID();
		orderamount = transdataRep.getOrderAmount();
		RetCode = transdataRep.getRetCode();
		TransDate = transdataRep.getTransDate();
		TransTime = transdataRep.getTransTime();
		SystemSSN = transdataRep.getSystemSSN();
		ylSystemSSN = transdataRep.getYlSystemSSN();
		SysCode = transdataRep.getSysCode();
		BankCard = transdataRep.getBankCard();
		BankCode = transdataRep.getBankCode();
		TransType = transdataRep.getTransType();
		log.info("BFT_QUERY UserID=[" + userid + "]");
		log.info("BFT_QUERY PayID=[" + payid + "]");
		log.info("BFT_QUERY OrderAmount=[" + orderamount + "]");
		log.info("BFT_QUERY TransDate=[" + TransDate + "]");
		log.info("BFT_QUERY TransTime=[" + TransTime + "]");
		log.info("BFT_QUERY SystemSSN=[" + SystemSSN + "]");
		log.info("BFT_QUERY ylSystemSSN=[" + ylSystemSSN + "]");
		log.info("BFT_QUERY RetCode=[" + RetCode + "]");
		log.info("BFT_QUERY SysCode=[" + SysCode + "]");
		log.info("BFT_QUERY BankCode=[" + BankCode + "]");
		log.info("BFT_QUERY BankCard=[" + BankCard + "]");
		log.info("BFT_QUERY TransType=[" + TransType + "]");

		// 帮付通的 transdataRep.getRetCode() 返回 T_0000才有查询结果
		
		log.info("BFT_QUERY 支付结果查询返回 RetCode=" + RetCode);
		if (!"T_0000".equals(RetCode)) {
			log.info("BFT_QUERY 支付结果查询返回 RetCode=" + RetCode + "  描述：" + getRetCode(RetCode).getDesc() + "::" + transdataRep.getDescription() + "  处理方式：不走绑卡流程 直接返回原状态");
			// 不走绑卡流程 直接返回原状态
			return bindState;
		}

		String retCode = "T_0000";
		TRegBindCardDao dao = new TRegBindCardDao();
		try {
			BindCardService service = new BindCardServiceImpl();
			retCode = service.doBindCard(transdataRep, "1");
		} catch (Exception e) {
			log.error("bft query error 商户业务逻辑处理出错...原因: " + e.getMessage());
			log.info("更新绑卡记录 bind_state：S0F 失败， stat：S0X 无效");
			dao.updateBindStateToFail(transdataRep.getPayID(), transdataRep.getSysCode() + "::" + transdataRep.getDescription() + "::" + e.getMessage());
			retCode = "T_0096";// 报文交互失败,其它错误
		}

		if (BindCardService.UNKNOW_RETCODE.equals(retCode)) {
			return bindState;
		}

		if ("T_0000".equals(retCode)) {
			// 绑卡成功
			bindState = "S0C";
		} else if ("T_0025".equals(retCode)) {// 订单状态已更新
			// 重新新查询绑卡表获取绑卡状态
			bindState = dao.getBindStateByCustCode(openPhone);
			log.info("订单[" + orderNo + "]状态已更新,重新新查询绑卡表获取绑卡状态 bindState=" + bindState);
		} else if ("T_0014".equals(retCode)) {
			return bindState;// 订单号不存在时，返回原有状态 【先查核心，后查绑卡表】
		} else {
			bindState = "S0X";// 绑卡失败
		}
		return bindState;
	}

	private static RetCode getRetCode(String retCode) {
		if ("T_0000".equals(retCode)) {
			return RetCode.T_0000;
		} else if ("T_0014".equals(retCode)) {
			return RetCode.T_0014;
		} else if ("T_0025".equals(retCode)) {
			return RetCode.T_0025;
		} else if ("T_0095".equals(retCode)) {
			return RetCode.T_0095;
		} else if ("T_0096".equals(retCode)) {
			return RetCode.T_0096;
		}
		return RetCode.T_XXXX;
	}

}
