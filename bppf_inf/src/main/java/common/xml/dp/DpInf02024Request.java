package common.xml.dp;

import org.dom4j.Document;

import common.utils.Charset;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02024Request extends CommonReqAbs {

	public DpInf02024Request(String xmlStr) throws Exception {

		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_STR)
	private String verifyType;
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String payPassword;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String bankCode;
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String openBank;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String areaCode;
	@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)
	private String bankAcct;
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String transAccName;
	@CheckerAnnotion(len = 18, type = CheckerAnnotion.TYPE_STR)
	private String cerNo;
	@CheckerAnnotion(len = 11, type = CheckerAnnotion.TYPE_STR)
	private String phone;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		orderNo = getNodeTextM(doc, "ORDERNO");
		custCode = getNodeTextM(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		
		verifyType = getNodeText(doc, "VERIFYTYPE");
		// 不填默认支付密码验证
		if (Charset.isEmpty(verifyType, true)) {
			verifyType = "002";
		}
//		if("002".equals(verifyType)){
//			payPassword = getNodeTextM(doc, "PAYPASSWORD");
//		}else{
//			payPassword = getNodeText(doc, "PAYPASSWORD");
//		}
		
		bankCode = getNodeTextM(doc, "BANKCODE");
		openBank = getNodeTextM(doc, "OPENBANK");
		areaCode = getNodeTextM(doc, "AREACODE");
		bankAcct = getNodeTextM(doc, "BANKACCT");
		transAccName = getNodeTextM(doc, "TRANSACCNAME");
		cerNo = getNodeTextM(doc, "CERNO");
		phone = getNodeText(doc, "PHONE");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");

	}

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(String verifyType) {
		this.verifyType = verifyType;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getOpenBank() {
		return openBank;
	}

	public void setOpenBank(String openBank) {
		this.openBank = openBank;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getBankAcct() {
		return bankAcct;
	}

	public void setBankAcct(String bankAcct) {
		this.bankAcct = bankAcct;
	}

	public String getTransAccName() {
		return transAccName;
	}

	public void setTransAccName(String transAccName) {
		this.transAccName = transAccName;
	}

	public String getCerNo() {
		return cerNo;
	}

	public void setCerNo(String cerNo) {
		this.cerNo = cerNo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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
