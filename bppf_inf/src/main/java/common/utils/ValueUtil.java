package common.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

public class ValueUtil {
	private static DecimalFormat DecimalFormatter=new DecimalFormat("#.##");
	
	public static String s(Object value) {
		if (value==null) {
			return "";
		}
		if (value instanceof Double||value instanceof Float) {
			return DecimalFormatter.format(value);
		}
		return value.toString();
	}
	
	public static Integer i(Object value) {
		if (value==null) {
			return 0;
		}
		Integer val=0;
		if (value instanceof String) {
			try {
				val=Integer.valueOf(value.toString());
			} catch (Exception e) {
			}
			if (val==null) {
				try {
					val=Float.valueOf(value.toString()).intValue();
				} catch (Exception er) {
				}
				if (val==null) {
					val=0;
				}
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val=((BigDecimal)value).intValue();
			return val;
		}
		if (value instanceof Number) {
			val=((Number)value).intValue();
			return val;
		}
		
		return 0;
	}
	
	public static Float f(Object value) {
		if (value==null) {
			return 0f;
		}
		Float val=0f;
		if (value instanceof String) {
			try {
				val=Float.valueOf(value.toString());
			} catch (Exception e) {
			}
			if (val==null) {
				val=0f;
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val=((BigDecimal)value).floatValue();
			return val;
		}
		if (value instanceof Number) {
			val=((Number)value).floatValue();
			return val;
		}
		
		return 0f;
	}
	
	public static Double d(Object value) {
		if (value==null) {
			return 0d;
		}
		Double val=0d;
		if (value instanceof String) {
			try {
				val=Double.valueOf(value.toString());
			} catch (Exception e) {
			}
			if (val==null) {
				val=0d;
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val=((BigDecimal)value).doubleValue();
			return val;
		}
		if (value instanceof Number) {
			val=((Number)value).doubleValue();
			return val;
		}
		
		return 0d;
	}
	
	public static Long l(Object value) {
		if (value==null) {
			return 0l;
		}
		Long val=0l;
		if (value instanceof String) {
			try {
				val=Long.valueOf(value.toString());
			} catch (Exception e) {
			}
			if (val==null) {
				try {
					val=Double.valueOf(value.toString()).longValue();
				} catch (Exception er) {
				}
				if (val==null) {
					val=0l;
				}
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val=((BigDecimal)value).longValue();
			return val;
		}
		if (value instanceof Number) {
			val=((Number)value).longValue();
			return val;
		}
		
		return 0l;
	}
	
	public static Date date(Object value) {
		if (value==null) {
			return null;
		}
		
		if (value instanceof java.sql.Timestamp) {
			return new Date(((java.sql.Timestamp)value).getTime());
		}
		
		if (value instanceof Date) {
			return (Date)value;
		}
		
		return null;
	}
	
	public static void main(String args[]) {
		System.out.println(Integer.getInteger("6"));
	}
}

