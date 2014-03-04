package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpAccountManagementResponse extends CommonRespAbs  {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_14_1 = "ORDERSEQ";
	
	private static final String TAG_NAME_14_2 = "TRANSSEQ";
	
	private static final String TAG_NAME_14_3= "TXNAMOUNT";
	
	private static final String TAG_NAME_14_4= "TRADEAMOUNT";
	
	private static final String TAG_NAME_14_5= "HANDLEAMOUNT";
	
	private static final String TAG_NAME_14_6 = "OPERTYPE";
	
	private static final String TAG_NAME_14_7 = "REMARK1";
	
	private static final String TAG_NAME_14_8 = "REMARK2";
	
	
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String responseCode,
			String responseDesc, String orderSeq, String transSeq, String txnamount, String tradeamount, String handleamount, String opertype,String REMARK1,String REMARK2) {
		
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
		dataSet.addElement(TAG_NAME_14_1).addText(orderSeq);
		dataSet.addElement(TAG_NAME_14_2).addText(transSeq);
		dataSet.addElement(TAG_NAME_14_3).addText(txnamount);
		dataSet.addElement(TAG_NAME_14_4).addText(tradeamount);
		dataSet.addElement(TAG_NAME_14_5).addText(handleamount);
		dataSet.addElement(TAG_NAME_14_6).addText(opertype);
		dataSet.addElement(TAG_NAME_14_7).addText(REMARK1);
		dataSet.addElement(TAG_NAME_14_8).addText(REMARK2);
		
		return doc.asXML();
	}
}
