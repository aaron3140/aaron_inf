package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.MathTool;
import common.xml.CommonRespAbs;

public class DpInf06006Response extends CommonRespAbs {

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

	private static final String TAG_NAME_14_2 = "TMNNUMNO";

	private static final String TAG_NAME_14_4 = "BILLNUM";

	private static final String TAG_NAME_14_5 = "DATAS";

	private static final String TAG_NAME_14_5_1 = "ORDERITEM";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,
			String KEEP, String RESULT, String RESPONSECODE,
			String RESPONSECONTENT, String SYSTEMNO, String TMNNUMNO,
			String BILLNUM, List<List<Map<String, String>>> list) {

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

		dataSet.addElement(TAG_NAME_14_2).addText(TMNNUMNO);

		dataSet.addElement(TAG_NAME_14_4).addText(BILLNUM);

		Element datas = dataSet.addElement(TAG_NAME_14_5);

		detailDate(datas, list);

		return doc.asXML();
	}

	private void detailDate(Element datas, List<List<Map<String, String>>> list) {

		String CUSTOMERNAME = "";
		String BILLBARCODE = "";
		String BILLNO = "";
		for (int j = 0; j < list.get(0).size(); j++) {
			Map<String, String> wMap = list.get(0).get(j);
			if ("W003".equals(wMap.get("KEY"))) {
				
//					item.addAttribute("CUSTOMERNAME", wMap.get("VALUE"));
				CUSTOMERNAME = wMap.get("VALUE");
				
			} else if ("W002".equals(wMap.get("KEY"))) {
				
//					item.addAttribute("BILLBARCODE", wMap.get("VALUE"));
				BILLBARCODE = wMap.get("VALUE");
			} else if ("W004".equals(wMap.get("KEY"))) {
				
//					item.addAttribute("BILLNO", wMap.get("VALUE"));
				BILLNO = wMap.get("VALUE");
			} 
		}
		for (int i = 1; i < list.size(); i++) {
			List<Map<String, String>> data = list.get(i);
			Element item = datas.addElement(TAG_NAME_14_5_1);
			
			item.addAttribute("CUSTOMERNAME", CUSTOMERNAME);
			item.addAttribute("BILLBARCODE", BILLBARCODE);
			item.addAttribute("BILLNO", MathTool.yuanToPoint(BILLNO));
			for (int j = 0; j < data.size(); j++) {
				Map<String, String> map = data.get(j);
				if ("D001".equals(map.get("KEY"))) {

					item.addAttribute("BILLMONTH", map.get("VALUE"));
				} else if ("R005".equals(map.get("KEY"))) {

					item.addAttribute("BILLBATCH", map.get("VALUE"));
				} else if ("D002".equals(map.get("KEY"))) {

					item.addAttribute("CONTRACTNO", MathTool.yuanToPoint(map.get("VALUE")));
				} else if ("D003".equals(map.get("KEY"))) {

					item.addAttribute("BILLDELAY", MathTool.yuanToPoint(map
							.get("VALUE")));
				} else if ("D004".equals(map.get("KEY"))) {

					item.addAttribute("BALANCE", MathTool.yuanToPoint(map
							.get("VALUE")));
				}  else if ("D005".equals(map.get("KEY"))) {

					item.addAttribute("LASTPAYDATE", map.get("VALUE"));
				}

			}
		}

	}

}
