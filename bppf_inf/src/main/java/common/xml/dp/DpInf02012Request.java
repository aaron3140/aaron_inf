package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02012Request extends CommonReqAbs {

	public DpInf02012Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	@CheckerAnnotion(len = 100, type = CheckerAnnotion.TYPE_STR)
	private String payPassword;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String txnAmount;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		orderNo = getNodeTextM(doc, "ORDERNO");
		custCode = getNodeTextM(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		txnAmount = getNodeTextM(doc, "TXNAMOUNT");
		payPassword = getNodeTextM(doc, "PAYPASSWORD");
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

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
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
