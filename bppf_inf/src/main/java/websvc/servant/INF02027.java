package websvc.servant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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
import common.utils.DateTime;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf02027Request;
import common.xml.dp.DpInf02027Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02027 {

	public static String svcInfName = "INF02027";

	private static final Logger LOG = Logger.getLogger(INF02027.class);

	public static String execute(String in0, String in1) {

		DpInf02027Request dpRequest = null;
		DpInf02027Response resp = null;
		RespInfo respInfo = null; // 返回信息头
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		String responseDesc = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			dpRequest = new DpInf02027Request(in1);
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

			// 判断有无交易查询权限
			// List privList =
			// PayCompetenceManage.payFunc(dpRequest.getCustCode());
			// boolean r = false;
			// for (int i = 0; i < privList.size(); i++) {
			// Map map = (Map) privList.get(i);
			// String str = map.get("PRIV_URL").toString();
			//
			// if (PrivConstant.WS_BILL_QUERY.equals(str)) {
			// r = true;
			// break;
			// }
			//
			// }
			//
			// if (!r) {
			// throw new Exception("你没有有线电视查询权限");
			// }

			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if (!(dpRequest.getAcceptDate().matches(RegexTool.DATE_FORMAT))) {

				throw new Exception("输入日期格式不正确");
			}
			
			if (!"0100".equals(dpRequest.getBusType())
					&& "05".equals(dpRequest.getQueryType())) {

				throw new Exception("该业务部支持用户证号查询");
			}

			if(dpRequest.getPayType().equals("0")){
				throw new Exception("目前只支持现金支付");
			}
			
			// 调业务网关
			String systemNo = "";
			String count = "0";
			String CARDNUM = null;
			String ADDRESS = null;
//			String USERHAND = null;

			List<List<Map<String, String>>> list = new ArrayList<List<Map<String, String>>>();
			PackageDataSet ds = null;

			if (!"05".equals(dpRequest.getQueryType())) {

				ds = sag0001(dpRequest);

				ArrayList value6923 = ds.getParamByID("6923", "692");

				String userCertId = (String) value6923.get(1);

				dpRequest.setQueryValue(userCertId);

				dpRequest.setQueryType("05");

				dpRequest.setKeep(dpRequest.getKeep() + "_1");
			}

			ds = sag0001(dpRequest);

			responseCode = (String) ds.getParamByID("0001", "000").get(0);
			if (Long.valueOf(responseCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
				systemNo = (String) ds.getParamByID("6901", "690").get(0);

				if (!"0120".equals(dpRequest.getBusType())) {
					CARDNUM = (String) ds.getParamByID("6923", "692").get(1);
					ADDRESS = (String) ds.getParamByID("6923", "692").get(0);
					count = (String) ds.getParamByID("6900", "690").get(0);

					list = unpack01(ds);
				} else {
					count = (String) ds.getParamByID("6923", "692").get(0);

					list = unpack05(ds);
				}

			}
			// 插入信息到出站日志表
			// sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
			// dpRequest.getKeep(), dpRequest.getIp(), svcCode, responseCode,
			// responseDesc, "S0A");
			resp = new DpInf02027Response();
			return resp.toXMLStr(dpRequest, respInfo.getReqWebsvrCode(),
					respInfo.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, systemNo, count, CARDNUM,
					ADDRESS, list);
		} catch (XmlINFException spe) {
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), id);
		}
	}

	private static PackageDataSet sag0001(DpInf02027Request dpRequest)
			throws Exception {

		// 通过客户编码查区域编码
		TCumInfoDao infoDao = new TCumInfoDao();
		String area_code = infoDao.getAreaCode(dpRequest.getCustCode());

		// 包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "CAC101");// 服务编码
		g675.put("6752", dpRequest.getChannelCode60To20());// 渠道号
		g675.put("6753", dpRequest.getKeep());// 流水号
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码
		g675.endRow();

		// 业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", "13010001");// 业务编码
		g676.put("6762", "0013");// 产品编码
		g676.put("6763", area_code);// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();

		// 查询信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", dpRequest.getQueryValue());// 2002075100
		g680.put("6802", "0");// 5
		// g680.put("6803", "");
		// g680.put("6804", "");
		// g680.put("6805", "");
		// g680.put("6806", "");
		g680.put("6807", "1");
		g680.put("6808", dpRequest.getAcceptDate());// 20130508151906
		g680.endRow();

		// 查询附加信息
		IParamGroup g682 = new ParamGroupImpl("682");
		g682.put("6820", "E001");
		g682.put("6821", "付费号码类型标识");
		g682.put("6822", "01");
		g682.put("6823", dpRequest.getQueryType());
		g682.endRow();

		g682.put("6820", "E024");
		g682.put("6821", "业务代码");
		g682.put("6822", "01");
		g682.put("6823", dpRequest.getBusType());
		g682.endRow();

		g682.put("6820", "E025");
		g682.put("6821", "交易类型");
		g682.put("6822", "01");
		g682.put("6823", "000");
		g682.endRow();

		g682.put("6820", "E026");
		g682.put("6821", "终端号");
		g682.put("6822", "01");
		g682.put("6823", dpRequest.getTmnNumNo());
		g682.endRow();

		g682.put("6820", "E029");
		g682.put("6821", "终端号流水");
		g682.put("6822", "01");
		g682.put("6823", "000000");
		g682.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SAG", "SAG0001", g675, g676, g680,
				g682);

		return ds;
	}

	@SuppressWarnings("unchecked")
	public static List<List<Map<String, String>>> unpack01(PackageDataSet ds) {

		String count = (String) ds.getParamByID("6900", "690").get(0);

		ArrayList value6920 = ds.getParamByID("6920", "692");
		ArrayList value6923 = ds.getParamByID("6923", "692");
		List<List<Map<String, String>>> data = new ArrayList<List<Map<String, String>>>();

		int lenght = Integer.parseInt(count);
		int num = 6;// R**每一个账单有21个参数

		for (int i = 0; i < lenght; i++) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			data.add(list);
			int startIndex = i * num + 2;// 本次循环取数据开始的索引【包含】
			int endIndex = startIndex + num;// 本次循环取数据结束的索引【不包含】

			for (int j = startIndex; j < endIndex; j++) {
				String key = (String) value6920.get(j);
				String value = (String) value6923.get(j);

				Map<String, String> map = new HashMap<String, String>();
				list.add(map);
				map.put("KEY", key);
				map.put("VALUE", value);
			}
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	public static List<List<Map<String, String>>> unpack05(PackageDataSet ds) {

		String count = (String) ds.getParamByID("6900", "690").get(0);

		ArrayList value6920 = ds.getParamByID("6920", "692");
		ArrayList value6923 = ds.getParamByID("6923", "692");
		List<List<Map<String, String>>> data = new ArrayList<List<Map<String, String>>>();

		int lenght = Integer.parseInt(count);
		int num = 3;// R**每一个账单有21个参数

		for (int i = 0; i < lenght; i++) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			data.add(list);
			int startIndex = i * num + 1;// 本次循环取数据开始的索引【包含】
			int endIndex = startIndex + num;// 本次循环取数据结束的索引【不包含】

			for (int j = startIndex; j < endIndex; j++) {
				String key = (String) value6920.get(j);
				String value = (String) value6923.get(j);

				Map<String, String> map = new HashMap<String, String>();
				list.add(map);
				map.put("KEY", key);
				map.put("VALUE", value);
			}
		}
		return data;
	}

}
