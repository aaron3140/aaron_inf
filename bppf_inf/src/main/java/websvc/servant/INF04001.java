package websvc.servant;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TInfDcoperlogDao;
import common.dao.TPhoneDao;
import common.entity.TInfOperInLog;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.utils.OrderConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf04001Request;
import common.xml.dp.DpInf04001Response;
import framework.exception.ExceptionHandler;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF04001 {
	private static final Log logger = LogFactory.getLog(INF04001.class);

	public static String svcInfName = "INF04001";

	public static String execute(String in0, String in1) {
		DpInf04001Request dpRequest = null;
		DpInf04001Response resp = new DpInf04001Response();
		RespInfo respInfo = null;
		String responseCode = "";
		logger.info("INF04001请求参数：：" + in1);
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String keep = "";// 获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);
		Long consumId = null;
		try {
			respInfo = new RespInfo(in1, "40");
			dpRequest = new DpInf04001Request(in1);
			// 判断是否是手机号码
			if (!isMobileNO(dpRequest.getPhone())) {
				// String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(),
				// respInfo.getRespType(), respInfo.getKeep(), "ERROR", "1",
				// "该号码不是有效手机号码！", "", "", "", "", "", "");
				// return ResponseVerifyAdder.pkgForMD5(oXml, "");
				throw new Exception("该号码不是有效手机号码！");
			}
			// 手机获取工号
			TPhoneDao phoneDao = new TPhoneDao();
			List list = phoneDao.getStaffId(dpRequest.getPhone());
			if (list == null) {
				throw new Exception("该手机号未开通企业账户!");
			}
			if (list.size() > 1) {
				throw new Exception("该手机对应多个企业账户!");
			}
			String staffId = (String) ((Map) list.get(0)).get("STAFF_ID");
			// 获取客户编码
			String cust_code = PayCompetenceManage.getCustCodeByStaff(staffId);

			// 通过客户编码查询接入机构编码
			String merId = PayCompetenceManage
					.getOrgMerIdFromCustCode(cust_code);

			// 关联机构验证
//			if (!TCumInfoDao.verifyMerIdCustCode(cust_code,dpRequest.getMerId())) {
//
//				if (TCumInfoDao.getMerIdByCustCode(cust_code,dpRequest.getMerId()))
//
//					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
//							INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
//			}
			// 获取终端
			String tmnNum = PayCompetenceManage.getTmnNumFromMerId(merId);

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS", "000000", "成功",
					merId, tmnNum, dpRequest.getChannelCode(), dpRequest
							.getOrderNo(), cust_code, staffId);
			return ResponseVerifyAdder.pkgForMD5(oXml, "");
		} catch (XmlINFException spe) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, spe.getMessage(),
						OrderConstant.S0A);
			}
			spe.setRespInfo(respInfo);

			String oXml = ExceptionHandler.toXML(spe, infId);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		} catch (Exception e) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, e.getMessage(),
						OrderConstant.S0A);
			}
			String oXml = ExceptionHandler.toConsumeXML(new XmlINFException(
					resp, e, respInfo), infId, consumId);

			return ResponseVerifyAdder.pkgForMD5(oXml, null);

		}
	}

	/**
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static void main(String[] args) {
		System.out.println(isMobileNO("18545676543"));
	}

}
