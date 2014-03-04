package websvc.servant;


import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TInfOrderBusCfgDao;
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
import common.service.TransManage;
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.ErrorProcess;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.TradeRequest;
import common.xml.dp.TradeResponse;

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
public class INF_01_003 {

    public static String svcInfName = "INF01003";
    private static final Logger log = Logger.getLogger(INF_01_003.class);
    private static final String AC_GUARANTEE_VERIFY = "1041";		// 担保交易确认
    
    private static final String AC_GUARANTEE_CANCLE = "1042";		// 担保交易取消
    
    private static final String AC_AUTH_VERIFY = "1051";			// 预授权确认
    
    private static final String AC_AUTH_CANCLE = "1052";			// 预授权取消
	
	public static String execute(String in0, String in1) {
		long startTime = System.currentTimeMillis();
		log.info("==============INF_01_003 start trade, starttime="+startTime+" ==============");
		TradeRequest tradeRequest = null; 		// 入参对象
		TradeResponse resp = null;				// 出参对象
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
		boolean isupdate = false;
		String outordercode = "";
		try{
			respInfo = new RespInfo(in1, "10");				// 返回信息头
			
			// 获取入参对象
			tradeRequest = new TradeRequest(in1);
			tmnNum = tradeRequest.getTmnNum();
			keep = tradeRequest.getKeep();
			ip = tradeRequest.getIp();
			String channelCode = tradeRequest.getChannelCode();
			
			// 出参对象
			resp = new TradeResponse();
			
			String agentCode = tradeRequest.getAgentCode();		// 商户编码
			String payeeCode = tradeRequest.getPayeeCode();		// 收款商户编码
			String txnAmount_req = tradeRequest.getTxnAmount();		// 交易金额：分为单位
			String actionCode_req = tradeRequest.getActionCode();	// 操作编码
			String transSeq = tradeRequest.getTransSeq();		// 交易流水号
			outordercode = tradeRequest.getOrderSeq(); //外部订单号
			
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
			List privList = PayCompetenceManage.payFunc(agentCode, channelCode);
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if("cln_Trade".equals(str) && ChannelCode.AGENT_CHANELCODE.equals(channelCode)){
					flag = true;
				}else if("ws_Trade".equals(str)){
					flag = true;
				}
			}
			if (!flag) {
				throw new Exception("没有交易-订单支付权限");
			}
			
			//付款商户关联机构验证
			if(!TCumInfoDao.verifyMerIdCustCode(tradeRequest.getAgentCode(), tradeRequest.getMerId())){
				
				if(TCumInfoDao.getMerIdByCustCode(tradeRequest.getAgentCode(),tradeRequest.getMerId()))
				
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			}
			
			//收款商户关联机构验证
			if(!TCumInfoDao.verifyMerIdCustCode(tradeRequest.getPayeeCode(), tradeRequest.getMerId())){
				
				if(TCumInfoDao.getMerIdByCustCode(tradeRequest.getPayeeCode(),tradeRequest.getMerId()))
				
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_R_NOT_MATCH_MERG_DESC);
			}
			
			
			//判断金额是否为0
			if (Double.parseDouble(txnAmount_req) <= 0) {
				throw new Exception("交易金额不能为0");
			}
			// 判断收款方和付款方是否是同一个
			if(agentCode.equals(payeeCode)) {
				throw new Exception("收款方和付款方不能相同");
			}
			
			// 判断是否担保确认
			if(actionCode_req.equals(AC_GUARANTEE_VERIFY)) {
				//判断收款方、付款方是否与订单相符
				String result=INF_01_003.validOrder(transSeq, agentCode,payeeCode);
				if(!"0".equals(result)){
					throw new Exception(result);
				}
				isGuaranteeVerify = true;
			} else 
			// 判断是否担保取消
			if(actionCode_req.equals(AC_GUARANTEE_CANCLE)) {
				//判断收款方、付款方是否与订单相符
				String result=INF_01_003.validOrder(transSeq, agentCode,payeeCode);
				if(!"0".equals(result)){
					throw new Exception(result);
				}
				isGuaranteeCancle =  true;
			} else 
			// 判断是否预授权确认
			if(actionCode_req.equals(AC_AUTH_VERIFY)) {
				//判断收款方、付款方是否与订单相符
				String result=INF_01_003.validOrder(transSeq, agentCode,payeeCode);
				if(!"0".equals(result)){
					throw new Exception(result);
				}
				isAuthVerify = true;
			} else 
			// 判断是否预授权取消
			if(actionCode_req.equals(AC_GUARANTEE_CANCLE) || actionCode_req.equals(AC_AUTH_CANCLE)) {
				
				//判断收款方、付款方是否与订单相符
				String result=INF_01_003.validOrder(transSeq, agentCode,payeeCode);
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
			
//			tradeRequest.setMark1("");
//			tradeRequest.setMark2("");
			isupdate = TInfOperInLogManager.verifyOrder(outordercode,keep,tmnNum,svcInfName,actionCode_req);
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
			} else if (actionCode.equals("1040")){
				// 一般交易流程
				dataSet = manage.auth(tradeRequest, cardAcctNbr, payeeCardAcctNbr,tradeRequest);
			} else {
				dataSet = manage.transProcess(tradeRequest, cardAcctNbr, payeeCardAcctNbr,tradeRequest);
			}

			
           /***************************************回调  将交易推送给商户  开始*******************************************************/
//			String callBackUrl = manage.getCallBackUrlByTremId(tradeRequest.getTmnNum());//回调地址
//			if(callBackUrl==null||callBackUrl.equals(""))
//				throw new Exception("商户未配置回调地址");
//             //拼装回调参数
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
//			StringBuffer sb = new StringBuffer("INTERFACETYPE=10005");//接口定值
//			sb.append("&TRANDATE=" + sdf.format(new Date()))//交易时间
//			.append("&KEEP=" + tradeRequest.getKeep())//Keep值
//			.append("&ORDERAMOUNT=" + txnAmount_req)//订单金额
//			.append("&TRADECODE=" + tradeRequest.getOrderSeq())//外订单号
//			.append("&SYSREFNO=" + dataSet.getByID("4002", "401"))//系统参考号
//			.append("&MERID=" + tradeRequest.getMerId())//接入号
//			.append("&ACTIONCODE=" + actionCode_req)//操作代码
//			.append("&RESULT=" + Charset.lpad(dataSet.getByID("0001", "000"), 6, "0"));//结果码
//			try {
//				//获取服务器证书
//				X509Certificate oCert = NETCAPKI.getSrvX509Certificate();
//				//进行BASE64编码后产生的字符串
//				String cer = NETCAPKI.getX509CertificateString(oCert);
//				//进行签名后得到二进制签名数据,BASE64编码后得到可视的SIGN
//				String sign = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(sb.toString().getBytes("UTF-8")));
//				sb.append("&SIGN="+ sign).append("&CER=" + cer);
//			} catch (Exception e) {
//				e.printStackTrace();
//				Log.info("签名信息出错");
//				throw new Exception("签名信息出错");
//			}
//			new Thread(new CallbackThread(manage,sb.toString(),callBackUrl)).start();
		/***************************************回调  将交易推送给商户  结束*******************************************************/
			
			String resultCode = (String) dataSet.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			
			//更新订单控制状态
			TInfOrderBusCfgDao cfgDao = new TInfOrderBusCfgDao();
			if(isupdate&&responseCode.equals("0000")){
            	
            	cfgDao.updateTInfOrderStat(tmnNum, outordercode, OrderConstant.S0C);
			}else if(isupdate&&!ErrorProcess.isTimeOut(responseCode)){

            	cfgDao.updateTInfOrderStat(tmnNum, outordercode, OrderConstant.S0F);
			}
			
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) dataSet.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}
            
			// 获取返回结果
//			responseCode = dataSet.getByID("0001", "000");		// 响应码
			
			String responseContent = dataSet.getByID("0002", "000");	// 响应码描述
			String respOrderSeq = tradeRequest.getOrderSeq();			// 订单号
			String respTransSeq = dataSet.getByID("4002", "401");		// 交易流水号
			String tradeTxnAmount = dataSet.getByID("6303", "600");		// 交易金额：出参时返回
			String agentBalance = dataSet.getByID("6035", "600");		// 付款商户账户余额
			String activeBalance = dataSet.getByID("6036", "600");		// 账户可用余额
			String frozenBalance = dataSet.getByID("6037", "600");		// 账户冻结余额
			String actionSysrefno = dataSet.getByID("4062", "401");     //业务系统参考号
			String paySysrefno = dataSet.getByID("4118", "401");        //系统参考号
			
            //当业务是预授权确认和取消	调用SCS0005 获取订单的支付系统参考号和业务系统参考号
			
			if(isAuthVerify||isAuthCancle){
				IParamGroup g002 = new ParamGroupImpl("002");// 包头
				g002.put("0021", "4002");
				g002.put("0022", respTransSeq);
				g002.endRow();			
				
				IServiceCall caller = new ServiceCallImpl();
				PackageDataSet ds = caller.call("SCS","SCS0005",g002);// 组成交易数据包,调用SCS0005接口
				
				String result = (String) ds.getParamByID("0001", "000").get(0);
				responseCode = result;
				//返回结果为失败时，抛出异常
				if(Long.valueOf(result) != 0) {
					String resultMsg = (String) ds.getParamByID("0002", "000").get(0);
					throw new Exception(resultMsg);
				}
				
				actionSysrefno = ds.getByID("4062", "404");     // 业务系统参考号
				paySysrefno = ds.getByID("4118", "408");        //支付系统参考号
			}
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
			
			//设置系统参考号  
			String sysRefno = "";
            if(isGuaranteeCancle||isAuthVerify){
				sysRefno = paySysrefno;
			}else  if(isGuaranteeVerify||isAuthCancle){
				sysRefno = actionSysrefno;
			}
			else{
            	sysRefno = respTransSeq;
            }
			//插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, responseContent, "S0A");
			long endTime = System.currentTimeMillis();
			log.info("==============INF_01_003 end trade, endtime="+endTime+"  usertime="+(endTime-startTime)+"ms=============");
			// 出参
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(),
					"SUCCESS", responseCode, responseContent, respOrderSeq, respTransSeq, tradeTxnAmount ,agentBalance,
					activeBalance, frozenBalance,sysRefno);
			
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
//			return ExceptionHandler.toXML(new XmlINFException(
//					resp, e, respInfo), id);
			return ExceptionHandler.toOutOrderXML(new XmlINFException(
					resp, e, respInfo), id,null,isupdate,tmnNum, outordercode,null,false);
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
		g002.put("0022",transSeq);
		PackageDataSet dataSet = null;
		String resultCode="";
		try {
			IServiceCall caller = new ServiceCallImpl();
			dataSet=caller.call("SCS","SCS0005",g002);	// 组成SCS0001交易数据包
			resultCode=dataSet.getByID("0001","000");	// 获取SCS0001接口的000组的0001参数
		} catch (Exception e) {
			e.printStackTrace();
			return "调用SCS0005服务超时异常";
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
		
		//判断订单状态
//		if(count>0){
//			String code=(String)dataSet.getParamByID("4013","401").get(0);
//			if(!code.equals("S0D")) {
//				return "订单已处理完";
//			}
//		}
		
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
	
	public static String executeForMD5(String in0, String in1){
		TradeRequest tradeRequest = null; 		// 入参对象
		TradeResponse resp = new TradeResponse();				// 出参对象
		RespInfo respInfo = null;				// 返回信息头
		String md5Key = null;
		try {
			respInfo = new RespInfo(in1, "10");	
			tradeRequest = new TradeRequest(in1);
			//客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(tradeRequest.getChannelCode())) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(tradeRequest.getStaffCode(), tokenValidTime);
				tradeRequest.verifyByMD5(tradeRequest.xmlSubString(in1), md5Key);
				TInfLoginLogDao.updateRanduseTimeByStaffCode(tradeRequest.getStaffCode());
			}
			
			PackageDataSet dataSet = callCUM1003(tradeRequest);
			String responseCode = dataSet.getByID("0001", "000");
			if (Long.valueOf(responseCode) == 0) {
				String oldXml = execute(in0, in1);
				
				return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);
			}else{
				throw new Exception("密码校验失败!");
			}
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(
					resp, e, respInfo), null);
		}
	}
	

	private static PackageDataSet callCUM1003(TradeRequest dpRequest)throws Exception{
		
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

}
