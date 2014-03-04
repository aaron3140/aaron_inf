package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02025Request extends CommonReqAbs {

	public DpInf02025Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String tmnNumNo;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String acceptDate;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String payType;
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_NUM)
	private String phone;
	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_NUM)
	private String busType;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		orderNo = getNodeTextM(doc, "ORDERNO");
		custCode = getNodeTextM(doc, "CUSTCODE");
		tmnNumNo = getNodeText(doc, "TMNNUMNO");
		acceptDate = getNodeTextM(doc, "ACCEPTDATE");
		payType = getNodeText(doc, "PAYTYPE");
		if (payType == null || payType.equals("") || payType.length() <= 0) {
			payType = "9";
		}
		phone = getNodeTextM(doc, "PHONE");
		busType = getNodeText(doc, "BUSTYPE");
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

	public String getTmnNumNo() {
		return tmnNumNo;
	}

	public void setTmnNumNo(String tmnNumNo) {
		this.tmnNumNo = tmnNumNo;
	}

	public String getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
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
