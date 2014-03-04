package common.utils;

import java.util.HashMap;

import common.entity.TInfErrorCode;

public class TInfErrorCodeUtil {
	
	
	public static HashMap<String , TInfErrorCode> map=null;
	
	public TInfErrorCode getErrorInfo(String errorCode){
		return map.get(errorCode);
	}
  
}
