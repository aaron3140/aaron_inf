package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.Element;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 * @author Tisson
 *
 */
public class DpCardOrderRequest extends CommonReqAbs {

	public DpCardOrderRequest(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}


	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_STR)
	private String agentCode;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String cardType;
	
	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String subCardType;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String cardPrefix;
	
	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_STR)
	private String cardAmt;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_NUM, regex="^[1-9]\\d*$")
	private String orderNum;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String tradeSeq;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String tradeTime;
	
@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String text1;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String text2;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String text3;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String text4;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String text5;
	
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
		cardType = getNodeTextM(doc, "CARDTYPE");
		orderNum = getNodeTextM(doc, "ORDERNUM");
		tradeSeq = getNodeTextM(doc, "TRADESEQ");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		cardAmt = getNodeTextM(doc, "CARDAMT");
		subCardType = getNodeText(doc, "SUBCARDTYPE");
		cardPrefix = getNodeText(doc, "CARDPREFIX");
		
		text1 = getNodeText(doc, "TEXT1");
		text2 = getNodeText(doc, "TEXT2");
		text3 = getNodeText(doc, "TEXT3");
		text4 = getNodeText(doc, "TEXT4");
		text5 = getNodeText(doc, "TEXT5");
		
	}


	public String getAgentCode() {
		return agentCode;
	}


	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}


	public String getCardType() {
		return cardType;
	}


	public void setCardType(String cardType) {
		this.cardType = cardType;
	}


	public String getText1() {
		return text1;
	}


	public void setText1(String text1) {
		this.text1 = text1;
	}


	public String getText2() {
		return text2;
	}


	public void setText2(String text2) {
		this.text2 = text2;
	}


	public String getText3() {
		return text3;
	}


	public void setText3(String text3) {
		this.text3 = text3;
	}


	public String getText4() {
		return text4;
	}


	public void setText4(String text4) {
		this.text4 = text4;
	}


	public String getText5() {
		return text5;
	}


	public void setText5(String text5) {
		this.text5 = text5;
	}

	public String getCardAmt() {
		return cardAmt;
	}


	public void setCardAmt(String cardAmt) {
		this.cardAmt = cardAmt;
	}


	public String getOrderNum() {
		return orderNum;
	}


	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
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


	public String getCardPrefix() {
		return cardPrefix;
	}


	public void setCardPrefix(String cardPrefix) {
		this.cardPrefix = cardPrefix;
	}


	public String getSubCardType() {
		return subCardType;
	}


	public void setSubCardType(String subCardType) {
		this.subCardType = subCardType;
	}


}
