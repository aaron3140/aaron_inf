package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.ChannelCode;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpBankAccountInfoRequest;
import common.xml.dp.DpBankAccountInfoResponset;
import common.xml.dp.DpCardAccountInfoResponset;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 客户银行信息查询接口
 * 
 * 
 */
public class INF02002 {
	public static String svcInfName = "02002";

	public static String execute(String in0, String in1) {
		//Long pk = null;
		DpBankAccountInfoRequest bankAccountInfoRequest = null;
		RespInfo respInfo = null;				// 返回信息头

		String agentCode = null; // 客户编码
		String tmnNum = null;	//受理终端号
		String channelCode= "";
		
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode1 = "";
		String keep = "";//获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		DpBankAccountInfoResponset resp = null;
		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			respInfo = new RespInfo(in1,"20");				// 返回信息头
			bankAccountInfoRequest = new DpBankAccountInfoRequest(in1);

			agentCode = bankAccountInfoRequest.getCustCode();
			tmnNum = bankAccountInfoRequest.getTmnNum();
			keep = bankAccountInfoRequest.getKeep();
			ip = bankAccountInfoRequest.getIp();
			channelCode = bankAccountInfoRequest.getChannelCode();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "agentCode"
					, agentCode, "", "", "S0A");
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
			
			boolean flag = false;
			List privList = PayCompetenceManage.payFunc(bankAccountInfoRequest.getCustCode());
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if(("cln_BankInfoQuery".equals(str)||"ipos_BankInfoQuery".equals(str)) && ChannelCode.AGENT_CHANELCODE.equals(channelCode)){
					flag = true;
				} 
			}
			if (!flag) {
				throw new Exception("没有授权银行查询权限");
			}
			
			// 写日志
//			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
//					svcInfName,"INF_01_001", SocketConfig.getSockIp(),
//					"AGENTCODE", agentCode);
//			id.setPk(pk);
			
			// 根据客户编码，调用CUM0003查询联系信息
			IParamGroup g0003_200 = new ParamGroupImpl("200"); // 包头
			g0003_200.put("2002",agentCode);
			g0003_200.endRow();

			IParamGroup g0003_002 = new ParamGroupImpl("002"); // 包头
			g0003_002.put("0011","207");
			g0003_002.endRow();
			
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet dataSet = caller.call("BIS","CUM0003", g0003_200,g0003_002);// 组成交易数据包,调用CUM0003接口
			
			String responseCode = dataSet.getByID("0001","000");	// 获取接口的000组的0001参数
			// 返回响应码
			String responseContent = dataSet.getByID("0002","000");	// 获取接口的000组的0002参数
			// 获取207组卡户号
			int count = dataSet.getParamSetNum("207");
			String bankAcctNbr = "";
			String bankName = "";
			String bankCode = "";
			String bankAcctName = "";
			String privateflag = "";
			for (int i = 0; i < count; i++) {
				// 获取卡户类型
				String cardAcctType=(String)dataSet.getParamByID("2048","207").get(i);
				// 获取天讯卡户号
				if("ACCT001".equals(cardAcctType)){
					bankAcctNbr=(String)dataSet.getParamByID("2049","207").get(i);
					bankName=(String)dataSet.getParamByID("C050","207").get(i);
					bankCode=(String)dataSet.getParamByID("2050","207").get(i);
					bankAcctName=(String)dataSet.getParamByID("2051","207").get(i);
					break;
				}
			}
			// 获取216组卡户号
			int count216 = dataSet.getParamSetNum("216");
			for (int i = 0; i < count216; i++) {
				String privateType = (String)dataSet.getParamByID("2069","216").get(i);
				if(privateType.equals("2570")){
					privateflag = (String)dataSet.getParamByID("2071", "216").get(i);
					if(privateflag.equals("1"))
						privateflag = "对私";
					else
					    privateflag = "对公";
					break;
				}
			}
			
			//插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode1, responseContent, "S0A");

			// 更新日志
			// TInfDcoperlogDao.update(pk, responseCode, responseContent);
			
			String responTime=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());// 查询时间
			
			
			// 返回结果
			resp = new DpBankAccountInfoResponset();
			String xmlStr=resp.toXMLStr(respInfo.getReqWebsvrCode(),respInfo.getRespType(),respInfo.getKeep(),"SUCCESS", 
					responseCode,responseContent, bankAcctNbr, bankName,bankCode, bankAcctName,privateflag);
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
	
	public static String executeForMD5(String in0, String in1){
		DpCardAccountInfoResponset resp = new DpCardAccountInfoResponset();
		RespInfo respInfo = null;				// 返回信息头
		String md5Key = null;
		try {
			respInfo = new RespInfo(in1, "10");	
			DpBankAccountInfoRequest dpRequest = new DpBankAccountInfoRequest(in1);
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
