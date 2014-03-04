package common.xml.dp;



import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;


/**
 *交易明细查询接口
 * 
 */


public class DpTransactionDetailQueryResponse extends CommonRespAbs {
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_14_1 = "DATAS";
	
	private static final String TAG_NAME_14_1_1 = "ORDERITEM";
	
	private static final String TAG_NAME_14_1_1_ATTR_1 = "TRANSSEQ";
	
	private static final String TAG_NAME_14_1_1_ATTR_2 = "ORDERTYPE";
	
	private static final String TAG_NAME_14_1_1_ATTR_3 = "CHANNELNAME";
	
	private static final String TAG_NAME_14_1_1_ATTR_4 = "CHANNELCODE";
	
	private static final String TAG_NAME_14_1_1_ATTR_5 = "ORDERTIME";
	
	private static final String TAG_NAME_14_1_1_ATTR_6 = "MEMO";
	
	private static final String TAG_NAME_14_1_1_ATTR_7 = "ORDERSTAT";
	
	private static final String TAG_NAME_14_1_1_ATTR_8 = "ORDERMOUNT";
	
	private static final String TAG_NAME_14_1_1_ATTR_9 = "PAYEECODE";
	
	private static final String TAG_NAME_14_1_1_ATTR_10 = "MARK1";
	
	private static final String TAG_NAME_14_1_1_ATTR_11 = "MARK2";
	
	
	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE ,
			String RESPONSECONTENT, String TRANSSEQ,String ORDERTYPE, String CHANNELNAME
			, String CHANNELCODE, String ORDERTIME, String MEMO, String ORDERSTAT,String ORDERMOUNT
			,String PAYEECODE,String PAYCODE,String ORDERCODE) {
		
		String newResCode = newCode(RESPONSECODE);
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		
		root.addElement(TAG_NAME_12).addText(newResCode);
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		
		Element dataSet = root.addElement(TAG_NAME_14);
		Element datas = dataSet.addElement(TAG_NAME_14_1);
		Element item = datas.addElement(TAG_NAME_14_1_1);
		item.addAttribute(TAG_NAME_14_1_1_ATTR_1, TRANSSEQ);
		item.addAttribute(TAG_NAME_14_1_1_ATTR_2, ORDERTYPE);
		item.addAttribute(TAG_NAME_14_1_1_ATTR_3, CHANNELNAME);
		item.addAttribute(TAG_NAME_14_1_1_ATTR_4, CHANNELCODE);
		item.addAttribute(TAG_NAME_14_1_1_ATTR_5, ORDERTIME);
		item.addAttribute(TAG_NAME_14_1_1_ATTR_6, MEMO);
		item.addAttribute(TAG_NAME_14_1_1_ATTR_7, ORDERSTAT);
		item.addAttribute(TAG_NAME_14_1_1_ATTR_8, ORDERMOUNT);
		item.addAttribute(TAG_NAME_14_1_1_ATTR_9, PAYEECODE);
		item.addAttribute("AGENTCODE", PAYCODE);
		item.addAttribute("ORDERSEQ", ORDERCODE);
		
		return doc.asXML();
	}
}
