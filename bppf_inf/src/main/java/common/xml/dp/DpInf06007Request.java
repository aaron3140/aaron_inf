package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06007Request extends CommonReqAbs {

	public DpInf06007Request(String xmlStr) throws Exception {

		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String childCustCode;

	public String getChildCustCode() {
		return childCustCode;
	}

	public void setChildCustCode(String childCustCode) {
		this.childCustCode = childCustCode;
	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String payPassword;

	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_NUM)
	private String operType;

	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String dayLimit;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String tradeTime;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		orderNo = getNodeTextM(doc, "ORDERNO");

		if (ChannelCode.IPOS_CHANELCODE.equals(this.channelCode)) {

			custCode = getNodeText(doc, "CUSTCODE");
		} else {

			custCode = getNodeTextM(doc, "CUSTCODE");
		}
		
		childCustCode = getNodeTextM(doc, "CHILDCUSTCODE");
		
		staffCode = getNodeTextM(doc, "STAFFCODE");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		payPassword = getNodeTextM(doc, "PAYPASSWORD");
		operType = getNodeTextM(doc, "OPERTYPE");
		
		//当操作类型为100时必填
		if("100".equals(operType)){
			dayLimit = getNodeTextM(doc, "DAYLIMIT");
		}else{
			dayLimit = getNodeText(doc, "DAYLIMIT");
		}
		
//		dayLimit = getNodeTextM(doc, "DAYLIMIT");

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

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getDayLimit() {
		return dayLimit;
	}

	public void setDayLimit(String dayLimit) {
		this.dayLimit = dayLimit;
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
