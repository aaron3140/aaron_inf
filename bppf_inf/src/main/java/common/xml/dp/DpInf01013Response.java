package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

/**
 * 
 * 本类描述: 实时解签接口
 * @version: 企业帐户前置接口 v1.0 
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email:  zhuxiaojun@tisson.com
 * @time: 2013-3-4上午11:25:14
 */
public class DpInf01013Response extends CommonRespAbs {
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
		
	
	private static final String TAG_NAME_14 = "ORDERSEQ";
	
	private static final String TAG_NAME_15 = "CUSTCODE";
	
	private static final String TAG_NAME_16 = "CONTRACTID";
	
	private static final String TAG_NAME_17 = "EXTERNALID";
	
	private static final String TAG_NAME_18 = "BANKACCT";
	
	private static final String TAG_NAME_19 = "TRANSSEQ";
	
	private static final String TAG_NAME_20 = "REMARK1";
	
	private static final String TAG_NAME_21 = "REMARK2";
	
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE ,
			String RESPONSECONTENT,String ORDERSEQ,String CUSTCODE,String CONTRACTID,String EXTERNALID
			,String BANKACCT,String TRANSSEQ,String REMARK1,String REMARK2) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		
		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		root.addElement(TAG_NAME_14).addText(ORDERSEQ);
		root.addElement(TAG_NAME_15).addText(CUSTCODE);
		root.addElement(TAG_NAME_16).addText(CONTRACTID);
		root.addElement(TAG_NAME_17).addText(EXTERNALID);
		root.addElement(TAG_NAME_18).addText(BANKACCT);
		root.addElement(TAG_NAME_19).addText(TRANSSEQ);
		root.addElement(TAG_NAME_20).addText(REMARK1);
		root.addElement(TAG_NAME_21).addText(REMARK2);
		return doc.asXML();
	}
}
