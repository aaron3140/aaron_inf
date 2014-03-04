package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.Charset;
import common.xml.CommonRespAbs;

public class DpInf06008Response extends CommonRespAbs {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "RESULTDATESET";

	private static final String TAG_NAME_14_1 = "DATAS";

	private static final String TAG_NAME_14_1_ITEM = "ITEMNUM";
	private static final String TAG_NAME_14_2_ITEM = "CARDITEM";

	private static final String TAG_NAME_14_2_ITEM_ATTR_1 = "CUSTCODE";
	private static final String TAG_NAME_14_2_ITEM_ATTR_2 = "CUSTNAME";
	private static final String TAG_NAME_14_2_ITEM_ATTR_3 = "CARDCODE";
	private static final String TAG_NAME_14_2_ITEM_ATTR_4 = "DAYLIMIT";
	private static final String TAG_NAME_14_2_ITEM_ATTR_5 = "DAYTOTAL";
	private static final String TAG_NAME_14_2_ITEM_ATTR_6 = "REGDATE";
	private static final String TAG_NAME_14_2_ITEM_ATTR_7 = "CARDSTAT";
	private static final String TAG_NAME_14_2_ITEM_ATTR_8 = "REMARK";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT, String ITEMNUM,List<Map<String,String>> list) {

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

		dataSet.addElement(TAG_NAME_14_1_ITEM).addText(ITEMNUM);
		Element datas = dataSet.addElement(TAG_NAME_14_2_ITEM);
		for (Map<String, String> map : list) {
			Element cardItem = datas.addElement(TAG_NAME_14_2_ITEM);
			 cardItem.addAttribute(TAG_NAME_14_2_ITEM_ATTR_1, map.get("CUST_CODE"));
			 cardItem.addAttribute(TAG_NAME_14_2_ITEM_ATTR_2, map.get("CUST_NAME"));
			 cardItem.addAttribute(TAG_NAME_14_2_ITEM_ATTR_3, map.get("ACCT_CODE"));
			 cardItem.addAttribute(TAG_NAME_14_2_ITEM_ATTR_4, map.get("DAYLIMIT"));
			 cardItem.addAttribute(TAG_NAME_14_2_ITEM_ATTR_5, map.get("DAYTOTAL"));
			 cardItem.addAttribute(TAG_NAME_14_2_ITEM_ATTR_6, map.get("REGDATE"));
			 cardItem.addAttribute(TAG_NAME_14_2_ITEM_ATTR_7, map.get("STAT"));
			 cardItem.addAttribute(TAG_NAME_14_2_ITEM_ATTR_8, map.get("REMARK"));
		}

		return doc.asXML();
	}

}
