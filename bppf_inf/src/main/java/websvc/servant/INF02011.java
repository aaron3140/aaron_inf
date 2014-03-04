package websvc.servant;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfConsumeDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TPhoneAreaDao;
import common.dao.TPnmPartnerDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfConsume;
import common.entity.TInfOperInLog;
import common.entity.VerifyConsumeEntity;
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
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.PasswordUtil;
import common.utils.PrivConstant;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.utils.WebSvcUtil;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02011Request;
import common.xml.dp.DpInf02011Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 邱亚建 2013-4-22 下午02:28:22<br>
 *         TODO 话费充值
 * 
 */
public class INF02011 {
	private static final Log logger = LogFactory.getLog(INF02011.class);

	public static String svcInfName = "INF02011";
	
	public static String executeForMD5(String in0, String in1) {

		DpInf02011Response resp = new DpInf02011Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf02011Request dpRequest = new DpInf02011Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {

				String tokenValidTime = TSymSysParamDao.getTokenValidTime();

				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest
						.getStaffCode(), tokenValidTime);

				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);

				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest
						.getStaffCode());

			}

			String oldXml = execute(in0, in1);

			return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);

		} catch (Exception e) {
			String oldXml = ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), null);

			return ResponseVerifyAdder.pkgForMD5(oldXml, null);
		}

	}

	@SuppressWarnings("unchecked")
	public static String execute(String in0, String in1) {
		DpInf02011Request dpRequest = null;

		DpInf02011Response resp = new DpInf02011Response();

		RespInfo respInfo = null;

		logger.info("INF02011请求参数：：" + in1);

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID infId = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		Long consumId = null;
		
		try {

			respInfo = new RespInfo(in1, "20");

			dpRequest = new DpInf02011Request(in1);
			
			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(), "", dpRequest.getTmnNum(), svcCode, "XML", "", "", "", "", OrderConstant.S0A);
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
			
			if (Double.valueOf(dpRequest.getTxnAmount())<1) {
				throw new Exception("金额不能少于1分钱");
			}
			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if (!(dpRequest.getTradeTime().matches(RegexTool.DATE_FORMAT))) {

				throw new Exception("输入日期格式不正确");
			}
			TransManage transManage = new TransManage();

			// IPOS处理
			if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())) {
				
				//密码鉴权
				PasswordUtil.AuthenticationPassWord1(dpRequest, dpRequest.getStaffCode(), dpRequest.getPayPassword(), dpRequest.getECardNo(), dpRequest.getPsamCardNo(), dpRequest.getPassFlag());
				
				Map<String, String> map = transManage
						.getCustCodeByExtTermNumNo(dpRequest.getTmnNumNo());
				if (map != null && map.size() != 0) {
					String custCode = map.get("CUST_CODE");
					String tmnNumNo = map.get("TERM_CODE");
					dpRequest.setCustCode(custCode);
					dpRequest.setTmnNum(tmnNumNo);
				} else {
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_DESC);
				}
			}else{
				
				//关联机构验证
				if(!TCumInfoDao.verifyMerIdCustCode(dpRequest.getCustCode(), dpRequest.getMerId())){
					
					if(TCumInfoDao.getMerIdByCustCode(dpRequest.getCustCode(),dpRequest.getMerId()))
						
						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
								INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
				}
			}
			
			
			//判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode());
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if(PrivConstant.IPOS_RECHARGE_TELFARE.equals(str)){
					r = true;
					break;
				}

			}
			
			if(!r){
				throw new Exception("你没有话费充值的权限");
			}

			PackageDataSet ds = null;
			
			String resultCode = "";

			String responseDesc = "";
			
			//--------------------------------------------------------------------------
			//获取溢价
			String phone = dpRequest.getPhone().substring(0, 7);
			String areaC = TPhoneAreaDao.getAreaByPhone(phone);
			// 产品编码
			String cp =dpRequest.getRechargeType()+areaC;
			String concession = scs0201(dpRequest,cp,areaC);
			
			DecimalFormat df = new DecimalFormat("#.00");
			String payAmount=df.format(Double.parseDouble(dpRequest.getTxnAmount())+Double.parseDouble(concession));
			//-----------------------------------------------------------------------------


			if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())) {

				Hashtable<String,Object> m = process2(dpRequest,payAmount,concession);

				resultCode = (String) m.get("ResultCode");

				responseDesc = (String) m.get("ResponseDesc");
				
				ds = (PackageDataSet)m.get("DateSet");

			} else {

				Hashtable<String,Object> m = process1(dpRequest,payAmount,concession);

				resultCode = (String) m.get("ResultCode");

				responseDesc = (String) m.get("ResponseDesc");
				
				consumId = (Long) m.get("ConsumId");
				
				ds = (PackageDataSet)m.get("DateSet");

			}

			String transSeq = ds.getByID("4002", "401");

			//过滤小数点
			payAmount = MathTool.pointToYuan(payAmount);
			payAmount = MathTool.yuanToPoint(payAmount);
			
			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", resultCode, responseDesc,
					dpRequest.getOrderNo(), dpRequest.getRemark1(), dpRequest.getRemark2(), transSeq, payAmount,payAmount);
			
			return oXml;
			
		} catch (XmlINFException spe) {

			spe.setRespInfo(respInfo);

			String oXml = ExceptionHandler.toXML(spe, infId);
			
			return oXml;

		} catch (Exception e) {

			String oXml = ExceptionHandler.toConsumeXML(new XmlINFException(resp, e, respInfo), infId,consumId);
			
			return oXml;

		}
	}

	
	private static Hashtable<String,Object> process2(DpInf02011Request dpRequest,String payAmount,String concession) throws Exception {

		PackageDataSet ds = null;

		String responseDesc = "";

		String resultCode = "";

		// 单位转换：分转元
		String reAmount = MathTool.pointToYuan(dpRequest.getRechargeAmount());
		
		String txAmount = MathTool.pointToYuan(dpRequest.getTxnAmount());
		
		dpRequest.setRechargeAmount(reAmount);
		
		dpRequest.setTxnAmount(txAmount);
		// 充值
		ds = recharge(dpRequest,payAmount,concession);

		resultCode = (String) ds.getParamByID("0001", "000").get(0);

		// 返回结果为失败时，获取结果描述
		if (Long.valueOf(resultCode) == 0) {

			responseDesc = (String) ds.getParamByID("0002", "000").get(0);
		}

		Hashtable<String,Object> map = new Hashtable<String,Object>();

		map.put("ResultCode", resultCode);
		
		map.put("ResponseDesc", responseDesc);
		
		map.put("DateSet", ds);

		return map;

	}
	
	private static Hashtable<String,Object> process1(DpInf02011Request dpRequest,String payAmount,String concession) throws Exception {
		
		
		Long consumId = null;
		// 业务组件
		SignBankManage manage = new SignBankManage();

		// 获取客户ID
		String custId = manage.getCustIdByCode(dpRequest.getCustCode());

		if (null == custId || "".equals(custId)) {
			throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
					INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
		}

		// 快捷交易验证
		VerifyConsumeEntity entity = new VerifyConsumeEntity(custId, dpRequest
				.getStaffCode(), payAmount, dpRequest
				.getPayPassword(), dpRequest.getChannelCode(), dpRequest
				.getTmnNum(), dpRequest.getIp());

		WebSvcUtil websvcutil = new WebSvcUtil();

		boolean flag = websvcutil.VerifyConsume(entity);

		TInfConsumeDao dao = new TInfConsumeDao();

		consumId = dao.getConsumeId();

		PackageDataSet ds = null;

		String responseDesc = "";

		String resultCode = "";

		// 调用核心接口之前将消费记录插入到消费表
		addConsume(dpRequest, payAmount,consumId, dao, custId, websvcutil.getSum_stat(),
				dpRequest.getRechargeType());
		// 单位转换：分转元
		String reAmount = MathTool.pointToYuan(dpRequest.getRechargeAmount());
		
		String txAmount = MathTool.pointToYuan(dpRequest.getTxnAmount());
		
		dpRequest.setRechargeAmount(reAmount);
		
		dpRequest.setTxnAmount(txAmount);
		// 充值
		ds = recharge(dpRequest,payAmount,concession);

		resultCode = (String) ds.getParamByID("0001", "000").get(0);

		// 返回结果为失败时，获取结果描述
		if (Long.valueOf(resultCode) == 0) {

			responseDesc = (String) ds.getParamByID("0002", "000").get(0);

			dao.updateOrderStat(consumId, OrderConstant.S0C);

			if (flag) {

				dao.updateSumStat(custId);
			}
		}

		Hashtable<String,Object> map = new Hashtable<String,Object>();

		map.put("ResultCode", resultCode);
		
		map.put("ResponseDesc", responseDesc);
		
		map.put("consumId", consumId);
		
		map.put("DateSet", ds);

		return map;

	}
	
	private static void addConsume(DpInf02011Request dpRequest,String payAmount, Long consumId,
			TInfConsumeDao dao, String custId, String stat, String actionCode) {

		TInfConsume c = new TInfConsume();
		
		c.setConsumeId(consumId);

		c.setCustId(Long.valueOf(custId));

		c.setOrderNo(dpRequest.getOrderNo());

		c.setAcctType("0007");

		c.setKeep(dpRequest.getKeep());

		c.setChannelType(dpRequest.getChannelCode());

		c.setTermId(dpRequest.getTmnNum());

		c.setActionCode(actionCode);

		c.setPdLineId(String.valueOf(dao.getPdlineId()));

		c.setAmount(payAmount);

		c.setStat(OrderConstant.S0A);

		c.setAcctDate(new Date());

		c.setRemark(dpRequest.getRemark1() + "::" + dpRequest.getRemark2());

		c.setSum_stat(stat);

		dao.insert(c);
	}
	
	/**
	 * 查询溢价
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static String scs0201(DpInf02011Request dpRequest,
			String prodCode,String areaCode) throws Exception {
		
		// 单位转换：分转元
		String amount = MathTool.pointToYuan(dpRequest.getTxnAmount());
		
		TPnmPartnerDao dao = new TPnmPartnerDao();
		Map<String, String> map = dao.getPrntnCodeAndPrntType(dpRequest
				.getCustCode());
		
		IParamGroup g423 = new ParamGroupImpl("423");
		g423.put("4230", "0003"); 	//溢价
		g423.put("2011", dpRequest.getMerId());	//接入机构的对应的商户编码
		g423.put("4330", map.get("PRTN_CODE"));	//实际做交易的商户编码
		g423.put("4331", map.get("PRTN_TYPE"));	//实际做交易的商户类型
		g423.put("2002", dpRequest.getCustCode());	//实际做交易的商户编码
		g423.put("4051", dpRequest.getRechargeType());	//业务编码
		g423.put("4049", prodCode);	//产品编码
		g423.put("4098", "110000");  //银行编码
		g423.put("4006", areaCode);  //区域编码
		g423.put("4144", dpRequest.getChannelCode60To20());  //渠道号
		g423.endRow();
		
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4025", amount);	//订单金额
		
		g402.put("4099", "0007");	//账户类型编码
		
		String concession ="0";

		try{
			// 组成数据包,调用SCS0201接口
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet dataSet = caller.call("SCS", "SCS0201", g423, g402);
			
			String resCode = dataSet.getByID("0001", "000");
			
			if (Long.valueOf(resCode) == 0) {
				
				String flagStr = dataSet.getByID("4230", "423");
				
				if ("0003".equals(flagStr)) {
					
					concession = dataSet.getByID("4329", "423");
					
					if (concession == null || "".equals(concession)
							|| Double.valueOf(concession) == 0) {
						
						concession = "0";
					}
				}
			}
		}catch(Exception e){
			
			logger.info("获取溢价  "+ e.getStackTrace());
		}
		
		// 返回结果  单位转换：元转分
		return MathTool.yuanToPoint(concession);

	}
	
	/**
	 * 调用SCS0001接口充值
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet recharge(DpInf02011Request dpRequest,String payAmount,String concession) throws Exception {

		TCumAcctDao acctDao = new TCumAcctDao();
		String acctCode = acctDao.getAcctCode(dpRequest.getCustCode());// 银行帐号
		String bankCode = acctDao.getBankCode(acctCode); // 银行编码[通过银行帐号查询]
		String rechargeType = dpRequest.getRechargeType();//业务编码
		
		//通过客户编码查区域编码
		String area_code = TCumInfoDao.getAreaCode(dpRequest.getCustCode());
		String provinceCode = TCumInfoDao.getProvinceCodeByPhoneNum(dpRequest.getPhone());//手机号对应的省份编码
		if(Charset.isEmpty(provinceCode, true)){//查不到，用企业账户的区域编码
			provinceCode = area_code;
		}
		
		bankCode = "110000";

		String newActionCode = rechargeType;
		
		// 单位转换：分转元
		payAmount = MathTool.pointToYuan(payAmount);
		concession = MathTool.pointToYuan(concession);

		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
//		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4006", area_code);// 所属区域编码  杭州330100
		g401.put("4007", dpRequest.getTmnNum());// 受理终端号
		g401.put("4008", dpRequest.getTradeTime());// 受理时间
		g401.put("4012", convertCodeToChar(dpRequest.getRechargeType(),dpRequest.getPayType()));// 订单描述
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode60To20());// 渠道类型编码
		// g401.put("4017", dpRequest.getOrderNo());// 订单号？？？
		g401.put("4017", dpRequest.getKeep());// 订单号？？？
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		g401.put("4028", dpRequest.getOrderNo());
		
		g401.put("4284", dpRequest.getMerId());//机构编码     //20130628 wanght
		g401.endRow();
		
		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", dpRequest.getTxnAmount());// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", payAmount);// 订单应付金额
		g402.put("4030", concession);// 溢价
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", getProductCode(rechargeType));// 产品编码
		g404.put("4051", newActionCode);// 业务编码
		g404.put("4052", dpRequest.getPhone());// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 当该值与不为空时，已该值作为actlist的系统参考号。否则由核心交易平台平台生成
		g404.put("4072", rechargeType);
		g404.endRow();
		
		

		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", dpRequest.getTxnAmount());// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", dpRequest.getTxnAmount());// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0200");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_USERTYPE");
		g407.put("4088", "1");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

        g407.put("4047","1");						//业务单序号
        g407.put("4051",newActionCode);			//业务编码
        g407.put("4087","SCS_DISCID");			//产品属性编码
        g407.put("4088",newActionCode + provinceCode);		//附加项数据类型
        g407.put("4089","");
        g407.put("4091","");
        g407.put("4093","");
        g407.put("4080","0");						//控制标识
        g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_SERVID");
		g407.put("4088", get4088Param(rechargeType));
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		
		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		if("9".equals(dpRequest.getPayType())){
			g408.put("4097", "PT2001");// 支付方式编码
		}else{
			g408.put("4097", "PT0004");// 支付方式编码
		}
		g408.put("4127", bankCode);// 前置支付机构,查卡表的bankcode
		g408.put("4098", bankCode);// 支付机构编码
		g408.put("4099", "0007");// 账户类型编码
		g408.put("4101", acctCode);// 账号
		g408.put("4102", dpRequest.getPayPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", payAmount);// 支付金额
		g408.put("4109", "0003");// 国际网络号
		g408.put("4119", "");
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}

	/**
	 * 获取SCS_SERVID  属性对应的属性值
	 * @param rechargeType
	 * @return
	 */
	private static String get4088Param(String rechargeType) {
		if ("03010008".equals(rechargeType)) {
			return "GMC405";
		}
		if ("05010005".equals(rechargeType)) {
			return "GMC403";
		}
		if ("04010003".equals(rechargeType)) {
			return "GMC404";
		}
		return "";
	}

	/**
	 * 获取产品编码
	 * @param rechargeType
	 * @return
	 */
	private  static String getProductCode(String rechargeType) {
		if ("03010008".equals(rechargeType)) {//电信
			return "0003";
		}
		if ("05010005".equals(rechargeType)) {//移动
			return "0005";  
		}
		if ("04010003".equals(rechargeType)) {//联通
			return "0004";
		}
		return "";
	}

	/**
	 * 获取充值业务类型说明
	 * 
	 * @param rechargeType
	 * @return
	 */
	private static String convertCodeToChar(String rechargeType, String PayType) {
		if ("03010008".equals(rechargeType)&&PayType.equals("9")) {
			return "现金支付-全国电信直充";
		}else if("03010008".equals(rechargeType)&&PayType.equals("0")){
			return "全国电信直充";
		}else if ("05010005".equals(rechargeType)&&PayType.equals("9")) {
			return "现金支付-全国移动直充";
		}else if ("05010005".equals(rechargeType)&&PayType.equals("0")) {
			return "全国移动直充";
		}else if ("04010003".equals(rechargeType)&&PayType.equals("9")) {
			return "现金支付-全国联通直充";
		}else if ("04010003".equals(rechargeType)&&PayType.equals("0")) {
			return "全国联通直充";
		}else{
			return "";
		}
		
	}
}
