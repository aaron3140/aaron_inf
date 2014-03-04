package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf02006Responset extends CommonRespAbs{
	
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";

	private static final String TAG_NAME_14_11="DATAS";
	
	private static String [] dataKeys={"PREORDERID","CUSTCODE","OBJCODE","OBJNAME","AMOUNT","REQDATE","MEMO","STAT"};
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,String KEEP, String RESULT, 
			String RESPONSECODE, String RESPONSECONTENT, List payBillList) {
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
		
		if (payBillList.size() > 0) {
			for (int i = 0,y = payBillList.size(); i < y; i++) {
				Element datas = dataSet.addElement(TAG_NAME_14_11);
				String value="";
				for (String key:dataKeys) {
					value=(String)((Map)payBillList.get(i)).get(key);
					if (value==null) {
						value="";
					}
					datas.addAttribute(key,value);
				}
			}
		}else{
			Element datas = dataSet.addElement(TAG_NAME_14_11);
		}
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
