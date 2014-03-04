package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;


/**
 * File                 : TradeRequest.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : bppf_inf
 * JDK version used     : JDK 1.6
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2012-2-2 下午05:49:15 [created]
 * Author               : Zhilong Luo 罗志龙
 * Email                : luozhilong@tisson.cn
 **/
public class TradeRequest extends CommonReqAbs {
	
	public TradeRequest(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String agentCode;
	
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private String areaCode;
	
	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_NUM)
	private String actionCode;
	
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String txnAmount;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String payeeCode;
	
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	private String goodsCode;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String goodsName;
	
	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	
	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_NUM)
	private String transSeq;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String tradeTime;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String mark1;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String mark2;


	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String password;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String verifycode;
	
	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		/*ParamValid paramValid = new ParamValid();
		String resultDesc=paramValid.Valid(doc,"INF_01_003");
		if (!resultDesc.equals("0")) {
			throw new Exception (resultDesc);
		}*/
		
		agentCode = getNodeTextM(doc, "AGENTCODE");
		areaCode = getNodeTextM(doc, "AREACODE");
		actionCode = getNodeTextM(doc, "ACTIONCODE");
		txnAmount = getNodeTextM(doc, "TXNAMOUNT");
		int index = txnAmount.indexOf(".");
		if (index != -1) {
			throw new Exception("金额以分为单位,不能有小数点");
		}
		payeeCode = getNodeTextM(doc, "PAYEECODE");
		goodsCode = getNodeText(doc, "GOODSCODE");
		goodsName = getNodeText(doc, "GOODSNAME");
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		transSeq = getNodeText(doc, "TRANSSEQ");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		mark1 = getNodeText(doc, "MARK1");
		mark2 = getNodeText(doc, "MARK2");
		if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
			staffCode = getNodeTextM(doc, "STAFFCODE");
			password = getNodeTextM(doc, "PASSWORD");
			verifycode = getNodeText(doc, "VERIFYCODE");
		}
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

	public String getVerifycode() {
		return verifycode;
	}

	public void setVerifycode(String verifycode) {
		this.verifycode = verifycode;
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

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
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

	public String getGoodsCode() {
		return goodsCode;
	}

	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
	}

	public String getTransSeq() {
		return transSeq;
	}

	public void setTransSeq(String transSeq) {
		this.transSeq = transSeq;
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getMark1() {
		return mark1;
	}

	public void setMark1(String mark1) {
		this.mark1 = mark1;
	}

	public String getMark2() {
		return mark2;
	}
	
	public void setMark2(String mark2) {
		this.mark2 = mark2;
	}

}
