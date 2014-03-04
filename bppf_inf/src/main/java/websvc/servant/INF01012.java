package websvc.servant;

/*
 * 登录验证接口
 */
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfVaildateDao;
import common.dao.TPnmPartnerDao;
import common.dao.TSymStaffRoleDao;
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
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.WebSvcTool;
import common.utils.verify.VCodeBuilder;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf01012Request;
import common.xml.dp.DpInf01012Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF01012 {

	public static String svcInfName = "INF01012";

	private static final Log logger = LogFactory.getLog(INF01012.class);

	public static String execute(String in0, String in1) {

		logger.info("请求参数：：" + in1);

		DpInf01012Request dpRequest = null;

		DpInf01012Response resp = new DpInf01012Response();

		RespInfo respInfo = null;

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);
		try {

			dpRequest = new DpInf01012Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"StaffCode", dpRequest.getStaffCode(), "VerifyCode",
					dpRequest.getVerifyCode(), "S0A");

			// 判断登陆用户是否有权限
			TSymStaffRoleDao dao = new TSymStaffRoleDao();

			logger.info("1.判断权限开始                                   渠道:"
					+ dpRequest.getChannelCode() + "工号:"
					+ dpRequest.getStaffCode());

			if ((dpRequest.getChannelCode())
					.equals(ChannelCode.AGENT_CHANELCODE)) {

				if (!(dao.adminRole("", dpRequest.getStaffCode()) || dao
						.adminRole(dpRequest.getStaffCode()))) {

					throw new Exception("你没有权限登录该客户端");
				}
			}

			TCumInfoDao infoDao = new TCumInfoDao();

			String custCode = infoDao.getCustCodeByStaff(dpRequest
					.getStaffCode());
			logger.info("staffCode:" + dpRequest.getStaffCode()
					+ " 对应custcode:" + custCode);

			if (!infoDao.isExistCust(custCode)) {

				throw new Exception("该账户异常");
			}

			TInfOperInLogManager manager = new TInfOperInLogManager();

			logger.info("2.判断流水号                                         KEEP:"
					+ dpRequest.getKeep());
			// 判断插入是否成功
			if (tInfOperInLog != null) {

				boolean flag = manager.selectTInfOperInLogByKeep(dpRequest
						.getKeep());
				// 判断流水号是否可用
				if (flag) {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);

					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			PackageDataSet ds = null;
			
			String responseCode = INFErrorDef.CUSTCODE_NOT_MATCH;

			String responseDesc = "验证码错误或有效期已过";
			
			// 锁屏操作
			if ("001".equals(dpRequest.getVerifyLevel())) {

				ds = bt001(dpRequest);

				responseCode = ds.getByID("0001", "000");

				responseDesc = ds.getByID("0002", "000");
				
				// 返回客户端
				String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(),
						respInfo.getRespType(), respInfo.getKeep(), "SUCCESS",
						responseCode, responseDesc, null, null, null, null,
						null, null, null, null, null, null, null, null);

				return oXml;

			} else if ("000".equals(dpRequest.getVerifyLevel())) {// 登录验证

				if (!Charset.isEmpty(dpRequest.getVerifyCode(), true)) {// 输入验证码
					
					String vCodeValidTime = TSymSysParamDao.getVerifyValidTime();

					// 验证码验证
					Map map = TInfVaildateDao.getVCode(dpRequest
							.getStaffCode(),vCodeValidTime);

					if (map == null
							|| !dpRequest.getVerifyCode().equalsIgnoreCase(
									(String) map.get("VAL_CODE"))) {
						// 失败返回客户端
						throw new INFException(responseCode,
								responseDesc);

					}

				} else {
					// 没输入验证码
					Map map = TInfVaildateDao.getStaffByCode(dpRequest
							.getStaffCode());

					boolean isVerfityPass = true;// 验证IMEI和IMSI是否通过

					if (map != null) {

						String imei = (String) map.get("IMEI");

						String imsi = (String) map.get("IMSI");

						if (!dpRequest.getImei().equals(imei)
								|| !dpRequest.getImsi().equals(imsi)) {
							// 不在常用手机登录
							isVerfityPass = false;

							responseCode = INFErrorDef.INPUT_VERIFYCODE_NOT_THE_SAME;

							responseDesc = INFErrorDef.INPUT_VERIFYCODE_NOT_THE_SAME_DESC;
						}
					} else {
						// 首次登录
						isVerfityPass = false;

						responseCode = INFErrorDef.INPUT_VERIFYCODE_FIRST;

						responseDesc = INFErrorDef.INPUT_VERIFYCODE_FIRST_DESC;
					}
					if (!isVerfityPass) {
						// 失败返回客户端
						throw new INFException(responseCode,
								responseDesc);

					}

				}
			}

			logger.info("3.调核心 -------------------------------------");
			// 调核心接口
			ds = bt001(dpRequest);

			responseCode = ds.getByID("0001", "000");

			responseDesc = ds.getByID("0002", "000");

			TPnmPartnerDao tptDao = new TPnmPartnerDao();

			String prtnCode = tptDao.getPrtnCodeByCustCode(custCode);

			logger.info("4.获取权限                         ：");

			// 生成登陆用户所对应的企业账户的权限字符串（cust_code的权限）
			String privUrl = "";

			List list = PayCompetenceManage.payFunc(custCode, "20");

			if (list.size() > 0) {

				for (int i = 0; i < list.size(); i++) {

					Map map = (Map) list.get(i);

					if (i == (list.size() - 1)) {

						privUrl = privUrl + map.get("PRIV_URL").toString();

					} else {

						privUrl = privUrl + map.get("PRIV_URL").toString()
								+ ",";

					}

				}

			}

			// 获取账户标识

			String acctstat = "0";

			// 获取注册类型
			String regType = tptDao.getRegTypeByStaff(dpRequest.getStaffCode());

			// 获取产品类型
			String pLineId = tptDao.getPlineIdByCusCode(custCode);

			logger.info("5.获取产品类型                        ：" + pLineId);

			String products = tptDao.convert(pLineId);

			// 通过之后生成加密令牌 生成16串
			String desRand = new VCodeBuilder().generate16();
			
			// 更新验证码相关信息
			updateVCode(dpRequest,desRand);

			// 帮卡相关
			Hashtable<String, String> ht = cardBind(dpRequest, custCode);

			String bankMode = ht.get("bankMode");

			String regChanal = ht.get("regChanal");

			String bindCard = ht.get("bindCard");

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, desRand, custCode, prtnCode,
					privUrl, acctstat, regType, products, bankMode, regChanal,
					bindCard, dpRequest.getRemark1(), dpRequest.getRemark2());

			return oXml;

		} catch (XmlINFException spe) {

			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), infId);
		}

	}

	public static Hashtable<String, String> cardBind(
			DpInf01012Request dpRequest, String custCode) throws Exception {

		Hashtable<String, String> param = new Hashtable<String, String>();

		TCumInfoDao daoInfoDao = new TCumInfoDao();

		String regChanal = daoInfoDao.getRegCanal(custCode);

		if (regChanal == null || regChanal.equals("")) {

			regChanal = "90";
		}
		boolean isCardBing = callCUM0003(dpRequest);

		String bindCard = "0";

		if (!isCardBing) {

			logger.info("核心查询结果为未绑卡，前往查询绑卡表 StaffCode="
					+ dpRequest.getStaffCode());
			// S0A:生成
			// S0D:绑卡中
			// S0F:绑卡失败
			// S0C:绑卡成功
			String bindCardStat = daoInfoDao.getCardBindStat(custCode);

			logger.info("查询帮卡表结果为：[" + bindCardStat + "] StaffCode="
					+ dpRequest.getStaffCode());

			if ("S0D".equals(bindCardStat)) {

				bindCard = "2";
			} else if ("S0C".equals(bindCardStat)) {

				bindCard = "1";
			} else {

				bindCard = "0";
			}
		} else {

			bindCard = "1";

			logger.info("核心查询结果为绑卡成功 StaffCode=" + dpRequest.getStaffCode());
		}

		logger.info("返回的绑卡状态为: BINDSTAT=" + bindCard);

		String bankMode = daoInfoDao.getBankMode(custCode);// 资金管理模式

		logger.info("bankMode:" + bankMode);

		param.put("bankMode", bankMode);

		param.put("regChanal", regChanal);

		param.put("bindCard", bindCard);

		return param;

	}

	public static void updateVCode(DpInf01012Request dpRequest,String desRand) {

		Map<String,String> m = new HashMap<String,String>();

		m.put("STAFFCODE", dpRequest.getStaffCode());

		m.put("VAL_CODE", dpRequest.getVerifyCode());
		
		m.put("VAL_DESRAND", desRand);
		
		m.put("IMEI", dpRequest.getImei());
		
		m.put("IMSI", dpRequest.getImsi());

		TInfVaildateDao.updateVCode1(m);

	}

	/**
	 * 是否绑卡
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static boolean callCUM0003(DpInf01012Request dpRequest)
			throws Exception {
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
		PackageDataSet DataSet = caller.call("BIS", "CUM0003", g0003_200,
				g0003_002);// 组成交易数据包,调用CUM0003接口

		// 获取207组卡户号
		int count = DataSet.getParamSetNum("207");
		String bankAcctNbr = "";
		for (int i = 0; i < count; i++) {
			// 获取卡户类型
			String cardAcctType = (String) DataSet.getParamByID("2048", "207")
					.get(i);
			// 获取天讯卡户号
			if ("ACCT001".equals(cardAcctType)) {
				bankAcctNbr = (String) DataSet.getParamByID("2049", "207").get(
						i);
				break;
			}
		}
		if (!Charset.isEmpty(bankAcctNbr, true)) {
			return true;
		}
		return false;
	}

	private static PackageDataSet bt001(DpInf01012Request dpRequest)
			throws Exception {

		// DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		// String tradeTime = df.format(new Date());

		String staff = dpRequest.getStaffCode();

		String verityType = dpRequest.getVerifyType();

		String tmnNum = dpRequest.getTmnNum();

		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", staff);
		g200.put("2903", "2007");
		g200.put("2904", dpRequest.getPassowrd());
		g200.put("2172", "0001");
		g200.put("2173", verityType);
		g200.endRow();

		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", dpRequest.getChannelCode());
		g211.put("2077", tmnNum);
		g211.put("2078", "");
		g211.put("2085", dpRequest.getIp());
		g211.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM1003", g200, g211);

		return dataSet;
	}

	public static String executeForMD5(String in0, String in1) {
		String oXml = execute(in0, in1);
		return ResponseVerifyAdder.pkgForMD5(oXml, null);
	}

}
