package websvc.servant;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.service.SplitMapper;
import common.service.TInfOperInLogManager;
import common.utils.ChannelCode;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf05105Request;
import common.xml.dp.DpInf05105Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF05105 {

	private static final Log logger = LogFactory.getLog(INF05104.class);

	public static String svcInfName = "INF05104";
	
	public static String executeForMD5(String in0, String in1) {

		DpInf05105Response resp = new DpInf05105Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf05105Request dpRequest = new DpInf05105Request(in1);

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
		DpInf05105Request dpRequest = null;

		DpInf05105Response resp = new DpInf05105Response();

		RespInfo respInfo = null;

		logger.info("INF05104请求参数：：" + in1);

//		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

//		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID infId = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		Long consumId = null;
		
		try {

			dpRequest = new DpInf05105Request(in1);
			
			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 插入信息到入站日志表
//			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(), "", dpRequest.getTmnNum(), svcCode, "XML", "", "", "", "", OrderConstant.S0A);
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

			String resultCode = "000000";

			String responseDesc = "成功";
			
			SplitMapper mapper = new SplitMapper();
			
			List<Map<String,Object>> list = mapper.querySplit(dpRequest);
			
			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", resultCode, responseDesc,list);
			
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
}
