package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02016Request extends CommonReqAbs {

	public DpInf02016Request(String xmlStr) throws Exception {

		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String custCode;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_STR)
	private String acctType;

	private String startdate;

	private String enddate;

	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_NUM)
	private String startNum;

	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_NUM)
	private String endNum;

	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_STR)
	private String transCode;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String income;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String bankMode;

	public String getBankMode() {
		return bankMode;
	}

	public void setBankMode(String bankMode) {
		this.bankMode = bankMode;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getStartNum() {
		return startNum;
	}

	public void setStartNum(String startNum) {
		this.startNum = startNum;
	}

	public String getEndNum() {
		return endNum;
	}

	public void setEndNum(String endNum) {
		this.endNum = endNum;
	}

	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		startdate = getNodeText(doc, "STARTDATE");
		enddate = getNodeText(doc, "ENDDATE");
		custCode = getNodeTextM(doc, "CUSTCODE");
		bankMode = getNodeText(doc, "BANKMODE");
		if(bankMode==null || "".equals(bankMode)){
			bankMode= "BT1001";
		}
		if (ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)) {

			staffCode = getNodeTextM(doc, "STAFFCODE");
		} else {

			staffCode = getNodeText(doc, "STAFFCODE");
		}

		startNum = getNodeText(doc, "STARTNUM");
		endNum = getNodeText(doc, "ENDNUM");
		acctType = getNodeTextM(doc, "ACCTTYPE");
		transCode = getNodeText(doc, "TRANSCODE");

		income = getNodeText(doc, "INCOME");

	}

}
