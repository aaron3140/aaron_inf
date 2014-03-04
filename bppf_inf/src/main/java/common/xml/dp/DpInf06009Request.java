package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.RegisterReqAbs;

public class DpInf06009Request extends RegisterReqAbs{

	public DpInf06009Request(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)
	private String areaCode;
	
//	@CheckerAnnotion(len = 7, type = CheckerAnnotion.TYPE_STR)
//	private String regType;
//	
//	public String getRegType() {
//		return regType;
//	}
//
//	public void setRegType(String regType) {
//		this.regType = regType;
//	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String pdline;

	public String getPdline() {
		return pdline;
	}

	public void setPdline(String pdline) {
		this.pdline = pdline;
	}

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		areaCode = getNodeTextM(doc, "AREACODE");
		
//		regType = getNodeTextM(doc, "REGTYPE");
		
		pdline = getNodeText(doc, "PDMLINE");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
		
	}
	
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
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
