package common.xml.dp;

import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.entity.Account;
import common.utils.Charset;
import common.xml.CommonRespAbs;

public class DpCardAccountInfoResponset extends CommonRespAbs{
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_14_11="DATAS";
	
	private static final String TAG_NAME_14_11_1="ACCOUNTITEM";
	
	private static final String TAG_NAME_14_11_1_ATTR_1 = "ACCTTYPE";
	
	private static final String TAG_NAME_14_11_1_ATTR_2 = "ACCTSTAT";
	
	private static final String TAG_NAME_14_11_1_ATTR_3 = "BALANCE";
	
	private static final String TAG_NAME_14_11_1_ATTR_4 = "ACTIVEBALANCE";
	
	private static final String TAG_NAME_14_11_1_ATTR_5 = "FROZENBALANCE";
	
	private static String [] dataKeys={"AGENTCODE","AGENTNAME","ADDR","RANGE","ORGCODE","BUSSINESSLIC",
			"LEGALREPRESENTATIVE","CONTACTER","CONTACTPHONE","CONTACTADDR","EMAIL","REGCERT",
			"FINANCIALCONTACTER","FINANCIALPHONE","FINANCIALEMAIL","TIME","REGDATE", "HASTYB"};
	
	
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE,String KEEP, String RESULT, 
			String RESPONSECODE, String RESPONSECONTENT,Hashtable<String,String> dataMap ,List<Account> accountItem) {
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
		
		for (String key:dataKeys) {
			String value = dataMap.get(key);
			if (value==null) {
				value="";
			}
			dataSet.addElement(key).addText(value);
		}
		
		
		Element datas = dataSet.addElement(TAG_NAME_14_11);
		for(int i=0;i<accountItem.size();i++){
			Element item = datas.addElement(TAG_NAME_14_11_1);
			item.addAttribute(TAG_NAME_14_11_1_ATTR_1, accountItem.get(i).getAcctType());
			item.addAttribute(TAG_NAME_14_11_1_ATTR_2, accountItem.get(i).getAcctStat());
			item.addAttribute(TAG_NAME_14_11_1_ATTR_3, accountItem.get(i).getBalance());
			item.addAttribute(TAG_NAME_14_11_1_ATTR_4, accountItem.get(i).getActiveBalance());
			item.addAttribute(TAG_NAME_14_11_1_ATTR_5, accountItem.get(i).getFrozenBalance());
		}
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
