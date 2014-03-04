package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf04001Response extends CommonRespAbs{
	
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_15 = "MERID";
	
	private static final String TAG_NAME_16 = "TNMNUM";
	
	private static final String TAG_NAME_17 = "CHANNELCODE";
	
	private static final String TAG_NAME_18 = "ORDERSEQ";
	
	private static final String TAG_NAME_19 = "CUSTCODE";
	
	private static final String TAG_NAME_20 = "STAFFID";
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT,
			String MERID,  String TNMNUM, String CHANNELCODE,String ORDERSEQ ,String CUSTCODE ,String STAFFID) {
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
		dataSet.addElement(TAG_NAME_15).addText(MERID);
		dataSet.addElement(TAG_NAME_16).addText(TNMNUM);
		dataSet.addElement(TAG_NAME_17).addText(CHANNELCODE);
		dataSet.addElement(TAG_NAME_18).addText(ORDERSEQ);
		dataSet.addElement(TAG_NAME_19).addText(CUSTCODE);
		dataSet.addElement(TAG_NAME_20).addText(STAFFID);
		return doc.asXML();
	}
	

}
