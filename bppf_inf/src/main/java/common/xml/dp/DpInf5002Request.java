package common.xml.dp;

import org.dom4j.Document;

import common.utils.ParamValid;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf5002Request extends CommonReqAbs {
	
	public DpInf5002Request(String xmlStr) throws Exception {
		super(xmlStr, null);

	}
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String password;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String verifycode;
	
//	@CheckerAnnotion(len = 2000, type = CheckerAnnotion.TYPE_STR)
	private String requestXml;
	
	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		ParamValid paramValid = new ParamValid();
		String resultDesc=paramValid.Valid(doc,"INF05002");
		if (!resultDesc.equals("0")) {
			throw new Exception (resultDesc);
		}
		
		staffCode = getNodeTextM(doc, "STAFFCODE");
		password = getNodeTextM(doc, "PASSWORD");
		verifycode = getNodeTextM(doc, "VERIFYCODE");
		requestXml = getNodeTextM(doc, "REQUESTXML");
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

	public String getVerifycode() {
		return verifycode;
	}

	public void setVerifycode(String verifycode) {
		this.verifycode = verifycode;
	}

	public String getRequestXml() {
		return requestXml;
	}

	public void setRequestXml(String requestXml) {
		this.requestXml = requestXml;
	}


}
