package common.xml.dp;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf13001Response extends CommonRespAbs {

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

	private static final String DAILY_TOTAL = "dailyTotal";

	private static final String MONTHY_TOTAL = "monthyTotal";

	private static final String YEARLY_TOTAL = "yearlyTotal";

	
	private static final String DATA_LIST = "dataList";
	
	private static final String TOTAL_BALANCE = "totalBalance";
	
	private static final String TOTAL_COUNT = "totalCount";
	
	
	private static final String ACTION_NAME = "actionName";
	
	//private static final String PRD_LINE = "prdLine";
	
	//private static final String ACTION_ID = "actionId";
	private static final String BALANCE = "balance";

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
		
		
		JSONObject dailyObject = (JSONObject)jsonObject.get(DAILY_TOTAL);
		Element dailySet = resultDataSet.addElement(DAILY_TOTAL);
		dailySet.addElement(TOTAL_BALANCE).addText(dailyObject.getString(TOTAL_BALANCE));
		dailySet.addElement(TOTAL_COUNT).addText(dailyObject.getString(TOTAL_COUNT));
		JSONArray dailyArray = dailyObject.getJSONArray(DATA_LIST);
		
		Element dailyDataSet = dailySet.addElement(DATA_LIST);		
		Iterator<JSONObject> iter = dailyArray.iterator();
		while(iter.hasNext()){
			JSONObject obj = iter.next();
			Element e = dailyDataSet.addElement("data");
			e.addElement(ACTION_NAME).addText(obj.getString(ACTION_NAME));
			e.addElement(TOTAL_COUNT).addText(obj.getString(TOTAL_COUNT));
			//e.addElement(PRD_LINE).addText(obj.getString(PRD_LINE));
			//e.addElement(ACTION_ID).addText(obj.getString(ACTION_ID));
			e.addElement(BALANCE).addText(obj.getString(BALANCE));
		}
		
		JSONObject monthObject = (JSONObject)jsonObject.get(MONTHY_TOTAL);
		Element monthSet = resultDataSet.addElement(MONTHY_TOTAL);
		monthSet.addElement(TOTAL_BALANCE).addText(monthObject.getString(TOTAL_BALANCE));
		monthSet.addElement(TOTAL_COUNT).addText(monthObject.getString(TOTAL_COUNT));
		JSONArray monthArray = monthObject.getJSONArray(DATA_LIST);
		
		Element monthDataSet = monthSet.addElement(DATA_LIST);		
		Iterator<JSONObject> iterm = monthArray.iterator();
		while(iterm.hasNext()){
			JSONObject obj = iterm.next();
			Element e = monthDataSet.addElement("data");
			e.addElement(ACTION_NAME).addText(obj.getString(ACTION_NAME));
			//e.addElement(PRD_LINE).addText(obj.getString(PRD_LINE));
			//e.addElement(ACTION_ID).addText(obj.getString(ACTION_ID));
			e.addElement(TOTAL_COUNT).addText(obj.getString(TOTAL_COUNT));
			e.addElement(BALANCE).addText(obj.getString(BALANCE));
		}

		
		JSONObject yearlyObject = (JSONObject)jsonObject.get(YEARLY_TOTAL);
		Element yearSet = resultDataSet.addElement(YEARLY_TOTAL);
		yearSet.addElement(TOTAL_BALANCE).addText(dailyObject.getString(TOTAL_BALANCE));
		yearSet.addElement(TOTAL_COUNT).addText(dailyObject.getString(TOTAL_COUNT));
		JSONArray yearArray = yearlyObject.getJSONArray(DATA_LIST);
		
		Element yearDataSet = yearSet.addElement(DATA_LIST);		
		Iterator<JSONObject> itery = yearArray.iterator();
		while(itery.hasNext()){
			JSONObject obj = itery.next();
			Element e = yearDataSet.addElement("data");
			e.addElement(ACTION_NAME).addText(obj.getString(ACTION_NAME));
			//e.addElement(PRD_LINE).addText(obj.getString(PRD_LINE));
			//e.addElement(ACTION_ID).addText(obj.getString(ACTION_ID));
			e.addElement(TOTAL_COUNT).addText(obj.getString(TOTAL_COUNT));
			e.addElement(BALANCE).addText(obj.getString(BALANCE));
		}
		return doc.asXML();
	}

}
