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
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.DateTime;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf02025Request;
import common.xml.dp.DpInf02025Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02025 {

	public static String svcInfName = "INF02025";

	private static final Logger LOG = Logger.getLogger(INF02025.class);

	public static String execute(String in0, String in1) {

		DpInf02025Request dpRequest = null;
		DpInf02025Response resp = null;
		RespInfo respInfo = null; // 返回信息头
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;

		String responseCode = "";
		String responseDesc = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			dpRequest = new DpInf02025Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());// 返回信息头
			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(), dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML", "CUSTCODE", dpRequest.getCustCode(), "", "", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			// 判断插入是否成功
			if (tInfOperInLog != null) {
				boolean flag = manager.selectTInfOperInLogByKeep(dpRequest.getKeep());
				// 判断流水号是否可用
				if (flag) {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE, INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			
			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if (!(dpRequest.getAcceptDate().matches(RegexTool.DATE_FORMAT))) {

				throw new Exception("输入日期格式不正确");
			}

			if(dpRequest.getPayType().equals("0")){
				throw new Exception("目前只支持现金支付");
			}
			
//			// 判断有无交易查询权限
//			List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode());
//			boolean r = false;
//			for (int i = 0; i < privList.size(); i++) {
//				Map map = (Map) privList.get(i);
//				String str = map.get("PRIV_URL").toString();
//				if (PrivConstant.WS_BILL_QUERY.equals(str)) {
//					r = true;
//					break;
//				}
//
//			}
//
//			if (!r) {
//				throw new Exception("你没有广州后付费查询权限");
//			}
			// 调业务网关
			String systemNo = "";
			String billNum="";
			List<List<Map<String, String>>> list = new ArrayList<List<Map<String, String>>>();
			PackageDataSet ds = null;
            ds = sag00020(dpRequest);
			responseCode = (String) ds.getParamByID("0001", "000").get(0);
			if (Long.valueOf(responseCode) == 0) {
				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
				systemNo = (String) ds.getParamByID("6901", "690").get(0);
				billNum = (String) ds.getParamByID("6900", "690").get(0);
				list = unpackSAG0001New(ds);
			}
			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), dpRequest.getKeep(), dpRequest.getIp(), svcCode, responseCode, responseDesc, "S0A");
			resp = new DpInf02025Response();
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", responseCode, responseDesc, systemNo,  billNum, dpRequest.getTmnNumNo(), list);
		} catch (XmlINFException spe) {
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			return ExceptionHandler.toXML(new XmlINFException(resp, e, respInfo), id);
		}
	}


	private static PackageDataSet sag00020(DpInf02025Request dpRequest) throws Exception {

		// 包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "PTC104");// 服务编码
		g675.put("6752", dpRequest.getChannelCode60To20());// 渠道号
		g675.put("6753", dpRequest.getKeep());// 流水号
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码
		g675.endRow();

		// 业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", "03010002");// 业务编码
		g676.put("6762", "0003");// 产品编码
		g676.put("6763", "440100");// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();

		// 查询信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", dpRequest.getPhone());
		g680.put("6802", "0");
		g680.put("6803", "");
		g680.put("6804", "");
		g680.put("6805", "");
		g680.put("6806", "");
		g680.put("6807", "");
		g680.put("6808", dpRequest.getAcceptDate());
		g680.endRow();

		// 查询附加信息
		IParamGroup g682 = new ParamGroupImpl("682");
		g682.put("6820", "J009");
		g682.put("6821", "原终端号");
		g682.put("6822", "01");
		g682.put("6823", dpRequest.getTmnNumNo());
		g682.endRow();

		g682.put("6820", "J010");
		g682.put("6821", "终端号流水");
		g682.put("6822", "01");
		g682.put("6823", dpRequest.getKeep());
		g682.endRow();

		g682.put("6820", "J044");
		g682.put("6821", "允许最大欠费月数");
		g682.put("6822", "01");
		g682.put("6823", "5");
		g682.endRow();

		g682.put("6820", "J040");
		g682.put("6821", "业务代码");
		g682.put("6822", "01");
		g682.put("6823", dpRequest.getBusType());
		g682.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SCS", "SAG0001", g675, g676, g680, g682);

		return ds;
	}

	@SuppressWarnings("unchecked")
	public static List<List<Map<String, String>>> unpackSAG0001New(PackageDataSet ds) {
		ArrayList value6920 = ds.getParamByID("6920", "692");
		ArrayList value6923 = ds.getParamByID("6923", "692");
		List<List<Map<String, String>>> data = new ArrayList<List<Map<String, String>>>();
		int num = 43;// R**每一个账单有43个参数
		int allNum = ds.getParamSetNum("692");
		int lenght = allNum / num;
		LOG.info("shuidianmei-电信后付费查询返回结果:共有[" + allNum + "]行，需循环[" + lenght + "]次");
		for (int i = 0; i < lenght; i++) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			data.add(list);
			int startIndex = i * num;// 本次循环取数据开始的索引【包含】
			int endIndex = (i + 1) * num;// 本次循环取数据结束的索引【不包含】
			LOG.info("shuidianmei-第[" + (i + 1) + "]次循环，取[" + startIndex + "]到[" + (endIndex - 1) + "]的数据");
			for (int j = startIndex; j < endIndex; j++) {
				String key = (String) value6920.get(j);
				String value = (String) value6923.get(j);
				LOG.info("shuidianmei-第[" + (i + 1) + "]次循环,取第[" + j + "]条数据key=[" + key + "] value=[" + value + "]]");
				Map<String, String> map = new HashMap<String, String>();
				list.add(map);
				map.put("KEY", key);
				map.put("VALUE", value);
			}
		}
		return data;
	}

}
