package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf02025Response extends CommonRespAbs {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "RESULTDATESET";

	private static final String TAG_NAME_14_1 = "SYSTEMNO";

	private static final String TAG_NAME_14_2 = "BILLNUM";

	private static final String TAG_NAME_14_3 = "DATAS";

	private static final String TAG_NAME_14_4 = "TMNNUMNO";

	private static final String TAG_NAME_14_5_1 = "BillDetail";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT, String SYSTEMNO, String BILLNUM, String TMNNUMNO, List<List<Map<String, String>>> list) {

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
		dataSet.addElement(TAG_NAME_14_1).addText(SYSTEMNO);
		dataSet.addElement(TAG_NAME_14_2).addText(BILLNUM);
		dataSet.addElement(TAG_NAME_14_4).addText(TMNNUMNO);
		Element datas = dataSet.addElement(TAG_NAME_14_3);
		detailDate(datas, list);
		return doc.asXML();
	}

	private void detailDate(Element datas, List<List<Map<String, String>>> list) {

		for (int i = 0; i < list.size(); i++) {
			List<Map<String, String>> data = list.get(i);
			Element item = datas.addElement(TAG_NAME_14_5_1);
			for (int j = 0; j < data.size(); j++) {
				Map<String, String> map = data.get(j);
				if ("J012".equals(map.get("KEY"))) {
					item.addAttribute("J012", map.get("VALUE"));
				} else if ("J013".equals(map.get("KEY"))) {
					item.addAttribute("J013", map.get("VALUE"));
				} else if ("J014".equals(map.get("KEY"))) {
					item.addAttribute("J014", map.get("VALUE"));
				} else if ("J016".equals(map.get("KEY"))) {
					item.addAttribute("J016", map.get("VALUE"));
				} else if ("J017".equals(map.get("KEY"))) {
					item.addAttribute("J017", map.get("VALUE"));
				} else if ("J018".equals(map.get("KEY"))) {
					item.addAttribute("J018", map.get("VALUE"));
				} else if ("J019".equals(map.get("KEY"))) {
					item.addAttribute("J019", map.get("VALUE"));
				} else if ("J022".equals(map.get("KEY"))) {
					item.addAttribute("J022", map.get("VALUE"));
				} else if ("J023".equals(map.get("KEY"))) {
					item.addAttribute("J023", map.get("VALUE"));
				} else if ("J024".equals(map.get("KEY"))) {
					item.addAttribute("J024", map.get("VALUE"));
				} else if ("J025".equals(map.get("KEY"))) {
					item.addAttribute("J025", map.get("VALUE"));
				} else if ("J026".equals(map.get("KEY"))) {
					item.addAttribute("J026", map.get("VALUE"));
				} else if ("J027".equals(map.get("KEY"))) {
					item.addAttribute("J027", map.get("VALUE"));
				} else if ("J028".equals(map.get("KEY"))) {
					item.addAttribute("J028", map.get("VALUE"));
				} else if ("J039".equals(map.get("KEY"))) {
					item.addAttribute("J039", map.get("VALUE"));
				} else if ("J015".equals(map.get("KEY"))) {
					item.addAttribute("J015", map.get("VALUE"));
				} else if ("J037".equals(map.get("KEY"))) {
					item.addAttribute("J037", map.get("VALUE"));
				} else if ("J038".equals(map.get("KEY"))) {
					item.addAttribute("J038", map.get("VALUE"));
				} else if ("J041".equals(map.get("KEY"))) {
					item.addAttribute("J041", map.get("VALUE"));
				}
			}
		}
	}
}