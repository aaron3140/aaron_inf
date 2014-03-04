package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.Element;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 * @author Tisson
 *
 */
public class DpBuyPhysicalCardRequest extends CommonReqAbs {

	public DpBuyPhysicalCardRequest(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}


	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_STR)
	private String agentCode;

	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String tradeSeq;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String tradeTime;
	
	@CheckerAnnotion(len = 100, type = CheckerAnnotion.TYPE_STR)
	private String company;
	
	@CheckerAnnotion(len = 100, type = CheckerAnnotion.TYPE_STR)
	private String contact;
	
	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String tel;
	
	@CheckerAnnotion(len = 250, type = CheckerAnnotion.TYPE_STR)
	private String add;
	
	@CheckerAnnotion(len = 100, type = CheckerAnnotion.TYPE_STR)
	private String email;
	
	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_STR)
	private String cardAmt;
	
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM, regex="^[1-9]\\d*$")
	private String orderNum;
	
	@CheckerAnnotion(len = 100, type = CheckerAnnotion.TYPE_STR)
	private String invoice;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String payType;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String isFirstFlag;
	
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
	protected String keep;
	
	
	
	
	@Override
	public void init(Document doc, Object reserved) throws Exception {
		Element ele = (Element) doc.selectSingleNode("//CTRL-INFO");
		keep = getAttrM(ele, "KEEP");
	}


	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		agentCode = getNodeTextM(doc, "AGENTCODE");
		tradeSeq = getNodeTextM(doc, "TRADESEQ");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		company = getNodeTextM(doc, "COMPANY");
		contact = getNodeTextM(doc, "CONTACT");
		tel = getNodeTextM(doc, "TEL");
		add = getNodeTextM(doc, "ADD");
		email = getNodeTextM(doc, "EMAIL");
		cardAmt = getNodeTextM(doc, "CARDAMT");
		orderNum = getNodeTextM(doc, "ORDERNUM");
		invoice = getNodeTextM(doc, "INVOICE");
		payType = getNodeTextM(doc, "PAYTYPE");
		isFirstFlag = getNodeTextM(doc, "ISFIRSTFLAG");
		
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


	public String getCompany() {
		return company;
	}


	public void setCompany(String company) {
		this.company = company;
	}


	public String getAdd() {
		return add;
	}


	public void setAdd(String add) {
		this.add = add;
	}


	public String getCardAmt() {
		return cardAmt;
	}


	public void setCardAmt(String cardAmt) {
		this.cardAmt = cardAmt;
	}


	public String getContact() {
		return contact;
	}


	public void setContact(String contact) {
		this.contact = contact;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getInvoice() {
		return invoice;
	}


	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}


	public String getIsFirstFlag() {
		return isFirstFlag;
	}


	public void setIsFirstFlag(String isFirstFlag) {
		this.isFirstFlag = isFirstFlag;
	}


	public String getOrderNum() {
		return orderNum;
	}


	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}


	public String getPayType() {
		return payType;
	}


	public void setPayType(String payType) {
		this.payType = payType;
	}


	public String getTel() {
		return tel;
	}


	public void setTel(String tel) {
		this.tel = tel;
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

	public String getKeep() {
		return keep;
	}


	public void setKeep(String keep) {
		this.keep = keep;
	}


}
