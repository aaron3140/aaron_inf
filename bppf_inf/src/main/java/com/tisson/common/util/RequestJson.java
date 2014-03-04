package com.tisson.common.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import common.utils.SagUtils;


/**
 * @author 邱亚建 2013-4-23 上午11:16:39<br>
 *         TODO 请求的json数据
 * 
 */
public class RequestJson implements Serializable {

	private static final long serialVersionUID = 8657830272931545053L;

	private String in0;
	private Map<String, String> verifyParameterMap = new HashMap<String, String>();
	private Map<String, String> attributesMap = new HashMap<String, String>();
	private String parametersXml = "";
    
	/**
	 * 要转换为xml的json
	 */

	private String in0Json;
	private String in1Json;
	private JSONObject object0;
	private JSONObject object1;
	private JSONObject payObject = new JSONObject();;
	/**
	 * @param in0Json in0参数，json格式
	 * @param in1Json in1参数，json格式
	 */
	public RequestJson(String in0Json,String in1Json) {
		this.in0Json = in0Json;
		this.in1Json = in1Json;
		paserJsonByStr();
	}
	public RequestJson(JSONObject object0,JSONObject object1) {
		this.object0 = object0;
		this.object1 = object1;
		paserJsonByObject();
	}
	/**
	 * 将提交的json转为xml格式
	 * 
	 * @return
	 */
	public String json2Xml() {
		String keep = attributesMap.get("KEEP");
		String websvrname = attributesMap.get("WEBSVRNAME");
		String websvrcode = attributesMap.get("WEBSVRCODE");
		if (StringUtils.isEmpty(keep)) {
			String tmnNum = verifyParameterMap.get("TMNNUM");
			keep = tmnNum + SagUtils.getSeqNbr("yyyyMMddhhmmss", 4);
		}

		String xml = "<PayPlatRequestParameter><CTRL-INFO" + " WEBSVRNAME=\""+websvrname+"\" " + " WEBSVRCODE=\""+websvrcode+"\" " + " APPFROM=\"1234\"" + " KEEP=\""
				+ keep + "\" />" + " 	<PARAMETERS>" + parametersXml + " 	</PARAMETERS>" + " </PayPlatRequestParameter>";

		return getCommonXml(xml);
	}

	
	/**
	 * 解析json
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void paserJsonByStr() {
		JSONObject object = JSONObject.fromObject(in0Json);
		in0 = object.getString("in0");

		object = JSONObject.fromObject(in1Json).getJSONObject("in1").getJSONObject("Request");
//		Iterator ii = object.entrySet().iterator();
//		while(ii.hasNext()) {
//			System.out.println(ii.next());
//		}
		Iterator<Entry<String, String>> it = object.getJSONObject("VerifyParameter").entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			verifyParameterMap.put(entry.getKey(), entry.getValue());
		}
//		it = object.getJSONObject("PayPlatRequestParameter").getJSONObject("CTRL-INFO").getJSONObject("@attributes").entrySet().iterator();
		JSONObject infoObject = object.getJSONObject("PayPlatRequestParameter");
		payObject.put("PayPlatRequestParameter", infoObject);
		it = infoObject.getJSONObject("CTRL-INFO").entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			attributesMap.put(entry.getKey(), entry.getValue());
		}
		it = object.getJSONObject("PARAMETERS").entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			// parametersMap.put(entry.getKey(), entry.getValue());
			parametersXml += " <" + key + ">" + value + "</" + key + "> ";
		}
	}
	/**
	 * 解析json
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void paserJsonByObject() {
		in0 = object0.getString("in0");
//		Iterator ii = object.entrySet().iterator();
//		while(ii.hasNext()) {
//			System.out.println(ii.next());
//		}
		JSONObject object = object1.getJSONObject("in1").getJSONObject("Request");
		Iterator<Entry<String, String>> it = object.getJSONObject("VerifyParameter").entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			verifyParameterMap.put(entry.getKey(), entry.getValue());
		}
//		it = object.getJSONObject("PayPlatRequestParameter").getJSONObject("CTRL-INFO").getJSONObject("@attributes").entrySet().iterator();
		JSONObject infoObject = object.getJSONObject("PayPlatRequestParameter");
		payObject.put("PayPlatRequestParameter", infoObject);
		it = infoObject.getJSONObject("CTRL-INFO").entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			attributesMap.put(entry.getKey(), entry.getValue());
		}
		it = object.getJSONObject("PayPlatRequestParameter").getJSONObject("PARAMETERS").entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			// parametersMap.put(entry.getKey(), entry.getValue());
			parametersXml += " <" + key + ">" + value + "</" + key + "> ";
		}
	}

	/**
	 * 获取公共的XML头包
	 * 
	 * @param merId
	 * @param channelCode
	 * @param tmnNum
	 * @param sign
	 * @param cer
	 * @param pay
	 * @return
	 */
	private String getCommonXml(String payPlatRequestParameter) {
		String sign = "";
		String cer = "";
		if(payObject!=null)
			cer = payObject.toString();
		System.out.println("json_cer:"+cer);
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<Request><VerifyParameter>" + "<MERID>" + verifyParameterMap.get("MERID")
				+ "</MERID>" + "<CHANNELCODE>" + verifyParameterMap.get("CHANNELCODE") + "</CHANNELCODE>" + "<TMNNUM>"
				+ verifyParameterMap.get("TMNNUM") + "</TMNNUM>" + "<SIGN>" +verifyParameterMap.get("SIGN") + "</SIGN>" + "<CER>"
				+ cer+ "</CER>" + "</VerifyParameter>" + payPlatRequestParameter + "</Request>";
		return xmlStr;
	}

	/**
	 * @return 第一个参数值,该值从提交的json参数中获取
	 */
	public String getIn0() {
		return in0;
	}

	/**
	 * @return 第二个参数值(xml格式)，该值为提交的json参数中获取对应的值后拼成xml
	 */
	public String getIn1(){
		return json2Xml();
	}
}
