package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

/**
 * @title DpInf02046Response.java
 * @description 理财申购接口回复类
 * @date 2014-02-11 09:35
 * @author lichunan
 * @version 1.0
 */
public class DpInf02046Response extends CommonRespAbs {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "RESULTDATESET";
	private static final String TAG_NAME_14_1 = "BUSINESSORDERNO";
	private static final String TAG_NAME_14_2 = "REMARK1";
	private static final String TAG_NAME_14_3 = "REMARK2";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,
			String KEEP, String RESULT, String RESPONSECODE,
			String RESPONSECONTENT, String BUSINESSORDERNO, String REMARK1,
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
		dataSet.addElement(TAG_NAME_14_1).addText(BUSINESSORDERNO);
		dataSet.addElement(TAG_NAME_14_2).addText(REMARK1);
		dataSet.addElement(TAG_NAME_14_3).addText(REMARK2);
		return doc.asXML();
	}
}
