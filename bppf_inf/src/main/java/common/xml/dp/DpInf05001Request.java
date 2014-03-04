package common.xml.dp;

import org.dom4j.Document;

import common.utils.ParamValid;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;
/**
 *代收付统一接口 
 * 
 */
public class DpInf05001Request extends CommonReqAbs {

	public DpInf05001Request(String xmlStr) throws Exception {
		super(xmlStr, null);

	}
	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String branchCode;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String externalId;
	
	@CheckerAnnotion(len = 5, type = CheckerAnnotion.TYPE_STR)
	private String busiType;
	
	@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)
	private String bankAcct;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String transContractId;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_NUM)
	private String transferFlag;
	
	@CheckerAnnotion(len = 54, type = CheckerAnnotion.TYPE_STR)
	private String colleCustCode;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String txnAmount;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String memo;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		ParamValid paramValid = new ParamValid();
		String resultDesc=paramValid.Valid(doc,"INF05001");
		if (!resultDesc.equals("0")) {
			throw new Exception (resultDesc);
		}
		
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		busiType = getNodeTextM(doc, "BUSITYPE");

		branchCode = getNodeText(doc,"BRANCHCODE");
		externalId = getNodeText(doc,"EXTERNALID");	
		bankAcct = getNodeTextM(doc,"BANKACCT");
		transContractId = getNodeTextM(doc,"TRANSCONTRACTID");		
		txnAmount = getNodeTextM(doc, "TXNAMOUNT");
		int index = txnAmount.indexOf(".");
		if (index != -1) {
			throw new Exception("金额以分为单位,不能有小数点");
		}
		memo = getNodeText(doc, "MEMO");

//		transferFlag = getNodeText(doc, "TRANSFERFLAG");
//		
//		if(transferFlag !=null&&"01".equals(transferFlag)){
//			
//			colleCustCode = getNodeTextM(doc, "COLLECUSTCODE");
//		}else{
//			
//			colleCustCode = getNodeText(doc, "COLLECUSTCODE");
//		}

		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

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
	
	public String getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getTransContractId() {
		return transContractId;
	}

	public void setTransContractId(String transContractId) {
		this.transContractId = transContractId;
	}	

	public String getBankAcct() {
		return bankAcct;
	}

	public void setBankAcct(String bankAcct) {
		this.bankAcct = bankAcct;
	}

	public String getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

}
