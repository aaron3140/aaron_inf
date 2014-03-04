package common.xml.dp;

import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.entity.Account;
import common.utils.Charset;
import common.xml.CommonRespAbs;

public class DpBankAccountInfoResponset extends CommonRespAbs{
	
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_14_11="ACCTCODE";
	
	private static final String TAG_NAME_14_12="BANKNAME";
	
	private static final String TAG_NAME_14_13="BANKCODE";
	
	private static final String TAG_NAME_14_14 = "ACCTNAME";
	
	private static final String TAG_NAME_14_15 = "PRIVATEFLAG";
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,String KEEP, String RESULT, 
			String RESPONSECODE, String RESPONSECONTENT,String bankAcctNbr, String bankName,String bankCode, String bankAcctName, String privateflag) {
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
		
		dataSet.addElement(TAG_NAME_14_11).addText(bankAcctNbr);
		dataSet.addElement(TAG_NAME_14_12).addText(bankName);
		dataSet.addElement(TAG_NAME_14_13).addText(bankCode);
		dataSet.addElement(TAG_NAME_14_14).addText(bankAcctName);
		dataSet.addElement(TAG_NAME_14_15).addText(privateflag);
		return doc.asXML();
	}
	
	public String toXMLStr(String RESULT, String KEEP,String RESPONSECODE,String RESPONSECONTENT){

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, RESPONSECODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSECONTENT);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		return doc.asXML();
	}
	

}
