package common.xml.dp;

import org.dom4j.Document;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf04001Request extends CommonReqAbs {
	
	public DpInf04001Request(String xmlStr) throws Exception {
		super(xmlStr);
	}

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_STR)
	private String phone;

	public void setParameters(Document doc, Object reserved) throws Exception {
		orderNo = getNodeTextM(doc, "ORDERNO");
		phone = getNodeTextM(doc, "MOBILE");
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
