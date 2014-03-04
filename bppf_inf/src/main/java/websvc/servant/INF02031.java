package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.RegisterManger;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.Charset;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpINF02031Request;
import common.xml.dp.DpINF02031Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02031 {

	public static String svcInfName = "INF02031";

	private static final Log log = LogFactory.getLog(INF02031.class);

	public static String execute(String in0, String in1) {

		log.info("请求参数：：" + in1);

		String responseCode = "";

		String responseDesc = "";

		DpINF02031Request dpRequest = null;

		DpINF02031Response resp = new DpINF02031Response();

		RespInfo respInfo = null;

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID infId = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		RegisterManger manger = new RegisterManger();

		Long pid = 0L;

		Long oid = 0L;

		try {
			respInfo = new RespInfo(in1, "20");

			dpRequest = new DpINF02031Request(in1);

			// 客户端MD5校验--------------------------------------------
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(), tokenValidTime);
			dpRequest.verifyByMD5(md5Key);
			TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(), dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML", "", "", "", "", "S0A");

			TInfOperInLogManager man = new TInfOperInLogManager();

			// 判断插入是否成功
			if (tInfOperInLog != null) {

				boolean flag = man.selectTInfOperInLogByKeep(dpRequest.getKeep());
				// 判断流水号是否可用
				if (flag) {
					flag = man.updateAllow(tInfOperInLog.getOperInId(), 1);

					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE, INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {

					flag = man.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

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

			String phone = dpRequest.getPhone();//
			if(Charset.isEmpty(phone, true)){
				String staffCode = dpRequest.getStaffCode();
				log.info("手机号码为空，取默认工号 ["+staffCode+"] 对应的手机号码");
				 phone = TCumInfoDao.getMobileByStaffCode(staffCode);
				 if(Charset.isEmpty(phone, true)){
					 throw new INFException(INFErrorDef.STAFF_CODE_MOBILE,INFErrorDef.STAFF_CODE_MOBILE_DESC);
				 }
			}
			log.info("接收短信的手机号码为 phone="+phone +"  订单号为：orderSeq="+dpRequest.getTransSeq());

			// 调用SCS0005
			IParamGroup g002 = new ParamGroupImpl("002");// 包头
			g002.put("0021", "4002");
			g002.put("0022", dpRequest.getTransSeq());
			g002.endRow();

			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet packageDataSet = caller.call("SCS", "SCS0005", g002);// 组成交易数据包,调用SCS0005接口

			String resultCode = (String) packageDataSet.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) packageDataSet.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}

			responseCode = packageDataSet.getByID("0001", "000");// 获取接口的000组的0001参数
			responseDesc = packageDataSet.getByID("0002", "000");// 获取接口的000组的0002参数
			/*
			 * 短信接口
			 */

			// 返回响应码
			int count401 = packageDataSet.getParamSetNum("401");
			if (count401 == 0) {
				throw new Exception("无此订单信息");
			}

			String returnOrderTime = packageDataSet.getByID("4008", "401");// 订单受理时间

			String busiType = packageDataSet.getByID("E049", "404");// 订单类型/////////
			// 获取订单支付金额
			String returnOrderAmount = packageDataSet.getByID("4023", "402");
			// 获取卡序列号K015和卡密码K014
			int count692 = packageDataSet.getParamSetNum("692");
			boolean hasCardInfo = false;
			String cardPassword = "";
			String cardSeqNum = "";
			if (count692 != 0) {
				
				ArrayList list6920 = packageDataSet.getParamByID("6920", "692");
				ArrayList list6921 = packageDataSet.getParamByID("6921", "692");
				ArrayList list6923 = packageDataSet.getParamByID("6923", "692");
				for (int i = 0; i < list6920.size(); i++) {
					String key = (String) list6920.get(i);
					String value = (String) list6921.get(i);
					if ("K014".equals(key)&&"卡密码".equals(value)) {
						cardPassword = (String) list6923.get(i);
					} else if ("K015".equals(key)&&"卡序列号".equals(value)) {
						cardSeqNum = (String) list6923.get(i);
					}
				}
				if(!Charset.isEmpty(cardSeqNum,true) && !Charset.isEmpty(cardPassword,true)){
					log.info("订单号："+dpRequest.getTransSeq()  + "  有卡信息，cardSeqNum="+cardSeqNum + "   cardPassword="+cardPassword);
					hasCardInfo = true;
				}else{
					log.info("订单号："+dpRequest.getTransSeq()  + "  无卡信息");
				}
			}else{
				log.info("订单号："+dpRequest.getTransSeq()  + "  无卡信息");
			}

			String custName = TCumInfoDao.getCustName(custCode);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String date = sdf.format(new Date());
			String txnAmount = returnOrderAmount;
			Date tradeDate = sdf.parse(returnOrderTime);
			String tradeTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tradeDate);
			String transSeq = dpRequest.getTransSeq();

			PackageDataSet sendSCS = sendSCS(custCode, custName, date, cardSeqNum, cardPassword, txnAmount, tradeTime, transSeq, hasCardInfo, busiType, phone);

			responseCode = (String) sendSCS.getParamByID("0001", "000").get(0);
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(responseCode) == 0) {
				responseDesc = (String) sendSCS.getParamByID("0002", "000").get(0);
			}

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, dpRequest.getRemark1(),
					dpRequest.getRemark2());

			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		} catch (XmlINFException spe) {
			manger.delCusInfo(pid.toString(), oid.toString());
			spe.setRespInfo(respInfo);
			String oXml = ExceptionHandler.toXML(spe, infId);
			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		} catch (Exception e) {
			manger.delCusInfo(pid.toString(), oid.toString());
			String oXml = ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), infId);
			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		}

	}

	/**
	 * 短信接口
	 * 
	 * @param custCode
	 * @param custName
	 * @param date
	 * @param cardNo
	 * @param password
	 * @param txnAmount
	 * @param tradeTime
	 * @param transSeq
	 * @param busiType
	 * @param phone
	 * @throws Exception
	 */
	private static PackageDataSet sendSCS(String custCode, String custName, String date, String cardNo, String password, String txnAmount, String tradeTime, String transSeq,
			boolean hasCardInfo, String busiType, String phone) throws Exception {

		String cardInfo = "";
		String id = "1006";
		if (hasCardInfo) {
			cardInfo = "|CARD_NO###" + cardNo + "|CARD_PWD###" + password;
			id = "1007";
		}
		String content = "CUST_NAME###" + custName + "|DEAL_BAL###" + txnAmount + "|ORDER_TYPE###" + busiType + cardInfo + "|ORDER_CODE###" + transSeq + "|DEAL_TIME###"
				+ tradeTime;

		IParamGroup g416 = new ParamGroupImpl("416");
		g416.put("4231", id);
		g416.put("4252", "CUST_CODE");
		g416.put("4253", custCode);
		g416.put("4254", "001");
		g416.put("4255", "LT001");
		g416.put("4256", phone);
		g416.put("4258", content);
		g416.put("4259", date);
		g416.endRow();

		IServiceCall caller = new ServiceCallImpl();

		PackageDataSet dataSet = null;
		dataSet = caller.call("SCS", "SCS4003", g416);//SCS4003
		return dataSet;
	}

}
