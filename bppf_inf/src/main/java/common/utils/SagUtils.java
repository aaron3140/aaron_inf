package common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class SagUtils {
	private static int counter = 0;
	
	/**
	 * 生成流水号：规则：当前时间+N位随机数字
	 * @param dateFormat
	 * @param n
	 * @return
	 */
	public static String getSeqNbr(String dateFormat,int n) {
		DateFormat df = new SimpleDateFormat(dateFormat);
		String time = df.format(new Date());
//		Long random = (long) (Math.random()*Math.pow(10, n));
//		String seqNbr = time + random.toString();//0000-9999
		
		String seqNbr = time + StringUtils.leftPad(counter + "", 4, '0');
		if (counter ++== 9999) {
			counter = 0;
		}
		return seqNbr;
	}
	
	public static String getSeqNbr6(int n) {
//		DateFormat df = new SimpleDateFormat(dateFormat);
//		String time = df.format(new Date());
		Long random = (long) (Math.random()*Math.pow(10, n));
		String seqNbr = random.toString();
		return seqNbr;
	}
	
	public static void main(String[] args) {
		for(int i =0 ; i<100000; i++)
		{
			System.out.println(i  + ": " + getSeqNbr("yyyyMMddhhmmss", 4));
		}
		System.out.println(getSeqNbr6(6));
	}
}
