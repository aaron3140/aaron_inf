package websvc.servant;

import java.util.ArrayList;
import java.util.List;

import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TOppPreOrderDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.ChannelCode;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02006Request;
import common.xml.dp.DpInf02006Responset;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 付款单查询接口
 * 
 * 
 */
public class INF02006 {
	public static String svcInfName = "02006";

	public static String execute(String in0, String in1) {
		//Long pk = null;
		DpInf02006Request request  = null;
		RespInfo respInfo = null;				// 返回信息头

		String custCode = null; // 商户编码
		String tmnNum = null;	//受理终端号
		String channelCode= "";
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode1 = "";
		String keep = "";//获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		DpInf02006Responset resp = null;
		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			respInfo = new RespInfo(in1,"20");				// 返回信息头
			request = new DpInf02006Request(in1);
			channelCode = request.getChannelCode();
			custCode = request.getCustCode();
			tmnNum = request.getTmnNum();
			keep = request.getKeep();
			ip = request.getIp();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "agentCode"
					, custCode, "", "", "S0A");
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
			
//			boolean flag = false;
//			List privList = PayCompetenceManage.payFunc(custCode, channelCode);
//			for (int i = 0; i < privList.size(); i++) {
//				Map map = (Map)privList.get(i);
//				String str = map.get("PRIV_URL").toString();
//				if("cln_OrderListQuery".equals(str) && ChannelCode.AGENT_CHANELCODE.equals(channelCode)){
//					flag = true;
//				}else if("ws_OrderListQuery".equals(str)){
//					flag = true;
//				}
//			}
//			if (!flag) {
//				throw new Exception("没有交易查询列表权限");
//			}
			TOppPreOrderDao dao = new TOppPreOrderDao();
			List payBillList = dao.getPayBillList(custCode,request.getObjCode(),request.getStat());
			if (payBillList.size() < 1 ) {
				payBillList = new ArrayList();
			}
			// 返回结果
			resp = new DpInf02006Responset();
			String xmlStr=resp.toXMLStr(respInfo.getReqWebsvrCode(),respInfo.getRespType(),respInfo.getKeep(),"SUCCESS", 
					"000000","成功",payBillList);
			if (xmlStr==null||xmlStr.length()<1) {
				throw new Exception("获取账户信息出错");
			}
			return xmlStr;
		} catch (XmlINFException spe) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode1, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode1, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), id);
		}
	}
	
	public static String executeForMD5(String in0, String in1){
		DpInf02006Responset resp = new DpInf02006Responset();
		RespInfo respInfo = null;				// 返回信息头
		String md5Key = null;
		try {
			respInfo = new RespInfo(in1, "10");	
			DpInf02006Request dpRequest = new DpInf02006Request(in1);
			//客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				 md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(),
							tokenValidTime);
				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);
				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());
			}
			
			String oldXml = execute(in0, in1);
			
			return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);
		} catch (Exception e) {
			String oldXml= ExceptionHandler.toXML(new XmlINFException(
					resp, e, respInfo), null);
			
			return ResponseVerifyAdder.pkgForMD5(oldXml, null);
		}
	}
}
