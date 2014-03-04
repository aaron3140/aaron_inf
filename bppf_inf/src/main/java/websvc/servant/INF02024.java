package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import mpi.client.data.TransData;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import com.tisson.pay.config.BftProperties;
import com.tisson.pay.service.impl.BftOrderPayRequestimpl;
import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TPnmPartnerDao;
import common.dao.TRegBindCardDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.entity.TRegBindCard;
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
import common.xml.dp.DpInf02024Request;
import common.xml.dp.DpInf02024Response;

import framework.config.ConfigReader;
import framework.config.GlobalConstants;
import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 邱亚建 2013-9-24 上午11:35:12<br/>
 * 
 * 本类描述:账户绑卡验证接口
 */
public class INF02024 {

	public static String svcInfName = "INF02024";

	private static final Logger log = Logger.getLogger(INF02024.class);

	public static String executeForMD5(String in0, String in1) {

		DpInf02024Response resp = new DpInf02024Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf02024Request dpRequest = new DpInf02024Request(in1);

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

		DpInf02024Request dpRequest = null;
		DpInf02024Response resp = null;
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

			dpRequest = new DpInf02024Request(in1);

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
			
			PackageDataSet ds = null;
			String resultCode = "";
			 ds = callCUM0003(dpRequest);
			
			resultCode = (String) ds.getParamByID("0001", "000").get(0);
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}
			 //银行开户名与注册申请人是否一致
			ArrayList list2024 = ds.getParamByID("2024", "203");
			ArrayList list2025 = ds.getParamByID("2025", "203");
			if (list2024 != null && !list2024.isEmpty()) {
				for (int i = 0; i < list2024.size(); i++) {
					if ("CUM_ACTIONMAN".equals(list2024.get(i))) {
						if(!dpRequest.getTransAccName().trim().equals(list2025.get(i))){
							throw new INFException(INFErrorDef.REALLY_NAME_NOT_THE_SAME, INFErrorDef.REALLY_NAME_NOT_THE_SAME_DESC);
						}
						break;
					}
				}
			}
			//银行开户证件号与注册证件号是否一致
			String certNbr = TCumInfoDao.getCertNbrByStaffCode(dpRequest.getStaffCode());
			if (!StringUtils.equals(dpRequest.getCerNo(), certNbr)) {
				throw new INFException(INFErrorDef.CERNO_NOT_THE_SAME, INFErrorDef.CERNO_NOT_THE_SAME_DESC);
			}
			
			//银行卡是否为借记卡
			TCumAcctDao dao = new TCumAcctDao();
			Boolean isdebitCard = dao.isDebitCard(dpRequest.getBankAcct());
			if(!isdebitCard){
				throw new INFException(INFErrorDef.WRONG_BANK_ACCT, INFErrorDef.WRONG_BANK_ACCT_DESC);
			}
			
			TRegBindCardDao cardDao = new TRegBindCardDao();
			boolean flag = cardDao.isBinding(dpRequest.getCustCode());//是否处于绑卡中状态
			if (flag) {//绑卡中
				log.info("该企业账户处于绑卡中... custCode=["+dpRequest.getCustCode()+"]");
				throw new INFException(INFErrorDef.CUST_CODE_IS_BINDING, INFErrorDef.CUST_CODE_IS_BINDING_DESC);
			}
			
			// 判断是第一次绑卡还是修改绑卡信息
			 flag = cardDao.isFristBindCardOrChange(dpRequest.getCustCode(), dpRequest.getBankAcct());
			if (!flag) {// 修改
				throw new INFException(INFErrorDef.DUPLICATE_BANK_ACCT, INFErrorDef.DUPLICATE_BANK_ACCT_DESC);
			}

			ds = callSCS0012(dpRequest);
			 resultCode = (String) ds.getParamByID("0001", "000").get(0);
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}
			String orderNo = (String) ds.getParamByID("4002", "401").get(0);// 预处理订单号
			String forwardUrl = getForwardUrl(dpRequest,orderNo);// 重定向URL
			// 订单生成成功 记录到regbindcard表
			TRegBindCard card = new TRegBindCard();

			card.setStaffCode(dpRequest.getStaffCode());
			card.setCustCode(dpRequest.getCustCode());
			// card.setBindOrderNo(dpRequest.getOrderNo());
			card.setBindOrderNo(orderNo);
			card.setBindState("S0A");//生成
			card.setBankCode(dpRequest.getBankCode());
			card.setBankName(dpRequest.getOpenBank());// ?
			card.setBankOpen(dpRequest.getOpenBank());
			card.setAreaCode(dpRequest.getAreaCode());
			card.setBankAcct(dpRequest.getBankAcct());
			card.setTransAccName(dpRequest.getTransAccName());
			card.setCerNo(dpRequest.getCerNo());
			card.setOpenPhone(dpRequest.getPhone());
			card.setStat("S0A");
			card.setRemark(dpRequest.getRemark1() + "::" + dpRequest.getRemark2());
			cardDao.add(card);
			
			cardDao.updateOrderToS0X(dpRequest.getCustCode(),orderNo,"已生成新订单");

			resp = new DpInf02024Response();
			responseCode = resultCode;
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, forwardUrl, orderNo, dpRequest
					.getRemark1(), dpRequest.getRemark2());
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
	 * 重定向URL
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static String getForwardUrl(DpInf02024Request dpRequest,String orderNo) throws Exception {
		String url = "";

		Date dateNow = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = formatter.format(dateNow);

		// 发送"支付请求"数据包
		TransData transdata = new TransData();// 定义请求数据包

		transdata.setUserID(dpRequest.getPhone());// 开户手机号
		transdata.setUserName(dpRequest.getTransAccName());// 用户开户姓名
		transdata.setBankCard(dpRequest.getBankAcct());
		transdata.setBankPID(dpRequest.getCerNo());// 身份证号码
//		transdata.setPayID(dpRequest.getOrderNo());// 商户订单号
		transdata.setPayID(orderNo);// 商户订单号
		transdata.setOrderAmount(BftProperties.getOrderAmount());// 订单金额
		transdata.setTransDate(date.substring(0, 8));// 交易日期
		transdata.setTransTime(date.substring(8));// 交易时间
		transdata.setPageType("3");// 3=HTML5
		transdata.setTransType("3001");// 交易类型
		transdata.setBankCode(convertBankCodeToBFT(dpRequest.getBankCode()));
		transdata.setAccID(dpRequest.getCustCode());
		// 以下参数从配置文件中读取
		transdata.setMerID(BftProperties.getMerId());// 填帮付通分配商户编号
		transdata.setMerName(BftProperties.getMerName());// 商户简称
		transdata.setMerHomeUrl(BftProperties.getMerHomeUrl());// 用户支付完成后,返回商户页面地址
		transdata.setMerBackUrl(BftProperties.getMerBackUrl());// 用户取消支付,,返回商户页面地址

		BftOrderPayRequestimpl orderpay = new BftOrderPayRequestimpl();// 声明"支付请求报文"类对像
		url = orderpay.sendOrderPayRequest(transdata);// 调用"订单支付请求"方法,生成请求串
		log.info("BFT_URL 返回给客户端的url（支付地址）=" + url);
		return url;
	}

	/**
	 * 转换银行编码
	 * @param bankCode
	 * @return
	 * @throws Exception
	 */
	private static String convertBankCodeToBFT(String bankCode) throws Exception {
		Properties pro = ConfigReader.readConfig(GlobalConstants.BFT_BANK_CODE_CONF );
		Object object = pro.get(bankCode);
		if(object!=null)
			return object.toString();
		return "";
//		if("866200".equals(bankCode)){
//			return "ICBC";//ICBC 工商银行
//		}else if("866300".equals(bankCode)){
//			return "ABC";//ABC 农业银行
//		}else if("866100".equals(bankCode)){
//			return "BOC";//BOC 中国银行
//		}else if("866500".equals(bankCode)){
//			return "CCB";//CCB 建设银行
//		}else if("866900".equals(bankCode)){
//			return "CMB";//CMB 招商银行
//		}else if("866000".equals(bankCode)){
//			return "PSBC";//PSBC 邮政储蓄
//		}else if("867400".equals(bankCode)){
//			return "ZXB";//ZXB 中信银行
//		}else if("867200".equals(bankCode)){
//			return "CEB";//CEB 光大银行
//		} else if ("866600".equals(bankCode)){
//			return "CMBC";//CMBC 民生银行
//		}else if("865800".equals(bankCode)){
//			return "HXB";//HXB 华夏银行
//		}else if("866800".equals(bankCode)){
//			return "GFB";//GFB 广发银行
//		}else if("867100".equals(bankCode)){
//			return "SPDB";//SPDB 浦发银行
//		}else if("865700".equals(bankCode)){
//			return "PAB";//PAB 平安银行
//		}else if("865500".equals(bankCode)){
//			return "BOS";//BOS 上海银行
//		}else if("867600".equals(bankCode)){
//			return "CIB";//CIB 兴业银行
//		}
	}
	
	/**
	 * 获取入账账户类型，0001资金帐户，0007交费易帐号
	 * @param custCode
	 * @return
	 * @throws Exception
	 */
	private static String getAccountType(String custCode) throws Exception {
		String type = "0001";//资金帐户
		TPnmPartnerDao dao = new TPnmPartnerDao();
		String plineId = dao.getPlineIdByCusCode(custCode);
		if(Charset.isEmpty(plineId, true)){
			return null;
		}
		if(plineId.contains("104")){
			type = "0007";
		}
		log.info("产品线编码："+plineId+"  入账账户类型：" +type);
		return type;
	}
	/**
	 * 校验密码
	 * 
	 * @param dpRequest
	 * @param staffCode
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callCUM1003(DpInf02024Request dpRequest) throws Exception {

		 String verityType = "0001"; //支付密码
//		String verityType = "0002"; // 登录密码

		String tmnNum = dpRequest.getTmnNum();

		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", dpRequest.getStaffCode());
		g200.put("2903", "2007");
		g200.put("2904", dpRequest.getPayPassword());
		g200.put("2172", "0001");
		g200.put("2173", verityType);
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
	 * 客户信息查询
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callCUM0003(DpInf02024Request dpRequest) throws Exception {
		IParamGroup g0003_200 = new ParamGroupImpl("200"); // 包头
		g0003_200.put("2002", dpRequest.getCustCode());
		g0003_200.endRow();

		IParamGroup g0003_002 = new ParamGroupImpl("002"); // 包头
		g0003_002.put("0011", "203");
		g0003_002.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet DataSet = caller.call("BIS", "CUM0003", g0003_200, g0003_002);// 组成交易数据包,调用CUM0003接口

		return DataSet;
	}

	
	/**
	 * 生成订单
	 * 
	 * @param acctCode
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callSCS0012(DpInf02024Request dpRequest) throws Exception {
		String accountType = getAccountType(dpRequest.getCustCode());
		if(Charset.isEmpty(accountType, true)){
			throw new INFException(INFErrorDef.GET_ACCTTYPE_FAIL, INFErrorDef.GET_ACCTTYPE_FAIL_DESC);
		}
		String orderAmount = BftProperties.getOrderAmount();
		String actionCode = "01010007";// 业务编码
		TCumAcctDao dao = new TCumAcctDao();
		String acctCode = dao.getAcctCode(dpRequest.getCustCode());
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", dpRequest.getAreaCode());// 所属区域编码
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", DateFormatUtils.format(new Date(), "yyyyMMddhhmmss"));// 受理时间
		g401.put("4012", "");// 401-4012 -订单备注
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", "80");// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 终端流水号
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		g401.put("4028", dpRequest.getOrderNo());// 外部订单号
		g401.put("4284", dpRequest.getMerId());// 机构编码
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", orderAmount);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", orderAmount);// 订单应付金额
		g402.endRow();

		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");
		g405.put("4021", "0001");
		g405.put("4066", orderAmount);
		g405.put("4067", "0");
		g405.put("4068", orderAmount);
		g405.put("4071", "101");
		g405.endRow();
		// 业务单信息

		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", dpRequest.getCustCode());// 业务对象
		// g404.put("4052", dpRequest.getBankAcct());// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4906", "0");// 404-4906 -查询系统参考号
		g404.put("4910", "");// 404-4906 -查询系统参考号
		g404.endRow();

		// 接SCS0012新增407入参
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");
		g407.put("4051", actionCode);
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0100");
		g407.put("4080", "0");
		g407.endRow();
		g407.put("4047", "1");
		g407.put("4051", actionCode);
		g407.put("4087", "SCS_SRCPAYRETID");
		g407.put("4088", dpRequest.getOrderNo()); // 交易流水号
		g407.put("4080", "0");
		g407.endRow();
		g407.put("4047", "1");
		g407.put("4051", actionCode);
		g407.put("4087", "SCS_DSTCAACCODE");
		g407.put("4088", acctCode);
		g407.put("4080", "0");
		g407.endRow();
		g407.put("4047", "1");
		g407.put("4051", actionCode);
		g407.put("4087", "SCS_DSTACCTTYPE");
		g407.put("4088", accountType);
		g407.put("4080", "0");
		g407.endRow();

		// 银行帐号户名
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKNAME");// 业务属性编码
		g407.put("4088", dpRequest.getTransAccName());// 属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		// 银行编码
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKID");// 业务属性编码
		g407.put("4088", dpRequest.getBankCode());// 属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		// 对公对私标识
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDTYPE");// 业务属性编码
		g407.put("4088", "1");// 属性值1 默认对私
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");
		g407.put("4051", actionCode); // 增加手机号码
		g407.put("4087", "SCS_PHONENUM");
		String mobile = dpRequest.getPhone();
		if (mobile != null && !mobile.equals("")) {
			g407.put("4088", mobile);
			g407.put("4080", "0");
		} else {
			g407.put("4088", "");
			g407.put("4080", "1");
		}
		g407.endRow();

		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");
		g408.put("4097", "PT1006");
		g408.put("4098", "110003"); 
		g408.put("4099", accountType);
		g408.put("4102", "123456");
		g408.put("4101", dpRequest.getBankAcct());
		g408.put("4021", "0001");
		g408.put("4104", orderAmount); // 支付金额
		g408.put("4911", dpRequest.getOrderNo()); // 支付请求流水号
		g408.endRow();
 
		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0012", g401, g402, g404, g405, g407, g408);

		// 返回结果
		return dataSet;
	}

}
