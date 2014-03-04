package websvc.servant;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TPhoneAreaDao;
import common.dao.TPnmPartnerDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.utils.MathTool;
import common.utils.OrderConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02044Request;
import common.xml.dp.DpInf02044Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02044 {

	private static final Log logger = LogFactory.getLog(INF02044.class);

	public static String svcInfName = "INF02044";

	public static String execute(String in0, String in1) {
		
		DpInf02044Request dpRequest = null;

		DpInf02044Response resp = new DpInf02044Response();

		RespInfo respInfo = null;

		String responseCode = "";

		logger.info("INF02044请求参数：：" + in1);

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf02044Request(in1);
			
			respInfo = new RespInfo(in1, dpRequest.getChannelCode());
			
			// 客户端MD5校验--------------------------------------------
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(),
					tokenValidTime);
			dpRequest.verifyByMD5(md5Key);
			TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());
			// -------------------------------------------------------------------

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(), dpRequest.getIp(), dpRequest.getTmnNum(),
					svcCode, "XML", "", "", "", "", OrderConstant.S0A);
			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {

				boolean flag = manager.selectTInfOperInLogByKeep(dpRequest.getKeep());
				// 判断流水号是否可用
				if (flag) {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);

					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			
			SignBankManage manage = new SignBankManage();
			String custId = manage.getCustIdByCode(dpRequest.getCustCode());
			if (null == custId || "".equals(custId)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
						INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
			}


			PackageDataSet ds = null;
			if (Double.valueOf(dpRequest.getFaceAmount()) < 1) {
				throw new Exception("面值不能少于1分钱");
			}

			String Amount = dpRequest.getFaceAmount();
			
			// 单位转换：分转元
			dpRequest.setFaceAmount(MathTool.pointToYuan(Amount));
			
			String areaCode = "";
			
			String prodCode = dpRequest.getProdCode();
			
			if("09010001".equals(dpRequest.getActionCode())){
				
				// 卡类型转换
				dpRequest.setProdCode(convertCardType(dpRequest.getProdCode(), dpRequest.getFaceAmount()));

				// 核心面值
				String faceAmount = convertCardMount(dpRequest.getFaceAmount());

				// 产品编码
				prodCode = dpRequest.getProdCode() + faceAmount + "00";
				
				//通过客户编码查区域编码
				areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());

			}else{
				
				String phone = dpRequest.getPhone().substring(0, 7);
				
				areaCode =TPhoneAreaDao.getAreaByPhone(phone);
				
				// 产品编码
				prodCode =dpRequest.getActionCode()+areaCode;
			}
			
			ds = scs0201(dpRequest,prodCode,areaCode);// 

			String responseDesc = "";
			
			responseCode = ds.getByID("0001", "000");
			
			String concession ="0";
			
			if (Long.valueOf(responseCode) == 0) {
				
				
				responseDesc = ds.getByID("0002", "000");
				
				String flagStr = ds.getByID("4230", "423");
				
				if ("0003".equals(flagStr)) {
					
					concession = ds.getByID("4329", "423");
					
					if (concession == null || "".equals(concession)
							|| Double.valueOf(concession) == 0) {
						
						concession = "0";
					}
				}
			}

			//单位转换：元转分
			concession = MathTool.yuanToPoint(concession);
			
			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, Amount,
					concession, dpRequest.getRemark1(), dpRequest.getRemark2());

			return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);

		} catch (XmlINFException spe) {

			spe.setRespInfo(respInfo);

			String oXml = ExceptionHandler.toXML(spe, infId);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		} catch (Exception e) {

			String oXml = ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), infId);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		}
	}
	
	private static String convertCardMount(String amount) {

		String m = "01";

		if ("10".equals(amount)) {

			m = "01";
		} else if ("20".equals(amount)) {

			m = "02";
		} else if ("30".equals(amount)) {

			m = "03";
		} else if ("50".equals(amount)) {

			m = "05";
		} else if ("100".equals(amount)) {

			m = "11";
		} else if ("200".equals(amount)) {

			m = "12";
		} else if ("300".equals(amount)) {

			m = "13";
		} else if ("500".equals(amount)) {

			m = "15";
		} else if ("1000".equals(amount)) {

			m = "21";
		}

		return m;
	}
	
	private static String convertCardType(String cardType, String faceAmount) {

		String type = cardType;

		if ("1001".equals(cardType)) {// 电信
			if ("30".equals(faceAmount)) {
				type = "0100";
			} else {
				type = "0102";
			}
		} else if ("2003".equals(cardType)) {// 天下通

			type = "0402";
		} else if ("1002".equals(cardType)) {// 联通

			type = "0320";
		} else if ("2004".equals(cardType)) {// 翼充卡

			type = "0501";
		}

		return type;
	}
	/**
	 * 
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet scs0201(DpInf02044Request dpRequest,
			String prodCode,String areaCode) throws Exception {
		
		TPnmPartnerDao dao = new TPnmPartnerDao();
		Map<String, String> map = dao.getPrntnCodeAndPrntType(dpRequest
				.getCustCode());
		
		IParamGroup g423 = new ParamGroupImpl("423");
		g423.put("4230", "0003"); 	//溢价
		g423.put("2011", dpRequest.getMerId());	//接入机构的对应的商户编码
		g423.put("4330", map.get("PRTN_CODE"));	//实际做交易的商户编码
		g423.put("4331", map.get("PRTN_TYPE"));	//实际做交易的商户类型
		g423.put("2002", dpRequest.getCustCode());	//实际做交易的商户编码
		g423.put("4051", dpRequest.getActionCode());	//业务编码
		g423.put("4049", prodCode);	//产品编码
		g423.put("4098", "110000");  //银行编码
		g423.put("4006", areaCode);  //区域编码
		g423.put("4144", dpRequest.getChannelCode());  //渠道号
		g423.endRow();
		
		IParamGroup g402 = new ParamGroupImpl("402");
		g402.put("4025", dpRequest.getFaceAmount());	//订单金额
		
		g402.put("4099", "0007");	//账户类型编码

		// 组成数据包,调用SCS0201接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0201", g423, g402);
		
		

		// 返回结果
		return dataSet;

	}

}
