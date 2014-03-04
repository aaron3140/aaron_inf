package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02035Request extends CommonReqAbs{

	public DpInf02035Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}
	
	//订单号
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	
	//客户编码
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	//用户名
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	//交易密码
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String payPassWord;
	
	//系统参考号
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	private String systemNo;
	
	//业务编码
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_NUM)
	private String busICode;
	
	//受理区域编码
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private String acceptAreaCode;
	
	//账户号码
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String acctCode;
	
	//受理时间
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String tradeTime;
	
	//交易金额
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String txnAmount;
	
	//预留域1
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	
	//预留域2
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		
		custCode = getNodeTextM(doc, "CUSTCODE");
		
		staffCode = getNodeTextM(doc, "STAFFCODE");
		
		payPassWord = getNodeText(doc, "PAYPASSWORD");
		
		systemNo = getNodeTextM(doc, "SYSTEMNO");
		
		busICode = getNodeTextM(doc, "BUSICODE");
		
		acceptAreaCode = getNodeText(doc, "ACCEPTAREACODE");
		
		acctCode = getNodeTextM(doc, "ACCTCODE");
		
		tradeTime = getNodeTextM(doc, "TRADETIME");
		
		txnAmount = getNodeTextM(doc, "TXNAMOUNT");
		
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

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getPayPassWord() {
		return payPassWord;
	}

	public void setPayPassWord(String payPassWord) {
		this.payPassWord = payPassWord;
	}

	public String getSystemNo() {
		return systemNo;
	}

	public void setSystemNo(String systemNo) {
		this.systemNo = systemNo;
	}

	public String getBusICode() {
		return busICode;
	}

	public void setBusICode(String busICode) {
		this.busICode = busICode;
	}

	public String getAcceptAreaCode() {
		return acceptAreaCode;
	}

	public void setAcceptAreaCode(String acceptAreaCode) {
		this.acceptAreaCode = acceptAreaCode;
	}

	public String getAcctCode() {
		return acctCode;
	}

	public void setAcctCode(String acctCode) {
		this.acctCode = acctCode;
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(String txnaMount) {
		this.txnAmount = txnaMount;
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
