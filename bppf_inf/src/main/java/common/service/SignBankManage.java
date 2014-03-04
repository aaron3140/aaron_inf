package common.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import websvc.servant.INF05002;

import common.algorithm.MD5;
import common.dao.BaseDao;
import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.entity.BankAcctInfo;
import common.entity.SignOrder;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.utils.Charset;
import common.utils.SpringContextHelper;
import common.xml.CommonReqAbs;
import common.xml.dp.DpAccountManagementRequest;
import common.xml.dp.DpInf02032Request;
import common.xml.dp.DpInf5002TradeRequest;

/**
 * 授权银行卡管理
 * File                 : SignBankManage.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : bppf
 * JDK version used     : JDK 1.6
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2012-3-29 上午12:46:44 [created]
 * Author               : Zhilong Luo 罗志龙
 * Email                : luozhilong@tisson.cn
 **/
public class SignBankManage {
	
	// 授权银行卡充值
	public static final String AC_AUTH_BANKCARD_RECHARGE = "01010001";
	
	// 授权银行卡提现
	public static final String AC_AUTH_BANKCARD_WITHD = "01020003";
	
	private BaseDao DAO = SpringContextHelper.getBaseDaoBean();
	
	
	public boolean verifyPayPwd(String staffId,String pwd) throws Exception{
		String pwdFromDb = getPwdFromDb(staffId);
		if (Charset.isEmpty(pwd)) {
			throw new Exception("支付密码为空值");
		}
		return pwd.equals(pwdFromDb);
	}
	
	/**
	 * 从数据获取用户名对应的密码密文(三次MD5加密)
	 * @param staffId
	 * @return
	 */
	private String getPwdFromDb(String staffId) {
		String sql = "select attr_value from T_Sym_StaffAttr where staff_id=?  and attr_type= 'SA0100'";
		return (String) DAO.queryForObject(sql, new Object[]{staffId}, String.class);
	}

	/**
	 * 调用CUM0002接口,查询客户详细信息
	 * @version: 1.00
	 * @history: 2012-3-27 下午05:45:21 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param custCode
	 * @return
	 * @throws Exception
	 * @see
	 */
	public PackageDataSet getCustInfo(String custCode) throws Exception {
		/**
		 * 调用CUM0002,根据客户信息查询结果
		 */
		// 查询明细信息
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "2002");// 查询条件：客户编码
		g002.put("0022", custCode);// 查询条件值
		
		// 组成数据包,调用CUM0002接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet packageDataSet = caller.call("BIS", "CUM0002", g002);
		
		// 返回查询结果
		return packageDataSet;
		
	}
	
	
	/**
	 * 获取指定客户的授权银行卡列表
	 * @version: 1.00
	 * @history: 2012-3-29 上午12:48:02 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param custId
	 * @return
	 * @throws Exception
	 * @see
	 */
	public List<BankAcctInfo> getBankAcctList(String custId) throws Exception {
		return getBankAcctList(custId, null);
	}
	
	/**
	 * 获取指定客户和指定账户ID的授权银行卡信息
	 * @version: 1.00
	 * @history: 2012-3-29 上午12:48:49 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param custId
	 * @param acctId
	 * @return
	 * @throws Exception
	 * @see
	 *//*
	public List<BankAcctInfo> getBankAcctInfo(String custCode) throws Exception {
		// 根据客户编码，调用CUM0003查询联系信息
		IParamGroup g0003_200 = new ParamGroupImpl("200"); // 包头
		g0003_200.put("2002",custCode);
		g0003_200.endRow();
		

		IParamGroup g0003_002 = new ParamGroupImpl("002"); // 包头
		g0003_002.put("0011","201");
		g0003_002.endRow();
		g0003_002.put("0011","202");
		g0003_002.endRow();
		g0003_002.put("0011","203");
		g0003_002.endRow();
		g0003_002.put("0011","204");
		g0003_002.endRow();
		g0003_002.put("0011","206");
		g0003_002.endRow();
		g0003_002.put("0011","207");
		g0003_002.endRow();
		g0003_002.put("0011","213");
		g0003_002.endRow();
		
		IServiceCall caller2 = new ServiceCallImpl();
		PackageDataSet DataSet = caller2.call("BIS","CUM0003", g0003_200,g0003_002);// 组成交易数据包,调用CUM0003接口
		
		// 获取201组
		String cer_id= DataSet.getByID("2009","201");
		String cer_num=DataSet.getByID("2010","201");
		custCode = DataSet.getByID("2002","201");
		
		// 获取207组卡户号
		int count = DataSet.getParamSetNum("207");
		String bankAcctNbr = "";
		String bankName = "";
		String bankAcctName = "";
		String bankfilialeName = "";
		String bankCode = "";
		String card_id = "";
		String cardType = ""; //对公对私标识
		String signContractId="";
		String addr = "";
		String mobile = "";
		for (int i = 0; i < count; i++) {
			// 获取卡户类型
			String cardAcctType=(String)DataSet.getParamByID("2048","207").get(i);
			// 获取银行卡户号
			if("ACCT001".equals(cardAcctType)){
				bankAcctNbr=(String)DataSet.getParamByID("2049","207").get(i);
				bankName=(String)DataSet.getParamByID("C050","207").get(i);
				bankAcctName=(String)DataSet.getParamByID("2051","207").get(i);
				bankCode=(String)DataSet.getParamByID("2050","207").get(i);
				card_id=(String)DataSet.getParamByID("C052","207").get(i);
				cardType=(String)DataSet.getParamByID("2052","207").get(i);
			}
		}
		// 获取202组客户联系信息
		int count2 = DataSet.getParamSetNum("202");
		for (int i = 0; i < count2; i++) {
			// 联系类型编码
			String contactType=(String)DataSet.getParamByID("2016","202").get(i);
			// 获取地址
			if("ADDR".equals(contactType)){
				addr=(String)DataSet.getParamByID("2018","202").get(i);
			}
			// 获取手机,固话,小灵通
			if("MOB".equals(contactType) || "PSTN".equals(contactType) || "PHS".equals(contactType)){
				mobile=(String)DataSet.getParamByID("2018","202").get(i);
			}
		}
		// 获取216组客户扩展信息
		int count2 = DataSet.getParamSetNum("216");
		for (int i = 0; i < count2; i++) {
			// 联系类型编码
			String attrType=(String)DataSet.getParamByID("2069","216").get(i);
			// 获取银行
			if("1012".equals(attrType)){
				bankName=(String)DataSet.getParamByID("2071","216").get(i);
			}
			// 获取签约id
			if("2810".equals(attrType)){
				signContractId=(String)DataSet.getParamByID("2071","216").get(i);
			}
			// 开户联系电话
			if("2814".equals(attrType)){
				mobile=(String)DataSet.getParamByID("2071","216").get(i);
			}
			// 卡折标识
			if("8035".equals(attrType)){
				card_id=(String)DataSet.getParamByID("2071","216").get(i);
			}
			// 证件类型
			if("154".equals(attrType)){
				cer_id=(String)DataSet.getParamByID("2071","216").get(i);
			}
			// 证件类型
			if("154".equals(attrType)){
				cer_id=(String)DataSet.getParamByID("2071","216").get(i);
			}
			// 证件号
			if("8037".equals(attrType)){
				cer_num=(String)DataSet.getParamByID("2071","216").get(i);
			}
//			// 账户归属地
//			if("8036".equals(attrType)){
//				(String)DataSet.getParamByID("2071","216").get(i);
//			}
			// 联系地址
			if("8038".equals(attrType)){
				addr=(String)DataSet.getParamByID("2071","202").get(i);
			}
		}
		
		
		BankAcctInfo info = new BankAcctInfo();	
		info.setBankAcctName(bankAcctName);
		info.setBankAcctNbr(bankAcctNbr);
		info.setBankCode(bankCode);
		info.setBankCardId(card_id);
		info.setCardType(cardType);
		info.setBankfilialeName(bankfilialeName);
		info.setCertCode(cer_num);
		info.setCertType(cer_id);
		info.setSignContractId(signContractId);
		List<BankAcctInfo> bankAcctList = new ArrayList<BankAcctInfo>(0);
		bankAcctList.add(info);
		return bankAcctList;
	}*/
	
	
	/**
	 * 获取指定客户和指定账户ID的授权银行卡列表
	 * @version: 1.00
	 * @history: 2012-3-29 上午12:48:49 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param custId
	 * @param acctId
	 * @return
	 * @throws Exception
	 * @see
	 */
	public List<BankAcctInfo> getBankAcctList(String custId, String acctId) throws Exception {
		
		List<BankAcctInfo> bankAcctList = new ArrayList<BankAcctInfo>(0);
		
		// 查询SQL
		StringBuffer sql = new StringBuffer();
		sql.append("select acct.acct_id, acct.acct_type, acct.acct_code, acct.bank_id, bank.bank_code, acct.acct_name, acct.card_type from t_cum_acct acct,t_sym_bank bank");
		sql.append(" where acct.stat='S0A' and acct.bank_id = bank.bank_id");
		sql.append(" and acct.cust_id='" + custId + "'");
		// 是否有指定银行账户ID
		if( ! Charset.isEmpty(acctId)) {
			sql.append(" and acct_id='" + acctId + "'");
		}
		
		// 获取客户帐号列表
		List<Map<String, Object>> cumAccts = DAO.queryForList(sql.toString());
		
		for(Map<String, Object> cumAcct : cumAccts) {
			// 账户类型
			String acctType = (String) cumAcct.get("ACCT_TYPE");
			// 判断是否是银行账户
			if(acctType.equals("ACCT001")) {
				BankAcctInfo info = new BankAcctInfo();							
				String bankAcctId = String.valueOf((BigDecimal) cumAcct.get("ACCT_ID"));		// 银行账户ID
				String bankAcctNbr = (String) cumAcct.get("ACCT_CODE");	 						// 银行卡号
				String bankId = String.valueOf((BigDecimal) cumAcct.get("BANK_ID"));			// 银行ID
				String bankCode = (String)cumAcct.get("BANK_CODE");			// 银行code
				String bankAcctName = (String) cumAcct.get("ACCT_NAME");						// 开户名
				String cardType = (String) cumAcct.get("CARD_TYPE");						// 卡折标识
				
				info.setBankAcctId(bankAcctId);
				info.setBankAcctNbr(bankAcctNbr);
				info.setBankId(bankId);
				info.setBankCode(bankCode);
				info.setBankAcctName(bankAcctName);
				info.setCardType(cardType);
				
				// 设置银行账户扩展属性
				info = getAcctAttr(info);
				// 添加到列表
				bankAcctList.add(info);
			}
		}
		// 返回结果
		return bankAcctList;
	}

	/**
	 * 设置银行账户扩展属性
	 * @version: 1.00
	 * @history: 2012-3-29 上午12:54:05 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param info
	 * @return
	 * @see
	 */
	private BankAcctInfo getAcctAttr(BankAcctInfo info) {
		String acctId = info.getBankAcctId();
		
		// 查询SQL
		StringBuffer sql = new StringBuffer();
		sql.append("select attr.attr_id, attr.value1 from t_cum_acct_attr attr where attr.stat='S0A'");
		sql.append(" and attr.acct_id='" + acctId + "'");
		
		// 获取帐号扩展属性列表
		List<Map<String, Object>> acctAttrs = DAO.queryForList(sql.toString());
		for(Map<String, Object> acctAttr : acctAttrs) {
			String attrId = String.valueOf((BigDecimal) acctAttr.get("ATTR_ID"));	// 扩展属性ID
			String value = (String) acctAttr.get("VALUE1");							// 扩展属性值
			
			// 是否是银行所在区域
			if(attrId.equals("2568")) {
				info.setAreaCode(value);
				continue;
			}
			// 是否是签约ID
			if(attrId.equals("2810")) {
				info.setSignContractId(value);
				continue;
			}
			// 是否是证件类型
			if(attrId.equals("2566")) {
				info.setCertType(value);
				continue;
			}
			// 是否是证件号码
			if(attrId.equals("2567")) {
				info.setCertCode(value);
				continue;
			}
			// 是否是银行开户行
			if(attrId.equals("2569")) {
				info.setBankfilialeName(value);
				continue;
			}
			// 是否是对公对私
			if(attrId.equals("2570")) {
				info.setBankCardId(value);
				continue;
			}
			/*// 是否是卡折标识
			if(attrId.equals("8035")) {
				info.setBankCardId(value);
				continue;
			}*/
		}
		// 返回
		return info;
	}
	private BankAcctInfo getBankInfo(BankAcctInfo info) {
		return info;
	}
	/**
	 * 充值/提现确认
	 * @version: 1.00
	 * @history: 2012-3-29 上午12:55:31 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param custCode
	 * @param order
	 * @throws Exception
	 * @see
	 */
	public PackageDataSet affirm(String custCode, SignOrder order,CommonReqAbs request) throws Exception {
		// 根据客户编码获得该客户天讯卡户号
		String cardAcctNbr = getTissonCardAcct(custCode);
		if(cardAcctNbr == null) {
			throw new Exception("获取卡户号失败");
		}
		// 调用SCS0001,完成充值/提现
		String actionCode = order.getActionCode();
		// 根据交易类型做分发操作
		if(actionCode.equals(AC_AUTH_BANKCARD_RECHARGE)) {
			// 调用充值
			return recharge(order, custCode, cardAcctNbr,request);
		} else if(actionCode.equals(AC_AUTH_BANKCARD_WITHD)) {
			// 调用提现
			return withdrawal(order, custCode, cardAcctNbr,request);
		} else {
			throw new Exception("非合法的交易类型");
		}
	}
	
	/**
	 * 根据客户编码查找客户ID
	 * @version: 1.00
	 * @history: 2012-3-29 下午04:05:28 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param custCode
	 * @return
	 * @see
	 */
	public String getCustIdByCode(String custCode) {
		
		String sql = "select info.cust_id from t_cum_info info where info.cust_code='" + custCode + "'";
		String custId = (String) DAO.queryForObject(sql, java.lang.String.class);
		
		return custId;
	}
	
	/**
	 * 根据客户编码获得该客户天讯卡户号
	 * @version: 1.00
	 * @history: 2012-2-23 下午05:23:51 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param agentCode
	 * @return
	 * @throws Exception
	 * @see
	 */
	private String getTissonCardAcct(String agentCode) throws Exception {
		/**
		 * 调用CUM0002,根据客户编码获得该客户天讯卡户号
		 */
		// 查询明细信息
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "2002");// 查询条件：客户编码
		g002.put("0022", agentCode);// 查询条件值
		
		// 组成数据包,调用CUM0002接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet packageDataSet = caller.call("BIS", "CUM0002", g002);
		
		// 获取返回值
		int count = packageDataSet.getParamSetNum("207");
		String cardAcctNbr = null;
		for(int i=0; i<count; i++) {
			// 获取卡户类型
			String cardAcctType = (String) packageDataSet.getParamByID("2048", "207").get(i);
			// 获取天讯卡户号
			if(cardAcctType.equals("ACCT002")) {
				cardAcctNbr = (String) packageDataSet.getParamByID("2049", "207").get(i);
				break;
			}
		}
		return cardAcctNbr;
	}
	
	/**
	 * 调用SCS0001,完成提现操作
	 * @version: 1.00
	 * @history: 2012-3-29 上午12:56:58 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param order
	 * @param custCode
	 * @param cardAcctNbr
	 * @return
	 * @throws Exception
	 * @see
	 */
//	private PackageDataSet withdrawal2(SignOrder order, String custCode, String cardAcctNbr,CommonReqAbs request) throws Exception {
//		String agentCode = custCode;											//商户编码
//		String channelCode = order.getChannelCode();							//交易渠道
//		String merId = order.getMerId();										
//		String tmnNum = order.getTmnNum();										//终端号
//		String areaCode = order.getBankAcctInfo().getAreaCode();				//区域编码
//		String actionCode = order.getActionCode();								//操作编码
//		String txnAmount = order.getAmount();									//交易金额
//		String transSeq = order.getTransSeq();									//订单号
//		String keep = order.getKeep();											//keep
//		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//		String tradeTime = df.format(new Date());								//交易时间
//		
//		String bankAcctNbr = order.getBankAcctInfo().getBankAcctNbr();			//银行卡号
//		String signContractId = order.getBankAcctInfo().getSignContractId();	//签约ID
//		String bankAcctName = order.getBankAcctInfo().getBankAcctName();		//开户名
//		String bankId = order.getBankAcctInfo().getBankCode();				//银行ID
//		String cardType = order.getBankAcctInfo().getCardType();			//卡折标识
//		String certCode = order.getBankAcctInfo().getCertCode();
//		String certType = order.getBankAcctInfo().getCertType();
//		String bankfilialeName = order.getBankAcctInfo().getBankfilialeName();
//		String bankCardId = order.getBankAcctInfo().getBankCardId();		//对公对私
//		String concessionType = order.getConcessionType(); 						//优惠方案方向
//		String concession = order.getConcession(); 								//优惠金额
//		String finalAmonut = order.getFinalAmount();							//最终订单金额
//		String peFlag = TSymSysParamDao.getPESwitch();
////		peFlag = "PT1004";
//		if (Charset.isEmpty(cardType)) {
//			cardType = "1";
//		}
//		if (Charset.isEmpty(bankCardId)) {
//			bankCardId = "1";
//		}
//		/**
//		 * 调用SCS0001,完成交易操作
//		 */
//		// 订单受理信息
//		IParamGroup g401 = new ParamGroupImpl("401");
//		g401.put("4004", agentCode);// 客户编码
//		g401.put("4005", "OT001");// 订单类型编码：业务类订单
//		g401.put("4006", areaCode);// 所属区域编码
//		g401.put("4007", tmnNum);// 受理终端号
//		g401.put("4008", tradeTime);// 受理时间
//		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
//		g401.put("4144", channelCode);// 渠道类型编码
//		/* 外部订单号改成4028
//		  g401.put("4017", orderSeq);// 终端流水号
//		  	keep值放4017
//		 */
//		g401.put("4017", keep);// 终端流水号
//		g401.put("4028", transSeq);// 订单号
//		g401.put("4018", "0.0.0.0");// 操作原始来源
//		g401.put("4012", "提现");// 订单备注
//		
//		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
//		g401.endRow();
//		
//		// 订单费用信息
//		IParamGroup g402 = new ParamGroupImpl("402");
//		g402.put("4021", "0001");// 币种编码
//		g402.put("4023", finalAmonut);// 订单原始金额
//		g402.put("4024", "0");// 订单优惠金额
//		g402.put("4025", finalAmonut);// 订单应付金额
//		g402.endRow();
//		
//		// 业务单信息
//		IParamGroup g404 = new ParamGroupImpl("404");
//		g404.put("4047", "1");// 业务单序号
//		g404.put("4049", "0001");// 产品编码
//		g404.put("4051", actionCode);// 业务编码
//		g404.put("4052", bankAcctName);// 业务对象
//		g404.put("4053", "1");// 业务数量
//		g404.put("4062", "");// 业务系统参考号
//		g404.put("4064", "");// 业务单所属客户编码
//		g404.endRow();
//		
//		//业务单手续费
//		if (concessionType != null && concession != null) {
//			g404.put("4047", "2");// 业务单序号
//			g404.put("4049", "0001");// 产品编码
//			g404.put("4051", "01050001");// 业务编码
//			g404.put("4052", "系统账户");// 业务对象
//			g404.put("4053", "1");// 业务数量
//			g404.put("4062", "");// 业务系统参考号
//			g404.put("4064", "");// 业务单所属客户编码
//			g404.endRow();
//		}
//		
//		// 业务单费用信息
//		IParamGroup g405 = new ParamGroupImpl("405");
//		g405.put("4047", "1");// 业务单序号
//		g405.put("4021", "0001");// 币种编码
//		g405.put("4066", txnAmount);// 业务单原始金额
//		g405.put("4067", "0");// 业务单优惠金额
//		g405.put("4068", txnAmount);// 业务单应付金额
//		g405.put("4071", "103");// 费用项标识
//		g405.endRow();
//		
//		// 业务单费用手续费信息
//		if (concessionType != null && concession != null) {
//			g405.put("4047", "2");// 业务单序号
//			g405.put("4021", "0001");// 币种编码
//			g405.put("4066", concession);// 业务单原始金额(手续费)
//			g405.put("4067", "0");// 业务单优惠金额
//			g405.put("4068", concession);// 业务单应付金额(手续费)
//			g405.put("4071", "103");// 费用项标识
//			g405.endRow();
//		}
//		IParamGroup g407 = new ParamGroupImpl("407");
//		// 支付单信息
//		IParamGroup g408 = new ParamGroupImpl("408");
//		
//		if ("PT1004".equals(peFlag)) {
//			String newOrgCode ="";
//			TransManage tm = new TransManage();
//			PackageDataSet dataSet2 = tm.firstRoute(custCode, areaCode, channelCode, actionCode, merId, tmnNum, txnAmount, "PT1004", bankId);
//			String respCode = dataSet2.getByID("0001", "000");
//			if (Long.valueOf(respCode) == 0) {
//				actionCode = dataSet2.getByID("4051","423");
//				newOrgCode = dataSet2.getByID("4098","423");
//			}
//			// 业务属性信息
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_TRADETYPE");// 业务属性编码
//			g407.put("4088", "020011");//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			///////////////////////////////////////////////////////////////////
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_CERTID");// 业务属性编码
//			g407.put("4088", certType);//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_CERTCODE");// 业务属性编码
//			g407.put("4088", certCode);//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_BANKBELONG");// 业务属性编码
//			g407.put("4088", areaCode);//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_BANKID");// 业务属性编码
//			g407.put("4088", bankId);// 属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_BANKCARDID");// 业务属性编码
//			g407.put("4088", cardType);// 属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_BANKCARDTYPE");// 业务属性编码
//			g407.put("4088", bankCardId);// 属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
//			g407.put("4088", "0200");//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
//			g407.put("4088", bankAcctNbr);// 属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_PAYTYPE");// 业务属性编码
//			g407.put("4088", "PT1004");//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_PAYORG");// 业务属性编码
//			g407.put("4088", newOrgCode);//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			//提现
//			g408.put("4103", "1");// 扣款顺序号
//			g408.put("4097", "PT0004");// 支付方式编码
//			g408.put("4098", "110000");// 支付机构编码
//			g408.put("4099", "0001");// 账户类型编码
//			g408.put("4101", cardAcctNbr);// 账号
//			g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
//			g408.put("4021", "0001");// 币种编码
//			g408.put("4104", finalAmonut);// 支付金额
//			g408.endRow();
//		}else{
//			// 业务属性信息
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
//			g407.put("4088", "0200");//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_TRADETYPE");// 业务属性编码
//			g407.put("4088", "1002");//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_SIGNID");// 业务属性编码
//			g407.put("4088", signContractId);//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_BANKID");// 业务属性编码
//			g407.put("4088", bankId);// 属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_BANKCODE");// 业务属性编码
//			g407.put("4088", bankAcctNbr);// 属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g408.put("4103", "1");// 扣款顺序号
//			g408.put("4097", "PT0004");// 支付方式编码
//			g408.put("4098", "110000");// 支付机构编码
//			g408.put("4099", "0001");// 账户类型编码
//			g408.put("4101", cardAcctNbr);// 账号
//			g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
//			g408.put("4021", "0001");// 币种编码
//			g408.put("4104", finalAmonut);// 支付金额
//			g408.endRow();
//		}
//		
//		
//		
//		// 组成数据包,调用SCS0001接口
//		IServiceCall caller = new ServiceCallImpl();
//		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
//		
//		// 返回结果
//		return dataSet;
//	}
	
	/**
	 * 调用SCS0001,完成充值操作
	 * @version: 1.00
	 * @history: 2012-3-29 上午01:02:05 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param order
	 * @param custCode
	 * @param cardAcctNbr
	 * @return
	 * @throws Exception
	 * @see
	 */
//	private PackageDataSet recharge2(SignOrder order, String custCode, String cardAcctNbr) throws Exception {
//		String agentCode = custCode;											//商户编码
//		String channelCode = order.getChannelCode();							//交易渠道
//		String tmnNum = order.getTmnNum();										//终端号
//		String merId = order.getMerId();										
//		String areaCode = order.getBankAcctInfo().getAreaCode();				//区域编码
//		String actionCode = order.getActionCode();								//操作编码
//		String txnAmount = order.getAmount();									//交易金额
//		String transSeq = order.getTransSeq();									//订单号号
//		String keep = order.getKeep();											//keep
//		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//		String tradeTime = df.format(new Date());								//交易时间
//		
//		String bankAcctNbr = order.getBankAcctInfo().getBankAcctNbr();
//		String bankAcctName = order.getBankAcctInfo().getBankAcctName();		//开户名
//		String bankId = order.getBankAcctInfo().getBankCode();					//银行ID
//		String signContractId = order.getBankAcctInfo().getSignContractId();	//签约ID
//		String cardType = order.getBankAcctInfo().getCardType();				//卡折标识
//		String certCode = order.getBankAcctInfo().getCertCode();
//		String certType = order.getBankAcctInfo().getCertType();
//		String bankfilialeName = order.getBankAcctInfo().getBankfilialeName();
//		String bankCardId = order.getBankAcctInfo().getBankCardId();			//对公对私
//		String concessionType = order.getConcessionType(); 						//优惠方案方向
//		String concession = order.getConcession(); 								//优惠金额
//		String finalAmonut = order.getFinalAmount();							//最终订单金额
//		String peFlag = TSymSysParamDao.getPESwitch();
//		
//		if (Charset.isEmpty(cardType)) {
//			cardType = "1";
//		}
//		if (Charset.isEmpty(bankCardId)) {
//			bankCardId = "1";
//		}
//		
//		/**
//		 * 调用SCS0001,完成交易操作
//		 */
//		// 订单受理信息
//		IParamGroup g401 = new ParamGroupImpl("401");
//		g401.put("4004", agentCode);// 客户编码
//		g401.put("4005", "OT001");// 订单类型编码：业务类订单
//		g401.put("4006", areaCode);// 所属区域编码
//		g401.put("4007", tmnNum);// 受理终端号
//		g401.put("4008", tradeTime);// 受理时间
//		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
//		g401.put("4144", channelCode);// 渠道类型编码
//		/* 外部订单号改成4028
//		  g401.put("4017", orderSeq);// 终端流水号
//		  	keep值放4017
//		 */
//		g401.put("4017", keep);// 终端流水号
//		g401.put("4028", transSeq);// 订单号
//		g401.put("4018", "0.0.0.0");// 操作原始来源
//		g401.put("4012", "");
//		g401.endRow();
//		
//		// 订单费用信息
//		IParamGroup g402 = new ParamGroupImpl("402");
//		g402.put("4021", "0001");// 币种编码
//		g402.put("4023", finalAmonut);// 订单原始金额
//		g402.put("4024", "0");// 订单优惠金额
//		g402.put("4025", finalAmonut);// 订单应付金额
//		g402.endRow();
//		
//		// 业务单信息
//		IParamGroup g404 = new ParamGroupImpl("404");
//		g404.put("4047", "1");// 业务单序号
//		g404.put("4049", "0001");// 产品编码
//		g404.put("4051", actionCode);// 业务编码
//		g404.put("4052", agentCode);// 业务对象
//		g404.put("4053", "1");// 业务数量
//		g404.put("4062", "");// 业务系统参考号
//		g404.put("4064", "");// 业务单所属客户编码
//		g404.endRow();
//		
//		//业务单手续费
//		if (concessionType != null && concession != null) {
//			g404.put("4047", "2");// 业务单序号
//			g404.put("4049", "0001");// 产品编码
//			g404.put("4051", "01050001");// 业务编码
//			g404.put("4052", "系统账户");// 业务对象
//			g404.put("4053", "1");// 业务数量
//			g404.put("4062", "");// 业务系统参考号
//			g404.put("4064", "");// 业务单所属客户编码
//			g404.endRow();
//		}
//		
//		// 业务单费用信息
//		IParamGroup g405 = new ParamGroupImpl("405");
//		g405.put("4047", "1");// 业务单序号
//		g405.put("4021", "0001");// 币种编码
//		g405.put("4066", txnAmount);// 业务单原始金额
//		g405.put("4067", "0");// 业务单优惠金额
//		g405.put("4068", txnAmount);// 业务单应付金额
//		g405.put("4071", "103");// 费用项标识
//		g405.endRow();
//		
//		// 业务单费用手续费信息
//		if (concessionType != null && concession != null) {
//			g405.put("4047", "2");// 业务单序号
//			g405.put("4021", "0001");// 币种编码
//			g405.put("4066", concession);// 业务单原始金额(手续费)
//			g405.put("4067", "0");// 业务单优惠金额
//			g405.put("4068", concession);// 业务单应付金额(手续费)
//			g405.put("4071", "103");// 费用项标识
//			g405.endRow();
//		}
//		
//		IParamGroup g407 = new ParamGroupImpl("407");
//		// 支付单信息
//		IParamGroup g408 = new ParamGroupImpl("408");
//		
//		if ("PT1004".equals(peFlag)) {
//			String newOrgCode ="";
//			TransManage tm = new TransManage();
//			PackageDataSet dataSet2 = tm.firstRoute(custCode, areaCode, channelCode, actionCode, merId, tmnNum, txnAmount, "PT1004", bankId);
//			String respCode = dataSet2.getByID("0001", "000");
//			if (Long.valueOf(respCode) == 0) {
//				actionCode = dataSet2.getByID("4051","423");
//				newOrgCode = dataSet2.getByID("4098","423");
//			}
//			// 业务属性信息
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_TRADETYPE");// 业务属性编码
//			g407.put("4088", "020011");//属性值1
//			g407.put("4089", "");
//			g407.put("4091", "");
//			g407.put("4093", "");
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_CERTID");// 业务属性编码
//			g407.put("4088", certType);//属性值1
//			g407.put("4089", "");
//			g407.put("4091", "");
//			g407.put("4093", "");
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_CERTCODE");// 业务属性编码
//			g407.put("4088", certCode);//属性值1
//			g407.put("4089", "");
//			g407.put("4091", "");
//			g407.put("4093", "");
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_BANKNAME");// 业务属性编码
//			g407.put("4088", bankAcctName);//属性值1
//			g407.put("4089", "");
//			g407.put("4091", "");
//			g407.put("4093", "");
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_BANKBELONG");// 业务属性编码
//			g407.put("4088", areaCode);//属性值1
//			g407.put("4089", "");
//			g407.put("4091", "");
//			g407.put("4093", "");
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_BANKID");// 业务属性编码
//			g407.put("4088", bankId);// 属性值1
//			g407.put("4089", "");
//			g407.put("4091", "");
//			g407.put("4093", "");
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_BANKCARDID");// 业务属性编码
//			g407.put("4088", cardType);// 属性值1
//			g407.put("4089", "");
//			g407.put("4091", "");
//			g407.put("4093", "");
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_BANKCARDTYPE");// 业务属性编码
//			g407.put("4088", bankCardId);// 属性值1
//			g407.put("4089", "");
//			g407.put("4091", "");
//			g407.put("4093", "");
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
//			g407.put("4088", "0100");//属性值1
//			g407.put("4089", "");
//			g407.put("4091", "");
//			g407.put("4093", "");
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
//			g407.put("4088", cardAcctNbr);// 属性值1
//			g407.put("4089", "");
//			g407.put("4091", "");
//			g407.put("4093", "");
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
//			g407.put("4088", "0001");//属性值1
//			g407.put("4089", "");
//			g407.put("4091", "");
//			g407.put("4093", "");
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g408.put("4103", "1");// 扣款顺序号
//			g408.put("4097", "PT1004");// 支付方式编码
//			g408.put("4098", newOrgCode);// 支付机构编码
//			g408.put("4099", bankId);// 账户类型编码
//			g408.put("4100", bankAcctName);//
//			g408.put("4101", bankAcctNbr);// 账号
//			g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
//			g408.put("4021", "0001");// 币种编码
//			g408.put("4104", finalAmonut);// 支付金额
//			g408.endRow();
//			//充值
//		}else{
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
//			g407.put("4088", "0100");//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
//			g407.put("4088", cardAcctNbr);//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_TRADETYPE");// 业务属性编码
//			g407.put("4088", "1001");//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_SIGNID");// 业务属性编码
//			g407.put("4088", signContractId);//属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_BANKNAME");// 业务属性编码
//			g407.put("4088", bankAcctName);// 属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			g407.put("4047", "1");// 业务单序号
//			g407.put("4051", actionCode);// 业务编码
//			g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
//			g407.put("4088", "0001");// 属性值1
//			g407.put("4080", "0");// 控制标识
//			g407.endRow();
//			
//			g408.put("4103", "1");// 扣款顺序号
//			g408.put("4097", "PT0005");// 支付方式编码
//			g408.put("4098", "110000");// 支付机构编码
//			g408.put("4099", bankId);// 账户类型编码
//			g408.put("4100", bankAcctName);//
//			g408.put("4101", bankAcctNbr);// 账号
//			g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
//			g408.put("4021", "0001");// 币种编码
//			g408.put("4104", finalAmonut);// 支付金额
//			g408.endRow();
//		}
//		
//		
//		
//		
//		// 组成数据包,调用SCS0001接口
//		IServiceCall caller = new ServiceCallImpl();
//		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
//		
//		// 返回结果
//		return dataSet;
//	}
	/**
	 * 调用SCS0001,完成提现操作
	 * @version: 1.00
	 * @history: 2012-3-29 上午12:56:58 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param order
	 * @param custCode
	 * @param cardAcctNbr
	 * @return
	 * @throws Exception
	 * @see
	 */
	
	//2013-4-20 wanght 
	private PackageDataSet withdrawal(SignOrder order, String custCode, String cardAcctNbr,DpAccountManagementRequest request) throws Exception {
		String agentCode = custCode;											//商户编码
		String channelCode = order.getChannelCode();							//交易渠道
		String merId = order.getMerId();										
		String tmnNum = order.getTmnNum();										//终端号
		String areaCode = order.getBankAcctInfo().getAreaCode();				//区域编码
		String actionCode = order.getActionCode();								//操作编码
		String txnAmount = order.getAmount();									//交易金额
		String transSeq = order.getTransSeq();									//订单号
		String keep = order.getKeep();											//keep
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeTime = df.format(new Date());								//交易时间
		
		String bankAcctNbr = order.getBankAcctInfo().getBankAcctNbr();			//银行卡号
//		String signContractId = order.getBankAcctInfo().getSignContractId();	//签约ID
		String bankAcctName = order.getBankAcctInfo().getBankAcctName();		//开户名
		String bankId = order.getBankAcctInfo().getBankCode();				//银行ID
		String cardType = order.getBankAcctInfo().getCardType();			//卡折标识
		String certCode = order.getBankAcctInfo().getCertCode();
		String certType = order.getBankAcctInfo().getCertType();
//		String bankfilialeName = order.getBankAcctInfo().getBankfilialeName();
		String bankCardId = order.getBankAcctInfo().getBankCardId();		//对公对私
		String concessionType = order.getConcessionType(); 						//优惠方案方向
		String concession = order.getConcession(); 								//优惠金额
		String finalAmonut = order.getFinalAmount();							//最终订单金额
		if (Charset.isEmpty(cardType)) {
			cardType = "1";
		}
		if (Charset.isEmpty(bankCardId)) {
			bankCardId = "1";
		}

		String newOrgCode ="";
		TransManage tm = new TransManage();
		PackageDataSet dataSet2 = tm.firstRoute(custCode, areaCode, channelCode, actionCode, merId, tmnNum, txnAmount, "PT1004", bankId);
		String respCode = dataSet2.getByID("0001", "000");
		if (Long.valueOf(respCode) == 0) {
			actionCode = dataSet2.getByID("4051","423");
			newOrgCode = dataSet2.getByID("4098","423");
		}
		
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", agentCode);// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", tmnNum);// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", channelCode);// 渠道类型编码
		/* 外部订单号改成4028
		  g401.put("4017", orderSeq);// 终端流水号
		  	keep值放4017
		  */
		g401.put("4017", keep);// 终端流水号
		g401.put("4028", transSeq);// 订单号
		g401.put("4018", "0.0.0.0");// 操作原始来源
		g401.put("4012", "提现");// 订单备注
		
		g401.put("4146", request.getStaffCode());//2013-4-20 wanght
		
		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
		g401.endRow();
		
		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", finalAmonut);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", finalAmonut);// 订单应付金额
		g402.endRow();
		
		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", bankAcctName);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 业务系统参考号
		g404.put("4064", "");// 业务单所属客户编码
		g404.endRow();
		
		//业务单手续费
		if (concessionType != null && concession != null && !concession.equals("0")) {
			g404.put("4047", "2");// 业务单序号
			g404.put("4049", "0001");// 产品编码
			g404.put("4051", "01050001");// 业务编码
			g404.put("4052", "系统账户");// 业务对象
			g404.put("4053", "1");// 业务数量
			g404.put("4062", "");// 业务系统参考号
			g404.put("4064", "");// 业务单所属客户编码
			g404.endRow();
		}
		
		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();
		
		// 业务单费用手续费信息
		if (concessionType != null && concession != null && !concession.equals("0")) {
			g405.put("4047", "2");// 业务单序号
			g405.put("4021", "0001");// 币种编码
			g405.put("4066", concession);// 业务单原始金额(手续费)
			g405.put("4067", "0");// 业务单优惠金额
			g405.put("4068", concession);// 业务单应付金额(手续费)
			g405.put("4071", "103");// 费用项标识
			g405.endRow();
		}
		
		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_TRADETYPE");// 业务属性编码
		g407.put("4088", "020011");//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		///////////////////////////////////////////////////////////////////
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_CERTID");// 业务属性编码
		g407.put("4088", certType);//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_CERTCODE");// 业务属性编码
		g407.put("4088", certCode);//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKBELONG");// 业务属性编码
		g407.put("4088", areaCode);//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKID");// 业务属性编码
		g407.put("4088", bankId);// 属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDID");// 业务属性编码
		g407.put("4088", cardType);// 属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDTYPE");// 业务属性编码
		g407.put("4088", bankCardId);// 属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", "0200");//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", bankAcctNbr);// 属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_PAYTYPE");// 业务属性编码
		g407.put("4088", "PT1004");//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_PAYORG");// 业务属性编码
		g407.put("4088", newOrgCode);//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		//提现
		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099", request.getAcctType() );// 账户类型编码 "0001"
		g408.put("4101", cardAcctNbr);// 账号
		g408.put("4102", request.getPassword());// 支付密码 MD5.MD5Encode("123456")
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", finalAmonut);// 支付金额
		g408.endRow();
	
		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
		
		// 返回结果
		return dataSet;
	}
	
	
	private PackageDataSet withdrawal(SignOrder order, String custCode, String cardAcctNbr,CommonReqAbs request) throws Exception {
		String agentCode = custCode;											//商户编码
		String channelCode = order.getChannelCode();							//交易渠道
		String merId = order.getMerId();										
		String tmnNum = order.getTmnNum();										//终端号
		String areaCode = order.getBankAcctInfo().getAreaCode();				//区域编码
		String actionCode = order.getActionCode();								//操作编码
		String txnAmount = order.getAmount();									//交易金额
		String transSeq = order.getTransSeq();									//订单号
		String keep = order.getKeep();											//keep
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeTime = df.format(new Date());								//交易时间
		
		String bankAcctNbr = order.getBankAcctInfo().getBankAcctNbr();			//银行卡号
//		String signContractId = order.getBankAcctInfo().getSignContractId();	//签约ID
		String bankAcctName = order.getBankAcctInfo().getBankAcctName();		//开户名
		String bankId = order.getBankAcctInfo().getBankCode();				//银行ID
		String cardType = order.getBankAcctInfo().getCardType();			//卡折标识
		String certCode = order.getBankAcctInfo().getCertCode();
		String certType = order.getBankAcctInfo().getCertType();
//		String bankfilialeName = order.getBankAcctInfo().getBankfilialeName();
		String bankCardId = order.getBankAcctInfo().getBankCardId();		//对公对私
		String concessionType = order.getConcessionType(); 						//优惠方案方向
		String concession = order.getConcession(); 								//优惠金额
		String finalAmonut = order.getFinalAmount();							//最终订单金额
		if (Charset.isEmpty(cardType)) {
			cardType = "1";
		}
		if (Charset.isEmpty(bankCardId)) {
			bankCardId = "1";
		}

		String newOrgCode ="";
		TransManage tm = new TransManage();
		PackageDataSet dataSet2 = tm.firstRoute(custCode, areaCode, channelCode, actionCode, merId, tmnNum, txnAmount, "PT1004", bankId);
		String respCode = dataSet2.getByID("0001", "000");
		if (Long.valueOf(respCode) == 0) {
			actionCode = dataSet2.getByID("4051","423");
			newOrgCode = dataSet2.getByID("4098","423");
		}
		
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", agentCode);// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", tmnNum);// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", channelCode);// 渠道类型编码
		/* 外部订单号改成4028
		  g401.put("4017", orderSeq);// 终端流水号
		  	keep值放4017
		  */
		g401.put("4017", keep);// 终端流水号
		g401.put("4028", transSeq);// 订单号
		g401.put("4018", "0.0.0.0");// 操作原始来源
		g401.put("4012", "提现");// 订单备注
		
		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
		g401.endRow();
		
		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", finalAmonut);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", finalAmonut);// 订单应付金额
		g402.endRow();
		
		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", bankAcctName);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 业务系统参考号
		g404.put("4064", "");// 业务单所属客户编码
		g404.endRow();
		
		//业务单手续费
		if (concessionType != null && concession != null) {
			g404.put("4047", "2");// 业务单序号
			g404.put("4049", "0001");// 产品编码
			g404.put("4051", "01050001");// 业务编码
			g404.put("4052", "系统账户");// 业务对象
			g404.put("4053", "1");// 业务数量
			g404.put("4062", "");// 业务系统参考号
			g404.put("4064", "");// 业务单所属客户编码
			g404.endRow();
		}
		
		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();
		
		// 业务单费用手续费信息
		if (concessionType != null && concession != null) {
			g405.put("4047", "2");// 业务单序号
			g405.put("4021", "0001");// 币种编码
			g405.put("4066", concession);// 业务单原始金额(手续费)
			g405.put("4067", "0");// 业务单优惠金额
			g405.put("4068", concession);// 业务单应付金额(手续费)
			g405.put("4071", "103");// 费用项标识
			g405.endRow();
		}
		
		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_TRADETYPE");// 业务属性编码
		g407.put("4088", "020011");//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		///////////////////////////////////////////////////////////////////
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_CERTID");// 业务属性编码
		g407.put("4088", certType);//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_CERTCODE");// 业务属性编码
		g407.put("4088", certCode);//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKBELONG");// 业务属性编码
		g407.put("4088", areaCode);//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKID");// 业务属性编码
		g407.put("4088", bankId);// 属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDID");// 业务属性编码
		g407.put("4088", cardType);// 属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDTYPE");// 业务属性编码
		g407.put("4088", bankCardId);// 属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", "0200");//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", bankAcctNbr);// 属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_PAYTYPE");// 业务属性编码
		g407.put("4088", "PT1004");//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_PAYORG");// 业务属性编码
		g407.put("4088", newOrgCode);//属性值1
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		//提现
		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码
		g408.put("4099",  "0001" );// 账户类型编码 "0001"
		g408.put("4101", cardAcctNbr);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码 MD5.MD5Encode("123456")
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", finalAmonut);// 支付金额
		g408.endRow();
	
		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
		
		// 返回结果
		return dataSet;
	}
	
	/**
	 * 调用SCS0001,完成充值操作
	 * @version: 1.00
	 * @history: 2012-3-29 上午01:02:05 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param order
	 * @param custCode
	 * @param cardAcctNbr
	 * @return
	 * @throws Exception
	 * @see
	 */
	private PackageDataSet recharge(SignOrder order, String custCode, String cardAcctNbr,CommonReqAbs request) throws Exception {
		String agentCode = custCode;											//商户编码
		String channelCode = order.getChannelCode();							//交易渠道
		String tmnNum = order.getTmnNum();										//终端号
		String merId = order.getMerId();										
		String areaCode = order.getBankAcctInfo().getAreaCode();				//区域编码
		String actionCode = order.getActionCode();								//操作编码
		String txnAmount = order.getAmount();									//交易金额
		String transSeq = order.getTransSeq();									//订单号号
		String keep = order.getKeep();											//keep
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeTime = df.format(new Date());								//交易时间
		
		String bankAcctNbr = order.getBankAcctInfo().getBankAcctNbr();
		String bankAcctName = order.getBankAcctInfo().getBankAcctName();		//开户名
		String bankId = order.getBankAcctInfo().getBankCode();					//银行ID
		String signContractId = order.getBankAcctInfo().getSignContractId();	//签约ID
		String cardType = order.getBankAcctInfo().getCardType();				//卡折标识
		String certCode = order.getBankAcctInfo().getCertCode();
		String certType = order.getBankAcctInfo().getCertType();
		String bankfilialeName = order.getBankAcctInfo().getBankfilialeName();
		String bankCardId = order.getBankAcctInfo().getBankCardId();			//对公对私
		String concessionType = order.getConcessionType(); 						//优惠方案方向
		String concession = order.getConcession(); 								//优惠金额
		String finalAmonut = order.getFinalAmount();							//最终订单金额
		
		if (Charset.isEmpty(cardType)) {
			cardType = "1";
		}
		if (Charset.isEmpty(bankCardId)) {
			bankCardId = "1";
		}
		
		String newOrgCode ="";
		TransManage tm = new TransManage();
		PackageDataSet dataSet2 = tm.firstRoute(custCode, areaCode, channelCode, actionCode, merId, tmnNum, txnAmount, "PT1004", bankId);
		String respCode = dataSet2.getByID("0001", "000");
		if (Long.valueOf(respCode) == 0) {
			actionCode = dataSet2.getByID("4051","423");
			newOrgCode = dataSet2.getByID("4098","423");
		}
		
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", agentCode);// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", tmnNum);// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", channelCode);// 渠道类型编码
		/* 外部订单号改成4028
		  g401.put("4017", orderSeq);// 终端流水号
		  	keep值放4017
		  */
		g401.put("4017", keep);// 终端流水号
		g401.put("4028", transSeq);// 订单号
		g401.put("4018", "0.0.0.0");// 操作原始来源
		g401.put("4012", "");
		
		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
		g401.endRow();
		
		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", finalAmonut);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", finalAmonut);// 订单应付金额
		g402.endRow();
		
		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", agentCode);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 业务系统参考号
		g404.put("4064", "");// 业务单所属客户编码
		g404.endRow();
		
		//业务单手续费
		if (concessionType != null && concession != null) {
			g404.put("4047", "2");// 业务单序号
			g404.put("4049", "0001");// 产品编码
			g404.put("4051", "01050001");// 业务编码
			g404.put("4052", "系统账户");// 业务对象
			g404.put("4053", "1");// 业务数量
			g404.put("4062", "");// 业务系统参考号
			g404.put("4064", "");// 业务单所属客户编码
			g404.endRow();
		}
		
		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		// 业务单费用手续费信息
		if (concessionType != null && concession != null) {
			g405.put("4047", "2");// 业务单序号
			g405.put("4021", "0001");// 币种编码
			g405.put("4066", concession);// 业务单原始金额(手续费)
			g405.put("4067", "0");// 业务单优惠金额
			g405.put("4068", concession);// 业务单应付金额(手续费)
			g405.put("4071", "103");// 费用项标识
			g405.endRow();
		}
		
		// 支付单信息
		IParamGroup g407 = new ParamGroupImpl("407");
		// 业务属性信息
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_TRADETYPE");// 业务属性编码
		g407.put("4088", "020011");//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_CERTID");// 业务属性编码
		g407.put("4088", certType);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_CERTCODE");// 业务属性编码
		g407.put("4088", certCode);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKNAME");// 业务属性编码
		g407.put("4088", bankAcctName);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKBELONG");// 业务属性编码
		g407.put("4088", areaCode);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKID");// 业务属性编码
		g407.put("4088", bankId);// 属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDID");// 业务属性编码
		g407.put("4088", cardType);// 属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDTYPE");// 业务属性编码
		g407.put("4088", bankCardId);// 属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", "0100");//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", cardAcctNbr);// 属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
		g407.put("4088", "0001");//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		//充值
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT1004");// 支付方式编码
		g408.put("4098", newOrgCode);// 支付机构编码
		g408.put("4099", bankId);// 账户类型编码
		g408.put("4100", bankAcctName);//
		g408.put("4101", bankAcctNbr);// 账号
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", finalAmonut);// 支付金额
		g408.endRow();
		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
		
		// 返回结果
		return dataSet;
	}


	public PackageDataSet RechargeAndWithdrawal(String custCode, SignOrder order,String mobile,DpAccountManagementRequest request) throws Exception {
         //		 根据客户编码获得该客户天讯卡户号
		String cardAcctNbr = getTissonCardAcct(custCode);
		if(cardAcctNbr == null) {
			throw new Exception("获取卡户号失败");
		}
		// 调用SCS0001,完成充值/提现
		String actionCode = order.getActionCode();
		// 根据交易类型做分发操作
		if(actionCode.equals(AC_AUTH_BANKCARD_RECHARGE)) {
			// 调用充值
			return newRecharge(order, custCode, cardAcctNbr,mobile,request);
		} else if(actionCode.equals(AC_AUTH_BANKCARD_WITHD)) {
			// 调用提现
			return withdrawal(order, custCode, cardAcctNbr,request);
		} else {
			throw new Exception("非合法的交易类型");
		}
	}

	public PackageDataSet Recharge(String custCode, SignOrder order,String mobile,DpInf02032Request request) throws Exception {
        //		 根据客户编码获得该客户天讯卡户号
		String cardAcctNbr = getTissonCardAcct(custCode);
		if(cardAcctNbr == null) {
			throw new Exception("获取卡户号失败");
		}
		// 调用充值
		return newRecharge1(order, custCode, cardAcctNbr,mobile,request);
		
	}

	private PackageDataSet newRecharge(SignOrder order, String custCode, String cardAcctNbr, String mobile,DpAccountManagementRequest request) throws Exception {
		
			String agentCode = custCode;											//商户编码
			String channelCode = order.getChannelCode();							//交易渠道
			String tmnNum = order.getTmnNum();										//终端号
			String merId = order.getMerId();										
			String areaCode = order.getBankAcctInfo().getAreaCode();				//区域编码
			String actionCode = order.getActionCode();								//操作编码
			String txnAmount = order.getAmount();									//交易金额
			String transSeq = order.getTransSeq();									//订单号号
			String keep = order.getKeep();											//keep
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String tradeTime = df.format(new Date());								//交易时间
			
			String bankAcctNbr = order.getBankAcctInfo().getBankAcctNbr();
			String bankAcctName = order.getBankAcctInfo().getBankAcctName();		//开户名
			String bankId = order.getBankAcctInfo().getBankCode();					//银行ID
//			String signContractId = order.getBankAcctInfo().getSignContractId();	//签约ID
			String cardType = order.getBankAcctInfo().getCardType();				//卡折标识
			String certCode = order.getBankAcctInfo().getCertCode();
			String certType = order.getBankAcctInfo().getCertType();
//			String bankfilialeName = order.getBankAcctInfo().getBankfilialeName();
			String bankCardId = order.getBankAcctInfo().getBankCardId();			//对公对私
			String concessionType = order.getConcessionType(); 						//优惠方案方向
			String concession = order.getConcession(); 								//优惠金额
			String finalAmonut = order.getFinalAmount();							//最终订单金额
			
			if (Charset.isEmpty(cardType)||cardType.equals("0")) {//卡折标识为0时,传1
				cardType = "1";
			}
			if (Charset.isEmpty(bankCardId)) {
				bankCardId = "1";
			}
			
			String newOrgCode ="";
			TransManage tm = new TransManage();
			PackageDataSet dataSet2 = tm.firstRoute(custCode, areaCode, channelCode, actionCode, merId, tmnNum, txnAmount, "PT1004", bankId);
			String respCode = dataSet2.getByID("0001", "000");
			if (Long.valueOf(respCode) == 0) {
				actionCode = dataSet2.getByID("4051","423");
				newOrgCode = dataSet2.getByID("4098","423");
			}
			
			/**
			 * 调用SCS0001,完成交易操作
			 */
			// 订单受理信息
			IParamGroup g401 = new ParamGroupImpl("401");
			g401.put("4004", agentCode);// 客户编码
//			g401.put("4004", "0");// 客户编码,禁用客户编码查询到收入信息
			g401.put("4005", "OT001");// 订单类型编码：业务类订单
			g401.put("4006", areaCode);// 所属区域编码
			g401.put("4007", tmnNum);// 受理终端号
			g401.put("4008", tradeTime);// 受理时间
			g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
			g401.put("4144", channelCode);// 渠道类型编码
			/* 外部订单号改成4028
			  g401.put("4017", orderSeq);// 终端流水号
			  	keep值放4017
			  */
			g401.put("4017", keep);// 终端流水号
			g401.put("4028", transSeq);// 订单号
			g401.put("4018", "0.0.0.0");// 操作原始来源
			
			g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
			g401.put("4012", "");
			
			g401.put("4146", request.getStaffCode());//2013-4-20 wanght
			g401.endRow();
			
			// 订单费用信息
			IParamGroup g402 = new ParamGroupImpl("402");
			g402.put("4021", "0001");// 币种编码
			g402.put("4023", finalAmonut);// 订单原始金额
			g402.put("4024", "0");// 订单优惠金额
			g402.put("4025", finalAmonut);// 订单应付金额
			g402.endRow();
			
			// 业务单信息
			IParamGroup g404 = new ParamGroupImpl("404");
			g404.put("4047", "1");// 业务单序号
			g404.put("4049", "0001");// 产品编码
			g404.put("4051", actionCode);// 业务编码
			g404.put("4052", agentCode);// 业务对象
			g404.put("4053", "1");// 业务数量
			g404.put("4062", "");// 业务系统参考号
			g404.put("4064", "");// 业务单所属客户编码
			g404.endRow();
			
			//业务单手续费
			if (concessionType != null && concession != null && !concession.equals("0")) {
				g404.put("4047", "2");// 业务单序号
				g404.put("4049", "0001");// 产品编码
				g404.put("4051", "01050001");// 业务编码
				g404.put("4052", "系统账户");// 业务对象
				g404.put("4053", "1");// 业务数量
				g404.put("4062", "");// 业务系统参考号
				g404.put("4064", "");// 业务单所属客户编码
				g404.endRow();
			}
			
			// 业务单费用信息
			IParamGroup g405 = new ParamGroupImpl("405");
			g405.put("4047", "1");// 业务单序号
			g405.put("4021", "0001");// 币种编码
			g405.put("4066", txnAmount);// 业务单原始金额
			g405.put("4067", "0");// 业务单优惠金额
			g405.put("4068", txnAmount);// 业务单应付金额
			g405.put("4071", "103");// 费用项标识
			g405.endRow();

			// 业务单费用手续费信息
			if (concessionType != null && concession != null && !concession.equals("0")) {
				g405.put("4047", "2");// 业务单序号
				g405.put("4021", "0001");// 币种编码
				g405.put("4066", concession);// 业务单原始金额(手续费)
				g405.put("4067", "0");// 业务单优惠金额
				g405.put("4068", concession);// 业务单应付金额(手续费)
				g405.put("4071", "103");// 费用项标识
				g405.endRow();
			}
			
			// 支付单信息
			IParamGroup g407 = new ParamGroupImpl("407");
			// 业务属性信息
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_TRADETYPE");// 业务属性编码
			g407.put("4088", "020011");//属性值1
			g407.put("4089", "");
			g407.put("4091", "");
			g407.put("4093", "");
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_CERTID");// 业务属性编码
			g407.put("4088", certType);//属性值1
			g407.put("4089", "");
			g407.put("4091", "");
			g407.put("4093", "");
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_CERTCODE");// 业务属性编码
			g407.put("4088", certCode);//属性值1
			g407.put("4089", "");
			g407.put("4091", "");
			g407.put("4093", "");
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_BANKNAME");// 业务属性编码
			g407.put("4088", bankAcctName);//属性值1
			g407.put("4089", "");
			g407.put("4091", "");
			g407.put("4093", "");
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_BANKBELONG");// 业务属性编码
			g407.put("4088", areaCode);//属性值1
			g407.put("4089", "");
			g407.put("4091", "");
			g407.put("4093", "");
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_BANKID");// 业务属性编码
			g407.put("4088", bankId);// 属性值1
			g407.put("4089", "");
			g407.put("4091", "");
			g407.put("4093", "");
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_BANKCARDID");// 业务属性编码 卡折标识
			g407.put("4088", cardType);// 属性值1
			g407.put("4089", "");
			g407.put("4091", "");
			g407.put("4093", "");
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_BANKCARDTYPE");// 业务属性编码  对公对私
			g407.put("4088", bankCardId);// 属性值1
			g407.put("4089", "");
			g407.put("4091", "");
			g407.put("4093", "");
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
			g407.put("4088", "0100");//属性值1
			g407.put("4089", "");
			g407.put("4091", "");
			g407.put("4093", "");
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
			g407.put("4088", cardAcctNbr);// 属性值1
			g407.put("4089", "");
			g407.put("4091", "");
			g407.put("4093", "");
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
			g407.put("4088", request.getAcctType());//属性值1
			g407.put("4089", "");
			g407.put("4091", "");
			g407.put("4093", "");
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_PHONENUM");// 业务属性编码
			if(mobile!=null&&mobile.equals("")){
			  g407.put("4088", mobile);//属性值1
			}else{
				g407.put("4088", "123456");//属性值1
			}
			g407.put("4089", "");
			g407.put("4091", "");
			g407.put("4093", "");
			g407.put("4080", "0");// 控制标识
			g407.endRow();
			//充值
			IParamGroup g408 = new ParamGroupImpl("408");
			g408.put("4103", "1");// 扣款顺序号
			g408.put("4097", "PT1004");// 支付方式编码
			g408.put("4098", newOrgCode);// 支付机构编码
			
			g408.put("4099", request.getAcctType());// 账户类型编码
			g408.put("4100", bankAcctName);//
			g408.put("4101", bankAcctNbr);// 账号
			
			g408.put("4102", MD5.MD5Encode("123456"));// 支付密码 //MD5.MD5Encode("123456")
			g408.put("4021", "0001");// 币种编码
			g408.put("4104", finalAmonut);// 支付金额
			g408.endRow();
			// 组成数据包,调用SCS0001接口
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
			
			// 返回结果
			return dataSet;		
	}
	
	private PackageDataSet newRecharge1(SignOrder order, String custCode, String cardAcctNbr, String mobile,DpInf02032Request request) throws Exception {
		
		String agentCode = custCode;											//商户编码
		String channelCode = order.getChannelCode();							//交易渠道
		String tmnNum = order.getTmnNum();										//终端号
		String merId = order.getMerId();										
		String areaCode = order.getBankAcctInfo().getAreaCode();				//区域编码
		String actionCode = order.getActionCode();								//操作编码
		String txnAmount = order.getAmount();									//交易金额
		String transSeq = order.getTransSeq();									//订单号号
		String keep = order.getKeep();											//keep
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeTime = df.format(new Date());								//交易时间
		
		String bankAcctNbr = order.getBankAcctInfo().getBankAcctNbr();
		String bankAcctName = order.getBankAcctInfo().getBankAcctName();		//开户名
		String bankId = order.getBankAcctInfo().getBankCode();					//银行ID
//		String signContractId = order.getBankAcctInfo().getSignContractId();	//签约ID
		String cardType = order.getBankAcctInfo().getCardType();				//卡折标识
		String certCode = order.getBankAcctInfo().getCertCode();
		String certType = order.getBankAcctInfo().getCertType();
//		String bankfilialeName = order.getBankAcctInfo().getBankfilialeName();
		String bankCardId = order.getBankAcctInfo().getBankCardId();			//对公对私
		String concessionType = order.getConcessionType(); 						//优惠方案方向
		String concession = order.getConcession(); 								//优惠金额
		String finalAmonut = order.getFinalAmount();							//最终订单金额
		
		if (Charset.isEmpty(cardType)||cardType.equals("0")) {//卡折标识为0时,传1
			cardType = "1";
		}
		if (Charset.isEmpty(bankCardId)) {
			bankCardId = "1";
		}
		
		String newOrgCode ="";
		TransManage tm = new TransManage();
		PackageDataSet dataSet2 = tm.firstRoute(custCode, areaCode, channelCode, actionCode, merId, tmnNum, txnAmount, "PT1004", bankId);
		String respCode = dataSet2.getByID("0001", "000");
		if (Long.valueOf(respCode) == 0) {
			actionCode = dataSet2.getByID("4051","423");
			newOrgCode = dataSet2.getByID("4098","423");
		}
		
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", agentCode);// 客户编码
//		g401.put("4004", "0");// 客户编码,禁用客户编码查询到收入信息
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", tmnNum);// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", channelCode);// 渠道类型编码
		/* 外部订单号改成4028
		  g401.put("4017", orderSeq);// 终端流水号
		  	keep值放4017
		  */
		g401.put("4017", keep);// 终端流水号
		g401.put("4028", transSeq);// 订单号
		g401.put("4018", "0.0.0.0");// 操作原始来源
		
		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
		g401.put("4012", "");
		
		g401.put("4146", request.getStaffCode());//2013-4-20 wanght
		g401.endRow();
		
		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", finalAmonut);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", finalAmonut);// 订单应付金额
		g402.endRow();
		
		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", agentCode);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 业务系统参考号
		g404.put("4064", "");// 业务单所属客户编码
		g404.endRow();
		
		//业务单手续费
		if (concessionType != null && concession != null && !concession.equals("0")) {
			g404.put("4047", "2");// 业务单序号
			g404.put("4049", "0001");// 产品编码
			g404.put("4051", "01050001");// 业务编码
			g404.put("4052", "系统账户");// 业务对象
			g404.put("4053", "1");// 业务数量
			g404.put("4062", "");// 业务系统参考号
			g404.put("4064", "");// 业务单所属客户编码
			g404.endRow();
		}
		
		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();

		// 业务单费用手续费信息
		if (concessionType != null && concession != null && !concession.equals("0")) {
			g405.put("4047", "2");// 业务单序号
			g405.put("4021", "0001");// 币种编码
			g405.put("4066", concession);// 业务单原始金额(手续费)
			g405.put("4067", "0");// 业务单优惠金额
			g405.put("4068", concession);// 业务单应付金额(手续费)
			g405.put("4071", "103");// 费用项标识
			g405.endRow();
		}
		
		// 支付单信息
		IParamGroup g407 = new ParamGroupImpl("407");
		// 业务属性信息
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_TRADETYPE");// 业务属性编码
		g407.put("4088", "020011");//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_CERTID");// 业务属性编码
		g407.put("4088", certType);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_CERTCODE");// 业务属性编码
		g407.put("4088", certCode);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKNAME");// 业务属性编码
		g407.put("4088", bankAcctName);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKBELONG");// 业务属性编码
		g407.put("4088", areaCode);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKID");// 业务属性编码
		g407.put("4088", bankId);// 属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDID");// 业务属性编码 卡折标识
		g407.put("4088", cardType);// 属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDTYPE");// 业务属性编码  对公对私
		g407.put("4088", bankCardId);// 属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", "0100");//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", cardAcctNbr);// 属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
		g407.put("4088", request.getAcctType());//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_PHONENUM");// 业务属性编码
		if(mobile!=null&&!mobile.equals("")){
		  g407.put("4088", mobile);//属性值1
		}else{
			g407.put("4088", "123456");//属性值1
		}
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		//充值
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT1004");// 支付方式编码
		g408.put("4098", newOrgCode);// 支付机构编码
		
		g408.put("4099", request.getAcctType());// 账户类型编码
		g408.put("4100", bankAcctName);//
		g408.put("4101", bankAcctNbr);// 账号
		
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码 //MD5.MD5Encode("123456")
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", finalAmonut);// 支付金额
		g408.endRow();
		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
		
		// 返回结果
		return dataSet;		
}
	/**
	 * INF05002 充值
	 * @param order
	 * @param custCode
	 * @param cardAcctNbr
	 * @param mobile
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public PackageDataSet rechargeForINF05002(SignOrder order,   String cardAcctNbr, String mobile,DpInf5002TradeRequest request) throws Exception {
		String acctType = INF05002.ACCT_TYPE;//账户类型编码
		String agentCode = request.getAgentCode();											//商户编码
		String channelCode = order.getChannelCode();							//交易渠道
		String tmnNum = order.getTmnNum();										//终端号
		String merId = order.getMerId();										
		String areaCode = order.getBankAcctInfo().getAreaCode();				//区域编码
		String actionCode ="01010001";								//操作编码
		String txnAmount = order.getAmount();									//交易金额
		String transSeq = order.getTransSeq();									//订单号号
		String keep = order.getKeep();											//keep
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String tradeTime = df.format(new Date());								//交易时间
		
		String bankAcctNbr = order.getBankAcctInfo().getBankAcctNbr();
		String bankAcctName = order.getBankAcctInfo().getBankAcctName();		//开户名
		String bankId = order.getBankAcctInfo().getBankCode();					//银行ID
		String cardType = order.getBankAcctInfo().getCardType();				//卡折标识
		String certCode = order.getBankAcctInfo().getCertCode();
		String certType = order.getBankAcctInfo().getCertType();
		String bankCardId = order.getBankAcctInfo().getBankCardId();			//对公对私
		String concessionType = order.getConcessionType(); 						//优惠方案方向
		String concession = order.getConcession(); 								//优惠金额
		String finalAmonut = order.getFinalAmount();							//最终订单金额
		
		if (Charset.isEmpty(cardType)||cardType.equals("0")) {//卡折标识为0时,传1
			cardType = "1";
		}
		if (Charset.isEmpty(bankCardId)) {
			bankCardId = "1";
		}
		
		String newOrgCode ="";
		TransManage tm = new TransManage();
		PackageDataSet dataSet2 = tm.firstRoute(agentCode, areaCode, channelCode, actionCode, merId, tmnNum, txnAmount, "PT1004", bankId);
		String respCode = dataSet2.getByID("0001", "000");
		if (Long.valueOf(respCode) == 0) {
			actionCode = dataSet2.getByID("4051","423");
			newOrgCode = dataSet2.getByID("4098","423");
		}
		
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", agentCode);// 客户编码
//		g401.put("4004", "0");// 客户编码,禁用客户编码查询到收入信息
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", tmnNum);// 受理终端号
		g401.put("4008", tradeTime);// 受理时间
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", channelCode);// 渠道类型编码
		/* 外部订单号改成4028
		  g401.put("4017", orderSeq);// 终端流水号
		  	keep值放4017
		 */
		g401.put("4017", keep);// 终端流水号
		g401.put("4028", transSeq);// 订单号
		g401.put("4018", "0.0.0.0");// 操作原始来源
		
		g401.put("4284", request.getMerId());//机构编码     //20130628 wanght
		g401.put("4012", "");
		
		g401.put("4146", request.getStaffCode());//2013-4-20 wanght
		g401.endRow();
		
		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", finalAmonut);// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", finalAmonut);// 订单应付金额
		g402.endRow();
		
		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4049", "0001");// 产品编码
		g404.put("4051", actionCode);// 业务编码
		g404.put("4052", agentCode);// 业务对象
		g404.put("4053", "1");// 业务数量
		g404.put("4062", "");// 业务系统参考号
		g404.put("4064", "");// 业务单所属客户编码
		g404.endRow();
		
		//业务单手续费
		if (concessionType != null && concession != null && !concession.equals("0")) {
			g404.put("4047", "2");// 业务单序号
			g404.put("4049", "0001");// 产品编码
			g404.put("4051", "01050001");// 业务编码
			g404.put("4052", "系统账户");// 业务对象
			g404.put("4053", "1");// 业务数量
			g404.put("4062", "");// 业务系统参考号
			g404.put("4064", "");// 业务单所属客户编码
			g404.endRow();
		}
		
		// 业务单费用信息
		IParamGroup g405 = new ParamGroupImpl("405");
		g405.put("4047", "1");// 业务单序号
		g405.put("4021", "0001");// 币种编码
		g405.put("4066", txnAmount);// 业务单原始金额
		g405.put("4067", "0");// 业务单优惠金额
		g405.put("4068", txnAmount);// 业务单应付金额
		g405.put("4071", "103");// 费用项标识
		g405.endRow();
		
		// 业务单费用手续费信息
		if (concessionType != null && concession != null && !concession.equals("0")) {
			g405.put("4047", "2");// 业务单序号
			g405.put("4021", "0001");// 币种编码
			g405.put("4066", concession);// 业务单原始金额(手续费)
			g405.put("4067", "0");// 业务单优惠金额
			g405.put("4068", concession);// 业务单应付金额(手续费)
			g405.put("4071", "103");// 费用项标识
			g405.endRow();
		}
		
		// 支付单信息
		IParamGroup g407 = new ParamGroupImpl("407");
		// 业务属性信息
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_TRADETYPE");// 业务属性编码
		g407.put("4088", "020011");//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_CERTID");// 业务属性编码
		g407.put("4088", certType);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_CERTCODE");// 业务属性编码
		g407.put("4088", certCode);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKNAME");// 业务属性编码
		g407.put("4088", bankAcctName);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKBELONG");// 业务属性编码
		g407.put("4088", areaCode);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKID");// 业务属性编码
		g407.put("4088", bankId);// 属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDID");// 业务属性编码 卡折标识
		g407.put("4088", cardType);// 属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDTYPE");// 业务属性编码  对公对私
		g407.put("4088", bankCardId);// 属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");// 业务属性编码
		g407.put("4088", "0100");//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTCAACCODE");// 业务属性编码
		g407.put("4088", cardAcctNbr);// 属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_DSTACCTTYPE");// 业务属性编码
		g407.put("4088", acctType);//属性值1
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_PHONENUM");// 业务属性编码
		if(mobile!=null&&!mobile.equals("")){
			g407.put("4088", mobile);//属性值1
		}else{
			g407.put("4088", "123456");//属性值1
		}
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();
		//充值
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT1004");// 支付方式编码
		g408.put("4098", newOrgCode);// 支付机构编码
		
		g408.put("4099", acctType);// 账户类型编码
		g408.put("4100", bankAcctName);//
		g408.put("4101", bankAcctNbr);// 账号
		
		g408.put("4102", MD5.MD5Encode("123456"));// 支付密码 //MD5.MD5Encode("123456")
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", finalAmonut);// 支付金额
		g408.endRow();
		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402, g404, g405, g407, g408);
		
		// 返回结果
		return dataSet;		
	}
	/**
	 * 转账
	 * @param tradeRequest
	 * @return
	 */
	public PackageDataSet transForINF05002(DpInf5002TradeRequest dpRequest,SignOrder order) throws Exception{

			TCumAcctDao acctDao = new TCumAcctDao();
			String acctCode = acctDao.getAcctCode(dpRequest.getAgentCode());// 

			String acctCodeR = acctDao.getAcctCode(dpRequest.getPayeeCode());// 

			String area_code = TCumInfoDao.getAreaCode(dpRequest.getAgentCode());

			String bankCode = "110000";

			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String tradeTime = df.format(new Date());

			String actionCode = "01030001";

			/**
			 * 调用SCS0001,完成交易操作
			 */
			// 订单受理信息
			IParamGroup g401 = new ParamGroupImpl("401");
			g401.put("4004", dpRequest.getAgentCode());// 客户编码
			g401.put("4005", "OT001");// 订单类型编码：业务类订单
			g401.put("4006", area_code);// 所属区域编码 广州地区440100
			g401.put("4007", dpRequest.getTmnNum());// 受理终端号
			g401.put("4008", tradeTime);// 受理时间
			g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
			g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
			g401.put("4017", dpRequest.getKeep());// 
			g401.put("4018", "0.0.0.0");// 受理终端号
			g401.put("4280", dpRequest.getPayeeCode());// 收款方
			g401.put("4028", dpRequest.getOrderSeq());// 订单号

			g401.endRow();

			// 订单费用信息
			IParamGroup g402 = new ParamGroupImpl("402");
			g402.put("4021", "0001");// 币种编码
			g402.put("4023", order.getAmount());// 订单原始金额
			g402.put("4024", "0");// 订单优惠金额
			g402.put("4025", order.getAmount());// 订单应付金额
			g402.endRow();

			// 业务单信息
			IParamGroup g404 = new ParamGroupImpl("404");
			g404.put("4047", "1");// 业务单序号
			g404.put("4049", "0001");// 产品编码 // 全国新宽联0007 腾讯QQ 0031 改0039 电子售卡
			g404.put("4051", actionCode);// 业务编码
			g404.put("4052", dpRequest.getPayeeCode());//
			g404.put("4053", "1");// 业务数量
			g404.endRow();

			// 业务单费用信息
			IParamGroup g405 = new ParamGroupImpl("405");
			g405.put("4047", "1");// 业务单序号
			g405.put("4021", "0001");// 币种编码
			g405.put("4066", order.getAmount());// 业务单原始金额
			g405.put("4067", "0");// 业务单优惠金额
			g405.put("4068", order.getAmount());// 业务单应付金额
			g405.put("4071", "101");// 费用项标识
			g405.endRow();

			// 业务属性信息
			IParamGroup g407 = new ParamGroupImpl("407");
			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_DEALTYPE");
			g407.put("4088", "0301");
			g407.put("4080", "1");// 控制标识
			g407.endRow();

			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_DSTCAACCODE");
			g407.put("4088", acctCodeR);
			g407.put("4080", "1");// 控制标识
			g407.endRow();

			g407.put("4047", "1");// 业务单序号
			g407.put("4051", actionCode);// 业务编码
			g407.put("4087", "SCS_DSTACCTTYPE");
			g407.put("4088", "0001");
			g407.put("4080", "1");// 控制标识
			g407.endRow();

			// 支付单信息
			IParamGroup g408 = new ParamGroupImpl("408");
			g408.put("4103", "1");// 扣款顺序号
			g408.put("4097", "PT0004");// 支付方式编码
			g408.put("4098", bankCode);// 
			g408.put("4099", INF05002.ACCT_TYPE);// 账户类型编码
			g408.put("4101", acctCode);// 账号
			g408.put("4102", "123456");// 支付密码
			g408.put("4021", "0001");// 币种编码
			g408.put("4104", order.getAmount());// 支付金额

			g408.endRow();

			// 组成数据包,调用SCS0001接口
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
					g404, g405, g407, g408);

			// 返回结果
			return dataSet;

	}

}
