package websvc.servant;


import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mortbay.log.Log;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfOrderBusCfgDao;
import common.dao.TInfVaildateDao;
import common.dao.TPnmPartnerDao;
import common.dao.TSymSysParamDao;
import common.dao.TbisTanOrderDao;
import common.entity.BankAcctInfo;
import common.entity.SignOrder;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.service.TransManage;
import common.utils.Charset;
import common.utils.ErrorProcess;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.SubmitForm;
import common.utils.WebSvcTool;
import common.utils.verify.NETCAPKI;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02032Request;
import common.xml.dp.DpInf05002Request;
import common.xml.dp.DpInf05002Responset;
import common.xml.dp.DpInf5002TradeRequest;
import common.xml.dp.Dpinf5002TradeResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;


/**
 * 交易接口
 * File                 : INF_01_001.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : bppf_inf
 * JDK version used     : JDK 1.6
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2012-2-2 下午05:51:48 [created]
 * Author               : Zhilong Luo 罗志龙
 * Email                : luozhilong@tisson.cn
 **/
public class INF05002 {
	
    public static String svcInfName = "INF05002";
    
    private static final String AC_GUARANTEE_VERIFY = "1041";		// 担保交易确认
    
    private static final String AC_GUARANTEE_CANCLE = "1042";		// 担保交易取消
    
    private static final String AC_AUTH_VERIFY = "1051";			// 预授权确认
    
    private static final String AC_AUTH_CANCLE = "1052";			// 预授权取消
    public static final String ACCT_TYPE = "0001";			// 账户类型编码

	
	public static String execute(String in0, String in1,boolean isRechargeAndTrans) {
		DpInf5002TradeRequest tradeRequest = null; 		// 入参对象
		Dpinf5002TradeResponse resp = null;				// 出参对象
		RespInfo respInfo = null;				// 返回信息头
		PackageDataSet dataSet = null;			// 调用接口返回结果
		boolean isGuaranteeVerify = false;		// 是否担保交易确认
		boolean isGuaranteeCancle = false;		// 是否担保交易取消
		boolean isAuthVerify = false;			// 是否预授权确认
		boolean isAuthCancle = false;			// 是否预授权取消
		String tmnNum = null;	//受理终端号
		
		TransManage manage = new TransManage();	// 业务组件
		
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		String keep = "";//获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			respInfo = new RespInfo(in1, "10");				// 返回信息头
			
			// 获取入参对象
			tradeRequest = new DpInf5002TradeRequest(in1);
			tmnNum = tradeRequest.getTmnNum();
			keep = tradeRequest.getKeep();
			ip = tradeRequest.getIp();
			
			// 出参对象
			resp = new Dpinf5002TradeResponse();
			
			String agentCode = tradeRequest.getAgentCode();		// 商户编码
			String payeeCode = tradeRequest.getPayeeCode();		// 收款商户编码
			String txnAmount_req = tradeRequest.getTxnAmount();		// 交易金额：分为单位
			String actionCode_req = tradeRequest.getActionCode();	// 操作编码
			String transSeq = tradeRequest.getTransSeq();		// 交易流水号
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "agentCode"
					, agentCode, "transSeq", transSeq, "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			//判断插入是否成功
			if(tInfOperInLog!=null){
				boolean flag = manager.selectTInfOperInLogByKeep(keep);
				//判断流水号是否可用
				if(flag){
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				}else{
					
					//准予通过
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			
			boolean flag = false;
			List privList = PayCompetenceManage.payFunc(tradeRequest.getAgentCode(), tradeRequest.getChannelCode());
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if("cln_AgentPay".equals(str)){
					flag = true;
				}
			}
			if (!flag) {
				throw new Exception("没有支付插件交易权限");
			}
			
			// 判断收款方和付款方是否是同一个
			if(agentCode.equals(payeeCode)) {
				throw new Exception("收款方和付款方不能相同");
			}
			
			// 判断是否担保确认
			if(actionCode_req.equals(AC_GUARANTEE_VERIFY)) {
				//判断收款方、付款方是否与订单相符
				String result=INF05002.validOrder(transSeq, agentCode,payeeCode);
				if(!"0".equals(result)){
					throw new Exception(result);
				}
				isGuaranteeVerify = true;
			} else 
			// 判断是否担保取消
			if(actionCode_req.equals(AC_GUARANTEE_CANCLE)) {
				//判断收款方、付款方是否与订单相符
				String result=INF05002.validOrder(transSeq, agentCode,payeeCode);
				if(!"0".equals(result)){
					throw new Exception(result);
				}
				isGuaranteeCancle =  true;
			} else 
			// 判断是否预授权确认
			if(actionCode_req.equals(AC_AUTH_VERIFY)) {
				//判断收款方、付款方是否与订单相符
				String result=INF05002.validOrder(transSeq, agentCode,payeeCode);
				if(!"0".equals(result)){
					throw new Exception(result);
				}
				isAuthVerify = true;
			} else 
			// 判断是否预授权取消
			if(actionCode_req.equals(AC_GUARANTEE_CANCLE) || actionCode_req.equals(AC_AUTH_CANCLE)) {
				//判断收款方、付款方是否与订单相符
				String result=INF05002.validOrder(transSeq, agentCode,payeeCode);
				if(!"0".equals(result)){
					throw new Exception(result);
				}
				isAuthCancle = true;
			}
			
			// 操作类型为交易确认/交易取消,交易流水号不能为空
			if(isGuaranteeVerify || isGuaranteeCancle || isAuthVerify || isAuthCancle) {
				if(Charset.trim(transSeq).equals("")) {
					throw new Exception("交易流水号不能为空");
				}
			}
			
			// 单位转换：分转元
			String txnAmount = MathTool.pointToYuan(txnAmount_req);
			tradeRequest.setTxnAmount(txnAmount);
			
			// 翻译ActionCode
			String actionCode = transActionCode(actionCode_req);
			if(actionCode == null) {
				throw new Exception("非法的操作编码");
			}
			tradeRequest.setActionCode(actionCode);
			
			// 获取付款客户的天讯卡户
			String cardAcctNbr = manage.getTissonCardAcct(agentCode);
			if(cardAcctNbr == null) {
				throw new Exception("付款客户的卡户不存在");
			} 
			
			// 获取收款客户的天讯卡户
			String payeeCardAcctNbr = manage.getTissonCardAcct(payeeCode);
			if(payeeCardAcctNbr == null) {
				throw new Exception("收款客户的卡户不存在");
			}
			
			tradeRequest.setMark1("");
			tradeRequest.setMark2("");
//			String orderseq = tradeRequest.getOrderSeq();
			boolean isupdate = TInfOperInLogManager.verifyOrder(tradeRequest.getOrderSeq(),keep,tmnNum,svcInfName,actionCode_req);
			// 操作类型为担保交易确认
			if(isGuaranteeVerify) {
				// 担保交易确认处理流程
				dataSet = manage.guaranteeVerify(tradeRequest, cardAcctNbr, payeeCardAcctNbr);
			} else 
			// 操作类型为担保交易取消
			if(isGuaranteeCancle) {
				// 担保交易取消处理流程
				dataSet = manage.guaranteeCancle(tradeRequest, cardAcctNbr, payeeCardAcctNbr);
			} else
			// 操作类型为预授权确认/预授权取消
			if(isAuthVerify || isAuthCancle) {
				// 预授权确认/预授权取消流程
				dataSet = manage.authVerifyOrCancle(tradeRequest, cardAcctNbr, payeeCardAcctNbr, isAuthVerify);
			} else if (isRechargeAndTrans){//充值转账
				
				// 业务组件
				SignBankManage signBankManage = new SignBankManage();
				// 获取客户ID
				String custId = signBankManage.getCustIdByCode(tradeRequest.getAgentCode());
				if (custId == null) {
					throw new Exception("该商户号不存在");
				}
				// 获取客户绑定银行卡列表
				List<BankAcctInfo> bankAcctList = signBankManage.getBankAcctList(custId);
				if (bankAcctList == null || bankAcctList.size() == 0) {
					throw new Exception("该商户的签约银行卡不存在");
				}
				if (bankAcctList.size() != 1) {
					throw new Exception("该商户存在多张签约银行卡");
				}
				BankAcctInfo bankAcctInfo = bankAcctList.get(0);

				// 生成授权银行卡充值单据
				SignOrder order = scs0201(tradeRequest, actionCode, bankAcctInfo);

				// 获取手机号码
				String mobile = cum003(tradeRequest);
				//付款人和收款人信息验证
				if (tradeRequest.getAgentCode().equals(tradeRequest.getCustCode())) {
					throw new Exception("收款方客户编码和付款方客户编码不能一样");
				}
				// 收款商户关联机构验证
//				if (!TCumInfoDao.verifyMerIdCustCode(tradeRequest.getAgentCode(), tradeRequest.getMerId())) {
//					if (TCumInfoDao.getMerIdByCustCode(tradeRequest.getAgentCode(), tradeRequest.getMerId()))
//						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
//								INFErrorDef.CUSTCODE_R_NOT_MATCH_MERG_DESC);
//				}

				
				//保存充值转账信息到前置的表
				TbisTanOrderDao tranDao = new TbisTanOrderDao();
				Hashtable<String, Object> rt = new Hashtable<String, Object>();
				rt.put("KEEP", tradeRequest.getKeep());
				rt.put("ORDER_CODE", tradeRequest.getOrderSeq());
				rt.put("PAY_CUSTCODE", tradeRequest.getAgentCode());
				rt.put("COLLE_CUSTCODE", tradeRequest.getPayeeCode());
				rt.put("PAY_MONEY", tradeRequest.getTxnAmount());//单位：元
				rt.put("RECHARGE_STAT", OrderConstant.S0P);
				rt.put("TRAN_STAT", OrderConstant.S0A);
				rt.put("BUS_TYPE", "OTR");
				tranDao.saveTanOrder(rt);
				//充值
				dataSet = signBankManage.rechargeForINF05002(order,  cardAcctNbr, mobile, tradeRequest);
				 String respCode = (String) dataSet.getParamByID("0001", "000").get(0);
				 String respDesc = "";
					// 返回结果为失败时，抛出异常
				if (Long.valueOf(respCode) == 0) {
					respDesc = (String)dataSet.getParamByID("0002", "000").get(0);
					// 更新充值状态
					tranDao.updateRecSucStat(tradeRequest.getKeep(), responseCode,respCode, respDesc);
				}
				//转账
				dataSet =signBankManage.transForINF05002(tradeRequest, order);
				String tranOrderId = dataSet.getByID("4002", "401");
				// 更新更新成功记录
				tranDao.updateTraOrder(tradeRequest.getKeep(), tranOrderId);
			} else if (actionCode.equals("1040")){
				// 一般交易流程
				dataSet = manage.auth(tradeRequest, cardAcctNbr, payeeCardAcctNbr,tradeRequest);
			} else {
				dataSet = manage.transProcess(tradeRequest, cardAcctNbr, payeeCardAcctNbr,tradeRequest);
			}
			
		/***************************************回调  将交易推送给商户  开始*******************************************************/
			String callBackUrl = manage.getCallBackUrlByTremId(tradeRequest.getTmnNum());//回调地址
			if(callBackUrl==null||callBackUrl.equals(""))
				throw new Exception("商户未配置回调地址");
	         //拼装回调参数
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			StringBuffer sb = new StringBuffer("INTERFACETYPE=10005");//接口定值
			sb.append("&TRANDATE=" + sdf.format(new Date()))//交易时间
			.append("&KEEP=" + tradeRequest.getKeep())//Keep值
			.append("&ORDERAMOUNT=" + txnAmount_req)//订单金额
			.append("&TRADECODE=" + tradeRequest.getOrderSeq())//外订单号
			.append("&SYSREFNO=" + dataSet.getByID("4002", "401"))//系统参考号
			.append("&MERID=" + tradeRequest.getMerId())//接入号
			.append("&ACTIONCODE=" + actionCode_req)//操作代码
			.append("&RESULT=" + Charset.lpad(dataSet.getByID("0001", "000"), 6, "0"));//结果码
			try {
				//获取服务器证书
				X509Certificate oCert = NETCAPKI.getSrvX509Certificate();
				//进行BASE64编码后产生的字符串
				String cer = NETCAPKI.getX509CertificateString(oCert);
				//进行签名后得到二进制签名数据,BASE64编码后得到可视的SIGN
				String sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(sb.toString().getBytes("UTF-8")));
				sb.append("&SIGN="+ sign).append("&CER=" + cer);
			} catch (Exception e) {
				e.printStackTrace();
				Log.info("签名信息出错");
				throw new Exception("签名信息出错");
			}
			new Thread(new CallbackThread(manage,sb.toString(),callBackUrl)).start();
		/***************************************回调  将交易推送给商户  结束*******************************************************/
			
			String resultCode = (String) dataSet.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			
			//更新订单控制状态
			TInfOrderBusCfgDao cfgDao = new TInfOrderBusCfgDao();
			if(isupdate&&responseCode.equals("0000")){

            	cfgDao.updateTInfOrderStat(tmnNum, tradeRequest.getOrderSeq(), OrderConstant.S0C);
			}else if(isupdate&&!ErrorProcess.isTimeOut(responseCode)){

            	cfgDao.updateTInfOrderStat(tmnNum, tradeRequest.getOrderSeq(), OrderConstant.S0F);
			}
			
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) dataSet.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}
			
			String responseContent = dataSet.getByID("0002", "000");	// 响应码描述
			String respOrderSeq = tradeRequest.getOrderSeq();			// 订单号
			String respTransSeq = dataSet.getByID("4002", "401");		// 交易流水号
			String tradeTxnAmount = dataSet.getByID("6303", "600");		// 交易金额：出参时返回
			String agentBalance = dataSet.getByID("6035", "600");		// 付款商户账户余额
			String activeBalance = dataSet.getByID("6036", "600");		// 账户可用余额
			String frozenBalance = dataSet.getByID("6037", "600");		// 账户冻结余额
			
			// 操作类型为交易确认/交易取消,不需要插入预处理表
			if( ! (isGuaranteeVerify || isGuaranteeCancle || isAuthVerify || isAuthCancle)) {
				// 插入预处理表
				tradeRequest.setTransSeq(respTransSeq);// 设置交易流水号
				manage.insertOppOrder(tradeRequest);
			} 
			
			// 元转分
			tradeTxnAmount = MathTool.yuanToPoint(tradeTxnAmount);
			agentBalance = MathTool.yuanToPoint(agentBalance);
			activeBalance = MathTool.yuanToPoint(activeBalance);
			frozenBalance = MathTool.yuanToPoint(frozenBalance);
			
			//插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, responseContent, "S0A");
			
			// 出参
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(),
					"SUCCESS", responseCode, responseContent, respOrderSeq, respTransSeq, tradeTxnAmount ,agentBalance,
					activeBalance, frozenBalance);
			
		} catch (XmlINFException spe) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e, respInfo), id);
		}
	}

	/**
	 * 翻译ActionCode
	 * @version: 1.00
	 * @history: 2012-2-20 下午08:38:09 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param actionCode
	 * @return
	 * @see
	 */
	private static String transActionCode(String actionCode) {
		String result = null;
		switch (Integer.parseInt(actionCode)) {
		case 1030:// 直接交易
			result = "01030001";
			break;
		case 1040:// 担保交易
			result = "01030005";
			break;
		case 1041:// 担保交易确认
			result = "01030005";
			break;
		case 1042:// 担保交易取消
			result = "01030005";
			break;
		case 1050:// 预授权
			result = "01030007";
			break;
		case 1051:// 预授权确认
			result = "01030007";
			break;
		case 1052:// 预授权取消
			result = "01030007";
			break;
		default:
			break;
		}
		return result;
	}
	
	/**
	 * 判断付款方/付款方是否与原订单相符
	 * @version: 1.00
	 * @history: 2012年5月16日 15:53:21 [created]
	 * @author WenChao chen 陈文超
	 * @param agentCode
	 * @param payeeCode
	 * @param transSeq
	 * @return
	 * @see
	 */
	private static String validOrder(String transSeq, String agentCode, String payeeCode) {
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021","4002");
		g002.put("0022",agentCode);
		PackageDataSet dataSet = null;
		String resultCode="";
		try {
			IServiceCall caller = new ServiceCallImpl();
			dataSet=caller.call("SCS","SCS0005",g002);	// 组成SCS0001交易数据包
			resultCode=dataSet.getByID("0001","000");	// 获取SCS0001接口的000组的0001参数
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!resultCode.equals("0000")) {
			return "订单不存在";
		}
		
		//获取401组数据
		int count=dataSet.getParamSetNum("401");
		if(count>0){
			String code=(String)dataSet.getParamByID("4004","401").get(0);
			if(!code.equals(agentCode)) {
				return "付款方与原订单不符";
			}
		}
		
		//获取404组数据
		count=dataSet.getParamSetNum("404");
		if(count>0){
			String code=(String)dataSet.getParamByID("4052","404").get(0);
			if(!code.equals(payeeCode)){
				return "收款方与原订单不符";
			}
		}
		
		return "0";
	}
	
	public static String executeForMD5(String in0, String in1) {
		DpInf05002Responset resp  = new DpInf05002Responset();
		RespInfo respInfo = null;				// 返回信息头
		
		DpInf5002TradeRequest tradeRequest = null; 		// 入参对象
		try {
			respInfo = new RespInfo(in1, "10");	
			DpInf05002Request dpRequest = new DpInf05002Request(in1);
			String verifyCode = dpRequest.getVerifyCode();
			verifyCode = verifyCode==null?"":verifyCode;
			
			//验证码是否超时
			String vCodeValidTime = TSymSysParamDao.getVerifyValidTime();
			Map codeMap = TInfVaildateDao.getVCode(dpRequest.getStaffCode(),vCodeValidTime);
			if(codeMap==null||!verifyCode.equalsIgnoreCase((String)codeMap.get("VAL_CODE"))){
				//失败返回客户端
				throw new Exception("验证码错误或有效期已过");
			}
			
			in1 = dpRequest.getRequestXml();
			tradeRequest = new DpInf5002TradeRequest(in1);
			
			// 验证商户签名
			tradeRequest.verify(in1);
			// 校验机构接入号和终端号绑定关系
			tradeRequest.verifyMeridTmnNum();
			PackageDataSet dataSet = callCUM1003(dpRequest);
			String responseCode = dataSet.getByID("0001", "000");
			if (Long.valueOf(responseCode) == 0) {
				
				//更新验证码无效
				TInfVaildateDao.updateVCode2(dpRequest.getStaffCode());
				
				String fundSource = dpRequest.getFundSource();
				boolean isRechargeAndTrans = false;//是否为充值转账
				if("0".equals(fundSource)){
					isRechargeAndTrans = false;
				}else if("1".equals(fundSource)){
					isRechargeAndTrans = true;
				}
				String oldXml = execute(in0, in1,isRechargeAndTrans);
				
				Document doc = DocumentHelper.parseText(oldXml);
				Element ele = (Element) doc.selectSingleNode("//RESPONSE-INFO");
				String result = ele.valueOf("@RESULT");
				String resCode = ((Element) doc.selectSingleNode("//RESPONSECODE")).getTextTrim();
				String resContent = ((Element) doc.selectSingleNode("//RESPONSECONTENT")).getTextTrim();
				String responseXml = ResponseVerifyAdder.pkg(oldXml);
				
				DpInf05002Responset dpInf05002Responset = new DpInf05002Responset();
				String outXml = dpInf05002Responset.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(),
						respInfo.getKeep(), result, resCode, resContent, responseXml);
				return ResponseVerifyAdder.pkgForMD5(outXml, null);
			}else{
				throw new Exception("密码校验失败!");
			}
		} catch (Exception e) {
			String oXml =  ExceptionHandler.toXML(new XmlINFException(
					resp, e, respInfo), null);
			try {
				Document doc = DocumentHelper.parseText(oXml);
				Element ele = (Element) doc.selectSingleNode("//RESPONSE-INFO");
				String result = ele.valueOf("@RESULT");
				String resCode = ((Element) doc.selectSingleNode("//RESPONSECODE")).getTextTrim();
				String resContent = ((Element) doc.selectSingleNode("//RESPONSECONTENT")).getTextTrim();
				String oldXml = ResponseVerifyAdder.pkg(oXml);
				
				DpInf05002Responset dpInf05002Responset = new DpInf05002Responset();
				String outXml = dpInf05002Responset.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(),
						respInfo.getKeep(), result, resCode, resContent, oldXml);
				return ResponseVerifyAdder.pkgForMD5(outXml, null);
			} catch (Exception e2) {
				DpInf05002Responset dpInf05002Responset = new DpInf05002Responset();
				return dpInf05002Responset.toXMLStr("", "", "019999", "服务器内部错误");
			}
			
		}
	}
	
	private static PackageDataSet callCUM1003(DpInf05002Request dpRequest)throws Exception{
		
//		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//		String tradeTime = df.format(new Date());
		
		String staff = dpRequest.getStaffCode();
		
		String verityType = "0001";	//支付密码
		
		String tmnNum = dpRequest.getTmnNum();
		
		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", staff);
		g200.put("2903", "2007");
		g200.put("2904", dpRequest.getPassword());
		g200.put("2172", "0001");
		g200.put("2173", verityType);
//		g200.put("2025", null);	
		g200.endRow();
		
		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", dpRequest.getChannelCode());
		g211.put("2077", tmnNum);
		g211.put("2078", null);
		g211.put("2085", dpRequest.getIp());
		g211.endRow();
			
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM1003", g200, g211);
		
		return dataSet;
	}
	
	private static SignOrder scs0201(DpInf5002TradeRequest dpRequest,
			String actionCode, BankAcctInfo bankAcctInfo) throws Exception {

		SignOrder order = new SignOrder();
		order.setMerId(dpRequest.getMerId());
		order.setKeep(dpRequest.getKeep());
		order.setChannelCode(dpRequest.getChannelCode());// 交易渠道
		order.setTmnNum(dpRequest.getTmnNum());// 交易终端号
		order.setActionCode(actionCode);// 操作类型：充值
		order.setTransSeq(dpRequest.getOrderSeq());// 交易序列号
		order.setBankAcctInfo(bankAcctInfo);// 客户绑定银行账户详细信息
		if (Double.valueOf(dpRequest.getTxnAmount()) < 0.1) {
			throw new Exception("金额不能少于1分钱");
		}
		
		String txnamount = dpRequest.getTxnAmount();// 单位已为元
		String finalAmount = txnamount;
		order.setAmount(txnamount);// 交易金额
		order.setFinalAmount(txnamount);

		TPnmPartnerDao dao = new TPnmPartnerDao();
		Map<String, String> map = dao.getPrntnCodeAndPrntType(dpRequest.getAgentCode());

		IParamGroup g423 = new ParamGroupImpl("423");
		g423.put("4230", "0001"); // 手续费
		g423.put("2011", dpRequest.getMerId()); // 接入机构的对应的商户编码
		g423.put("4330", map.get("PRTN_CODE")); // 实际做交易的商户编码
		g423.put("4331", map.get("PRTN_TYPE")); // 实际做交易的商户类型
		g423.put("2002", dpRequest.getAgentCode()); // 实际做交易的商户编码
		g423.put("4051", actionCode); // 业务编码
		g423.put("4098", bankAcctInfo.getBankCode()); // 银行编码
		g423.put("4006", bankAcctInfo.getAreaCode()); // 区域编码
		g423.put("4144", dpRequest.getChannelCode()); // 渠道号
		g423.endRow();

		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4025", txnamount); // 订单金额
		g402.put("4099", ACCT_TYPE); // 账户类型编码

		// 组成数据包,调用SCS0201接口
		IServiceCall caller = new ServiceCallImpl();

		PackageDataSet dataSet1 = caller.call("SCS", "SCS0201", g423, g402);
		String resCode = dataSet1.getByID("0001", "000");
		if (Long.valueOf(resCode) == 0) {
			String flagStr = dataSet1.getByID("4230", "423");
			if ("0001".equals(flagStr)) {
				String concessionType = dataSet1.getByID("4328", "423");
				String concession = dataSet1.getByID("4329", "423");
				if (concession == null || "".equals(concession)
						|| Double.valueOf(concession) == 0) {
					concession = "0";
				}

				order.setConcession(concession);
				order.setConcessionType(concessionType);
				// finalAmount = txnamount;
				double temp = 0;
				if ("DT003".equals(concessionType)) {
					temp = Double.parseDouble(txnamount)
							+ Double.parseDouble(concession);
					finalAmount = String.valueOf(temp);
				} else {
					temp = Double.parseDouble(txnamount)
							- Double.parseDouble(concession);
					if (temp < 0) {
						throw new Exception("计算手续费金额后为负数");
					}
					txnamount = String.valueOf(temp);
				}
				order.setAmount(txnamount);// 交易金额
				order.setFinalAmount(finalAmount);// 最终交易金额
			}
		}

		return order;

	}
	private static String cum003(DpInf5002TradeRequest dpRequest) throws Exception {

		String mobile = null;
		// 根据客户编码，调用CUM0003查询联系信息
		IParamGroup g0003_200 = new ParamGroupImpl("200"); // 包头
		g0003_200.put("2002", dpRequest.getAgentCode());
		g0003_200.endRow();

		IParamGroup g0003_002 = new ParamGroupImpl("002"); // 包头
		g0003_002.put("0011", "207");
		g0003_002.endRow();

		// 组成数据包,调用CUM0003接口
		IServiceCall caller2 = new ServiceCallImpl();
		PackageDataSet dataSet = caller2.call("BIS", "CUM0003", g0003_200,
				g0003_002);// 组成交易数据包,调用CUM0003接口

		// 获取客户编码的手机号
		int count = dataSet.getParamSetNum("202");
		for (int i = 0; i < count; i++) {
			String code = (String) dataSet.getParamByID("2016", "202").get(i);
			if (code.equals("MOB")) {
				mobile = dataSet.getByID("2018", "202");
				break;
			}
		}

		return mobile;
	}

	public static void main(String[] args) {
		String pay = "<PayPlatRequestParameter><CTRL-INFO WEBSVRNAME=\"test\" WEBSVRCODE=\"test\" APPFROM=\"1234\" KEEP=\"90000086201209201211316330\" /><PARAMETERS><AGENTCODE>ti02@189.com</AGENTCODE><AREACODE>441000</AREACODE><ACTIONCODE>1030</ACTIONCODE><TXNAMOUNT>1</TXNAMOUNT><PAYEECODE>ti04@189.com</PAYEECODE><GOODSCODE>ak47</GOODSCODE><GOODSNAME>枪一支</GOODSNAME><ORDERSEQ>111111111111111</ORDERSEQ><TRANSSEQ></TRANSSEQ><TRADETIME>20120918000000</TRADETIME><MARK1></MARK1><MARK2></MARK2></PARAMETERS></PayPlatRequestParameter>";
		try {
			String sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(pay
					.getBytes("UTF-8")));
			String cer = NETCAPKI.getX509CertificateString(NETCAPKI
					.getSrvX509Certificate());
			System.out.println(sign);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class CallbackThread implements Runnable{
	private Logger log = Logger.getLogger(CallbackThread.class);
	private TransManage manage = null;
	private String sb = null;
	private String callBackUrl = null;
	CallbackThread(TransManage manage,String sb,String callBackUrl){
		this.manage = manage;
		this.sb = sb;
		this.callBackUrl = callBackUrl;
	}
	
	public void run(){
		// TODO Auto-generated method stub
		//进行回调
		log.info("回调信息 ：     ACTSER: "+callBackUrl+"--RESULT: "+sb);
		SubmitForm sform=new SubmitForm();
		sform.setStrUrl(callBackUrl);
		sform.submitForm(sb);
		//获取返回值
		if (sform.getResponseStr()==null||!sform.getResponseStr().trim().equals("UPTRAN_10005")) {
			//回调失败，将回调信息加到定时任务表中
			manage.makeTask(callBackUrl,sb,"UPTRAN_10005");
		}else
			log.info("回调接收到返回 ："+sform.getResponseStr());
	}
	
}
