package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.utils.PrivConstant;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02032Request extends CommonReqAbs {

	public DpInf02032Request(String xmlStr) throws Exception {
		super(xmlStr, null);

	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String password;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String custCode;

	@CheckerAnnotion(len = 54, type = CheckerAnnotion.TYPE_STR)
	private String colleCustCode;

	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String txnamount;

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String acctType;

	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String tranType;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_NUM)
	private String preOrderSeq;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		custCode = getNodeTextM(doc, "CUSTCODE");
		txnamount = getNodeTextM(doc, "TXNAMOUNT");
//		acctType = getNodeText(doc, "ACCTTYPE");
		
		this.setAcctType(PrivConstant.ENTER_ACCT_TYPE);

		if (ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)){
			
			 staffCode = getNodeTextM(doc, "STAFFCODE");
			 
			 password = getNodeTextM(doc, "PASSWORD");
		}

		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");

		colleCustCode = getNodeTextM(doc, "COLLECUSTCODE");

		int index = txnamount.indexOf(".");
		if (index != -1) {
			throw new Exception("金额以分为单位,不能有小数点");
		}

		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		
		tranType = getNodeText(doc, "TRANTYPE");
		
		if(tranType==null||"".equals(tranType)){
			
			this.setTranType("0");
		}

		if("1".equals(tranType)){
			
			preOrderSeq = getNodeTextM(doc, "PREORDERSEQ");
		}
	}

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public String getPreOrderSeq() {
		return preOrderSeq;
	}

	public void setPreOrderSeq(String preOrderSeq) {
		this.preOrderSeq = preOrderSeq;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getColleCustCode() {
		return colleCustCode;
	}

	public void setColleCustCode(String colleCustCode) {
		this.colleCustCode = colleCustCode;
	}

	public String getTxnamount() {
		return txnamount;
	}

	public void setTxnamount(String txnamount) {
		this.txnamount = txnamount;
	}

	public String getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
	}

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
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
