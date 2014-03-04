package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.RegisterReqAbs;

public class DpInf02022Request extends RegisterReqAbs {

	public DpInf02022Request(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 7, type = CheckerAnnotion.TYPE_STR)
	private String regType;

	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String custName;
	
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String products;
	
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String enterName;
	
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String enterAddress;
	
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private String areaCode;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_NUM)
	private String agentCode;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_NUM)
	private String trade;
	
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String businessLicence;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String applyer;
	
	@CheckerAnnotion(len = 18, type = CheckerAnnotion.TYPE_STR)
	private String certNo;
	
	@CheckerAnnotion(len = 18, type = CheckerAnnotion.TYPE_STR)
	private String reveNbr;

	public String getReveNbr() {
		return reveNbr;
	}

	public void setReveNbr(String reveNbr) {
		this.reveNbr = reveNbr;
	}

	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String email;
	
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String enterOrgCode;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String enterPerson;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String enterCerNo;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String regDate;
	
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_STR)
	private String verifyCode;
	
	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_NUM)
	private String verifyType;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		regType = getNodeTextM(doc, "REGTYPE");
		
		if("PRT1001".equals(regType)){
			
			certNo = getNodeTextM(doc, "CERTNO");
			enterOrgCode = getNodeText(doc, "ENTERORGCODE");
			enterPerson = getNodeText(doc, "ENTERPERSON");
			enterCerNo = getNodeText(doc, "ENTERCERNO");
			
			businessLicence = getNodeText(doc, "BUSINESSLICENCE");
		}else{
			
			certNo = getNodeText(doc, "CERTNO");
			enterOrgCode = getNodeTextM(doc, "ENTERORGCODE");
			enterPerson = getNodeTextM(doc, "ENTERPERSON");
			enterCerNo = getNodeTextM(doc, "ENTERCERNO");
			
			businessLicence = getNodeTextM(doc, "BUSINESSLICENCE");
		}
		
		reveNbr = getNodeText(doc, "REVENBR");
		areaCode = getNodeTextM(doc, "AREACODE");
		custName = getNodeTextM(doc, "CUSTNAME");
		products = getNodeTextM(doc, "PRODUCTS");
		enterName = getNodeTextM(doc, "ENTERNAME");
		enterAddress = getNodeTextM(doc, "ENTERADDRESS");
		agentCode = getNodeText(doc, "AGENTCODE");
		trade = getNodeTextM(doc, "TRADE");
		
		applyer = getNodeTextM(doc, "APPLYER");
		email = getNodeTextM(doc, "EMAIL");
		regDate = getNodeTextM(doc, "REGDATE");
		verifyCode = getNodeText(doc, "VERIFYCODE");
		
		verifyType = getNodeText(doc, "VERIFYTYPE");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");

	}
	
	public String getRegType() {
		return regType;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getProducts() {
		return products;
	}

	public void setProducts(String products) {
		this.products = products;
	}

	public String getEnterName() {
		return enterName;
	}

	public void setEnterName(String enterName) {
		this.enterName = enterName;
	}

	public String getEnterAddress() {
		return enterAddress;
	}

	public void setEnterAddress(String enterAddress) {
		this.enterAddress = enterAddress;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getTrade() {
		return trade;
	}

	public void setTrade(String trade) {
		this.trade = trade;
	}

	public String getBusinessLicence() {
		return businessLicence;
	}

	public void setBusinessLicence(String businessLicence) {
		this.businessLicence = businessLicence;
	}

	public String getApplyer() {
		return applyer;
	}

	public void setApplyer(String applyer) {
		this.applyer = applyer;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEnterOrgCode() {
		return enterOrgCode;
	}

	public void setEnterOrgCode(String enterOrgCode) {
		this.enterOrgCode = enterOrgCode;
	}

	public String getEnterPerson() {
		return enterPerson;
	}

	public void setEnterPerson(String enterPerson) {
		this.enterPerson = enterPerson;
	}

	public String getEnterCerNo() {
		return enterCerNo;
	}

	public void setEnterCerNo(String enterCerNo) {
		this.enterCerNo = enterCerNo;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(String verifyType) {
		this.verifyType = verifyType;
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
