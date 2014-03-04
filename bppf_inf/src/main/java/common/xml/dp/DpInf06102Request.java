package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.utils.Charset;
import common.utils.MathTool;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06102Request extends CommonReqAbs {

	public DpInf06102Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_STR)
	private String tmnNumNo;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String outTmnNumNo;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String acceptDate;
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_STR)
	private String systemNo;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String txnAmount;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String cashType;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String cashOrder;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String cashNumber;
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String accName;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String bankAcct;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String cardFlag;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String privateFlag;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String payType;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String creditValidTime;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String creditValidCode;
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String payPassword;
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String psamCardNo;
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String eCardNo;
	public String getECardNo() {
		return eCardNo;
	}

	public void setECardNo(String eCardNo) {
		this.eCardNo = eCardNo;
	}

	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String passFlag;
	public String getPassFlag() {
		return passFlag;
	}

	public void setPassFlag(String passFlag) {
		this.passFlag = passFlag;
	}

	@CheckerAnnotion(len = 79, type = CheckerAnnotion.TYPE_STR)
	private String trackTwo;
	@CheckerAnnotion(len = 108, type = CheckerAnnotion.TYPE_STR)
	private String trackThree;
	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_STR)
	private String networkNo;
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String contactPhone;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String contactAddr;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String operUser;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String operPassword;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		orderSeq = getNodeTextM(doc, "ORDERSEQ");

		if (ChannelCode.IPOS_CHANELCODE.equals(this.channelCode)) {

			custCode = getNodeText(doc, "CUSTCODE");
		} else {

			custCode = getNodeTextM(doc, "CUSTCODE");
		}

		tmnNumNo = getNodeText(doc, "TMNNUMNO");

		acceptDate = getNodeTextM(doc, "ACCEPTDATE");
		systemNo = getNodeTextM(doc, "SYSTEMNO");
		// 分转元
		txnAmount = MathTool.pointToYuan(getNodeTextM(doc, "TXNAMOUNT"));


		cashType = getNodeText(doc, "CASHTYPE");

		cashOrder = getNodeText(doc, "CASHORDER");
		cashNumber = getNodeTextM(doc, "CASHNUMBER");

		payType = getNodeText(doc, "PAYTYPE");// 支付方式
		// 默认企业账户 0
		if (Charset.isEmpty(payType)) {
			payType = "0";
		}
		if ((ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)||(ChannelCode.IPOS_CHANELCODE.equals(this.channelCode)))&&!"0".equals(payType)) {
			
			throw new Exception("必须为企业账户");
		}
		
		if (ChannelCode.IPOS_CHANELCODE.equals(this.channelCode)) {
			
			psamCardNo = getNodeTextM(doc, "PSAMCARDNO");
			eCardNo = getNodeTextM(doc, "ECARDNO");
			passFlag = getNodeTextM(doc, "PASSFLAG");
		}else{
			psamCardNo = getNodeText(doc, "PSAMCARDNO");
			eCardNo = getNodeText(doc, "ECARDNO");
			passFlag = getNodeText(doc, "PASSFLAG");
		}
		
		if ("0".equals(payType)) {
			// 0企业账户

			operUser = getNodeTextM(doc, "OPERUSER");
			operPassword = getNodeTextM(doc, "OPERPASSWORD");
		} else {

			operUser = getNodeText(doc, "OPERUSER");
			operPassword = getNodeText(doc, "OPERPASSWORD");
		}
		if ("1".equals(payType)) {
			// 1 POS
			payPassword = getNodeTextM(doc, "PAYPASSWORD");
			psamCardNo = getNodeTextM(doc, "PSAMCARDNO");
			trackTwo = getNodeTextM(doc, "TRACKTWO");

		} else {
			payPassword = getNodeText(doc, "PAYPASSWORD");
			psamCardNo = getNodeText(doc, "PSAMCARDNO");
			trackTwo = getNodeText(doc, "TRACKTWO");

		}
		if ("2".equals(payType)) {
			// 2 代收付
			bankAcct = getNodeTextM(doc, "BANKACCT");
			accName = getNodeTextM(doc, "ACCNAME");
			privateFlag = getNodeTextM(doc, "PRIVATEFLAG");
			cardFlag = getNodeTextM(doc, "CARDFLAG");
			if ("2".equals(cardFlag)) {
				creditValidTime = getNodeTextM(doc, "CREDITVALIDTIME");
				creditValidCode = getNodeTextM(doc, "CREDITVALIDCODE");
			} else {
				creditValidTime = getNodeText(doc, "CREDITVALIDTIME");
				creditValidCode = getNodeText(doc, "CREDITVALIDCODE");
			}
			contactPhone = getNodeTextM(doc, "CONTACTPHONE");
		} else {
			bankAcct = getNodeText(doc, "BANKACCT");
			accName = getNodeText(doc, "ACCNAME");
			privateFlag = getNodeText(doc, "PRIVATEFLAG");
			cardFlag = getNodeText(doc, "CARDFLAG");
			contactPhone = getNodeText(doc, "CONTACTPHONE");
		}

		networkNo = getNodeText(doc, "NETWORKNO");
		if (Charset.isEmpty(networkNo, true)) {
			networkNo = "003";// 不填写默认003磁卡
		}
		trackThree = getNodeText(doc, "TRACKTHREE");
		contactAddr = getNodeText(doc, "CONTACTADDR");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getCashNumber() {
		return cashNumber;
	}

	public void setCashNumber(String cashNumber) {
		this.cashNumber = cashNumber;
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

	public String getOutTmnNumNo() {
		return outTmnNumNo;
	}

	public void setOutTmnNumNo(String outTmnNumNo) {
		this.outTmnNumNo = outTmnNumNo;
	}

	public String getSystemNo() {
		return systemNo;
	}

	public void setSystemNo(String systemNo) {
		this.systemNo = systemNo;
	}

	public String getCashType() {
		return cashType;
	}

	public void setCashType(String cashType) {
		this.cashType = cashType;
	}

	public String getCashOrder() {
		return cashOrder;
	}

	public void setCashOrder(String cashOrder) {
		this.cashOrder = cashOrder;
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

}
