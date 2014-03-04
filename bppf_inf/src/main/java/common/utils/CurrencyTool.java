package common.utils;

import java.math.BigDecimal;

public class CurrencyTool {
	/**
	 * 从接口中获取交易金额
	 * @version 1.00
	 * @history: 2010-12-28 下午5:52:11 [created]
	 * @author Leyi Tang 唐乐毅
	 * @param money 交易金额
	 * @return String
	 * @see
	 */
	public static String fen2Yuan(String money) {
		BigDecimal yuan = new BigDecimal(money).divide(new BigDecimal("100"));
		String yuanString = yuan.setScale(2).toString();
		return yuanString;
	}
	
	public static String fen2Yuan(String money, int scale) {
		BigDecimal yuan = new BigDecimal(money).divide(new BigDecimal("100"));
		String yuanString = yuan.setScale(scale).toString();
		return yuanString;
	}
	
	public static String yuan2Fen(String money) {
		BigDecimal fen = new BigDecimal(money).multiply(new BigDecimal("100"));
		return fen.setScale(0, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	//返回-1 表示m1 < m2; 0 m1 = m2; 1 m1 > m2
	public static int compare(String m1, String m2) {
		BigDecimal b1 = new BigDecimal(m1);
		BigDecimal b2 = new BigDecimal(m2);
		return b1.compareTo(b2);
	}
	
	public static void main(String[] args) {
		String yuanStr = fen2Yuan("10000999898956550");
		System.out.println(yuanStr);
		
		String fenStr = yuan2Fen("1234500.1201");
		System.out.println(fenStr);
	}

}
