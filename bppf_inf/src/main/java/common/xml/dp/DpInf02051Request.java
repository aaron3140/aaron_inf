package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 * @title DpInf02051Request.java
 * @description 理财历史利率查询请求参数类
 * @date 2014-02-07 14:30
 * @author lichunan
 * @version 1.0
 */
public class DpInf02051Request extends CommonReqAbs {

	public DpInf02051Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

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
	 * 基金代码
	 */
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private String fundCode;
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
		custCode = getNodeTextM(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		fundCode = getNodeTextM(doc, "FUNDCODE");
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
	
	public String getFundCode() {
		return fundCode;
	}

	public void setFundCode(String fundCode) {
		this.fundCode = fundCode;
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
