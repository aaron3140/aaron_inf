package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf06005Response extends CommonRespAbs {
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "TMNNUMNO";
	private static final String TAG_NAME_15 = "ORDERSEQ";
	private static final String TAG_NAME_16 = "SYSTEMNO";
	private static final String TAG_NAME_17 = "ACCOUNTBALANCE";
	private static final String TAG_NAME_18 = "REMARK1";
	private static final String TAG_NAME_19 = "REMARK2";
	private static final String TAG_NAME_20 = "RESULTDATESET";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT, String TMNNUMNO, String ORDERSEQ,
			String SYSTEMNO, String ACCOUNTBALANCE, String REMARK1, String REMARK2) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);

		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		Element dataSet = root.addElement(TAG_NAME_20);
		dataSet.addElement(TAG_NAME_14).addText(TMNNUMNO);
		dataSet.addElement(TAG_NAME_15).addText(ORDERSEQ);
		dataSet.addElement(TAG_NAME_16).addText(SYSTEMNO);
		dataSet.addElement(TAG_NAME_17).addText(ACCOUNTBALANCE);
		dataSet.addElement(TAG_NAME_18).addText(REMARK1);
		dataSet.addElement(TAG_NAME_19).addText(REMARK2);
		return doc.asXML();
	}

}
