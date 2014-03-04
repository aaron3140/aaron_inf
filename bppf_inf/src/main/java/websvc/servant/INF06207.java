package websvc.servant;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import common.service.TransManage;
import common.utils.ChannelCode;
import common.utils.PrivConstant;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf06207Request;
import common.xml.dp.DpInf06207Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF06207 {
	private static final Log logger = LogFactory.getLog(INF06207.class);

	public static String svcInfName = "INF06207";

	public static String execute(String in0, String in1) {
		DpInf06207Request dpRequest = null;

		DpInf06207Response resp = new DpInf06207Response();

		logger.info("请求参数：：" + in1);

		RespInfo respInfo = null;

		String responseCode = "";

		String responseDesc = "";

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String keep = "";// 获取流水号

		String ip = "";

		String orderSeq = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf06207Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			tmnNum = dpRequest.getTmnNum();

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			orderSeq = dpRequest.getOrderSeq();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "ORDERSEQ", orderSeq, "", "", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {

				boolean flag = manager.selectTInfOperInLogByKeep(keep);
				// 判断流水号是否可用
				if (flag) {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);

					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE, INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {

					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}


			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if (!(dpRequest.getTradeTime().matches(RegexTool.DATE_FORMAT))) {
				throw new Exception("输入日期格式不正确");
			}

			TransManage transManage = new TransManage();
			// IPOS处理
			if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())) {

				Map<String, String> map = transManage.getCustCodeByExtTermNumNo(dpRequest.getTmnNumNo());
				if (map != null && map.size() != 0) {
					String custCode = map.get("CUST_CODE");
					String tmnNumNo = map.get("TERM_CODE");
					dpRequest.setCustCode(custCode);
					dpRequest.setTmnNumNo(tmnNumNo);
				} else {
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_DESC);
				}
			} else {
				if (dpRequest.getTmnNumNo().length() < 12) {
					String tmnNumNo = transManage.getTermNumNoByExt(dpRequest.getTmnNumNo(), dpRequest.getCustCode());
					if (tmnNumNo != null && !"".equals(tmnNumNo)) {
						dpRequest.setTmnNumNo(tmnNumNo);
					} else {
						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.TMNNUMNO_NOT_MATCH_DESC);
					}
				}
			}

	         // 判断有无权限
            List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode());
            boolean r = false;
            for (int i = 0; i < privList.size(); i++) {
            Map map = (Map) privList.get(i);
            String str = map.get("PRIV_URL").toString();
            if (PrivConstant.EPOS_DG_BUS.equals(str)) {
            r = true;
            break;
            }
            }
            if (!r) {
            throw new Exception("你没有东莞公交操作权限");
            }
			// 关联机构验证
			/*if (!TCumInfoDao.verifyMerIdCustCode(dpRequest.getCustCode(), dpRequest.getMerId())) {
				if (TCumInfoDao.getMerIdByCustCode(dpRequest.getCustCode(), dpRequest.getMerId())) {
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
				}
			}*/

			// 原订单编码
			String oldtransseq = dpRequest.getTransseq();
			// 新返回的订单编码
			String transseq = "";

			// 充值冲正
			PackageDataSet ds = scs0102reverse(dpRequest);
			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) ds.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			} else {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
				transseq = (String) ds.getParamByID("4002", "401").get(0);
			}

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, oldtransseq, orderSeq,
					dpRequest.getTmnNumNo(), dpRequest.getStaffCode(), transseq, dpRequest.getRemark1(), dpRequest.getRemark2());
		} catch (XmlINFException spe) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), infId);

		}
	}

	/**
	 * 调用SCS0102 完成充值冲正
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet scs0102reverse(DpInf06207Request dpRequest) throws Exception {

		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4002", dpRequest.getTransseq()); // 原交易流水号
		g401.put("4230", "0001"); // 操作类型
		g401.put("4144", dpRequest.getChannelCode60To20()); // 渠道类型编码
		g401.put("4007", dpRequest.getTmnNum()); // 终端号
		g401.put("4017", dpRequest.getKeep()); // 终端流水号(本次冲正的keep)
		g401.put("4146", dpRequest.getStaffCode()); // 操作员
		g401.put("4152", dpRequest.getRemark1()); // 备注说明
		g401.put("4028", dpRequest.getOrderSeq());// 外部订单号
		g401.put("4142", "OT101"); // 操作类型编码
		g401.endRow();

		IServiceCall caller = new ServiceCallImpl();
		return caller.call("SCS", "SCS0102", g401);
	}

}
