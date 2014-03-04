package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf02010Response extends CommonRespAbs {
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "ORDERSEQ";
	private static final String TAG_NAME_15 = "TRANSSEQ";
	private static final String TAG_NAME_16 = "TXNAMOUNT";
	private static final String TAG_NAME_17 = "REMARK1";
	private static final String TAG_NAME_18 = "REMARK2";
	private static final String TAG_NAME_19 = "RESULTDATESET";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT,
			String ORDERSEQ, String REMARK1, String REMARK2, String TRANSSEQ, String TXNAMOUNT) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);

		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		Element dataSet = root.addElement(TAG_NAME_19);
		dataSet.addElement(TAG_NAME_14).addText(ORDERSEQ);
		dataSet.addElement(TAG_NAME_15).addText(TRANSSEQ);
		dataSet.addElement(TAG_NAME_16).addText(TXNAMOUNT);
		dataSet.addElement(TAG_NAME_17).addText(REMARK1);
		dataSet.addElement(TAG_NAME_18).addText(REMARK1);
		return doc.asXML();
	}

}
