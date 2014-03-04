package common.xml.dp;

import org.dom4j.Document;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf04002Request extends CommonReqAbs {
	
	public DpInf04002Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_STR)
	private String passwordType;
	@CheckerAnnotion(len = 108, type = CheckerAnnotion.TYPE_STR)
	private String password;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String staffId;

	public void setParameters(Document doc, Object reserved) throws Exception {
		orderNo = getNodeTextM(doc, "ORDERNO");
		custCode = getNodeTextM(doc, "CUSTCODE");
		passwordType = getNodeTextM(doc, "PASSWORDTPYE");
		password = getNodeTextM(doc, "PASSWORD");
		staffId = getNodeTextM(doc, "STAFFID");
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	
	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}
	

	public String getPasswordType() {
		return passwordType;
	}

	public void setPasswordType(String passwordType) {
		this.passwordType = passwordType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

}
