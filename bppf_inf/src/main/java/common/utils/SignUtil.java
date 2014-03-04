package common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import common.utils.exception.ServiceException;

public class SignUtil {

	public static String getSign(final Object obj,String signKey) throws ServiceException {
		try {
			StringBuffer buffer = new StringBuffer();
			Map< String, Object > map = ObjectHelper.getClassFields(obj, true);
			List<Map.Entry<String, Object>> list = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
			//排序
			Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {
				public int compare(final Map.Entry<String, Object> o1, final Map.Entry<String, Object> o2) {
					return (o1.getKey()).toString().compareTo(o2.getKey());
				} });
			for (Map.Entry<String, Object> maEntry :list) {
				Object val = maEntry.getValue();

				if (val == null) {
					continue;
				}

				buffer.append(maEntry.getKey()).append("=").append(val).append("&");
			}
			String sign = buffer.append("key=").append(signKey).toString();
			 sign = MD5.getMD5(sign.getBytes("utf-8")).toUpperCase();
			return sign;
		} catch (Exception e) {
			throw new ServiceException("SIGN_FAIL");//TODO
		}
	}
	
	

}
