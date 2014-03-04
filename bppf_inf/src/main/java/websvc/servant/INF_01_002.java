package websvc.servant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import common.dao.TInfDcoperlogDao;
import common.entity.Order;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.Charset;
import common.utils.MathTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpTransactionQueryRequest;
import common.xml.dp.DpTransactionQueryResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * @author yangcheng
 *  交易查询接口INF_01_002
 */
public class INF_01_002 {

	public static String svcInfName = "01_002";
	
	private static String [] actionCodes={"01010001","01010002","01030001","01030005"};
	private static String [] rtnActionCodes={"1","2","3","4"};
	
	private static String [] orderstats={"S0C","S0F","S0A","S0V","S0U"};
	private static String [] rtnOrderstats={"1","2","3","4","5"};
	
	public static String execute(String in0, String in1) {
		//Long pk = null;
		DpTransactionQueryRequest dpTransactionQueryRequest = null;
		DpTransactionQueryResponse resp = new DpTransactionQueryResponse();
		RespInfo respInfo = null;				// 返回信息头
		String agentcode = null;  
		String tmnNum = null;  
		String searchtime = null;   
		String startdate = null; 
		String enddate = null;	
		String orderSeq=null;
		String transSeq=null;	
		String ordertype=null;	
		String orderstat=null;	
		String areacode=null;	
		
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		String keep = "";//		获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);

		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			respInfo = new RespInfo(in1, "20");				// 返回信息头
			dpTransactionQueryRequest = new DpTransactionQueryRequest(in1);
			
			agentcode = dpTransactionQueryRequest.getAgentCode();
			tmnNum = dpTransactionQueryRequest.getTmnNum();
			searchtime = dpTransactionQueryRequest.getSearchtime();
			startdate = dpTransactionQueryRequest.getStartdate();
			enddate = dpTransactionQueryRequest.getEnddate();
			orderSeq=dpTransactionQueryRequest.getOrderSeq();
			transSeq=dpTransactionQueryRequest.getTransSeq();
			ordertype=dpTransactionQueryRequest.getOrdertype();
			orderstat=dpTransactionQueryRequest.getOrderstat();
			areacode=dpTransactionQueryRequest.getAreacode();
			keep = dpTransactionQueryRequest.getKeep();
			ip = dpTransactionQueryRequest.getIp();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "agentCode"
					, agentcode, "transSeq", transSeq, "S0A");
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
			
			
			String str=validDate(searchtime);
			if (!str.equals("0")) {
				throw new Exception("查询时间"+str);
			}
			pageList(dpTransactionQueryRequest);
			//写日志 
//			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
//					svcInfName, svcCode, SocketConfig.getSockIp(),
//					"AGENTCODE", agentcode,"CARDTYPE",ordertype);
//			id.setPk(pk);
			
			//调用SCS0007
			IParamGroup g002 = new ParamGroupImpl("002");// 包头
			
			g002.put("0021", "4004");
			g002.put("0022", agentcode);
			g002.endRow();
			
			g002.put("0021", "4007");
			g002.put("0022", tmnNum);
			g002.endRow();
			
			g002.put("0021", "4005");
			g002.put("0022", "OT001");// 固定OT001
			g002.endRow();
			
			//少一个
			if(!Charset.trim(startdate).equals("") && !Charset.trim(enddate).equals("")) {
				g002.put("0021", "4008A");
				g002.put("0022", startdate);
				g002.endRow();
				
				g002.put("0021", "4008B");
				g002.put("0022", enddate);
				g002.endRow();
			} else if(Charset.trim(startdate).equals("") && Charset.trim(enddate).equals("")) {
				
			} else {
				throw new Exception("交易起始时间和交易结束时间必须同时填写");
			}
			
			if(!Charset.trim(orderSeq).equals("")) {
				g002.put("0021", "4017");
				g002.put("0022", orderSeq);
				g002.endRow();
			}
			
			if(!Charset.trim(transSeq).equals("")) {
				g002.put("0021", "4002");
				g002.put("0022", transSeq);
				g002.endRow();
			}
	
			// 这里的ordertype是actionCode
			if(!Charset.trim(ordertype).equals("")) {
				String actioncode = transActionCode(ordertype);
				if(actioncode == null) {
					throw new Exception("找不到相对应的订单类型");
				}
				g002.put("0021", "4051");
				g002.put("0022", actioncode);
				g002.endRow();
			}
			
			if(!Charset.trim(orderstat).equals("")) {
				orderstat = transOrderStat(orderstat);
				if(orderstat == null) {
					throw new Exception("找不到相对应的订单状态");
				}
				g002.put("0021", "4013");
				g002.put("0022", orderstat);
				g002.endRow();
			}
			
			if(!Charset.trim(areacode).equals("")) {
				g002.put("0021", "4006");
				g002.put("0022", areacode);
				g002.endRow();
			}
			
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet packageDataSet = caller.call("SCS","SCS0007",g002);// 组成交易数据包,调用SCS0007接口
			
			String resultCode = (String) packageDataSet.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) packageDataSet.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}
			
			// 返回响应码
			responseCode = packageDataSet.getByID("0001", "000");// 获取接口的000组的0001参数
			String responseDesc = packageDataSet.getByID("0002", "000");// 获取接口的000组的0002参数
			
			String returnTotalnums=packageDataSet.getByID("0019", "001");//总记录数
			
			int count = packageDataSet.getParamSetNum("401");
			
			List<Order> orderList=new ArrayList<Order>();
			
			for (int i = 0; i < count; i++) {
				Order order=new Order();
				order.setOrderSeq((String) packageDataSet.getParamByID("4017", "401").get(i)); //终端流水号
				order.setTransSeq((String) packageDataSet.getParamByID("4002", "401").get(i)); //订单编码
				String actionType = (String) packageDataSet.getParamByID("4051", "401").get(i);
				order.setActionType(transActionCodeReverse(actionType)); //业务类型
				order.setActionName((String) packageDataSet.getParamByID("E051", "401").get(i)); //业务名称
				order.setOrderTime((String) packageDataSet.getParamByID("4008", "401").get(i));//订单受理时间
				order.setMemo((String) packageDataSet.getParamByID("4117", "401").get(i));//订单备注
				order.setOrderStat((String) packageDataSet.getParamByID("E013", "401").get(i));//订单状态名称
				
				String orderAmount = (String) packageDataSet.getParamByID("4025", "401").get(i);
				// 元转分
				orderAmount = MathTool.yuanToPoint(orderAmount);
				order.setOrderAmount(orderAmount);//订单金额
				orderList.add(order);
			}
			
			//插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, responseDesc, "S0A");
			
			resp = new DpTransactionQueryResponse();
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), 
					"SUCCESS", responseCode, responseDesc,startdate ,enddate,returnTotalnums, orderList);
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
	 * 翻译actionCode
	 * @version: 1.00
	 * @history: 2012-2-22 下午09:51:06 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param actionCode
	 * @return
	 * @see
	 */
	private static String transActionCode(String actionCode) {
		for (int i=0;i<actionCodes.length;i++) {
			if (actionCode.equals(rtnActionCodes[i])) {
				return actionCodes[i];
			}
		}
		return null;
	}
	
	/**
	 * 反向翻译actionCode
	 * @version: 1.00
	 * @history: 2012-2-22 下午09:51:06 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param actionCode
	 * @return
	 * @see
	 */
	private static String transActionCodeReverse(String actionCode) {
		for (int i=0;i<actionCodes.length;i++) {
			if (actionCode.equals(actionCodes[i])) {
				return rtnActionCodes[i];
			}
		}
		
		return null;
	}
	
	/**
	 * 翻译订单状态
	 * @version: 1.00
	 * @history: 2012-2-22 下午09:56:10 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param orderstat
	 * @return
	 * @see
	 */
	private static String transOrderStat(String orderstat) {
		for (int i=0;i<rtnOrderstats.length;i++) {
			if (orderstat.equals(rtnOrderstats[i])) {
				return orderstats[i];
			}
		}
		
		return "";
	}
	
	public  static List<Order> pageList(DpTransactionQueryRequest tqr)throws Exception{
		List<Order> list=new ArrayList<Order>();
		
		IParamGroup g002 = new ParamGroupImpl("002");
		// 订单号
		if (!isEmpty(tqr.getTransSeq())) {
			g002.put("0021","4002");
			g002.put("0022",tqr.getTransSeq());
			g002.endRow();
		}
		// 交易流水号
		if (!isEmpty(tqr.getOrderSeq())) {
			g002.put("0021","4017");
			g002.put("0022",tqr.getOrderSeq());
			g002.endRow();
		}
		// 客户编码
		g002.put("0021","4004");
		g002.put("0022",tqr.getAgentCode());
		g002.endRow();
		
		// 支付状态
		if(!isEmpty(tqr.getOrderstat())) {
			String orderstat = transOrderStat(tqr.getOrderstat());
			if(orderstat == null) {
				throw new Exception("找不到相对应的订单状态");
			}
			g002.put("0021","4014");
			g002.put("0022",orderstat);
			g002.endRow();
		}
		// 交易时间
		if(!isEmpty(tqr.getStartdate()) && !isEmpty(tqr.getEnddate())) {
			g002.put("0021","4008A");
			g002.put("0022",tqr.getStartdate());
			g002.endRow();
			
			g002.put("0021","4008B");
			g002.put("0022",tqr.getEnddate());
			g002.endRow();
		} else if(isEmpty(tqr.getStartdate()) && isEmpty(tqr.getEnddate())) {
			
		} else {
			throw new Exception("交易起始时间和交易结束时间必须同时填写");
		}
		
		// 交易类型
		if(!isEmpty(tqr.getOrdertype())){
			String actioncode = transActionCode(tqr.getOrdertype());
			if(actioncode == null) {
				throw new Exception("找不到相对应的订单类型");
			}
			g002.put("0021","4051");
			g002.put("0022",actioncode);
			g002.endRow();
		}
		// 所属区域
		if(!isEmpty(tqr.getAreacode())){
			g002.put("0021","4006");
			g002.put("0022",tqr.getAreacode());
			g002.endRow();
		}
		// 终端号
		if(!isEmpty(tqr.getTmnNum())){
			g002.put("0021","4007");
			g002.put("0022",tqr.getTmnNum());
			g002.endRow();
		}
		
		PackageDataSet ds = null;
		String resultCode="";
		try {
			IServiceCall caller = new ServiceCallImpl();
			ds = caller.call("SCS","SCS0014",g002);
			resultCode=ds.getByID("0001","000");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String responseCode = ds.getByID("0001", "000");// 获取接口的000组的0001参数
		String responseDesc = ds.getByID("0002", "000");// 获取接口的000组的0002参数
		
		String returnTotalnums=ds.getByID("0019", "001");//总记录数
		
		int count = ds.getParamSetNum("401");
		
		List<Order> orderList=new ArrayList<Order>();
		
		for (int i = 0; i < count; i++) {
			Order order=new Order();
			order.setOrderSeq((String) ds.getParamByID("4017", "401").get(i)); //终端流水号
			order.setTransSeq((String) ds.getParamByID("4002", "401").get(i)); //订单编码
			String actionType = (String) ds.getParamByID("4051", "401").get(i);
			order.setActionType(transActionCodeReverse(actionType)); //业务类型
			order.setActionName((String) ds.getParamByID("E051", "401").get(i)); //业务名称
			order.setOrderTime((String) ds.getParamByID("4008", "401").get(i));//订单受理时间
			order.setMemo((String) ds.getParamByID("4117", "401").get(i));//订单备注
			order.setOrderStat((String) ds.getParamByID("E013", "401").get(i));//订单状态名称
			
			String orderAmount = (String) ds.getParamByID("4025", "401").get(i);
			// 元转分
			orderAmount = MathTool.yuanToPoint(orderAmount);
			order.setOrderAmount(orderAmount);//订单金额
			orderList.add(order);
		}
		
		return list;
	}
	
	
	private static  boolean  isEmpty(Object o) {
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
	
	public static void main(String args[]) {
		System.out.println(validDate("20120515100000"));
	}
}

