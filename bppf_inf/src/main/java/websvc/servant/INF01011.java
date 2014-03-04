package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.dao.TCumAcctDao;
import common.dao.TCumAttrDao;
import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TSymAreaNewDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf01011Request;
import common.xml.dp.DpInf01011Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 *  签约接口INF01011
 */
public class INF01011 {

	public static String svcInfName = "INF01011";
	
	public static String execute(String in0, String in1) {
		//Long pk = null;
		DpInf01011Request dpRequest = null;
		DpInf01011Response resp = null;
		RespInfo respInfo = null;				// 返回信息头
		String tmnNum = null;  
		String orderSeq = null;  
		String branchCode=null;
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		
		String keep = "";//获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			respInfo = new RespInfo(in1, "20");				// 返回信息头
			dpRequest = new DpInf01011Request(in1);
			branchCode=dpRequest.getBranchCode();
			tmnNum = dpRequest.getTmnNum();
			orderSeq = dpRequest.getOrderSeq();
			
			keep = dpRequest.getKeep();
			ip = dpRequest.getIp();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "ORDERSEQ"
					, orderSeq, "", "", "S0A");
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
			
			boolean flag = false;
			/*TCumInfoDao dao  =  new TCumInfoDao();
			String enterpriseCode = dao.getCustCodeForEnterpriseCode(dpRequest.getMerId());*/
			String custCode=dpRequest.getCustCode();
			if (dpRequest.getPrivateFlag() == "0") {
				throw new Exception("系统暂不支持对公标识");
			}
			List privList = PayCompetenceManage.payFunc(custCode, dpRequest.getChannelCode());
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if("ws_ColNPay_Sign".equals(str)){
					flag = true;
				}
			}
			if (!flag) {
				throw new Exception("没有代收付-签约权限");
			}
				
			TSymAreaNewDao dao= new TSymAreaNewDao();
			boolean isAreaCode = false;
			isAreaCode = dao.isAreaCode(dpRequest.getProvince()+"0000", "01");
			if(!isAreaCode){
				throw new Exception("省级代码输入有误");
			}
			isAreaCode = dao.isAreaCode(dpRequest.getAreaCode(), "02");
			if(!isAreaCode){
				throw new Exception("地市级代码不存在");
			}
			String areacode2 = (dpRequest.getAreaCode()).substring(0,2);
			if(!areacode2.equalsIgnoreCase(dpRequest.getProvince())){
					throw new Exception("省级代码与地市级代码不匹配");
				}
			
			String certype = dpRequest.getCertCode();
            String cerno = dpRequest.getCertNo();
            if(certype.equals("00")&&!(cerno.length()==15||cerno.length()==18))
            	throw new Exception("身份证号码格式不对!");
            
            String projectNo = TCumAttrDao.getProjectNo(custCode);
            if(StringUtils.isEmpty(projectNo)){
            	throw new Exception("该客户编码没有找到项目编号!");
            }
            
			PackageDataSet ds = null;
			try {
				if ("BT001".equals(dpRequest.getBusiType())) {
					ds = bt001(dpRequest, projectNo);
				}else{
					throw new Exception("系统暂只支持代收签约");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
			
			String resultCode = (String) ds.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) ds.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}
			
			responseCode = ds.getByID("0001", "000");// 获取接口的000组的0001参数
			String responseDesc = ds.getByID("0002", "000");// 获取接口的000组的0002参数
			
			
			String orderId = unpackSCS0001(ds);
			
			//插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, responseDesc, "S0A");
			
			resp = new DpInf01011Response();
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), 
					"SUCCESS", responseCode, responseDesc,dpRequest.getOrderSeq(),orderId);
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
	 //签约
	/**
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet bt001(DpInf01011Request dpRequest, String projectNo) throws Exception{
		
		String certCode = dpRequest.getCertCode();
		
		TCumAcctDao acctDao = new TCumAcctDao();
//		String bankCode =acctDao.getBankCode(dpRequest.getBankAcct());    //银行编码
		TCumInfoDao dao = new TCumInfoDao();
		String prtnCode = dao.getPrtnCode(dpRequest.getCustCode());
		
		/**
		 * 调用CUM0012,完成交易操作
		 */
		// 

		IParamGroup g218 = new ParamGroupImpl("218");
		g218.put("2011", prtnCode);
		g218.put("2002", dpRequest.getCustCode()); //客户编码
		g218.put("2004", "C03");//客户类型编码
		g218.put("2091", dpRequest.getRecvcorp());//收款单位名称
		g218.put("2181", dpRequest.getProvince());
		g218.put("2182", dpRequest.getPrtnType());
		g218.put("2183", projectNo);
		g218.put("2185", "0001");  //签约方式
		g218.put("2186", dpRequest.getBranchProp());
		g218.put("2187", dpRequest.getBranchName());//网点名称
		
	
		g218.put("2184", dpRequest.getVeriType());       
//		g218.put("2188", dpRequest.getExternalId());       //外部签约ID
		g218.endRow();
		
		
		// 
		IParamGroup g207 = new ParamGroupImpl("207");
		g207.put("2009", certCode);// 证件类型编码`
		g207.put("2010", dpRequest.getCertNo());//证件号码
		g207.put("2050", dpRequest.getBankCode());//银行账户所属银行代码
		g207.put("2051", dpRequest.getBankInfo());//开户行信息
		g207.put("2055", dpRequest.getBranchCode());//外部业务编码   add by 20131231
		g207.put("2158", dpRequest.getAccName());//银行账户户名
		g207.put("2150", dpRequest.getBankArea());//账号归属地
		g207.put("2151", dpRequest.getCardFlag());//卡折标识
		g207.put("2152", dpRequest.getPrivateFlag());//对公/对私标识
		g207.put("2154", dpRequest.getContactPhone());//联系号码
		g207.put("2155", dpRequest.getContactAddr());//联系地址
		g207.put("2156", dpRequest.getCreditValidTime());//信用卡有效期
		g207.put("2157", dpRequest.getCreditValidCode());//信用卡验证码
		g207.put("2159", dpRequest.getBankAcct());//银行账户		
		g207.put("4097", "PT1004");//支付方式--银行代收（无磁无密）
		g207.endRow();
		
		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", dpRequest.getChannelCode());
		g211.put("2077", dpRequest.getTmnNum());
		g211.put("2078", "");
		g211.endRow();
		
		// 组成数据包,调用CUM0012接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM0012", g218, g207,g211);
		
		// 返回结果
		return dataSet;
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
	
	private static String validDate(String d) {
		if (d==null) {
			return "值为空";
		}
		
		if (d.length()!=14) {
			return "长度不为14";
		}
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMMddHHmmss");
		
		try {
			Long l=0l;
			Long l2=System.currentTimeMillis();
			if (sdf2.format(sdf.parse(d)).equals(d)) {
				l=sdf.parse(d).getTime();
				if (Math.abs(l2-l)>1000*60*60) {
					return "与当前时间相隔超过一小时";
				}
				return "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "格式不为yyyyMMddHHmmss";
	}
	
	@SuppressWarnings("unchecked")
	public static String unpackSCS0001(PackageDataSet dataSet) {
		String orderID = (String)dataSet.getParamByID("2149", "207").get(0);
		return orderID;
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
		      String value1 = o1.get("beginDate");   
		      String value2 = o2.get("beginDate");
		      //如果 为null，则将其值设为最大,以保证空值是显示在最后一列
		      if(isEmpty(value1))
		    	  value1 = "99991230235959";
		      if(isEmpty(value2))
		    	  value2 = "99991230235959";
		      return value1.compareTo(value2);
	      }  
	} 
	
	
}

