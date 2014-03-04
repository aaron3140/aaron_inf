package common.xml.dp;

import org.dom4j.Document;

import common.utils.Charset;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 * 支付插件交易接口
 * 
 */
public class DpInf05002Request extends CommonReqAbs {

	public DpInf05002Request(String xmlStr) throws Exception {

		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String password;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String verifyCode;

	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String fundSource;

	// @CheckerAnnotion(len = 2000, type = CheckerAnnotion.TYPE_STR)
	private String requestXml;

	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		staffCode = getNodeTextM(doc, "STAFFCODE");
		password = getNodeTextM(doc, "PASSWORD");
		verifyCode = getNodeText(doc, "VERIFYCODE");
		fundSource = getNodeText(doc, "FUNDSOURCE");
		if (Charset.isEmpty(fundSource, true)) {
			fundSource = "0";
		}
		requestXml = getNodeTextM(doc, "REQUESTXML");
	}

	public String getFundSource() {
		return fundSource;
	}

	public void setFundSource(String fundSource) {
		this.fundSource = fundSource;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getRequestXml() {
		return requestXml;
	}

	public void setRequestXml(String requestXml) {
		this.requestXml = requestXml;
	}

}
