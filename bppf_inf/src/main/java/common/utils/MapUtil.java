package common.utils;

import java.math.BigDecimal;
import java.util.Map;

public class MapUtil {
	public static String s(Map map,String key) {
		if (map.get(key)==null) {
			return "";
		}
		return map.get(key).toString();
	}
	
	public static Integer i(Map map,String key) {
		if (map.get(key)==null) {
			return 0;
		}
		Object o=map.get(key);
		Integer val=0;
		if (o instanceof String) {
			try {
				val=Integer.valueOf(o.toString());
			} catch (Exception e) {
			}
			if (val==null) {
				try {
					val=Float.valueOf(o.toString()).intValue();
				} catch (Exception er) {
				}
				if (val==null) {
					val=0;
				}
			}
			return val;
		}
		if (o instanceof BigDecimal) {
			val=((BigDecimal)o).intValue();
			return val;
		}
		if (o instanceof Number) {
			val=((Number)o).intValue();
			return val;
		}
		
		return 0;
	}
	
	public static Float f(Map map,String key) {
		if (map.get(key)==null) {
			return 0f;
		}
		Object o=map.get(key);
		Float val=0f;
		if (o instanceof String) {
			try {
				val=Float.valueOf(o.toString());
			} catch (Exception e) {
			}
			if (val==null) {
				val=0f;
			}
			return val;
		}
		if (o instanceof BigDecimal) {
			val=((BigDecimal)o).floatValue();
			return val;
		}
		if (o instanceof Number) {
			val=((Number)o).floatValue();
			return val;
		}
		
		return 0f;
	}
	
	public static Double d(Map map,String key) {
		if (map.get(key)==null) {
			return 0d;
		}
		Object o=map.get(key);
		Double val=0d;
		if (o instanceof String) {
			try {
				val=Double.valueOf(o.toString());
			} catch (Exception e) {
			}
			if (val==null) {
				val=0d;
			}
			return val;
		}
		if (o instanceof BigDecimal) {
			val=((BigDecimal)o).doubleValue();
			return val;
		}
		if (o instanceof Number) {
			val=((Number)o).doubleValue();
			return val;
		}
		
		return 0d;
	}
	
	public static Long l(Map map,String key) {
		if (map.get(key)==null) {
			return 0l;
		}
		Object o=map.get(key);
		Long val=0l;
		if (o instanceof String) {
			try {
				val=Long.valueOf(o.toString());
			} catch (Exception e) {
			}
			if (val==null) {
				try {
					val=Double.valueOf(o.toString()).longValue();
				} catch (Exception er) {
				}
				if (val==null) {
					val=0l;
				}
			}
			return val;
		}
		if (o instanceof BigDecimal) {
			val=((BigDecimal)o).longValue();
			return val;
		}
		if (o instanceof Number) {
			val=((Number)o).longValue();
			return val;
		}
		
		return 0l;
	}
	
	public static void main(String args[]) {
		System.out.println(Integer.getInteger("6"));
	}
}

