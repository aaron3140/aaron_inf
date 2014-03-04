package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.ValueUtil;
import common.xml.CommonRespAbs;

public class DpInf05003Response extends CommonRespAbs {
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	
	private static final String TAG_NAME_14 = "TRANSSEQ";
	
	private static final String TAG_NAME_15= "ORDERSEQ";	
	
	private static final String TAG_NAME_16 = "TXNAMOUNT";
	
	private static final String TAG_NAME_17= "TRADEAMOUNT";	
	
	private static final String TAG_NAME_18= "HANDLEAMOUNT";	
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE ,
			String RESPONSECONTENT,String TRANSSEQ,String ORDERSEQ,String TXNAMOUNT,
			String TRADEAMOUNT,String HANDLEAMOUNT) {
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		
		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		root.addElement(TAG_NAME_14).addText(ValueUtil.s(TRANSSEQ));
		root.addElement(TAG_NAME_15).addText(ValueUtil.s(ORDERSEQ));
		root.addElement(TAG_NAME_16).addText(TXNAMOUNT);
		root.addElement(TAG_NAME_17).addText(TRADEAMOUNT);
		root.addElement(TAG_NAME_18).addText(HANDLEAMOUNT);
		return doc.asXML();
	}
	
}
