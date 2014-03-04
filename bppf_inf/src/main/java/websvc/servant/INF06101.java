package websvc.servant;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfOrderBusCfgDao;
import common.dao.TPnmPartnerDao;
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
import common.utils.DateTool;
import common.utils.ErrorProcess;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf06101Request;
import common.xml.dp.DpInf06101Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 邱亚建 2013-4-7 下午04:28:08<br>
 *         TODO 信用卡还款接口
 * 
 */
public class INF06101 {
	private static final Log logger = LogFactory.getLog(INF06101.class);

	public static String svcInfName = "INF06101";

	public static String executeForMD5(String in0, String in1) {
		String oXml = execute(in0, in1);
		return ResponseVerifyAdder.pkgForMD5(oXml, null);
	}

	public static String execute(String in0, String in1) {
		DpInf06101Request dpRequest = null;

		DpInf06101Response resp = new DpInf06101Response();

		RespInfo respInfo = null;

		String responseCode = "";

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String keep = "";// 获取流水号

		String ip = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);
		boolean isupdate = false;
		String orderseq = "";
		String buscode = "0000";
		try {

			dpRequest = new DpInf06101Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			tmnNum = dpRequest.getTmnNum();

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();
			orderseq = dpRequest.getOrderSeq();
			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum,
					svcCode, "XML", "", "", "", "", "S0A");
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
			List privList = PayCompetenceManage.payFunc(
					dpRequest.getCustCode());
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.WS_PAYMENTS_CREDIT.equals(str)) {
					r = true;
					break;
				}

			}

			if (!r) {
				throw new Exception("你没有信用卡还款的权限");
			}

			TransManage transManage = new TransManage();

			// IPOS处理
			if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())) {

				Map<String, String> map = transManage
						.getCustCodeByExtTermNumNo(dpRequest.getTmnNumNo());
				if (map != null && map.size() != 0) {
					String custCode = map.get("CUST_CODE");
					String tmnNumNo = map.get("TERM_CODE");
					dpRequest.setCustCode(custCode);
					dpRequest.setTmnNumNo(tmnNumNo);
				} else {
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_DESC);
				}
				/*
				 * String cust =
				 * transManage.getCustCodeByExtTermNumNo(dpRequest.
				 * getTmnNumNo()); if (cust == null || "".equals(cust)) { throw
				 * new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
				 * INFErrorDef.CUSTCODE_NOT_MATCH_DESC); } else {
				 * dpRequest.setCustCode(cust);
				 * dpRequest.setTmnNumNo(transManage
				 * .getTermNumNoByExt(dpRequest.getTmnNumNo()));
				 * 
				 * }
				 */
			} else {
				if (dpRequest.getTmnNumNo().length() < 12) {
					// dpRequest.setTmnNumNo(transManage.getTermNumNoByExt(dpRequest.getTmnNumNo()));
					String tmnNumNo = transManage.getTermNumNoByExt(dpRequest
							.getTmnNumNo(), dpRequest.getCustCode());
					if (tmnNumNo != null && !"".equals(tmnNumNo)) {
						dpRequest.setTmnNumNo(tmnNumNo);
					} else {
						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
								INFErrorDef.TMNNUMNO_NOT_MATCH_DESC);
					}
				}
			}

			// 关联机构验证
			if (!TCumInfoDao.verifyMerIdCustCode(dpRequest.getCustCode(),
					dpRequest.getMerId())) {

				if (TCumInfoDao.getMerIdByCustCode(dpRequest.getCustCode(),
						dpRequest.getMerId()))

					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			}

			// 验证客户编码和终端号是否对应
			// if
			// (!ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode()))
			// {
			// boolean flag =
			// transManage.isCustCodeMatchTermNumNo(dpRequest.getCustCode(),
			// dpRequest.getTmnNumNo());
			// if (!flag) {
			// throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
			// INFErrorDef.CUSTCODE_NOT_MATCH_TERMNUMNO_DESC);
			// }
			// }

			PackageDataSet ds = null;
			String payType = dpRequest.getPayType();
			String areaCode = dpRequest.getBankArea();
			TCumAcctDao acctDao = new TCumAcctDao();
			String targetAccount = dpRequest.getTargetAccount();
			//判断是否是信用卡
            if(!acctDao.getIsCreditCard(targetAccount)){
            	throw new Exception("信用卡有误!");
			}
			String bankCode = dpRequest.getBankCode();
			if(Charset.isEmpty(bankCode, true)){
				bankCode = acctDao.getBankCode(targetAccount);
			}
			if(areaCode.equals("")||areaCode==null){
//				areaCode = acctDao.getAreaCode(targetAccount);
				areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());
			}
			logger.info(dpRequest.getKeep()+" bankCode>>>"+bankCode+" areaCode>>>"+areaCode+" targetAccount>>>"+targetAccount);
			if(bankCode==null||bankCode.equals("")||areaCode==null||areaCode.equals("")){
				throw new INFException(INFErrorDef.CAN_NOT_FIND_ROUTE,INFErrorDef.CAN_NOT_FIND_ROUTE_DESC_2);
			}
			
			isupdate = TInfOperInLogManager.verifyOrder(orderseq,keep,tmnNum,svcInfName,buscode);
			
			if ("1".equals(payType)) {
				ds = tradePos(dpRequest,areaCode,bankCode);// 交易[信用卡还款]
			} else if ("2".equals(payType)) {
				ds = tradeDaiFu(dpRequest,areaCode,bankCode);// 交易[信用卡还款]
			} else {
				throw new INFException(INFErrorDef.PAYTYPE_UNSUPPORT,
						INFErrorDef.PAYTYPE_UNSUPPORT_DESC);
			}
			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			//更新订单控制状态
			TInfOrderBusCfgDao cfgDao = new TInfOrderBusCfgDao();
			if(isupdate&&responseCode.equals("0000")){

            	cfgDao.updateTInfOrderStat(tmnNum, orderseq, OrderConstant.S0C);
			}else if(isupdate&&!ErrorProcess.isTimeOut(responseCode)){

            	cfgDao.updateTInfOrderStat(tmnNum, orderseq, OrderConstant.S0F);
			}
			String responseDesc = null;
			
//			if("0040".equals(resultCode)){
//				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
//				String transSeq = ds.getByID("4002", "401");
//				return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
//						.getRespType(), respInfo.getKeep(), "FAIL",
//						responseCode, responseDesc, dpRequest.getTmnNumNo(),
//						transSeq, dpRequest.getOrderSeq(), "", "",
//						"");
//			}
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0 || Long.valueOf(resultCode) == 40) {//结果码为0040时也取响应描述  qiuyajian 2013-09-10 9:49
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}
			String transSeq = ds.getByID("4002", "401");
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dpRequest.getTmnNumNo(),
					transSeq, orderseq, dpRequest
							.getTargetAccount(), dpRequest.getRemark1(),
					dpRequest.getRemark2());
		} catch (XmlINFException spe) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, spe.getMessage(),
						"S0A");
			}
			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
//			return ExceptionHandler.toXML(
//					new XmlINFException(resp, e, respInfo), infId);
			return ExceptionHandler.toOutOrderXML(new XmlINFException(
					resp, e, respInfo), infId,null,isupdate,tmnNum, orderseq,null,false);

		}
	}

	/**
	 * 调用SCS0001
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet tradeDaiFu(DpInf06101Request dpRequest,String areaCode,String bankCode)
			throws Exception {
		 PackageDataSet dataSet2 = null;
		 TPnmPartnerDao dao = new TPnmPartnerDao();
		 String patnercode = dao.getPrtnCode301ByCustCode(dpRequest.getCustCode());
		try {
			// 一次路由
			TransManage tm = new TransManage();
			dataSet2 = tm.firstRoute(dpRequest.getCustCode(), areaCode, dpRequest.getChannelCode(), "06010005",patnercode, dpRequest.getTmnNum(), dpRequest
					.getTxnAmount(), "PT1004", bankCode);
		} catch (Exception e) {
			throw new INFException(INFErrorDef.CAN_NOT_FIND_ROUTE,
					INFErrorDef.CAN_NOT_FIND_ROUTE_DESC);
		}
		String newOrgCode = dataSet2.getByID("4098", "423");
		String newActionCode = dataSet2.getByID("4051", "423");
		//如果外部没有送过来,则根据custcode去查企业账户的区域编码
		String bankArea = dpRequest.getBankArea();
		if(Charset.isEmpty(bankArea,true)){
			bankArea = TCumInfoDao.getAreaCode(dpRequest.getCustCode());
		}
//		String newOrgCode = "86650002";
//		String newActionCode = "06010005";
//		String acctName = acctDao.getAccName(dpRequest.getCustCode(), "ACCT001");
//		String bankId = acctDao.getBankCode(targetAccount); // 银行编码[通过银行帐号查询]
		String txnAmount = MathTool.pointToYuan(dpRequest.getTxnAmount());// 分转元
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", dpRequest.getAcceptDate());// 受理时间
		g401.put("4012", "信用卡还款");// 订单描述，目前是硬编码
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode60To20());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 终端流水号
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		g401.put("4028", dpRequest.getOrderSeq());// 外部订单号
		g401.put("4029", dpRequest.getOutCustSign());// 外部客户标识
		
		g401.put("4284", dpRequest.getMerId());// 机构编码 //20130628 wanght
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", txnAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", txnAmount);// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4064", dpRequest.getCustCode());
		g404.put("4049", "0006");// 产品编码
		g404.put("4051", newActionCode);// 业务编码
		g404.put("4052", dpRequest.getTargetAccount());// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4072", newActionCode);
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "101");// 费用项标识
		g405.endRow();

		//控制标识说明:0 核心添加到数据库 1 不添加
		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_CERTID");//证件类型
		String certType = dpRequest.getCertType();
		if(isEmpty(certType)){
			g407.put("4088", "");
			g407.put("4080", "1");// 控制标识
		}else{
			g407.put("4088", certType);
			g407.put("4080", "0");// 控制标识
		}
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_CERTCODE");
		String certNo = dpRequest.getCertNo();
		if(isEmpty(certNo)){
			g407.put("4088", "");
			g407.put("4080", "1");// 控制标识
		}else{
			g407.put("4088", certNo);
			g407.put("4080", "0");// 控制标识
		}
		g407.endRow();
		
		//银行帐号所属银行代码
		if (Charset.isEmpty(bankCode, true)) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_BANKID");
			g407.put("4080", "1");// 控制标识
			g407.put("4088", "");
			g407.endRow();
		} else {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_BANKID");
			g407.put("4080", "0");// 控制标识
			g407.put("4088", bankCode);
			g407.endRow();
		}

		String phoneNum = dpRequest.getContactPhone();
		if (Charset.isEmpty(phoneNum, true)) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_PHONENUM");
			g407.put("4080", "1");
			g407.put("4088", "");
			g407.endRow();
		} else {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_PHONENUM");
			g407.put("4080", "0");
			g407.put("4088", phoneNum);
			g407.endRow();
		}

		String addr = dpRequest.getContactAddr();
		if (Charset.isEmpty(addr, true)) {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_ADDREASS");
			g407.put("4080", "1");// 控制标识
			g407.put("4088", "");
			g407.endRow();
		} else {
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", newActionCode);// 业务编码
			g407.put("4087", "SCS_ADDREASS");
			g407.put("4080", "0");// 控制标识
			g407.put("4088", addr);
			g407.endRow();
		}

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_CREDITEFFDATE");// 信用卡有效期
		String creditValidTime = dpRequest.getCreditValidTime();
		if(isEmpty(creditValidTime)){
			g407.put("4088", "");
			g407.put("4080", "1");// 控制标识
		}else{
			g407.put("4088", creditValidTime);
			g407.put("4080", "0");// 控制标识
		}
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_CREDITVERIFYCODE");// 信用卡验证码
		String creditValidCode = dpRequest.getCreditValidCode();
		if(isEmpty(creditValidCode)){
			g407.put("4088", "");
			g407.put("4080", "1");// 控制标识
		}else{
			g407.put("4088", creditValidCode);
			g407.put("4080", "0");// 控制标识
		}
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDTYPE");// 对公/对私标识
		g407.put("4080", "0");
		g407.put("4088", dpRequest.getPrivateFlag());
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_BANKNAME");//银行帐号户名
		g407.put("4080", "0");
		g407.put("4088", dpRequest.getAccName());
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_BANKBELONG");//银行帐号归属地
		if(isEmpty(bankArea)){
			g407.put("4088", "");
			g407.put("4080", "1");// 控制标识
		}else{
			g407.put("4088", bankArea);
			g407.put("4080", "0");// 控制标识
		}
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_CARDCHECKDATE");//清算日期
		g407.put("4080", "0");
		g407.put("4088", DateTool.getCurDate3());
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT1004");// 支付方式编码
		g408.put("4098", newOrgCode);// 支付机构编码 86650002
		g408.put("4099", "0001");// 账户类型编码
		String bankInfo = dpRequest.getBankInfo();
		if(!isEmpty(bankInfo)){
			g408.put("4100", bankInfo);// 开户行信息
		}
		g408.put("4101", dpRequest.getTargetAccount());// 账号
		g408.put("4102", dpRequest.getPayPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.put("4107", "");
		g408.put("4108", "");
		g408.put("4109", "");
//		g408.put("4119", dpRequest.getOrderSeq());
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}

	/**
	 * 调用SCS0001
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet tradePos(DpInf06101Request dpRequest,String areaCode,String bankCode)
			throws Exception {
		PackageDataSet dataSet2 = null;
		TPnmPartnerDao dao = new TPnmPartnerDao();
		String patnercode = dao.getPrtnCode301ByCustCode(dpRequest.getCustCode());
		try {
		// 一次路由
		TransManage tm = new TransManage();
		dataSet2 = tm.firstRoute(dpRequest.getCustCode(),
				areaCode, dpRequest.getChannelCode(), "06010004",patnercode, dpRequest.getTmnNum(), dpRequest
						.getTxnAmount(), "PT1003", bankCode);
		} catch (Exception e) {
			throw new INFException(INFErrorDef.CAN_NOT_FIND_ROUTE,
					INFErrorDef.CAN_NOT_FIND_ROUTE_DESC);
		}
		String newActionCode = dataSet2.getByID("4051", "423");
		// String newActionCode = "06010004";
		String newOrgCode = dataSet2.getByID("4098", "423");

		String txnAmount = MathTool.pointToYuan(dpRequest.getTxnAmount());// 分转元

		// DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		// String tradeTime = df.format(new Date());
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", dpRequest.getAcceptDate());// 受理时间
		g401.put("4012", "易办事信用卡还款");// 订单描述，目前是硬编码
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode60To20());// 渠道类型编码
		g401.put("4017", dpRequest.getOrderSeq());// 终端流水号
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		g401.put("4029", dpRequest.getOutCustSign());//外部客户标识

		g401.put("4284", dpRequest.getMerId());// 机构编码 //20130628 wanght
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", txnAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", txnAmount);// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4064", dpRequest.getCustCode());
		g404.put("4049", "0006");// 产品编码
		g404.put("4051", newActionCode);// 业务编码
		// g404.put("4052", TCumInfoDao.getCustName(dpRequest.getCustCode()));//
		// 业务对象
		g404.put("4052", dpRequest.getTargetAccount());// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");
		g404.put("4072", newActionCode);
		g404.endRow();

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");
		g407.put("4080", "1");// 控制标识
		g407.put("4088", dpRequest.getTargetAccount());// 还款目标账户
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_PINDATA");
		g407.put("4080", "1");// 控制标识
		g407.put("4088", dpRequest.getPayPassword());// 支付密码
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_PCICTRL");
		g407.put("4080", "1");// 控制标识
		g407.put("4088", dpRequest.getPsamCardNo());// PSAM卡
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_CARDTYPE");
		g407.put("4080", "1");// 控制标识
		g407.put("4088", "1");// 卡类型
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDTYPE");
		g407.put("4080", "1");// 控制标识
		g407.put("4088", "1");// 卡折标示
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_VERIFYTYPE");// 签约方式
		g407.put("4080", "1");
		g407.put("4088", "0001");
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT1003");// 支付方式编码
		// g408.put("4098", newOrgCode);// 支付机构编码 86980001
		g408.put("4098", newOrgCode);// 支付机构编码 86980001
		g408.put("4099", "0001");// 账户类型编码
		// g408.put("4100", "中国工商银行");// 开户名称
		g408.put("4101", dpRequest.getPayAccount());// 账号
		g408.put("4102", dpRequest.getPayPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", txnAmount);// 支付金额
		g408.put("4127", bankCode);// 前置支付机构,查卡表的bankcode
		g408.put("4107", dpRequest.getTrackTwo());// 磁道2信息
		g408.put("4108", dpRequest.getTrackThree());// 磁道3信息
		g408.put("4109", dpRequest.getNetworkNo());// 国际网络号
		// g408.put("4119", "");// 支付请求流水号
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}

	private static boolean isEmpty(String str){ 
		return Charset.isEmpty(str, true);
	}
}
