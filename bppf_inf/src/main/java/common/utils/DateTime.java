package common.utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @see 2008-4-30
 * @author lyz 凌云志
 * @version 1.0
 */

/**
 * 注意：nowDateXXBit的方法为生成网银参数专用
 * 请勿随便修改
 */
public class DateTime {
	
	/**
	 * @see 返回与当前日期相差scale天的日期或时间
	 * @author lyz
	 * @param scale 天数
	 * @param time 是否返回时间
	 * @return
	 */
	public static String someDay(int scale, boolean time) {
		int factor = 60 * 60 * 24 * 1000;
		Date d = new Date(new Date().getTime() + scale * factor);
		String pattern = "yyyy-MM-dd";
		if (time) {
			pattern += " HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(d);
	}
	
	/**
	 * @see 返回与当前日期相差scale分钟的时间
	 * @author lyz
	 * @param scale 分钟数
	 * @return
	 */
	public static String someTime(int scale) {
		int factor = 60 * 1000;
		Date d = new Date(new Date().getTime() + scale * factor);
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(d);
	}
	
	/**
	 * @see 输出指定格式日期，可输入字符串或Date类型
	 * @author lyz
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static String toDateTimeString(Object source, String pattern) {
		String output = "";
		SimpleDateFormat sdf = new SimpleDateFormat();
		try {
			if (source instanceof String) {
				output = (String)source;
				//
				String str = output.replace("-", "").replace("" , "");
				if (str.length() == 14) {
					sdf.applyPattern("yyyyMMddHHmmss");
				} else if (str.length() == 8) {
					sdf.applyPattern("yyyyMMdd");
				} else {
					return output;
				}
				Date d = sdf.parse(str);
				sdf.applyPattern(pattern);
				output = sdf.format(d);
			} else if (source instanceof Date) {
				sdf.applyPattern(pattern);
				output = sdf.format((Date)source);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return output;
	}
	
	/**
	 * @see
	 * @author lyz
	 * @param time
	 * @return
	 */
	public static String today(boolean time) {
		String pattern = "yyyy-MM-dd";
		if (time) {
			pattern += " HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}

	/**
	 * @see 生成yyyymmddhh24miss形式的当前日期和时间
	 * @return
	 * @author lyz 凌云志
	 */
	public static String now14Bit() {
		try {
			Date d = new Date();
			String str = d.toLocaleString();
			String front = str.split(" ")[0];
			String[] fArr = front.split("-");
			if (fArr[1].length() < 2) {
				fArr[1] = "0" + fArr[1];
			}
			if (fArr[2].length() < 2) {
				fArr[2] = "0" + fArr[2];
			}
			String back = d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds();
			String[] bArr = back.split(":");
			if (bArr[0].length() < 2) {
				bArr[0] = "0" + bArr[0];
			}
			if (bArr[1].length() < 2) {
				bArr[1] = "0" + bArr[1];
			}
			if (bArr[2].length() < 2) {
				bArr[2] = "0" + bArr[2];
			}
			front = fArr[0] + fArr[1] + fArr[2];
			back = bArr[0] + bArr[1] + bArr[2];
			return front + back;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * @see 生成mmddhh24miss形式的当前日期和时间
	 * @return
	 * @author lyz 凌云志
	 */
	public static String now10Bit() {
		try {
			Date d = new Date();
			String str = d.toLocaleString();
			String front = str.split(" ")[0];
			String[] fArr = front.split("-");
			if (fArr[1].length() < 2) {
				fArr[1] = "0" + fArr[1];
			}
			if (fArr[2].length() < 2) {
				fArr[2] = "0" + fArr[2];
			}
			String back = d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds();
			String[] bArr = back.split(":");
			if (bArr[0].length() < 2) {
				bArr[0] = "0" + bArr[0];
			}
			if (bArr[1].length() < 2) {
				bArr[1] = "0" + bArr[1];
			}
			if (bArr[2].length() < 2) {
				bArr[2] = "0" + bArr[2];
			}
			front = fArr[1] + fArr[2];
			back = bArr[0] + bArr[1] + bArr[2];
			return front + back;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * @see 生成yyyy-mm-dd hh24:mi:ss形式的标准当前日期和时间
	 * @see 可直接保存到DateTime和Timestamp数据库字段
	 * @author lyz
	 * @return
	 */
	public static String nowDateTime() {
		try {
			Date d = new Date();
			String str = d.toLocaleString();
			String front = str.split(" ")[0];
			String[] fArr = front.split("-");
			if (fArr[1].length() < 2) {
				fArr[1] = "0" + fArr[1];
			}
			if (fArr[2].length() < 2) {
				fArr[2] = "0" + fArr[2];
			}
			String back = d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds();
			String[] bArr = back.split(":");
			if (bArr[0].length() < 2) {
				bArr[0] = "0" + bArr[0];
			}
			if (bArr[1].length() < 2) {
				bArr[1] = "0" + bArr[1];
			}
			if (bArr[2].length() < 2) {
				bArr[2] = "0" + bArr[2];
			}
			front = fArr[0] + "-" + fArr[1] + "-" + fArr[2];
			back = bArr[0] + ":" + bArr[1] + ":" + bArr[2];
			return front + " " + back;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * @see 生成yyyymmddhh24miss形式的当前日期和时间
	 * @return
	 * @author lyz 凌云志
	 */
	public static String nowDate14Bit() {
		try {
			Date d = new Date();
			String str = d.toLocaleString();
			String front = str.split(" ")[0];
			String[] fArr = front.split("-");
			if (fArr[1].length() < 2) {
				fArr[1] = "0" + fArr[1];
			}
			if (fArr[2].length() < 2) {
				fArr[2] = "0" + fArr[2];
			}
			String back = d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds();
			String[] bArr = back.split(":");
			if (bArr[0].length() < 2) {
				bArr[0] = "0" + bArr[0];
			}
			if (bArr[1].length() < 2) {
				bArr[1] = "0" + bArr[1];
			}
			if (bArr[2].length() < 2) {
				bArr[2] = "0" + bArr[2];
			}
			front = fArr[0] + fArr[1] + fArr[2];
			back = bArr[0] + bArr[1] + bArr[2];
			return front + back;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * @see 生成yyyymmdd形式的当前日期
	 * @return
	 * @author lyz 凌云志
	 */
	public static String nowDate8Bit() {
		try {
			Date d = new Date();
			String str = d.toLocaleString();
			String front = str.split(" ")[0];
			String[] fArr = front.split("-");
			if (fArr[1].length() < 2) {
				fArr[1] = "0" + fArr[1];
			}
			if (fArr[2].length() < 2) {
				fArr[2] = "0" + fArr[2];
			}
			front = fArr[0] + fArr[1] + fArr[2];
			return front;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * @see 生成yymmdd形式的当前日期
	 * @return
	 * @author lyz 凌云志
	 */
	public static String nowDate6Bit() {
		try {
			Date d = new Date();
			String str = d.toLocaleString();
			String front = str.split(" ")[0];
			String[] fArr = front.split("-");
			if (fArr[1].length() < 2) {
				fArr[1] = "0" + fArr[1];
			}
			if (fArr[2].length() < 2) {
				fArr[2] = "0" + fArr[2];
			}
			front = fArr[0].substring(2, 4) + fArr[1] + fArr[2];
			return front;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * @see 生成HHmmss的时间
	 * @author lyz
	 * @return
	 */
	public static String nowTime6Bit() {
		try {
			Date d = Calendar.getInstance().getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
			return sdf.format(d);
		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
	}
	
	/**
	 * @see 将数据库读出的日期字符串小数点后去掉，如"2000-01-01 01:01:01.0" -> "2000-01-01 01:01:01"
	 * @author lyz
	 * @param dateStr
	 * @return
	 */
	public static String trimLast(String dateStr) {
		if (dateStr == null) {
			return "";
		}
		int pos = dateStr.lastIndexOf(".");
		if (pos < 0) {
			return dateStr;
		}
		return dateStr.substring(0, pos);
	}
	
	/*
	 * @see    yyyymmddhh24miss 字符串 转为 YYYY年MM月DD日HH时
	 * @author wdh
	 * @param dateStr
	 * @return
	 */
	public static final String strToTime(String s) {
		if(Strings.isEmpty(s)){
			return "";
		}else{
			return s.substring(0, 4)+"年"+s.substring(4,6)+"月"+s.substring(6,8)+"日"+s.substring(8,10)+"时"; 
		}		 

	}
	
	/*
	 * @see  Date 转为 yyyymmddhh24miss 字符串
	 * @author wdh
	 * @param dateStr
	 * @return
	 */
	public static final String dateToString(Date d) {
		String value = dateFormat(d);
		if (null == value || "".equals(value)) {
			return null;
		}
		String front = value.split(" ")[0];
		String h = value.split(" ")[1];
		String[] fArr = front.split("-");
		if (fArr[1].length() < 2) {
			fArr[1] = "0" + fArr[1];
		}
		if (fArr[2].length() < 2) {
			fArr[2] = "0" + fArr[2];
		}
		front = fArr[0] + fArr[1] + fArr[2];

		Pattern p1=Pattern.compile("-");

		Matcher m1=p1.matcher(front);

		String s1=m1.replaceAll(""); 
		
		Pattern p2=Pattern.compile(":");

		Matcher m2=p2.matcher(h);

		String s2=m2.replaceAll("");
		
		return s1 + s2;  

	}
	
	/*
	 * @see  Date 转为 yyyy-MM-dd HH:mm:ss 形式的标准时间 
	 * @author wdh
	 * @param dateStr
	 * @return
	 */
	public static final String dateFormat(Date d) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		return sdf.format(d);
		 
	}
	
	
	/*
	 * @see 获取开始时间,如"2002-1-1"转为"20020101000000"
	 * @author wdh
	 * @param dateStr
	 * @return
	 */
	public static final String getStartTime(String value) {
		if (null == value || "".equals(value)) {
			return null;
		}
		String front = value.split(" ")[0];
		String[] fArr = front.split("-");
		if (fArr[1].length() < 2) {
			fArr[1] = "0" + fArr[1];
		}
		if (fArr[2].length() < 2) {
			fArr[2] = "0" + fArr[2];
		}
		front = fArr[0] + fArr[1] + fArr[2];
		String regEx="-"; 

		Pattern p=Pattern.compile(regEx);

		Matcher m=p.matcher(front);

		String s=m.replaceAll("");     
		return s+"000000";  

	}

	/*
	 * @see 获取结束时间,如"2002-1-1"转为"20020101235959"
	 * @author wdh
	 * @param dateStr
	 * @return
	 */
	public static final String getEndTime(String value) {
		if (null == value || "".equals(value)) {
			return null;
		}
		String front = value.split(" ")[0];
		String[] fArr = front.split("-");
		if (fArr[1].length() < 2) {
			fArr[1] = "0" + fArr[1];
		}
		if (fArr[2].length() < 2) {
			fArr[2] = "0" + fArr[2];
		}
		front = fArr[0] + fArr[1] + fArr[2];
		String regEx="-"; 

		Pattern p=Pattern.compile(regEx);

		Matcher m=p.matcher(front);

		String s=m.replaceAll("");
		return s+"235959";

	}

	public static String dateFormat8Bit(Date d) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		return sdf.format(d);
	}
	
	public static String timeFormat8Bit(Date d) {
		SimpleDateFormat sdf=new SimpleDateFormat("hhmmss");
		return sdf.format(d);
	}
	
	public static String date8BitForNhwy() {
		try {
			Date d = new Date();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
			return sdf.format(d);
		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
	}
	
	/**
	 * @see 生成HH:mm:ss的时间
	 * @author lyz
	 * @return
	 */
	public static String nowTime6BitForNhwy() {
		try {
			Date d = Calendar.getInstance().getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			return sdf.format(d);
		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
	}
	
	public static String now() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
	
	public static String yesterday() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()-2*24*60*60*1000));
	}
}
