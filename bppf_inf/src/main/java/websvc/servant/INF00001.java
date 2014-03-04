package websvc.servant;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TInfDcoperlogDao;
import common.dao.TInfOrderBusCfgDao;
import common.dao.TOppPreOrderDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.MathTool;
import common.utils.SubmitForm;
import common.utils.WebSvcTool;
import common.utils.verify.NETCAPKI;
import common.xml.RespInfo;
import common.xml.dp.DpInf00001Request;
import common.xml.dp.DpInf00001Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF00001 {

	public static String svcInfName = "INF00001";

	private static final Log logger = LogFactory.getLog(INF00001.class);

	public static String execute(String in0, String in1) {

		DpInf00001Request dpRequest = null;

		RespInfo respInfo = null; // 返回信息头

		SagManager sagManager = new SagManager();

		TInfOperInLog tInfOperInLog = null;

		String svcCode = WebSvcTool.getSvcCode(in0);

		DpInf00001Response resp = null;

		INFLogID id = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG);

		try {

			dpRequest = new DpInf00001Request(in1);

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

			// 校验机构接入号和终端号绑定关系
			dpRequest.verifyMeridTmnNum();

			TOppPreOrderDao oppDao = new TOppPreOrderDao();

			List<Map<String, String>> callBack = oppDao
					.getCallBackByBatch(dpRequest.getTmnNum());

			String url = callBackUrl(callBack, dpRequest.getBussType());

			if (url == null || "".equals(url)) {

				throw new Exception("回调地址不能为空");
			}

			String responseCode = "000000";

			String callBackInfo = "";
			
			TInfOrderBusCfgDao dao = new TInfOrderBusCfgDao();
			
			Map<String,String> param = new HashMap<String,String>();
			
			param.put("TRAN_SEQ", dpRequest.getTranSeq());
			
			param.put("TMNNUM", dpRequest.getTmnNum());

			if ("00".equals(dpRequest.getBussType())) {

				Map<String, String> order = oppDao.getPreOrderBySeq(dpRequest
						.getTranSeq());
				
				if (order == null) {

					throw new Exception("没有可用的数据");
				}

				if ("001".equals(order.get("OBJ_STAT"))) {

					callBackInfo = process1(dpRequest, oppDao, order);
				} else if ("003".equals(order.get("OBJ_STAT"))) {

					callBackInfo = process3(dpRequest, oppDao, order);
				} else if ("006".equals(order.get("OBJ_STAT"))) {

					callBackInfo = process6(dpRequest, oppDao, order);
				}

			} else {
				
				if(dao.getTranSeq(param)!=null){
					
					throw new Exception("不能重复回调");
				}

				callBackInfo = process(dpRequest);
				
			}

			SubmitForm sform = new SubmitForm();

			sform.setStrUrl(url);

			sform.submitForm(callBackInfo);

			String responseDesc = sform.getResponseStr();

			String f = "SUCCESS";

			if (responseDesc == null
					|| (!"UPTRAN_10001".equals(responseDesc.trim())&&!"UPTRAN_10003".equals(responseDesc.trim())&&!"UPTRAN_10006".equals(responseDesc.trim())
							&& !"UPTRAN_10009".equals(responseDesc.trim()) && !"UPTRAN_10004"
							.equals(responseDesc.trim()))) {

				throw new INFException("001002", "回调失败");

			}

			if ("00".equals(dpRequest.getBussType())) {

				oppDao.updateBySeq(dpRequest.getTranSeq());

			}else{
				
				dao.saveTranSeq(param);
			}
			// 返回结果
			resp = new DpInf00001Response();

			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo
					.getRespType(), respInfo.getKeep(), f, responseCode,
					responseDesc, dpRequest.getTranSeq(), dpRequest
							.getCustCode(), dpRequest.getRemark1(), dpRequest
							.getRemark2());

		} catch (XmlINFException spe) {

			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);

		} catch (Exception e) {

			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), id);
		}
	}

	private static String callBackUrl(List<Map<String, String>> callBack,
			String bussType) {

		String url = null;

		if ("00".equals(bussType)) {

			for (Map<String, String> map : callBack) {

				if ("ACTSER".equals(map.get("LINK_TYPE"))) {

					url = map.get("LINK_INFO");

					break;

				}
			}
		} else {
			for (Map<String, String> map : callBack) {

				if ("EBANKACTSER".equals(map.get("LINK_TYPE"))) {

					url = map.get("LINK_INFO");

					break;

				}
			}
		}

		return url;

	}

	private static String process(DpInf00001Request dpRequest) throws Exception {

		PackageDataSet ds = scs0005(dpRequest.getTranSeq());

		String stat = ds.getByID("4013", "401");

		stat = "S0C".equals(stat) ? "0000" : "0001";

		String orderAmount = ds.getByID("4026", "402");

		orderAmount = MathTool.yuanToPoint(orderAmount);

		String tradeCode = ds.getByID("4028", "401");

		String sysInfo = ds.getByID("4002", "401");

		String merId = dpRequest.getMerId();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

		String tranDate = sdf.format(new Date());

		StringBuffer sb = new StringBuffer("INTERFACETYPE=10004");

		sb.append("&TRANDATE=").append(tranDate);

		sb.append("&ORDERAMOUNT=").append(orderAmount);

		sb.append("&TRADECODE=").append(tradeCode);

		sb.append("&SYSINFO=").append(sysInfo);

		sb.append("&MERID=").append(merId);

		sb.append("&RESULT=").append(stat);

		String CER = "";
		String SIGN = "";

		try {
			// 获取服务器证书
			X509Certificate oCert = NETCAPKI.getSrvX509Certificate();

			// 进行BASE64编码后产生的字符串
			CER = NETCAPKI.getX509CertificateString(oCert);

			// 进行签名后得到二进制签名数据,BASE64编码后得到可视的SIGN
			SIGN = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(sb.toString()
					.getBytes("UTF-8")));

		} catch (Exception e1) {

			throw new Exception("签名发生错误");

		}

		sb.append("&SIGN=").append(SIGN);

		sb.append("&CER=").append(CER);

		return sb.toString();
	}

	private static PackageDataSet scs0005(String transSeq) throws Exception {

		// 调用SCS0005
		IParamGroup g002 = new ParamGroupImpl("002");// 包头

		g002.put("0021", "4002");

		g002.put("0022", transSeq);

		g002.endRow();

		IServiceCall caller = new ServiceCallImpl();

		PackageDataSet dataSet = caller.call("SCS", "SCS0005", g002);// 组成交易数据包,调用SCS0005接口

		return dataSet;
	}

	private static String process6(DpInf00001Request dpRequest,
			TOppPreOrderDao oppDao, Map<String, String> order) throws Exception {

		String details = "";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		String[] split = order.get("MEMO").split("KEEP=");

		String KEEP = split[1];

		if (order.get("ORDER_ID") == null) {

			details += 0 + "^" + order.get("TERM_SEQ")
					+ "^000000000000000^^00000001^" + split[0] + "|";

		} else {
			String str = order.get("ORDER_ID");

			str = null == str ? "" : str;
			// 格式：1^1354588907001^121204007140760^20121204104201^00000000^1|
			details += 0 + "^" + order.get("TERM_SEQ") + "^" + str + "^"
					+ sdf.format(order.get("EFF_DATE")) + "^00000000^"
					+ split[0] + "^" + str + "|";
		}

		details = details.substring(0, details.length() - 1);

		Long OrderAmount = oppDao.getOrderAmount(dpRequest.getTranSeq());

		String amount = (OrderAmount * 100.00) + "";

		StringBuffer sb = new StringBuffer();

		String custcode = order.get("CUST_CODE");

		String termid = order.get("TERM_ID");
		logger
				.info("接入终端号: " + termid + " 商户编码：" + custcode + " KEEP: "
						+ KEEP);
		String str = "";

		str += "KEEP=" + KEEP;

		str += "&ORDERAMOUNT=" + amount;

		str += "&CUSTCODE=" + custcode;

		str += "&DETAILS=" + details;

		String CER = "";

		String SIGN = "";

		try {
			// 获取服务器证书
			X509Certificate oCert = NETCAPKI.getSrvX509Certificate();

			// 进行BASE64编码后产生的字符串
			CER = NETCAPKI.getX509CertificateString(oCert);

			// 进行签名后得到二进制签名数据,BASE64编码后得到可视的SIGN
			SIGN = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(str
					.getBytes("UTF-8")));

		} catch (Exception e1) {

			throw new Exception("签名发生错误");

		}

		SimpleDateFormat currentDate = new SimpleDateFormat("yyyyMMddHHmmss");

		currentDate.format(new Date());
		
		sb.append("INTERFACETYPE=10006");

		sb.append("&TRANDATE=").append(currentDate);

		sb.append("&ORDERAMOUNT=").append(amount);

		sb.append("&CUSTCODE=").append(custcode);

		sb.append("&DETAILS=").append(details);

		sb.append("&KEEP=").append(KEEP);

		sb.append("&SIGN=").append(SIGN);

		sb.append("&CER=").append(CER);

		return sb.toString();
	}
	
	private static String process3(DpInf00001Request dpRequest,
			TOppPreOrderDao oppDao, Map<String, String> order) throws Exception {

		Long OrderAmount = oppDao.getOrderAmount(dpRequest.getTranSeq());

		String amount = (OrderAmount * 100.00) + "";
		
		StringBuffer sb = new StringBuffer();
		
		String KEEP  = order.get("MEMO");
		
		if(KEEP !=null){
			
			KEEP = KEEP.split("KEEP=")[1];
		}
		
		sb.append("KEEP=" + KEEP);
		sb.append("&CUSTCODE=" + order.get("CUST_CODE"));
		sb.append("&RECCODE=" + order.get("OBJ_CODE"));
		sb.append("&ORDERSEQ=" + order.get("TERM_SEQ"));
		
		sb.append("&CONFIRMAMOUNT="+ amount);
		sb.append("&TRANSSEQ=" + order.get("ORDER_ID"));
		sb.append("&RESULT=" + "00000000");

		String CER = "";
		String SIGN = "";

		try {
			// 获取服务器证书
			X509Certificate oCert = NETCAPKI.getSrvX509Certificate();
			// 进行BASE64编码后产生的字符串
			CER = NETCAPKI.getX509CertificateString(oCert);

			// 进行签名后得到二进制签名数据,BASE64编码后得到可视的SIGN
			SIGN = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(sb
					.toString().getBytes("UTF-8")));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		SimpleDateFormat currentDate = new SimpleDateFormat("yyyyMMddHHmmss");

		currentDate.format(new Date());

		sb = new StringBuffer();
		sb.append("INTERFACETYPE=10003");
		sb.append("&TRANDATE=").append(currentDate);
		sb.append("&ORDERAMOUNT=").append(amount);
		sb.append("&CUSTCODE=").append(order.get("CUST_CODE"));
		sb.append("&RECCODE=").append(order.get("OBJ_CODE"));
		sb.append("&ORDERSEQ=").append(order.get("TERM_SEQ"));
		sb.append("&TRANSSEQ=").append(order.get("ORDER_ID"));

		sb.append("&REMARK1=").append(KEEP);

		sb.append("&SIGN=").append(SIGN);
		sb.append("&CER=").append(CER);
		return sb.toString();
	}

	private static String process1(DpInf00001Request dpRequest,
			TOppPreOrderDao oppDao, Map<String, String> order) throws Exception {

		String details = "";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		String[] split = order.get("MEMO").split("KEEP=");

		String KEEP = split[1];

		if (order.get("ORDER_ID") == null) {

			details += 0 + "^" + order.get("TERM_SEQ")
					+ "^000000000000000^^00000001^" + split[0] + "|";

		} else {
			String str = order.get("ORDER_ID");

			str = null == str ? "" : str;
			// 格式：1^1354588907001^121204007140760^20121204104201^00000000^1|
			details += 0 + "^" + order.get("TERM_SEQ") + "^" + str + "^"
					+ sdf.format(order.get("EFF_DATE")) + "^00000000^"
					+ split[0] + "^" + str + "|";
		}

		details = details.substring(0, details.length() - 1);

		Long OrderAmount = oppDao.getOrderAmount(dpRequest.getTranSeq());

		String amount = (OrderAmount * 100.00) + "";

		StringBuffer sb = new StringBuffer();

		String custcode = order.get("CUST_CODE");

		String termid = order.get("TERM_ID");
		logger
				.info("接入终端号: " + termid + " 商户编码：" + custcode + " KEEP: "
						+ KEEP);
		String str = "";

		str += "KEEP=" + KEEP;

		str += "&ORDERAMOUNT=" + amount;

		str += "&CUSTCODE=" + custcode;

		str += "&DETAILS=" + details;

		String CER = "";

		String SIGN = "";

		try {
			// 获取服务器证书
			X509Certificate oCert = NETCAPKI.getSrvX509Certificate();

			// 进行BASE64编码后产生的字符串
			CER = NETCAPKI.getX509CertificateString(oCert);

			// 进行签名后得到二进制签名数据,BASE64编码后得到可视的SIGN
			SIGN = NETCAPKI.base64Encode(NETCAPKI.signPKCS1(str
					.getBytes("UTF-8")));

		} catch (Exception e1) {

			throw new Exception("签名发生错误");

		}

		SimpleDateFormat currentDate = new SimpleDateFormat("yyyyMMddHHmmss");

		currentDate.format(new Date());

		sb.append("INTERFACETYPE=10001");

		sb.append("&TRANDATE=").append(currentDate);

		sb.append("&ORDERAMOUNT=").append(amount);

		sb.append("&CUSTCODE=").append(custcode);

		sb.append("&DETAILS=").append(details);

		sb.append("&KEEP=").append(KEEP);

		sb.append("&SIGN=").append(SIGN);

		sb.append("&CER=").append(CER);

		return sb.toString();
	}

}
