package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06003Request extends CommonReqAbs {

	public DpInf06003Request(String xmlStr) throws Exception {

		super(xmlStr, null);

	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_STR)
	private String tmnNumNo;

	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String outTmnNumNo;

	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_STR)
	private String selectType;

	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String selectValue;

	@CheckerAnnotion(len = 11, type = CheckerAnnotion.TYPE_NUM)
	private String phoneNumber;

	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String billMonth;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String acceptDate;

	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String acceptAreaCode;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String additem1;

	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_STR)
	private String additem2;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark3;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark4;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		orderSeq = getNodeTextM(doc, "ORDERSEQ");

		if (ChannelCode.IPOS_CHANELCODE.equals(this.channelCode)) {

			custCode = getNodeText(doc, "CUSTCODE");
		} else {

			custCode = getNodeTextM(doc, "CUSTCODE");
		}
		
		if (ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)) {

			staffCode = getNodeTextM(doc, "STAFFCODE");
		} else {

			staffCode = getNodeText(doc, "STAFFCODE");
		}
		tmnNumNo = getNodeText(doc, "TMNNUMNO");

		selectType = getNodeTextM(doc, "SELECTTYPE");
		selectValue = getNodeTextM(doc, "SELECTVALUE");
		phoneNumber = getNodeText(doc, "PHONENUMBER");
		billMonth = getNodeText(doc, "BILLMONTH");
		acceptDate = getNodeTextM(doc, "ACCEPTDATE");
		acceptAreaCode = getNodeText(doc, "ACCEPTAREACODE");
		additem1 = getNodeTextM(doc, "ADDITEM1");
		// 上海水和煤气要求必须输入
		if ("310000".equals(acceptAreaCode) || "310100".equals(acceptAreaCode)) {
			// 项目编码
			String num = additem1.substring(additem1.length() - 4, additem1.length() - 3);
			if ("1".equals(num) || "3".equals(num)) {
				additem2 = getNodeTextM(doc, "ADDITEM2");
			} else {
				additem2 = getNodeText(doc, "ADDITEM2");
			}
		} else {
			additem2 = getNodeText(doc, "ADDITEM2");
		}

		// 北京供暖作为发票台头使用，而且必须输入。
		if ("110000".equals(acceptAreaCode)) {
			remark1 = getNodeTextM(doc, "REMARK1");
		} else {
			remark1 = getNodeText(doc, "REMARK1");
		}
		remark2 = getNodeText(doc, "REMARK2");
		remark3 = getNodeText(doc, "REMARK3");
		remark4 = getNodeText(doc, "REMARK4");

	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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

	public String getOutTmnNumNo() {
		return outTmnNumNo;
	}

	public void setOutTmnNumNo(String outTmnNumNo) {
		this.outTmnNumNo = outTmnNumNo;
	}

	public String getSelectType() {
		return selectType;
	}

	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}

	public String getSelectValue() {
		return selectValue;
	}

	public void setSelectValue(String selectValue) {
		this.selectValue = selectValue;
	}

	public String getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(String billMonth) {
		this.billMonth = billMonth;
	}

	public String getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
	}

	public String getAcceptAreaCode() {
		return acceptAreaCode;
	}

	public void setAcceptAreaCode(String acceptAreaCode) {
		this.acceptAreaCode = acceptAreaCode;
	}

	public String getAdditem1() {
		return additem1;
	}

	public void setAdditem1(String additem1) {
		this.additem1 = additem1;
	}

	public String getAdditem2() {
		return additem2;
	}

	public void setAdditem2(String additem2) {
		this.additem2 = additem2;
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

	public String getRemark3() {
		return remark3;
	}

	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}

	public String getRemark4() {
		return remark4;
	}

	public void setRemark4(String remark4) {
		this.remark4 = remark4;
	}

}
