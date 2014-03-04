package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.MathTool;
import common.xml.CommonRespAbs;

public class DpInf06003Response extends CommonRespAbs {

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

	private static final String TAG_NAME_14_3 = "BILLSTAT";
	
	private static final String TAG_NAME_14_4 = "BILLNUM";

	private static final String TAG_NAME_14_5 = "DATAS";

	private static final String TAG_NAME_14_5_1 = "ORDERITEM";

//	private static final String[] ORDER_ATTR = { "CUSTOMERNAME", "BILLBARCODE",
//			"BILLNO", "BILLMONTH", "BILLBATCH", "CONTRACTNO", "BILLAMOUNT",
//			"BILLDELAY", "BALANCE", "BILLDATE", "LASTPAYDATE",
//			"BILLSTATUS", "PASSWORD", "THIRDCODE", "REMARK1",
//			"REMARK2", "REMARK3", "REMARK4","REMARK5","REMARK6","REMARK7" };

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,
			String KEEP, String RESULT, String RESPONSECODE,
			String RESPONSECONTENT, String SYSTEMNO,String BILLSTAT, String TMNNUMNO,
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

		dataSet.addElement(TAG_NAME_14_3).addText(BILLSTAT);
		
		dataSet.addElement(TAG_NAME_14_4).addText(BILLNUM);


		Element datas = dataSet.addElement(TAG_NAME_14_5);
		
//		Element item = datas.addElement(TAG_NAME_14_5_1);
		
		detailDate(datas,list);
		
		return doc.asXML();
	}
	
	private void detailDate(Element datas,List<List<Map<String, String>>> list){
		
		
		for (int i = 0; i < list.size(); i++) {
			List<Map<String, String>> data = list.get(i);
			Element item = datas.addElement(TAG_NAME_14_5_1);
			for (int j = 0; j < data.size(); j++) {
				
				Map<String, String> map = data.get(j);
				if("R001".equals(map.get("KEY"))){
					
					item.addAttribute("CUSTOMERNAME", map.get("VALUE"));
					
				}else if("R002".equals(map.get("KEY"))){
					
					item.addAttribute("BILLBARCODE", map.get("VALUE"));
				}else if("R003".equals(map.get("KEY"))){
					
					item.addAttribute("BILLNO", map.get("VALUE"));
				}else if("R004".equals(map.get("KEY"))){
					
					item.addAttribute("BILLMONTH", map.get("VALUE"));
				}else if("R005".equals(map.get("KEY"))){
					
					item.addAttribute("BILLBATCH", map.get("VALUE"));
				}else if("R006".equals(map.get("KEY"))){
					
					item.addAttribute("CONTRACTNO", map.get("VALUE"));
				}else if("R007".equals(map.get("KEY"))){
					
					item.addAttribute("BILLAMOUNT", MathTool.yuanToPoint(map.get("VALUE")));
				}else if("R008".equals(map.get("KEY"))){
					
					item.addAttribute("BILLDELAY", MathTool.yuanToPoint(map.get("VALUE")));
				}else if("R009".equals(map.get("KEY"))){
					
					item.addAttribute("BALANCE", MathTool.yuanToPoint(map.get("VALUE")));
				}else if("R010".equals(map.get("KEY"))){
					
					item.addAttribute("BILLDATE", map.get("VALUE"));
				}else if("R011".equals(map.get("KEY"))){
					
					item.addAttribute("LASTPAYDATE", map.get("VALUE"));
				}else if("R012".equals(map.get("KEY"))){
					
					item.addAttribute("BILLSTATUS", map.get("VALUE"));
				}else if("R013".equals(map.get("KEY"))){
					
					item.addAttribute("PASSWORD", map.get("VALUE"));
				}else if("R014".equals(map.get("KEY"))){
					
					item.addAttribute("THIRDCODE", map.get("VALUE"));
				}else if("R015".equals(map.get("KEY"))){
					
					item.addAttribute("REMARK1", map.get("VALUE"));
				}else if("R016".equals(map.get("KEY"))){
					
					item.addAttribute("REMARK2", map.get("VALUE"));
				}else if("R017".equals(map.get("KEY"))){
					
					item.addAttribute("REMARK3", map.get("VALUE"));
				}else if("R018".equals(map.get("KEY"))){
					
					item.addAttribute("REMARK4", map.get("VALUE"));
				}else if("R019".equals(map.get("KEY"))){
					
					item.addAttribute("REMARK5", map.get("VALUE"));
				}else if("R020".equals(map.get("KEY"))){
					
					item.addAttribute("REMARK6", map.get("VALUE"));
				}else if("R021".equals(map.get("KEY"))){
					
					item.addAttribute("REMARK7", map.get("VALUE"));
				}
				
				
			}
		}
		
	}
}
