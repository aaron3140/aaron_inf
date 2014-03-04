package common.xml.dp;

import org.dom4j.Document;

import common.utils.ParamValid;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf01011Request extends CommonReqAbs {

	public DpInf01011Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}
	
	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)//1
	private String orderSeq;
	
//	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)//1
//	private String prtnCode;
		
//	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)//1
//	private String externalId;
	
	@CheckerAnnotion(len = 54, type = CheckerAnnotion.TYPE_STR)//1
	private String custCode;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_NUM)//1
	private String province;

	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)//1
	private String areaCode;
	
	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_NUM)
	private String project;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)//1
	private String branchProp;
	
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)//1
	private String branchName;    

	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_NUM)//1
	private String branchCode;

	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)//1
	private String prtnType;
	
	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_NUM)//1
	private String veriType;	

	@CheckerAnnotion(len = 5, type = CheckerAnnotion.TYPE_STR)//1
	private String busiType;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_NUM)//1
	private String certCode;	
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)//1
	private String certNo;

	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)//1
	private String accName;

	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)//1
	private String bankArea;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)//1
	private String bankInfo;
	
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)//1
	private String bankCode;
	
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_NUM)//1
	private String bankAcct;
	
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)//1
	private String cardFlag;

	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)//1
	private String privateFlag;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_NUM)//1
	private String contactPhone;
	
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)//1
	private String contactAddr;
	
	@CheckerAnnotion(len = 100, type = CheckerAnnotion.TYPE_STR)//1
	private String recvcorp;
	
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)//1
	private String creditValidTime;
	
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_NUM)//1
	private String creditValidCode;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)//1
	private String memo;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)//1
	private String remark;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)//1
	private String remark2;
	
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		ParamValid paramValid = new ParamValid();
		String resultDesc=paramValid.Valid(doc,"INF01011");
		if (!resultDesc.equals("0")) {
			throw new Exception (resultDesc);
		}
		
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
//		externalId = getNodeTextM(doc,"EXTERNALID");
		busiType = getNodeTextM(doc, "BUSITYPE");
		certCode = getNodeTextM(doc, "CERTCODE");
		certNo = getNodeTextM(doc, "CERTNO");
		accName = getNodeTextM(doc, "ACCNAME");
		bankArea = getNodeTextM(doc, "BANKAREA");
		bankInfo = getNodeTextM(doc, "BANKINFO");
		bankCode =getNodeTextM(doc,"BANKCODE");
		if(bankCode!=null&&bankCode.length()<6){
			throw new Exception("请填写六位的银行编码");
		}
		bankAcct = getNodeTextM(doc, "BANKACCT");
		cardFlag = getNodeTextM(doc, "CARDFLAG");
		privateFlag = getNodeTextM(doc, "PRIVATEFLAG");
		contactPhone = getNodeTextM(doc, "CONTACTPHONE");
		contactAddr = getNodeTextM(doc, "CONTACTADDR");
		creditValidTime = getNodeText(doc, "CREDITVALIDTIME");
		creditValidCode = getNodeText(doc, "CREDITVALIDCODE");
//		prtnCode = getNodeTextM(doc, "PRTNCODE");
		custCode = getNodeTextM(doc, "CUSTCODE");

		
		province = getNodeTextM(doc, "PROVINCE");
		areaCode = getNodeTextM(doc,"AREACODE");
		branchProp = getNodeTextM(doc, "BRANCHPROP");
		project = getNodeTextM(doc, "PROJECT");
		
		branchCode=getNodeText(doc,"BRANCHCODE");
		branchName = getNodeText(doc, "BRANCHNAME");
		
		recvcorp = getNodeTextM(doc, "RECVCORP");
		prtnType = getNodeTextM(doc, "PRTNTYPE");
		veriType = getNodeTextM(doc, "VERITYPE");
		memo = getNodeText(doc, "MEMO");
		remark = getNodeText(doc, "REMARK");
		remark2 = getNodeText(doc, "REMARK2");
		
	}

	
	public String getBankArea() {
		return bankArea;
	}


	public void setBankArea(String bankArea) {
		this.bankArea = bankArea;
	}


//	public String getExternalId() {
//		return externalId;
//	}
//
//	public void setExternalId(String externalId) {
//		this.externalId = externalId;
//	}

	public String getBankCode() {
		return bankCode;
	}


	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}


	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	
	public String getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getCertCode() {
		return certCode;
	}

	public void setCertCode(String certCode) {
		this.certCode = certCode;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getBankInfo() {
		return bankInfo;
	}

	public void setBankInfo(String bankInfo) {
		this.bankInfo = bankInfo;
	}

	public String getBankAcct() {
		return bankAcct;
	}

	public void setBankAcct(String bankAcct) {
		this.bankAcct = bankAcct;
	}

	public String getCardFlag() {
		return cardFlag;
	}

	public void setCardFlag(String cardFlag) {
		this.cardFlag = cardFlag;
	}

	public String getPrivateFlag() {
		return privateFlag;
	}

	public void setPrivateFlag(String privateFlag) {
		this.privateFlag = privateFlag;
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

	public String getCreditValidTime() {
		return creditValidTime;
	}

	public void setCreditValidTime(String creditValidTime) {
		this.creditValidTime = creditValidTime;
	}

	public String getCreditValidCode() {
		return creditValidCode;
	}

	public void setCreditValidCode(String creditValidCode) {
		this.creditValidCode = creditValidCode;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPrtnType() {
		return prtnType;
	}

	public void setPrtnType(String prtnType) {
		this.prtnType = prtnType;
	}

	public String getVeriType() {
		return veriType;
	}

	public void setVeriType(String veriType) {
		this.veriType = veriType;
	}

	public String getBranchProp() {
		return branchProp;
	}

	public void setBranchProp(String branchProp) {
		this.branchProp = branchProp;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getRecvcorp() {
		return recvcorp;
	}

	public void setRecvcorp(String recvcorp) {
		this.recvcorp = recvcorp;
	}


	public String getBranchCode() {
		return branchCode;
	}


	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	
}
