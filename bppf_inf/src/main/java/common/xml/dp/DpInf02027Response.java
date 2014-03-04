package common.xml.dp;

import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import common.xml.CommonRespAbs;

//
public class DpInf02027Response extends CommonRespAbs {

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

	private static final String TAG_NAME_14_2 = "CARDNUM";

	private static final String TAG_NAME_14_3 = "ADDRESS";

//	private static final String TAG_NAME_14_4 = "USERHAND";

	private static final String TAG_NAME_14_5 = "DATAS";

	private static final String TAG_NAME_14_6 = "TMNNUMNO";
	
	private static final String TAG_NAME_14_8 = "EXPENSETYPE";
	
	private static final String TAG_NAME_14_7 = "RECORDNUM";

	private static final String TAG_NAME_14_5_1 = "BillDetail";

	public String toXMLStr(DpInf02027Request dpRequest,String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, 
			String RESPONSECODE, String RESPONSECONTENT, String SYSTEMNO, String RECORDNUM, String CARDNUM,String ADDRESS,List<List<Map<String, String>>> list) {

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
		dataSet.addElement(TAG_NAME_14_8).addText(dpRequest.getQueryType());
		dataSet.addElement(TAG_NAME_14_6).addText(dpRequest.getTmnNumNo());
		
		dataSet.addElement(TAG_NAME_14_7).addText(RECORDNUM);
		
		if(CARDNUM!=null){
			dataSet.addElement(TAG_NAME_14_2).addText(CARDNUM);
		}
		if(ADDRESS!=null){
			dataSet.addElement(TAG_NAME_14_3).addText(ADDRESS);
		}
//		if(USERHAND!=null){
//			dataSet.addElement(TAG_NAME_14_4).addText(USERHAND);
//		}
		
		Element datas = dataSet.addElement(TAG_NAME_14_5);
		detailDate(datas, list);
		return doc.asXML();
	}

	private void detailDate(Element datas, List<List<Map<String, String>>> list) {

		for (int i = 0; i < list.size(); i++) {
			List<Map<String, String>> data = list.get(i);
			Element item = datas.addElement(TAG_NAME_14_5_1);
			for (int j = 0; j < data.size(); j++) {

				Map<String, String> map = data.get(j);
				if ("E004".equals(map.get("KEY"))) {
					item.addAttribute("BILLFLAG", map.get("VALUE"));
				} else if ("E005".equals(map.get("KEY"))) {
					item.addAttribute("SHOWNO", map.get("VALUE"));
				} else if ("E006".equals(map.get("KEY"))) {
					item.addAttribute("CARDNO", map.get("VALUE"));
				} else if ("E007".equals(map.get("KEY"))) {
					item.addAttribute("USERNAME", map.get("VALUE"));
				} else if ("6804".equals(map.get("KEY"))) {
					item.addAttribute("PAYAMOUNT", map.get("VALUE"));
				}else if ("E027".equals(map.get("KEY"))) {
					item.addAttribute("SAVEAMOUNT", map.get("VALUE"));
				}else if ("E009".equals(map.get("KEY"))) {
					item.addAttribute("USERNO", map.get("VALUE"));
				}else if ("E002".equals(map.get("KEY"))) {
					item.addAttribute("ADDR", map.get("VALUE"));
				}else if ("E011".equals(map.get("KEY"))) {
					item.addAttribute("ID", map.get("VALUE"));
				}else if ("E012".equals(map.get("KEY"))) {
					item.addAttribute("NAME", map.get("VALUE"));
				}else if ("E013".equals(map.get("KEY"))) {
					item.addAttribute("MOUNT", map.get("VALUE"));
				}
			}
		}

	}

}
