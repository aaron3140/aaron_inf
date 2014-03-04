package common.xml.dp;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class INF02042Request extends CommonReqAbs {

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;

	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String operType;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String issueType;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String issueChannel;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String issueScope;

	@CheckerAnnotion( type = CheckerAnnotion.TYPE_STR)
	private String issueDateStart;
	
	@CheckerAnnotion( type = CheckerAnnotion.TYPE_STR)
	private String issueDateEnd;
	
//	@CheckerAnnotion( type = CheckerAnnotion.TYPE_DATE)
//	private Date issueDateEnd;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	private String staffId;

	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private Long start;

	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private Long page;

	public INF02042Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	public INF02042Request() {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		custCode = getNodeTextM(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		issueType = getNodeTextM(doc, "ISSUETYPE");
		issueChannel = getNodeText(doc, "ISSUECHANNEL");
		issueScope = getNodeText(doc, "ISSUESCOPE");
		issueDateStart = getNodeText(doc, "ISSUEDATESTART");
		issueDateEnd = getNodeText(doc, "ISSUEDATEEND");
//		issueDateEnd = getNodeText(doc, "ISSUEDATESTART");
		String startStr = getNodeText(doc, "start");
		
		if (StringUtils.isEmpty(startStr))
			start = 1l;
		else
			start = Long.valueOf(startStr);
		String pageStr = getNodeText(doc, "page");
		if (StringUtils.isEmpty(pageStr))
			page = 5l;
		else
		page = Long.valueOf(pageStr);

	}
	

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}


	public Long getPage() {
		return page;
	}

	public void setPage(Long page) {
		this.page = page;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public String getCustCode() {
		return custCode;
	}

	public String getIssueChannel() {
		return issueChannel;
	}

	public String getIssueScope() {
		return issueScope;
	}

	public String getIssueType() {
		return issueType;
	}

	public String getOperType() {
		return operType;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public void setIssueChannel(String issueChannel) {
		this.issueChannel = issueChannel;
	}

	public void setIssueScope(String issueScope) {
		this.issueScope = issueScope;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}


	public String getIssueDateStart() {
		return issueDateStart;
	}


	public void setIssueDateStart(String issueDateStart) {
		this.issueDateStart = issueDateStart;
	}


	public String getIssueDateEnd() {
		return issueDateEnd;
	}


	public void setIssueDateEnd(String issueDateEnd) {
		this.issueDateEnd = issueDateEnd;
	}

//	public Date getIssueDateStart() {
//		return issueDateStart;
//	}
//
//	public void setIssueDateStart(Date issueDateStart) {
//		this.issueDateStart = issueDateStart;
//	}
//
//	public Date getIssueDateEnd() {
//		return issueDateEnd;
//	}
//
//	public void setIssueDateEnd(Date issueDateEnd) {
//		this.issueDateEnd = issueDateEnd;
//	}

	
	
}
