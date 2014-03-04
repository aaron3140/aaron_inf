package common.xml.dp;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf06205Response extends CommonRespAbs {
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";
	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_20 = "RESULTDATESET";
	private static final String TAG_NAME_14 = "ORDERSEQ";
	private static final String TAG_NAME_15 = "TMNNUMNO";
	private static final String TAG_NAME_16 = "STAFFCODE";

	private static final String TAG_NAME_18 = "REMARK1";
	private static final String TAG_NAME_19 = "REMARK2";
	//private static final String TAG_NAME_21 = "TRANSSEQ";
	private static final String TAG_NAME_21 = "SYSTEMNO";
	private static final String TAG_NAME_22 = "TAC";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT, Map<String, String> map,
			String orderSeq, String TmnNumNo, String staffCode,String systemNo,String tac, String REMARK1, String REMARK2) {
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
		dataSet.addElement(TAG_NAME_14).addText(orderSeq);
		dataSet.addElement(TAG_NAME_15).addText(TmnNumNo);
		dataSet.addElement(TAG_NAME_16).addText(staffCode);
		dataSet.addElement(TAG_NAME_21).addText(systemNo);
		dataSet.addElement(TAG_NAME_22).addText(tac);
		

		if (map != null && map.size() > 0) {
			Set<Entry<String, String>> entrySet = map.entrySet();
			Iterator<Entry<String, String>> it = entrySet.iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				String key = entry.getKey();
				String value = entry.getValue();
				dataSet.addElement(key).addText(value);
			}
		}

		dataSet.addElement(TAG_NAME_18).addText(REMARK1);
		dataSet.addElement(TAG_NAME_19).addText(REMARK2);
		return doc.asXML();
	}

}
