package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf12034Request extends CommonReqAbs {

	public DpInf12034Request(String xmlStr) throws Exception {
		super(xmlStr, null);
		// TODO Auto-generated constructor stub
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	private String systemNo;

	public String getSystemNo() {
		return systemNo;
	}

	public void setSystemNo(String systemNo) {
		this.systemNo = systemNo;
	}

	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_NUM)
	private String productCode;
	
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private String areaCode;
	
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_NUM)
	private String acctCode;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String tradeTime;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String txtAmount;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		custCode = getNodeTextM(doc, "CUSTCODE");
		
		systemNo = getNodeTextM(doc, "SYSTEMNO");

		productCode = getNodeTextM(doc, "PRODUCTCODE");
		
		areaCode = getNodeText(doc, "ACCEPTAREACODE");
		
		acctCode = getNodeTextM(doc, "ACCTCODE");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		txtAmount = getNodeTextM(doc, "TXNAMOUNT");
		
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
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
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
	public String getTxtAmount() {
		return txtAmount;
	}
	public void setTxtAmount(String txtAmount) {
		this.txtAmount = txtAmount;
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
