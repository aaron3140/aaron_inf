package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06203Request extends CommonReqAbs {

	public DpInf06203Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_STR)
	private String tmnNumNo;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String ecardNo;
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String psamcardNo;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String passFlag;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String tradeTime;
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String payPassword;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String cardoprType;
	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_STR)
	private String cityCode;
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String cardId;
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String cardmknd;
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String cardsknd;

	@CheckerAnnotion(len = 120, type = CheckerAnnotion.TYPE_STR)
	private String command;
	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String commandlen;

	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String cardModel;
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String transType;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String deposit;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String origamt;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String cardvalDate;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String srcbal;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String cardseq;
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String keyver;
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String algind;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String cardRand;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String mac1;
	@CheckerAnnotion(len = 18, type = CheckerAnnotion.TYPE_STR)
	private String divData;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String batchNo;
	
	/*@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String cardcnt;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String befbalance;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String txnamt;
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String cardverno;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String saleMode;//开卡冲正必填
	
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String handingCharge;//充值冲正必填
	@CheckerAnnotion(len = 9, type = CheckerAnnotion.TYPE_STR)
	private String lastpossvseq;//充值冲正必填
*/	
	
	
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String keySet;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		if (ChannelCode.IPOS_CHANELCODE.equals(this.channelCode)) {
			custCode = getNodeText(doc, "CUSTCODE");
			
			ecardNo = getNodeTextM(doc, "ECARDNO");
			psamcardNo = getNodeTextM(doc, "PSAMCARDNO");
			passFlag = getNodeTextM(doc, "PASSFLAG");
			
		} else {
			custCode = getNodeTextM(doc, "CUSTCODE");
			
			ecardNo = getNodeText(doc, "ECARDNO");
			psamcardNo = getNodeText(doc, "PSAMCARDNO");
			passFlag = getNodeText(doc, "PASSFLAG");
			
		}
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		tmnNumNo = getNodeTextM(doc, "TMNNUMNO");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		payPassword = getNodeTextM(doc, "PAYPASSWORD");
		cardoprType = getNodeTextM(doc, "CARDOPRTYPE");
		/*if ("2062".equals(cardoprType)) {
			handingCharge = getNodeTextM(doc, "HANDINGCHARGE");
			lastpossvseq = getNodeTextM(doc, "LASTPOSSVSEQ");
			saleMode = getNodeText(doc, "SALEMODE");
		} else if ("2063".equals(cardoprType)) {
			saleMode = getNodeTextM(doc, "SALEMODE");
			handingCharge = getNodeText(doc, "HANDINGCHARGE");
			lastpossvseq = getNodeText(doc, "LASTPOSSVSEQ");
		}*/
		
		cityCode = getNodeTextM(doc, "CITYCODE");
		cardId = getNodeTextM(doc, "CARDID");
		cardmknd = getNodeTextM(doc, "CARDMKND");
		cardsknd = getNodeTextM(doc, "CARDSKND");

		commandlen = getNodeText(doc, "COMMANDLEN");
		if(!commandlen.matches("^0+$")){
			command = getNodeTextM(doc, "COMMAND");
		}else{
			command = getNodeText(doc, "COMMAND");
		}

		cardModel = getNodeTextM(doc, "CARDMODEL");
		transType = getNodeTextM(doc, "TRANSTYPE");
		deposit = getNodeTextM(doc, "DEPOSIT");
		origamt = getNodeTextM(doc, "ORIGAMT");
		cardvalDate = getNodeTextM(doc, "CARDVALDATE");
		srcbal = getNodeTextM(doc, "SRCBAL");
		cardseq = getNodeTextM(doc, "CARDSEQ");
		keyver = getNodeTextM(doc, "KEYVER");
		algind = getNodeTextM(doc, "ALGIND");
		cardRand = getNodeTextM(doc, "CARDRAND");
		mac1 = getNodeTextM(doc, "MAC1");
		divData = getNodeTextM(doc, "DIVDATA");
		batchNo = getNodeTextM(doc, "BATCHNO");
		
		/*cardcnt = getNodeTextM(doc, "CARDCNT");
		befbalance = getNodeTextM(doc, "BEFBALANCE");
		txnamt = getNodeTextM(doc, "TXNAMT");
		cardverno = getNodeTextM(doc, "CARDVERNO");*/
		
		keySet = getNodeTextM(doc, "KEYSET");

		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getOrderSeq() {
		return orderSeq;
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

	public String getCardoprType() {
		return cardoprType;
	}

	public void setCardoprType(String cardoprType) {
		this.cardoprType = cardoprType;
	}

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

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getDeposit() {
		return deposit;
	}

	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}

	public String getOrigamt() {
		return origamt;
	}

	public void setOrigamt(String origamt) {
		this.origamt = origamt;
	}

	public String getCardvalDate() {
		return cardvalDate;
	}

	public void setCardvalDate(String cardvalDate) {
		this.cardvalDate = cardvalDate;
	}

	public String getSrcbal() {
		return srcbal;
	}

	public void setSrcbal(String srcbal) {
		this.srcbal = srcbal;
	}

	public String getCardseq() {
		return cardseq;
	}

	public void setCardseq(String cardseq) {
		this.cardseq = cardseq;
	}

	public String getKeyver() {
		return keyver;
	}

	public void setKeyver(String keyver) {
		this.keyver = keyver;
	}

	public String getAlgind() {
		return algind;
	}

	public void setAlgind(String algind) {
		this.algind = algind;
	}

	public String getCardRand() {
		return cardRand;
	}

	public void setCardRand(String cardRand) {
		this.cardRand = cardRand;
	}

	public String getMac1() {
		return mac1;
	}

	public void setMac1(String mac1) {
		this.mac1 = mac1;
	}

	public String getDivData() {
		return divData;
	}

	public void setDivData(String divData) {
		this.divData = divData;
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

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommandlen() {
		return commandlen;
	}

	public void setCommandlen(String commandlen) {
		this.commandlen = commandlen;
	}
	
	

	public String getEcardNo() {
		return ecardNo;
	}

	public void setEcardNo(String ecardNo) {
		this.ecardNo = ecardNo;
	}

	public String getPsamcardNo() {
		return psamcardNo;
	}

	public void setPsamcardNo(String psamcardNo) {
		this.psamcardNo = psamcardNo;
	}

	public String getPassFlag() {
		return passFlag;
	}

	public void setPassFlag(String passFlag) {
		this.passFlag = passFlag;
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
