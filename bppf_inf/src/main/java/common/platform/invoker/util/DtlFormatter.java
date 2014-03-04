/**
 * File                 : DtlFormatter.java [交费易移植]
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : apms
 * JDK version used     : JDK 1.5
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2009-6-13 上午11:05:31 [created]
 * Author               : Yunzhi Ling 凌云志
 * Email                : lingyunzhi@tisson.cn
 **/

package common.platform.invoker.util;

import java.util.List;

import org.apache.log4j.Logger;

public class DtlFormatter {
	
	private static final Logger log = Logger.getLogger(DtlFormatter.class);

	/**
	 * @see 按格式返回明细项值，用于循环输入492参数组
	 * @author lyz
	 * @param dataType 格式编码，492-4922
	 * @param oriValue 原始值，492-4923
	 * @return
	 */
	public static String getDetailValue(String dataType, String oriValue) {
		String newValue = oriValue;
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			if ("01".equals(dataType)) {
				newValue = oriValue;
			} else if ("02".equals(dataType)) {
				newValue = maskName(oriValue);
			} else if ("11".equals(dataType)) {
				newValue = "￥" + oriValue + "元";
			} else if ("12".equals(dataType)) {
				newValue = "￥" + oriValue + "元";
			} else if ("21".equals(dataType)) {
//				sdf.applyPattern("yyyyMMdd");
//				Date d = sdf.parse(oriValue);
//				sdf.applyPattern("yyyy年MM月dd日");
//				newValue = sdf.format(d);
				newValue = formateDate(oriValue, "yyyy年MM月dd日");
			} else if ("22".equals(dataType)) {
//				sdf.applyPattern("yyyyMM");
//				Date d = sdf.parse(oriValue);
//				sdf.applyPattern("yyyy年MM月");				
//				newValue = sdf.format(d);
				newValue = formateDate(oriValue, "yyyy年MM月");
			} else if ("23".equals(dataType)) {
//				sdf.applyPattern("yyyy");
//				Date d = sdf.parse(oriValue);
//				sdf.applyPattern("yyyy年");				
//				newValue = sdf.format(d);
				newValue = formateDate(oriValue, "yyyy年");
			} else if ("24".equals(dataType)) {
				if (oriValue.length() != 16) {
					newValue = oriValue;
				} else {
					String str1 = oriValue.substring(0, 8);
					String str2 = oriValue.substring(8);
//					sdf.applyPattern("yyyyMMdd");
//					Date d1 = sdf.parse(str1);
//					Date d2 = sdf.parse(str2);
//					sdf.applyPattern("yyyy年MM月dd日");
//					newValue = sdf.format(d1) + " - " + sdf.format(d2);
					newValue = formateDate(str1, "yyyy年MM月dd日") + " - " + formateDate(str2, "yyyy年MM月dd日");
				}
			} else {
				newValue = oriValue;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error(e);
			newValue = oriValue;
		}
		return newValue;
	}
	
	/**
	 * @see
	 * @author lyz
	 * @param input
	 * @return
	 */
	private static String maskName(String input) {
		if (input == null) {
			return "";
		}
		try {
			int len = input.length();
			if (len < 2) {
				return input;
			} else if (len < 4) {
				char[] arr = input.toCharArray();
				arr[1] = '*';
				return new String(arr);
			} else {
				char[] arr = input.toCharArray();
				for (int i=0; i<arr.length; i++) {
					if ((i+1)%2 == 0) {
						arr[i] = '*';
					}
				}
				return new String(arr);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error(e);
			return input;
		}
	}
	
	/**
	 * @see 修正日期
	 * @author lyz
	 * @param oriValue
	 * @param pattern
	 * @return
	 */
	private static String formateDate(String oriValue, String pattern) {
		String newValue = oriValue;
		try {
			String yyyy = "";
			String MM = "";
			String dd = "";
			String HH = "";
			String mm = "";
			String ss = "";
			try {
				yyyy = oriValue.substring(0, 4);
			} catch (Exception e) {
			}
			try {
				MM = oriValue.substring(4, 6);
			} catch (Exception e) {
			}
			try {
				dd = oriValue.substring(6, 8);
			} catch (Exception e) {
			}
			try {
				HH = oriValue.substring(8, 10);
			} catch (Exception e) {
			}
			try {
				mm = oriValue.substring(10, 12);
			} catch (Exception e) {
			}
			try {
				ss = oriValue.substring(12, 14);
			} catch (Exception e) {
			}
			newValue = pattern.replace("yyyy", yyyy)
							  .replace("MM", MM)
							  .replace("dd", dd)
							  .replace("HH", HH)
							  .replace("mm", mm)
							  .replace("ss", ss);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error(e);
		}
		return newValue; 
	}
	
	/**
	 * @see
	 * @author lyz
	 * @param idList
	 * @param valueList
	 * @param dtlId
	 * @return
	 */
	public static String getDetailById(List idList, List valueList, String dtlId) {
		String value = "";
		try {
			for (int i=0; i<idList.size(); i++) {
				if (dtlId.equals(idList.get(i))) {
					return (String)valueList.get(i);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error(e);
			return "";
		}
		return value;
	}
	
}

