package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.MathTool;
import common.xml.CommonRespAbs;

public class DpInf12037Response extends CommonRespAbs{
	
	private static final String TAG_NAME1 = "PayPlatResponseParameter";
	
	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_14_1 = "TRAINNUM";
	
	private static final String TAG_NAME_14_2 = "SYSTEMNO";
	
	private static final String TAG_NAME_14_3 = "DATAS";
	
	private static final String TAG_NAME_14_3_1 = "ORDERDETAIL";
	
	private static final String TAG_NAME_14_4 = "TICKETCODE";
	
	private static final String TAG_NAME_14_5 = "TRAINNO";
	
	private static final String TAG_NAME_14_6 = "STARTDATE";
	
	private static final String TAG_NAME_14_12 = "STARTTIME";
	
	private static final String TAG_NAME_14_7 = "STARTSTAND";
	
	private static final String TAG_NAME_14_8 = "ENDSTAND";
	
	private static final String TAG_NAME_14_9 = "STATUS";
	
	private static final String TAG_NAME_14_10 = "PRICEALL";
	
	private static final String TAG_NAME_14_11 = "CHARGEALL";
	
	private static final String TAG_NAME_15 = "REMARK1";
	
	private static final String TAG_NAME_16 = "REMARK2";
	
	
	public String toXMLStr(String REQWEBSVRCODE, 
			String RESPONSETYPE, 
			String KEEP,
			String RESULT, 
			String RESPONSECODE, 
			String RESPONSECONTENT, 
			String TRAINNUM,
			String SYSTEMNO, 
			String TICKETCODE,
			String TRAINNO,
			String STARTDATE,
			String STARTTIME,
			String STARTSTAND,
			String ENDSTAND,
			String STATUS,
			String PRICEALL,
			String CHARGEALL,
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
		
		dataSet.addElement(TAG_NAME_14_1).addText(TRAINNUM);
		
		dataSet.addElement(TAG_NAME_14_2).addText(SYSTEMNO);
		
		dataSet.addElement(TAG_NAME_14_4).addText(TICKETCODE);
		
		dataSet.addElement(TAG_NAME_14_5).addText(TRAINNO);
		
		dataSet.addElement(TAG_NAME_14_6).addText(STARTDATE);
		
		dataSet.addElement(TAG_NAME_14_12).addText(STARTTIME);
		
		dataSet.addElement(TAG_NAME_14_7).addText(STARTSTAND);
		
		dataSet.addElement(TAG_NAME_14_8).addText(ENDSTAND);
		
		dataSet.addElement(TAG_NAME_14_9).addText(STATUS);
		
		dataSet.addElement(TAG_NAME_14_10).addText(PRICEALL);
		
		dataSet.addElement(TAG_NAME_14_11).addText(CHARGEALL);
		
		Element detail = dataSet.addElement(TAG_NAME_14_3);
		
		detailDate(detail, list);

		dataSet.addElement(TAG_NAME_15).addText(REMARK1);
		
		dataSet.addElement(TAG_NAME_16).addText(REMARK2);
		
		return doc.asXML();
	}
	
	private void detailDate(Element datas, List<List<Map<String, String>>> list) {

		for (int i = 0; i < list.size(); i++) {
			List<Map<String, String>> data = list.get(i);
			Element item = datas.addElement(TAG_NAME_14_3_1);
			for (int j = 0; j < data.size(); j++) {
				Map<String, String> map = data.get(j);
				if ("G001".equals(map.get("KEY"))) {
					item.addAttribute("G001", map.get("VALUE"));
				} else if ("G002".equals(map.get("KEY"))) {
					item.addAttribute("G002", map.get("VALUE"));
				} else if ("G003".equals(map.get("KEY"))) {
					item.addAttribute("G003", map.get("VALUE"));
				} else if ("G004".equals(map.get("KEY"))) {
					String m = MathTool.yuanToPoint(map.get("VALUE"));
					item.addAttribute("G004", m);
				} else if ("G005".equals(map.get("KEY"))) {
					String u = MathTool.yuanToPoint(map.get("VALUE"));
					item.addAttribute("G005", u);
				}
			}
		}
	}
}
