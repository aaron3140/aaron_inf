package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf13001Request extends CommonReqAbs {

	public DpInf13001Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		super.setCustCode(getNodeTextM(doc, "CUSTCODE"));
		super.setStaffCode(getNodeTextM(doc, "STAFFCODE"));
	}

}
