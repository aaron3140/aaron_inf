package common.xml.dp;

import org.dom4j.Document;

import common.utils.ParamValid;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 * 
 * 本类描述: 实时冲正接口
 * @version: 企业帐户前置接口 v1.0 
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email:  zhuxiaojun@tisson.com
 * @time: 2013-3-4上午11:23:47
 */
public class DpInf01015Request extends CommonReqAbs {
	public DpInf01015Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_NUM)
	private String bankAcct;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_NUM)
	private String apTransSeq;
	
	@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)
	private String appKeep;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String txnAmount;
		
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub
//		ParamValid paramValid = new ParamValid();
//		String resultDesc=paramValid.Valid(doc,"INF01015");
//		if (!resultDesc.equals("0")) {
//			throw new Exception (resultDesc);
//		}
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		custCode = getNodeTextM(doc,"CUSTCODE");
		apTransSeq = getNodeTextM(doc,"APTRANSSEQ");
		appKeep = getNodeTextM(doc,"APPKEEP");
		bankAcct = getNodeTextM(doc,"BANKACCT");
		txnAmount = getNodeTextM(doc,"TXNAMOUNT");
		int index = txnAmount.indexOf(".");
		if (index != -1) {
			throw new Exception("金额以分为单位,不能有小数点");
		}
		remark1 = getNodeText(doc,"REMARK1");
		remark2 = getNodeText(doc,"REMARK2");
	}

	public String getAppKeep() {
		return appKeep;
	}

	public void setAppKeep(String appKeep) {
		this.appKeep = appKeep;
	}

	public String getApTransSeq() {
		return apTransSeq;
	}

	public void setApTransSeq(String apTransSeq) {
		this.apTransSeq = apTransSeq;
	}

	public String getBankAcct() {
		return bankAcct;
	}

	public void setBankAcct(String bankAcct) {
		this.bankAcct = bankAcct;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
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

	public String getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}



}
