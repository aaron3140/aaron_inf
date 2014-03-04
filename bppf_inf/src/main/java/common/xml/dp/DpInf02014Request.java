package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02014Request extends CommonReqAbs {

	public DpInf02014Request(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String objectCode;
	
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	private String apptransSeq;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String txnAmount;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String tradeTime;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	public String getCustCode() {
		return custCode;
	}


	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}


	public String getObjectCode() {
		return objectCode;
	}


	public void setObjectCode(String objectCode) {
		this.objectCode = objectCode;
	}


	public String getOrderSeq() {
		return orderSeq;
	}


	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
	}


	public String getApptransSeq() {
		return apptransSeq;
	}


	public void setApptransSeq(String apptransSeq) {
		this.apptransSeq = apptransSeq;
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
	
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		custCode = getNodeTextM(doc, "CUSTCODE");
		objectCode = getNodeText(doc, "OBJECTCODE");
		
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		apptransSeq = getNodeTextM(doc, "APPTRANSSEQ");
		txnAmount = getNodeTextM(doc, "TXNAMOUNT");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

}
