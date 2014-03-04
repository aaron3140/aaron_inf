package common.xml.dp;

import org.dom4j.Document;

import common.utils.Charset;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf01012Request extends CommonReqAbs {

	public DpInf01012Request(String xmlStr) throws Exception {

		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getPassowrd() {
		return passowrd;
	}

	public void setPassowrd(String passowrd) {
		this.passowrd = passowrd;
	}

	public String getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(String verifyType) {
		this.verifyType = verifyType;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getWifimac() {
		return wifimac;
	}

	public void setWifimac(String wifimac) {
		this.wifimac = wifimac;
	}

	public String getBluemac() {
		return bluemac;
	}

	public void setBluemac(String bluemac) {
		this.bluemac = bluemac;
	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String passowrd;
	
	@CheckerAnnotion(len = 4, type = CheckerAnnotion.TYPE_STR)
	private String verifyType;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String verifyCode;
	
	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_NUM)
	private String verifyLevel;
	
	public String getVerifyLevel() {
		return verifyLevel;
	}

	public void setVerifyLevel(String verifyLevel) {
		this.verifyLevel = verifyLevel;
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String imei;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String imsi;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String wifimac;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String bluemac;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	
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
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		staffCode = getNodeTextM(doc, "STAFFCODE");
		passowrd = getNodeTextM(doc, "PASSWORD");
		verifyType = getNodeTextM(doc, "VERIFYTYPE");
		
		verifyLevel = getNodeText(doc, "VERIFYLEVEL");
		if(Charset.isEmpty(verifyLevel, true)){
			verifyLevel="000";
		}
		
		verifyCode = getNodeText(doc, "VERIFYCODE");
		if("001".equals(verifyLevel)){
			imei = getNodeText(doc, "IMEI");
			imsi = getNodeText(doc, "IMSI");
			wifimac = getNodeText(doc, "WIFIMAC");
			bluemac = getNodeText(doc, "BLUEMAC");
		}else{
			
//			verifyCode = getNodeTextM(doc, "VERIFYCODE");
			if(Charset.isEmpty(verifyCode, true)){
				imei = getNodeTextM(doc, "IMEI");
				imsi = getNodeTextM(doc, "IMSI");
			}else{
				imei = getNodeText(doc, "IMEI");
				imsi = getNodeText(doc, "IMSI");
			}
		
			wifimac = getNodeTextM(doc, "WIFIMAC");
			bluemac = getNodeTextM(doc, "BLUEMAC");
		}
		
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
		
	}
}
