package websvc.servant;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumAcctDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.PaymentTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf01021Request;
import common.xml.dp.DpInf01021Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

//签约绑定查询接口
public class INF01021 {
private static final Log logger = LogFactory.getLog(INF01021.class);
	
	public static String svcInfName = "INF01021";
	
	public static String executeForMD5(String in0, String in1){
		String oXml = execute(in0,in1);
		return ResponseVerifyAdder.pkgForMD5(oXml, null);
	}
	
	public static String execute(String in0,String in1){
		DpInf01021Request dpRequest = null;
		
		DpInf01021Response resp = new DpInf01021Response();
		
		RespInfo respInfo = null;

		String responseCode = "";

		logger.info("请求参数：：" + in1);
		
		SagManager sagManager = new SagManager();
		
		TInfOperInLog tInfOperInLog = null;
		
		String keep = "";//获取流水号
		
		String ip = "";
		
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		String tmnNum = null;  
		
		INFLogID infId = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		
		try {

			respInfo = new RespInfo(in1, "20");

			dpRequest = new DpInf01021Request(in1);
			
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
			//签约绑定查询权限判断
			boolean flag=false;
			List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode(),dpRequest.getChannelCode());
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if("ws_ColNPay_Sign_Search".equals(str)){                          
					flag = true;
				}
			}
			if (!flag) {
				throw new Exception("没有签约绑定查询权限");
			}
			PackageDataSet ds = null;
			ds = contractBind(dpRequest);
			
			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			String responseDesc = null;
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}
			
			String contractId = ds.getByID("2149", "207");
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dpRequest.getOrderSeq(),contractId,dpRequest.getRemark1(),dpRequest.getRemark2());
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

	private static PackageDataSet contractBind(DpInf01021Request dpRequest) throws Exception {

		TCumInfoDao dao = new TCumInfoDao();
		String prtnCode = dao.getPrtnCode(dpRequest.getCustCode());
		TCumAcctDao acctDao = new TCumAcctDao();
		String bankCode =acctDao.getBankCode(dpRequest.getBankAcct());    //银行编码
		/**
		 * 调用CUM0012,完成交易操作
		 */

		IParamGroup g218 = new ParamGroupImpl("218");
		g218.put("2011", prtnCode);
		g218.put("2187", dpRequest.getBranchName());//用户全称
		g218.endRow();

		IParamGroup g207 = new ParamGroupImpl("207");
		g207.put("2050", bankCode);//银行账户所属银行代码
		g207.put("2159", dpRequest.getBankAcct());//银行账户		
		g207.put("4097", "PT1004");//支付方式--银行代收（无磁无密）
		g207.endRow();
		
		// 组成数据包,调用CUM0012接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM0017", g218, g207);
		return dataSet;
	}
}
