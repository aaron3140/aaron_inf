package common.utils;

import java.util.regex.Pattern;

import common.dao.TCumInfoDaoTemp;
import common.dao.TScsOrderDao;

import framework.exception.INFException;
import framework.exception.INFErrorDef;

public class ParamChecker {
	/**
	 * 从接口中获取参数 判断为不为空
	 * 为空则抛出异常
	 * @version 1.00
	 * @history: 2010-12-28 上午11:49:06 [created]
	 * @author Leyi Tang 唐乐毅
	 * @param value
	 * @param name
	 * @see
	 */	
	

	
	public static void checkString(String value, String name) throws INFException
	{
		if (Charset.isEmpty(value)) {
			throw new INFException(INFErrorDef.INF_ParaNotNull_ERRCODE,name + "参数不能为空");
		}
	}
	
	/**
	 * 判断浮点数
	 * 为浮点数则返回真
	 * @version 1.00
	 * @history: 2011-04-18 上午11:49:06 [created]
	 * @author Zhenyang Liang 梁镇阳
	 * @param str
	 * @see
	 */	
	public static boolean isFloat(String str) {     
	    Pattern pattern = Pattern.compile("^\\d*(\\.)?\\d*$");     
	    return pattern.matcher(str).matches();     
	  }
	/**
	 * 从接口中获取参数 判断不为 非数字字段(该数字字段可以有小数点)
	 * 为非数字字段则抛出异常
	 * @version 1.00
	 * @history: 2011-04-18 上午11:49:06 [created]
	 * @author Zhenyang Liang 梁镇阳
	 * @param numeric
	 * @param name
	 * @see
	 */	
	public static void checkNumeric_hasDot(String numeric, String name)throws INFException{
			
				if (!isFloat(numeric)) {
					throw new INFException(INFErrorDef.INF_ParaNumOnly_ERRCODE,name + "参数不能为非数字字段");
				}		
			}
	/**
	 * 从接口中获取参数 判断不为 非数字字段
	 * 为非数字字段则抛出异常
	 * @version 1.00
	 * @history: 2011-04-15 上午11:49:06 [created]
	 * @author Zhenyang Liang 梁镇阳
	 * @param numeric
	 * @param name
	 * @see
	 */			
		
	public static void checkNumeric(String numeric, String name)throws INFException{
		
		for (int i = 0; i < numeric.trim().length(); ++i){
			if (!Character.isDigit(numeric.trim().charAt(i))) {
				throw new INFException(INFErrorDef.INF_ParaNumOnly_ERRCODE,name + "参数不能为非数字字段");
			}		
		}
	}
	
	
	/**
	 * 从接口中获取校验码 比较newSign 是否和传入的 sign 相等
	 * 不相等则抛异常
	 * @version 1.00
	 * @history: 2011-01-26 上午10:23:20 [created]
	 * @author Leyi Tang 唐乐毅
	 * @param sign
	 * @param newSign
	 * @see
	 */	
	public static void checkSign(String sign,String newSign) throws Exception
	{
		checkString(sign, "sign");
		if (!sign.trim().equals(newSign)) {
			throw new Exception("校验码错误");
		}
	}
	
	/**
	 * 检查版本号 version(版本号)
	 * 从接口中获取 version(版本号)
	 * 格式不正确 则抛出异常
	 * @version 1.00
	 * @history: 2011-07-01 上午11:03:20 [created]
	 * @author Leyi Tang 唐乐毅
	 * @param version 版本号
	 * @throws INFException
	 */
	public static void checkVersion(String version,String name) throws INFException{
		String[] versions = version.trim().replace(".", ",").split(",");
		boolean isFlag = true;
		int versionLength = versions.length;
		String newVersion = version.trim().replace(".", "");
		if (versionLength >= 1) {
			for (int i = 0; i < newVersion.length(); i++) {
				if (!Character.isDigit(newVersion.charAt(i))) {
					isFlag = false;
					break;
				}
			}
		}else {
			isFlag = false;
		}
	    if (!isFlag) {
	    	throw new INFException(INFErrorDef.INF_ParaNumOnly_ERRCODE,name + "格式不正确(格式应为:x.x.x)");
		}
	}

}
