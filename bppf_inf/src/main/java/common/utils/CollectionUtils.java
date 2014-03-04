package common.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class CollectionUtils {
	public static final String DEFAULT_SEPERATOR = ",";
	
	public static String join(Object[] items) {
		return join(items, DEFAULT_SEPERATOR);
	}
	
	public static String join(Collection items) {
		return join(items, DEFAULT_SEPERATOR);
	}
	
	public static String join(Object[] items, String seperator) {
		return join(Arrays.asList(items), seperator);
	}
	
	public static String join(Collection items, String seperator) {
		StringBuilder sb = new StringBuilder();
		
		int i = 0;
		for(Object o : items) {
			if(i++ > 0)
				sb.append(seperator);
			sb.append(o);
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] emptyArray(Class<T> componentType) {
		return (T[]) Array.newInstance(componentType, 0);
	}
	
	public static boolean contains(Object items, Object target) {
		if(items == null)
			return false;
		
		if(items.getClass().isArray()) {
			items = Arrays.asList((Object[]) items);
		}
		
		if(items instanceof Collection) {
			return ((Collection) items).contains(target);
		}
		
		if(items instanceof Map) {
			return ((Map) items).containsKey(target);
		}
		
		return false;
	}
	
	public static int getSize(Object o) {
		if(o.getClass().isArray())
			return Array.getLength(o);
		if(o instanceof Map)
			return ((Map)o).size();
		if(o instanceof Collection)
			return ((Collection)o).size();
		
		return -1;
	}
	
	public static Object[] union(Object[] a, Object... b) {
		Object[] result = new Object[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}
	
	public static void reverse(Object[] array) {
		if (array == null) {
			return;
		}

		Object tmp = null;
		for(int i = 0, j = array.length - 1; j > i; i++, j--) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
		}
	}
	
	public static void main(String[] args) {
		String[] emptyStrArr = CollectionUtils.emptyArray(String.class);
		System.out.println(emptyStrArr.length);
		
		Integer[] items = { 1, 2, 3 };
		CollectionUtils.reverse(items);
		System.out.println(Arrays.asList(items));
		
//		Object items = new Object();
//		String s = "a";
//		System.out.println(contains(items, s));
	}
}