package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf05003Request extends CommonReqAbs {

	public DpInf05003Request(String xmlStr) throws Exception {
		super(xmlStr,null);	
	}
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String transCode;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String recvCode;
	
	@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)
	private String revaccNo;
	
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String revaccName;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String addr;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String phone;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_NUM)
	private String certId;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String certCode;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String bankBelong;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String bankId;
	
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String bankSubId;
	
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String bankCardId;
	
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String bankCardType;
	
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String txnAmount;
	
	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String tradeTime;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark;
	
	public void init(Document doc, Object reserved) throws Exception{
		
	}
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub
		transCode = getNodeTextM(doc, "TRANSCODE");
		recvCode = getNodeText(doc, "RECVCODE");
		revaccNo = getNodeTextM(doc, "REVACCNO");
		revaccName = getNodeTextM(doc, "REVACCNAME");
		addr = getNodeText(doc, "ADDR");
		phone = getNodeText(doc, "PHONE");
		certId = getNodeTextM(doc, "CERTID");
		certCode = getNodeTextM(doc, "CERTCODE");
		bankBelong = getNodeTextM(doc, "BANKBELONG");
		bankId = getNodeTextM(doc, "BANKID");
		bankSubId=getNodeText(doc,"BANKSUBID");
		bankCardId = getNodeTextM(doc, "BANKCARDID");
		bankCardType = getNodeTextM(doc, "BANKCARDTYPE");
		txnAmount = getNodeTextM(doc, "TXNAMOUNT");
		int index = txnAmount.indexOf(".");
		if (index != -1) {
			throw new Exception("金额以分为单位,不能有小数点");
		}
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		tradeTime=getNodeTextM(doc,"TRADETIME");
		remark = getNodeText(doc, "REMARK");		
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getBankBelong() {
		return bankBelong;
	}

	public void setBankBelong(String bankBelong) {
		this.bankBelong = bankBelong;
	}

	public String getBankCardId() {
		return bankCardId;
	}

	public void setBankCardId(String bankCardId) {
		this.bankCardId = bankCardId;
	}

	public String getBankSubId() {
		return bankSubId;
	}

	public void setBankSubId(String bankSubId) {
		this.bankSubId = bankSubId;
	}

	public String getBankCardType() {
		return bankCardType;
	}

	public void setBankCardType(String bankCardType) {
		this.bankCardType = bankCardType;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getCertCode() {
		return certCode;
	}

	public void setCertCode(String certCode) {
		this.certCode = certCode;
	}

	public String getCertId() {
		return certId;
	}

	public void setCertId(String certId) {
		this.certId = certId;
	}

	public String getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRecvCode() {
		return recvCode;
	}

	public void setRecvCode(String recvCode) {
		this.recvCode = recvCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRevaccName() {
		return revaccName;
	}

	public void setRevaccName(String revaccName) {
		this.revaccName = revaccName;
	}

	public String getRevaccNo() {
		return revaccNo;
	}

	public void setRevaccNo(String revaccNo) {
		this.revaccNo = revaccNo;
	}

	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	public String getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}
	
 
}
