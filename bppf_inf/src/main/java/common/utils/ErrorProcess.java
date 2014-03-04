package common.utils;

public class ErrorProcess {
	
	public static String PASSERROR = "2135"; //密码验证失败
	public static boolean isTimeOut(String errorCode){
		
		if("9002".equals(errorCode)||"0030".equals(errorCode)||"0031".equals(errorCode)||"0023".equals(errorCode)||"0040".equals(errorCode)){
			return true;
		}else{
			return false;
		}
	}

}
