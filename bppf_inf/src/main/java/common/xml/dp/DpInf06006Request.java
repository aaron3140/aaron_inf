package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06006Request extends CommonReqAbs {

	public DpInf06006Request(String xmlStr) throws Exception {

		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;

	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String tmnNumNo;

	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String plateNo;

	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_NUM)
	private String engineNo;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String acceptDate;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		orderSeq = getNodeTextM(doc, "ORDERSEQ");

		if (ChannelCode.IPOS_CHANELCODE.equals(this.channelCode)) {

			custCode = getNodeText(doc, "CUSTCODE");
		} else {

			custCode = getNodeTextM(doc, "CUSTCODE");
		}
		tmnNumNo = getNodeText(doc, "TMNNUMNO");

		plateNo = getNodeTextM(doc, "PLATENO");
		
		engineNo = getNodeText(doc, "ENGINENO");

		acceptDate = getNodeTextM(doc, "ACCEPTDATE");

		remark1 = getNodeText(doc, "REMARK1");
		
		remark2 = getNodeText(doc, "REMARK2");

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

	public String getTmnNumNo() {
		return tmnNumNo;
	}

	public void setTmnNumNo(String tmnNumNo) {
		this.tmnNumNo = tmnNumNo;
	}

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	public String getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
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
