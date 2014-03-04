package common.xml.dp;

import org.dom4j.Document;
import org.dom4j.Element;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 * @author Tisson
 *  无密卡信息查询接口
 */
public class DpPaymentInfoQueryRequest extends CommonReqAbs {

	public DpPaymentInfoQueryRequest(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 40, type = CheckerAnnotion.TYPE_STR)
	private String cardNo;
	
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String txnType;
	
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String txnChannel;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String startTime;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String endTime;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String startRecord;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String maxRecord;
	
	protected String keep;
	
	
	public void setMaxRecord(String maxRecord) {
		this.maxRecord = maxRecord;
	}
	
	public void setStartRecord(String startRecord) {
		this.startRecord = startRecord;
	}
	
	public String getMaxRecord() {
		return maxRecord;
	}
	
	public String getStartRecord() {
		return startRecord;
	}
	
	@Override
	public void init(Document doc, Object reserved) throws Exception {
		Element ele = (Element) doc.selectSingleNode("//CTRL-INFO");
		keep = getAttrM(ele, "KEEP");
	}


	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		cardNo = getNodeText(doc, "CARDNO");
		txnType = getNodeText(doc, "TXNTYPE");
		txnChannel = getNodeText(doc, "TXNCHANNEL");
		startTime = getNodeTextM(doc, "STARTTIME");
		endTime = getNodeTextM(doc, "ENDTIME");
		      
		startRecord = getNodeText(doc, "STARTRECORD");
		maxRecord = getNodeText(doc, "MAXRECORD");
	}

	public String getCardNo() {
		return cardNo;
	}


	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getKeep() {
		return keep;
	}


	public void setKeep(String keep) {
		this.keep = keep;
	}


	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getTxnChannel() {
		return txnChannel;
	}


	public void setTxnChannel(String txnChannel) {
		this.txnChannel = txnChannel;
	}


	public String getTxnType() {
		return txnType;
	}


	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}


}
