package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.utils.Charset;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06101Request extends CommonReqAbs {

	public DpInf06101Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_STR)
	private String tmnNumNo;
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String psamCardNo;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String acceptDate;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String txnAmount;
	@CheckerAnnotion(len = 19, type = CheckerAnnotion.TYPE_STR)
	private String targetAccount;

	// ====

	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String accName;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String bankAcct;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String cardFlag;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String privateFlag;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String payType;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String creditValidTime;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String creditValidCode;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String certType;
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String bankInfo;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String bankArea;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String bankCode;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String certNo;

	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String contactPhone;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String contactAddr;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String operUser;
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String operPassword;

	// ====
	@CheckerAnnotion(len = 19, type = CheckerAnnotion.TYPE_STR)
	private String payAccount;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String payPassword;
	@CheckerAnnotion(len = 79, type = CheckerAnnotion.TYPE_STR)
	private String trackTwo;
	@CheckerAnnotion(len = 108, type = CheckerAnnotion.TYPE_STR)
	private String trackThree;
	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_STR)
	private String networkNo;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String outCustSign;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		tmnNumNo = getNodeTextM(doc, "TMNNUMNO");
		acceptDate = getNodeTextM(doc, "ACCEPTDATE");
		txnAmount = getNodeTextM(doc, "TXNAMOUNT");
		targetAccount = getNodeTextM(doc, "TARGETACCOUNT");
		trackThree = getNodeText(doc, "TRACKTHREE");
		contactAddr = getNodeText(doc, "CONTACTADDR");
		operUser = getNodeText(doc, "OPERUSER");
		operPassword = getNodeText(doc, "OPERPASSWORD");
		payPassword = getNodeTextM(doc, "PAYPASSWORD");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
		creditValidTime = getNodeText(doc, "CREDITVALIDTIME");
		creditValidCode = getNodeText(doc, "CREDITVALIDCODE");
		certType = getNodeText(doc, "CERTTYPE");
		certNo = getNodeText(doc, "CERTNO");
		bankInfo = getNodeText(doc, "BANKINFO");
		bankArea = getNodeText(doc, "BANKAREA");
		bankCode = getNodeText(doc, "BANKCODE");

		outCustSign = getNodeTextM(doc, "OUTCUSTSIGN");
		bankAcct = getNodeText(doc, "BANKACCT");
		cardFlag = getNodeText(doc, "CARDFLAG");
		contactPhone = getNodeText(doc, "CONTACTPHONE");
		

		if (ChannelCode.IPOS_CHANELCODE.equals(this.channelCode)) {
			custCode = getNodeText(doc, "CUSTCODE");
		} else {
			custCode = getNodeTextM(doc, "CUSTCODE");
		}
		// 渠道20时为必送；其它渠道可不送
		if (ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)) {
			staffCode = getNodeTextM(doc, "STAFFCODE");
		} else {
			staffCode = getNodeText(doc, "STAFFCODE");
		}

		payType = getNodeText(doc, "PAYTYPE");// 支付方式
		// 不填默认为POS方式 1
		if (Charset.isEmpty(payType)) {
			payType = "1";
		}

		if ("1".equals(payType)) {// POS

			payAccount = getNodeTextM(doc, "PAYACCOUNT");
			psamCardNo = getNodeTextM(doc, "PSAMCARDNO");
			trackTwo = getNodeTextM(doc, "TRACKTWO");
			networkNo = getNodeTextM(doc, "NETWORKNO");
		} else {
			payAccount = getNodeText(doc, "PAYACCOUNT");
			psamCardNo = getNodeText(doc, "PSAMCARDNO");
			trackTwo = getNodeText(doc, "TRACKTWO");
			networkNo = getNodeText(doc, "NETWORKNO");
		}

		if ("2".equals(payType)) {// 代付
			accName = getNodeTextM(doc, "ACCNAME");
			privateFlag = getNodeTextM(doc, "PRIVATEFLAG");
		} else {
			accName = getNodeText(doc, "ACCNAME");
			privateFlag = getNodeText(doc, "PRIVATEFLAG");
		}

		/*
		 * if ("3".equals(payType)) {// 代收付 bankAcct = getNodeTextM(doc, "BANKACCT"); accName = getNodeTextM(doc, "ACCNAME"); privateFlag = getNodeTextM(doc, "PRIVATEFLAG");
		 * cardFlag = getNodeTextM(doc, "CARDFLAG"); if ("2".equals(cardFlag)) {// 卡折为信用卡时必填 creditValidTime = getNodeTextM(doc, "CREDITVALIDTIME"); creditValidCode =
		 * getNodeTextM(doc, "CREDITVALIDCODE"); } else { creditValidTime = getNodeText(doc, "CREDITVALIDTIME"); creditValidCode = getNodeText(doc, "CREDITVALIDCODE"); }
		 * contactPhone = getNodeTextM(doc, "CONTACTPHONE"); } else { bankAcct = getNodeText(doc, "BANKACCT"); accName = getNodeText(doc, "ACCNAME"); privateFlag = getNodeText(doc,
		 * "PRIVATEFLAG"); cardFlag = getNodeText(doc, "CARDFLAG"); contactPhone = getNodeText(doc, "CONTACTPHON"); }
		 */

	}

	public String getBankInfo() {
		return bankInfo;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public void setBankInfo(String bankInfo) {
		this.bankInfo = bankInfo;
	}

	public String getBankArea() {
		return bankArea;
	}

	public void setBankArea(String bankArea) {
		this.bankArea = bankArea;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
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

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
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

	public String getTargetAccount() {
		return targetAccount;
	}

	public void setTargetAccount(String targetAccount) {
		this.targetAccount = targetAccount;
	}

	public String getPayAccount() {
		return payAccount;
	}

	public void setPayAccount(String payAccount) {
		this.payAccount = payAccount;
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

	public String getTmnNumNo() {
		return tmnNumNo;
	}

	public void setTmnNumNo(String tmnNumNo) {
		this.tmnNumNo = tmnNumNo;
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

	public String getPsamCardNo() {
		return psamCardNo;
	}

	public void setPsamCardNo(String psamCardNo) {
		this.psamCardNo = psamCardNo;
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

	public String getOutCustSign() {
		return outCustSign;
	}

	public void setOutCustSign(String outCustSign) {
		this.outCustSign = outCustSign;
	}
	
	

}
