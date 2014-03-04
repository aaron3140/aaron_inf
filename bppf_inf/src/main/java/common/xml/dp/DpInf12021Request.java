package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf12021Request extends CommonReqAbs {

	public DpInf12021Request(String xmlStr) throws Exception {
		super(xmlStr, null);
		// TODO Auto-generated constructor stub
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_NUM)
	private String rechargeType;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_NUM)
	private String verify;

	@CheckerAnnotion(len = 11, type = CheckerAnnotion.TYPE_NUM)
	private String phone;

	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String rechargeFlow;
	
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	private String systemNO;

	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String txnAmount;

	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private String acceptAreaCode;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String tradeTime;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub
		
		orderNo = getNodeTextM(doc, "ORDERNO");

		custCode = getNodeTextM(doc, "CUSTCODE");

		rechargeType = getNodeTextM(doc, "RECHARGETYPE");

		verify = getNodeTextM(doc, "VERIFY");

		phone = getNodeTextM(doc, "PHONE");

		rechargeFlow = getNodeTextM(doc, "RECHARGEFLOW");
		
		systemNO=getNodeText(doc, "SYSTEMNO");

		txnAmount = getNodeTextM(doc, "TXNAMOUNT");

		acceptAreaCode = getNodeText(doc, "ACCEPTAREACODE");

		tradeTime = getNodeTextM(doc, "TRADETIME");

		remark1 = getNodeText(doc, "REMARK1");

		remark2 = getNodeText(doc, "REMARK2");


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

	public String getRechargeType() {
		return rechargeType;
	}

	public void setRechargeType(String rechargeType) {
		this.rechargeType = rechargeType;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRechargeFlow() {
		return rechargeFlow;
	}

	public void setRechargeFlow(String rechargeFlow) {
		this.rechargeFlow = rechargeFlow;
	}

	public String getSystemNO() {
		return systemNO;
	}

	public void setSystemNO(String systemNO) {
		this.systemNO = systemNO;
	}

	public String getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}

	public String getAcceptAreaCode() {
		return acceptAreaCode;
	}

	public void setAcceptAreaCode(String acceptAreaCode) {
		this.acceptAreaCode = acceptAreaCode;
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

}
