package common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.dao.TSymCustomDao;
import common.entity.VerifyConsumeEntity;
import common.platform.invoker.bean.IParamGroup;
import common.platform.invoker.bean.ParamGroupImpl;
import common.platform.invoker.caller.IServiceCall;
import common.platform.invoker.caller.ServiceCallImpl;
import common.platform.provider.server.PackageDataSet;

import framework.exception.INFErrorDef;
import framework.exception.INFException;

/**
 * 快捷交易消费验证工具类
 * 
 * @author sonsy
 * 
 */
public class WebSvcUtil {

	 private String sum_stat = "S0A";
	 
	 private static final Log logger = LogFactory.getLog(WebSvcUtil.class);
	/**
	 * 验证交易额,有交易密码时验证交易密码
	 * @param entity
	 * @return 返回是否需要清空累计金额
	 * @throws Exception
	 */
	public boolean VerifyConsume(VerifyConsumeEntity entity) throws Exception {

		boolean flag = false;
		TSymCustomDao buss = new TSymCustomDao();
		String totalAmount = buss.getTh(entity.getCustId(), "T22", "TH005");//累积限额
		String oneAmount = buss.getTh(entity.getCustId(), "T22", "TH004");//单笔限额
//		int amount = Integer.parseInt(entity.getTxnAmount()); //当前交易金额
		double amount = Double.parseDouble(entity.getTxnAmount()); //当前交易金额
		
		logger.info("totalAmount::::::::"+totalAmount);
		logger.info("amount::::::::"+amount);
		logger.info("oneAmount::::::::"+oneAmount);
		
		
		if (!"0".equals(totalAmount)) {//有设置快捷交易
			String allTrade = buss.getAmountCount(entity.getCustId()); //消费累计
//			int allAmount = amount + Integer.parseInt(allTrade);//消费累计,包括本次交易
			double allAmount = amount + Double.parseDouble(allTrade);//消费累计,包括本次交易
			
			logger.info("allTrade::::::::"+allTrade);
			logger.info("allAmount::::::::"+allAmount);
//			if (allAmount > Integer.parseInt(totalAmount)|| amount > Integer.parseInt(oneAmount)) {//超过累积限额  或  超过单笔限额
			if (allAmount > Double.parseDouble(totalAmount)|| amount > Double.parseDouble(oneAmount)) {//超过累积限额  或  超过单笔限额
				
				
				//校验交易密码
				String pwd = entity.getPayPassword();
				if (Charset.isEmpty(pwd, true)) {
					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
							INFErrorDef.PAY_PWD_FAULT_NULL);
				} else {
					PackageDataSet dataSet = callCUM1003(entity);
					String resCode = dataSet.getByID("0001", "000");
					if (Long.valueOf(resCode) != 0) {
						String num = dataSet.getByID("6047", "601");
						throw new INFException(INFErrorDef.PAY_PWD_FAULT,INFErrorDef.PAY_PWD_FAULT_DESC+num+"次");
					}
				}
				// 校验通过
//				if(amount > Integer.parseInt(oneAmount)){ //超出单笔限额  (不清累计  and  本笔不记入累计)
				if(amount > Double.parseDouble(oneAmount)){ //超出单笔限额  (不清累计  and  本笔不记入累计)
					setSum_stat("S0N");//本次交易不记录累计
					System.out.println("WebSvcUtil====="+getSum_stat());
					return false;
				}
				else //超出累积限额  全部清零
//					if (allAmount > Integer.parseInt(totalAmount))
					flag = true;
			}
		} else {//没有设置快捷交易  校验密码
			String pwd = entity.getPayPassword();
			if (Charset.isEmpty(pwd, true)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
						INFErrorDef.PAY_PWD_FAULT_NULL);
			} else {
				PackageDataSet dataSet = callCUM1003(entity);
				String resCode = dataSet.getByID("0001", "000");
				if (Long.valueOf(resCode) != 0) {
					String num = dataSet.getByID("6047", "601");
					throw new INFException(INFErrorDef.PAY_PWD_FAULT,INFErrorDef.PAY_PWD_FAULT_DESC+num+"次");
				}
			}
		}
		return flag;
	}

	public boolean VerifyConsume1(VerifyConsumeEntity entity) throws Exception {

		boolean flag = false;
		TSymCustomDao buss = new TSymCustomDao();
		String totalAmount = buss.getTh(entity.getCustId(), "T22", "TH005");//累积限额
		String oneAmount = buss.getTh(entity.getCustId(), "T22", "TH004");//单笔限额
		int amount = Integer.parseInt(entity.getTxnAmount()); //当前交易金额
		
		if (!"0".equals(totalAmount)) {//有设置快捷交易
			String allTrade = buss.getAmountCount(entity.getCustId()); //消费累计
			int allAmount = amount + Integer.parseInt(allTrade);//消费累计,包括本次交易
			if (allAmount > Integer.parseInt(totalAmount)|| amount > Integer
							.parseInt(oneAmount)) {//超过累积限额  或  超过单笔限额
				
				//校验交易密码
//				String pwd = entity.getPayPassword();
//				if (Charset.isEmpty(pwd, true)) {
//					throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
//							INFErrorDef.PAY_PWD_FAULT_NULL);
//				} else {
//					PackageDataSet dataSet = callCUM1003(entity);
//					String resCode = dataSet.getByID("0001", "000");
//					if (Long.valueOf(resCode) != 0) {
//						throw new INFException(INFErrorDef.PAY_PWD_FAULT,
//								INFErrorDef.PAY_PWD_FAULT_DESC);
//					}
//				}
				// 校验通过
				if(amount > Integer.parseInt(oneAmount)){ //超出单笔限额  (不清累计  and  本笔不记入累计)
					setSum_stat("S0N");//本次交易不记录累计
					System.out.println("WebSvcUtil====="+getSum_stat());
					flag = false;
				}
				else //超出累积限额  全部清零
//					if (allAmount > Integer.parseInt(totalAmount))
					flag = true;
			}
		} /*else {//没有设置快捷交易  校验密码
			String pwd = entity.getPayPassword();
			if (Charset.isEmpty(pwd, true)) {
				throw new INFException(INFErrorDef.CUSTCODE_NOT_MATCH,
						INFErrorDef.PAY_PWD_FAULT_NULL);
			} else {
				PackageDataSet dataSet = callCUM1003(entity);
				String resCode = dataSet.getByID("0001", "000");
				if (Long.valueOf(resCode) != 0) {
					throw new INFException(INFErrorDef.PAY_PWD_FAULT,
							INFErrorDef.PAY_PWD_FAULT_DESC);
				}
			}
		}*/
		return flag;
	}
	
	/**
	 * 验证消费类交易密码
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	 static PackageDataSet callCUM1003(VerifyConsumeEntity entity)
			throws Exception {
		String staff = entity.getStaffCode();
		String verityType = "0001"; // 支付密码
		String tmnNum = entity.getTmnNum();
		IParamGroup g200 = new ParamGroupImpl("200");
		g200.put("2901", "2171");
		g200.put("2902", staff);
		g200.put("2903", "2007");
		g200.put("2904", entity.getPayPassword());
		g200.put("2172", "0001");
		g200.put("2173", verityType);
		// g200.put("2025", null);
		g200.endRow();

		IParamGroup g211 = new ParamGroupImpl("211");
		g211.put("2076", entity.getChannelCode());
		g211.put("2077", tmnNum);
		g211.put("2078", null);
		g211.put("2085", entity.getIp());
		g211.endRow();

		IServiceCall caller = new ServiceCallImpl();
		PackageDataSet dataSet = caller.call("BIS", "CUM1003", g200, g211);

		return dataSet;
	}

	public String getSum_stat() {
		return sum_stat;
	}

	public void setSum_stat(String sumStat) {
		sum_stat = sumStat;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		WebSvcUtil.setSum_stat("S0N");
//		System.out.println(WebSvcUtil.getSum_stat());
	}

}
