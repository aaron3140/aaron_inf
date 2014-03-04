package websvc.servant;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.service.RegisterManger;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf06009Request;
import common.xml.dp.DpInf06009Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF06009 {

	public static String svcInfName = "INF06009";

	private static final Log log = LogFactory.getLog(INF06009.class);

	public static String execute(String in0, String in1) {

		log.info("请求参数：：" + in1);

		String responseCode = "";

		String responseDesc = "";

		DpInf06009Request dpRequest = null;

		DpInf06009Response resp = new DpInf06009Response();

		RespInfo respInfo = null;

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		try {
			respInfo = new RespInfo(in1, "20");

			dpRequest = new DpInf06009Request(in1);

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"", "", "", "", "S0A");
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

			RegisterManger manger = new RegisterManger();

			List list = manger.getAgentByArea(dpRequest.getAreaCode(),dpRequest.getPdline());

			responseCode = "000000";
			responseDesc = "成功";

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, list);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		} catch (XmlINFException spe) {

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

	public static String executeForMD5(String in0, String in1) {
		String oXml = execute(in0, in1);
		return ResponseVerifyAdder.pkgForMD5(oXml, null);
	}

}
