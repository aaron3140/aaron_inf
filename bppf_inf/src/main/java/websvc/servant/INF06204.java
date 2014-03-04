package websvc.servant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.SagNoticeManager;
import common.service.TInfOperInLogManager;
import common.service.TransManage;
import common.utils.ChannelCode;
import common.utils.PrivConstant;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf06204Request;
import common.xml.dp.DpInf06204Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 2014年1月15日 下午2:08:57<br>
 * 
 *         本来描述：[东莞公交]开卡接口
 */
public class INF06204 {
	private static final Log logger = LogFactory.getLog(INF06204.class);

	public static String svcInfName = "INF06204";

	public static String execute(String in0, String in1) {
		DpInf06204Request dpRequest = null;

		DpInf06204Response resp = new DpInf06204Response();

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

			dpRequest = new DpInf06204Request(in1);

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
			// if (!TCumInfoDao.verifyMerIdCustCode(dpRequest.getCustCode(), dpRequest.getMerId())) {
			// if (TCumInfoDao.getMerIdByCustCode(dpRequest.getCustCode(), dpRequest.getMerId())) {
			// throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
			// }
			// }

			// 明细项内容
			String detailContent = "";
			// 开卡操作
			PackageDataSet ds = null;// callSCS0013(dpRequest);// 售卡
			//系统参考号
			String systemNo=dpRequest.getSystemNo();

			String operationType = dpRequest.getOperationType();
			// ATO 开卡 ATR 开卡充正
			if ("ATO".equals(operationType)) {
				ds = SagNoticeManager.openCard(dpRequest, true);
			} else if ("ATR".equals(operationType)) {
				// 先冲正业务后冲正支付
				// 调业务网关
				ds = SagNoticeManager.openCard(dpRequest, false);
			}

			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}
			detailContent = (String) ds.getParamByID("6923", "692").get(0);
			Map<String, String> map = null;
			if (detailContent != null && !detailContent.equals("")) {
				map = parserXml(detailContent);// 解析xml
			}
			if ("ATR".equals(operationType)) {
				// 调核心 冲正支付单
				ds = scs0102reverse(dpRequest);
				resultCode = (String) ds.getParamByID("0001", "000").get(0);
				if (Long.valueOf(resultCode) == 0) {
					responseDesc = (String) ds.getParamByID("0002", "000").get(0);
				}
				map.put("TRANSSEQ", (String) ds.getParamByID("4002", "401").get(0));
				map.put("APTRANSSEQ", dpRequest.getApTransSeq());
			}
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, map, orderSeq,
					dpRequest.getTmnNumNo(), dpRequest.getStaffCode(),systemNo,dpRequest.getTac(), dpRequest.getRemark1(), dpRequest.getRemark2());
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
	 * 调用SCS0102 完成冲正
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet scs0102reverse(DpInf06204Request dpRequest) throws Exception {

		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4002", dpRequest.getApTransSeq()); // 原交易流水号
		g401.put("4230", "0001"); // 操作类型
		g401.put("4144", dpRequest.getChannelCode60To20()); // 渠道类型编码
		g401.put("4007", "000006000001"); // 终端号
		g401.put("4017", dpRequest.getKeep()); // 终端流水号(本次冲正的keep)
		g401.put("4146", dpRequest.getStaffCode()); // 操作员
		g401.put("4152", dpRequest.getRemark1()); // 备注说明
		g401.put("4028", dpRequest.getOrderSeq());// 外部订单号
		g401.put("4142", "OT101"); // 操作类型编码
		g401.endRow();

		IServiceCall caller = new ServiceCallImpl();
		return caller.call("SCS", "SCS0102", g401);
	}

	/**
	 * 解析返回的xml，获取返回数据
	 * 
	 * @param detailContent
	 * @return
	 * @throws Exception
	 */
	private static Map<String, String> parserXml(String detailContent) throws Exception {
		Document doc = DocumentHelper.parseText(detailContent);
		Map<String, String> map = new HashMap<String, String>();
		String[] keys = { "TRANSTYPE", "POSID", "SAMID", "EDCARDID", "CITYCODE", "CARDID", "CARDMKND", "CARDSKND" };
		Element root = doc.getRootElement();
		for (String key : keys) {
			String value = root.elementTextTrim(key.toLowerCase());
			map.put(key, value);
		}
		return map;
	}

	/**
	 * 调用SCS0013 完成订单确定操作
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callSCS0013(DpInf06204Request dpRequest) throws Exception {
		/**
		 * 调用SCS0001,完成交易操作
		 */
		// 通过客户编码查区域编码
		String actionCode = "12010006";// 业务编码
		// 订单受理信息
		IParamGroup g401 = new ParamGroupImpl("401");
		// g401.put("4002", dpRequest.getTransSeq());// 订单编码
		g401.put("4005", "OT001");// 订单类型编码：业务类订单
		g401.put("4144", dpRequest.getChannelCode60To20());// 渠道类型编码
		g401.put("4146", dpRequest.getStaffCode());// 操作员
		g401.put("4142", "OT005");
		g401.put("4012", "东莞通-开卡");
		g401.endRow();

		// 业务属性信息
		IParamGroup g407 = new ParamGroupImpl("407");
		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_CARDTYPE");
		g407.put("4088", dpRequest.getCardmknd());
		g407.put("4089", dpRequest.getCardsknd());
		g407.put("4091", "01");
		g407.put("4093", "");
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		g407.put("4047", "1");// 业务单序号
		g407.put("4051", actionCode);// 业务编码
		g407.put("4087", "SCS_ATTRLINE4");
		g407.put("4088", "X001");
		g407.put("4089", "开卡参数");
		g407.put("4091", "01");

		String params = "<PARAMS>" //
				+ "<POSID>" + dpRequest.getTmnNumNo() + "</POSID>" + //
				"<POSSEQUENCE>" + dpRequest.getOrderSeq() + "</POSSEQUENCE>" + //
				"<TERMID>" + dpRequest.getTmnNumNo() + "</TERMID>" + //
				"<OPERID>" + dpRequest.getTmnNumNo() + "</OPERID>" + //
				"<EDCARDID>" + dpRequest.getEdcardId() + "</EDCARDID>" + //
				"<CARDID>" + dpRequest.getCardId() + "</CARDID>" + //
				"<CARDCNT>" + dpRequest.getCardcnt() + "</CARDCNT>" + //
				"<CARDMKND>" + dpRequest.getCardmknd() + "</CARDMKND>" + //
				"<CARDSKND>" + dpRequest.getCardsknd() + "</CARDSKND>" + //
				"<CARDMODEL>" + dpRequest.getCardModel() + "</CARDMODEL>" + //
				"<SALEMODE>" + dpRequest.getSaleMode() + "</SALEMODE>" + //
				"<DEPOSIT>" + dpRequest.getDeposit() + "</DEPOSIT>" + //
				"<BEFBALANCE>" + dpRequest.getBefbalance() + "</BEFBALANCE>" + //
				"<TXNAMT>" + dpRequest.getTxnamt() + "</TXNAMT>" + //
				"<CARDVALDATE>" + dpRequest.getCardvalDate() + "</CARDVALDATE>" + //
				"<CITYCODE>" + dpRequest.getCityCode() + "</CITYCODE>" + //
				"<CARDVERNO>" + dpRequest.getCardverno() + "</CARDVERNO>" + //
				"<BATCHNO>" + dpRequest.getBatchNo() + "</BATCHNO>" + //
				"<AUTHSEQ>" + dpRequest.getAuthseq() + "</AUTHSEQ>" + //
				"<LIMITEDAUTHSEQL>" + dpRequest.getLimitedauthseql() + "</LIMITEDAUTHSEQL>" + //
				"<KEYSET>" + dpRequest.getKeySet() + "</KEYSET>" + //
				"</PARAMS>"; //

		g407.put("4093", params);
		g407.put("4080", "0");// 控制标识
		g407.endRow();

		// 支付单信息
		IParamGroup g480 = new ParamGroupImpl("480");
		// 交易确认
		g480.put("4802", "0000");
		g480.put("4803", "成功");
		g480.put("4804", "ST002");
		g480.put("4805", "S0A");
		g480.endRow();

		// 组成数据包,调用SCS0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SCS", "SCS0013", g401, g407, g480);

		// 返回结果
		return dataSet;

	}

}
