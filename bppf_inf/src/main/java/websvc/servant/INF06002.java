package websvc.servant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumInfoDaoTemp;
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
import common.service.TransManage;
import common.utils.ChannelCode;
import common.utils.PrivConstant;
import common.utils.SagUtils;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf06002Request;
import common.xml.dp.DpInf06002Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 邱亚建 时间：2013-6-6 上午10:23:03 类描述：地市查询接口
 */
public class INF06002 {
	private static final Log logger = LogFactory.getLog(INF06002.class);

	public static String svcInfName = "INF06002";

	public static String executeForMD5(String in0, String in1) {

		DpInf06002Response resp = new DpInf06002Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf06002Request dpRequest = new DpInf06002Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {

				String tokenValidTime = TSymSysParamDao.getTokenValidTime();

				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest
						.getStaffCode(), tokenValidTime);

				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);

				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest
						.getStaffCode());

			}

			String oldXml = execute(in0, in1);

			return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);

		} catch (Exception e) {
			String oldXml = ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), null);

			return ResponseVerifyAdder.pkgForMD5(oldXml, null);
		}

	}

	@SuppressWarnings("unchecked")
	public static String execute(String in0, String in1) {
		DpInf06002Request dpRequest = null;

		DpInf06002Response resp = new DpInf06002Response();

		RespInfo respInfo = null;

		String responseCode = "";

		logger.info("INF06002请求参数：：" + in1);

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String keep = "";// 获取流水号

		String ip = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf06002Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			tmnNum = dpRequest.getTmnNum();

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum,
					svcCode, "XML", "", "", "", "", "S0A");
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

			TransManage transManage = new TransManage();

			// IPOS处理
			if (ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())) {

				Map<String, String> map = transManage
						.getCustCodeByExtTermNumNo(dpRequest.getTmnNumNo());
				if (map != null && map.size() != 0) {
					String custCode = map.get("CUST_CODE");
					String tmnNumNo = map.get("TERM_CODE");
					dpRequest.setCustCode(custCode);
					dpRequest.setTmnNumNo(tmnNumNo);
				} else {
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.CUSTCODE_NOT_MATCH_DESC);
				}
				/*
				 * String cust =
				 * transManage.getCustCodeByExtTermNumNo(dpRequest.
				 * getTmnNumNo()); if (cust == null || "".equals(cust)) { throw
				 * new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
				 * INFErrorDef.CUSTCODE_NOT_MATCH_DESC); } else {
				 * dpRequest.setCustCode(cust);
				 * dpRequest.setTmnNumNo(transManage
				 * .getTermNumNoByExt(dpRequest.getTmnNumNo()));
				 * 
				 * }
				 */
			} else {
				if (dpRequest.getTmnNumNo().length() < 12) {
					// dpRequest.setTmnNumNo(transManage.getTermNumNoByExt(dpRequest.getTmnNumNo()));
					String tmnNumNo = transManage.getTermNumNoByExt(dpRequest
							.getTmnNumNo(), dpRequest.getCustCode());
					if (tmnNumNo != null && !"".equals(tmnNumNo)) {
						dpRequest.setTmnNumNo(tmnNumNo);
					} else {
						throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
								INFErrorDef.TMNNUMNO_NOT_MATCH_DESC);
					}
				}
			}
			
			// 判断有无交易查询权限
			List privList = PayCompetenceManage.payFunc(
					dpRequest.getCustCode());
			boolean r = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.WS_ADDR_QUERY.equals(str)) {
					r = true;
					break;
				}

			}

			if (!r) {
				throw new Exception("你没有地市查询的权限");
			}

			// 验证客户编码和终端号是否对应
			// if(!ChannelCode.IPOS_CHANELCODE.equals(dpRequest.getChannelCode())){
			// boolean flag =
			// transManage.isCustCodeMatchTermNumNo(dpRequest.getCustCode(),
			// dpRequest.getTmnNumNo());
			// if (!flag) {
			// throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
			// INFErrorDef.CUSTCODE_NOT_MATCH_TERMNUMNO_DESC);
			// }
			// }

			PackageDataSet ds = null;
			ds = queryInfo(dpRequest);// 查询地市信息

			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			String responseDesc = null;
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}

			ArrayList paymentCodes = ds.getParamByID("6923", "692");
			ArrayList busCodes = ds.getParamByID("6929", "692");
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (int i = 0; i < ds.getParamSetNum("692"); i++) {
				Map<String, String> map = new HashMap<String, String>();
				list.add(map);
				map.put("PAYMENTCODE", (String) paymentCodes.get(i));
				map.put("BUSCODE", "00" + (String) busCodes.get(i));
				i++;
				map.put("PAYMENTNAME", (String) paymentCodes.get(i));
				map.put("BUSNAME", (String) busCodes.get(i));
			}
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, dpRequest.getCustCode(),
					dpRequest.getTmnNumNo(), dpRequest.getAccepTareaCode(),
					dpRequest.getAcceptDate(), list, dpRequest.getRemark1(),
					dpRequest.getRemark2());
		} catch (XmlINFException spe) {

//			if (tInfOperInLog != null) {
//				// 插入信息到出站日志表
//				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
//						keep, ip, svcCode, responseCode, spe.getMessage(),
//						"S0A");
//			}
			spe.setRespInfo(respInfo);

			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

//			if (tInfOperInLog != null) {
//				// 插入信息到出站日志表
//				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
//						keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
//			}
			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), infId);

		}
	}

	/**
	 * 调用SAG0001 查询地市信息
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet queryInfo(DpInf06002Request dpRequest)
			throws Exception {

		String areaCode = dpRequest.getAccepTareaCode();
		if (areaCode == null || "".equals(areaCode)) {
			areaCode = TCumInfoDaoTemp.queryAreacodeByCustCode(dpRequest
					.getCustCode());// 所属区域编码
			dpRequest.setAccepTareaCode(areaCode);
		}

		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String requestDate = df.format(new Date());
		df = new SimpleDateFormat("HHmmss");
		String requestTime = df.format(new Date());
		String tradeSeq = SagUtils.getSeqNbr("yyyyMMddhhmmssSSS", 8);
		/**
		 * 调用SAG0001,完成查询操作
		 */
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "GTC102");// 服务编码
		g675.put("6752", dpRequest.getChannelCode60To20());// 渠道号
		g675.put("6753", tradeSeq);// 流水号
		g675.put("6754", requestDate);// 发起请求日期，格式YYYYMMDD
		g675.put("6755", requestTime);// 发起请求时间，格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码，INF:表示INF前置平台 UPPS:标识交易核心平台 GOS：运营管理门户
		g675.endRow();

		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", "11010001");// 业务编码
		g676.put("6762", "0001");// 产品编码
		g676.put("6763", areaCode);// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();

		IParamGroup g680 = new ParamGroupImpl("680");
		df = new SimpleDateFormat("yyyyMMddhhmmss");
		g680.put("6801", "");
		g680.put("6802", "");
		g680.put("6803", "");
		g680.put("6804", "");
		g680.put("6805", "");
		g680.put("6806", "");
		g680.put("6807", "");
		g680.put("6808", df.format(new Date()));// 前向商户终端号
		g680.endRow();

		// 组成数据包,调用SAG0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller
				.call("SAG", "SAG0001", g675, g676, g680);

		// 返回结果
		return dataSet;

	}
}
