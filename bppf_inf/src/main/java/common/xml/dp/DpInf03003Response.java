package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.ValueUtil;
import common.xml.CommonRespAbs;

public class DpInf03003Response extends CommonRespAbs {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	
	
	private static final String TAG_NAME_14= "RESULTDATESET";
	
	private static final String TAG_NAME_14_4 = "DATAS";	
	
	private static final String TAG_NAME_14_4_1 = "PREORDERSEQ";
	private static final String TAG_NAME_14_4_2 = "TXNAMOUNT";
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE ,
			String RESPONSECONTENT,String PREORDERSEQ, String TXNAMOUNT) {
		
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
		Element datas = dataSet.addElement(TAG_NAME_14_4); 
		datas.addElement(TAG_NAME_14_4_1).addText(PREORDERSEQ);
		datas.addElement(TAG_NAME_14_4_2).addText(TXNAMOUNT);
		
		return doc.asXML();
	}
}
