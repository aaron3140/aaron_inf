package common.xml.dp;

import org.dom4j.Document;

import common.utils.ParamValid;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 * 
 * 本类描述: 实时验证接口
 * @version: 企业帐户前置接口 v1.0 
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email:  zhuxiaojun@tisson.com
 * @time: 2013-3-4上午11:23:47
 */
public class DpInf01014Request extends CommonReqAbs {
	public DpInf01014Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String custCode;		
	
	@CheckerAnnotion(len = 5, type = CheckerAnnotion.TYPE_STR)
	private String busiType;
	
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private String areacode;
	
	@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)
	private String bankAcct;

	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String transAccName;
	
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private String bankCode;
	
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String openBank;
	
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String cardFlag;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String validity;
	
	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String cvn2;

	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String privateFlag;
	
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_NUM)
	private String certType;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String certNo;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_NUM)
	private String tel;	
	
//	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)//1
//	private String txnAmount;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String memo;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		custCode = getNodeTextM(doc,"CUSTCODE");
		busiType = getNodeTextM(doc, "BUSITYPE");
		areacode = getNodeText(doc, "AREACODE");								
		
		bankAcct = getNodeTextM(doc, "BANKACCT");
		transAccName = getNodeTextM(doc, "TRANSACCNAME");	
		bankCode = getNodeTextM(doc,"BANKCODE");
		if(bankCode!=null&&bankCode.length()<6){
			throw new Exception("请填写六位的银行编码");
		}
		openBank = getNodeText(doc,"OPENBANK");
		
		cardFlag = getNodeTextM(doc, "CARDFLAG");
		privateFlag = getNodeTextM(doc, "PRIVATEFLAG");
		
		if("2".equals(cardFlag)){
			cvn2 = getNodeTextM(doc,"CVN2");
			validity = getNodeTextM(doc,"VALIDITY");		
			
		}else{
			
			cvn2 = getNodeText(doc,"CVN2");
			validity = getNodeText(doc,"VALIDITY");		
		}
		
		
		certType = getNodeTextM(doc,"CERTTYPE");
		certNo = getNodeTextM(doc,"CERTNO");
		
		tel = getNodeText(doc,"TEL");		
		memo = getNodeText(doc, "MEMO");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getAreacode() {
		return areacode;
	}

	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}

	public String getBankAcct() {
		return bankAcct;
	}

	public void setBankAcct(String bankAcct) {
		this.bankAcct = bankAcct;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getCardFlag() {
		return cardFlag;
	}

	public void setCardFlag(String cardFlag) {
		this.cardFlag = cardFlag;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getCvn2() {
		return cvn2;
	}

	public void setCvn2(String cvn2) {
		this.cvn2 = cvn2;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getOpenBank() {
		return openBank;
	}

	public void setOpenBank(String openBank) {
		this.openBank = openBank;
	}

	public String getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
	}

	public String getPrivateFlag() {
		return privateFlag;
	}

	public void setPrivateFlag(String privateFlag) {
		this.privateFlag = privateFlag;
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

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getTransAccName() {
		return transAccName;
	}

	public void setTransAccName(String transAccName) {
		this.transAccName = transAccName;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	

}
