package websvc.servant;

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
import common.utils.DateTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf02014Request;
import common.xml.dp.DpInf02014Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02014 {

	public static String svcInfName = "INF02014";

	private static final Log logger = LogFactory.getLog(INF02014.class);

	public static String execute(String in0, String in1) {

		logger.info("请求参数：：" + in1);

		DpInf02014Request dpRequest = null;

		DpInf02014Response resp = new DpInf02014Response();

		RespInfo respInfo = null;

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String responseCode = "";

		String responseDesc = "";

		String keep = "";// 获取流水号

		String ip = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		PackageDataSet ds = null;

		try {
			dpRequest = new DpInf02014Request(in1);

			// 判断有无交易查询权限
//			 List privList =PayCompetenceManage.payFunc(dpRequest.getCustCode(),dpRequest.getChannelCode());
//			 
//			 boolean r = false;
//			 
//			 for (int i = 0; i < privList.size(); i++) {
//				 Map map = (Map)privList.get(i);
//				 String str = map.get("PRIV_URL").toString();
//				
//				 if(PrivConstant.WS_TRADE_REFUND.equals(str)){
//					 r = true;
//					 break;
//				 }
//			
//			 }	
//			 if(!r){
//				 throw new Exception("你没交易退款的权限");
//			 }

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			tmnNum = dpRequest.getTmnNum();

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum,
					svcCode, "XML", "custCode", dpRequest.getCustCode(),
					"APPTRANSSEQ", dpRequest.getApptransSeq(), "S0A");

			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {

				boolean flag = manager.selectTInfOperInLogByKeep(keep);
				// 判断流水号是否可用
				if (flag) {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);

					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			// 订单信息是否正确，进行验证
			String result = validOrder(dpRequest);
			
			if(!"0".equals(result)){
				throw new Exception(result);
			}

			// 调核心接口
			ds = scs0102(dpRequest);

			responseCode = ds.getByID("0001", "000");

			responseDesc = ds.getByID("0002", "000");

			String orderSeq = ds.getByID("4002", "401");

			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep,
					ip, svcCode, responseCode, responseDesc, "S0A");

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dpRequest.getOrderSeq(),
					dpRequest.getCustCode(), dpRequest.getObjectCode(),
					orderSeq, dpRequest.getApptransSeq(), dpRequest
							.getTxnAmount(), dpRequest.getRemark1(), dpRequest
							.getRemark2());

			return oXml;

		} catch (XmlINFException spe) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, spe.getMessage(),
						"S0A");
			}
			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), infId);

		}

	}


	//判断付款方/付款方是否与原订单相符
	private static String validOrder(DpInf02014Request dpRequest) {
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021","4002");
		g002.put("0022",dpRequest.getApptransSeq());
		PackageDataSet dataSet = null;
		String resultCode="";
		String resultDesc="";
		try {
			IServiceCall caller = new ServiceCallImpl();
			dataSet=caller.call("SCS","SCS0005",g002);	// 组成SCS0001交易数据包
			resultCode=dataSet.getByID("0001","000");	// 获取SCS0001接口的000组的0001参数
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!resultCode.equals("0000")) {
			
			resultDesc = dataSet.getByID("0002","000");
			
			return resultDesc;
		}
		
		//获取401组数据
		int	count=dataSet.getParamSetNum("401");
			if(count>0){
				  String strdate=(String)dataSet.getByID("4008","401");
					if(!DateTool.beforOneDay(strdate)){
						return "需T+1后才能发起退款交易";
					}
			if(!"".equals(dpRequest.getObjectCode())){
				String code=(String)dataSet.getParamByID("4004","401").get(0);
				if(!code.equals(dpRequest.getObjectCode())){
					return "付款方与原订单不符";
				}
					}
		}
		
		//获取402组数据
		count=dataSet.getParamSetNum("402");
		if(count>0){
			String amount =(String)dataSet.getParamByID("4023","402").get(0);
				
			int d1 = (int)(Double.parseDouble(amount)*100);
			
			int d2 = Integer.parseInt(dpRequest.getTxnAmount());
			
			if(d1-d2!=0) {
				
				return "退款金额与原订单不符";
			}
		}
		//获取404组数据
		 count=dataSet.getParamSetNum("404");
		if(count>0){
			String code=(String)dataSet.getParamByID("4052","404").get(0);
			
			if(!code.equals(dpRequest.getCustCode())) {
				return "收款方与原订单不符";
			}
		}
		return "0";
	}
	
	private static PackageDataSet scs0102(DpInf02014Request dpRequest)
			throws Exception {

		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4002", dpRequest.getApptransSeq());// 订单编码
		g401.put("4230", "0005");// 退款
		g401.put("4144", dpRequest.getChannelCode());// 渠道类型编码
		g401.put("4007", dpRequest.getTmnNum());// 终端号
		g401.put("4017", dpRequest.getKeep());// 终端流水号
		g401.put("4028", dpRequest.getOrderSeq());// 外部订单号
		g401.put("4146", "00");// 操作员
		g401.put("4152", "");// 备注说明
		g401.put("4148", dpRequest.getMerId());// 操作机构编码
		g401.put("4142", "OT501");// 全单退款
		g401.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0102", g401);

		return dataSet;
	}
}
