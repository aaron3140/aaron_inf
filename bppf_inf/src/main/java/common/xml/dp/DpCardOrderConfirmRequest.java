package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.Element;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 * @author Tisson
 *
 */
public class DpCardOrderConfirmRequest extends CommonReqAbs {

	public DpCardOrderConfirmRequest(String xmlStr ) throws Exception {
		
		super(xmlStr,null);
	}


	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_STR)
	private String agentCode;
	
	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String areaCode;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String txnChannel;

	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String tradeSeq;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String tradeTime;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String oldTradeSeq;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String oldTradeTime;
	
	
	
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
	
	//User Info attributes
	
	private Element userEle;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String userType;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String orgName;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String userName;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String idType;

	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String inNo;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String address;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String phone;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String userInfoText1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String userInfoText2;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String userInfoText3;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String userInfoText4;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String userInfoText5;

	//Account Info attributes
	
	private Element acctEle;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String acctName;

	@CheckerAnnotion(len = 40, type = CheckerAnnotion.TYPE_STR)
	private String acctNo;

	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_STR)
	private String money;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String acctTradeTime;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String actionInfoText1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String actionInfoText2;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String actionInfoText3;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String actionInfoText4;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String actionInfoText5;
	
	private String keep;
	
	@Override
	public void init(Document doc, Object reserved) throws Exception {
		Element ele = (Element) doc.selectSingleNode("//CTRL-INFO");
		keep = getAttrM(ele, "KEEP");
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		agentCode = getNodeTextM(doc, "AGENTCODE");
		areaCode = getNodeText(doc, "AREACODE");
		txnChannel = getNodeTextM(doc, "TXNCHANNEL");
		tradeSeq = getNodeTextM(doc, "TRADESEQ");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		oldTradeSeq = getNodeTextM(doc, "OLDTRADESEQ");
		oldTradeTime = getNodeTextM(doc, "OLDTRADETIME");
		
		text1 = getNodeText(doc, "TEXT1");
		text2 = getNodeText(doc, "TEXT2");
		text3 = getNodeText(doc, "TEXT3");
		text4 = getNodeText(doc, "TEXT4");
		text5 = getNodeText(doc, "TEXT5");
		
		//User Info
		userEle = (Element) doc.selectSingleNode("//USERINFO");
		if(userEle != null){
			 userType = getAttr(userEle, "USERTYPE");
			 orgName = getAttr(userEle, "ORGNAME");
			 userName = getAttr(userEle, "USERNAME");
			 idType = getAttr(userEle, "ID_TYPE");
			 inNo = getAttr(userEle, "ID_NO");
			 address = getAttr(userEle, "ADDRESS");
			 phone = getAttr(userEle, "PHONE");
			 userInfoText1 = getAttr(userEle, "TEXT1");
			 userInfoText2 = getAttr(userEle, "TEXT2");
			 userInfoText3 = getAttr(userEle, "TEXT3");
			 userInfoText4 = getAttr(userEle, "TEXT4");
			 userInfoText5 = getAttr(userEle, "TEXT5");
		}
		
		acctEle = (Element) doc.selectSingleNode("//ACCTINFO");
		if(acctEle != null){
			acctName = getAttr(acctEle, "ACCTNAME");
			acctNo = getAttr(acctEle, "ACCTNO");
			money = getAttr(acctEle, "MONEY");
			acctTradeTime = getAttr(acctEle, "TRADETIME");
			actionInfoText1 = getAttr(acctEle, "TEXT1");
			actionInfoText2 = getAttr(acctEle, "TEXT2");
			actionInfoText3 = getAttr(acctEle, "TEXT3");
			actionInfoText4 = getAttr(acctEle, "TEXT4");
			actionInfoText5 = getAttr(acctEle, "TEXT5");
		}
		
		
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
	public String getAreaCode() {
		return areaCode;
	}

	public String getAgentCode() {
		return agentCode;
	}


	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getOldTradeSeq() {
		return oldTradeSeq;
	}


	public void setOldTradeSeq(String oldTradeSeq) {
		this.oldTradeSeq = oldTradeSeq;
	}


	public String getOldTradeTime() {
		return oldTradeTime;
	}


	public void setOldTradeTime(String oldTradeTime) {
		this.oldTradeTime = oldTradeTime;
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

	public String getTxnChannel() {
		return txnChannel;
	}


	public void setTxnChannel(String txnChannel) {
		this.txnChannel = txnChannel;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getIdType() {
		return idType;
	}


	public void setIdType(String idType) {
		this.idType = idType;
	}


	public String getInNo() {
		return inNo;
	}


	public void setInNo(String inNo) {
		this.inNo = inNo;
	}


	public String getOrgName() {
		return orgName;
	}


	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getUserInfoText1() {
		return userInfoText1;
	}


	public void setUserInfoText1(String userInfoText1) {
		this.userInfoText1 = userInfoText1;
	}


	public String getUserInfoText2() {
		return userInfoText2;
	}


	public void setUserInfoText2(String userInfoText2) {
		this.userInfoText2 = userInfoText2;
	}


	public String getUserInfoText3() {
		return userInfoText3;
	}


	public void setUserInfoText3(String userInfoText3) {
		this.userInfoText3 = userInfoText3;
	}


	public String getUserInfoText4() {
		return userInfoText4;
	}


	public void setUserInfoText4(String userInfoText4) {
		this.userInfoText4 = userInfoText4;
	}


	public String getUserInfoText5() {
		return userInfoText5;
	}


	public void setUserInfoText5(String userInfoText5) {
		this.userInfoText5 = userInfoText5;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getUserType() {
		return userType;
	}


	public void setUserType(String userType) {
		this.userType = userType;
	}


	public String getAcctName() {
		return acctName;
	}


	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}


	public String getAcctNo() {
		return acctNo;
	}


	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}


	public String getAcctTradeTime() {
		return acctTradeTime;
	}


	public void setAcctTradeTime(String acctTradeTime) {
		this.acctTradeTime = acctTradeTime;
	}


	public String getActionInfoText1() {
		return actionInfoText1;
	}


	public void setActionInfoText1(String actionInfoText1) {
		this.actionInfoText1 = actionInfoText1;
	}


	public String getActionInfoText2() {
		return actionInfoText2;
	}


	public void setActionInfoText2(String actionInfoText2) {
		this.actionInfoText2 = actionInfoText2;
	}


	public String getActionInfoText3() {
		return actionInfoText3;
	}


	public void setActionInfoText3(String actionInfoText3) {
		this.actionInfoText3 = actionInfoText3;
	}


	public String getActionInfoText4() {
		return actionInfoText4;
	}


	public void setActionInfoText4(String actionInfoText4) {
		this.actionInfoText4 = actionInfoText4;
	}


	public String getActionInfoText5() {
		return actionInfoText5;
	}


	public void setActionInfoText5(String actionInfoText5) {
		this.actionInfoText5 = actionInfoText5;
	}


	public String getMoney() {
		return money;
	}


	public void setMoney(String money) {
		this.money = money;
	}


	public Element getAcctEle() {
		return acctEle;
	}


	public void setAcctEle(Element acctEle) {
		this.acctEle = acctEle;
	}


	public Element getUserEle() {
		return userEle;
	}


	public void setUserEle(Element userEle) {
		this.userEle = userEle;
	}

	public String getKeep() {
		return keep;
	}

	public void setKeep(String keep) {
		this.keep = keep;
	}


}
