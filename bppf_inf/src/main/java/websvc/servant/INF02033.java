package websvc.servant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TInfLoginLogDao;
import common.dao.TSymSysParamDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.ChannelCode;
import common.utils.DateTime;
import common.utils.MathTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf02033Request;
import common.xml.dp.DpInf02033Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF02033 {

	public static String svcInfName = "INF02022";

	private static final Log log = LogFactory.getLog(INF02022.class);

	public static String executeForMD5(String in0, String in1) {

		DpInf02033Response resp = new DpInf02033Response();

		RespInfo respInfo = null; // 返回信息头

		String md5Key = null;

		try {

			DpInf02033Request dpRequest = new DpInf02033Request(in1);

			respInfo = new RespInfo(in1, dpRequest.getChannelCode());

			// 客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {

				String tokenValidTime = TSymSysParamDao.getTokenValidTime();

				md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest
						.getStaffCode(), tokenValidTime);

				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);

				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest
						.getStaffCode());
				
				//密码鉴权
//				PasswordUtil.AuthenticationPassWord3(dpRequest, dpRequest.getStaffCode(), dpRequest.getPayPassword());

			}

			String oldXml = execute(in0, in1);

			return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);

		} catch (Exception e) {
			String oldXml = ExceptionHandler.toXML(new XmlINFException(resp, e,
					respInfo), null);

			return ResponseVerifyAdder.pkgForMD5(oldXml, null);
		}

	}
	
	public static String execute(String in0, String in1) {

		log.info("请求参数：：" + in1);

		String responseCode = "";

		String responseDesc = "";

		DpInf02033Request dpRequest = null;

		DpInf02033Response resp = new DpInf02033Response();

		RespInfo respInfo = null;

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID infId = new INFLogID(svcInfName,
				TInfDcoperlogDao.PARTY_GROUP_AG);

		try {
			respInfo = new RespInfo(in1, "20");

			dpRequest = new DpInf02033Request(in1);

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),
					dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML",
					"", "", "", "", "S0A");

			TInfOperInLogManager man = new TInfOperInLogManager();

			// 判断插入是否成功
			if (tInfOperInLog != null) {

				boolean flag = man.selectTInfOperInLogByKeep(dpRequest
						.getKeep());
				// 判断流水号是否可用
				if (flag) {
					flag = man.updateAllow(tInfOperInLog.getOperInId(), 1);

					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {

					flag = man.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}

			// 授权
			
			//获取系统参考号
			String systemNo="";
			
			PackageDataSet ds = scs0017(dpRequest);
			
			responseCode = (String) ds.getParamByID("0001", "000").get(0);
			
			if(Long.valueOf(responseCode) == 0){
				
				systemNo=(String) ds.getParamByID("4062", "404").get(0);
			}
			

			String txtMount = "";

			ds = sag0004(dpRequest,systemNo);

			responseCode = (String) ds.getParamByID("0001", "000").get(0);

			if (Long.valueOf(responseCode) == 0) {

				responseDesc = (String) ds.getParamByID("0002", "000").get(0);

				Map<String, String> m = unpack04(ds);

				txtMount = m.get("6804");

				// 单位转换：元转分
				txtMount = MathTool.yuanToPoint(txtMount);

			}

			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), "SUCCESS",
					responseCode, responseDesc, systemNo, txtMount, dpRequest
							.getRemark1(), dpRequest.getRemark2());

			return oXml;

		} catch (XmlINFException spe) {

			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, infId);

		} catch (Exception e) {

			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), infId);

		}
	}

	private static PackageDataSet scs0017(DpInf02033Request dpRequest)
			throws Exception {

		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "SCS_ACTNUM");// 

		g002.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SCS", "SCS0017", g002);

		return ds;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> unpack04(PackageDataSet ds) {

		// String count = (String) ds.getParamByID("6900", "690").get(0);
		//
		ArrayList value6920 = ds.getParamByID("6920", "692");
		ArrayList value6923 = ds.getParamByID("6923", "692");
		// List<List<Map<String, String>>> data = new ArrayList<List<Map<String,
		// String>>>();
		//
		// int lenght = 1;//Integer.parseInt(count);
		// int num = 8;// R**每一个账单有21个参数

		// for (int i = 0; i < lenght; i++) {
		// List<Map<String, String>> list = new ArrayList<Map<String,
		// String>>();
		// data.add(list);
		// int startIndex = i * num + 2;// 本次循环取数据开始的索引【包含】
		// int endIndex = startIndex + num;// 本次循环取数据结束的索引【不包含】

		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < 8; i++) {
			String key = (String) value6920.get(i);
			String value = (String) value6923.get(i);

			// list.add(map);
			map.put(key, value);
			// map.put("VALUE", value);
		}
		// }
		return map;
	}

	private static PackageDataSet sag0004(DpInf02033Request dpRequest,String systemNo)
			throws Exception {

		// 通过客户编码查区域编码
		TCumInfoDao infoDao = new TCumInfoDao();

		String area_code = dpRequest.getAreaCode();

		if (area_code == null || "".equals(area_code)) {

			area_code = infoDao.getAreaCode(dpRequest.getCustCode());
		}

		// 包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "PQC401");// 服务编码
		g675.put("6752", dpRequest.getChannelCode());// 渠道号
		g675.put("6753", dpRequest.getKeep());// 流水号
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码
		g675.endRow();

		// 业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", "07010002");// 业务编码
		g676.put("6762", dpRequest.getProductCode());// 产品编码
		g676.put("6763", area_code);// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();

		// 查询信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", dpRequest.getAcctCode());// 2002075100
		g680.put("6802", "2");// 
		// g680.put("6803", "");
		// g680.put("6804", "");
		// g680.put("6805", "");
		// g680.put("6806", "");
		g680.put("6807", "1");
		g680.put("6808", dpRequest.getTradeTime());// 20130508151906
		g680.put("6809", systemNo);
		g680.endRow();

		// 查询附加信息
		IParamGroup g682 = new ParamGroupImpl("682");
		g682.put("6820", "Q002");
		g682.put("6821", "数量");
		g682.put("6822", "11");
		g682.put("6823", dpRequest.getRechAmount());
		g682.endRow();

		g682.put("6820", "Q003");
		g682.put("6821", "购买账号类型");
		g682.put("6822", "01");
		g682.put("6823", "1");// dpRequest.getProductCode()
		g682.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SAG", "SAG0004", g675, g676, g680,
				g682);

		return ds;
	}

}
