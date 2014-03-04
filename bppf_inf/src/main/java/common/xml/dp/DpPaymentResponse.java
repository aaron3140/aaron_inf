package common.xml.dp;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import common.xml.CommonRespAbs;

public class DpPaymentResponse extends CommonRespAbs {
	
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_141= "ORDERID";
	
	private static final String TAG_NAME_142 = "CARDINFOS";
	
	private static final String TAG_NAME_142_ATTR_1 = "CARDNO";
	
	private static final String TAG_NAME_142_ATTR_2 = "BALANCE";
	
	private static final String TAG_NAME_142_ATTR_3 = "TXNAMOUNT";
	
	
	public String toXMLStr(String RESULT, String KEEP, String REQWEBSVRCODE, String resCode, String reason, String orderId, List<String[]> objs) {
		//responseCode补充到6位
		String newResCode = newCode(resCode);
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, "10");
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		
		root.addElement(TAG_NAME_12).addText(newResCode);
		root.addElement(TAG_NAME_13).addText(reason);
		
		Element dataSet = root.addElement(TAG_NAME_14);
		dataSet.addElement(TAG_NAME_141).addText(orderId);
		
		for (String[] str : objs) {
			Element cardInfo = dataSet.addElement(TAG_NAME_142);
			cardInfo.addAttribute(TAG_NAME_142_ATTR_1, str[0]);
			cardInfo.addAttribute(TAG_NAME_142_ATTR_2, str[1]);
			cardInfo.addAttribute(TAG_NAME_142_ATTR_3, str[2]);
		}
		
		return doc.asXML();
	}
}
