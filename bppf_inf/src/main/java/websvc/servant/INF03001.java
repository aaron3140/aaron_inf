package websvc.servant;

/*
 * 短信下发接口
 */
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfVaildateDao;
import common.dao.TSymStaffLogDao;
import common.entity.TInfOperInLog;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.HttpPostUtils;
import common.utils.SmsTool;
import common.utils.WebSvcTool;
import common.utils.verify.VCodeBuilder;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf03001Request;
import common.xml.dp.DpInf03001Response;

import framework.config.PayWapConfig;
import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF03001 {

	public static String svcInfName = "03001";

	private static final Log log = LogFactory.getLog(INF03001.class);

	public static String execute(String in0, String in1) {

		log.info("请求参数：：" + in1);

		String responseCode = "019999";

		String responseDesc = "发送短信失败";

		DpInf03001Request dpRequest = null;

		DpInf03001Response resp = new DpInf03001Response();

		RespInfo respInfo = null;

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		try {
			respInfo = new RespInfo(in1, "20");

			dpRequest = new DpInf03001Request(in1);

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(), dpRequest.getIp(), dpRequest.getTmnNum(),
					svcCode, "XML", "", "", "", "", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {

				boolean flag = manager.selectTInfOperInLogByKeep(dpRequest.getKeep());
				// 判断流水号是否可用
				if (flag) {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);

					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			String sendType = dpRequest.getSendType();
			if (!"2".equals(sendType)&&!TSymStaffLogDao.vertifyStaff(dpRequest.getStaffCode())) {

				throw new Exception("工号无效");
			}else if("2".equals(sendType)&&TCumInfoDao.isRegExistCust(dpRequest.getStaffCode())){
				throw new Exception("该手机号已经注册");
			}

			String v = new VCodeBuilder().generate();
			
			String vCode= null; //返回接口的验证码
			
			String msg = "你的翼支付登录验证码是: " + v;
			
			
			if ("0".equals(sendType)) {

				String mobile = TSymStaffLogDao.getMobile(dpRequest
						.getStaffCode());

				if (mobile != null && !"".equals(mobile)) {
					
					//发送短信
					int code = sendMessage(mobile,msg);
					
					if (code == HttpStatus.SC_OK) {
						
						//保存验证码
						saveVCode(dpRequest,v);
						
						responseCode = "000000";

						responseDesc = "成功";
					} 
				} else {
					throw new Exception("该用户不是合法用户或没有设置手机号码");
				}

			} else if ("1".equals(sendType)) {
				
				//保存验证码
				saveVCode(dpRequest,v);
				
				vCode = v;
				
				responseCode = "000000";

				responseDesc = "成功";

			} else if ("2".equals(sendType)) {
				
				msg = "你的翼支付注册验证码是: " + v;

				String mobile = dpRequest.getStaffCode();

				//发送短信
				int code = sendMessage(mobile,msg);

				if (code == HttpStatus.SC_OK) {

					//保存验证码
					saveVCode(dpRequest,v);
					
					responseCode = "000000";

					responseDesc = "成功";
				} 
			}
			
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
					dpRequest.getKeep(), dpRequest.getIp(), svcCode, responseCode, responseDesc, "S0A");
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, vCode);

		} catch (XmlINFException spe) {

//			if (tInfOperInLog != null) {
//				// 插入信息到出站日志表
//				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
//						dpRequest.getKeep(), dpRequest.getIp(), svcCode, responseCode, spe.getMessage(),
//						"S0A");
//			}

			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

//			if (tInfOperInLog != null) {
//				// 插入信息到出站日志表
//				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
//						dpRequest.getKeep(), dpRequest.getIp(), svcCode, responseCode, e.getMessage(), "S0A");
//			}
			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), infId);

		}

	}

	public static void saveVCode(DpInf03001Request dpRequest,String vCode){
		
		Map m = new HashMap();
		
		m.put("STAFFCODE", dpRequest.getStaffCode());
		
		m.put("VAL_CODE", vCode);
		
		Map map = TInfVaildateDao.getStaffByCode(dpRequest.getStaffCode());
		
		if(map!=null){
			
			TInfVaildateDao.updateVCode(m);
		}else{
			
			TInfVaildateDao.saveVCode(m);
		}
		
	}
	public static int sendMessage(String mobile,String msg){
		
		String url = PayWapConfig.getPAYWAP_URL();
		
		String account = PayWapConfig.getPAYWAP_ACCOUNT();
		
		String pwd = PayWapConfig.getPAYWAP_PASSWORD();
		
		SmsTool smstool = new SmsTool();
	
		log.info("mobile:" + mobile + "msg:" + msg);
		
		log.info(url);
		
		int code = 0;

		try {
			Document document = smstool.getDocument(pwd, mobile,
					msg);
			
			code = smstool.sendsms(account, url, document);

		} catch (Throwable e) {
			log.error(e);
		}
		
		
		return code;
	}
	public static String executeForMD5(String in0, String in1) {
		String oXml = execute(in0, in1);
		return ResponseVerifyAdder.pkgForMD5(oXml, null);
	}

	public static void main(String arg[]) {
		String mobile = "13556182709";
		String url = "http://132.97.117.206:7005/mis/msg.do?";
		String msg = "你的翼支付登录验证码是: " + 9980;
		String res = null;
		try {
			res = new HttpPostUtils(url).sendPostRequest(
					"method=receiveSms&type=2&mobile=" + mobile + "&&msg="
							+ msg, "utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("url：" + url + "\n msg: " + msg + "\n res: " + res);
	}
}
