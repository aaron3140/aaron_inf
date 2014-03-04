package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.ValueUtil;
import common.xml.CommonRespAbs;

public class DpInf01012Response extends CommonRespAbs {

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
	
	private static final String TAG_NAME_14_4_1 = "TOKENCODE";
	
	private static final String TAG_NAME_14_4_2 = "CUSTCODE";
	
	private static final String TAG_NAME_14_4_3 = "PRTNCODE";
	
	private static final String TAG_NAME_14_4_4 = "PRIVURL";
	
	private static final String TAG_NAME_14_4_5 = "ACCTSTAT";
	
	private static final String TAG_NAME_14_4_6 = "REGTYPE";
	
	private static final String TAG_NAME_14_4_7 = "PRODUCTS";
	
	private static final String TAG_NAME_14_4_8 = "BANKMODE";
	
	private static final String TAG_NAME_14_4_9 = "REGCHANAL";
	
	private static final String TAG_NAME_14_4_10 = "BINDCARD";
	private static final String TAG_NAME_14_4_11 = "REMARK1";
	
	private static final String TAG_NAME_14_4_12 = "REMARK2";
	
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE ,
			String RESPONSECONTENT,String TOKENCODE,String CUSTCODE,String PRTNCODE, String PRIVURL,String ACCTSTAT,String REGTYPE,String PRODUCTS,String bankMode,String regChanal, String bindCard,String REMARK1,String REMARK2) {
		
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
		datas.addElement(TAG_NAME_14_4_1).addText(ValueUtil.s(TOKENCODE));
		datas.addElement(TAG_NAME_14_4_2).addText(ValueUtil.s(CUSTCODE));
		datas.addElement(TAG_NAME_14_4_3).addText(ValueUtil.s(PRTNCODE));
		datas.addElement(TAG_NAME_14_4_4).addText(ValueUtil.s(PRIVURL));
		
		datas.addElement(TAG_NAME_14_4_5).addText(ValueUtil.s(ACCTSTAT));
		datas.addElement(TAG_NAME_14_4_6).addText(ValueUtil.s(REGTYPE));
		datas.addElement(TAG_NAME_14_4_7).addText(ValueUtil.s(PRODUCTS));
		datas.addElement(TAG_NAME_14_4_8).addText(ValueUtil.s(bankMode));
		datas.addElement(TAG_NAME_14_4_9).addText(ValueUtil.s(regChanal));
		datas.addElement(TAG_NAME_14_4_10).addText(ValueUtil.s(bindCard));
		datas.addElement(TAG_NAME_14_4_11).addText(ValueUtil.s(REMARK1));
		datas.addElement(TAG_NAME_14_4_12).addText(ValueUtil.s(REMARK2));
		
		return doc.asXML();
	}
}
