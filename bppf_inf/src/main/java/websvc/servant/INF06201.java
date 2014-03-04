package websvc.servant;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.service.TransManage;
import common.utils.ChannelCode;
import common.utils.DateTime;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf06201Request;
import common.xml.dp.DpInf06201Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author 2014年1月14日 上午10:16:48<br>
 * 
 *         本来描述：[东莞公交]签到接口
 */
public class INF06201 {
	private static final Log logger = LogFactory.getLog(INF06201.class);

	public static String svcInfName = "INF06201";

	public static String execute(String in0, String in1) {
		DpInf06201Request dpRequest = null;

		DpInf06201Response resp = new DpInf06201Response();

		logger.info("请求参数：：" + in1);

		RespInfo respInfo = null;

		String responseCode = "";

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String keep = "";// 获取流水号

		String ip = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		String tmnNum = null;

		INFLogID infId = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf06201Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			tmnNum = dpRequest.getTmnNum();

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "", "", "", "", "S0A");
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

			// 判断有无权限
			/*
			 * boolean r = false; List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode(), ChannelCode.CHANELCODE_10); for (int i = 0; i < privList.size(); i++) { Map
			 * map = (Map) privList.get(i); String str = map.get("PRIV_URL").toString();
			 * 
			 * if (PrivConstant.MULTIMEDIA_PAYMENT.equals(str)) { r = true; break; }
			 * 
			 * }
			 * 
			 * if (!r) { throw new Exception("你没有签到的权限"); }
			 */

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
			// 关联机构验证
//			if (!TCumInfoDao.verifyMerIdCustCode(dpRequest.getCustCode(), dpRequest.getMerId())) {
//				if (TCumInfoDao.getMerIdByCustCode(dpRequest.getCustCode(), dpRequest.getMerId())) {
//					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH, INFErrorDef.CUSTCODE_NOT_MATCH_MERG_DESC);
//				}
//			}

			// 明细项内容
			String detailContent = "";
			PackageDataSet ds = sag0002(dpRequest);// 交易
			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			String responseDesc = null;
			// 返回结果为失败时，抛出异常
			if (Long.valueOf(resultCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
			}

			detailContent = (String) ds.getParamByID("6923", "692").get(0);
			Map<String, String> map = parserXml(detailContent);// 解析xml

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, map, dpRequest.getTmnNumNo(),
					dpRequest.getRemark1(), dpRequest.getRemark2());
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
	 * 解析业务网关返回的xml，获取返回数据
	 * 
	 * @param detailContent
	 * @return
	 * @throws Exception
	 */
	private static Map<String, String> parserXml(String detailContent) throws Exception {
		Document doc = DocumentHelper.parseText(detailContent);
		Map<String, String> map = new HashMap<String, String>();
		String[] keys = { "POSID", "KEYSET", "SAMID", "SAMAUTHINFO", "EDAUTHINFO", "SETTDATE", "BATCHNO", "SYSDATETIME", "AUTHCODE", "PARAMBIT" };
		Element root = doc.getRootElement();
		for (String key : keys) {
			String value = root.elementTextTrim(key.toLowerCase());
			map.put(key, value);
		}
		return map;
	}

	/**
	 * test
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String detailContent = "<params><length>0305</length><syncinfo>287211434366</syncinfo><signcode>0</signcode><algorithm>0</algorithm><pversion>01</pversion><messagetype>5000</messagetype><version>10</version><transcmessage>2051</transcmessage><messagedatetime>20140114102447</messagedatetime><mac>00000000</mac><responsecode>00</responsecode><unitid>10000004</unitid><txnmode>01</txnmode><samid>0000000000000000</samid><samauthinfo>0000000000000000</samauthinfo><edauthinfo>0000000000000000</edauthinfo><posid>523009000013</posid><termid>440121014120</termid><operid>0000000000201359</operid><edcardid>0000000000000000</edcardid><settdate>00000000</settdate><batchno>000000</batchno><sysdatetime>00000000000000</sysdatetime><authcode>000000000000000000000000</authcode><parambit>00000000000000000000000000000000</parambit><keyset>00000000000000000000000000000000</keyset><reserved>00000000000000000000</reserved><resultcode>00000</resultcode></params>";
		Map<String, String> map = parserXml(detailContent);
		System.out.println(map);

	}

	private static PackageDataSet sag0002(DpInf06201Request dpRequest) throws Exception {
		// 通过客户编码查区域编码
		String areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());
		// 包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "COC201");// 服务编码
		g675.put("6752", dpRequest.getChannelCode());// 渠道号
		g675.put("6753", dpRequest.getKeep());// 流水号
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码
		g675.endRow();

		// 业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", "12010001");// 业务编码
		g676.put("6762", "0012");// 产品编码
		g676.put("6763", areaCode);// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();

		// 查询信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", "");
		g680.put("6802", "");
		g680.put("6803", "");
		g680.put("6804", "");
		g680.put("6805", "");
		g680.put("6806", "");
		g680.put("6807", "");
		g680.put("6808", dpRequest.getTradeTime());// 20130508151906
		g680.endRow();

		// 查询项
		IParamGroup g682 = new ParamGroupImpl("682");
		g682.put("6820", "X001");
		g682.put("6821", "xml");// 订单项名称
		g682.put("6822", "01");
		g682.put("6823", "<PARAMS><POSID>" + dpRequest.getTmnNumNo() + "</POSID><TERMID>" + dpRequest.getTmnNumNo() + "</TERMID><OPERID>" + dpRequest.getTmnNumNo() + "</OPERID></PARAMS>");
		g682.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SAG", "SAG0002", g675, g676, g680, g682);

		return ds;
	}

}
