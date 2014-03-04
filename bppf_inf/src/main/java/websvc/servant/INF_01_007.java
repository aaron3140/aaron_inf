package websvc.servant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mortbay.log.Log;

import common.algorithm.MD5;
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
import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.MathTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.ResponseVerifyAdder;
import common.xml.dp.DpInf01007Request;
import common.xml.dp.DpInf01007Response;
import common.xml.dp.DpInf02010Request;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 *  交易查询接口INF_01_007
 */
public class INF_01_007 {

	 public static String svcInfName = "01_007";

	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		//Long pk = null;
		DpInf01007Request dpInf01007Request = null;
		DpInf01007Response resp = new DpInf01007Response();
		RespInfo respInfo = null;				// 返回信息头
		String tmnNum = null;  
		String transSeq=null;	
		
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		String keep = "";//获取流水号
		String ip = "";
		String channelCode ="";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			respInfo = new RespInfo(in1, "20");				// 返回信息头
			dpInf01007Request = new DpInf01007Request(in1);
			channelCode = dpInf01007Request.getChannelCode();
			tmnNum = dpInf01007Request.getTmnNum();
			transSeq = dpInf01007Request.getTransSeq();
			keep = dpInf01007Request.getKeep();
			ip = dpInf01007Request.getIp();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "transSeq"
					, transSeq, "", "", "S0A");
			TInfOperInLogManager manager = new TInfOperInLogManager();
			//判断插入是否成功
			if(tInfOperInLog!=null){
				boolean flag = manager.selectTInfOperInLogByKeep(keep);
				//判断流水号是否可用
				if(flag){
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,
							INFErrorDef.POSSEQNO_CONFLICT_REASON);
				}else{
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			
//			判断有无交易查询权限
			boolean flag = false;
			if (Charset.isEmpty(dpInf01007Request.getCustCode()) && ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
				List privList = PayCompetenceManage.payFunc(dpInf01007Request.getCustCode(), dpInf01007Request.getChannelCode());
				for (int i = 0; i < privList.size(); i++) {
					Map map = (Map)privList.get(i);
					String str = map.get("PRIV_URL").toString();
					if("cln_OrderDetailQuery".equals(str) && ChannelCode.AGENT_CHANELCODE.equals(channelCode)){
						flag = true;
						break;
					}else if("ws_OrderDetailQuery".equals(str)){
						flag = true;
						break;
					}
				}
			}else{
				flag = true;
			}
			if (!flag) {
				throw new Exception("没有交易查询明细权限");
			}
			
			//写日志 
//			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
//					svcInfName, svcCode, SocketConfig.getSockIp(),"ORDERCODE",transSeq);
//			id.setPk(pk);
			
			//校验密码
			String verify = dpInf01007Request.getVerify();
			if(verify !=null &&!"".equals(verify)&&"01".equals(verify)){
				String payPassword = dpInf01007Request.getPayPassword();
				if (Charset.isEmpty(payPassword, true)) {
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.PAY_PWD_FAULT_NULL);
				}else {
					
				PackageDataSet dataSet = callCUM1003(dpInf01007Request);
				String resCode = dataSet.getByID("0001", "000");
				if (Long.valueOf(resCode) != 0) {
					throw new INFException(INFErrorDef.PAY_PWD_FAULT,
							INFErrorDef.PAY_PWD_FAULT_DESC);
				}
				}
			}
			
			
			//调用SCS0005
			IParamGroup g002 = new ParamGroupImpl("002");// 包头
			g002.put("0021", "4002");
			g002.put("0022", transSeq);
			g002.endRow();			
			
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet dataSet = caller.call("SCS","SCS0005",g002);// 组成交易数据包,调用SCS0005接口
			
			String resultCode = (String) dataSet.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) dataSet.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}
			
			responseCode = dataSet.getByID("0001", "000");// 获取接口的000组的0001参数
			String responseDesc = dataSet.getByID("0002", "000");// 获取接口的000组的0002参数
			
//			 返回响应码
			int count401 = dataSet.getParamSetNum("401");
			if(count401 == 0) {
				throw new Exception("无此订单信息");
			}
			
			//插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, responseDesc, "S0A");
			System.out.println("====================startunpack=================");
			List<Map<String, String>> list=unpackSCS0005(dataSet);
			System.out.println("====================endunpack=================");
			resp = new DpInf01007Response();
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(),
					"SUCCESS", responseCode, responseDesc,list);
		} catch (XmlINFException spe) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, spe.getMessage(), "S0A");
			}
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, id);
		} catch (Exception e) {
			if(tInfOperInLog!=null){
				//插入信息到出站日志表
				sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, e.getMessage(), "S0A");
			}
			return ExceptionHandler.toXML(new XmlINFException(
				resp, e, respInfo), id);
		}
		
	}
	
	/**
	 * 反向翻译订单状态
	 */
//	private static String transOrderStatReverse(String orderstat) {
//		String [] statNames={"S0C","S0F","S0A","S0V","S0U","S0D"};
//		String [] statCodes={"1","2","3","4","5","6"};
//		
//		if (isEmpty(orderstat)) {
//			return "";
//		}
//		
//		for (int i=0;i<statNames.length;i++) {
//			if (statNames[i].equals(orderstat)) {
//				return statCodes[i];
//			}
//		}
//		
//		return "";
//	}
	
	/**
	 * 反向翻译支付状态
	 */
//	private static String transPayStatReverse(String paystat) {
//		String [] statNames={"S0A","S0D","S0E","S0F","S0R","S0S","S0T"};
//		String [] statCodes={"1","2","3","4","5","6","7"};
//		
//		if (isEmpty(paystat)) {
//			return "";
//		}
//		
//		for (int i=0;i<statNames.length;i++) {
//			if (statNames[i].equals(paystat)) {
//				return statCodes[i];
//			}
//		}
//		
//		return "";
//	}

	/**
	 * 接口SCS0005解包
	 * @param dataSet
	 * @return
	 */
	public static List<Map<String, String>> unpackSCS0005(PackageDataSet dataSet) {
//		String[] codesOrder = {"401.4002","401.4017","404.E051","401.E144","401.4144","401.4008",
//								"401.4012","401.4013","402.4026","401.4004","404.4052","401.4014","401.4010"};
//		String[] namesOrder = {"TRANSSEQ","ORDERSEQ","ORDERTYPE","CHANNELNAME","CHANNELCODE","ORDERTIME",
//								"MEMO","ORDERSTAT","ORDERAMOUNT","AGENTCODE","PAYEECODE","PAYSTAT","PROCESSTIME"};
		
		String[] codesOrder = {"401.4002","401.4028","401.4004","401.4005","401.4006","401.4007","401.4008",
							   "401.4012","401.4013","402.4026","401.4014"};
		String[] namesOrder = {"TRANSSEQ","ORDERSEQ","CUSTCODE","ORDERTYPE","AREACODE","TMNNUM","ORDERTIME",
							   "MEMO","ORDERSTAT","ORDERAMOUNT","PAYSTAT"};
		
		String[] arrayCodes404 = {"404.4046","404.E051","404.4055","404.4057","404.4052","404.4062"};
		String[] arrayNames404 = {"BUSINO","BUSINAME","BUSITIME","BUSISTAT","OBJECTCODE","BUSISYSREFNO"};
		
		String[] arrayCodes405 = {"405.4046","405.4068"};
		String[] arrayNames405 = {"BUSINO","BUSIPRICE"};

		String[] arrayCodes408= {"408.E097","408.E098","408.4101","408.4104","408.E021","408.4110","408.4118"};
		String[] arrayNames408= {"PAYTYPENAME","PAYORGNAME","ACCOUNT","PAYAMOUNT","CURRENCY","PAYSTAT","PAYSYSREFNO"};
		
		String[] arrayCodes692 = {"692.6920","692.6921","692.6922","692.6923"};
		String[] arrayNames692 = {"ITEMCODE","ITEMNAME","ITEMTYPE","ITEMVALUE"};
		
		//保存所有组信息
		List<Map<String, String>> listItem = new ArrayList<Map<String,String>>();
		// 获取订单主体信息
		Map<String, String> mapOrder = getCommonMap(dataSet, codesOrder, namesOrder);
		//mapOrder.put("ORDERSTAT",transOrderStatReverse(mapOrder.get("ORDERSTAT")));
		//mapOrder.put("PAYSTAT",transPayStatReverse(mapOrder.get("PAYSTAT")));
		mapOrder.put("ORDERAMOUNT",MathTool.yuanToPoint(mapOrder.get("ORDERAMOUNT")));
		
		String type = dataSet.getByID("4051", "404");
		// 担保交易
		if (true) {
			//获取404组信息
			List<Map<String, String>> list404 = getCommonList(dataSet, arrayCodes404, arrayNames404);
			//获取405组信息
			List<Map<String, String>> list405 = getCommonList(dataSet, arrayCodes405, arrayNames405);
			//获取408组信息
			List<Map<String, String>> list408 = getCommonList(dataSet, arrayCodes408, arrayNames408);
			//获取692组信息
			List<Map<String, String>> list692 = getCommonList(dataSet, arrayCodes692, arrayNames692);
			
			if (list404!=null&&list404.size()>0) {
				for (Map<String,String> map404:list404) {
					for (Map<String,String> map405:list405) {
						if (map404.get("BUSINO").equals(map405.get("BUSINO"))) {
							map404.put("BUSIPRICE",MathTool.yuanToPoint(map405.get("BUSIPRICE")));
							break;
						}
					}
						map404.put("BUSITYPENAME","担保确认");
						map404.put("MapType","404");
						listItem.add(map404);
				}
			}
			if (list408!=null&&list408.size()>0) {
				for (Map<String,String> map408:list408) {
//					if (map408.get("PAYTYPENAME").equals("PT9001")) {
//						map408.put("BUSITYPENAME","担保取消");
//					} else if (map408.get("PAYTYPENAME").equals("PT0004")) {
//						map408.put("BUSITYPENAME","担保申请");
//					}
					map408.put("PAYAMOUNT",MathTool.yuanToPoint(map408.get("PAYAMOUNT")));
					map408.put("MapType","408");
					listItem.add(map408);
				}
			}
			if (list692!=null&&list692.size()>0) {
				for (Map<String,String> map692:list692) {
					map692.put("MapType","692");
					listItem.add(map692);
				}
			}
		}
		//对集合list根据时间进行排序
//		Collections.sort(listItem,new MapCompare());
		
		listItem.add(mapOrder);
		return listItem;
	}
	
	public static Map<String, String> getCommonMap(PackageDataSet dataSet,String[] arrayCodes,String[] arrayNames){
		Map<String, String> map = new HashMap<String, String>();
		String code = "";
		String paramID = "";
		String tableID = "";
		String value = "";
		List<String> arrayValues = null;
		
		for (int i = 0; i < arrayCodes.length; i++) {
			code = arrayCodes[i];
			paramID = code.split("\\.")[1];
			tableID = code.split("\\.")[0];
			//键值对获取相关值
			arrayValues = dataSet.getParamByID(paramID, tableID);
			value="";
			if ( null != arrayValues && !arrayValues.isEmpty()) {
				value = arrayValues.get(0);
			}
			map.put(arrayNames[i], value);
		}
		
		return map;
	}
	
	private static boolean isEmpty(Object o) {
		if (o==null) {
			return true;
		}
		if (o instanceof String) {
			if (((String) o).trim().length()<1) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> getCommonList(PackageDataSet dataSet,String[] arrayCodes,String[] arrayNames){
		
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		String code = "";
		String paramID = "";
		String tableID = "";
		String value = "";
		List<String> arrayValues = null;
		boolean flag = false;
		
		for (int i = 0; i < arrayCodes.length; i++) {
			code = arrayCodes[i];
			paramID = code.split("\\.")[1];
			tableID = code.split("\\.")[0];
			//键值对获取相关值
			arrayValues = dataSet.getParamByID(paramID, tableID);
			
			if (!flag) {
				for (int j = 0; j < arrayValues.size(); j++) {
					Map<String, String> map = new HashMap<String, String>();
					list.add(map);
				}
				flag = true;
			}
			
			if ( null != arrayValues && !arrayValues.isEmpty()) {
				for (int j = 0; j < arrayValues.size(); j++) {
					value = arrayValues.get(j);
					list.get(j).put(arrayNames[i], value);
				}
			}
		}

		return list;
	}
	
	//比较器
	static class MapCompare implements Comparator<Map<String,String>>{   
	      public int compare(Map<String,String> o1, Map<String,String> o2) {  
		      String value1 = o1.get("BUSITIME");   
		      String value2 = o2.get("BUSITIME");
		      //如果 为null，则将其值设为最大,以保证空值是显示在最后一列
		      if(isEmpty(value1))
		    	  value1 = "99991230235959";
		      if(isEmpty(value2))
		    	  value2 = "99991230235959";
		      return value1.compareTo(value2);
	      }  
	}
	
	public static String executeForMD5(String in0, String in1){
		DpInf01007Response resp = new DpInf01007Response();
		RespInfo respInfo = null;				// 返回信息头
		String md5Key = null;
		try {
			respInfo = new RespInfo(in1, "10");	
			DpInf01007Request dpRequest = new DpInf01007Request(in1);
			String custCode = dpRequest.getCustCode();
			if (Charset.isEmpty(custCode)) {
				throw new Exception("CUSTCODE为空");
			}
			//客户端MD5校验
			if (ChannelCode.AGENT_CHANELCODE.equals(dpRequest.getChannelCode())) {
				String tokenValidTime = TSymSysParamDao.getTokenValidTime();
				 md5Key = TInfLoginLogDao.getMD5KeyByStaffCode(dpRequest.getStaffCode(),
							tokenValidTime);
				dpRequest.verifyByMD5(dpRequest.xmlSubString(in1), md5Key);
				TInfLoginLogDao.updateRanduseTimeByStaffCode(dpRequest.getStaffCode());
			}

			String oldXml = execute(in0, in1);
			
			return ResponseVerifyAdder.pkgForMD5(oldXml, md5Key);
		} catch (Exception e) {
			String oldXml= ExceptionHandler.toXML(new XmlINFException(
					resp, e, respInfo), null);
			
			return ResponseVerifyAdder.pkgForMD5(oldXml, null);
		}
	}
	
	
	/**
	 *  调用CUM1003校验密码
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet callCUM1003(DpInf01007Request dpRequest)throws Exception {

String staff = dpRequest.getStaffCode();
String verityType = "0001"; // 支付密码

String tmnNum = dpRequest.getTmnNum();

IParamGroup g200 = new ParamGroupImpl("200");
g200.put("2901", "2171");
g200.put("2902", staff);
g200.put("2903", "2007");
g200.put("2904", dpRequest.getPayPassword());
g200.put("2172", "0001");
g200.put("2173", verityType);
// g200.put("2025", null);
g200.endRow();

IParamGroup g211 = new ParamGroupImpl("211");
g211.put("2076", dpRequest.getChannelCode());
g211.put("2077", tmnNum);
g211.put("2078", null);
g211.put("2085", dpRequest.getIp());
g211.endRow();

IServiceCall caller = new ServiceCallImpl();
PackageDataSet dataSet = caller.call("BIS", "CUM1003", g200, g211);

return dataSet;
}
	
}



