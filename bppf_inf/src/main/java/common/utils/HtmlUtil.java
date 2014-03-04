package common.utils;

import org.apache.commons.lang.StringUtils;

public class HtmlUtil {

	/**
	 * @param body 需要处理的字符串，包含了html标签，和单引号或双引号的处理
	 * @author aaronMing
	 **/
	public static String convertToValidHtmlForJson(String body) {
		if (StringUtils.isNotEmpty(body)) {
			return jsonConvertHtml(body); 
		} else
			return null;

	}

	public static String delHtml(String body) {
		if (StringUtils.isNotEmpty(body))
			return body.replaceAll("\\<[^\\>]*\\>", "");
		else
			return null;
	}
 
	private static String jsonConvertHtml(String body) {
		if (StringUtils.isNotEmpty(body))
		{
			return body.replace("\"", "&quot;").replace("'", "&apos;");
		}
		else
			return null;
	}

	public static void main(String args[]) {
		
		String jsonStr = convertToValidHtmlForJson("<br / > 43243<p>few</p>43422 &lt;script>alert(\"fuck\")&lt;/script>few<br>few</br>");
		System.out.println(jsonStr);
	}
}
