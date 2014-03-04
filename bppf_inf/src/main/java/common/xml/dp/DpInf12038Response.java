package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.MathTool;
import common.xml.CommonRespAbs;

public class DpInf12038Response extends CommonRespAbs {

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

	private static final String TAG_NAME_14_2 = "ORDERSEQ";

	private static final String TAG_NAME_14_3 = "TRANSSEQ";
	
	private static final String TAG_NAME_14_7 = "PRICEALL";
	
	private static final String TAG_NAME_14_8 = "CHARGEALL";
	
	private static final String TAG_NAME_14_9 = "TICKETCODE";

	private static final String TAG_NAME_14_4 = "DATAS";
	
	private static final String TAG_NAME_14_5 = "REMARK1";
	
	private static final String TAG_NAME_14_6 = "REMARK2";


	private static final String TAG_NAME_14_4_1 = "TicketDetail";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, 
			String RESPONSECODE, String RESPONSECONTENT, String SYSTEMNO, String ORDERSEQ, String TRANSSEQ,String PRICEALL,String CHARGEALL,String TICKETCODE,List<List<Map<String, String>>> list,String REMARK1,String REMARK2) {

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
		dataSet.addElement(TAG_NAME_14_2).addText(ORDERSEQ);
		dataSet.addElement(TAG_NAME_14_3).addText(TRANSSEQ);
		
		dataSet.addElement(TAG_NAME_14_7).addText(PRICEALL);
		dataSet.addElement(TAG_NAME_14_8).addText(CHARGEALL);
		dataSet.addElement(TAG_NAME_14_9).addText(TICKETCODE);
		Element datas = dataSet.addElement(TAG_NAME_14_4);
		detailDate(datas, list);
		
		dataSet.addElement(TAG_NAME_14_5).addText(REMARK1);
		dataSet.addElement(TAG_NAME_14_6).addText(REMARK2);
		return doc.asXML();
	}

	private void detailDate(Element datas, List<List<Map<String, String>>> list) {

		for (int i = 0; i < list.size(); i++) {
			List<Map<String, String>> data = list.get(i);
			Element item = datas.addElement(TAG_NAME_14_4_1);
			for (int j = 0; j < data.size(); j++) {

				Map<String, String> map = data.get(j);
				if ("G001".equals(map.get("KEY"))) {
					item.addAttribute("G001", map.get("VALUE"));
				}else if ("G002".equals(map.get("KEY"))) {
					item.addAttribute("G002", map.get("VALUE"));
				}else if ("G003".equals(map.get("KEY"))) {
					item.addAttribute("G003", map.get("VALUE"));
				}else if ("G004".equals(map.get("KEY"))) {
					String m = MathTool.yuanToPoint(map.get("VALUE"));
					item.addAttribute("G004", m);
				}else if ("G005".equals(map.get("KEY"))) {
					String u = MathTool.yuanToPoint(map.get("VALUE"));
					item.addAttribute("G005", u);
				}
			}
		}

	}

}
