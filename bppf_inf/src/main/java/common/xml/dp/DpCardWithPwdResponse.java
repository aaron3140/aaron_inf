package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import common.xml.CommonRespAbs;

public class DpCardWithPwdResponse extends CommonRespAbs {
	
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
	
	private static final String TAG_NAME_141_ATTR_1 = "CARDSEQ";
	
	private static final String TAG_NAME_141_ATTR_2 = "CARDAMT";
	
	private static final String TAG_NAME_141_ATTR_3 = "CARDNO";
	
	private static final String TAG_NAME_141_ATTR_4 = "BALANCE";
	
	private static final String TAG_NAME_141_ATTR_5 = "AVAILBALANCE";
	
	private static final String TAG_NAME_141_ATTR_6 = "FROZEN_BAL";
	
	private static final String TAG_NAME_141_ATTR_7 = "SERVICE_FEE";
	
	private static final String TAG_NAME_141_ATTR_8 = "CARDSTAT";
	
	private static final String TAG_NAME_141_ATTR_9 = "CARDTYPENAME";
	
	private static final String TAG_NAME_141_ATTR_10 = "CARDTYPE";
	
	private static final String TAG_NAME_141_ATTR_11 = "CARDHOLDER_NAME";
	
	private static final String TAG_NAME_141_ATTR_12 = "EXPDATE";
	
	private static final String TAG_NAME_141_ATTR_13 = "MAKECARD_DATE";
	
	private static final String TAG_NAME_141_ATTR_14 = "FROZEN_DATE";
	
	private static final String TAG_NAME_141_ATTR_15 = "UNFROZEN_DATE";
	
	private static final String TAG_NAME_141_ATTR_16 = "LAST_DATE";
	
	private static final String TAG_NAME_141_ATTR_17 = "CARDHOLDER_IDTYPE";
	
	private static final String TAG_NAME_141_ATTR_18 = "ID";
	
	private static final String TAG_NAME_141_ATTR_19 = "PHONE";
	
	public String toXMLStr(String RESULT, String KEEP, String REQWEBSVRCODE,String RESPONSECODE ,
			String RESPONSECONTENT, String CARDSEQ, String CARDTYPE, String CARDAMT, String CARDNO, 
			String BALANCE, String AVAILBALANCE, String EXPDATE, String FROZEN_BAL, String SERVICE_FEE,
			String CARDSTAT, String CARDTYPENAME, String CARDHOLDER_NAME, String MAKECARD_DATE,
			String FROZEN_DATE, String UNFROZEN_DATE, String LAST_DATE, String CARDHOLDER_IDTYPE,
			String ID, String PHONE) {
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
		cardInfo.addAttribute(TAG_NAME_141_ATTR_1, CARDSEQ);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_2, CARDAMT);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_3, CARDNO);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_4, BALANCE);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_5, AVAILBALANCE);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_6, FROZEN_BAL);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_7, SERVICE_FEE);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_8, CARDSTAT);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_9, CARDTYPENAME);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_10, CARDTYPE);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_11, CARDHOLDER_NAME);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_12, EXPDATE);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_13, MAKECARD_DATE);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_14, FROZEN_DATE);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_15, UNFROZEN_DATE);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_16, LAST_DATE);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_17, CARDHOLDER_IDTYPE);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_18, ID);
		cardInfo.addAttribute(TAG_NAME_141_ATTR_19, PHONE);
		
		
		
		
		return doc.asXML();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
}
