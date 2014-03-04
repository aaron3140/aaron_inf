package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.MathTool;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06200Request extends CommonReqAbs {

	public DpInf06200Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_STR)
	private String tmnNumNo;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String acceptDate;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String txnAmount;
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String busObject;

	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String psamCardNo;
	// ====

	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String bankAcct;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String cardFlag;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String privateFlag;

	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String payPassword;
	@CheckerAnnotion(len = 79, type = CheckerAnnotion.TYPE_STR)
	private String trackTwo;
	@CheckerAnnotion(len = 108, type = CheckerAnnotion.TYPE_STR)
	private String trackThree;
	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_STR)
	private String networkNo;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		custCode = getNodeTextM(doc, "CUSTCODE");
		tmnNumNo = getNodeTextM(doc, "TMNNUMNO");
		acceptDate = getNodeTextM(doc, "ACCEPTDATE");
		txnAmount = getNodeTextM(doc, "TXNAMOUNT");
		busObject = getNodeTextM(doc, "BUSOBJECT");
		bankAcct = getNodeTextM(doc, "BANKACCT");
		privateFlag = getNodeTextM(doc, "PRIVATEFLAG");
		cardFlag = getNodeTextM(doc, "CARDFLAG");
		payPassword = getNodeTextM(doc, "PAYPASSWORD");
		psamCardNo = getNodeTextM(doc, "PSAMCARDNO");
		trackTwo = getNodeTextM(doc, "TRACKTWO");
		trackThree = getNodeText(doc, "TRACKTHREE");
		networkNo = getNodeTextM(doc, "NETWORKNO");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
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

	public String getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
	}

	public String getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}

	public String getBusObject() {
		return busObject;
	}

	public void setBusObject(String busObject) {
		this.busObject = busObject;
	}

	public String getPsamCardNo() {
		return psamCardNo;
	}

	public void setPsamCardNo(String psamCardNo) {
		this.psamCardNo = psamCardNo;
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

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getTrackTwo() {
		return trackTwo;
	}

	public void setTrackTwo(String trackTwo) {
		this.trackTwo = trackTwo;
	}

	public String getTrackThree() {
		return trackThree;
	}

	public void setTrackThree(String trackThree) {
		this.trackThree = trackThree;
	}

	public String getNetworkNo() {
		return networkNo;
	}

	public void setNetworkNo(String networkNo) {
		this.networkNo = networkNo;
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
