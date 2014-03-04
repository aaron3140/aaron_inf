package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf05104Request extends CommonReqAbs {

	public DpInf05104Request(String xmlStr) throws Exception {
		super(xmlStr,null);
		// TODO Auto-generated constructor stub
	}
	
	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;
	

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
//	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String payPassword;
	
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String operType;
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_STR)
	private String planCode;

	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String planName;
	
	@CheckerAnnotion(len = 300, type = CheckerAnnotion.TYPE_STR)
	private String planDesc;
	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_STR)
	private String planType;

	@CheckerAnnotion(len = 300, type = CheckerAnnotion.TYPE_STR)
	private String planCustCode;
	@CheckerAnnotion(len = 300, type = CheckerAnnotion.TYPE_STR)
	private String planValue;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String tradeTime;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub

		orderNo = getNodeTextM(doc, "ORDERNO");
		if (ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)) {
			custCode = getNodeText(doc, "CUSTCODE");
			staffCode = getNodeTextM(doc, "STAFFCODE");
			payPassword = getNodeTextM(doc, "PAYPASSWORD");
		}else{
			
			custCode = getNodeTextM(doc, "CUSTCODE");
			staffCode = getNodeText(doc, "STAFFCODE");
			payPassword = getNodeText(doc, "PAYPASSWORD");
		}
		
		operType= getNodeTextM(doc, "OPERTYPE");
		
		planCode = getNodeTextM(doc, "PLANCODE");
	
		planName = getNodeTextM(doc, "PLANNAME");
		planDesc = getNodeTextM(doc, "PLANDESC");
		planType = getNodeTextM(doc, "PLANTYPE");
		planCustCode = getNodeText(doc, "PLANCUSTCODE");
		planValue = getNodeText(doc, "PLANVALUE");
		tradeTime = getNodeTextM(doc, "TRADETIME");
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

	public String getPlanDesc() {
		return planDesc;
	}

	public void setPlanDesc(String planDesc) {
		this.planDesc = planDesc;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getPlanCustCode() {
		return planCustCode;
	}

	public void setPlanCustCode(String planCustCode) {
		this.planCustCode = planCustCode;
	}

	public String getPlanValue() {
		return planValue;
	}

	public void setPlanValue(String planValue) {
		this.planValue = planValue;
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
