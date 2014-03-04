package common.xml.dp;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.DateTime;
import common.xml.CommonRespAbs;

public class DpInf05105Response extends CommonRespAbs {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "RESULTDATESET";

	private static final String TAG_NAME_14_4 = "DATAS";

	private static final String TAG_NAME_14_4_1 = "PLANITEM";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,
			String KEEP, String RESULT, String RESPONSECODE,
			String RESPONSECONTENT, List<Map<String, Object>> DATAS) {

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

		Element detail = dataSet.addElement(TAG_NAME_14_4);

		for (int i = 0; i < DATAS.size(); i++) {
			Element trainDetail = detail.addElement(TAG_NAME_14_4_1);

			Map<String, Object> map = (Map<String, Object>) DATAS.get(i);

			Iterator<?> iterator = map.keySet().iterator();

			while (iterator.hasNext()) {

				String key = (String) iterator.next();

				if ("PLAN_DATE".equals(key)) {

					trainDetail.addAttribute(key, DateTime.trimLast(map
							.get(key).toString()));

				} else {

					trainDetail.addAttribute(key, map.get(key).toString());
				}

			}

		}

		return doc.asXML();
	}
}
