package common.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectHelper {

	public static Map< String, Object > getClassFields ( Object obj, boolean includeParentClass ) throws IllegalArgumentException, IllegalAccessException{
		Map< String, Object > map = new HashMap< String, Object > ( );
		getMap(obj,map);
		if ( includeParentClass )
			getParentClassFields ( map, obj);
		return map;
	}
	
	private static Map<String, Object> getMap(Object obj,Map< String, Object > map) throws IllegalAccessException {
		
		String filedName = "";

		Field[] fields = obj.getClass().getDeclaredFields ( );
		for ( Field field : fields ){
			field.setAccessible(true);
			 filedName = field.getName();
			if ("sign".equals(filedName)) {
				continue;
			}
			map.put ( filedName, field.get(obj) );
		}
		return map;
	}
	
	private static Map< String, Object > getParentClassFields ( Map< String, Object > map, Object obj ) throws IllegalAccessException{
		Field[] fields = obj.getClass().getSuperclass().getDeclaredFields();
		String filedName = "";
		for ( Field field : fields ){
			field.setAccessible(true);
			 filedName = field.getName();
			if ("sign".equals(filedName)) {
				continue;
			}
			map.put ( filedName, field.get(obj) );
		}
		return map;
	}
}
