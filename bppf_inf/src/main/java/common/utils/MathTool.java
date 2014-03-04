package common.utils;

import java.text.DecimalFormat;

public class MathTool {
	
	/**
	 * 单位换算,元转分
	 * @version: 1.00
	 * @history: 2012-4-7 下午07:21:35 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param amount
	 * @return
	 * @see
	 */
	public static String yuanToPoint(String amount) {
		if(amount ==null || "".equals(amount))
			amount = "0";
		
		Double amountD=Double.parseDouble(amount);
		amountD = amountD * 100;
		DecimalFormat df = new DecimalFormat("0.##");
//		DecimalFormat df = new DecimalFormat("###0.00");
		return df.format(amountD);
	}
	
	/**
	 * 单位换算,分转元
	 * @version: 1.00
	 * @history: 2012-4-7 下午07:21:35 [created]
	 * @author Zhilong Luo 罗志龙
	 * @param amount
	 * @return
	 * @see
	 */
	public static String pointToYuan(String amount) {
		if(amount ==null || "".equals(amount))
			amount = "0";
		Double amountD = Double.parseDouble(amount)/100;
		DecimalFormat df = new DecimalFormat("0.##");;//不用科学计数法
//		return amountD.toString();
		return df.format(amountD);  
	}
	
	
	public static void main(String[] args) {
		System.out.println(yuanToPoint(""));
	}
}
