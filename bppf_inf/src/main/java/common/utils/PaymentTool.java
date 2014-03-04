package common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import common.dao.TCumInfoDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.xml.dp.DpInf05001Request;
import common.xml.dp.DpInf05103Request;


public class PaymentTool {

	/**
	 * 调用CUM0002,根据客户编码获得该客户天讯资金账户号
	 */
	public static String getTissonCardAcct(String agentCode) throws Exception {
		
		// 查询明细信息
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "2002");// 查询条件：客户编码
		g002.put("0022", agentCode);// 查询条件值
		
		// 组成数据包,调用CUM0002接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet packageDataSet = caller.call("BIS", "CUM0002", g002);
		
		// 获取返回值
		int count = packageDataSet.getParamSetNum("207");
		String cardAcctNbr = null;
		for(int i=0; i<count; i++) {
			// 获取卡户类型
			String cardAcctType = (String) packageDataSet.getParamByID("2048", "207").get(i);
			// 获取天讯资金账户号
			if(cardAcctType.equals("ACCT002")) {
				cardAcctNbr = (String) packageDataSet.getParamByID("2049", "207").get(i);
				break;
			}
		}
		return cardAcctNbr;
	}
	/**
	 * 调用CUM0013 获取网点编码
	 * @param contractId  签约ID
	 * @param transAccNo  银行卡号
	 * @param netCode     网点编码
	 * @return
	 * @throws Exception
	 */
	public static String getNetCode(String contractId ,String transAccNo,String netCode) throws Exception{
		String custCode;
		
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0011", "207");
		g002.endRow();
		
		IParamGroup g207 = new ParamGroupImpl("207");
		if(!Charset.isEmpty(contractId )){
			g207.put("0021", "2071");
			g207.put("0022", contractId);
			g207.endRow();
		}
		
		if(!Charset.isEmpty(transAccNo )){
			g207.put("0021", "2049");
			g207.put("0022", transAccNo);
			g207.endRow();
		}
		
		// 组成数据包,调用CUM0013接口
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM0013", g002, g207);
		
		String resultCode1 = (String) dataSet.getParamByID("0001", "000").get(0);
		//返回结果为失败时，抛出异常
		if(Long.valueOf(resultCode1) != 0) {
			String resultMsg = (String) dataSet.getParamByID("0002", "000").get(0);
			throw new Exception(resultMsg);
		}
		
		custCode = dataSet.getByID("2002", "201");// 获取接口的201组的2002参数
//		if (Charset.isEmpty(custCode)) {
//			throw new Exception("无对应的网点编码");
//		}
		if(netCode!=null&&!netCode.equals("")&&!custCode.equals(netCode)){
			throw new Exception("网点编码输入不正确");
		}
		return  custCode;
	}
	
	
}
