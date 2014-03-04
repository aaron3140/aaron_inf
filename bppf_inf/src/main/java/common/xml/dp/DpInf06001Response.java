package common.xml.dp;

import java.util.ArrayList;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf06001Response extends CommonRespAbs {
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
	private static final String TAG_NAME_16 = "KEYDATAS";
	private static final String TAG_NAME_17 = "KEYITEM";
	private static final String TAG_NAME_17_ATTR_1 = "KEYTYPE";
	private static final String TAG_NAME_17_ATTR_2 = "KEYVALUE";
	private static final String TAG_NAME_17_ATTR_3 = "KEYMAC";
	private static final String TAG_NAME_18 = "NETWORKNO";
	private static final String TAG_NAME_19 = "REMARK1";
	private static final String TAG_NAME_20 = "REMARK2";
	private static final String TAG_NAME_21 = "RESULTDATESET";

	@SuppressWarnings("unchecked")
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT,
			String ORDERSEQ, String REMARK1, String REMARK2, String TMNNUMNO, ArrayList keyTypes, ArrayList keyValues, ArrayList keyMacValues,
			String networkNo) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);

		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		Element dataSet = root.addElement(TAG_NAME_21);
		dataSet.addElement(TAG_NAME_14).addText(TMNNUMNO);
		dataSet.addElement(TAG_NAME_15).addText(ORDERSEQ);
		Element keyDatas = root.addElement(TAG_NAME_16);
		String macValues = "";
		for (int i = 0; i < keyTypes.size(); i++) {
			Element keyItems = keyDatas.addElement(TAG_NAME_17);
			keyItems.addAttribute(TAG_NAME_17_ATTR_1, (String) keyTypes.get(i));
			keyItems.addAttribute(TAG_NAME_17_ATTR_2, (String) keyValues.get(i));
			macValues = (String) keyMacValues.get(i);
			if(macValues!=null&&macValues.length()>8)
				macValues = macValues.substring(0, 8);
			keyItems.addAttribute(TAG_NAME_17_ATTR_3, macValues);
		}
		dataSet.addElement(TAG_NAME_18).addText(networkNo);
		dataSet.addElement(TAG_NAME_19).addText(REMARK1);
		dataSet.addElement(TAG_NAME_20).addText(REMARK2);
		return doc.asXML();
	}

}
