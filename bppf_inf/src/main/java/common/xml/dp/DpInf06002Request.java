package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.utils.DateTool;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06002Request extends CommonReqAbs {

	public DpInf06002Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

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

	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String accepTareaCode;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String acceptDate;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
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
		
		tmnNumNo = getNodeTextM(doc, "TMNNUMNO");

		accepTareaCode = getNodeText(doc, "ACCEPTAREACODE");
		acceptDate = getNodeTextM(doc, "ACCEPTDATE");
		if(!DateTool.validateDateFormat(acceptDate)){
			throw new Exception("输入日期格式不正确");
		}
		
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
	}

	public String getTmnNumNo() {
		return tmnNumNo;
	}

	public void setTmnNumNo(String tmnNumNo) {
		this.tmnNumNo = tmnNumNo;
	}

//	public String getOutTmnNumNo() {
//		return outTmnNumNo;
//	}
//
//	public void setOutTmnNumNo(String outTmnNumNo) {
//		this.outTmnNumNo = outTmnNumNo;
//	}

	public String getAccepTareaCode() {
		return accepTareaCode;
	}

	public void setAccepTareaCode(String accepTareaCode) {
		this.accepTareaCode = accepTareaCode;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
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

	
	public static void main(String[] args) {
		String date="20130709031528";
		System.out.println(DateTool.validateDateFormat(date));
	}
}
