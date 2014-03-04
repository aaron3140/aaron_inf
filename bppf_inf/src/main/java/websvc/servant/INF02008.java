package websvc.servant;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import common.utils.PasswordUtil;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02008Request;
import common.xml.dp.DpInf02008Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02008 {

	public static String svcInfName = "INF02008";

	private static final Log logger = LogFactory.getLog(INF02008.class);

	public static String execute(String in0, String in1) {

		logger.info("请求参数：：" + in1);

		DpInf02008Request dpRequest = null;

		DpInf02008Response resp = new DpInf02008Response();

		RespInfo respInfo = null;

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String responseCode = "";

		String responseDesc = "";

		String keep = "";// 获取流水号

		String ip = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		PackageDataSet ds = null;

		try {
			dpRequest = new DpInf02008Request(in1);
			String operType = dpRequest.getOperType();
			String md5Key = null;
			if("00".equals(operType)){
				// 客户端MD5校验--------------------------------------------
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(),
						tokenValidTime);
				dpRequest.verifyByMD5(md5Key);
				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());
				//-------------------------------------------------------------------
			}
			
//			判断有无交易查询权限
//			List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode());
//			boolean r = false;
//			for (int i = 0; i < privList.size(); i++) {
//				Map map = (Map)privList.get(i);
//				String str = map.get("PRIV_URL").toString();
//
//				if(PrivConstant.IPOS_PASSWORD_RESET.equals(str)||PrivConstant.WS_PASSWORD_RESET.equals(str)){
//					r = true;
//					break;
//				}
//
//			}
//			
//			if(!r){
//				throw new Exception("你没有修改密码的权限");
//			}

			respInfo = new RespInfo(in1, "20");

			tmnNum = dpRequest.getTmnNum();

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum,
					svcCode, "XML", "StaffCode", dpRequest.getStaffCode(),
					"VerifyCode", dpRequest.getVerifyCode(), "S0A");

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
			
			
			String newPassword = dpRequest.getNewPassword();
			String afftPassword = dpRequest.getAfftPassword();
			if(!StringUtils.equals(newPassword, afftPassword)){
				throw new INFException(INFErrorDef.PASSWORD_NOT_THE_SAME,INFErrorDef.PASSWORD_NOT_THE_SAME_DESC);
			}
			
			
			// 调核心接口
			if("00".equals(operType)){
				ds = cum2003(dpRequest);//修改
			}else if("01".equals(operType)){
//				String validTime = TSymSysParamDao.getVerifyValidTime();
//				String vCode = TInfLoginLogDao.getVerifyCode(dpRequest.getStaffCode(), dpRequest.getVerifyCode(), validTime);
				String vCodeValidTime = TSymSysParamDao.getVerifyValidTime();
				Map codeMap = TInfVaildateDao.getVCode(dpRequest.getStaffCode(),vCodeValidTime);

				if (codeMap==null||!dpRequest.getVerifyCode().equalsIgnoreCase((String)codeMap.get("VAL_CODE"))) {
					// 失败返回客户端
					throw new INFException(INFErrorDef.VERIFYCODE_ERROR,INFErrorDef.VERIFYCODE_ERROR_DESC);
				}
				ds = callCUM5004(dpRequest);//重置
			}

			responseCode = ds.getByID("0001", "000");

			responseDesc = ds.getByID("0002", "000");

			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep,
					ip, svcCode, responseCode, responseDesc, "S0A");
			
			//更新验证码无效
			TInfVaildateDao.updateVCode2(dpRequest.getStaffCode());

			String oXml = "";

			if (Long.valueOf(responseCode) != 0) {
				// 失败返回客户端
				oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
						.getRespType(), respInfo.getKeep(), "FAULT",
						responseCode, responseDesc, null, null);
			}else{
				oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
						.getRespType(), respInfo.getKeep(), "SUCCESS",
						responseCode, responseDesc, dpRequest.getRemark1(),
						dpRequest.getRemark2());
			}

			return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);
			

		} catch (XmlINFException spe) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, spe.getMessage(),
						"S0A");
			}
			spe.setRespInfo(respInfo);

			String oXml =ExceptionHandler.toXML(spe, infId);
			
			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		} catch (Exception e) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			String oXml =ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), infId);
			
			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		}

	}


	/**
	 * 重置工号密码
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callCUM5004(DpInf02008Request dpRequest) throws Exception {
		
		String staff = dpRequest.getStaffCode();
		String passwordType = dpRequest.getPasswordType();
		String tmnNum = dpRequest.getTmnNum();
		String encryptPwd = PasswordUtil.encryptPwd(dpRequest.getNewPassword(), staff);
		
		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", staff);
		g200.put("2007", encryptPwd);
		g200.put("2173",passwordType);
		g200.endRow();
		
		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", dpRequest.getChannelCode());
		g211.put("2077", tmnNum);
		g211.put("2078", dpRequest.getCustCode());
		g211.put("2085", dpRequest.getIp());
		g211.endRow();
		
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM5004", g200, g211);
		
		return dataSet;
	}
	private static PackageDataSet cum2003(DpInf02008Request dpRequest) throws Exception {

		String staff = dpRequest.getStaffCode();

		String newPassword = dpRequest.getNewPassword();

		String passwordType = dpRequest.getPasswordType();

		String tmnNum = dpRequest.getTmnNum();

		IParamGroup g217 = new ParamGroupImpl("217");
		g217.put("2171", staff);
		g217.put("2007", newPassword);
		g217.put("C007", dpRequest.getPassword());
		g217.put("2172", "0001");
		g217.put("2173", passwordType);
		g217.endRow();

		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", dpRequest.getChannelCode());
		g211.put("2077", tmnNum);
		g211.put("2078", null);
		g211.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM2003", g217, g211);

		return dataSet;
	}


}
