package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf2003Responset extends CommonRespAbs{
	
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_14_11="VERSION";
	
	private static final String TAG_NAME_14_12="ISOPTIONAL";
	
	private static final String TAG_NAME_14_13 = "URL";
	
	private static final String TAG_NAME_14_14 = "FORCEUPGRADE";
	
	private static final String TAG_NAME_14_15 = "UPDATEINFO";
	
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,String KEEP, String RESULT, 
			String RESPONSECODE, String RESPONSECONTENT,String version, String isOptional, String url,String forceupgrade,String UPDATEINFO) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		
		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		
		Element dataSet = root.addElement(TAG_NAME_14);
		
		dataSet.addElement(TAG_NAME_14_11).addText(version);
		dataSet.addElement(TAG_NAME_14_12).addText(isOptional);
		dataSet.addElement(TAG_NAME_14_13).addText(url);
		dataSet.addElement(TAG_NAME_14_14).addText(forceupgrade);
		
		UPDATEINFO = UPDATEINFO.replaceAll("\r\n", "|");
		
		dataSet.addElement(TAG_NAME_14_15).addText(UPDATEINFO);
		
		return doc.asXML();
	}
	
	public String toXMLStr(String RESULT, String KEEP,String RESPONSECODE,String RESPONSECONTENT){

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, RESPONSECODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSECONTENT);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		return doc.asXML();
	}
	

}
