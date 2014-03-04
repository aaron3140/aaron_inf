package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf02022Response extends CommonRespAbs {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";

	private static final String TAG_NAME_14_1 = "REGTYPE";
	private static final String TAG_NAME_14_2 = "CUSTNAME";
	private static final String TAG_NAME_14_3 = "PRODUCTS";
	private static final String TAG_NAME_14_4 = "ENTERNAME";
	private static final String TAG_NAME_14_5 = "REGDATE";
	private static final String TAG_NAME_14_6 = "REMARK1";
	private static final String TAG_NAME_14_7 = "REMARK2";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,
			String KEEP, String RESULT, String RESPONSECODE,
			String RESPONSECONTENT, String REGTYPE, String CUSTNAME,
			String PRODUCTS, String ENTERNAME, String REGDATE, String REMARK1,
			String REMARK2) {

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);

		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		
		Element dataSet = root.addElement(TAG_NAME_14);
		
		dataSet.addElement(TAG_NAME_14_1).addText(REGTYPE);

		dataSet.addElement(TAG_NAME_14_2).addText(CUSTNAME);
		dataSet.addElement(TAG_NAME_14_3).addText(PRODUCTS);
		dataSet.addElement(TAG_NAME_14_4).addText(ENTERNAME);
		dataSet.addElement(TAG_NAME_14_5).addText(REGDATE);
		dataSet.addElement(TAG_NAME_14_6).addText(REMARK1);
		dataSet.addElement(TAG_NAME_14_7).addText(REMARK2);

		return doc.asXML();
	}

}
