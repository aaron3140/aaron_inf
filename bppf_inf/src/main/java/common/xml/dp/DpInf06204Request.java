package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.utils.Charset;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06204Request extends CommonReqAbs {

	public DpInf06204Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_STR)
	private String apTransSeq;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_STR)
	private String tmnNumNo;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String transType;
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	private String systemNo;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String tradeTime;

	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String edcardId;
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String cardId;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String cardcnt;
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String cardmknd;
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String cardsknd;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String cardModel;
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String saleMode;

	/*
	 * @CheckerAnnotion(len = 120, type = CheckerAnnotion.TYPE_STR) private String command;
	 * 
	 * @CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR) private String commandlen;
	 */

	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String deposit;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String befbalance;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String txnamt;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String cardvalDate;
	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_STR)
	private String cityCode;
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String cardverno;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String batchNo;
	@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)
	private String authseq;
	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String limitedauthseql;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String tac;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String txnDate;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String txnTime;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String keySet;
	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_STR)
	private String operationType;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		orderSeq = getNodeTextM(doc, "ORDERSEQ");

		operationType = getNodeText(doc, "OPERATIONTYPE");
		if (Charset.isEmpty(operationType, true)) {
			operationType = "ATO";
		}
		if ("ATR".equals(operationType)) {
			apTransSeq = getNodeTextM(doc, "APTRANSSEQ");
		} else {
			apTransSeq = getNodeText(doc, "APTRANSSEQ");
		}

		if (ChannelCode.IPOS_CHANELCODE.equals(this.channelCode)) {
			custCode = getNodeText(doc, "CUSTCODE");
		} else {
			custCode = getNodeTextM(doc, "CUSTCODE");
		}

		tmnNumNo = getNodeTextM(doc, "TMNNUMNO");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		
		transType = getNodeTextM(doc, "TRANSTYPE");
		systemNo = getNodeTextM(doc, "SYSTEMNO");
		
		
		edcardId = getNodeTextM(doc, "EDCARDID");
		cardId = getNodeTextM(doc, "CARDID");
		cardcnt = getNodeTextM(doc, "CARDCNT");
		cardmknd = getNodeTextM(doc, "CARDMKND");
		cardsknd = getNodeTextM(doc, "CARDSKND");
		cardModel = getNodeTextM(doc, "CARDMODEL");
		saleMode = getNodeTextM(doc, "SALEMODE");

		/*
		 * command=getNodeTextM(doc, "COMMAND"); commandlen=getNodeTextM(doc, "COMMANDLEN");
		 */

		deposit = getNodeTextM(doc, "DEPOSIT");
		befbalance = getNodeTextM(doc, "BEFBALANCE");
		txnamt = getNodeTextM(doc, "TXNAMT");
		cardvalDate = getNodeTextM(doc, "CARDVALDATE");
		cityCode = getNodeTextM(doc, "CITYCODE");
		cardverno = getNodeTextM(doc, "CARDVERNO");
		batchNo = getNodeTextM(doc, "BATCHNO");
		authseq = getNodeTextM(doc, "AUTHSEQ");
		limitedauthseql = getNodeTextM(doc, "LIMITEDAUTHSEQL");
		tac = getNodeTextM(doc, "TAC");
		txnDate = getNodeTextM(doc, "TXNDATE");
		txnTime = getNodeTextM(doc, "TXNTIME");
		keySet = getNodeTextM(doc, "KEYSET");

		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getSystemNo() {
		return systemNo;
	}

	public void setSystemNo(String systemNo) {
		this.systemNo = systemNo;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getOrderSeq() {
		return orderSeq;
	}

	public String getApTransSeq() {
		return apTransSeq;
	}

	public void setApTransSeq(String apTransSeq) {
		this.apTransSeq = apTransSeq;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
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

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	/*
	 * public String getCommand() { return command; }
	 * 
	 * 
	 * 
	 * public void setCommand(String command) { this.command = command; }
	 * 
	 * 
	 * 
	 * public String getCommandlen() { return commandlen; }
	 * 
	 * 
	 * 
	 * public void setCommandlen(String commandlen) { this.commandlen = commandlen; }
	 */

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getCardmknd() {
		return cardmknd;
	}

	public void setCardmknd(String cardmknd) {
		this.cardmknd = cardmknd;
	}

	public String getCardsknd() {
		return cardsknd;
	}

	public void setCardsknd(String cardsknd) {
		this.cardsknd = cardsknd;
	}

	public String getCardModel() {
		return cardModel;
	}

	public void setCardModel(String cardModel) {
		this.cardModel = cardModel;
	}

	public String getDeposit() {
		return deposit;
	}

	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}

	public String getCardvalDate() {
		return cardvalDate;
	}

	public void setCardvalDate(String cardvalDate) {
		this.cardvalDate = cardvalDate;
	}

	public String getEdcardId() {
		return edcardId;
	}

	public void setEdcardId(String edcardId) {
		this.edcardId = edcardId;
	}

	public String getCardcnt() {
		return cardcnt;
	}

	public void setCardcnt(String cardcnt) {
		this.cardcnt = cardcnt;
	}

	public String getSaleMode() {
		return saleMode;
	}

	public void setSaleMode(String saleMode) {
		this.saleMode = saleMode;
	}

	public String getBefbalance() {
		return befbalance;
	}

	public void setBefbalance(String befbalance) {
		this.befbalance = befbalance;
	}

	public String getTxnamt() {
		return txnamt;
	}

	public void setTxnamt(String txnamt) {
		this.txnamt = txnamt;
	}

	public String getCardverno() {
		return cardverno;
	}

	public void setCardverno(String cardverno) {
		this.cardverno = cardverno;
	}

	public String getAuthseq() {
		return authseq;
	}

	public void setAuthseq(String authseq) {
		this.authseq = authseq;
	}


	public String getLimitedauthseql() {
		return limitedauthseql;
	}

	public void setLimitedauthseql(String limitedauthseql) {
		this.limitedauthseql = limitedauthseql;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getKeySet() {
		return keySet;
	}

	public void setKeySet(String keySet) {
		this.keySet = keySet;
	}
	
	

	public String getTac() {
		return tac;
	}

	public void setTac(String tac) {
		this.tac = tac;
	}

	
		
	public String getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}

	public String getTxnTime() {
		return txnTime;
	}

	public void setTxnTime(String txnTime) {
		this.txnTime = txnTime;
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
