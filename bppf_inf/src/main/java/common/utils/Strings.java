package common.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Random;

import sun.misc.BASE64Decoder;

public class Strings {

	public static final String EMPTY_STRING = "";

	/**
	 * 判断字符串是否为空
	 * 
	 * @param o
	 * @return boolean
	 */

	public static boolean isEmpty(String str) {
		if (null == str || "".equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	public static final String replaceToUnicode(String str) {
		if (isEmpty(str))
			return EMPTY_STRING;
		str = str.replaceAll("\'", "`");
		str = str.replaceAll("\"", "`");
		str = str.replaceAll(",", "，");
		return str;
	}

	/**
	 * 判断字符串是否为空,为空则返回字符串 0 ,否则原样返回
	 * 
	 * @param o
	 * @return String
	 */

	public static String NullStringToZero(String str) {
		if (null == str || "".equals(str)) {
			return "0";
		} else {
			return str;
		}
	}

	/**
	 * 判断bigDecimal类型的值是否为空是否为空
	 * 
	 * @param o
	 * @return boolean
	 */

	public static boolean isEmpty(java.math.BigDecimal str) {
		if (null == str || "".equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 中文转换
	 * 
	 * @param tmpStr
	 *            string
	 * @return string
	 */
	public static String toGB2312(String strvalue) {

		try {
			if (strvalue == null)
				return "";
			else {
				strvalue = new String(strvalue.getBytes("8859_1"), "gb2312");
				// return strvalue;
			}
			return strvalue;
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * 中文转换
	 * 
	 * @param tmpStr
	 *            string
	 * @return string
	 */
	public static String GBKtoISO8859(String strvalue) {

		try {
			if (strvalue == null)
				return "";
			else {
				strvalue = new String(strvalue.getBytes("GBK"), "8859_1");
				// return strvalue;
			}
			return strvalue;
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * 中文转换
	 * 
	 * @param tmpStr
	 *            string
	 * @return string
	 */
	public static String toUTF_8(String strvalue) {

		try {
			if (strvalue == null)
				return "";
			else {
				strvalue = new String(strvalue.getBytes("8859_1"), "UTF-8");
				// return strvalue;
			}
			return strvalue;
		} catch (Exception e) {
			return "";
		}

	}

	public static String toChinese(String strvalue) {

		return strvalue;

	}

	public static String _toUTF8(String sourcestr) {
		try {
			String s = new String(sourcestr.getBytes("GBK"));
			sourcestr = new String(s.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}

		return sourcestr;
	}

	/**
	 * 例如提交下面的表单，http://localhost:8080/Logon/UserEditAction.do? expression=普通用户
	 * 在Action中读取参数expression时，用下面的句子： String
	 * expression=request.getParameter("expression");
	 * 得到的expression是Unicode码，不是我们想要的，先变换成8859_1，变换函数如下：
	 * 
	 * @param strvalue
	 *            String
	 * @return String
	 */
	public static String toGBK(String strvalue) {
		// 处理中文问题,实现编码转换

		if (strvalue != null) {

			try {

				String temp_p = strvalue;

				byte[] temp_t = temp_p.getBytes("8859_1");

				strvalue = new String(temp_t);

			}

			catch (Exception e) {

				System.err.println("toGBK exception:" + e.getMessage());

				System.err.println("The String is:" + strvalue);

			}

		}

		return strvalue;

	}

	public static final String GBK(String value) {
		if (value == null) {
			return value;
		}
		try {
			return new String(value.getBytes(), "GBK");
		} catch (Exception e) {

		}
		return value;
	}

	/**
	 * 分割字符串返回数组
	 * 
	 * @param o
	 * @return
	 */
	public static String[] splitString(String srcString, String separator) {
		if (isEmpty(srcString))
			return null;
		String[] dstString = srcString.split(separator);
		return dstString;
	}

	/**
	 * 产生一个随机数
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomInt(int maxValue) {
		java.util.Random random = new java.util.Random();
		String strValue = String.valueOf(maxValue);
		String ret = "00000000000" + random.nextInt(maxValue);
		return ret.substring(ret.length() - strValue.length() + 1);
	}

	/**
	 * 从string中取得double
	 * 
	 * @param str
	 *            参数
	 * @return double
	 */
	public static double getDouble(String str) {
		double dbNumber = 0;
		try {
			dbNumber = Double.parseDouble(str.trim());
		} catch (Exception e) {
		}
		return dbNumber;
	}

	/**
	 * 从String中取得Long
	 * 
	 * @param str
	 *            String参数
	 * @return Long
	 */
	public static long getLong(String str) {
		long lnNumber = 0;
		try {
			lnNumber = Long.parseLong(str);
		} catch (Exception e) {
		}
		return lnNumber;
	}

	/**
	 * @param str
	 * @param num
	 *            备注:把str字符串,拷贝num次并相加
	 */
	public static String copyStr(String str, int num) throws Exception {
		StringBuffer strBuf = new StringBuffer();
		try {
			for (int i = 0; i < num; i++) {
				strBuf.append(str);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return strBuf.toString();
	}

	/**
	 * 得到一定小数位的字符串
	 * 
	 * @param pNumber
	 *            数字
	 * @param pDecimalLen
	 *            小数位数
	 * @return
	 */
	public static String getRightNumber(String pNumber, int pDecimalLen) {
		String strNumber = "";

		try {
			if (pNumber == null || pNumber.trim().equals(""))
				return strNumber;
			// pNumber="0";
			strNumber = pNumber;
			if (pDecimalLen > 0 && !pNumber.equals("")) {
				String strFormat = ".";
				for (int I = 0; I < pDecimalLen; I++)
					strFormat = strFormat + "0";
				DecimalFormat df = new DecimalFormat(strFormat);
				strNumber = String.valueOf(df.format(Double
						.parseDouble(pNumber)));
				// 如果第一个是小数点,前面加0
				if (strNumber.substring(0, 1).equals("."))
					strNumber = "0" + strNumber;
			}
		} catch (Exception Ex) {
			System.out.println("Format:getRightNumber() " + Ex.toString());
		} finally {

		}
		return strNumber;
	}

	public static final String trim(String str) {
		if (str == null) {
			return "";
		}
		return str.trim();
	}

	public static final String getValue(String value, Object _default) {
		if (null == value) {
			if (null == _default) {
				return "";
			}
			return _default.toString();
		}
		return value;
	}

	public static final String getString(BigDecimal decimal) {
		if (null == decimal) {
			return "";
		}
		return decimal.toString();
	}

	public static final BigDecimal getBigDecimal(String value, Object _default) {
		if (null == value || "".equals(value)) {
			if (null == _default) {
				return null;
			}
			return (BigDecimal) _default;
		}
		return new BigDecimal(value);
	}

	public static final Timestamp getStartTimestamp(String value,
			Object _default) {
		if (null == value || "".equals(value)) {
			if (null == _default) {
				return null;
			}
			return (Timestamp) _default;
		}
		if (value.length() <= 10) {
			return Timestamp.valueOf(value + " 00:00:00.0");
		} else {
			return Timestamp.valueOf(value);
		}

	}

	public static final Timestamp getEndTimestamp(String value, Object _default) {
		if (null == value || "".equals(value)) {
			if (null == _default) {
				return null;
			}
			return (Timestamp) _default;
		}
		if (value.length() <= 10) {
			return Timestamp.valueOf(value + " 23:59:59.0");
		} else {
			return Timestamp.valueOf(value);
		}

	}

	public static final int getInteger(String iValue, int _default) {
		int result = _default;
		if (null == iValue || "".equals(iValue)) {
			return result;
		}
		try {
			result = Integer.parseInt(iValue);
		} catch (Exception e) {
			// Nothing
		}
		return result;
	}

	/*
	 * 将 s 进行 BASE64 编码
	 */
	public static final String getBASE64(String s) {

		if (s == null) {
			return null;
		}
		String str = (new sun.misc.BASE64Encoder()).encode(s.getBytes());

		return str;

	}

	/*
	 * 将 BASE64 编码的字符串 s 进行解码
	 */
	public static final String getFromBASE64(String s) {

		if (s == null) {
			return null;
		}

		BASE64Decoder decoder = new BASE64Decoder();

		try {

			byte[] b = decoder.decodeBuffer(s);

			return new String(b);

		} catch (Exception e) {

			return null;

		}

	}

	/*
	 * 随机字符串
	 */
	public static String getRandomString(int size) {
		char[] c = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'q',
				'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd',
				'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm' };
		Random random = new Random(); // 初始化随机数产生器
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size; i++) {
			sb.append(c[Math.abs(random.nextInt()) % c.length]);
		}
		return sb.toString();
	}
	

	public static void main(String[] args) {
		System.out.println(Strings.isEmpty(""));
		String a2 = "kk&22&33&44&55";
		String arr[];
		arr = Strings.splitString(a2, "&");
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}

	}

}
