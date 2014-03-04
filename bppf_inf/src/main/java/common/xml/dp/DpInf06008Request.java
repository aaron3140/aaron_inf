package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06008Request extends CommonReqAbs {

	public DpInf06008Request(String xmlStr) throws Exception {

		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String childCustCode;

	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_NUM)
	private String acctType;

	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_NUM)
	private String startNum;

	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_NUM)
	private String endNum;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		if (ChannelCode.IPOS_CHANELCODE.equals(this.channelCode)) {

			custCode = getNodeText(doc, "CUSTCODE");
		} else {

			custCode = getNodeTextM(doc, "CUSTCODE");
		}
		staffCode = getNodeTextM(doc, "STAFFCODE");
		childCustCode = getNodeText(doc, "CHILDCUSTCODE");
		acctType = getNodeTextM(doc, "ACCTTYPE");

		startNum = getNodeText(doc, "STARTNUM");
		endNum = getNodeText(doc, "ENDNUM");

	}

	public String getChildCustCode() {
		return childCustCode;
	}

	public void setChildCustCode(String childCustCode) {
		this.childCustCode = childCustCode;
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

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}

	public String getEndNum() {
		return endNum;
	}

	public void setEndNum(String endNum) {
		this.endNum = endNum;
	}

	public String getStartNum() {
		return startNum;
	}

	public void setStartNum(String startNum) {
		this.startNum = startNum;
	}

}
