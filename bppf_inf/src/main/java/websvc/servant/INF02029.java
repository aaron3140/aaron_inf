package websvc.servant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TInfVaildateDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.Charset;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf02029Request;
import common.xml.dp.DpInf02029Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 邱亚建 2013-10-31 下午03:31:47<br/>
 * 
 * 本类描述:客户信息验证接口
 */
public class INF02029 {

	public static String svcInfName = "INF02029";

	private static final Logger log = Logger.getLogger(INF02029.class);

	public static String execute(String in0, String in1) {

		DpInf02029Request dpRequest = null;
		DpInf02029Response resp = null;
		RespInfo respInfo = null; // 返回信息头
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String keep = "";
		String ip = "";
		String responseCode = "000000";
		String responseDesc = "成功";
		String svcCode = WebSvcTool.getSvcCode(in0);
		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			dpRequest = new DpInf02029Request(in1);

			// 客户端MD5校验--------------------------------------------
//			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
//			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(), tokenValidTime);
//			dpRequest.verifyByMD5(md5Key);
//			TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());
			// -------------------------------------------------------------------

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

			// // 判断有权限
			// List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode());
			// boolean r = false;
			// for (int i = 0; i < privList.size(); i++) {
			// Map map = (Map) privList.get(i);
			// String str = map.get("PRIV_URL").toString();
			// if (PrivConstant.WS_BILL_QUERY.equals(str)) {
			// r = true;
			// break;
			// }
			//
			// }
			//
			// if (!r) {
			// throw new Exception("你没有客户信息校验权限");
			// }
			
			Map<String, String> map = new HashMap<String, String>();
			// 校验验证码
			String regVerifyCode = dpRequest.getRegVerifyCode();
			if (!Charset.isEmpty(regVerifyCode, true)) {
//				String oldv = TInfLoginLogDao.getRVerifyCode(dpRequest.getStaffCode());
				String vCodeValidTime = TSymSysParamDao.getVerifyValidTime();
				Map codeMap = TInfVaildateDao.getVCode(dpRequest.getStaffCode(),vCodeValidTime);
				log.info("codeMap#######################  "+codeMap);
				if (codeMap ==null||!regVerifyCode.equalsIgnoreCase((String)codeMap.get("VAL_CODE"))) {
					map.put("REGVERIFYCODE", "0");
				}
			}
 
			//验证码为空时才验证其他信息
			if(Charset.isEmpty(regVerifyCode, true)){
				// 校验客户编码和用户名是否匹配
				TCumInfoDao cumInfoDao = new TCumInfoDao();
				String custCodeByStaff = cumInfoDao.getCustCodeByStaff(dpRequest.getStaffCode());
				if (Charset.isEmpty(custCodeByStaff, true)) {
					throw new INFException("019999", "用户名不存在");
				}
				String custCode = dpRequest.getCustCode();
				if (!Charset.isEmpty(custCode, true)) {
					if (!StringUtils.equals(custCodeByStaff, custCode)) {
						throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST, INFErrorDef.CUSTCODE_NOT_MATCH_STAFF_DESC);
					}
				} else {
					dpRequest.setCustCode(custCodeByStaff);
				}

				
				// 校验登录密码
				PackageDataSet ds = null;
				if (!Charset.isEmpty(dpRequest.getPassword(), true)) {
					try {
						ds = callCUM1003(dpRequest);
					} catch (Exception e) {
						map.put("PASSWORD", "0");
						log.error("密码校验结果：" + e.getMessage());
					}
				}


				// 验证工号对应的身份证号码
				String contactNo2 = dpRequest.getContactNo();
				if (!Charset.isEmpty(contactNo2, true)) {
					String certNbr;
					try {
						certNbr = TCumInfoDao.getCertNbrByStaffCode(dpRequest.getStaffCode());
						if (!StringUtils.equals(contactNo2, certNbr)) {
							map.put("CONTACTNO", "0");
							log.info("INF02029 工号身份证校验不通过 接收到的=["+contactNo2+"]  查询到的=[" +certNbr +"]");
						}
					} catch (Exception e) {
						log.error("查询工号身份证出错["+dpRequest.getStaffCode()+"]  :" + e.getMessage());
						map.put("CONTACTNO", "0");
					}
				}

				// 验证注册身份证号，银行卡号，户名，开卡证件号
				String bankAcct = dpRequest.getBankAcct();
				String transAccnNme = dpRequest.getTransAccnNme();
				String certType = dpRequest.getCertType();//CUM_INDUSTRY
				String cerNo = dpRequest.getCerNo();
				// 以上三个值有一个不为空就要调用CUM0003
				if (!Charset.isEmpty(bankAcct, true) || !Charset.isEmpty(transAccnNme, true) || !Charset.isEmpty(cerNo, true)|| !Charset.isEmpty(certType, true)) {
					try {
						ds = callCUM0003(dpRequest);
					} catch (Exception e) {
						// 调用接口出现异常时
						log.error("调用CUM0003 返回结果：" + e.getMessage());
						if (!Charset.isEmpty(bankAcct, true)) {
							map.put("BANKACCT", "0");
						}
						if (!Charset.isEmpty(transAccnNme, true)) {
							map.put("TRANSACCNAME", "0");
						}
						if (!Charset.isEmpty(certType, true)) {
							map.put("CERTTYPE", "0");
						}
						if (!Charset.isEmpty(cerNo, true)) {
							map.put("CERNO", "0");
						}
					}
					responseCode = (String) ds.getParamByID("0001", "000").get(0);
					if (Long.valueOf(responseCode) == 0) {
						responseDesc = (String) ds.getParamByID("0002", "000").get(0);
						ArrayList list2048 = ds.getParamByID("2048", "207");
						ArrayList list2049 = ds.getParamByID("2049", "207");
						ArrayList list2051 = ds.getParamByID("2051", "207");
						if (list2048 != null && !list2048.isEmpty()) {
							for (int i = 0; i < list2048.size(); i++) {
								if ("ACCT001".equals(list2048.get(i))) {
									String queryBankAcct = (String) list2049.get(i);
									String queryTransAccnNme = (String) list2051.get(i);
									if (!Charset.isEmpty(bankAcct, true) && !bankAcct.equals(queryBankAcct)) {
										map.put("BANKACCT", "0");
										log.info("INF02029 银行账号校验不通过 接收到的=["+bankAcct+"]  查询到的=[" +queryBankAcct +"]");
									}
									if (!Charset.isEmpty(transAccnNme, true) && !transAccnNme.equals(queryTransAccnNme)) {
										map.put("TRANSACCNAME", "0");
										log.info("INF02029 开户名校验不通过 接收到的=["+transAccnNme+"]  查询到的=[" +queryTransAccnNme +"]");
									}
									break;
								}
							}
						} else {
							if (!Charset.isEmpty(dpRequest.getBankAcct(), true)) {
								map.put("BANKACCT", "0");
							}
							if (!Charset.isEmpty(dpRequest.getTransAccnNme(), true)) {
								map.put("TRANSACCNAME", "0");
							}
						}
						ArrayList list2069 = ds.getParamByID("2069", "216");
						ArrayList list2071 = ds.getParamByID("2071", "216");
						if (list2069 != null && !list2069.isEmpty()) {
							for (int i = 0; i < list2069.size(); i++) {
								if ("2566".equals(list2069.get(i))) {// 证件号码
									String queryCerType = (String) list2071.get(i);
									if (!Charset.isEmpty(certType, true) && !certType.equals(queryCerType)) {
										map.put("CERTTYPE", "0");
										log.info("INF02029 开户证件类型校验不通过 接收到的=["+certType+"]  查询到的=[" +queryCerType +"]");
									}
								}
								if ("2567".equals(list2069.get(i))) {// 证件号码
									String queryCerNo = (String) list2071.get(i);
									if (!Charset.isEmpty(cerNo, true) && !cerNo.equals(queryCerNo)) {
										map.put("CERNO", "0");
										log.info("INF02029 开户证件号校验不通过 接收到的=["+cerNo+"]  查询到的=[" +queryCerNo +"]");
									}
								}
							}
						} else {
							if (!Charset.isEmpty(dpRequest.getCerNo(), true)) {
								map.put("CERNO", "0");
							}
							if (!Charset.isEmpty(certType, true)) {
								map.put("CERTTYPE", "0");
							}
						}
					}
				}
			}
			

			if(!map.isEmpty()){
				//011014 客户信息验证不通过
				responseCode = "011014";
				responseDesc =  "客户信息验证不通过";
			}
			
			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), dpRequest.getKeep(), dpRequest.getIp(), svcCode, responseCode, responseDesc, "S0A");
			
			//更新验证码无效
			TInfVaildateDao.updateVCode2(dpRequest.getStaffCode());
			
			resp = new DpInf02029Response();
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, map, dpRequest.getCustCode(),
					dpRequest.getStaffCode(), dpRequest.getRemark1(), dpRequest.getRemark2());
		} catch (XmlINFException spe) {
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), id);
		}
	}

	/**
	 * 客户信息查询
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callCUM0003(DpInf02029Request dpRequest) throws Exception {
		IParamGroup g0003_200 = new ParamGroupImpl("200"); // 包头
		g0003_200.put("2002", dpRequest.getCustCode());
		g0003_200.endRow();

		IParamGroup g0003_002 = new ParamGroupImpl("002"); // 包头
		g0003_002.put("0011", "203");
		g0003_002.endRow();
		g0003_002.put("0011", "207");
		g0003_002.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet DataSet = caller.call("BIS", "CUM0003", g0003_200, g0003_002);// 组成交易数据包,调用CUM0003接口

		return DataSet;
	}

	/**
	 * 校验密码
	 * 
	 * @param dpRequest
	 * @param staffCode
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callCUM1003(DpInf02029Request dpRequest) throws Exception {

		// String verityType = "0001"; //支付密码
		String verityType = "0002"; // 登录密码

		String tmnNum = dpRequest.getTmnNum();

		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", dpRequest.getStaffCode());
		g200.put("2903", "2007");
		g200.put("2904", dpRequest.getPassword());
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

}
