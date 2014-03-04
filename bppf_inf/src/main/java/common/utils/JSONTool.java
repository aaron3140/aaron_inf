package common.utils;

import net.sf.json.JSONObject;

public class JSONTool {

	public static String createJson(String[] names, Object[] values) {

		JSONObject jsonObject = new JSONObject();// 实例化JSONObject
		for (int i = 0; i < names.length; i++) {
			if ("ERRORCODE".equalsIgnoreCase(names[i])) {
				//responseCode补充到6位
				String newResCode = Charset.lpad( (String) values[i], 6, "0");
				jsonObject.put(names[i], newResCode);
			} else {
				jsonObject.put(names[i], values[i] == null ? "" : values[i] );
			}
		}

		return jsonObject.toString();
	}

	public static String createErrJsonStr(String errCode, String errMsg) {
		return "{\"errorcode\":\"" + errCode + "\", \"errorreason\":\""
				+ errMsg + "\"}";
	}
	
	public static String createStandardErrJsonStr(String errCode, String errMsg ,String sig) {
		String[] jsonNames = {"ERRORCODE", "ERRORMSG","SIG"};
		return createJson(jsonNames, new Object[] {errCode, errMsg , sig});
	}
	
	public static String createStandardErrJsonStr(String errCode, String errMsg) {
		String[] jsonNames = {"ERRORCODE", "ERRORMSG"};
		return createJson(jsonNames, new Object[] {errCode, errMsg });
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
