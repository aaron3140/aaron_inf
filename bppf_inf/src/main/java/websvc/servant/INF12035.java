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
import common.service.SignBankManage;
import common.service.TInfOperInLogManager;
import common.utils.DateTime;
import common.utils.WebSvcTool;
import common.xml.RespInfo;
import common.xml.dp.DpInf12035Request;
import common.xml.dp.DpInf12035Response;

import framework.exception.ExceptionHandler;
import framework.exception.INFErrorDef;
import framework.exception.INFException;
import framework.exception.INFLogID;
import framework.exception.XmlINFException;

public class INF12035 {

	public static String svcInfName = "INF12035";
	
	private static final Logger LOG = Logger.getLogger(INF12035.class);
	
	public static String execute(String in0, String in1) {
		
		//本接口的字段信息包含公共信息
		DpInf12035Request dpRequest = null;
		
		// 返回信息头
		RespInfo respInfo = null; 
		
		//SAG网关类
		SagManager sagManager = new SagManager();
		
		//日志类
		TInfOperInLog tInfOperInLog = null;
		
		//获取服务编码
		String svcCode = WebSvcTool.getSvcCode(in0);
		
		//用于封装返回的xml报文
		DpInf12035Response resp = null;
		
		//svcInfName&partyGroup
		INFLogID infId = new INFLogID(svcInfName, TInfDcoperlogDao.PARTY_GROUP_AG); 
		
		try {
			
			//一系列的验证和接口参数的设置
			dpRequest = new DpInf12035Request(in1);
			
			// 根据in1这个xml文件设置返回的信息头
			respInfo = new RespInfo(in1, dpRequest.getChannelCode());
			
			TInfOperInLogManager manager = new TInfOperInLogManager();

			// 插入信息到入站日志表
			tInfOperInLog = sagManager.insertTInfOperInLog(dpRequest.getKeep(),dpRequest.getIp(), dpRequest.getTmnNum(), svcCode, "XML","agentCode", dpRequest.getCustCode(), "", "", "S0A");
			
			// 判断插入是否成功
			if (tInfOperInLog != null) {
				boolean flag = manager.selectTInfOperInLogByKeep(dpRequest.getKeep());//根据流水号查询入站日志
				// 判断流水号是否可用
				if (flag) {//流水号对应的日志数大于一，重复不允许交易
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 1);
					throw new INFException(INFErrorDef.POSSEQNO_CONFLICT_CODE,INFErrorDef.POSSEQNO_CONFLICT_REASON);
				} else {
					flag = manager.updateAllow(tInfOperInLog.getOperInId(), 0);
				}
			}
			
			// 业务组件
			SignBankManage manage = new SignBankManage();

			// 获取客户ID
			String custId = manage.getCustIdByCode(dpRequest.getCustCode());
			
			if (null == custId || "".equals(custId)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_EXIST,
						INFErrorDef.CUSTCODE_NOT_EXIST_DESC);
			}
			
			// 业务编码
			String actionCode = "01010013";

			// 产品编码
			String proudCode = "04040800";
			
			String responseDesc = "";

			String resultCode = "";
			
			String searchId ="";
			
			PackageDataSet ds = null;
			
			ds = sag0001(dpRequest, actionCode, proudCode);
			
			resultCode = (String) ds.getParamByID("0001", "000").get(0);
			
			List<List<Map<String, String>>> list = new ArrayList<List<Map<String, String>>>();
			
			// 返回结果为失败时，获取结果描述
			if (Long.valueOf(resultCode) == 0) {

				responseDesc = (String) ds.getParamByID("0002", "000").get(0);
				searchId = (String) ds.getParamByID("6923", "692").get(3);
				list = unpack(ds);

			}
			
			// 插入信息到出站日志表
			sagManager.insertTInfOperOutLog(tInfOperInLog.getOperInId(),
					dpRequest.getKeep(), dpRequest.getIp(), svcCode, "",
					"000000", "S0A");
			
			resp = new DpInf12035Response();
			
			String oXml = resp.toXMLStr(respInfo.getReqWebsvrCode(), 
					respInfo.getRespType(), 
					respInfo.getKeep(), 
					"SUCCESS", 
					resultCode,
					responseDesc, 
					searchId, 
					ds.getByID("6900", "690"), 
					list,
					dpRequest.getRemark1(),
					dpRequest.getRemark2()); 
			
			return oXml;
			
		}catch (XmlINFException spe) {
			spe.setRespInfo(respInfo);
			return ExceptionHandler.toXML(spe, infId);
		}catch (Exception e) {
			return ExceptionHandler.toXML(
					new XmlINFException(resp, e, respInfo), infId);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<List<Map<String, String>>> unpack(PackageDataSet ds) {
		String count = (String) ds.getParamByID("6900", "690").get(0);

		ArrayList value6920 = ds.getParamByID("6920", "692");
		ArrayList value6923 = ds.getParamByID("6923", "692");
		List<List<Map<String, String>>> data = new ArrayList<List<Map<String, String>>>();

		int lenght = Integer.parseInt(count);
		int num = 10;// R**每一个账单有21个参数

		for (int i = 0; i < lenght; i++) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			data.add(list);
			int startIndex = i * num + 4;// 本次循环取数据开始的索引【包含】
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
	
	private static PackageDataSet sag0001(DpInf12035Request dpRequest,
			String actionCode, String proudCode) throws Exception{
		
		/**
		 * 调用SAG0001,完成交易操作
		 */
		
		// 订单受理信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "GOC101");
		g675.put("6752", dpRequest.getChannelCode60To20());
		g675.put("6753", dpRequest.getKeep());
		g675.put("6754", DateTime.nowDate8Bit());// 
		g675.put("6755", DateTime.nowTime6Bit());// 
		g675.put("6756", "INF");// 

		g675.endRow();

		// 订单费用信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", actionCode);// 
		g676.put("6762", proudCode);// 
		g676.put("6763", dpRequest.getAcceptAreacode());// 
		g676.put("6764", dpRequest.getMerId());// 
		g676.put("6765", dpRequest.getTmnNum());// 

		g676.endRow();

		// 业务单信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", "");// 
		g680.put("6802", "");// 
		g680.put("6803", "");//
		g680.put("6804", "");//
		g680.put("6805", "");// 
		g680.put("6806", "");// 
		g680.put("6807", "");//
		g680.put("6808", DateTime.nowDate14Bit());// 

		g680.endRow();

		// 业务单费用信息
		IParamGroup g682 = new ParamGroupImpl("682");

		g682.put("6820", "K001");//
		g682.put("6821", "出发站名称");// 
		g682.put("6822", "01");// 
		g682.put("6823", dpRequest.getFromStation());//
		
		g682.endRow();
		
		g682.put("6820", "K002");//
		g682.put("6821", "到达站名称");// 
		g682.put("6822", "01");// 
		g682.put("6823", dpRequest.getToStation());// 

		g682.endRow();
		
		g682.put("6820", "K003");//
		g682.put("6821", "出发日期");// 
		g682.put("6822", "01");// 
		g682.put("6823", dpRequest.getDate());// 

		g682.endRow();

		// 组成数据包,调用SAG0001接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("SAG", "SAG0001", g675, g676,
				g680, g682);
		
		return dataSet;
	}
	
}
