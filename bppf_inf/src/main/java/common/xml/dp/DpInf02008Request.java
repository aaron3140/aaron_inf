package common.xml.dp;

import org.dom4j.Document;

import common.utils.Charset;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02008Request extends CommonReqAbs {

	public DpInf02008Request(String xmlStr) throws Exception {

		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String verifyCode;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String password;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String operType;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String newPassword;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String afftPassword;

	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_NUM)
	private String passwordType;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

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

	public String getPassword() {
		return password;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getAfftPassword() {
		return afftPassword;
	}

	public void setAfftPassword(String afftPassword) {
		this.afftPassword = afftPassword;
	}

	public String getPasswordType() {
		return passwordType;
	}

	public void setPasswordType(String passwordType) {
		this.passwordType = passwordType;
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

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		custCode = getNodeTextM(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		operType = getNodeText(doc, "OPERTYPE");
		if(Charset.isEmpty(operType, true)){
			operType="00";
		}
		if("00".equals(operType)){
			password = getNodeTextM(doc, "PASSWORD");
			newPassword = getNodeTextM(doc, "NEWPASSWORD");
			afftPassword = getNodeTextM(doc, "AFFTPASSWORD");
			verifyCode = getNodeText(doc, "VERIFYCODE");
		}else{
			password = getNodeText(doc, "PASSWORD");
			newPassword = getNodeText(doc, "NEWPASSWORD");
			afftPassword = getNodeText(doc, "AFFTPASSWORD");
			verifyCode = getNodeTextM(doc, "VERIFYCODE");
		}

		passwordType = getNodeTextM(doc, "PASSWORDTYPE");

		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

}
