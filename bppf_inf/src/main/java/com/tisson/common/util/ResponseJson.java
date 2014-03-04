package com.tisson.common.util;

import java.io.Serializable;
import java.util.Iterator;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultAttribute;

/**
 * @author 邱亚建 2013-4-23 上午11:17:47<br>
 *         TODO 响应的json
 * 
 */
public class ResponseJson implements Serializable {

	private static final long serialVersionUID = -6259846770302732145L;
	/**
	 * 要转换为json的xml
	 */
	private String xmlIn;
	private String resultDatesetJson = "";

	private String SIGN;
	private String CER;
	private String RESPONSECODE;
	private String RESPONSECONTENT;

	// RESPONSE-INFO属性
	private String REQWEBSVRCODE;
	private String RESPONSETYPE;
	private String KEEP;
	private String RESULT;
	
	private boolean hasResultDateset = true;

	public ResponseJson(String xmlIn) throws Exception {
		this.xmlIn = xmlIn;
		paserXml();
	}

	/**
	 * @return xml对应的json
	 */
	public String xml2Json() {
		return getCommonJson(resultDatesetJson);
	}

	/**
	 * @return xml对应的json对象
	 */
	public JSONObject getJsonObject(){
		return JSONObject.fromObject(xml2Json());
	}
	
	@SuppressWarnings("unchecked")
	private void paserXml() throws Exception {
		Document doc = DocumentHelper.parseText(xmlIn);
		RESPONSECODE = getNodeTextM(doc, "RESPONSECODE");
		RESPONSECONTENT = getNodeTextM(doc, "RESPONSECONTENT");
		SIGN = getNodeTextM(doc, "SIGN");
		CER = getNodeTextM(doc, "CER");
//		CER = "";

		Element element = (Element) getNodeM(doc, "RESPONSE-INFO ");
		REQWEBSVRCODE = getAttrM(element, "REQWEBSVRCODE");
		RESPONSETYPE = getAttrM(element, "RESPONSETYPE");
		KEEP = getAttrM(element, "KEEP");
		RESULT = getAttrM(element, "RESULT");
		if(Long.valueOf(RESPONSECODE) !=0  && !"011014".endsWith(RESPONSECODE)) {
			hasResultDateset = false;
			return;
		}
		element = (Element) getNodeM(doc, "RESULTDATESET ");
		if(element!=null){
		Iterator it = element.elementIterator();
		while (it.hasNext()) {
			Element el = (Element) it.next();
			if (el.isTextOnly()) {
				resultDatesetJson += "\"" + el.getName() + "\":\"" + el.getTextTrim() + "\",";
			} else {
				Iterator elementIterator = el.elementIterator();
				String itemName = "";
				String attr = "";
				while (elementIterator.hasNext()) {
					Element element2 = (Element) elementIterator.next();
					itemName = element2.getName();
					Iterator<DefaultAttribute> attributeIterator = element2.attributeIterator();
//					attr += "{{\"@attributes\":{";
					attr += "{";
					while (attributeIterator.hasNext()) {
						DefaultAttribute attribute = attributeIterator.next();
						attr += "\"" + attribute.getName() + "\":\"" + attribute.getStringValue() + "\",";
					}
					attr = deleteLastDot(attr);
//					attr += "}},";
					attr += "},";
				}
				resultDatesetJson +=put2DatasTemplete(attr,itemName);
			}
		}}
	}

	private String put2DatasTemplete(String datasItem,String itemName) {
		String data = "\"DATAS\":{\""+itemName+"\":[" + datasItem + "]}";
		return deleteLastDot(data)+",";
	}

	private String getCommonJson(String resultDateset) {
		String json = "{\"Response\":{\"VerifyParameter\": {\"SIGN\": \"" + SIGN + "\", \"CER\": \"" + CER
				+ "\"},\"PayPlatResponseParameter\": { \"RESPONSE-INFO\": { \"REQWEBSVRCODE\": \"" + REQWEBSVRCODE
				+ "\",\"RESPONSETYPE\": \"" + RESPONSETYPE + "\",\"KEEP\": \"" + KEEP + "\", \"RESULT\": \"" + RESULT + "\"},\"RESPONSECODE\": \""
				+ RESPONSECODE + "\",\"RESPONSECONTENT\": \"" + RESPONSECONTENT + "\""+(hasResultDateset?",\"RESULTDATESET\": {" + deleteLastDot(resultDateset) + "}":"")+"}}}";
		return json;

	}

	/**
	 * 删除最后一个逗号
	 * @param str
	 * @return
	 */
	private String deleteLastDot(String str) {
		if (StringUtils.isNotBlank(str)) {
			if (str.lastIndexOf(',') != -1)
				return str.substring(0, str.lastIndexOf(',')) +str.substring(str.lastIndexOf(',')+1, 	str.length())  ;
			else
				return str;
		}
		return str;
	}

	private String getNodeTextM(Document doc, String nodeName) throws Exception {
		Element e = (Element) getNodeM(doc, nodeName);
		if(e==null) return "";
		return e.getTextTrim();
	}

	private Node getNodeM(Document doc, String nodeName) throws Exception {
		Node n = doc.selectSingleNode("//" + nodeName);
		return n;
	}

	private String getAttr(Element ele, String attrName) {
		return ele.valueOf("@" + attrName);
	}

	private String getAttrM(Element ele, String attrName) throws Exception {
		String s = getAttr(ele, attrName);
		return s;
	}
	
	public static void main(String[] args) throws Exception {
//		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><PayPlatResponseParameter><RESPONSE-INFO REQWEBSVRCODE=\"test\" RESPONSETYPE=\"20\" KEEP=\"440106014048201311050949022495\" RESULT=\"SUCCESS\"/><RESPONSECODE>000000</RESPONSECODE><RESPONSECONTENT>成功</RESPONSECONTENT><RESULTDATESET><CUSTCODE>13250256899</CUSTCODE><STAFFCODE>13250256899test</STAFFCODE><DATAS><VERIFYITEM CERNO=\"0\" REGVERIFYCODE=\"0\" BANKACCT=\"0\"/></DATAS><REMARK1>remark1</REMARK1><REMARK2>remark2</REMARK2></RESULTDATESET></PayPlatResponseParameter>";
//		String xml = "<PayPlatResponseParameter><RESPONSE-INFO REQWEBSVRCODE=\"INF02029\" RESPONSETYPE=\"20\" KEEP=\"440106003094201311061444551420\" RESULT=\"SUCCESS\"/><RESPONSECODE>011014</RESPONSECODE><RESPONSECONTENT>客户信息验证不通过</RESPONSECONTENT><RESULTDATESET><CUSTCODE>mzl</CUSTCODE><STAFFCODE>mzl</STAFFCODE><DATAS><VERIFYITEM CONTACTNO=\"0\"/></DATAS><REMARK1></REMARK1><REMARK2></REMARK2></RESULTDATESET></PayPlatResponseParameter>";
//		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><PayPlatResponseParameter><RESPONSE-INFO REQWEBSVRCODE=\"test\" RESPONSETYPE=\"20\" KEEP=\"440106014048201311050949022495\" RESULT=\"SUCCESS\"/><RESPONSECODE>000000</RESPONSECODE><RESPONSECONTENT>成功</RESPONSECONTENT><RESULTDATESET><CUSTCODE>13250256899</CUSTCODE><STAFFCODE>13250256899test</STAFFCODE><REMARK1>remark1</REMARK1><REMARK2>remark2</REMARK2></RESULTDATESET></PayPlatResponseParameter>";
//		String data = "<DATAS><VERIFYITEM CERNO=\"0\" REGVERIFYCODE=\"0\" BANKACCT=\"0\"/></DATAS>";
		
//		String xml="<Response><VerifyParameter><SIGN></SIGN><CER></CER></VerifyParameter><PayPlatResponseParameter><RESPONSE-INFO REQWEBSVRCODE=\"test\" RESPONSETYPE=\"20\" KEEP=\"500012000001201401020205540001\" RESULT=\"SUCCESS\"/><RESPONSECODE>000000</RESPONSECODE><RESPONSECONTENT> 成功</RESPONSECONTENT><RESULTDATESET><msgs><msgs ISSUE_TYPE=\"01\" ISSUE_NAME=\"111\" CHANNEL=\"01\" STAFF_NAME=\"系统管理员\"/><msgs ISSUE_TYPE=\"01\" ISSUE_NAME=\"0\" CHANNEL=\"01\" STAFF_NAME=\"系统管理员\"/></msgs></RESULTDATESET></PayPlatResponseParameter></Response>";
		String xml="<Response><VerifyParameter><SIGN></SIGN><CER></CER></VerifyParameter><PayPlatResponseParameter><RESPONSE-INFO REQWEBSVRCODE=\"test\" RESPONSETYPE=\"20\" KEEP=\"500012000001201401020205540001\" RESULT=\"SUCCESS\"/><RESPONSECODE>000000</RESPONSECODE><RESPONSECONTENT> 成功</RESPONSECONTENT><RESULTDATESET><msgs><msgs ISSUE_TYPE=\"01\" ISSUE_NAME=\"0\" CHANNEL=\"01\" STAFF_NAME=\"系统管理员\"/></msgs></RESULTDATESET></PayPlatResponseParameter></Response>";
		
		ResponseJson responseJson = new ResponseJson(xml);
		xml = responseJson.xml2Json();
		System.out.println(xml);
	}
	
	
}
