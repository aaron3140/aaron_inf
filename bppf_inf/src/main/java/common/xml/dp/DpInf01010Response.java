package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf01010Response extends CommonRespAbs  {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_14_1 = "TERMSEQ";
	
	private static final String TAG_NAME_14_2 = "OBJCODE";
	
	private static final String TAG_NAME_14_3 = "OBJTYPE";
	
	private static final String TAG_NAME_14_4 = "PAYAMOUNT";
	
	private static final String TAG_NAME_14_5 = "PAYTIME";
	
	private static final String TAG_NAME_14_6 = "RETUNRNCODE";
	
	private static final String TAG_NAME_14_7 = "RESPONSECODE";
	
	private static final String TAG_NAME_14_8 = "HMAC";
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String responseCode,
			String responseDesc, String termSeq, String objCode,
			String objType, String payAmount, String payTime,
			String returnCode, String responseCode2, String hmac) {
		
		String newResCode = newCode(responseCode);
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		
		root.addElement(TAG_NAME_12).addText(newResCode);
		root.addElement(TAG_NAME_13).addText(responseDesc);
		
		Element dataSet = root.addElement(TAG_NAME_14);
		dataSet.addElement(TAG_NAME_14_1).addText(termSeq);
		dataSet.addElement(TAG_NAME_14_2).addText(objCode);
		dataSet.addElement(TAG_NAME_14_3).addText(objType);
		dataSet.addElement(TAG_NAME_14_4).addText(payAmount);
		dataSet.addElement(TAG_NAME_14_5).addText(payTime);
		dataSet.addElement(TAG_NAME_14_6).addText(returnCode);
		dataSet.addElement(TAG_NAME_14_7).addText(responseCode2);
		dataSet.addElement(TAG_NAME_14_8).addText(hmac);
	
		return doc.asXML();
	}
}
