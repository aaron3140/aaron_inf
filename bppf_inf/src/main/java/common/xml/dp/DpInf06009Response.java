package common.xml.dp;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf06009Response extends CommonRespAbs{

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
	
	private static final String TAG_NAME_14_4_1 = "ORDERITEM";
	
	private static final String[] ORDER_ATTR = { "AGENTCODE", "AGENTNAME", "AGENTTYPE" };

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT, List<Map<String,String>> list) {

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);  
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);

		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);

		if (list != null && list.size() > 0) {

			Element dataSet = root.addElement(TAG_NAME_14);

			Element datas = dataSet.addElement(TAG_NAME_14_1);
			
			for (int i = 0; i < list.size(); i++) {
				
				Element item = datas.addElement(TAG_NAME_14_4_1);
				
				Map<String, String> map = list.get(i);

				for (String attr : ORDER_ATTR) {
					item.addAttribute(attr, map.get(attr));
				}
			}
		}

		return doc.asXML();
	}
}
