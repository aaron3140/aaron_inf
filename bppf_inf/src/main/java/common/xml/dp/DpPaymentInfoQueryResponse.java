package common.xml.dp;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import common.xml.CommonRespAbs;

public class DpPaymentInfoQueryResponse extends CommonRespAbs {
	
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";
	
	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";
	
	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";
	
	private static final String TAG_NAME_11_ATTR_3 = "KEEP";
	
	private static final String TAG_NAME_11_ATTR_4 = "RESULT";
	
	private static final String TAG_NAME_12 = "RESPONSECODE";
	
	private static final String TAG_NAME_13 = "RESPONSECONTENT";
	
	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_141 = "DATAS";
	
	private static final String TAG_NAME_141_ATTR_1 = "MERCHANTNAME";
	
	private static final String TAG_NAME_141_ATTR_2 = "GOODSNAME";
	
	private static final String TAG_NAME_141_ATTR_3 = "TXNCHANNEL";
	
	private static final String TAG_NAME_141_ATTR_4 = "TXNTYPE";
	
	private static final String TAG_NAME_141_ATTR_5 = "ORDERID";
	
	private static final String TAG_NAME_141_ATTR_6 = "TRADESEQ";
	
	private static final String TAG_NAME_141_ATTR_7 = "TRADETIME";
	
	private static final String TAG_NAME_141_ATTR_8 = "TXNAMOUNT";
	
	private static final String TAG_NAME_141_ATTR_9 = "PAYSTAT";

	
	public String toXMLStr(String RESULT, String KEEP, String REQWEBSVRCODE,String RESPONSECODE ,
			String RESPONSECONTENT, List<String> merchantNameList, List<String> goodsNameList,
			List<String> txnChannelList, List<String> txnTypeList, List<String> orderIdList, 
			List<String> tradeSeqList, List<String> tradeTimeList, List<String> txnAmountList, 
			List<String> payStatList) {
		String newResCode = newCode(RESPONSECODE);
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, "20");
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);
		
		root.addElement(TAG_NAME_12).addText(newResCode);
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		
		Element dataSet = root.addElement(TAG_NAME_14);
		for(int i = 0; i < orderIdList.size(); i++){
		Element paymentInfo = dataSet.addElement(TAG_NAME_141);
		paymentInfo.addAttribute(TAG_NAME_141_ATTR_1, merchantNameList.get(i));
		paymentInfo.addAttribute(TAG_NAME_141_ATTR_2, goodsNameList.get(i));
		paymentInfo.addAttribute(TAG_NAME_141_ATTR_3, txnChannelList.get(i));
		paymentInfo.addAttribute(TAG_NAME_141_ATTR_4, txnTypeList.get(i));
		paymentInfo.addAttribute(TAG_NAME_141_ATTR_5, orderIdList.get(i));
		paymentInfo.addAttribute(TAG_NAME_141_ATTR_6, tradeSeqList.get(i));
		paymentInfo.addAttribute(TAG_NAME_141_ATTR_7, tradeTimeList.get(i));
		paymentInfo.addAttribute(TAG_NAME_141_ATTR_8, txnAmountList.get(i));
		paymentInfo.addAttribute(TAG_NAME_141_ATTR_9, payStatList.get(i));
		}
		
		return doc.asXML();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
}
