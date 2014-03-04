package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf02026Response  extends CommonRespAbs {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_14_1 = "TMNNUMNO";

	private static final String TAG_NAME_14_2 = "ORDERSEQ";

	private static final String TAG_NAME_14_3 = "TRANSSEQ";

	private static final String TAG_NAME_14_4 = "SYSTEMNO";

	private static final String TAG_NAME_14_5 = "TXNAMOUNT";

	private static final String TAG_NAME_14_6 = "REMARK1";

	private static final String TAG_NAME_14_7 = "REMARK2";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,
			String KEEP, String RESULT, String RESPONSECODE,
			String RESPONSECONTENT,String TMNNUMNO,String ORDERSEQ, String TRANSSEQ,
			String SYSTEMNO, String TXNAMOUNT,
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

		Element resultData = root.addElement(TAG_NAME_14);
		resultData.addElement(TAG_NAME_14_1).addText(TMNNUMNO);
		resultData.addElement(TAG_NAME_14_2).addText(ORDERSEQ);
		resultData.addElement(TAG_NAME_14_3).addText(TRANSSEQ);
		resultData.addElement(TAG_NAME_14_4).addText(SYSTEMNO);
		resultData.addElement(TAG_NAME_14_5).addText(TXNAMOUNT);
		resultData.addElement(TAG_NAME_14_6).addText(REMARK1);
		resultData.addElement(TAG_NAME_14_7).addText(REMARK2);

		return doc.asXML();
	}

}
