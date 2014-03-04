package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 *快捷交易设置接口
 * 
 */
public class DpInf02009Request extends CommonReqAbs {

	public DpInf02009Request(String xmlStr) throws Exception {

		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	@CheckerAnnotion(len = 100, type = CheckerAnnotion.TYPE_STR)
	private String payPassword;

	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String peramount;

	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String allamount;

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String tradetime;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getPeramount() {
		return peramount;
	}

	public void setPeramount(String peramount) {
		this.peramount = peramount;
	}

	public String getAllamount() {
		return allamount;
	}

	public void setAllamount(String allamount) {
		this.allamount = allamount;
	}

	public String getTradetime() {
		return tradetime;
	}

	public void setTradetime(String tradetime) {
		this.tradetime = tradetime;
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

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		custCode = getNodeTextM(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		orderNo = getNodeTextM(doc, "ORDERNO");
		payPassword = getNodeTextM(doc, "PAYPASSWORD");
		peramount = getNodeTextM(doc, "PERAMOUNT");
		allamount = getNodeTextM(doc, "ALLAMOUNT");
		tradetime = getNodeTextM(doc, "TRADETIME");
		
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

}
