package websvc.servant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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
import common.utils.DateTime;
import common.utils.PrivConstant;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf06006Request;
import common.xml.dp.DpInf06006Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF06006 {

	public static String svcInfName = "INF06006";

	private static final Logger LOG = Logger.getLogger(INF06003.class);

	public static String execute(String in0, String in1) {

		DpInf06006Request dpRequest = null;
		DpInf06006Response resp = null;
		RespInfo respInfo = null; // 返回信息头

		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;

		String keep = "";

		String ip = "";

		String responseCode = "";
		String responseDesc = "";

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {

			dpRequest = new DpInf06006Request(in1);

			keep = dpRequest.getKeep();

			ip = dpRequest.getIp();

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());// 返回信息头

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"CUSTCODE", dpRequest.getCustCode(), "", "", "S0A");

			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {
				boolean flag = manager.selectTInfOperInLogByKeep(dpRequest
						.getKeep());
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
			} else {
				if (dpRequest.getTmnNumNo().length() < 12) {
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

				if (PrivConstant.WS_TAX_QUERY.equals(str)) {
					r = true;
					break;
				}

			}

			if (!r) {
				throw new Exception("你没有车船税账单查询的权限");
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

			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			String regex = "^\\d{4}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])(([0|1|]\\d)|2[0-3])([0-5]\\d)([0-5]\\d)$";
			if (!(dpRequest.getAcceptDate().matches(regex))) {

				throw new Exception("输入日期格式不正确");
			}

			// 调业务网关
			String totalCount = "0";

			String systemNo = "";

			List<List<Map<String, String>>> list = new ArrayList<List<Map<String, String>>>();

			PackageDataSet ds = sag0001(dpRequest);

			responseCode = (String) ds.getParamByID("0001", "000").get(0);

			if (Long.valueOf(responseCode) == 0) {

				responseDesc = (String) ds.getParamByID("0002", "000").get(0);

				systemNo = (String) ds.getParamByID("6901", "690").get(0);

				list = unpackSAG0001New(ds);

				for (int i = 0; i < list.size(); i++) {

					List<Map<String, String>> data = list.get(i);

					for (int j = 0; j < data.size(); j++) {

						Map<String, String> map = data.get(j);
						if ("W009".equals(map.get("KEY"))) {

							totalCount = map.get("VALUE");

							break;

						}

					}
				}
			}

			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
					dpRequest.getKeep(), dpRequest.getIp(), svcCode,
					responseCode, responseDesc, "S0A");

			resp = new DpInf06006Response();

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, systemNo, dpRequest
							.getTmnNumNo(), totalCount, list);
		} catch (XmlINFException spe) {
			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, spe.getMessage(),
						"S0A");
			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			if (tInfOperInLog != null) {
				// 插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
						keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), id);
		}
	}

	private static PackageDataSet sag0001(DpInf06006Request dpRequest)
			throws Exception {

		// 包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "CXC101");// 服务编码
		g675.put("6752", dpRequest.getChannelCode60To20());// 渠道号
		g675.put("6753", dpRequest.getKeep());// 流水号
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码

		g675.endRow();

		// 业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", "03010004");// 业务编码
		g676.put("6762", "0043");// 产品编码
		g676.put("6763", "000000");// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码

		if (!isEmpty(dpRequest.getTmnNumNo())) {

			g676.put("6765", dpRequest.getTmnNumNo());// 前向商户终端号
		}

		g676.endRow();

		// 查询信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", dpRequest.getPlateNo());// 2002075100
		g680.put("6802", "0");// 5
		g680.put("6803", "");
		g680.put("6804", "");
		g680.put("6805", "");
		g680.put("6806", "");
		g680.put("6807", "");
		g680.put("6808", dpRequest.getAcceptDate());// 20130508151906

		g680.endRow();

		// 查询附加信息
		IParamGroup g682 = new ParamGroupImpl("682");

		g682.put("6820", "1001");
		g682.put("6821", "发动机号");
		g682.put("6822", "01");
		g682.put("6823", dpRequest.getEngineNo());

		g682.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SAG", "SAG0001", g675, g676, g680,
				g682);

		return ds;
	}

	public static List<List<Map<String, String>>> unpackSAG0001New(
			PackageDataSet ds) {
		ArrayList value6920 = ds.getParamByID("6920", "692");
		ArrayList value6923 = ds.getParamByID("6923", "692");
		List<List<Map<String, String>>> data = new ArrayList<List<Map<String, String>>>();
		int num = 5;// R**每一个账单有21个参数
		int allNum = ds.getParamSetNum("692");
		int lenght = allNum / num;

		for (int i = 0; i < lenght; i++) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			data.add(list);
			int startIndex = i * num;// 本次循环取数据开始的索引【包含】
			int endIndex = (i + 1) * num;// 本次循环取数据结束的索引【不包含】

			for (int j = startIndex; j < endIndex; j++) {
				String key = (String) value6920.get(j);
				String value = (String) value6923.get(j);
				LOG.info("chechuangshui-第[" + (i + 1) + "]次循环,取第[" + j
						+ "]条数据key=[" + key + "] value=[" + value + "]]");
				Map<String, String> map = new HashMap<String, String>();
				list.add(map);
				map.put("KEY", key);
				map.put("VALUE", value);
			}
		}
		return data;
	}

	private static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof String) {
			if (((String) o).trim().length() < 1) {
				return true;
			}
		}
		return false;
	}

}
