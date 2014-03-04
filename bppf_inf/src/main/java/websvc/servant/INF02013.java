package websvc.servant;

import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymCustomDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02013Request;
import common.xml.dp.DpInf02013Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 快捷交易查询接口
 * 
 * 
 */
public class INF02013 {
	public static String svcInfName = "INF02013";

	public static String execute(String in0, String in1) {

		DpInf02013Request dbRequest = null;
		
		RespInfo respInfo = null;				// 返回信息头
		
		SagManager sagManager = new SagManager();
		
		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);
		
		DpInf02013Response resp = null;
		
		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		
		try {
			
			respInfo = new RespInfo(in1,"20");				// 返回信息头
			
			dbRequest = new DpInf02013Request(in1);
			
			// 客户端MD5校验--------------------------------------------
			String tokenValidTime = TSymSysParamDao.getTokenValidTime();
			String md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dbRequest.getStaffCode(),
					tokenValidTime);
			dbRequest.verifyByMD5(md5Key);
			TInfLoginLogDao.updateRanduseTimeByStaffCode(dbRequest.getStaffCode());
			//-------------------------------------------------------------------
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dbRequest.getKeep(), dbRequest.getIp(), dbRequest.getTmnNum(), svcCode, "XML", "agentCode"
					, dbRequest.getCustCode(), "", "", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			//判断插入是否成功
			if(tInfOperInLog!=null){
				boolean flag = manager.selectTInfOperInLogByKeep(dbRequest.getKeep());
				//判断流水号是否可用
				if(flag){
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				}else{
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			// 业务组件
			SignBankManage manage = new SignBankManage();
			
			// 获取客户ID
			String custId = manage.getCustIdByCode(dbRequest.getCustCode());
			if(custId == null) {
				throw new Exception("该商户号不存在");
			}
			
			TSymCustomDao buss = new TSymCustomDao();
			
			//单笔交易
			String perAmount = buss.getTh(custId, "T22", "TH004");
			//累计交易
			String allAmount = buss.getTh(custId, "T22", "TH005");
			//当月累计消费
			String allTrade = buss.getAmountCount(custId);
			
			//插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), dbRequest.getKeep(), dbRequest.getIp(), svcCode, "", "000000", "S0A");

			
			// 返回结果
			resp = new DpInf02013Response();
			
			String oXml=resp.toXMLStr(respInfo.getReqWebsvrCode(),respInfo.getRespType(),respInfo.getKeep(),"SUCCESS", 
					"000000","成功", perAmount, allAmount, allTrade,dbRequest.getRemark1(),dbRequest.getRemark2());
			
			return ResponseVerifyAdder.pkgForMD5(oXml, md5Key);

		} catch (XmlINFException spe) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), dbRequest.getKeep(), dbRequest.getIp(), svcCode, "", spe.getMessage(), "S0A");
			}
			
			spe.setRespInfo(respInfo);
			
			String oXml = ExceptionHandler.toXML(spe, id);
			
			return ResponseVerifyAdder.pkgForMD5(oXml, null);
			
		} catch (Exception e) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), dbRequest.getKeep(), dbRequest.getIp(), svcCode, "", e.getMessage(), "S0A");
			}
			String oXml = ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), id);
			
			return ResponseVerifyAdder.pkgForMD5(oXml, null);
		}
	}
}
