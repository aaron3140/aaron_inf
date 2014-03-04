package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf12038Request extends CommonReqAbs {

	public DpInf12038Request(String xmlStr) throws Exception {
		super(xmlStr, null);
		// TODO Auto-generated constructor stub
	}
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 11, type = CheckerAnnotion.TYPE_NUM)
	private String phone;

	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String searchId;
	
	@CheckerAnnotion(len = 512, type = CheckerAnnotion.TYPE_STR)
	private String trainId;
	
	@CheckerAnnotion(len = 1024, type = CheckerAnnotion.TYPE_STR)
	private String bookInfo;
	
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	private String systemNo;
	
//	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
//	private String isOut;
	
	public String getSystemNo() {
		return systemNo;
	}

	public void setSystemNo(String systemNo) {
		this.systemNo = systemNo;
	}

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String date;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub
		
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		custCode = getNodeTextM(doc, "CUSTCODE");
		
		phone = getNodeTextM(doc, "PHONE");

		searchId = getNodeTextM(doc, "SEARCHID");
		
		trainId = getNodeText(doc, "TRAINID");
		
		bookInfo = getNodeTextM(doc, "BOOKINFO");
		
		systemNo = getNodeTextM(doc, "SYSTEMNO");
//		isOut = getNodeTextM(doc, "ISOUT");
		date = getNodeTextM(doc, "DATE");
		
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSearchId() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public String getTrainId() {
		return trainId;
	}

	public void setTrainId(String trainId) {
		this.trainId = trainId;
	}

	public String getBookInfo() {
		return bookInfo;
	}

	public void setBookInfo(String bookInfo) {
		this.bookInfo = bookInfo;
	}

//	public String getIsOut() {
//		return isOut;
//	}
//
//	public void setIsOut(String isOut) {
//		this.isOut = isOut;
//	}

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

}
