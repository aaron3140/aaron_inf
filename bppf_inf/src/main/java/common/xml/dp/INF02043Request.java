package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class INF02043Request extends CommonReqAbs {

	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String issueId;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String issueType;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		staffCode = getNodeTextM(doc, "STAFFCODE");
		issueId = getNodeTextM(doc, "ISSUEID");
		issueType = getNodeTextM(doc, "ISSUETYPE");

	}
	
	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}



	private String staffId;

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	
	public String getIssueId() {
		return issueId;
	}

	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public INF02043Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}



}
