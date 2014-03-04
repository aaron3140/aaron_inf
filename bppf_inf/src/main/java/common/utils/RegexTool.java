package common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexTool {		public static final String 	DATE_FORMAT = "^\\d{4}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])(([0|1|]\\d)|2[0-3])([0-5]\\d)([0-5]\\d)$";
	
	///
	public static List<String> find(String reg, String str) {
	    Pattern p = Pattern.compile(reg);
	   Matcher m = p.matcher(str);
	   List<String> matchList = new ArrayList<String>();
	   
	   while(m.find())
       {
		   matchList.add(m.group()); 
       }
	   
	   return matchList;
	   
	}		public static boolean isMatch(String reg, String str) {		return Pattern.matches(reg, str);	}
	
	public static void main(String[] args) {		//System.out.println(isMatch("^\\w+$", "中文"));		System.out.println(isMatch("^[\\w\\u4e00-\\u9fa5]+$", "_中6文"));		System.out.println(isMatch("^[1-9]\\d*$", "1"));
	}

}
