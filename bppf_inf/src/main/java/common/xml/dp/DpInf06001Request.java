package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06001Request extends CommonReqAbs {

	public DpInf06001Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_STR)
	private String tmnNumNo;
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String psamCardNo;
	@CheckerAnnotion(len = 16, type = CheckerAnnotion.TYPE_STR)
	private String random;
	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_STR)
	private String condition;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String encryption;
	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_STR)
	private String networkNo;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		
		if(ChannelCode.IPOS_CHANELCODE.equals(this.channelCode)){
			
			custCode = getNodeText(doc, "CUSTCODE");
		}else{
			
			custCode = getNodeTextM(doc, "CUSTCODE");
		}
		tmnNumNo = getNodeTextM(doc, "TMNNUMNO");
		psamCardNo = getNodeTextM(doc, "PSAMCARDNO");
		random = getNodeTextM(doc, "RANDOM");
		condition = getNodeTextM(doc, "CONDITION");
		encryption = getNodeTextM(doc, "ENCRYPTION");
		networkNo = getNodeTextM(doc, "NETWORKNO");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getTmnNumNo() {
		return tmnNumNo;
	}

	public void setTmnNumNo(String tmnNumNo) {
		this.tmnNumNo = tmnNumNo;
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

	public String getPsamCardNo() {
		return psamCardNo;
	}

	public void setPsamCardNo(String psamCardNo) {
		this.psamCardNo = psamCardNo;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getEncryption() {
		return encryption;
	}

	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	public String getNetworkNo() {
		return networkNo;
	}

	public void setNetworkNo(String networkNo) {
		this.networkNo = networkNo;
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
