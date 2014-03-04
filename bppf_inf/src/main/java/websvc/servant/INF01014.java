package websvc.servant;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import common.utils.Charset;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf01014Request;
import common.xml.dp.DpInf01014Response;
import common.xml.dp.DpInf01015Request;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 
 * 本类描述: 实时验证接口
 * @version: 企业帐户前置接口 v1.0 
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email:  zhuxiaojun@tisson.com
 * @time: 2013-3-4上午11:02:36
 */
public class INF01014 {
	private static final Log log = LogFactory.getLog(INF01014.class);
	
	public static String svcInfName = "INF01014";

	public static String execute(String in0, String in1) {
		// TODO Auto-generated method stub
		DpInf01014Request dpRequest = null;
		DpInf01014Response response = new DpInf01014Response();
		RespInfo respInfo = null;

		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		String keep = "";//获取流水号
		String ip = "";
		String channelCode ="";
		String tmnNum ="";
		String svcCode = WebSvcTool.getSvcCode(in0);
		INFLogID infId = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			respInfo = new RespInfo(in1, "20");				// 返回信息头
			dpRequest = new DpInf01014Request(in1);
			channelCode = dpRequest.getChannelCode();
			tmnNum = dpRequest.getTmnNum();
			keep = dpRequest.getKeep();
			ip = dpRequest.getIp();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "transSeq"
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
			//判断有无权限
			boolean flag = false;
			List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode(), channelCode);
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if("ws_ColNPay_Payagent_Trueverify".equals(str)){
					flag=true;
					break;
				}
			}
			if (!flag) {
				throw new Exception("没有实时验证交易权限");
			}
			String busiType = dpRequest.getBusiType();
			dpRequest.setBusiType("00"+busiType);
			PackageDataSet ps = validPayment(dpRequest);
			String resultCode = (String) ps.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) ps.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}
			
			responseCode = ps.getByID("0001", "000");// 获取接口的000组的0001参数
			String responseDesc = ps.getByID("0002", "000");// 获取接口的000组的0002参数
			String txtAmout = "";
			String transSeq = "";
			
			if(dpRequest.getBusiType().equals("0002")){
				transSeq = ps.getByID("4002", "401");
				if(transSeq==null)
					transSeq = "";
				txtAmout ="1";
			}
			return response.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS",responseCode,
					responseDesc, dpRequest.getOrderSeq(),dpRequest.getCustCode(),dpRequest.getBankAcct(),busiType,
					txtAmout,transSeq,dpRequest.getRemark1(),dpRequest.getRemark2());
		} catch (XmlINFException spe) {
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, infId);
		} catch (Exception e) {
			
			return ExceptionHandler.toXMLERROR(
					new XmlINFException(response, e, respInfo), infId);
		}
	}

	private static PackageDataSet validPayment(DpInf01014Request dpRequest) throws Exception {
		
		IParamGroup g218 = new ParamGroupImpl("218");
		g218.put("2002", dpRequest.getCustCode());
		g218.put("2184", dpRequest.getBusiType());
		g218.endRow();
		
		IParamGroup g207 = new ParamGroupImpl("207");
		
		g207.put("2009", dpRequest.getCertType());
		g207.put("2010", dpRequest.getCertNo());
		g207.put("2158", dpRequest.getTransAccName());
		g207.put("2150", dpRequest.getAreacode());
		g207.put("2050", dpRequest.getBankCode());
//		g207.put("2050", "86610001");
		g207.put("4097", "PT1004");
		
		if(!Charset.isEmpty(dpRequest.getOpenBank()))
			g207.put("2051", dpRequest.getOpenBank());
		g207.put("2151", dpRequest.getCardFlag());
		g207.put("2152", dpRequest.getPrivateFlag());
		if(!Charset.isEmpty(dpRequest.getTel()))
		    g207.put("2154", dpRequest.getTel());
		if(!Charset.isEmpty(dpRequest.getValidity()))
		    g207.put("2156", dpRequest.getValidity());
		if(!Charset.isEmpty(dpRequest.getCvn2()))
		    g207.put("2157", dpRequest.getCvn2());
		g207.put("2159", dpRequest.getBankAcct());
		g207.endRow();
		
		IParamGroup g211 = new ParamGroupImpl("211");
		
		g211.put("2076", dpRequest.getChannelCode());
		g211.put("2077", dpRequest.getTmnNum());
		g211.put("2078", dpRequest.getCustCode());
		
		
		IServiceCall caller = new ServiceCallImpl();
		return caller.call("BIS","CUM0016",g218,g207,g211);
	}
}
