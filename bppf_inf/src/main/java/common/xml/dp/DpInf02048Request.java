package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 * @title DpInf02048Request.java
 * @description 理财赎回接口请求类
 * @date 2014-02-11 09:29
 * @author lichunan
 * @version 1.0
 */
public class DpInf02048Request extends CommonReqAbs {

	public DpInf02048Request(String xmlStr) throws Exception {
		super(xmlStr, null);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 外部订单号
	 */
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String orderSeq;
	/**
	 * 客户编码
	 */
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	/**
	 * 用户名
	 */
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	/**
	 * 支付密码
	 */
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String passWord;
	/**
	 * 赎回类型
	 */
	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_NUM)
	private String ransomType;
	/**
	 * 赎回金额
	 */
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String totalAmount;
	/**
	 * 受理时间
	 */
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String tradeTime;
	/**
	 * 预留域1
	 */
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	/**
	 * 预留域2
	 */
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub
		orderSeq = getNodeTextM(doc, "ORDERSEQ");
		custCode = getNodeTextM(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		passWord = getNodeTextM(doc, "PASSWORD");
		ransomType = getNodeTextM(doc, "RANSOMTYPE");
		totalAmount = getNodeTextM(doc, "TOTALAMOUNT");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	
	public String getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(String orderSeq) {
		this.orderSeq = orderSeq;
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	
	public String getRansomType() {
		return ransomType;
	}

	public void setRansomType(String ransomType) {
		this.ransomType = ransomType;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
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
