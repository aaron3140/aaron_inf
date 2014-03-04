package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class INF02040Request extends CommonReqAbs {

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		staffCode = getNodeTextM(doc, "STAFFCODE");

	}

	private String staffId;

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}


	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public INF02040Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}



}
