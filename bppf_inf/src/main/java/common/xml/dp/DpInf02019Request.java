package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02019Request extends CommonReqAbs {

	public DpInf02019Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	// @CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String payPassword;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String payType;
	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_NUM)
	private String gameCode;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String battleAcct;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String gameAcct;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String rechargeAmount;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String orderaAmount;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String tradeTime;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		orderNo = getNodeTextM(doc, "ORDERNO");
		custCode = getNodeTextM(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		payPassword = getNodeText(doc, "PAYPASSWORD");
		payType = getNodeText(doc, "PAYTYPE");
		if(payType==null || payType.equals("") || payType.length()<=0){
			payType="0";
		}
		gameCode = getNodeTextM(doc, "GAMECODE");
		
		if("1001".equals(gameCode)){
			
			gameAcct = getNodeText(doc, "GAMEACCT");
			battleAcct = getNodeTextM(doc, "BATTLEACCT");
		}else{
			
			battleAcct = getNodeText(doc, "BATTLEACCT");
			gameAcct = getNodeTextM(doc, "GAMEACCT");
		}

		
		rechargeAmount = getNodeTextM(doc, "RECHARGEAMOUNT");
		orderaAmount = getNodeTextM(doc, "ORDERAMOUNT");
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

	public String getGameCode() {
		return gameCode;
	}

	public void setGameCode(String gameCode) {
		this.gameCode = gameCode;
	}

	public String getBattleAcct() {
		return battleAcct;
	}

	public void setBattleAcct(String battleAcct) {
		this.battleAcct = battleAcct;
	}

	public String getGameAcct() {
		return gameAcct;
	}

	public void setGameAcct(String gameAcct) {
		this.gameAcct = gameAcct;
	}

	public String getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(String rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public String getOrderaAmount() {
		return orderaAmount;
	}

	public void setOrderaAmount(String orderaAmount) {
		this.orderaAmount = orderaAmount;
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

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}
	
}
