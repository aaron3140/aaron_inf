package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 *客户端版本管理接口
 * 
 */
public class DpInf2003Request extends CommonReqAbs{
	
	public DpInf2003Request(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}
	
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	private String imsi;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String curVersion;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String system;
	
	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String sysVersion;
	
	@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)
	private String phone;
	
	@CheckerAnnotion(len = 11, type = CheckerAnnotion.TYPE_STR)
	private String productNo;

	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		imsi = getNodeTextM(doc, "IMSI");
		curVersion = getNodeTextM(doc, "CURVERSION");
		system = getNodeTextM(doc, "SYSTEM");
		sysVersion = getNodeTextM(doc, "SYSVERSION");
		phone = getNodeTextM(doc, "PHONE");
		productNo = getNodeText(doc, "PRODUCTNO");
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getCurVersion() {
		return curVersion;
	}

	public void setCurVersion(String curVersion) {
		this.curVersion = curVersion;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getSysVersion() {
		return sysVersion;
	}

	public void setSysVersion(String sysVersion) {
		this.sysVersion = sysVersion;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}
	
}
