package common.utils;

public class ObjectUtils {
	public static boolean isEmpty(Object o) {
		return isEmpty(o, true);
//		if(items == null)
//			return true;
//		
//		for(Object item : items) {
//			if(!isEmpty(item, true))
//				return false;
//		}
//		
//		return true;
	}
	
	public static boolean isEmpty(Object o, boolean trim) {
		if(o == null)
			return true;
		
		if(o instanceof String) {
			String s = (String) o;
			s = trim ? s.trim() : s;
			return s.length() == 0;
		}
		
		return CollectionUtils.getSize(o) == 0;
	}
	
	public static void main(String[] args) {
		String[] s = new String[0];
		System.out.println(isEmpty(s));
	}
}
