package websvc.servant;
/*
 * 加密随机数下发接口
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf03002Request;
import common.xml.dp.DpInf03002Response;
import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF03002 {

	public static String svcInfName = "03002";

	private static final Log logger = LogFactory.getLog(INF03002.class);

	public static String execute(String in0, String in1) {

		DpInf03002Request dpRequest = null;
		
		DpInf03002Response resp = new DpInf03002Response();
		
		RespInfo respInfo = null;

		String responseCode = "";

		logger.info("请求参数：：" + in1);

		PackageDataSet ds = null;
		
		SagManager sagManager = new SagManager();
		
		TInfOperInLog tInfOperInLog = null;
		
		String keep = "";//获取流水号
		
		String ip = "";
		
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		String tmnNum = null;  
		
		INFLogID infId = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		
		try {

			respInfo = new RespInfo(in1, "20");

			dpRequest = new DpInf03002Request(in1);
			
			tmnNum = dpRequest.getTmnNum();
			
			keep = dpRequest.getKeep();
			
			ip = dpRequest.getIp();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", ""
					, "", "", "", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			//判断插入是否成功
			if(tInfOperInLog!=null){
				
				boolean flag = manager.selectTInfOperInLogByKeep(keep);
				//判断流水号是否可用
				if(flag){
					
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				}else{
					
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			
			ds = bt002(dpRequest);

			responseCode = ds.getByID("0001", "000");
			
			String responseDesc = ds.getByID("0002", "000");

			String cum = ds.getByID("2174", "217");

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, cum);

		} catch (XmlINFException spe) {

			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);
			
			return ExceptionHandler.toXML(spe, infId);
			
		}catch (Exception e) {

			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(
				resp, e, respInfo), infId);
			
		}

	}

	private static PackageDataSet bt002(DpInf03002Request dpRequest)
			throws Exception {

		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "CUM_RAND");
		g002.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM0014", g002);

		return dataSet;
	}
	

	public static String executeForMD5(String in0, String in1) {
		String oXml = execute(in0,in1);
		return ResponseVerifyAdder.pkgForMD5(oXml, null);
	}
}
