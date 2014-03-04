package common.xml.dp;

import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf13002Response extends CommonRespAbs {

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
	
	private static final String CUST_TYPE = "custType";

	private static final String TOTAL_BALANCE_POSITION = "totalBalancePosition";

	private static final String TOTAL_COST_POSITION = "totalCostPosition";

	private static final String COMMISSION_POSITION = "commissionPosition";
	
	private static final String NET_EXPAND_POSITION = "netExpandPosition";
	

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
		resultDataSet.addElement(CUST_TYPE).addText(jsonObject.getString(CUST_TYPE));
		resultDataSet.addElement(TOTAL_BALANCE_POSITION).addText(jsonObject.getString(TOTAL_BALANCE_POSITION));
		resultDataSet.addElement(TOTAL_COST_POSITION).addText(jsonObject.getString(TOTAL_BALANCE_POSITION));
		resultDataSet.addElement(COMMISSION_POSITION).addText(jsonObject.getString(TOTAL_BALANCE_POSITION));
		resultDataSet.addElement(NET_EXPAND_POSITION).addText(jsonObject.getString(TOTAL_BALANCE_POSITION));
		
		return doc.asXML();
	}

}
