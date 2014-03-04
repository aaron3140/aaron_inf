package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.algorithm.RSA;
import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.entity.register.TSymStaff;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.RegisterManger;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.CreateConstant;
import common.utils.DateTool;
import common.utils.PasswordUtil;
import common.utils.PrtnUtil;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02022Request;
import common.xml.dp.DpInf02022Response;

import framework.config.ConfigReader;
import framework.config.GlobalConstants;
import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02022 {

	public static String svcInfName = "INF02022";

	private static final Log log = LogFactory.getLog(INF02022.class);

	public static String execute(String in0, String in1) {

		log.info("请求参数：：" + in1);

		String responseCode = "";

		String responseDesc = "";

		DpInf02022Request dpRequest = null;

		DpInf02022Response resp = new DpInf02022Response();

		RespInfo respInfo = null;

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		RegisterManger manger = new RegisterManger();

		Long pid = 0L;

		Long oid = 0L;

		try {
			respInfo = new RespInfo(in1, "20");

			dpRequest = new DpInf02022Request(in1);

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"", "", "", "", "S0A");

			TInfOperInLogManager man = new TInfOperInLogManager();

			// 判断插入是否成功
			if (tInfOperInLog != null) {

				boolean flag = man.selectTInfOperInLogByKeep(dpRequest
						.getKeep());
				// 判断流水号是否可用
				if (flag) {
					flag = man.updateAllow(tInfOperInLog.getOperInId(), 1);

					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {

					flag = man.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			// PRTNNAME唯一验证
			if (manger.checkPrtnName(dpRequest.getCustName())) {

				throw new Exception("商户名称已存在,创建企业失败");
			}

			// CUSTCODE唯一验证
			if (manger.checkCustCode(dpRequest.getCustName())) {

				throw new Exception("企业客户编码已存在,创建企业失败");
			}

//			// 短信验证
//			if ("001".equals(dpRequest.getVerifyType())) {
//
//				String oldv = TInfLoginLogDao.getRVerifyCode(dpRequest
//						.getCustName());
//
//				if (!oldv.equalsIgnoreCase(dpRequest.getVerifyCode())) {
//
//					throw new Exception("验证码过期");
//				}
//			}
			
			if ("PRT1002".equals(dpRequest.getRegType())) {

				dpRequest.setCertNo(dpRequest.getEnterCerNo());
			}

			
			//获取代理商所属的商户
			Long agentPrtn=0l;
			
			if(dpRequest.getAgentCode()!=null&&!"".equals(dpRequest.getAgentCode())){
				
				agentPrtn = manger.getAgentByPtrnCode(dpRequest.getAgentCode());
			}else{
				
				Properties pro = ConfigReader.readConfig(GlobalConstants.MERID_CONF );
				agentPrtn = Long.parseLong(pro.get("MERID").toString());
			}
			
			// 获取组织机构
			Long orgId = manger.getOrgByPrtn(agentPrtn);

			// 创建管理员信息
			TSymStaff staff = manger.addSymStaff(dpRequest);

			// 根据规则生成合作伙伴编码
			String prtnCode = PrtnUtil.getPrtnCode();

			// 1. 插入合作伙伴记录
			pid = manger.addPartner(dpRequest.getEnterName(), prtnCode, dpRequest
					.getRegType(), dpRequest.getAreaCode(), agentPrtn
					.toString());

			log.info("1.创建商户：：" + pid);
			// 2.保存机构信息
			oid = manger.addOrg(agentPrtn.toString(), dpRequest.getAreaCode(),
					dpRequest.getEnterName());

			staff.setOrgId(oid);

			log.info("2.创建机构：：" + oid);

			// 3.绑定prtn和org的关联关系
			manger.builPartner(pid.toString(), oid.toString());

			log.info("3.创建机构 商户 ：：");
			// 调用CUM0001接口,新建客户信息
			createCum0001(dpRequest, orgId, staff.getStaffId(), prtnCode);

			String custId = manger.getCustId(dpRequest.getCustName());

			log.info("4.创建客户信息：：" + custId);
			// 调用EBK1004接口,创建卡户
			PackageDataSet ds = createCardAcct(dpRequest, orgId, staff
					.getStaffId());

			// 设置生成的必要账户号
			String cardAcctNbr = (String) ds.getParamByID("6001", "601").get(0);

			log.info("5.创建创建卡户：：" + cardAcctNbr);

			// 激活卡号
			ds = createEbk0003(cardAcctNbr);

			log.info("6.激活卡号：：");

			ds = createExtAcct(dpRequest, cardAcctNbr, "0110", orgId, staff
					.getStaffId());

			log.info("7.创建酬金账户：：");

			String products = dpRequest.getProducts();
			if (products.contains("104")) {

				ds = createExtAcct(dpRequest, cardAcctNbr, "0007", orgId, staff
						.getStaffId());

				log.info("8.创建交费易账户：：");
			}

			/**
			 * 调用CUM0005接口,绑定客户和卡户信息
			 */
			binding(dpRequest.getCustName(), "ACCT002", cardAcctNbr, "OTHER",
					staff.getStaffId());

			log.info("9.绑定客户和卡户信息：：");

			String plines = dpRequest.getProducts().replaceAll("\\|", ",");

			log.info("10.产品线信息：：" + plines);
			// 获取角色
			List<String> roles = manger.findRoleList(plines);

			log.info("11.获取角色：：" + roles);
			// 功能权限
			List<String> func = manger.findFuncprivsByCust(plines,agentPrtn );

			log.info("12.功能权限：：" + func);
			// 工号权限
			List<String> funcId = manger.findFuncId(func);

			log.info("13.工号权限：：" + funcId);

			// 其他属性
			List<Object[]> parm = RegisterManger.attrInfo(dpRequest, custId,
					plines);

			// 事务提交数据
			manger.tran_AaveAttr(custId, staff, roles, func, funcId, parm);

			log.info("14.事务提交数据：：");
			
			try{
			
				if(dpRequest.getAgentCode()!=null&&!"".equals(dpRequest.getAgentCode())){
					
					Map p_cust = manger.findCustByPtrnId(dpRequest.getAgentCode());
					
					Long groupNo = manger.findGroupNo("RL003", p_cust.get("CUST_NAME").toString());
					
					PackageDataSet cum4002 = createCum4002(
							dpRequest.getCustName(), staff.getStaffId(), oid.toString(),
							"ADD", p_cust.get("CUST_CODE").toString(), groupNo.toString());
					
					
					log.info("15.创建代理商群组：：");
					
				}
			}catch(Exception e){
				throw new INFException(INFErrorDef.AGENTCODE_CRE_FAULT,
						INFErrorDef.AGENTCODE_CRE_FAULT_DESC);
			}
			
			if(roles.size()>0){
				
				String roleName = ConverRole(roles.get(0));
				
				String content = PasswordUtil.getContent(dpRequest.getCustName(), roleName,
						staff.getStaffId(), staff.getPwd(), staff.getClPwd(), null, null);
				
				PasswordUtil.scs4003(CreateConstant.SMS_Create_QY_PayPwd, CreateConstant.SMS_SENDTYPE_CUSTCODE, dpRequest.getCustName(), staff.getMobile(), content, staff.getStaffCode());
				
			}

			log.info("16.发送短信：：");
			// 授权
			responseCode = "000000";
			responseDesc = "成功";

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dpRequest.getRegType(),
					dpRequest.getCustName(), dpRequest.getProducts(), dpRequest
							.getEnterName(), dpRequest.getRegDate(), dpRequest
							.getRemark1(), dpRequest.getRemark2());

			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		} catch (XmlINFException spe) {

			manger.delCusInfo(pid.toString(), oid.toString());
			// if(tInfOperInLog!=null){
			// //插入信息到出站日志表
			// sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
			// dpRequest.getKeep(), dpRequest.getKeep(), svcCode, responseCode,
			// spe.getMessage(), "S0A");
			// }

			spe.setRespInfo(respInfo);

			String oXml = ExceptionHandler.toXML(spe, infId);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		} catch (Exception e) {

			manger.delCusInfo(pid.toString(), oid.toString());
			// if(tInfOperInLog!=null){
			// //插入信息到出站日志表
			// sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
			// dpRequest.getKeep(), dpRequest.getKeep(), svcCode, responseCode,
			// e.getMessage(), "S0A");
			// }
			String oXml = ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), infId);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		}

	}

	private static String ConverRole(String roleId){
		
		String r = "企业账户管理员";
		
		if("104".equals(roleId)){
			
			r = "手机IPOS管理员";
		}
		
		return r;
	}
	/**
	 * 调用cum4002 , 维护客户群的客户 , update
	 * 
	 * @param custCode
	 * @param login_staffId
	 * @param login_orgId
	 * @param operation
	 * @param p_custCode
	 * @param groupNo
	 * @param p_custCode_old
	 * @param groupNo_old
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet createCum4002(String custCode, String staffId,
			String orgId, String operation, String p_custCode,
			String groupNo)
			throws Exception {

		// 客户信息基本组
		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2905", operation);
		g200.put("2002", p_custCode);
		g200.endRow();

		IParamGroup g212 = new ParamGroupImpl("212");
		g212.put("2101", groupNo);
		g212.endRow();

		IParamGroup g213 = new ParamGroupImpl("213");
		g213.put("2002", custCode);
		g213.endRow();

		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", "OTHER");// 渠道类型
		g211.put("2077", CreateConstant.SYM_OPER_CUMSOURCE);// 操作来源
		g211.put("2078", staffId);// 操作执行者
		g211.put("2079", orgId);// 操作机构
		g211.endRow();

		// 组成数据包,调用CUM0001接口
		IServiceCall caller = new ServiceCallImpl();
		return caller.call("BIS", "CUM4002", g200, g212, g213, g211);

	}


	/**
	 * 调用CUM0005接口,绑定客户和卡户信息
	 * 
	 * @version: 1.00
	 * @history: 2012-2-18 下午02:46:03 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param cumCode
	 * @param CAType
	 * @param CANbr
	 * @param channelTypeCode
	 * @param operator
	 * @return
	 * @throws Exception
	 * @see
	 */
	private static PackageDataSet binding(String cumCode, String CAType,
			String CANbr, String channelTypeCode, String staff)
			throws Exception {
		// 操作公用信息
		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2002", cumCode);// 客户编码
		g200.endRow();

		// 客户支付方案信息
		IParamGroup g207 = new ParamGroupImpl("207");
		g207.put("4097", "PT011");
		g207.put("2048", CAType);// 卡户类型编码
		g207.put("2049", CANbr);// 卡户号
		g207.put("2050", "110000");
		g207.put("2051", "企业账户");
		g207.put("2052", "0");
		g207.put("2068", cumCode);
		g207.endRow();

		// 操作记录信息
		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", channelTypeCode);// 渠道类型编码
		g211.put("2077", "20");// 操作来源
		g211.put("2078", staff);// 操作执行者
		g211.endRow();

		// 组成数据包,调用CUM0005接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller
				.call("BIS", "CUM0005", g200, g207, g211);
		return dataSet;
	}

	/**
	 * 调用cum4002 , 维护客户群的客户
	 * 
	 * @param custCode
	 * @param login_staffId
	 * @param login_orgId
	 * @param operation
	 * @param p_custCode
	 * @param groupNo
	 * @param p_custCode_old
	 * @param groupNo_old
	 * @return
	 * @throws Exception
	 */
	// private PackageDataSet createCum4002(String custCode, Long orgId,
	// String p_custCode, String groupNo) throws Exception {
	//
	// // 客户信息基本组
	// IParamGroup g200 = new ParamGroupImpl("200");
	// g200.put("2905", "ADD");
	// g200.put("2002", p_custCode);
	// g200.endRow();
	//
	// IParamGroup g212 = new ParamGroupImpl("212");
	// g212.put("2101", groupNo);
	// g212.endRow();
	//
	// IParamGroup g213 = new ParamGroupImpl("213");
	// g213.put("2002", custCode);
	// g213.endRow();
	//
	// IParamGroup g211 = new ParamGroupImpl("211");
	// g211.put("2076", "OTHER");// 渠道类型
	// g211.put("2077", "");// 操作来源 Constants.SYM_OPER_CUMSOURCE
	// g211.put("2078", "");// 操作执行者 login_staffId
	// g211.put("2079", orgId.toString());// 操作机构
	// g211.endRow();
	//
	// // 组成数据包,调用CUM4002接口
	// IServiceCall caller = new ServiceCallImpl();
	//		
	// return caller.call("BIS", "CUM4002", g200, g212, g213, g211);
	//		
	// }

	/**
	 * 调用EBK1005接口,创建扩展账户
	 * 
	 * @version: 1.00
	 * @history: 2012-2-18 下午03:19:24 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param cardAcctNbr
	 * @param acctType
	 * @param staffId
	 * @param staffOrgId
	 * @return
	 * @throws Exception
	 * @see
	 */
	private static PackageDataSet createExtAcct(DpInf02022Request dpRequest,
			String cardAcctNbr, String accType, Long orgId, String staff)
			throws Exception {
		// 电子银行基本组
		IParamGroup g600 = new ParamGroupImpl("600");
		g600.put("6316", "0400");// 操作类型编码：开户
		g600.put("6001", cardAcctNbr);// 卡户号
		g600.put("6421", accType);// 账户类型编码
		g600.put("6032", "0");// 账户消费次限额
		g600.put("6033", "0");// 账户消费日限额
		g600.put("6034", "0");// 账户消费总限额
		g600.put("6306", dpRequest.getChannelCode());// 操作来源
		g600.put("6307", staff);// 操作执行者 staffId
		g600.put("6308", orgId.toString());// 执行机构标识
		g600.put("6309", new SimpleDateFormat("MMdd").format(new Date()));// 操作日期（必填，格式：MMDD）
		g600.put("6310", new SimpleDateFormat("HHmmss").format(new Date()));// 操作时间（必填，格式：HH24MMSS）
		g600.endRow();

		// 组成数据包,调用EBK1005接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("EAS", "EBK1005", g600);
		return dataSet;
	}

	/**
	 * 调用EBK0003接口，激活卡号
	 * 
	 * @param partner
	 * @param staffId
	 * @param staffOrgId
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet createEbk0003(String cardAcctNbr)
			throws Exception {
		// 客户信息基本组
		IParamGroup g600 = new ParamGroupImpl("600");
		g600.put("6316", "0102"); // 激活
		g600.put("6001", cardAcctNbr);// 卡户号
		g600.put("6306", "0.0.0.0");// 操作来源
		g600.put("6309", DateTool.formatCurDate("MMdd"));// 卡户号
		g600.put("6310", DateTool.formatCurDate("HHmmss"));// 卡户号
		g600.endRow();

		// 组成数据包,调用CUM0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("EAS", "EBK0003", g600);

		return dataSet;
	}

	/**
	 * 调用EBK1004接口，创建卡户
	 * 
	 * @version: 1.00
	 * @history: 2012-2-18 下午02:45:43 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param CATypeCode
	 * @param staffId
	 * @param staffOrgId
	 * @param cumAreaCode
	 * @return
	 * @throws Exception
	 * @see
	 */
	private static PackageDataSet createCardAcct(DpInf02022Request dpRequest,
			Long orgId, String staff) throws Exception {
		// 电子银行基本组
		IParamGroup g600 = new ParamGroupImpl("600");
		g600.put("6316", "0100");// 操作类型编码
		g600.put("6002", RSA.encrypt("123456"));// 卡户密码
		g600.put("6003", "01");// 卡户分类编码
		g600.put("6004", "00000000000000000000");// 卡户特征序列
		g600.put("6306", dpRequest.getChannelCode());// 操作来源
		g600.put("6307", staff);// 操作执行者 staffId
		g600.put("6308", orgId.toString());// 执行机构标识
		g600.put("6309", new SimpleDateFormat("MMdd").format(new Date()));// 操作日期（必填，格式：MMDD）
		g600.put("6310", new SimpleDateFormat("HHmmss").format(new Date()));// 操作时间（必填，格式：HH24MMSS）
		g600.put("2008", dpRequest.getAreaCode());// 客户所属区域
		g600.endRow();

		// 组成数据包,调用EBK1004接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("EAS", "EBK1004", g600);
		return dataSet;
	}

	/**
	 * 调用CUM0001接口，新建客户信息
	 * 
	 * @param agent
	 * @param prtnCode
	 * @param custType
	 * @param staffId
	 * @param staffOrgId
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet createCum0001(DpInf02022Request dpRequest,
			Long orgId, String staff, String prtnCode) throws Exception {
		// 客户基本信息
		IParamGroup g201 = new ParamGroupImpl("201");
		g201.put("2002", dpRequest.getCustName());// 客户编码
		g201.put("2003", dpRequest.getEnterName());// 客户名称
		g201.put("C003", "U");// 客户性别：未知
		g201.put("2004", CreateConstant.CUM_CUST_TYPE_C02);// 企业商户
		g201.put("2005", CreateConstant.CUM_CUST_LEV_COM);// 普通客户

		g201.put("2006", "A01");// 客户认证等级编码：非实名制
		g201.put("2007", RSA.encrypt("123456"));// 客户密码
		g201.put("2008", dpRequest.getAreaCode());// 所属区域编码
		g201.put("2011", prtnCode);// 所属合作伙伴编码
		g201.put("C013", "X");// 是否开通电子银行:不创建
		g201.endRow();

		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", "OTHER");// 渠道类型
		g211.put("2077", dpRequest.getChannelCode());// 操作来源
		g211.put("2078", staff);// 操作执行者
		g211.put("2079", orgId.toString());// 操作机构
		g211.endRow();

		// 组成数据包,调用CUM0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM0001", g201, g211);

		return dataSet;
	}

	public static String executeForMD5(String in0, String in1) {
		String oXml = execute(in0, in1);
		return ResponseVerifyAdder.pkgForMD5(oXml, null);
	}
}
