package websvc.servant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.service.SagManager;
import common.service.SplitMapper;
import common.service.TInfOperInLogManager;
import common.utils.ChannelCode;
import common.utils.OrderConstant;
import common.utils.PasswordUtil;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf05104Request;
import common.xml.dp.DpInf05104Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF05104 {

	private static final Log logger = LogFactory.getLog(INF05104.class);

	public static String svcInfName = "INF05104";

	public static String executeForMD5(String in0, String in1) {

		DpInf05104Response resp = new DpInf05104Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf05104Request dpRequest = new DpInf05104Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {

				String tokenValidTime = TSymSysParamDao.getTokenValidTime();

				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest
						.getStaffCode(), tokenValidTime);

				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);

				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest
						.getStaffCode());
				
				//密码鉴权
				PasswordUtil.AuthenticationPassWord3(dpRequest, dpRequest.getStaffCode(), dpRequest.getPayPassword());

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
		DpInf05104Request dpRequest = null;

		DpInf05104Response resp = new DpInf05104Response();

		RespInfo respInfo = null;

		logger.info("INF05104请求参数：：" + in1);

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		Long consumId = null;

		try {

			respInfo = new RespInfo(in1, "20");

			dpRequest = new DpInf05104Request(in1);

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					"", dpRequest.getTmnNum(), svcCode, "XML", "", "", "", "",
					OrderConstant.S0A);
			TInfOperInLogManager manager = new TInfOperInLogManager();
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

			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if (!(dpRequest.getTradeTime().matches(RegexTool.DATE_FORMAT))) {

				throw new Exception("输入日期格式不正确");
			}
			// TransManage transManage = new TransManage();

			// 判断有无交易查询权限
			// List privList =
			// PayCompetenceManage.payFunc(dpRequest.getCustCode());
			// boolean r = false;
			// for (int i = 0; i < privList.size(); i++) {
			// Map map = (Map)privList.get(i);
			// String str = map.get("PRIV_URL").toString();
			//
			// if(PrivConstant.IPOS_RECHARGE_TELFARE.equals(str)){
			// r = true;
			// break;
			// }
			//
			// }
			//			
			// if(!r){
			// throw new Exception("你没有话费充值的权限");
			// }

			String resultCode = "000000";

			String responseDesc = "成功";

			SplitMapper mapper = new SplitMapper();

			mapper.saveSplit(dpRequest);

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS", resultCode,
					responseDesc, dpRequest.getOrderNo(), dpRequest
							.getRemark1(), dpRequest.getRemark2());

			return oXml;

		} catch (XmlINFException spe) {

			spe.setRespInfo(respInfo);

			String oXml = ExceptionHandler.toXML(spe, infId);

			return oXml;

		} catch (Exception e) {

			String oXml = ExceptionHandler.toConsumeXML(new XmlINFException(
					resp, e, respInfo), infId, consumId);

			return oXml;

		}
	}

}
