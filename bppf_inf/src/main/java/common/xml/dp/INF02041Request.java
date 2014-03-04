package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class INF02041Request extends CommonReqAbs {

	public INF02041Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	public INF02041Request() {
		// TODO Auto-generated constructor stub
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;
	
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String operType;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String issueType;
	
	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String issueId;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;


	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		custCode = getNodeTextM(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		issueType = getNodeTextM(doc, "ISSUETYPE");
		operType = getNodeTextM(doc, "OPERTYPE");
		issueId = getNodeTextM(doc, "ISSUEID");
			

	}
	
	private String staffId;

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getIssueId() {
		return issueId;
	}

	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}


}
