package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.MathTool;
import common.xml.CommonRespAbs;

public class DpInf06104Response extends CommonRespAbs {
	
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "TMNNUMNO";

	private static final String TAG_NAME_16 = "ORDERSEQ";
	private static final String TAG_NAME_17 = "TRANSSEQ";
	private static final String TAG_NAME_18 = "SYSTEMNO";
	private static final String TAG_NAME_19 = "TXNAMOUNT";
	private static final String TAG_NAME_20 = "CASHTYPE";
//	private static final String TAG_NAME_21 = "CASHORDER";
	private static final String TAG_NAME_22 = "REMARK1";
	private static final String TAG_NAME_23 = "REMARK2";
	private static final String TAG_NAME_24 = "RESULTDATESET";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT, String TMNNUMNO,
			String ORDERSEQ, String TRANSSEQ, String SYSTEMNO, String TXNAMOUNT, String CASHTYPE, String REMARK1, String REMARK2) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);

		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		Element dataSet = root.addElement(TAG_NAME_24);
		dataSet.addElement(TAG_NAME_14).addText(TMNNUMNO);

		dataSet.addElement(TAG_NAME_16).addText(ORDERSEQ);
		dataSet.addElement(TAG_NAME_17).addText(TRANSSEQ);
		dataSet.addElement(TAG_NAME_18).addText(SYSTEMNO);
		//元转分
		dataSet.addElement(TAG_NAME_19).addText(MathTool.yuanToPoint(TXNAMOUNT));
		dataSet.addElement(TAG_NAME_20).addText(CASHTYPE);
//		dataSet.addElement(TAG_NAME_21).addText(CASHORDER);
		dataSet.addElement(TAG_NAME_22).addText(REMARK1);
		dataSet.addElement(TAG_NAME_23).addText(REMARK2);
		return doc.asXML();
	}

}
