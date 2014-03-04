package common.xml.dp;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.Charset;
import common.xml.CommonRespAbs;

public class DpInf02029Response extends CommonRespAbs {

	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "RESULTDATESET";
	
	private static final String TAG_NAME_14_1 = "CUSTCODE";
	private static final String TAG_NAME_14_2 = "STAFFCODE";
	private static final String TAG_NAME_14_3 = "DATAS";
	private static final String TAG_NAME_14_3_1 = "VERIFYITEM";
	private static final String TAG_NAME_14_4 = "REMARK1";
	private static final String TAG_NAME_14_5 = "REMARK2";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT, Map<String, String> map, String custCode, String satffCode,String remark1, String remark2) {

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
		if(!Charset.isEmpty(custCode, true)){
			dataSet.addElement(TAG_NAME_14_1).addText(custCode);
		}
		dataSet.addElement(TAG_NAME_14_2).addText(satffCode);
		Element datas = dataSet.addElement(TAG_NAME_14_3).addElement(TAG_NAME_14_3_1);
		    
		Set<Entry<String, String>> entrySet = map.entrySet();
		Iterator<Entry<String, String>> it = entrySet.iterator();  
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			datas.addAttribute(key, value);
		}
		dataSet.addElement(TAG_NAME_14_4).addText(remark1);
		dataSet.addElement(TAG_NAME_14_5).addText(remark2);
		return doc.asXML();
	}

}