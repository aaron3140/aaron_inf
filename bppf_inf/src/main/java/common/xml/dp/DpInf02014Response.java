package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf02014Response extends CommonRespAbs {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "ORDERSEQ";

	private static final String TAG_NAME_15 = "CUSTCODE";

	private static final String TAG_NAME_16 = "OBJECTCODE";

	private static final String TAG_NAME_17 = "TRANSSEQ";

	private static final String TAG_NAME_18 = "APPTRANSSEQ";

	private static final String TAG_NAME_19 = "TXNAMOUNT";

	private static final String TAG_NAME_20 = "REMARK1";

	private static final String TAG_NAME_21 = "REMARK2";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,
			String KEEP, String RESULT, String RESPONSECODE,
			String RESPONSECONTENT, String ORDERSEQ, String CUSTCODE,
			String OBJECTCODE, String TRANSSEQ, String APPTRANSSEQ,String TXNAMOUNT,
			String REMARK1, String REMARK2) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);

		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		root.addElement(TAG_NAME_14).addText(ORDERSEQ);
		root.addElement(TAG_NAME_15).addText(CUSTCODE);
		root.addElement(TAG_NAME_16).addText(OBJECTCODE);
		root.addElement(TAG_NAME_17).addText(TRANSSEQ);
		root.addElement(TAG_NAME_18).addText(APPTRANSSEQ);
		root.addElement(TAG_NAME_19).addText(TXNAMOUNT);
		
		root.addElement(TAG_NAME_20).addText(REMARK1);
		root.addElement(TAG_NAME_21).addText(REMARK2);

		return doc.asXML();
	}

}
