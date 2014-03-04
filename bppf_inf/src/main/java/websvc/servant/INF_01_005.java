package websvc.servant;

import common.dao.TInfDcoperlogDao;
import common.entity.TInfOperInLog;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.service.SagManager;
import common.service.TInfOperInLogManager;
import common.utils.MathTool;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpTransactionDetailQueryRequest;
import common.xml.dp.DpTransactionDetailQueryResponse;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

/**
 *  交易查询接口INF_01_005
 */
public class INF_01_005 {

	 public static String svcInfName = "01_005";

	public static void main(String[] args) {
		
	}

	public static String execute(String in0, String in1) {
		//Long pk = null;
		DpTransactionDetailQueryRequest dpTransactionDetailQueryRequest = null;
		DpTransactionDetailQueryResponse resp = new DpTransactionDetailQueryResponse();
		RespInfo respInfo = null;				// 返回信息头
		String transSeq=null;	
		String tmnNum = null;	//受理终端号
		
		SagManager sagManager = new SagManager();
		TInfOperInLog tInfOperInLog = null;
		String responseCode = "";
		String keep = ""; //		获取流水号
		String ip = "";
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		INFLogID id = new INFLogID(svcInfName,TInfDcoperlogDao.PARTY_GROUP_AG);
		try{
			respInfo = new RespInfo(in1, "20");				// 返回信息头
			dpTransactionDetailQueryRequest = new DpTransactionDetailQueryRequest(in1);
			
			transSeq = dpTransactionDetailQueryRequest.getTransSeq();
			tmnNum = dpTransactionDetailQueryRequest.getTmnNum();
			keep = dpTransactionDetailQueryRequest.getKeep();
			ip = dpTransactionDetailQueryRequest.getIp();
			
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
					
			//写日志 
//			pk = TInfDcoperlogDao.insert(TInfDcoperlogDao.PARTY_GROUP_AG,
//					svcInfName, svcCode, SocketConfig.getSockIp(),"ORDERCODE",transSeq);
//			id.setPk(pk);
						
			//调用SCS0005
			IParamGroup g002 = new ParamGroupImpl("002");// 包头
			g002.put("0021", "4002");
			g002.put("0022", transSeq);
			g002.endRow();			
			
			IServiceCall caller = new ServiceCallImpl();
			PackageDataSet packageDataSet = caller.call("SCS","SCS0005",g002);// 组成交易数据包,调用SCS0005接口
			
			String resultCode = (String) packageDataSet.getParamByID("0001", "000").get(0);
			responseCode = resultCode;
			//返回结果为失败时，抛出异常
			if(Long.valueOf(resultCode) != 0) {
				String resultMsg = (String) packageDataSet.getParamByID("0002", "000").get(0);
				throw new Exception(resultMsg);
			}
			
			responseCode = packageDataSet.getByID("0001", "000");// 获取接口的000组的0001参数
			String responseDesc = packageDataSet.getByID("0002", "000");// 获取接口的000组的0002参数
			
//			 返回响应码
			int count401 = packageDataSet.getParamSetNum("401");
			if(count401 == 0) {
				throw new Exception("无此订单信息");
			}
			
			String returnOrderCode=packageDataSet.getByID("4002", "401");//订单编号
			String returnChannelName = packageDataSet.getByID("4144", "401");//受理渠道名称//////
			String returnChannelCode = packageDataSet.getByID("4007", "401");//终端号
			String returnOrderTime = packageDataSet.getByID("4008", "401");//订单受理时间
			String returnMemo = packageDataSet.getByID("4012", "401");//订单备注
			String returnOrderStat = packageDataSet.getByID("4013", "401");//订单状态////////
			// 反向翻译订单状态
			returnOrderStat = transOrderStatReverse(returnOrderStat);
			String returnRecvCode =  packageDataSet.getByID("4052", "404");//收款商户编码//////
			String returnPayCode =  packageDataSet.getByID("4004", "401");//付款商户编码//////
			String returnTransCode =  packageDataSet.getByID("4017", "401");//付款商户编码//////
			
			String  returnOrderType=packageDataSet.getByID("4051", "404");//订单类型/////////
			if (returnOrderType.equals("01030001")){
				returnOrderType="直接交易";
			}
			if (returnOrderType.equals("01030005")){
				returnOrderType="担保交易";
			}
			else if(returnOrderType.equals("01030007")){
				returnOrderType="预授权";
			}
			//获取订单支付金额
			String returnOrderAmount = packageDataSet.getByID("4023", "402");
			String orderAmountType = packageDataSet.getByID("4022", "402");
			if(orderAmountType.equals("元")) {
				returnOrderAmount = MathTool.yuanToPoint(returnOrderAmount);
			}
			
			//插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(), keep, ip, svcCode, responseCode, responseDesc, "S0A");
			
			resp = new DpTransactionDetailQueryResponse();
			return resp.toXMLStr(respInfo.getReqWebsvrCode(), respInfo.getRespType(), respInfo.getKeep(),
					"SUCCESS", responseCode, responseDesc, returnOrderCode, returnOrderType, returnChannelName,
					returnChannelCode, returnOrderTime, returnMemo, returnOrderStat, returnOrderAmount, 
					returnRecvCode,returnPayCode,returnTransCode);
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
	 * @version: 1.00
	 * @history: 2012-2-22 下午09:56:10 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param orderstat
	 * @return
	 * @see
	 */
	private static String transOrderStatReverse(String orderstat) {
		if(orderstat.equals("S0C")) {
			return "1";
		} else if(orderstat.equals("S0F")) {
			return "2";
		} else if(orderstat.equals("S0A")) {
			return "3";
		} else if(orderstat.equals("S0V")) {
			return "4";
		} else if(orderstat.equals("S0U")) {
			return "5";
		} else if(orderstat.equals("S0D")) {
			return "6";
		} else {
			return "";
		}
	}

}

