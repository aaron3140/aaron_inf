package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf06002Response extends CommonRespAbs {
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "CUSTCODE";
	private static final String TAG_NAME_15 = "TMNNUMNO";
	private static final String TAG_NAME_16 = "OUTTMNNUMNO";
	private static final String TAG_NAME_17 = "ACCEPTAREACODE";
	private static final String TAG_NAME_18 = "ACCEPTDATE";
	private static final String TAG_NAME_19 = "DATAS";
	private static final String TAG_NAME_19_ATTR_1 = "PAYMENTCODE";
	private static final String TAG_NAME_19_ATTR_2 = "BUSCODE";
	private static final String TAG_NAME_19_ATTR_3 = "PAYMENTNAME";
	private static final String TAG_NAME_19_ATTR_4 = "BUSNAME";
	private static final String TAG_NAME_20 = "BUSDATAS";
	private static final String TAG_NAME_21 = "REMARK1";
	private static final String TAG_NAME_22 = "REMARK2";
	private static final String TAG_NAME_23 = "RESULTDATESET";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT, String CUSTCODE, String TMNNUMNO,
			 String ACCEPTAREACODE, String ACCEPTDATE, List<Map<String,String>> items, String REMARK1, String REMARK2) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);

		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		Element dataSet = root.addElement(TAG_NAME_23);
		dataSet.addElement(TAG_NAME_14).addText(CUSTCODE);
		dataSet.addElement(TAG_NAME_15).addText(TMNNUMNO);
//		dataSet.addElement(TAG_NAME_16).addText(OUTTMNNUMNO);
		dataSet.addElement(TAG_NAME_17).addText(ACCEPTAREACODE);
		dataSet.addElement(TAG_NAME_18).addText(ACCEPTDATE);
		
//		Element dataItem = dataSet.addElement(TAG_NAME_19).addElement(TAG_NAME_20);
		Element data = dataSet.addElement(TAG_NAME_19);
		for (Map<String, String> map : items) {
			Element dataItem =data.addElement(TAG_NAME_20);
			dataItem.addAttribute(TAG_NAME_19_ATTR_1, map.get(TAG_NAME_19_ATTR_1));
			dataItem.addAttribute(TAG_NAME_19_ATTR_2, map.get(TAG_NAME_19_ATTR_2));
			dataItem.addAttribute(TAG_NAME_19_ATTR_3, map.get(TAG_NAME_19_ATTR_3));
			dataItem.addAttribute(TAG_NAME_19_ATTR_4, map.get(TAG_NAME_19_ATTR_4));
		}
		
		dataSet.addElement(TAG_NAME_21).addText(REMARK1);
		dataSet.addElement(TAG_NAME_22).addText(REMARK2);
		return doc.asXML();
	}

}
