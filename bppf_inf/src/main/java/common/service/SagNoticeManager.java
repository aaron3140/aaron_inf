package common.service;

import common.dao.TCumInfoDao;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;
import common.utils.DateTime;
import common.xml.dp.DpInf06204Request;
import common.xml.dp.DpInf06205Request;

/**
 * @author 邱亚建 2014-1-19 上午10:44:30<br>
 * 
 *         本来描述：封装开卡，开卡冲正，充值，充值冲正业务
 */
public class SagNoticeManager {

	/**
	 * 
	 * @param dpRequest
	 * @param isActivate
	 * @return
	 * @throws Exception
	 */
	private static PackageDataSet rechargeOrActivate(DpInf06205Request dpRequest, boolean isActivate) throws Exception {
		// 通过客户编码查区域编码
		String areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());

		// 包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", "COC202");// 服务编码
		g675.put("6752", dpRequest.getChannelCode());// 渠道号
		g675.put("6753", dpRequest.getKeep());// 流水号
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码
		g675.endRow();

		// 业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		g676.put("6761", "12010001");// 业务编码
		g676.put("6762", "0012");// 产品编码
		g676.put("6763", areaCode);// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();

		// 查询信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", "");
		g680.put("6802", "");
		g680.put("6803", "");
		g680.put("6804", "");
		g680.put("6805", "");
		g680.put("6806", "");
		g680.put("6807", "");
		g680.put("6808", dpRequest.getTradeTime());// 20130508151906
		g680.endRow();

		// 查询项
		IParamGroup g682 = new ParamGroupImpl("682");
		g682.put("6820", "X001");
		g682.put("6821", "xml");// 订单项名称
		g682.put("6822", "01");

		g682.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SAG", "SAG0002", g675, g676, g680, g682);

		return ds;
	}

	/**
	 * 开卡，开卡充正
	 * 
	 * @param dpRquest
	 * @param isOpen
	 *            true 开卡 false 开卡冲正
	 * @return
	 * @throws Exception
	 */
	public static PackageDataSet openCard(DpInf06204Request dpRequest, boolean isOpen) throws Exception {
		// 通过客户编码查区域编码
		String areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());
		String actionCode = "12010004";
		String serCode = "";
		String serName = "";
		String orderDesc = "";
		String txnamt = "0";//开卡与开卡冲正交易金额均为固定值0
		if(isOpen){//开卡
			serCode = "COC401";
			serName = "SAG0004";
			orderDesc = "开卡参数";
		}else{//开卡冲正
			serCode = "COC902";
			serName = "SAG0009";
			orderDesc = "开卡冲正参数";
		}
		
		// 包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", serCode);// 服务编码
		g675.put("6752", dpRequest.getChannelCode());// 渠道号
		g675.put("6753", dpRequest.getKeep());// 流水号
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码
		g675.endRow();

		// 业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		
		g676.put("6761", actionCode);// 业务编码
		g676.put("6762", "0012");// 产品编码
		g676.put("6763", areaCode);// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();

		// 查询信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", "");
		g680.put("6802", "");
		g680.put("6803", "");
		g680.put("6804", "");
		g680.put("6805", "");
		g680.put("6806", "");
		g680.put("6807", "");
		g680.put("6808", dpRequest.getTradeTime());// 查询时间
		g680.put("6809", dpRequest.getSystemNo());// 系统参考号
		g680.endRow();

		// 查询项
		IParamGroup g682 = new ParamGroupImpl("682");
		g682.put("6820", "X001");
		g682.put("6821", orderDesc);// 订单项名称
		g682.put("6822", "01");
		
		
		//String deposit = dpRequest.getDeposit();
		//if (deposit.matches("^0+$")) {
		//	txnamt=(Long.valueOf(txnamt)-2000)+"";
		//}
		
		String params = "<PARAMS>" //
				+ "<POSID>" + dpRequest.getTmnNumNo() + "</POSID>" + //
				"<POSSEQUENCE>" + dpRequest.getOrderSeq() + "</POSSEQUENCE>" + //
				"<TERMID>" + dpRequest.getTmnNumNo() + "</TERMID>" + //
				"<OPERID>" + dpRequest.getTmnNumNo() + "</OPERID>" + //
				"<EDCARDID>" + dpRequest.getEdcardId() + "</EDCARDID>" + //
				"<CARDID>" + dpRequest.getCardId() + "</CARDID>" + //
				"<CARDCNT>" + dpRequest.getCardcnt() + "</CARDCNT>" + //
				"<CARDMKND>" + dpRequest.getCardmknd() + "</CARDMKND>" + //
				"<CARDSKND>" + dpRequest.getCardsknd() + "</CARDSKND>" + //
				"<CARDMODEL>" + dpRequest.getCardModel() + "</CARDMODEL>" + //
				"<SALEMODE>" + dpRequest.getSaleMode() + "</SALEMODE>" + //
				"<DEPOSIT>" + dpRequest.getDeposit() + "</DEPOSIT>" + //
				"<BEFBALANCE>" + dpRequest.getBefbalance() + "</BEFBALANCE>" + //
				"<TXNAMT>" +txnamt+ "</TXNAMT>" + //
				"<TRANSTYPE>" + dpRequest.getTransType() + "</TRANSTYPE>" + 
				"<CARDVALDATE>" + dpRequest.getCardvalDate() + "</CARDVALDATE>" + //
				"<CITYCODE>" + dpRequest.getCityCode() + "</CITYCODE>" + //
				"<CARDVERNO>" + dpRequest.getCardverno() + "</CARDVERNO>" + //
				"<BATCHNO>" + dpRequest.getBatchNo() + "</BATCHNO>" + //
				"<AUTHSEQ>" + dpRequest.getAuthseq() + "</AUTHSEQ>" + //
				"<LIMITEDAUTHSEQL>" + dpRequest.getLimitedauthseql() + "</LIMITEDAUTHSEQL>" + //
				"<TAC>" + dpRequest.getTac() + "</TAC>" + //
				"<TXNDATE>" + dpRequest.getTxnDate() + "</TXNDATE>" + //
				"<TXNTIME>" + dpRequest.getTxnTime() + "</TXNTIME>" + //
				"<KEYSET>" + dpRequest.getKeySet() + "</KEYSET>" + //
				"</PARAMS>"; //
		g682.put("6823", params);

		g682.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SAG", serName, g675, g676, g680, g682);

		return ds;
	}
	/**
	 * 充值，充值充正
	 * 
	 * @param dpRquest
	 * @param isRecharge
	 *            true 充值 false 充值冲正
	 * @return
	 * @throws Exception
	 */
	public static PackageDataSet rechargeCard(DpInf06205Request dpRequest, boolean isRecharge) throws Exception {
		// 通过客户编码查区域编码
		String areaCode = TCumInfoDao.getAreaCode(dpRequest.getCustCode());
		String actionCode = "12010004";
		String serCode = "";
		String serName = "";
		String orderDesc = "";
		if(isRecharge){//充值
			serCode = "COC402";
			serName = "SAG0004";
			orderDesc = "充值参数";
		}else{//充值冲正
			serCode = "COC901";
			serName = "SAG0009";
			orderDesc = "充值冲正参数";
		}
		
		// 包头控制信息
		IParamGroup g675 = new ParamGroupImpl("675");
		g675.put("6751", serCode);// 服务编码
		g675.put("6752", dpRequest.getChannelCode());// 渠道号
		g675.put("6753", dpRequest.getKeep());// 流水号
		g675.put("6754", DateTime.nowDate8Bit());// 发起请求日期格式YYYYMMDD
		g675.put("6755", DateTime.nowTime6Bit());// 发起请求时间格式HH24MISS
		g675.put("6756", "INF");// 接口平台编码
		g675.endRow();
		
		// 业务基础信息
		IParamGroup g676 = new ParamGroupImpl("676");
		
		g676.put("6761", actionCode);// 业务编码
		g676.put("6762", "0012");// 产品编码
		g676.put("6763", areaCode);// 受理区域编码
		g676.put("6764", dpRequest.getMerId());// 前向商户编码
		g676.put("6765", dpRequest.getTmnNum());// 前向商户终端号
		g676.endRow();
		
		// 查询信息
		IParamGroup g680 = new ParamGroupImpl("680");
		g680.put("6801", "");
		g680.put("6802", "");
		g680.put("6803", "");
		g680.put("6804", "");
		g680.put("6805", "");
		g680.put("6806", "");
		g680.put("6807", "");
		g680.put("6808", dpRequest.getTradeTime());// 查询时间
		g680.put("6809", dpRequest.getSystemNo());// 系统参考号
		g680.endRow();
		
		// 查询项
		IParamGroup g682 = new ParamGroupImpl("682");
		g682.put("6820", "X001");
		g682.put("6821", orderDesc);// 订单项名称
		g682.put("6822", "01");
		

		String params = "<PARAMS>" //
				+ "<POSID>" + dpRequest.getTmnNumNo() + "</POSID>" + //
				"<POSSEQUENCE>" + dpRequest.getOrderSeq() + "</POSSEQUENCE>" + //
				"<TERMID>" + dpRequest.getTmnNumNo() + "</TERMID>" + //
				"<OPERID>" + dpRequest.getTmnNumNo() + "</OPERID>" + //
				"<CARDID>" + dpRequest.getCardId() + "</CARDID>" + //
				"<CARDCNT>" + dpRequest.getCardcnt() + "</CARDCNT>" + //
				"<CARDMKND>" + dpRequest.getCardmknd() + "</CARDMKND>" + //
				"<CARDSKND>" + dpRequest.getCardsknd() + "</CARDSKND>" + //
				"<CARDMODEL>" + dpRequest.getCardModel() + "</CARDMODEL>" + //
				"<BEFBALANCE>" + dpRequest.getBefbalance() + "</BEFBALANCE>" + //
				"<ORIGAMT>" + dpRequest.getOrigamt() + "</ORIGAMT>" + //
				"<TXNAMT>" + dpRequest.getTxnamt() + "</TXNAMT>" + //
				"<TRANSTYPE>" + dpRequest.getTransType() + "</TRANSTYPE>" + 
				"<HANDINGCHARGE>" + dpRequest.getHandingCharge() + "</HANDINGCHARGE>" + //
				"<DEPOSIT>" + dpRequest.getDeposit() + "</DEPOSIT>" + //
				"<CARDVALDATE>" + dpRequest.getCardvalDate() + "</CARDVALDATE>" + //
				"<CITYCODE>" + dpRequest.getCityCode() + "</CITYCODE>" + //
				"<CARDVERNO>" + dpRequest.getCardverno() + "</CARDVERNO>" + //
				"<BATCHNO>" + dpRequest.getBatchNo() + "</BATCHNO>" + //
				"<AUTHSEQ>" + dpRequest.getAuthseq() + "</AUTHSEQ>" + //
				"<LIMITEDAUTHSEQL>" + dpRequest.getLimitedauthseql() + "</LIMITEDAUTHSEQL>" + //
				"<LASTPOSSVSEQ>" + dpRequest.getLastpossvseq() + "</LASTPOSSVSEQ>" + //
				"<TAC>" + dpRequest.getTac() + "</TAC>" + //
				"<TXNDATE>" + dpRequest.getTxnDate() + "</TXNDATE>" + //
				"<TXNTIME>" + dpRequest.getTxnTime() + "</TXNTIME>" + //
				"<KEYSET>" + dpRequest.getKeySet() + "</KEYSET>" + //
				"</PARAMS>"; //
		g682.put("6823", params);
		
		g682.endRow();
		
		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SAG", serName, g675, g676, g680, g682);
		
		return ds;
	}
/*
	*//**
	 * 获取系统参考号
	 * 
	 * @param dpRequest
	 * @return
	 * @throws Exception
	 *//*
	private static String callSCS0017GetSystemNo() throws Exception {
		IParamGroup g002 = new ParamGroupImpl("002");
		g002.put("0021", "SCS_ACTNUM");//
		g002.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet ds = caller.call("SCS", "SCS0017", g002);
		String responseCode = (String) ds.getParamByID("0001", "000").get(0);

		if (Long.valueOf(responseCode) == 0) {
			return (String) ds.getParamByID("4062", "404").get(0);
		}
		return "";
	}*/
}
