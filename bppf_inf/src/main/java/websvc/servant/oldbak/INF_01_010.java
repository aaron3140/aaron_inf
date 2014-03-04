package websvc.servant.oldbak;

import java.net.SocketTimeoutException;

import common.algorithm.MD5;
import common.dao.TInfDcoperlogDao;
import common.dao.TPnmAttrDao;
import common.entity.ParamSAG0006;
import common.entity.TInfOperInLog;
import common.platform.provider.bean.SocketConfig;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.DateTime;
import common.utils.SubmitForm;
import common.xml.RespInfo;
import common.xml.dp.DpInf01010Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author Tisson LML
 *  业务网关回调
 */
public class INF_01_010 {

	public static String svcInfName = "01_010";

	public static String execute(String C0_OrderId,String C1_SerNum,String C2_Code,
			String C3_InOrderNo,String C4_Mobile,String C5_TxnAmt,
			String C6_ReturnCode,String C7_ResponseCode,String callBackMsg) {
		Long pk = null;
		RespInfo respInfo = null;	// 返回信息头
		SagManager sagManager = new SagManager();
		DpInf01010Response resp = new DpInf01010Response();
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		TPnmAttrDao dao = new TPnmAttrDao();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		try{
			/**
			 * 业务处理
			 */
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(C1_SerNum, "", "", "01_010", "XML", "AGENTCODE"
					, C2_Code, "", "", "S0A");
			
			TInfOperInLogManager manager = new TInfOperInLogManager();
			//判断插入是否成功
			if(tInfOperInLog!=null){
				boolean flag = manager.selectTInfOperInLogByKeep(C1_SerNum);
				//判断流水号是否可用
				if(flag){
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				}else{
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			//验证商户平台流水号唯一性
//			boolean exist = WebSvcTool.checkTradeSeqAndAgent(C1_SerNum, C2_Code);
//			if(exist){
//				throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
//						INFErrorDef.POSSEQNO_CONFLICT_REASON);
//			}
			
			//写日志 
			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
					svcInfName, "SAG0006", SocketConfig.getSockIp(),
					"TRADESEQ", C1_SerNum, "AGENTCODE", C2_Code);
			id.setPk(pk);
			
			//组装ParamSAG0006
			ParamSAG0006 params = packParamSAG0006(C1_SerNum, C0_OrderId, callBackMsg);
			// 调用接口SAG0001,完成业务查询
			PackageDataSet dataSet = sagManager.createSAG0006(params);
			//判断返回结果
			String resultCode = (String) dataSet.getParamByID("0001", "000").get(0);
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) dataSet.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}
			// 获取返回值
			responseCode = dataSet.getByID("0001", "000");		// 响应码
			String responseDesc = dataSet.getByID("0002", "000");		// 响应码描述
			//String respEventSeq = dataSet.getByID("6902", "690");		// 商户系统流水号
			String callBackUrl = dataSet.getByID("6903", "690");		// 商户回调URL
			String custCode = dataSet.getByID("6904", "690");		// 客户编码
			
			//根据custCode获取key
			String keyValue = dao.getKeyByCustCode(custCode);
			//拼接入参，进行加密
			StringBuffer sb = new StringBuffer();
			sb.append(C0_OrderId).append("|").append(C1_SerNum).append("|").append(C2_Code).append("|").append(C3_InOrderNo).append("|")
				.append(C4_Mobile).append("|").append(C5_TxnAmt).append("|").append(C6_ReturnCode).append("|").append(C7_ResponseCode)
				.append("|").append(keyValue);
			String hmac = MD5.getMD5(sb.toString().getBytes());
			
			sb = new StringBuffer();
			sb.append("TERMSEQ=").append(C1_SerNum).append("&OBJCODE=").append(C4_Mobile)
				.append("&OBJTYPE=").append("1").append("&PAYAMOUNT=").append(C5_TxnAmt)
				.append("&PAYTIME=").append(DateTime.nowDate14Bit()).append("&RETUNRNCODE=").append(C6_ReturnCode)
				.append("&RESPONSECODE=").append(C7_ResponseCode).append("&HMAC=").append(hmac);
			
			//更新日志
			TInfDcoperlogDao.update(pk, responseCode, responseDesc);
			//插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), C1_SerNum, "", "01_010", responseCode, responseDesc, "S0A");
			
			SubmitForm sform=new SubmitForm();
			sform.setStrUrl(callBackUrl);
			sform.submitForm(sb.toString());
			
			if (sform.getResponseStr()==null||!sform.getResponseStr().trim().equals("success")) {
				sagManager.makeTask(callBackUrl,sb.toString(),"success");
			}
		
			return sform.getResponseStr();
//			return resp.toXMLStr("test", "10", "123456789",
//					"SUCCESS", responseCode, responseDesc, C1_SerNum,C4_Mobile,
//					 "1",  C5_TxnAmt,  DateTime.nowDate14Bit(),
//					 C6_ReturnCode,  C7_ResponseCode,  hmac);
			
		} catch (XmlINFException spe) {
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			if (e instanceof SocketTimeoutException) {
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), C1_SerNum, "", "01_010", responseCode, "调用接口超时", "S0A");
				return ExceptionHandler.toXML(new XmlINFException(resp,new Exception("调用接口超时"), respInfo), id);
			}
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), C1_SerNum, "", "01_010", responseCode, e.getMessage(), "S0A");
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), id);
		}
		
	}
	
	/**
	 * 组装ParamSAG0006
	 * @param tradeSeq
	 * @param channelCode
	 * @param callBackMsg
	 * @return
	 */
	private static ParamSAG0006 packParamSAG0006(String tradeSeq,String channelCode,String callBackMsg) {
		ParamSAG0006 obj = new ParamSAG0006();
		obj.setServCode("PFB001");
		obj.setChannelCode(channelCode);
		obj.setTradeSeq(tradeSeq);
		obj.setInfCode("INF");
		obj.setActionCode("02010001");
		obj.setProdCode("0001");
		obj.setReceiverCode("000000");
		obj.setCallBackMsg(callBackMsg);
		return obj;
	}
	
}

