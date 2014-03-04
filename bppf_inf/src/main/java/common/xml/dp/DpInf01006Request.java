package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.utils.Charset;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 *交易查询接口
 * 
 */
public class DpInf01006Request extends CommonReqAbs {

	public DpInf01006Request(String xmlStr) throws Exception {
		super(xmlStr, null);

	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String dataType;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String objectCode;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String transCard;

	// @CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	// private String searchtime;

	// @CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String startdate;
	//
	// @CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String enddate;

	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)
	private String countTotal;

	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_NUM)
	private String startNum;

	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_NUM)
	private String endNum;

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;

	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String busObject;

	// @CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_STR)
	private String acctType;

	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_STR)
	private String orderstat;

	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_NUM)
	private String areacode;

	@CheckerAnnotion(len = 1000, type = CheckerAnnotion.TYPE_STR)
	private String actionCode;

	@CheckerAnnotion(len = 1000, type = CheckerAnnotion.TYPE_STR)
	private String productCode;

	private String includesoncard;

	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		/*
		 * ParamValid paramValid = new ParamValid();
		 * 
		 * String resultDesc=paramValid.Valid(doc,"INF_01_006"); if (!resultDesc.equals("0")) { throw new Exception (resultDesc); }
		 */

		custCode = getNodeText(doc, "CUSTCODE");
		if (Charset.isEmpty(custCode)) {
			objectCode = getNodeTextM(doc, "OBJECTCODE");
		}

		if (ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)) {

			staffCode = getNodeTextM(doc, "STAFFCODE");
		} else {

			staffCode = getNodeText(doc, "STAFFCODE");
		}

		dataType = getNodeText(doc, "DATATYPE");
		if (Charset.isEmpty(dataType, true)) {// 不选默认为：在途 0
			dataType = "0";
		}

		transCard = getNodeText(doc, "TRANSCARD");
		// searchtime = getNodeTextM(doc, "SEARCHTIME");
		startdate = getNodeText(doc, "STARTDATE");
		enddate = getNodeText(doc, "ENDDATE");
		countTotal = getNodeText(doc, "COUNTTOTAL");
		startNum = getNodeText(doc, "STARTNUM");
		endNum = getNodeText(doc, "ENDNUM");
		orderSeq = getNodeText(doc, "ORDERSEQ");
		busObject = getNodeText(doc, "BUSOBJECT");
		acctType = getNodeText(doc, "ACCTTYPE");
		orderstat = getNodeText(doc, "ORDERSTAT");
		areacode = getNodeText(doc, "AREACODE");
		actionCode = getNodeText(doc, "ACTIONCODE");
		productCode = getNodeText(doc, "PRODUCTCODE");
		includesoncard = getNodeText(doc, "INCLUDESONCARD");
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getBusObject() {
		return busObject;
	}

	public void setBusObject(String busObject) {
		this.busObject = busObject;
	}

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getObjectCode() {
		return objectCode;
	}

	public void setObjectCode(String objectCode) {
		this.objectCode = objectCode;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
	}

	public String getOrderstat() {
		return orderstat;
	}

	public void setOrderstat(String orderstat) {
		this.orderstat = orderstat;
	}

	public String getAreacode() {
		return areacode;
	}

	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public String getEndNum() {
		return endNum;
	}

	public void setEndNum(String endNum) {
		this.endNum = endNum;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getStartNum() {
		return startNum;
	}

	public void setStartNum(String startNum) {
		this.startNum = startNum;
	}

	public String getTransCard() {
		return transCard;
	}

	public void setTransCard(String transCard) {
		this.transCard = transCard;
	}

	public String getCountTotal() {
		return countTotal;
	}

	public void setCountTotal(String countTotal) {
		this.countTotal = countTotal;
	}

	public String getIncludesoncard() {
		return includesoncard;
	}

	public void setIncludesoncard(String includesoncard) {
		this.includesoncard = includesoncard;
	}
}
