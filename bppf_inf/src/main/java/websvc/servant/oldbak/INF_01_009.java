package websvc.servant.oldbak;

import java.net.SocketTimeoutException;
import java.util.List;

import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.TCumAcctManager;
import common.service.TInfOperInLogManager;
import common.utils.SagUtils;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf01009Request;
import common.xml.dp.DpInf01009Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson LML
 *  充值缴费类下单接口
 */
public class INF_01_009 {

	 public static String svcInfName = "01_009";

	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		//Long pk = null;
		DpInf01009Request dpSag0001Request = null;
		
		RespInfo respInfo = null;	// 返回信息头
		//String actionCode = null;   //业务编码
		String channelCode = null;// 交易渠道
		String termSeq = null;		// 外部系统充值缴费流水号
		String eventSeq = null;    //系统参考号
		String objCode = null;   //充值缴费对象标识
		String objType = null;	//充值缴费对象类型
		String payAmount = null;	//充值缴费金额
		String payTime = null;	//充值缴费时间
		String orgCode = null;	//充值缴费单位编码
		String callBackURL = null;	//回调地址
		List itemList = null;	//订单扩展属性
		String tmnNum = null;	//受理终端号
		String custCode = null;	//客户编码
		String merId = null;	//标识
		
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode="";
		
		DpInf01009Response resp = new DpInf01009Response();
		String keep = "";//		获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			
			dpSag0001Request = new DpInf01009Request(in1);
			
			respInfo = new RespInfo(in1, dpSag0001Request.getChannelCode());	// 返回信息头
			
			channelCode = dpSag0001Request.getChannelCode();
			termSeq = SagUtils.getSeqNbr("yyyyMMddhhmmssSSS", 8);
			eventSeq = dpSag0001Request.getEventSeq();
			objCode = dpSag0001Request.getObjCode();
			objType = dpSag0001Request.getObjType();
			payAmount = dpSag0001Request.getPayAmount();
			payTime = dpSag0001Request.getPaytime();
			orgCode = dpSag0001Request.getOrgCode();
			callBackURL = dpSag0001Request.getCallBackURL();
			itemList = dpSag0001Request.getItemList();
			tmnNum = dpSag0001Request.getTmnNum();
			merId = dpSag0001Request.getMerId();
			keep = dpSag0001Request.getKeep();
			ip = dpSag0001Request.getIp();
					
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "objCode"
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
			
			
			//查询银行卡号
			TCumAcctManager tCumAcctManager=new TCumAcctManager();
			String acctCode=tCumAcctManager.getAcctCode(custCode);
//			String acctCode="605810023222267949";
			/**
			 * 调用SCS0001,完成交易操作
			 */
			String areaCode="";
			PackageDataSet dataSet = sagManager.callSCS0001(keep, custCode,areaCode,eventSeq,tmnNum, payTime, channelCode
					, termSeq, objType, payAmount, objCode, orgCode, callBackURL,acctCode,dpSag0001Request);
			
			//判断返回结果
			String resultCode = (String) dataSet.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) dataSet.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}
			
			// 获取返回值
			String responseDesc = dataSet.getByID("0002", "000");		// 响应码描述
			String orderId = dataSet.getByID("4002", "401");			//交易订单号
			String tradeTime = dataSet.getByID("4010", "401");			//交易时间
	
			//插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, responseDesc, "S0A");
		
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(),
					"SUCCESS", responseCode, responseDesc, eventSeq, tradeTime, keep, orderId);
			
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
					sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, "调用接口超时", "S0A");
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
	
	
	
	
}

