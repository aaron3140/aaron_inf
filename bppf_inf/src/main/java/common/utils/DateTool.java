package common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cxf.common.util.StringUtils;

public class DateTool {
	
	
	/**
	 * 验证日期是否为yyyyMMddhhmmss格式
	 * @param date
	 * @return
	 */
	public static boolean validateDateFormat(String date) {
		String regex = "^\\d{4}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])(([0|1|]\\d)|2[0-3])([0-5]\\d)([0-5]\\d)$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(date);	
		return m.matches();
	}
	
	

	/**
	 * @version: 1.00
	 * @history: 2010-12-29 上午10：31：05 [created]
	 * @author Leyi Tang 唐乐毅
	 * 格式化日期  的公共类
	 * @param format 格式
	 * @return String
	 */
	public static String formatCurDate(String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		String s = simpleDateFormat.format(new Date());//格式化日期
		return s;
	}

	/**
	 * @version: 1.00
	 * @history: 2010-12-28 上午10：25：13 [created]
	 * @author Leyi Tang 唐乐毅
	 * 格式化日期 yyyymmdd 
	 * 例如：20101228
	 * @return String
	 */
	public static String getCurDate1()
	{
		return formatCurDate("yyyyMMdd");//格式化日期yyyymmdd
	}
	
	/**
	 * @version: 1.00
	 * @history: 2010-12-28 上午10：27：06 [created]
	 * @author Leyi Tang 唐乐毅
	 * 格式化日期yyyy-MM-dd HH:mm:ss
	 * 例如：2010-12-28 10:31:11
	 * @return String
	 */
	public static String getCurDate2()
	{
		return formatCurDate("yyyy-MM-dd HH:mm:ss");//格式化日期yyyy-MM-dd HH:mm:ss
	}	
	
	/**
	 * @version: 1.00
	 * @history: 2010-12-28 上午10：29：26 [created]
	 * @author Leyi Tang 唐乐毅
	 * 格式化日期yyyyMMddHHmmss
	 * 例如：20101228103111
	 * @return String
	 */
	public static String getCurDate3()
	{
		return formatCurDate("yyyyMMddHHmmss");//格式化日期yyyyMMddHHmmss
	}
	
	/**
	 * 格式化时间 yyyyMMddHHmmss
	 * @param time
	 * @return
	 */
	@SuppressWarnings("unused")
	public static  String formatDate(String time){
		 
		 String formatedTime = "";
		 if(!StringUtils.isEmpty(time)){
			 formatedTime = time.substring(0,4)
			 .concat(time.substring(5,7))
			 .concat(time.substring(8,10))
			 .concat(time.substring(11,13))
			 .concat(time.substring(14,16))
			 .concat(time.substring(17,19));
		 }
		 
		 return formatedTime;
	 }
	public  static String afterSeconds( long secondCount) {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(new Date()
				.getTime()
				+ 1000 * secondCount));
	}


	/**
	 * @version: 1.00
	 * @history: 2013-3-1 上午10：31：05 [created]
	 * @author xiaojun zhu 
	 * 格式化日期  的公共类
	 * @param date   日期
	 * @param format 返回格式  如 yyyyMMddHHmmss
	 * @return
	 */
	public static String getStrDate(Date date,String format) {
		if(date==null)
			date = new Date();
		if(format==null)
			format = "yyyyMMddHHmmss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		String sdate = simpleDateFormat.format(date);
		
		Calendar curr = Calendar.getInstance();
		curr.set(Calendar.YEAR,curr.get(Calendar.YEAR)+1);
		Date nextyeardate=curr.getTime();
		
		return sdate;
	}

	/**
	 * 获得当前时间前的时间
	 * @version: 1.00
	 * @history: 2013-3-1 上午10：31：05 [created]
	 * @author xiaojun zhu 
	 * @param model  时间类型  如: 1 Calendar.YEAR  2:Calendar.MONTH
	 * @param format 时间格式  如 yyyyMMddHHmmss
	 * @return
	 */
	public static String getBerforDate(int model,String format) {
		Calendar curr = Calendar.getInstance();
		curr.set(Calendar.YEAR,curr.get(model)-1);
		Date date=curr.getTime();
		String strdate = getStrDate(date,format);
		return strdate;
	}
	/**
	 * 判断给定时间是早于还是晚于当前时间
	 * 
	 * @param date
	 * @return
	 */
	private static boolean befornow(Date date) {
		boolean falg = false;
		if (date == null)
			return false;
		falg = date.before(new Date());
		return falg;
	}

	/**
	 * 得到某天的最后一个时刻23:59:59,标志某天的结束
	 * 
	 * @return 某天的最后日期，某天23：59：59
	 */
	public static Date getTodayLast(String strdate) {
		Date currentDate = null;
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
		"yyyyMMddhhmmss");
		try {
			Date date = formatter.parse(strdate);
			 GregorianCalendar gregorianCalendar = new GregorianCalendar();
		        gregorianCalendar.setTime(date);
				gregorianCalendar.set(Calendar.HOUR_OF_DAY, 23);
				gregorianCalendar.set(Calendar.MINUTE, 59);
				gregorianCalendar.set(Calendar.SECOND, 59);
				currentDate = gregorianCalendar.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currentDate;
	}
	/**
	 * 交易退款T+1判断
	 * 
	 * @param date
	 * @return
	 */
	public static boolean beforOneDay(String strdate) {
		boolean falg = false;
		 Date lastdate = getTodayLast(strdate);
		 System.out.println(DateTool.getStrDate(lastdate, null));
		 falg = befornow(lastdate);
		return falg;
	}
	
	/**
	 * @description 将形如"20130807171001"的字符串转换为"yyyy-MM-dd HH:mm:ss"或者"yyyy-MM-dd"格式的字符串
	 * @date 2013-09-16 14:52
	 * @author lichunan
	 * @param str
	 * @param toFormat
	 * @return
	 */
	public static String strToDateFormatStr(String str, String toFormat){
		
		if(StringUtils.isEmpty(str)){
			return null;
		}
		
		if(StringUtils.isEmpty(toFormat)){
			toFormat = "yyyy-MM-dd HH:mm:ss";
		}
		
		int length = str.length();
		if(length < 8){
			return null;
		}
		
		String year = str.substring(0, 4);
		String month = str.substring(4, 6);
		String day = str.substring(6, 8);
		
		StringBuffer strBuffer = new StringBuffer("");
		strBuffer.append(year);
		strBuffer.append("-");
		strBuffer.append(month);
		strBuffer.append("-");
		strBuffer.append(day);
		
		if("yyyy-MM-dd".equalsIgnoreCase(toFormat)){
			return strBuffer.toString();
		}
		
		if("yyyy-MM-dd HH:mm:ss".equalsIgnoreCase(toFormat)){
			if(length == 14){
				String hour = str.substring(8, 10);
				String minute = str.substring(10, 12);
				String second = str.substring(12, 14);
				strBuffer.append(" ");
				strBuffer.append(hour);
				strBuffer.append(":");
				strBuffer.append(minute);
				strBuffer.append(":");
				strBuffer.append(second);
			}else{
				return null;
			}
		}
		
		return strBuffer.toString();
	}
	public static void main(String[] args) throws ParseException {

		System.out.println(DateTool.beforOneDay("20130520155911"));

	}

}
