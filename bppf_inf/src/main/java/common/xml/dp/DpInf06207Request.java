package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06207Request extends CommonReqAbs {

	public DpInf06207Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}
	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_STR)
	private String tmnNumNo;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String tradeTime;
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_STR)
	private String transseq;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		custCode = getNodeText(doc, "CUSTCODE");
		tmnNumNo = getNodeTextM(doc, "TMNNUMNO");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		
		transseq=getNodeText(doc, "TRANSSEQ");
		
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


	public String getStaffCode() {
		return staffCode;
	}


	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
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



	public String getTransseq() {
		return transseq;
	}



	public void setTransseq(String transseq) {
		this.transseq = transseq;
	}

	
	
}
