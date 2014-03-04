package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf12035Response extends CommonRespAbs{

	private static final String TAG_NAME1 = "PayPlatResponseParameter";
	
	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_14_1 = "SEARCHID";
	
	private static final String TAG_NAME_14_2 = "TRAINNUM";
	
	private static final String TAG_NAME_14_4 = "DATAS";
	
	private static final String TAG_NAME_14_4_1 = "TRAINDETAIL";
	
	private static final String TAG_NAME_15 = "REMARK1";
	
	private static final String TAG_NAME_16 = "REMARK2";
	
	//private String [] details ={"K001","K002","K003","K004","G001","G002","G003","G004","G005","G006","G007","G008","G009"};
	
	public String toXMLStr(String REQWEBSVRCODE, 
			String RESPONSETYPE, 
			String KEEP,
			String RESULT, 
			String RESPONSECODE, 
			String RESPONSECONTENT, 
			String SEARCHID, 
			String TRAINNUM, 
			List<List<Map<String, String>>> list,
			String REMARK1,
			String REMARK2){
		
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
		
		dataSet.addElement(TAG_NAME_14_1).addText(SEARCHID);
		dataSet.addElement(TAG_NAME_14_2).addText(TRAINNUM);
		
		Element detail = dataSet.addElement(TAG_NAME_14_4);
		
		detailDate(detail, list);
		
		dataSet.addElement(TAG_NAME_15).addText(REMARK1);
		
		dataSet.addElement(TAG_NAME_16).addText(REMARK2);
		
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
				} else if ("G002".equals(map.get("KEY"))) {
					item.addAttribute("G002", map.get("VALUE"));
				} else if ("G003".equals(map.get("KEY"))) {
					item.addAttribute("G003", map.get("VALUE"));
				} else if ("G004".equals(map.get("KEY"))) {
					item.addAttribute("G004", map.get("VALUE"));
				} else if ("G005".equals(map.get("KEY"))) {
					item.addAttribute("G005", map.get("VALUE"));
				} else if ("G006".equals(map.get("KEY"))) {
					item.addAttribute("G006", map.get("VALUE"));
				} else if ("G007".equals(map.get("KEY"))) {
					item.addAttribute("G007", map.get("VALUE"));
				} else if ("G008".equals(map.get("KEY"))) {
					item.addAttribute("G008", map.get("VALUE"));
				} else if ("G009".equals(map.get("KEY"))) {
					item.addAttribute("G009", map.get("VALUE"));
				}else if ("G010".equals(map.get("KEY"))) {
					item.addAttribute("G010", map.get("VALUE"));
				} 
			}
		}
	}
	
}
