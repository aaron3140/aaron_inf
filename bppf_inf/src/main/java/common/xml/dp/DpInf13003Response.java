package common.xml.dp;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf13003Response extends CommonRespAbs {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String RESULTDATESET = "RESULTDATESET";
	//------------------------------------------------------------

	private static final String CUST_CODE = "custCode";

	private static final String MY_CUSTOMER_INFO = "myCustomerInfo";

	private static final String RANK_LIST = "rankList";

	private static final String POSITION = "position";
	private static final String BALANCE = "balance";
	
	private static final String CUSTOMER_NAME = "customerName";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, 
			String RESPONSECODE, String RESPONSECONTENT,String custCode,JSONObject jsonObject) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		root.addElement(TAG_NAME_12).addText(RESPONSECODE);
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		
		Element resultDataSet = root.addElement(RESULTDATESET);
		resultDataSet.addElement(CUST_CODE).addText(custCode);
		if(RESPONSECODE.equalsIgnoreCase("100")){
			JSONObject cusObj = jsonObject.getJSONObject(MY_CUSTOMER_INFO);
			Element cusInfo = resultDataSet.addElement(MY_CUSTOMER_INFO);
			cusInfo.addElement(POSITION).addText(cusObj.getString(POSITION));
			cusInfo.addElement(CUSTOMER_NAME).addText(cusObj.getString(CUSTOMER_NAME));
			cusInfo.addElement(BALANCE).addText(cusObj.getString(BALANCE));
			
			
			JSONArray rankListArray = jsonObject.getJSONArray(RANK_LIST);		
			Element dailyDataSet = resultDataSet.addElement(RANK_LIST);		
			Iterator<JSONObject> iter = rankListArray.iterator();
			while(iter.hasNext()){
				JSONObject obj = iter.next();
				Element e = dailyDataSet.addElement("data");
				e.addElement(POSITION).addText(obj.getString(POSITION));
				e.addElement(CUSTOMER_NAME).addText(obj.getString(CUSTOMER_NAME));
				e.addElement(BALANCE).addText(obj.getString(BALANCE));
			}
		}

		return doc.asXML();
	}

}
