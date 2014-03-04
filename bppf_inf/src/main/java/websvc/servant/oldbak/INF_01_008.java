package websvc.servant.oldbak;

import java.net.SocketTimeoutException;
import java.util.List;

import common.dao.TInfDcoperlogDao;
import common.entity.ParamSAG0002;
import common.entity.TInfOperInLog;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.SagUtils;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf01008Request;
import common.xml.dp.DpInf01008Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson LML
 *  业务网关查询接口
 */
public class INF_01_008 {

	 public static String svcInfName = "01_008";

	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		//Long pk = null;
		DpInf01008Request dpSag0001Request = null;
		RespInfo respInfo = null;	// 返回信息头
		//String actionCode = null;   //业务编码
		String channelCode = null;// 交易渠道
		String objCode = null;    //用户标识
		String objType = null;   //用户标识类型
		String orgCode = null;	//鉴权单位代码
		List itemList = null;	//鉴权附加信息
		String merId = null;//前向商户编码
		String tnmNum = null;//前向商户终端号
		
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		DpInf01008Response resp = new DpInf01008Response();
		
		String keep = "";//		获取流水号
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			respInfo = new RespInfo(in1, "10");	// 返回信息头
			dpSag0001Request = new DpInf01008Request(in1);
			String tradeSeq = SagUtils.getSeqNbr("yyyyMMddhhmmssSSS", 8);
			merId = dpSag0001Request.getMerId();
			tnmNum = dpSag0001Request.getTmnNum();
			channelCode = dpSag0001Request.getChannelCode();
			objCode = dpSag0001Request.getTmnNum();
			objType = dpSag0001Request.getObjType();
			orgCode = dpSag0001Request.getOrgCode();
			itemList = dpSag0001Request.getItemList();
			keep = dpSag0001Request.getKeep();
			ip = dpSag0001Request.getIp();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tnmNum, svcCode, "XML", "objCode"
					, objCode, "orgCode", orgCode, "S0A");
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
			
			//写日志 
//			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
//					svcInfName, svcCode, SocketConfig.getSockIp(),
//					"KEEP", keep, "AGENTCODE", objCode);
//			id.setPk(pk);
			
			ParamSAG0002 params = packParamSAG0002(merId,tnmNum,tradeSeq, channelCode, objCode, objType, orgCode, itemList);
			// 调用接口SAG0001,完成业务查询
			PackageDataSet dataSet = sagManager.createSAG0002(params);
			String resultCode = (String) dataSet.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) dataSet.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}
			// 获取返回值
			responseCode = dataSet.getByID("0001", "000");		// 响应码
			String responseDesc = dataSet.getByID("0002", "000");		// 响应码描述
			String respEventSeq = dataSet.getByID("6901", "690");		// 系统参考号
	
			//插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, responseDesc, "S0A");
			
			//更新日志
			//TInfDcoperlogDao.update(pk, responseCode, responseDesc);
		
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(),
					"SUCCESS", responseCode, responseDesc, respEventSeq);
			
		} catch (XmlINFException spe) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			if (e instanceof SocketTimeoutException) {
				if(tInfOperInLog!=null){
					//插入信息到出站日志表
					sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
				}
				return ExceptionHandler.toXML(new XmlINFException(resp,new Exception("调用接口超时"), respInfo), id);
			}
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), id);
		}
		
	}
	
	/**
	 * 组装ParamSAG0002
	 * @param tradeSeq
	 * @param channelCode
	 * @param objCode
	 * @param objType
	 * @param orgCode
	 * @param itemList
	 * @return
	 */
	private static ParamSAG0002 packParamSAG0002(String merId,String tnmNum,String tradeSeq,String channelCode,String objCode,String objType,String orgCode ,List itemList) {
		ParamSAG0002 obj = new ParamSAG0002();
		obj.setMerid(merId);
		obj.setTnmNum(tnmNum);
		obj.setServCode("PCR001");
		obj.setChannelCode(channelCode);
		obj.setTradeSeq(tradeSeq);
		obj.setInfCode("INF");
		obj.setActionCode("02010001");
		obj.setProdCode("0001");
		obj.setReceiverCode("000000");
		obj.setObjCode(objCode);
		obj.setObjType(objType);
		obj.setOrgCode(orgCode);
		obj.setItemList(itemList);
		return obj;
	}
	
}

