package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbsLogin;

public class DpInf03002Request extends CommonReqAbsLogin {

	public DpInf03002Request(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(String verifyType) {
		this.verifyType = verifyType;
	}

	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_STR)
	private String verifyType;
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		staffCode = getNodeTextM(doc, "STAFFCODE");
		verifyType = getNodeTextM(doc, "VERIFYTYPE");

	}

}
