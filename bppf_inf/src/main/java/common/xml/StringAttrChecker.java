package common.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import common.utils.RegexTool;

/**
 * 只检查String类型属性的len和type
 * 
 * @author Administrator
 * 
 */
public class StringAttrChecker {

	public static void checkFields(Object o) throws Exception {

		Field[] fields = o.getClass().getDeclaredFields();

		for (Field field : fields) {
			
			String n = field.getName();
			
			// 当字段有CheckerAnnotion标记修饰时
			if (field.isAnnotationPresent(CheckerAnnotion.class)) {
				Method m = o.getClass().getMethod(
						"get" + n.substring(0, 1).toUpperCase() + n.substring(1));
				Object v =   m.invoke(o);
				
				// 取得标记的内容
				CheckerAnnotion anno = field
						.getAnnotation(CheckerAnnotion.class);
				int len = anno.len();
				String type = anno.type();
				boolean strict = anno.strict();
				boolean required = anno.required();

				if (v == null || "".equals(v)) {
					if (required) {
						throw new Exception(n + "字段不能为空");
					} else {
						continue;
					}
				}

				if (CheckerAnnotion.TYPE_STR.equalsIgnoreCase(type)
						&& len != -1) {
					String vstr =v.toString();
					if (strict) {
						if (vstr.getBytes().length != len)
							throw new Exception(n + "字段长度不是" + len);
					} else {
						if (vstr.getBytes().length > len)
							throw new Exception(n + "字段长度超过" + len);
					}

				} else if (CheckerAnnotion.TYPE_NUM.equalsIgnoreCase(type)) {
					String vstrl = v.toString();
					try {
						// type为CheckerAnnotion.TYPE_NUM要检查该字段是否全部是数字
						new BigDecimal(vstrl);
					} catch (Exception e) {
						throw new Exception(n + "字段内容不是数字");
					}
					
					if(vstrl.startsWith("-")) {
						throw new Exception(n + "字段不能为负数");
					}

					if (strict) {
						if (vstrl.length() != len)
							throw new Exception(n + "字段长度不是" + len);
					} else {
						if (vstrl.length() > len)
							throw new Exception(n + "字段长度超过" + len);
					}

				}
				
				//检查正则表达式
				String regex = anno.regex().trim();
				if (regex != null && !"".equals(regex)) {
					if (!RegexTool.isMatch(regex, v.toString())) {
						throw new Exception (n +  "字段不符合正则表达式" + regex);
					}
				}
			}

		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ " <PayPlatRequestParameter>" + " 	<CTRL-INFO "
					+ " WEBSVRNAME=\"服务名称\" " + " WEBSVRCODE=\"服务编码\" "
					+ " APPFROM=\"请求本服务的应用标识码\"" + " 		KEEP=\"本次服务的标识流水\" />"
					+ " 	<PARAMETERS>" + " 		<PRODUCTNO>1</PRODUCTNO>"
					+ " <BUSINESSTYPE>1</BUSINESSTYPE>"
					+ " <PRODUCTMODEL>3</PRODUCTMODEL>"
					+ " <TXNTYPE>4</TXNTYPE>" + " <AUTHCODE>5</AUTHCODE>"
					+ " <TXNAMOUNT>6</TXNAMOUNT>"
					+ " <PAYORGCODE>7</PAYORGCODE>"
					+ " <SUPPLYORGCODE>8</SUPPLYORGCODE>"
					+ " <ACCEPTSEQNO>9</ACCEPTSEQNO>" 
					+ " <OLDACCEPTSEQNO>91a11</OLDACCEPTSEQNO>" 
				    + " 	</PARAMETERS>"
					+ " </PayPlatRequestParameter> ";

			//PayPlatRequestForAuthPay req = new PayPlatRequestForAuthPay(xmlStr, true);
			// checkFields(req);

			//System.out.println(req.getClass());
			//System.out.println(((Object) req).getClass());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
