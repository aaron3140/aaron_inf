package common.xml.dp;

import org.dom4j.Document;

import common.utils.Charset;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02029Request extends CommonReqAbs {

	public DpInf02029Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String regVerifyCode;
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String password;
	@CheckerAnnotion(len = 18, type = CheckerAnnotion.TYPE_STR)
	private String contactNo;
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String certType;
	@CheckerAnnotion(len = 18, type = CheckerAnnotion.TYPE_STR)
	private String cerNo;
	@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)
	private String bankAcct;
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String transAccnNme;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		password = getNodeText(doc, "PASSWORD");
		custCode = getNodeText(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		regVerifyCode = getNodeText(doc, "REGVERIFYCODE");
		contactNo = getNodeText(doc, "CONTACTNO");
		certType = getNodeText(doc, "CERTTYPE");
		cerNo = getNodeText(doc, "CERNO");
		bankAcct = getNodeText(doc, "BANKACCT");
		transAccnNme = getNodeText(doc, "TRANSACCNAME");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getContactNo() {
		return contactNo;
	}

	public String getRegVerifyCode() {
		return regVerifyCode;
	}

	public void setRegVerifyCode(String regVerifyCode) {
		this.regVerifyCode = regVerifyCode;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCerNo() {
		return cerNo;
	}

	public void setCerNo(String cerNo) {
		this.cerNo = cerNo;
	}

	public String getBankAcct() {
		return bankAcct;
	}

	public void setBankAcct(String bankAcct) {
		this.bankAcct = bankAcct;
	}

	public String getTransAccnNme() {
		return transAccnNme;
	}

	public void setTransAccnNme(String transAccnNme) {
		this.transAccnNme = transAccnNme;
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
