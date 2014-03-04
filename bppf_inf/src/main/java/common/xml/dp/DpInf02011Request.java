package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02011Request extends CommonReqAbs {

	public DpInf02011Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String tmnNumNo;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String payType;

	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String psamCardNo;
	
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String eCardNo;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String passFlag;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
//	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String payPassword;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_NUM)
	private String rechargeType;
	@CheckerAnnotion(len = 11, type = CheckerAnnotion.TYPE_NUM)
	private String phone;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String rechargeAmount;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String txnAmount;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String tradeTime;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		orderNo = getNodeTextM(doc, "ORDERNO");
		if (ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)) {
			
			custCode = getNodeTextM(doc, "CUSTCODE");
			tmnNumNo = getNodeText(doc, "TMNNUMNO");
			psamCardNo = getNodeText(doc, "PSAMCARDNO");
			eCardNo = getNodeText(doc, "ECARDNO");
			passFlag = getNodeText(doc, "PASSFLAG");
		}else{
			
			custCode = getNodeText(doc, "CUSTCODE");
			tmnNumNo = getNodeTextM(doc, "TMNNUMNO");
			psamCardNo = getNodeTextM(doc, "PSAMCARDNO");
			eCardNo = getNodeTextM(doc, "ECARDNO");
			passFlag = getNodeTextM(doc, "PASSFLAG");
		}
		
		staffCode = getNodeTextM(doc, "STAFFCODE");
		payType= getNodeText(doc, "PAYTYPE");
		if(payType==null || payType.equals("") || payType.length()<=0){
			payType="0";
		}
		txnAmount = getNodeTextM(doc, "TXNAMOUNT");
		phone = getNodeTextM(doc, "PHONE");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		rechargeType = getNodeTextM(doc, "RECHARGETYPE");
		rechargeAmount = getNodeTextM(doc, "RECHARGEAMOUNT");
		payPassword = getNodeTextM(doc, "PAYPASSWORD");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getTmnNumNo() {
		return tmnNumNo;
	}

	public void setTmnNumNo(String tmnNumNo) {
		this.tmnNumNo = tmnNumNo;
	}

	public String getPsamCardNo() {
		return psamCardNo;
	}

	public void setPsamCardNo(String psamCardNo) {
		this.psamCardNo = psamCardNo;
	}

	public String getECardNo() {
		return eCardNo;
	}

	public void setECardNo(String eCardNo) {
		this.eCardNo = eCardNo;
	}

	public String getPassFlag() {
		return passFlag;
	}

	public void setPassFlag(String passFlag) {
		this.passFlag = passFlag;
	}
	
	public String getRechargeType() {
		return rechargeType;
	}

	public void setRechargeType(String rechargeType) {
		this.rechargeType = rechargeType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(String rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public String getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
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

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}
	

}
