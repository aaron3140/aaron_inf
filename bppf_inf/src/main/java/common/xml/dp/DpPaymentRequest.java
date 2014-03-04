package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.Element;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 * @author Tisson
 *
 */
public class DpPaymentRequest extends CommonReqAbs {

	public DpPaymentRequest(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}


	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_STR)
	private String agentCode;
	
	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String areaCode;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String txnChannel;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String payType;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String cardType;

	@CheckerAnnotion(len = 40, type = CheckerAnnotion.TYPE_STR)
	private String cardNo;

	@CheckerAnnotion(len = 512, type = CheckerAnnotion.TYPE_STR)  
	private String cardPwd;

	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM, regex="^[1-9]\\d*$")
	private String txnAmount;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String goodsName;

	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_STR)
	private String goodsCode;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String tradeSeq;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String tradeTime;
	
private String keep;
	
	
	@Override
	public void init(Document doc, Object reserved) throws Exception {
		Element ele = (Element) doc.selectSingleNode("//CTRL-INFO");
		keep = getAttrM(ele, "KEEP");
	}
	
	public void setKeep(String keep) {
		this.keep = keep;
	}
	
	public String getKeep() {
		return keep;
	}
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		agentCode = getNodeTextM(doc, "AGENTCODE");
		areaCode = getNodeText(doc, "AREACODE");
		txnChannel = getNodeTextM(doc, "TXNCHANNEL");
		payType = getNodeTextM(doc, "PAYTYPE");
		cardNo = getNodeTextM(doc, "CARDNO");
		cardPwd = getNodeTextM(doc, "CARDPWD");
		txnAmount = getNodeTextM(doc, "TXNAMOUNT");
		goodsName = getNodeText(doc, "GOODSNAME");
		goodsCode = getNodeText(doc, "GOODSCODE");
		tradeSeq = getNodeTextM(doc, "TRADESEQ");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		cardType = getNodeTextM(doc, "CARDTYPE");
		
	}


	public String getAgentCode() {
		return agentCode;
	}


	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}


	public String getCardNo() {
		return cardNo;
	}


	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}


	public String getCardPwd() {
		return cardPwd;
	}


	public void setCardPwd(String cardPwd) {
		this.cardPwd = cardPwd;
	}


	public String getGoodsCode() {
		return goodsCode;
	}


	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}


	public String getPayType() {
		return payType;
	}


	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getTradeSeq() {
		return tradeSeq;
	}


	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}


	public String getTradeTime() {
		return tradeTime;
	}


	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}


	public String getTxnAmount() {
		return txnAmount;
	}


	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}


	public String getTxnChannel() {
		return txnChannel;
	}


	public void setTxnChannel(String txnChannel) {
		this.txnChannel = txnChannel;
	}


	public String getGoodsName() {
		return goodsName;
	}


	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
	public String getAreaCode() {
		return areaCode;
	}


	public String getCardType() {
		return cardType;
	}


	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

}
