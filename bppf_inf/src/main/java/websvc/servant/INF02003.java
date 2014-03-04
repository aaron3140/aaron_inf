package websvc.servant;

import java.util.List;

import common.dao.TInfDcoperlogDao;
import common.dao.TVmClientDao;
import common.entity.TInfOperInLog;
import common.entity.VmClient;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.Charset;
import common.utils.DownloadTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf2003Request;
import common.xml.dp.DpInf2003Responset;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 版本管理接口
 * 
 * 
 */
public class INF02003 {
	public static String svcInfName = "02003";

	public static String execute(String in0, String in1) {
		//Long pk = null;
		DpInf2003Request request = null;
		RespInfo respInfo = null;				// 返回信息头

		String tmnNum = null;	//受理终端号
		
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "019999";
		String responseContent = "数据库没有配置版本信息";
		String keep = "";//获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		DpInf2003Responset resp = null;
		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			respInfo = new RespInfo(in1,"20");				// 返回信息头
			request = new DpInf2003Request(in1);
			tmnNum	= request.getTmnNum();
			String imsi = request.getImsi();
			String curVersion = request.getCurVersion();
			String system = request.getSystem();
			String sysVersion = request.getSysVersion();
			String phone = request.getPhone();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "IMSI"
					, imsi, "CURVERSION", curVersion, "S0A");
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
			
			/*boolean flag = false;
			List privList = PayCompetenceManage.payFunc(request.getCustCode(), request.getChannelCode());
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if("cln_VersionMng".equals(str)){
					flag = true;
				}
			}
			if (!flag) {
				throw new Exception("没有版本管理权限");
			}*/
			
			List<VmClient> vmClientList = null;

			String clientStyle = TVmClientDao.CLIENT_STYLE_NOR;
			
			
			String version = "";
			String isOptional = "";
			String url = "";
			String isForceup = "";//是否无条件强制客户端升级
			
			int count = TVmClientDao.checkIsExist(phone, system, sysVersion);
			
			if (count == 0) {
				//throw new INFException(MTPErrorDef.INF_NOClient_ERRCODE, "翼支付客户端尚不支持您的终端类型");
				phone = "default";
				sysVersion = "default";
				
			}
			
			vmClientList = TVmClientDao.getLatestClient(system, sysVersion, phone, clientStyle, "");
			List<VmClient> vmClientList2 = TVmClientDao.queryCurClient(curVersion, system, sysVersion, phone, clientStyle);
			
			VmClient vmClient2 = null;
			
			VmClient vmClient = new VmClient();
			String stat = "";
			String optionNal = "";
			if (vmClientList2!=null&&!vmClientList2.isEmpty()) {
				vmClient2 = vmClientList2.get(0);
				if (vmClient2 != null && !"".equals(vmClient2)) {
					stat = vmClient2.getStat();
				}
			}
			if (vmClientList!=null&&!vmClientList.isEmpty()) {
				// 判断stat值，给optionNal赋值
				if (!Charset.isEmpty(stat)) {
					if ("S0A".equals(stat)) {
						optionNal = "0";
					} else if ("S0P".equals(stat)) {
						optionNal = "1";
					} else if ("S0X".equals(stat)) {
						optionNal = "0";
					}
				} else {
					optionNal = "0";
				}
				
				vmClient = vmClientList.get(0);
				version = vmClient.getVersion();
				//版本相同时不需要
				if(vmClient2!=null&&compareToVersion(vmClient2.getVersion(),vmClient.getVersion())){
					
					optionNal = "2";
				}
				isOptional = optionNal;
				isForceup = "Y".equals(vmClient.getIsForceup()) ? "1" : "";
				url = DownloadTool.getClientUrl(vmClient.getFileId(), vmClient.getClientId());
				responseCode = "000000";
				responseContent = "成功";
			}
			
			//插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, responseContent, "S0A");
					
			
			// 返回结果
			resp = new DpInf2003Responset();
			
			String verDes = "";
			
			if(vmClient.getVersionDesc() !=null){
				
				verDes = vmClient.getVersionDesc();
				
				verDes = verDes.replaceAll("\r\n", "^");
			}
			String xmlStr=resp.toXMLStr(respInfo.getReqWebsvrCode(),respInfo.getRespType(),respInfo.getKeep(),"SUCCESS", 
					responseCode,responseContent, version, isOptional, url, isForceup,verDes);
			if (xmlStr==null||xmlStr.length()<1) {
				throw new Exception("获取账户信息出错");
			}
			return xmlStr;
		} catch (XmlINFException spe) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), id);
		}
	}
	
	public static boolean compareToVersion(String currentVersion, String maxVer) {

		currentVersion = currentVersion.replace(".", "_");

		maxVer = maxVer.replace(".", "_");
		
		String[] c = currentVersion.split("_");

		String[] m = maxVer.split("_");

		boolean res = false;

		int r = c.length - m.length;

		if (r == 0) {

			for (int i = 0; i < c.length; i++) {

				if (c[i].equalsIgnoreCase(m[i])) {

					res = true;
				} else {

					res = false;
					break;
				}
			}

		}

		return res;
	}
	
	
//	private static boolean isEmpty(Object o) {
//		if (o==null) {
//			return true;
//		}
//		if (o instanceof String) {
//			if (((String) o).trim().length()<1) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	/**
	 * 比较版本大小(currentVersion 与 minVer的大小)
	 * @param currentVersion 版本号
	 * @param minVer 数据表中升级计划设定的最低版本minVer
	 * @return 当 currentVersion >= minVer 返回true,否则返回false
	 */
//	public static boolean compareToVersion(String currentVersion,String minVer){
//		boolean isFlag = false;//当 currentVersion >= minVer 返回true,否则返回false
//		String[] currentVersionS = currentVersion.trim().replace(".", ",").split(",");
//		String[] minVerS = minVer.trim().replace(".", ",").split(",");
//		int currentVersionSLength = currentVersionS.length;
//		int minVersLength = minVerS.length;
//		int beforeNum  = 0;
//		int endNum = 0;
//		if(currentVersionSLength == minVersLength){
//			StringBuffer beforeString = new StringBuffer();
//			StringBuffer endString = new StringBuffer();
//			for (int i = 0; i < currentVersionSLength; i++) {
//				beforeString.append(currentVersionS[i]);
//				endString.append(minVerS[i]);
//				beforeNum = Integer.parseInt(beforeString.toString());
//				endNum = Integer.parseInt(endString.toString());
//				if(beforeNum > endNum){
//					isFlag = true;
//					break;
//				}
//			}
//		}else{
//			if (currentVersionSLength > minVersLength) {
//				for (int i = 0; i < minVersLength; i++) {
//					if (Integer.parseInt(currentVersionS[i]) < Integer.parseInt(minVerS[i])) {
//						isFlag = false;
//					}else{
//						isFlag = true;
//						break;
//					}
//				}
//			}else{
//				for (int i = 0; i < currentVersionSLength; i++) {
//					if (Integer.parseInt(currentVersionS[i]) >= Integer.parseInt(minVerS[i])) {
//						isFlag = true;
//					}else{
//						isFlag = false;
//						break;
//					}
//				}
//			}
//			
//		}		
//		return isFlag;
//	}
	
	public static String executeForMD5(String in0, String in1) {
		String oXml = execute(in0,in1);
		return ResponseVerifyAdder.pkgForMD5(oXml, null);
	}
}
