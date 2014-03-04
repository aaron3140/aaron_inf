package common.xml.dp;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import common.xml.CommonRespAbs;

public class DpInf06004Response extends CommonRespAbs {
	private static final String TAG_NAME1 = "PayPlatResponseParameter";

	private static final String TAG_NAME_11 = "RESPONSE-INFO";

	private static final String TAG_NAME_11_ATTR_1 = "REQWEBSVRCODE";

	private static final String TAG_NAME_11_ATTR_2 = "RESPONSETYPE";

	private static final String TAG_NAME_11_ATTR_3 = "KEEP";

	private static final String TAG_NAME_11_ATTR_4 = "RESULT";

	private static final String TAG_NAME_12 = "RESPONSECODE";

	private static final String TAG_NAME_13 = "RESPONSECONTENT";

	private static final String TAG_NAME_14 = "TMNNUMNO";
	private static final String TAG_NAME_15 = "OUTTMNNUMNO";
	private static final String TAG_NAME_16 = "ORDERSEQ";
	private static final String TAG_NAME_17 = "SYSTEMNO";
	private static final String TAG_NAME_18 = "REMARK1";
	private static final String TAG_NAME_19 = "REMARK2";
	private static final String TAG_NAME_20 = "RESULTDATESET";
	
	private static final String TAG_NAME_21 = "DATAS";
	
	private static final String TAG_NAME_21_1 ="FAREDETAIL";
	
//	private static final String TAG_NAME_21_1 = "FLAG";
//	
//	private static final String TAG_NAME_21_2 = "NAME";
//	
//	private static final String TAG_NAME_21_3 = "DATATYPE";
//	
//	private static final String TAG_NAME_21_4 = "CONTENT";
	
	private String [] details ={"FLAG","NAME","DATATYPE","CONTENT"};
	

	public String toXMLStr(String REQWEBSVRCODE, String RESPONSETYPE, String KEEP, String RESULT, String RESPONSECODE, String RESPONSECONTENT, String TMNNUMNO, 
			String ORDERSEQ, String SYSTEMNO, String REMARK1, String REMARK2, List<String> list) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(TAG_NAME1);
		Element resInfo = root.addElement(TAG_NAME_11);
		resInfo.addAttribute(TAG_NAME_11_ATTR_1, REQWEBSVRCODE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_2, RESPONSETYPE);
		resInfo.addAttribute(TAG_NAME_11_ATTR_3, KEEP);
		resInfo.addAttribute(TAG_NAME_11_ATTR_4, RESULT);

		root.addElement(TAG_NAME_12).addText(newCode(RESPONSECODE));
		root.addElement(TAG_NAME_13).addText(RESPONSECONTENT);
		Element dataSet = root.addElement(TAG_NAME_20);
		dataSet.addElement(TAG_NAME_14).addText(TMNNUMNO);
//		dataSet.addElement(TAG_NAME_15).addText(OUTTMNNUMNO);
		dataSet.addElement(TAG_NAME_16).addText(ORDERSEQ);
		dataSet.addElement(TAG_NAME_17).addText(SYSTEMNO);
		
		if(list.size()>0 && list != null){
			
			Element detail = dataSet.addElement(TAG_NAME_21);
			
			Element fareDetail = detail.addElement(TAG_NAME_21_1);
			
			for(int i=0; i<list.size(); i++){
				
				fareDetail.addAttribute(details[i], list.get(i).toString());
			}
			
		}
		dataSet.addElement(TAG_NAME_18).addText(REMARK1);
		dataSet.addElement(TAG_NAME_19).addText(REMARK2);
		return doc.asXML();
	}

}
