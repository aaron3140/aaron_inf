package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpAccountManagementRequest extends CommonReqAbs {
	
	public DpAccountManagementRequest(String xmlStr) throws Exception {
		super(xmlStr, null);

	}
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String agentCode;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_NUM)
	private String transferFlag;

	@CheckerAnnotion(len = 54, type = CheckerAnnotion.TYPE_STR)
	private String colleCustCode;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_NUM)
	private String opertype;
	
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String txnamount;
	
	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String password;
	
//	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String acctType;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	public String getTransferFlag() {
		return transferFlag;
	}

	public void setTransferFlag(String transferFlag) {
		this.transferFlag = transferFlag;
	}

	public String getColleCustCode() {
		return colleCustCode;
	}

	public void setColleCustCode(String colleCustCode) {
		this.colleCustCode = colleCustCode;
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
	
	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}

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

	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
//		ParamValid paramValid = new ParamValid();
//		String resultDesc=paramValid.Valid(doc,"INF_01_004");
//		if (!resultDesc.equals("0")) {
//			throw new Exception (resultDesc);
//		}
		
		agentCode = getNodeTextM(doc, "AGENTCODE");
		opertype = getNodeTextM(doc, "OPERTYPE");
		txnamount = getNodeTextM(doc, "TXNAMOUNT");
		
//		acctType = getNodeTextM(doc, "ACCTTYPE");
		acctType = getNodeText(doc, "ACCTTYPE");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
		
//		transferFlag = getNodeText(doc, "TRANSFERFLAG");
		
//		if(transferFlag !=null&&"01".equals(transferFlag)){
//			
//			colleCustCode = getNodeTextM(doc, "COLLECUSTCODE");
//		}else{
//			
//			colleCustCode = getNodeText(doc, "COLLECUSTCODE");
//		}
		
		int index = txnamount.indexOf(".");
		if (index != -1) {
			throw new Exception("金额以分为单位,不能有小数点");
		}
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)||ChannelCode.IVR_CHANELCODE.equals(channelCode)) {
			staffCode = getNodeTextM(doc, "STAFFCODE");
			password = getNodeTextM(doc, "PASSWORD");
		}
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getOpertype() {
		return opertype;
	}

	public void setOpertype(String opertype) {
		this.opertype = opertype;
	}

	public String getTxnamount() {
		return txnamount;
	}

	public void setTxnamount(String txnamount) {
		this.txnamount = txnamount;
	}

	public String getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
	}

}
