package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbsLogin;

public class DpInf03003Request extends CommonReqAbsLogin {

	public DpInf03003Request(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String password;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String agentCode;
	
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private String areaCode;
	
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String txnAmount;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String payeeCode;
	
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String payeeName;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String tradeTime;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	
	
	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}

	public String getPayeeCode() {
		return payeeCode;
	}

	public void setPayeeCode(String payeeCode) {
		this.payeeCode = payeeCode;
	}

	public String getPayeeName() {
		return payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
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

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		staffCode = getNodeTextM(doc, "STAFFCODE");
		password = getNodeTextM(doc, "PASSWORD");
		agentCode = getNodeTextM(doc, "AGENTCODE");
		areaCode = getNodeTextM(doc, "AREACODE");
		txnAmount = getNodeTextM(doc, "TXNAMOUNT");
		payeeCode = getNodeTextM(doc, "PAYEECODE");
		payeeName = getNodeText(doc, "PAYEENAME");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		remark1 = getNodeText(doc, "REMARK1");
	}

}
