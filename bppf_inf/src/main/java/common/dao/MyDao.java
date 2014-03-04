package common.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.LinkedCaseInsensitiveMap;

import common.utils.SpringContextHelper;
import common.utils.ValueUtil;

public class MyDao {
	
	private static BaseDao baseDao = SpringContextHelper.getBaseDaoBean();
	
	public static List list(String sql,Class clazz) {
		List list=baseDao.queryForList(sql);
		return listMapToListEntity(list,clazz);
	}
	
	
	@SuppressWarnings("unchecked")
	public static List listMapToListEntity(List<LinkedCaseInsensitiveMap> listMap,Class clazz) {
		if (listMap==null||listMap.isEmpty()) {
			return null;
		}
		List list=new ArrayList();
		if (listMap!=null) {
			for (LinkedCaseInsensitiveMap map:listMap) {
				list.add(mapToEntity(map,clazz));
			}
		}
		return list;
	}
	
	public static Object mapToEntity(LinkedCaseInsensitiveMap map, Class clazz) {
		Iterator<Map.Entry<String, Object>> i=map.entrySet().iterator();
		Field field=null;
		Object value=null;
		Object obj=null;
		try {
			obj=clazz.newInstance();
			while (i.hasNext()) {
				Map.Entry<String, Object> entry=i.next();
				value=entry.getValue();
				if (value==null) {
					continue;
				}
				if (entry.getKey().equals("ROWNUM_COLUMN_")) {
					continue;
				}
				try{
					field=clazz.getDeclaredField(toJavaFirstLower(entry.getKey()));
				} catch (Exception e) {
					System.out.println("没有此字段："+toJavaFirstLower(entry.getKey()));
					continue;
				}
				if (field==null) {
					continue;
				}
				field.setAccessible(true);
				
				if (field.getType()==String.class) {
					field.set(obj,ValueUtil.s(value));
				} else if (field.getType()==Long.class) {
					field.set(obj,ValueUtil.l(value));
				} else if (field.getType()==Date.class) {
					field.set(obj,ValueUtil.date(value));
				} else if (field.getType()==Double.class) {
					field.set(obj,ValueUtil.d(value));
				} else if (field.getType()==Integer.class) {
					field.set(obj,ValueUtil.i(value));
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return obj;
	}
	
	/**
	 * 如：ORG_ID 转换成： orgId
	 * _ORG_ID_ 转换成： orgId
	 */
	public static String toJavaFirstLower(String str){
		if (str==null||str.length()<1) {
			return "";
		}
		StringBuffer sb=new StringBuffer();
		boolean flag=false;		// true 当前字符为符号
		boolean first=false;	// false 第一个字母未出现
		for (int i=0;i<str.length();i++) {
			char c=str.charAt(i);
			if (isCharNum(c)) {
				if (flag&&first) {
					sb.append(Character.toUpperCase(c));
				} else {
					sb.append(Character.toLowerCase(c));
				};
				flag=false;
				if (!first) {
					first=true;
				}
			} else {
				flag=true;
			}
		}
		return sb.toString();
	}
	
	/**
	 * 是否字母或者数字 
	 */
	public static boolean isCharNum(char c){
		if ((c>='A'&&c<='Z')||(c>='a'&&c<='z')||(c>='0'&&c<='9')){
			return true;
		}
		return false;
	}
	
	public static void main(String args[]) throws Exception {
		System.out.println(Class.forName("java.lang.String")==String.class);
	}
}
