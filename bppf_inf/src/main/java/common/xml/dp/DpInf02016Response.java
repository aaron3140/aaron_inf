package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf02016Response extends CommonRespAbs {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "RESULTDATESET";

	private static final String TAG_NAME_14_1 = "STARTDATE";

	private static final String TAG_NAME_14_2 = "ENDDATE";

	private static final String TAG_NAME_14_3 = "TOTALNUMS";

	private static final String TAG_NAME_14_4 = "DATAS";

	private static final String TAG_NAME_14_4_1 = "ORDERITEM";

	private static final String[] ORDER_ATTR = { "SYSTEMNO", "CARDCODE",
			"ACCTTYPE", "ACCTCODE", "TRANSCODE", "TRANSNAME", "INCOME",
			"TRANSAMOUNT", "TRANSBALANCE", "TRANSSOURCE", "TRANSDATE",
			"TRANSTIME", "TRANSNOTESDATE", "RESULTCODE", "RESULTCODEDEC",
			"ORDERNO", "EXECUTION", "REMARK" };

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,
			String KEEP, String RESULT, String RESPONSECODE,
			String RESPONSECONTENT, String STARTDATE, String ENDDATE,
			String TOTALNUMS, List<Map<String, String>> list) {

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
		dataSet.addElement(TAG_NAME_14_1).addText(STARTDATE);
		dataSet.addElement(TAG_NAME_14_2).addText(ENDDATE);
		if (!(null == TOTALNUMS)) {
			dataSet.addElement(TAG_NAME_14_3).addText(TOTALNUMS);
		}

		Element datas = dataSet.addElement(TAG_NAME_14_4);
		for (int i = 0; i < list.size(); i++) {
			Element item = datas.addElement(TAG_NAME_14_4_1);
			Map<String, String> map = list.get(i);

			for (String attr : ORDER_ATTR) {
				item.addAttribute(attr, map.get(attr));
			}
		}
		return doc.asXML();
	}

}
