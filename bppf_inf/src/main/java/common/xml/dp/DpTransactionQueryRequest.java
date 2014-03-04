package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;
/**
 *交易查询接口 
 * 
 */
public class DpTransactionQueryRequest extends CommonReqAbs {

	public DpTransactionQueryRequest(String xmlStr) throws Exception {
		super(xmlStr, null);

	}
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String agentCode;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String searchtime;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String startdate;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String enddate;

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	private String transSeq;

	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String ordertype;

	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String orderstat;

	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private String areacode;

	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		agentCode = getNodeTextM(doc, "AGENTCODE");
		searchtime = getNodeTextM(doc, "SEARCHTIME");
		startdate = getNodeText(doc, "STARTDATE");
		enddate = getNodeText(doc, "ENDDATE");
		orderSeq = getNodeText(doc, "ORDERSEQ");
		transSeq = getNodeText(doc, "TRANSSEQ");
		ordertype = getNodeText(doc, "ORDERTYPE");
		orderstat = getNodeText(doc, "ORDERSTAT");
		areacode = getNodeText(doc, "AREACODE");
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getSearchtime() {
		return searchtime;
	}

	public void setSearchtime(String searchtime) {
		this.searchtime = searchtime;
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

	public String getTransSeq() {
		return transSeq;
	}

	public void setTransSeq(String transSeq) {
		this.transSeq = transSeq;
	}

	public String getOrdertype() {
		return ordertype;
	}

	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
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

}
