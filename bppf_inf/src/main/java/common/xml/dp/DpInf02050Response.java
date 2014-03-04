package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

/**
 * @title DpInf02050Response.java
 * @description 理财余额查询接口回复类
 * @date 2014-02-07 14:37
 * @author lichunan
 * @version 1.0
 */
public class DpInf02050Response extends CommonRespAbs {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "RESULTDATESET";
	private static final String TAG_NAME_14_1 = "BALANCE";
	private static final String TAG_NAME_14_2 = "CARRDATEINCOME";
	private static final String TAG_NAME_14_3 = "INCOME";
	private static final String TAG_NAME_14_4 = "YEARRATE";
	private static final String TAG_NAME_14_5 = "PERMILINCOME";
	private static final String TAG_NAME_14_6 = "SEVENDAYINCOME";
	private static final String TAG_NAME_14_7 = "THIRTYDAYINCOME";
	private static final String TAG_NAME_14_8 = "REMARK1";
	private static final String TAG_NAME_14_9 = "REMARK2";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,
			String KEEP, String RESULT, String RESPONSECODE,
			String RESPONSECONTENT, String BALANCE, String CARRDATEINCOME,
			String INCOME, String YEARRATE, String PERMILINCOME,
			String SEVENDAYINCOME, String THIRTYDAYINCOME, String REMARK1,
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
		dataSet.addElement(TAG_NAME_14_1).addText(BALANCE);
		dataSet.addElement(TAG_NAME_14_2).addText(CARRDATEINCOME);
		dataSet.addElement(TAG_NAME_14_3).addText(INCOME);
		dataSet.addElement(TAG_NAME_14_4).addText(YEARRATE);
		dataSet.addElement(TAG_NAME_14_5).addText(PERMILINCOME);
		dataSet.addElement(TAG_NAME_14_6).addText(SEVENDAYINCOME);
		dataSet.addElement(TAG_NAME_14_7).addText(THIRTYDAYINCOME);
		dataSet.addElement(TAG_NAME_14_8).addText(REMARK1);
		dataSet.addElement(TAG_NAME_14_9).addText(REMARK2);
		return doc.asXML();
	}

}
