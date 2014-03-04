package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02026Request extends CommonReqAbs {

	public DpInf02026Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String tmnNumNo;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	String userName;
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	String systemo;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	String txnamount;
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_NUM)
	String contactPhone;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	String contactAddr;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	String operUser;
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	String operPassword;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String payType;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		orderNo = getNodeTextM(doc, "ORDERNO");
		custCode = getNodeTextM(doc, "CUSTCODE");
		tmnNumNo = getNodeText(doc, "TMNNUMNO");
		payType = getNodeText(doc, "PAYTYPE");
		if (payType == null || payType.equals("") || payType.length() <= 0) {
			payType = "9";
		}
		userName = getNodeText(doc, "USERNAME");
		systemo = getNodeTextM(doc, "SYSTEMNO");
		txnamount = getNodeTextM(doc, "TXNAMOUNT");
		contactPhone = getNodeTextM(doc, "CONTACTPHONE");
		contactAddr = getNodeText(doc, "CONTACTADDR");
		operUser = getNodeText(doc, "OPERUSER");
		operPassword = getNodeText(doc, "OPERPASSWORD");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
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

	public String getTmnNumNo() {
		return tmnNumNo;
	}

	public void setTmnNumNo(String tmnNumNo) {
		this.tmnNumNo = tmnNumNo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSystemo() {
		return systemo;
	}

	public void setSystemo(String systemo) {
		this.systemo = systemo;
	}

	public String getTxnamount() {
		return txnamount;
	}

	public void setTxnamount(String txnamount) {
		this.txnamount = txnamount;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactAddr() {
		return contactAddr;
	}

	public void setContactAddr(String contactAddr) {
		this.contactAddr = contactAddr;
	}

	public String getOperUser() {
		return operUser;
	}

	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}

	public String getOperPassword() {
		return operPassword;
	}

	public void setOperPassword(String operPassword) {
		this.operPassword = operPassword;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

}
