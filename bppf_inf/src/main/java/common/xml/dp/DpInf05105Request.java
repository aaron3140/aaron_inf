package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf05105Request extends CommonReqAbs {

	public DpInf05105Request(String xmlStr) throws Exception {
		super(xmlStr,null);
		// TODO Auto-generated constructor stub
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_STR)
	private String planCode;

	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String planName;

	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_STR)
	private String planType;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub
		
		if (ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)) {
			staffCode = getNodeTextM(doc, "STAFFCODE");
		}else{
			staffCode = getNodeText(doc, "STAFFCODE");
		}
		
		custCode = getNodeTextM(doc, "CUSTCODE");
		planCode = getNodeText(doc, "PLANCODE");
	
		planName = getNodeText(doc, "PLANNAME");

		planType = getNodeText(doc, "PLANTYPE");

		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");

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
	public String getPlanCode() {
		return planCode;
	}
	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
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
