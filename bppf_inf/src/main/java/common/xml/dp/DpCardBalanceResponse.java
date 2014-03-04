package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import common.xml.CommonRespAbs;

public class DpCardBalanceResponse extends CommonRespAbs {
	
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_141 = "CARDINFOS";
	
	//private static final String TAG_NAME_141_ATTR_1 = "CARDSEQ";
	
	//private static final String TAG_NAME_141_ATTR_2 = "CARDTYPE";
	
	//private static final String TAG_NAME_141_ATTR_3 = "CARDAMT";
	
	//private static final String TAG_NAME_141_ATTR_4 = "CARDNO";
	
	//private static final String TAG_NAME_141_ATTR_5 = "BALANCE";
	
	//private static final String TAG_NAME_141_ATTR_6 = "EXPDATE";

	private static final String TAG_NAME_141_ATTR_BALANCE = "BALANCE";
	
	private static final String TAG_NAME_141_ATTR_AVAILBALANCE = "AVAILBALANCE";
	
	public String toXMLStr(String RESULT, String KEEP, String REQWEBSVRCODE,String RESPONSECODE ,
			String RESPONSECONTENT, String BALANCE, String AVAILBALANCE) {
		//responseCode补充到6位
		String newResCode = newCode(RESPONSECODE);
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, "20");
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		
		root.addElement(TAG_NAME_12).addText(newResCode);
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		
		Element dataSet = root.addElement(TAG_NAME_14);
		Element cardInfo = dataSet.addElement(TAG_NAME_141);
		//cardInfo.addAttribute(TAG_NAME_141_ATTR_2, CARDTYPE);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_BALANCE, BALANCE);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_AVAILBALANCE, AVAILBALANCE);
		
		
		
		return doc.asXML();
	}
	

}
