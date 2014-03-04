package common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @title JsonUtil.java
 * @description json工具类
 * @author lichunan
 * @date 2014-02-21 15:51
 * @version 1.0
 */
public class JsonUtil {
	
	/**
	 * 将json转换成Map对象
	 * @param jsonString 
	 * @return
	 */
	@SuppressWarnings("unused")
	public static Map<String, Object> getMap(String jsonString){
		JSONObject jsonObject;
		Map<String, Object> valueMap = new HashMap<String, Object>();
		try {
			jsonObject = new JSONObject(jsonString);
			@SuppressWarnings("unchecked")
			Iterator<String> keyIter = jsonObject.keys();
			while(keyIter.hasNext()){
				String key = (String)keyIter.next();
				Object value = jsonObject.get(key);
				valueMap.put(key, value);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return valueMap;
	}
	
	/**
	 * 把jsonArray转换为ArrayList形式
	 * @param jsonArray
	 * @return
	 */
	public static List<Map<String, Object>> getList(JSONArray jsonArray){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			for(int i=0; i<jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				list.add(getMap(jsonObject.toString()));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
}
