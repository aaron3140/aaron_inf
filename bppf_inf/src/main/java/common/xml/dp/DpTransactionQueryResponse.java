package common.xml.dp;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.entity.Order;
import common.xml.CommonRespAbs;

public class DpTransactionQueryResponse extends CommonRespAbs {
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_14_1= "STARTDATE";	
	
	private static final String TAG_NAME_14_2 = "ENDDATE";
	
	private static final String TAG_NAME_14_3 = "TOTALNUMS";	
	
	private static final String TAG_NAME_14_4 = "DATAS";	
	
	private static final String TAG_NAME_14_4_1 = "ORDERITEM";
	
	private static final String TAG_NAME_14_4_1_ATTR_1 = "ORDERSEQ";
	
	private static final String TAG_NAME_14_4_1_ATTR_2 = "TRANSSEQ";
	
	private static final String TAG_NAME_14_4_1_ATTR_3 = "ACTIONTYPE";
	
	private static final String TAG_NAME_14_4_1_ATTR_4 = "ACTIONNAME";
	
	private static final String TAG_NAME_14_4_1_ATTR_5 = "ORDERTIME";
	
	private static final String TAG_NAME_14_4_1_ATTR_6 = "MEMO";
	
	private static final String TAG_NAME_14_4_1_ATTR_7 = "ORDERSTAT";
	
	private static final String TAG_NAME_14_4_1_ATTR_8 = "ORDERAMOUNT";
	
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE ,
			String RESPONSECONTENT,String STARTDATE,String ENDDATE,String TOTALNUMS,List<Order> orderList) {
		
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
		dataSet.addElement(TAG_NAME_14_1).addText(STARTDATE);
		dataSet.addElement(TAG_NAME_14_2).addText(ENDDATE);
		dataSet.addElement(TAG_NAME_14_3).addText(TOTALNUMS);
		
		Element datas = dataSet.addElement(TAG_NAME_14_4);
		for (int i = 0; i < orderList.size(); i++) {
			Element item = datas.addElement(TAG_NAME_14_4_1);
			item.addAttribute(TAG_NAME_14_4_1_ATTR_1, orderList.get(i).getOrderSeq());
			item.addAttribute(TAG_NAME_14_4_1_ATTR_2, orderList.get(i).getTransSeq());
			item.addAttribute(TAG_NAME_14_4_1_ATTR_3, orderList.get(i).getActionType());
			item.addAttribute(TAG_NAME_14_4_1_ATTR_4, orderList.get(i).getActionName());
			item.addAttribute(TAG_NAME_14_4_1_ATTR_5, orderList.get(i).getOrderTime());
			item.addAttribute(TAG_NAME_14_4_1_ATTR_6, orderList.get(i).getMemo());
			item.addAttribute(TAG_NAME_14_4_1_ATTR_7, orderList.get(i).getOrderStat());
			item.addAttribute(TAG_NAME_14_4_1_ATTR_8, orderList.get(i).getOrderAmount());
		}
		return doc.asXML();
	}
}
