package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.utils.MathTool;
import common.xml.CommonRespAbs;

public class DpInf06103Response extends CommonRespAbs {
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "TMNNUMNO";
//	private static final String TAG_NAME_15 = "OUTTMNNUMNO";
	private static final String TAG_NAME_16 = "ORDERSEQ";
	private static final String TAG_NAME_17 = "ACCTCODE";
	private static final String TAG_NAME_18 = "TRANSSEQ";
	private static final String TAG_NAME_19 = "SYSTEMNO";
	private static final String TAG_NAME_20 = "TXNAMOUNT";
	private static final String TAG_NAME_21 = "AMOUNT";
	private static final String TAG_NAME_22 = "REMARK1";
	private static final String TAG_NAME_23 = "REMARK2";
	private static final String TAG_NAME_24 = "RESULTDATESET";

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT, String TMNNUMNO,
			String ORDERSEQ, String ACCTCODE, String TRANSSEQ, String SYSTEMNO, String TXNAMOUNT, String AMOUNT, String REMARK1, String REMARK2) {
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
//		dataSet.addElement(TAG_NAME_15).addText(OUTTMNNUMNO);
		dataSet.addElement(TAG_NAME_16).addText(ORDERSEQ);
		dataSet.addElement(TAG_NAME_17).addText(ACCTCODE);
		dataSet.addElement(TAG_NAME_18).addText(TRANSSEQ);
		dataSet.addElement(TAG_NAME_19).addText(SYSTEMNO);
		//元转分
		dataSet.addElement(TAG_NAME_20).addText(MathTool.yuanToPoint(TXNAMOUNT));
		dataSet.addElement(TAG_NAME_21).addText(AMOUNT);
		dataSet.addElement(TAG_NAME_22).addText(REMARK1);
		dataSet.addElement(TAG_NAME_23).addText(REMARK2);
		return doc.asXML();
	}

}
