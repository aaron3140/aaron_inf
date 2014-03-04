package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;


public class DpInf12035Request extends CommonReqAbs {
	
	public DpInf12035Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private String acceptAreacode;
	
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String fromStation;
	
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String toStation;
	
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_NUM)
	private String date;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
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



	public String getAcceptAreacode() {
		return acceptAreacode;
	}



	public void setAcceptAreacode(String acceptAreacode) {
		this.acceptAreacode = acceptAreacode;
	}



	public String getFromStation() {
		return fromStation;
	}



	public void setFromStation(String fromStation) {
		this.fromStation = fromStation;
	}



	public String getToStation() {
		return toStation;
	}



	public void setToStation(String toStation) {
		this.toStation = toStation;
	}



	public String getDate() {
		return date;
	}



	public void setDate(String date) {
		this.date = date;
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
		
		setOrderNo(getNodeTextM(doc, "ORDERNO"));
		
		setCustCode(getNodeTextM(doc, "CUSTCODE"));
		
		setAcceptAreacode(getNodeText(doc, "ACCEPTAREACODE"));
		
		setFromStation(getNodeTextM(doc, "FROMSTATION"));
		
		setToStation(getNodeTextM(doc, "TOSTATION"));
		
		setDate(getNodeTextM(doc, "DATE"));
		
		setRemark1(getNodeText(doc, "REMARK1"));
		
		setRemark2(getNodeText(doc, "REMARK2"));
		
	}
	
}
