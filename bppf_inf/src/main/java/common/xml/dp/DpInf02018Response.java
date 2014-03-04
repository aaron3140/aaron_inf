package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf02018Response extends CommonRespAbs {

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

	private static final String TAG_NAME_14_3 = "CARDAMOUNT";

	private static final String TAG_NAME_14_4 = "CARDNO";

	private static final String TAG_NAME_14_5 = "CARDPASSWORD";
	
	private static final String TAG_NAME_14_6 = "EXPDATE";

	private static final String TAG_NAME_14_7 = "REMARK1";

	private static final String TAG_NAME_14_8 = "REMARK2";
	
	private static final String TAG_NAME_14_9 = "PAYAMOUNT";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,
			String KEEP, String RESULT, String RESPONSECODE,
			String RESPONSECONTENT, String ORDERSEQ, String TRANSSEQ,
			String CARDAMOUNT, String CARDNO, String CARDPASSWORD,String EXPDATE, 
			String REMARK1, String REMARK2,String PAYAMOUNT) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);

		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);

		Element resultData = root.addElement(TAG_NAME_14);
		resultData.addElement(TAG_NAME_14_1).addText(ORDERSEQ);
		resultData.addElement(TAG_NAME_14_2).addText(TRANSSEQ);
		
		if(PAYAMOUNT !=null){
			resultData.addElement(TAG_NAME_14_9).addText(PAYAMOUNT);
		}
		resultData.addElement(TAG_NAME_14_3).addText(CARDAMOUNT);
		resultData.addElement(TAG_NAME_14_4).addText(CARDNO);
		resultData.addElement(TAG_NAME_14_5).addText(CARDPASSWORD);
		resultData.addElement(TAG_NAME_14_6).addText(EXPDATE);
		resultData.addElement(TAG_NAME_14_7).addText(REMARK1);
		resultData.addElement(TAG_NAME_14_8).addText(REMARK2);

		return doc.asXML();
	}
}
