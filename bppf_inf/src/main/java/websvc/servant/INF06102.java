package websvc.servant;

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
import common.service.TransManage;
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.PasswordUtil;
import common.utils.PaymentTool;
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf06102Request;
import common.xml.dp.DpInf06102Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 邱亚建<br>
 *         时间：2013-6-8 上午11:14:17 <br>
 *         类描述：水电煤账单缴费接口
 */
public class INF06102 {
	private static final Log logger = LogFactory.getLog(INF06102.class);

	public static String svcInfName = "INF06102";

	public static String executeForMD5(String in0, String in1) {

		DpInf06102Response resp = new DpInf06102Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf06102Request dpRequest = new DpInf06102Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {

				String tokenValidTime = TSymSysParamDao.getTokenValidTime();

				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest
						.getOperUser(), tokenValidTime);

				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);

				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest
						.getOperUser());

			}

			String oldXml = execute(in0, in1);

			return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);

		} catch (Exception e) {
			String oldXml = ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), null);

			return ResponseVerifyAdder.pkgForMD5(oldXml, null);
		}

	}

	public static String execute(String in0, String in1) {
		DpInf06102Request dpRequest = null;

		DpInf06102Response resp = new DpInf06102Response();

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

		try {

			dpRequest = new DpInf06102Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			tmnNum = dpRequest.getTmnNum();

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

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
			// 判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(
					dpRequest.getCustCode());
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.WS_BILL_PAYMENT.equals(str)) {
					r = true;
					break;
				}

			}

			if (!r) {
				throw new Exception("你没有水电煤账单缴费的权限");
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
			// if(!ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())){
			// boolean flag =
			// transManage.isCustCodeMatchTermNumNo(dpRequest.getCustCode(),
			// dpRequest.getTmnNumNo());
			// if (!flag) {
			// throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
			// INFErrorDef.CUSTCODE_NOT_MATCH_TERMNUMNO_DESC);
			// }
			// }

			PackageDataSet ds = null;
			
			// 支付方式
			String payType = dpRequest.getPayType();
			if ("0".equals(payType)) {// 企业账户
				
				//密码鉴权
				PasswordUtil.AuthenticationPassWord(dpRequest, dpRequest.getOperUser(), dpRequest.getOperPassword(), dpRequest.getECardNo(), dpRequest.getPsamCardNo(), dpRequest.getPassFlag());
//				if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())) {
//					
//					logger.info("BCD密文密码>>>>>"+dpRequest.getOperPassword());
//					
//					//解析BCD密码
//					String pwd = PasswordUtil.getDpassWord(dpRequest.getOperPassword(),dpRequest.getECardNo(),dpRequest.getPsamCardNo(),dpRequest.getPassFlag());
//					
////					logger.info("BCD明文密码>>>>>"+pwd);
//					//明文转换成密文
//					pwd = PasswordUtil.ConvertPassword(dpRequest.getOperUser(), pwd);
//					logger.info("CUM加密后的密文>>>>>"+pwd);
//					
//					ds = PasswordUtil.callCUM1003(dpRequest, dpRequest.getOperUser(), pwd,dpRequest.getPassFlag());
//					
//				}else{
//					
//					ds = PasswordUtil.callCUM1003(dpRequest, dpRequest.getOperUser(), dpRequest.getOperPassword(),"2");
//				}
//				
//				String resCode = ds.getByID("0001", "000");
//				
//				if (Long.valueOf(resCode) != 0) {
//					
//					throw new INFException(INFErrorDef.PAY_PWD_FAULT,
//							INFErrorDef.PAY_PWD_FAULT_DESC);
//				}
				String payCardNo = PaymentTool.getTissonCardAcct(dpRequest
						.getCustCode());// 支付卡户号
				ds = enterprisePay(dpRequest, payCardNo);// 交易
			} else if ("1".equals(payType)) {// POS
				ds = posPay(dpRequest);// 交易
			} else if ("2".equals(payType)) {// 代收付

			}

			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			String responseDesc = null;
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}

//			String transSeq = ds.getByID("6903", "690");
			String transSeq = ds.getByID("4002", "401");
			// TMNNUMNO, OUTTMNNUMNO, ORDERSEQ, TRANSSEQ, SYSTEMNO, TXNAMOUNT,
			// CASHTYPE, CASHORDER, REMARK1, REMARK2
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dpRequest.getTmnNumNo(),
					dpRequest.getOrderSeq(), transSeq, dpRequest.getSystemNo(),
					dpRequest.getTxnAmount(), dpRequest.getCashType(),
					dpRequest.getCashOrder(), dpRequest.getRemark1(), dpRequest
							.getRemark2());
		} catch (XmlINFException spe) {

//			if (tInfOperInLog != null) {
//				// 插入信息到出站日志表
//				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
//						keep, ip, svcCode, responseCode, spe.getMessage(),
//						"S0A");
//			}
			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

//			if (tInfOperInLog != null) {
//				// 插入信息到出站日志表
//				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
//						keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
//			}
			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), infId);

		}
	}

	/**
	 * 调用SCS0001,企业账户支付
	 * 
	 * @param dpRequest
	 * @param payCardNo
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet enterprisePay(DpInf06102Request dpRequest,
			String payCardNo) throws Exception {

		String areaCode = TCumInfoDaoTemp.queryAreacodeByCustCode(dpRequest
				.getCustCode());// 所属区域编码
		String newActionCode = "11010001";// 11010001
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", dpRequest.getTmnNumNo());// 受理终端号
		g401.put("4008", dpRequest.getAcceptDate());// 受理时间
		g401.put("4012", "水电煤销账");// 订单描述，目前是硬编码
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode60To20());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 终端流水号
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		g401.put("4028", dpRequest.getOrderSeq());// 外部订单号

		g401.put("4284", dpRequest.getMerId());// 机构编码 //20130628 wanght
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", dpRequest.getTxnAmount());// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", dpRequest.getTxnAmount());// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4064", dpRequest.getCustCode());
		g404.put("4049", "0011");// 产品编码,固定
		g404.put("4051", newActionCode);// 业务编码
		String bisObject = dpRequest.getCashOrder();
		if (Charset.isEmpty(bisObject, true)) {
			bisObject = dpRequest.getCashNumber();
		} else {
			bisObject += dpRequest.getCashNumber();
		}
		g404.put("4052", bisObject);// 业务对象
		g404.put("4053", "1");// 业务数量
		// g404.put("4062", dpRequest.getCashOrder());// 销账单参考号
		g404.put("4062", dpRequest.getSystemNo());// 系统参考号1
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
		g407.put("4087", "SCS_ATTRLINE1");
		g407.put("4088", "4001");
		g407.put("4089", "销帐单号类型");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getCashType());// 0(条码) 1(缴费单号)
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE2");
		g407.put("4088", "4002");
		g407.put("4089", "扣款支付机构");
		g407.put("4091", "01");
		g407.put("4093", "4002");// 扣款支付机构
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE3");
		g407.put("4088", "4003");
		g407.put("4089", "扣款手机号/卡号");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getContactPhone());// 福建莆田，该字段为必填，填写该用户的客户号
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE4");
		g407.put("4088", "4004");
		g407.put("4089", "扣款密码");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getPayPassword());// 扣款密码
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_SERVID");
		g407.put("4088", "GTC402");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_DEALTYPE");
		g407.put("4088", "0200");// 交易代码
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		String cashOrder = dpRequest.getCashOrder();
		String flag = "0";
		if (Charset.isEmpty(cashOrder, true)) {
			flag = "1";
		}
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ACCTDATE");
		g407.put("4088", cashOrder);// 账期
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", flag);// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT0004");// 支付方式编码
		g408.put("4098", "110000");// 支付机构编码 86980001
		String channelCode = dpRequest.getChannelCode();
		if("80".equals(channelCode)){
			g408.put("4099", "0001");// 账户类型编码
		}else{
			g408.put("4099", "0007");// 账户类型编码
		}
		g408.put("4101", payCardNo);// 账号
		g408.put("4102", dpRequest.getPayPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", dpRequest.getTxnAmount());// 支付金额
		g408.put("4127", "110000");// 
		g408.put("4109", dpRequest.getNetworkNo());// 国际网络号
		g408.put("4119", "");
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}

	/**
	 * 调用SCS0001,POS支付
	 * 
	 * @param dpRequest
	 * @param payCardNo
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet posPay(DpInf06102Request dpRequest)
			throws Exception {

		String areaCode = TCumInfoDaoTemp.queryAreacodeByCustCode(dpRequest
				.getCustCode());// 所属区域编码
		String newActionCode = "11010001";// 11010001
		TCumAcctDao acctDao = new TCumAcctDao();
		String bankCode = acctDao.getBankCode(dpRequest.getBankAcct()); // 银行编码[通过银行帐号查询]
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4004", dpRequest.getCustCode());// 客户编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4006", areaCode);// 所属区域编码
		g401.put("4007", dpRequest.getTmnNumNo());// 受理终端号
		g401.put("4008", dpRequest.getAcceptDate());// 受理时间
		g401.put("4012", "水电煤销账");// 订单描述，目前是硬编码
		g401.put("4016", "LG001");// 客户登录认证方式编码：用户名
		g401.put("4144", dpRequest.getChannelCode60To20());// 渠道类型编码
		g401.put("4017", dpRequest.getKeep());// 终端流水号
		g401.put("4018", dpRequest.getTmnNum());// 操作原始来源
		g401.put("4028", dpRequest.getOrderSeq());// 外部订单号

		g401.put("4284", dpRequest.getMerId());// 机构编码 //20130628 wanght
		g401.endRow();

		// 订单费用信息
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4021", "0001");// 币种编码
		g402.put("4023", dpRequest.getTxnAmount());// 订单原始金额
		g402.put("4024", "0");// 订单优惠金额
		g402.put("4025", dpRequest.getTxnAmount());// 订单应付金额
		g402.endRow();

		// 业务单信息
		IParamGroup g404 = new ParamGroupImpl("404");
		g404.put("4047", "1");// 业务单序号
		g404.put("4064", dpRequest.getCustCode());
		// TODO 产品编码,固定
		g404.put("4049", "0011");// 产品编码,固定
		g404.put("4051", newActionCode);// 业务编码
		String bisObject = dpRequest.getCashOrder();
		if (Charset.isEmpty(bisObject, true)) {
			bisObject = dpRequest.getCashNumber();
		} else {
			bisObject += dpRequest.getCashNumber();
		}
		g404.put("4052", bisObject);// 业务对象
		g404.put("4053", "1");// 业务数量
		// g404.put("4062", dpRequest.getCashOrder());// 销账单参考号

		g404.put("4062", dpRequest.getSystemNo());// 系统参考号1
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
		g407.put("4087", "SCS_ATTRLINE1");
		g407.put("4088", "4001");
		g407.put("4089", "销帐单号类型");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getCashType());// 0(条码) 1(缴费单号)
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE2");
		g407.put("4088", "4002");
		g407.put("4089", "扣款支付机构");
		g407.put("4091", "01");
		// TODO 扣款支付机构
		g407.put("4093", "4002");// 扣款支付机构
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE3");
		g407.put("4088", "4003");
		g407.put("4089", "扣款手机号/卡号");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getContactPhone());// 福建莆田，该字段为必填，填写该用户的客户号
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE4");
		g407.put("4088", "4004");
		g407.put("4089", "扣款密码");
		g407.put("4091", "01");
		g407.put("4093", dpRequest.getPayPassword());// 扣款密码
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_SERVID");
		g407.put("4088", "GTC402");
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_CARDEFFDATE");
		// TODO 卡有效期
		g407.put("4088", "201306020");// 卡有效期
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_CARDCHECKDATE");
		// TODO 清算日期
		g407.put("4088", "201306018");// 清算日期
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_PRTNORGID");
		g407.put("4088", "10000001");// 代理机构标识
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		// g407.put("4047", "1");// 业务单序号
		// g407.put("4051", newActionCode);// 业务编码
		// g407.put("4087", "SCS_SENDORGID");
		// g407.put("4088", dpRequest.getCustCode());// 发送机构标识
		// g407.put("4089", "");
		// g407.put("4091", "");
		// g407.put("4093", "");
		// g407.put("4080", "0");// 控制标识
		// g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_PINDATA");
		// TODO 个人标识码数据 ??
		g407.put("4088", "1");// 个人标识码数据
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_PCICTRL");
		// TODO PCI号 ??
		g407.put("4088", "1");// PCI号
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_PHONENUM");
		g407.put("4088", dpRequest.getContactPhone());// 电话号码
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_CARDTYPE");
		g407.put("4088", dpRequest.getCardFlag());// 卡折标识
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识SCS_PHONENUM
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_BANKCARDTYPE");
		// g407.put("4088", dpRequest.getPrivateFlag());// 对公对私标识
		g407.put("4088", "1");// 对公对私标识
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ADDREASS");
		g407.put("4088", dpRequest.getContactAddr());// 联系地址
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", "1");// 控制标识
		g407.endRow();

		String cashOrder = dpRequest.getCashOrder();
		String flag = "0";
		if (Charset.isEmpty(cashOrder, true)) {
			flag = "1";
		}
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", newActionCode);// 业务编码
		g407.put("4087", "SCS_ACCTDATE");
		g407.put("4088", cashOrder);// 账期
		g407.put("4089", "");
		g407.put("4091", "");
		g407.put("4093", "");
		g407.put("4080", flag);// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g408 = new ParamGroupImpl("408");
		g408.put("4103", "1");// 扣款顺序号
		g408.put("4097", "PT1003");// 支付方式编码
		g408.put("4098", bankCode);// 支付机构编码
		g408.put("4099", "0001");// 账户类型编码
		g408.put("4101", dpRequest.getBankAcct());// 账号
		g408.put("4102", dpRequest.getPayPassword());// 支付密码
		g408.put("4021", "0001");// 币种编码
		g408.put("4104", dpRequest.getTxnAmount());// 支付金额
		g408.put("4109", dpRequest.getNetworkNo());// 国际网络号
		g408.put("4119", "");
		g408.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0001", g401, g402,
				g404, g405, g407, g408);

		// 返回结果
		return dataSet;

	}
}
