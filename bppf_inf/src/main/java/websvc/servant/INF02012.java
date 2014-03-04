package websvc.servant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TCumInfoDaoTemp;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
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
import common.utils.MathTool;
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02012Request;
import common.xml.dp.DpInf02012Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 邱亚建 2013-4-22 下午02:49:46<br>
 *         TODO酬金结转接口
 * 
 */
public class INF02012 {
	private static final Log logger = LogFactory.getLog(INF02012.class);

	public static String svcInfName = "INF02012";

	public static String execute(String in0, String in1) {
		DpInf02012Request dpRequest = null;

		DpInf02012Response resp = new DpInf02012Response();

		RespInfo respInfo = null;

		String responseCode = "";

		logger.info("INF02012请求参数：：" + in1);

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String keep = "";// 获取流水号

		String ip = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			respInfo = new RespInfo(in1, "20");

			dpRequest = new DpInf02012Request(in1);
			
			// 客户端MD5校验--------------------------------------------
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(),
					tokenValidTime);
			dpRequest.verifyByMD5(md5Key);
			TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());
			//-------------------------------------------------------------------
			
//			判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode(), dpRequest.getChannelCode());
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if(PrivConstant.IPOS_RECEIVED_HONORARIUM.equals(str)){
					r = true;
					break;
				}

			}
			
			if(!r){
				throw new Exception("你没有酬金结转的权限");
			}
			
			//关联机构验证
			if(!TCumInfoDao.verifyMerIdCustCode(dpRequest.getCustCode(), dpRequest.getMerId())){
				
				if(TCumInfoDao.getMerIdByCustCode(dpRequest.getCustCode(),dpRequest.getMerId()))
				
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			}
			
			//密码验证
			PackageDataSet dataSet = callCUM1003(dpRequest);
			String resCode = dataSet.getByID("0001", "000");
			if (Long.valueOf(resCode) != 0) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
						INFErrorDef.PAY_PWD_FAULT);
			}
			
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
			
			PackageDataSet ds = null;
			if (Double.valueOf(dpRequest.getTxnAmount())<1) {
				throw new Exception("金额不能少于1分钱");
			}
			// 单位转换：分转元
			String txAmountin = MathTool.pointToYuan(dpRequest.getTxnAmount());
			dpRequest.setTxnAmount(txAmountin);
			// 调用SCS0018接口获取手续费
			IParamGroup g001 = new ParamGroupImpl("001");
			g001.put("0021", "01");// 税金计算
			g001.endRow();
			IParamGroup g423 = new ParamGroupImpl("423");
			g423.put("4004", dpRequest.getCustCode());// 客户编码
			g423.put("4051", "01030008");// 业务编码
			g423.put("4049", "0001");// 产品编码
			g423.endRow();
			IParamGroup g402 = new ParamGroupImpl("402");
			g402.put("4025", dpRequest.getTxnAmount());// 订单金额
			g402.endRow();
			IServiceCall caller = new ServiceCallImpl();
			ds= caller.call("SCS", "SCS0018", g001,  g402,g423);
			String resultCode = ds.getByID("0001", "000");
			String responseDesc = null;
			if(Long.valueOf(resultCode) != 0) {
				responseDesc = ds.getByID("0002", "000");
				throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE, responseDesc);
			}
			 String isConfig = ds.getByID("4230", "423");//是否配置了，未配置：    00 ;   税金配置：  01 ; 手续费配置：02
			 String inComeTax = "0";//所得税
			 if("01".equals(isConfig)) {
				 inComeTax = ds.getByID("4068", "423");
				 if(inComeTax==null || "".equals(inComeTax)||Double.valueOf(inComeTax)==0){
					 inComeTax="0";
					}
			 }
			
			ds = recharge(dpRequest,inComeTax);// 充值

			 resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			
			// 返回结果为失败时，获取结果描述
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}

			String transSeq = ds.getByID("4002", "401");
			String txanAmounts = ds.getByID("6303", "600");// 交易金额
			String balanceIpos = ds.getByID("6036", "600");// 可用余额
//			String reward = ds.getByID("6035", "600");// 总余额
			String reward = getRewardMoney(dpRequest.getCustCode());// 总余额

			// 单位转换：元转分
			String txanAmount = MathTool.yuanToPoint(txanAmounts);
			inComeTax = MathTool.yuanToPoint(inComeTax);
			balanceIpos = MathTool.yuanToPoint(balanceIpos);
//			reward = MathTool.yuanToPoint(reward);
			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc,
					dpRequest.getOrderNo(), dpRequest.getRemark1(), dpRequest.getRemark2(), transSeq, txanAmount, balanceIpos, reward, inComeTax);
			
			return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);
			
		} catch (XmlINFException spe) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);

			String oXml =ExceptionHandler.toXML(spe, infId);
			
			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		} catch (Exception e) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			String oXml =ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), infId);
			
			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		}
	}

	private static PackageDataSet callCUM1003(
			DpInf02012Request dpRequest) throws Exception {

		String staff = dpRequest.getStaffCode();

		String verityType = "0001"; // 支付密码

		String tmnNum = dpRequest.getTmnNum();

		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", staff);
		g200.put("2903", "2007");
		g200.put("2904", dpRequest.getPayPassword());
		g200.put("2172", "0001");
		g200.put("2173", verityType);
		// g200.put("2025", null);
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
	
	/**
	 * 调用SCS0001接口充值
	 * 
	 * @param dpRequest
	 * @param inComeTax 
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet recharge(DpInf02012Request dpRequest, String inComeTax) throws Exception {

		String areaCode = TCumInfoDaoTemp.queryAreacodeByCustCode(dpRequest.getCustCode());// 所属区域编码
		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 银行帐号
		String bankCode = acctDao.getBankCode(acctCode); // 银行编码[通过银行帐号查询]
		bankCode = "110000";
		// 一次路由
//		TransManage tm = new TransManage();
		// PackageDataSet dataSet2 = tm.firstRoute(dpRequest.getCustCode(), areaCode, dpRequest.getChannelCode(), "07010002", dpRequest.getMerId(),
		// dpRequest.getTmnNum(), dpRequest.getTxnAmount(), "PT1004", bankCode);

		// String newActionCode = dataSet2.getByID("4051", "423");
		String newActionCode = "01030008";
		// String newOrgCode = dataSet2.getByID("4098", "423");
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeTime = df.format(new Date());
		
		String sourceAmount = dpRequest.getTxnAmount();
		String resultAmount = "0";
		double temp = Double.parseDouble(sourceAmount);
		if(inComeTax!=null&&!inComeTax.equals("0")){
			temp = temp - Double.parseDouble(inComeTax);
			resultAmount = String.valueOf(temp);
		}else{
			resultAmount =sourceAmount;
		}
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4012", "佣金结转交易");// 订单描述
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		// g401.put("4017", dpRequest.getOrderNo());// 订单号？？？
		g401.put("4017", dpRequest.getKeep());// 订单号？？？
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		
		g401.put("4284", dpRequest.getMerId());//机构编码     //20130628 wanght
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", sourceAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", sourceAmount);// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", newActionCode);// 业务编码
		g404.put("4052", dpRequest.getCustCode());// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 当该值与不为空时，已该值作为actlist的系统参考号。否则由核心交易平台平台生成
		g404.put("4072", newActionCode);
		g404.endRow();
		
		if(!inComeTax.equals("0")){
		g404.put("4047", "2");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", "01050002");// 业务编码
		g404.put("4052", "");// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 当该值与不为空时，已该值作为actlist的系统参考号。否则由核心交易平台平台生成
		g404.put("4072", "");
		g404.endRow();
		}
		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", resultAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", resultAmount);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();
		
		if(!inComeTax.equals("0")){
		g405.put("4047", "2");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", inComeTax);
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", inComeTax);
		g405.put("4071", "103");// 费用项标识
		g405.endRow();
		}
		

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0300");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");
		g407.put("4088", "0007");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		if(!inComeTax.equals("0")){
		g407.put("4047", "2");// 业务单序号
		g407.put("4051", "01050002");// 业务编码
		g407.put("4087", "SCS_FAREACCTTYPE");
		g407.put("4088", "0007");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_MOBPHONE");
		g407.put("4088", "00000000000");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		}
		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", bankCode);// 支付机构编码
		g408.put("4099", "0110");// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", dpRequest.getPayPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", sourceAmount);// 支付金额
		g408.put("4127", bankCode);// 前置支付机构,查卡表的bankcode
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}
	/**
	 * 获取酬金账户余额
	 * @param dpRequest
	 * @param inComeTax
	 * @return
	 * @throws Exception
	 */
	private static String getRewardMoney(String agentCode) throws Exception {
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
		// 返回结果为失败时，抛出异常
		if (Long.valueOf(resultCode) != 0) {
			String resultMsg = (String) DataSet.getParamByID("0002", "000").get(0);
			throw new Exception(resultMsg);
		}

		// 返回响应码
		count = DataSet.getParamSetNum("604");
		if (count < 1) {
			throw new Exception("无账户信息");
		}
		for (int i = 0; i < count; i++) {
			String accttype = (String) DataSet.getParamByID("6421", "604").get(i);// 账户类型
			if("0110".equals(accttype)){//酬金账户
				String Monetaryunit = (String) DataSet.getParamByID("6404", "604").get(i); // 货币单位
				String activeBalance = (String) DataSet.getParamByID("6036", "604").get(i); // 账户可用余额
				if ("元".equals(Monetaryunit)) {
					activeBalance = MathTool.yuanToPoint(activeBalance);
				}
				return activeBalance;// 账户可用余额
			}
		}
		return "0";
		
	}
	
}
