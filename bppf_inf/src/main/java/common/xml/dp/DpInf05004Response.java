package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf05004Response extends CommonRespAbs {
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "DATAS";
	
	private static final String TAG_NAME_14_0 = "PAYBILLITERM";
	
	private static final String TAG_NAME_14_1 = "TRANSSEQ";
	
	private static final String TAG_NAME_14_2 = "PREORDERID";
	
	private static final String TAG_NAME_14_3 = "CUSTCODE";
	
	private static final String TAG_NAME_14_4 = "OBJCODE";
	
	private static final String TAG_NAME_14_5 = "AMOUNT";
	
	private static final String TAG_NAME_14_6 = "REQDATA";
	
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, 
			String RESPONSECODE, String RESPONSECONTENT,String TRANSSEQ,String PREORDERID,String CUSTCODE,
			String OBJCODE,String AMOUNT,String REQDATA){
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		
		root.addElement(TAG_NAME_12).addText(RESPONSECODE);
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		Element datas=root.addElement(TAG_NAME_14);
		
		Element iterm=datas.addElement(TAG_NAME_14_0);
		
		iterm.addAttribute(TAG_NAME_14_1, TRANSSEQ);
		iterm.addAttribute(TAG_NAME_14_2, PREORDERID );
		iterm.addAttribute(TAG_NAME_14_3, CUSTCODE);
		iterm.addAttribute(TAG_NAME_14_4, OBJCODE);
		iterm.addAttribute(TAG_NAME_14_5, AMOUNT);
		iterm.addAttribute(TAG_NAME_14_6, REQDATA);
		
		return doc.asXML();
	}
	
	
}
