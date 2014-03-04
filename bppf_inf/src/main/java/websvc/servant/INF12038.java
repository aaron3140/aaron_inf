package websvc.servant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.utils.DateTime;
import common.utils.MathTool;
import common.utils.PrivConstant;
import common.utils.RegexTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf12038Request;
import common.xml.dp.DpInf12038Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF12038 {

	public static String svcInfName = "INF12038";

	private static final Log logger = LogFactory.getLog(INF12038.class);

	public static String execute(String in0, String in1) {

		DpInf12038Request dpRequest = null;

		RespInfo respInfo = null; // 返回信息头

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		DpInf12038Response resp = null;

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf12038Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode()); // 返回信息头

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"agentCode", dpRequest.getCustCode(), "", "", "S0A");

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
			List<?> privList = PayCompetenceManage.payFunc(dpRequest
					.getCustCode(), "80");
			boolean re = false;
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map) privList.get(i);
				String str = map.get("PRIV_URL").toString();

				if (PrivConstant.WS_ENT_TICKET.equals(str)) {
					re = true;
					break;
				}
			}
			if (!re) {
				throw new Exception("你没有火车票预订权限");
			}

			// 业务组件
			SignBankManage manage = new SignBankManage();

			// 获取客户ID
			String custId = manage.getCustIdByCode(dpRequest.getCustCode());

			if (null == custId || "".equals(custId)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
						INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
			}

			// 对时间格式进行匹配 时间格式：yyyyMMddHHmmss
			if (!(dpRequest.getDate().matches(RegexTool.DATE_FORMAT))) {

				throw new Exception("输入日期格式不正确");
			}

			String responseDesc = "";

			String transSeq = "";

			String systemNo = "";
			
			String PRICEALL = "";
			String CHARGEALL = "";
			String TICKETCODE = "";

			List<List<Map<String, String>>> list = null;

			PackageDataSet ds = sag0001(dpRequest);

			String responseCode = (String) ds.getParamByID("0001", "000")
					.get(0);

			if (Long.valueOf(responseCode) == 0) {

				responseDesc = (String) ds.getParamByID("0002", "000").get(0);

				transSeq = (String) ds.getParamByID("6903", "690").get(0);

				systemNo = (String) ds.getParamByID("6901", "690").get(0);
				
				PRICEALL = (String) ds.getParamByID("6923", "692").get(4);
				
				PRICEALL = MathTool.yuanToPoint(PRICEALL);
				
				CHARGEALL = (String) ds.getParamByID("6923", "692").get(5);
				
				CHARGEALL = MathTool.yuanToPoint(CHARGEALL);
				
				TICKETCODE = (String) ds.getParamByID("6923", "692").get(6);

				list = unpack(ds);

			}

			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
					dpRequest.getKeep(), dpRequest.getIp(), svcCode, "",
					"000000", "S0A");

			// 返回结果
			resp = new DpInf12038Response();

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, systemNo, dpRequest
							.getOrderSeq(), transSeq, PRICEALL,CHARGEALL,TICKETCODE,list, dpRequest
							.getRemark1(), dpRequest.getRemark2());

		} catch (XmlINFException spe) {

			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);

		} catch (Exception e) {

			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), id);
		}
	}

	@SuppressWarnings("unchecked")
	public static List<List<Map<String, String>>> unpack(PackageDataSet ds) {

		String count = (String) ds.getParamByID("6900", "690").get(0);

		ArrayList value6920 = ds.getParamByID("6920", "692");
		ArrayList value6923 = ds.getParamByID("6923", "692");
		List<List<Map<String, String>>> data = new ArrayList<List<Map<String, String>>>();

		int lenght = Integer.parseInt(count);
		int num = 5;// R**每一个账单有21个参数

		for (int i = 0; i < lenght; i++) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			data.add(list);
			int startIndex = i * num+7;// 本次循环取数据开始的索引【包含】
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

	/**
	 * 调用sag0001接口
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet sag0001(DpInf12038Request dpRequest)
			throws Exception {

		// 包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "GOC301");// 服务编码
		g675.put("6752", dpRequest.getChannelCode60To20());// 渠道号
		g675.put("6753", dpRequest.getKeep());// 流水号
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码
		g675.endRow();

		// 业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", "01010013");// 业务编码
		g676.put("6762", "04040800");// 产品编码
		g676.put("6763", "000000");// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();

		// 查询信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", dpRequest.getPhone());// 2002075100
		g680.put("6802", "1");// 用户标识类型
		g680.put("6809", dpRequest.getSystemNo());
		g680.endRow();

		// 查询附加信息
		IParamGroup g682 = new ParamGroupImpl("682");
		g682.put("6820", "K001");
		g682.put("6821", "查询ID");
		g682.put("6822", "01");
		g682.put("6823", dpRequest.getSearchId());
		g682.endRow();

		g682.put("6820", "K002");
		g682.put("6821", "车次ID");
		g682.put("6822", "01");
		g682.put("6823", dpRequest.getTrainId());
		g682.endRow();

		g682.put("6820", "K003");
		g682.put("6821", "预定信息(json格式)");
		g682.put("6822", "01");
		g682.put("6823", dpRequest.getBookInfo());
		g682.endRow();

		g682.put("6820", "K004");
		g682.put("6821", "是否只预订不出票");
		g682.put("6822", "01");
		g682.put("6823", "true");//dpRequest.getIsOut()
		g682.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SAG", "SAG0001", g675, g676, g680,
				g682);

		return ds;

	}
}
