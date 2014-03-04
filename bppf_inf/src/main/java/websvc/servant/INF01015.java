package websvc.servant;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TCumInfoDao;
import common.dao.TInfDcoperlogDao;
import common.dao.TScsOrderDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.PayCompetenceManage;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.Charset;
import common.utils.MathTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf01015Request;
import common.xml.dp.DpInf01015Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 * 
 * 本类描述: 实时冲正接口
 * @version: 企业帐户前置接口 v1.0 
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email:  zhuxiaojun@tisson.com
 * @time: 2013-3-4上午11:02:51
 */
public class INF01015 {
	private static final Log log = LogFactory.getLog(INF01015.class);
	public static String svcInfName = "INF01015";
	public static String execute(String in0, String in1) {
		// TODO Auto-generated method stub
		DpInf01015Request dpRequest = null;
		DpInf01015Response response = new DpInf01015Response();
		RespInfo respInfo = null;				// 返回信息头
		String tmnNum = null;  	
		
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		String keep = "";//获取流水号
		String ip = "";
		String channelCode ="";
		String svcCode = WebSvcTool.getSvcCode(in0);
		INFLogID infId = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try {
			respInfo = new RespInfo(in1, "20");				// 返回信息头
			dpRequest = new DpInf01015Request(in1);
			channelCode = dpRequest.getChannelCode();
			tmnNum = dpRequest.getTmnNum();
			keep = dpRequest.getKeep();
			ip = dpRequest.getIp();
			
			//插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(keep, ip, tmnNum, svcCode, "XML", "transSeq"
					, "", "", "", "S0A");
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
			//判断有无权限
			boolean flag = false;
			List privList = PayCompetenceManage.payFunc(dpRequest.getCustCode(), channelCode);
			for (int i = 0; i < privList.size(); i++) {
				Map map = (Map)privList.get(i);
				String str = map.get("PRIV_URL").toString();
				if("ws_ColNPay_Payagent_Reverse".equals(str)){
					flag=true;
					break;
				}
			}
			if (!flag) {
				throw new Exception("没有订单冲正交易权限");
			}
			
			PackageDataSet ps = packageSCS0015(dpRequest);
			
			String resultCode = (String) ps.getParamByID("0001", "000").get(0);
			 responseCode = resultCode;
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) ps.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg+" 银行账号或原流水号输入错误");
			}
			String actonCode = ps.getByID("4051", "404");
			if(Charset.isEmpty(actonCode, true))
			   throw new Exception(" 银行账号或原流水号输入错误");
			
			String transSeq = ps.getByID("4002", "401");
			String custCode = ps.getByID("4004", "401");
			if(ps.getByID("E003", "401").equals("C03")){
				TCumInfoDao tCumInfoDao  =  new TCumInfoDao();
			   custCode = tCumInfoDao.getCustCodeFromDSF(custCode); //网点编码对应的客户编码
			}
			if(custCode!=null&&!custCode.equals(dpRequest.getCustCode()))
				throw new Exception("客户编码输入有误");
			
			String orderSeq = ps.getByID("4028", "401");
			if(orderSeq!=null&&!orderSeq.equals(dpRequest.getApTransSeq()))
				throw new Exception("原订单号输入有误");
			String amount = ps.getByID("4104", "408");
			if(amount!=null){
			  String money = MathTool.yuanToPoint(amount);
			  if(!money.equals(dpRequest.getTxnAmount()))
				  throw new Exception("冲正金额输入有误");
			}else{
				throw new Exception("冲正金额输入有误");
			}
			
			//订单信息是否正确，进行验证
//			TScsOrderDao orderDao =new TScsOrderDao();
//			List list = orderDao.getOrderByOrderCode(dpRequest.getApTransSeq(),dpRequest.getAppKeep());
//			String orderCode ="";
//			if(list!=null&&list.size()==1){
//				orderCode = ((Map)list.get(0)).get("ORDER_ID").toString();
//				BigDecimal payMoney = (BigDecimal)((Map)list.get(0)).get("PAY_MONEY");
//				String money = MathTool.yuanToPoint(payMoney.toString());
//				if(!(dpRequest.getTxnAmount()).equals(money)){
//					throw new Exception("冲正金额不对");
//				}
//				String acctCode = ((Map)list.get(0)).get("ACCT_CODE").toString();
//				if(!(dpRequest.getBankAcct()).equals(acctCode))
//					throw new Exception("银行账号输入有误");
//			}else{
//				throw new Exception("原订单号或原keep流水输入有误");
//			}
//			String custid=  ((Map)list.get(0)).get("CUST_ID").toString();
//			TCumInfoDao infoDao = new TCumInfoDao();
//			boolean bool = infoDao.validateCustCode(custid, dpRequest.getCustCode());
//			if(!bool)
//				throw new Exception("客户编码输入有误");
			
			 PackageDataSet pds = null;
             pds = reverse(dpRequest,transSeq); 
              
             resultCode = (String) pds.getParamByID("0001", "000").get(0);
 			 responseCode = resultCode;
 			//返回结果为失败时，抛出异常
 			if(Long.valueOf(resultCode) != 0) {
 				String resultMsg = (String) pds.getParamByID("0002", "000").get(0);
 				throw new Exception(resultMsg);
 			}
 			responseCode = pds.getByID("0001", "000");// 获取接口的000组的0001参数
			String responseDesc = pds.getByID("0002", "000");// 获取接口的000组的0002参数

			return response.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(), "SUCCESS", 
					responseCode,responseDesc, dpRequest.getOrderSeq(),dpRequest.getCustCode(),dpRequest.getApTransSeq(),dpRequest.getAppKeep(),
					dpRequest.getBankAcct(),dpRequest.getTxnAmount(),transSeq,dpRequest.getRemark1(),dpRequest.getRemark2());
		} catch (XmlINFException spe) {
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, infId);
		} catch (Exception e) {
			return ExceptionHandler.toXML(
					new XmlINFException(response, e, respInfo), infId);
		}
	}
	/**
	 * 查询订单详情
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet packageSCS0015(DpInf01015Request dpRequest) throws Exception {
		IParamGroup g001 = new ParamGroupImpl("001");
		g001.put("0012", "1");
		g001.put("0013", "2");
		g001.endRow();
		
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "4101");
		g002.put("0022", dpRequest.getBankAcct());
		g002.endRow();
		
		g002.put("0021", "4017");
		g002.put("0022", dpRequest.getAppKeep());
		g002.endRow();
		
		IServiceCall caller = new ServiceCallImpl();
		return caller.call("SCS","SCS0015",g001,g002);
	}
	/**
	 * 调用冲正接口
	 * @param dpRequest
	 * @param orderCode
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet reverse(DpInf01015Request dpRequest, String orderCode) throws Exception {
		
		IParamGroup g401 = new ParamGroupImpl("401");
		g401.put("4002", orderCode);  //订单编码
		g401.put("4230", "0001");    //操作类型
		g401.put("4144", "80");      //渠道类型编码
		g401.put("4007", dpRequest.getTmnNum());         //终端号
		g401.put("4017", dpRequest.getKeep());        //终端流水号(本次冲正的keep)
		g401.put("4028", dpRequest.getOrderSeq());// 外部订单号
		g401.put("4146", "00");                          //操作员
		g401.put("4152", dpRequest.getRemark1());        //备注说明
		g401.put("4142", "OT101");    //操作类型编码
		g401.endRow();
		
		IServiceCall caller = new ServiceCallImpl();
		return caller.call("SCS","SCS0102",g401);
	}

}
