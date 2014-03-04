package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf12037Request  extends CommonReqAbs{

	public DpInf12037Request(String xmlStr) throws Exception {
		super(xmlStr, null);

	}
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	private String systemNo;
	
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_NUM)
	private String keepNo;
	
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String acceptDate;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	
	

	public String getCustCode() {
		return custCode;
	}



	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}



	public String getSystemNo() {
		return systemNo;
	}



	public void setSystemNo(String systemNo) {
		this.systemNo = systemNo;
	}



	public String getKeepNo() {
		return keepNo;
	}



	public void setKeepNo(String keepNo) {
		this.keepNo = keepNo;
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



	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		setCustCode(getNodeTextM(doc, "CUSTCODE"));
		
		setSystemNo(getNodeText(doc, "SYSTEMNO"));
		
		setKeepNo(getNodeText(doc, "KEEPNO"));
		
		setAcceptDate(getNodeTextM(doc,"ACCEPTDATE"));
		
		setRemark1(getNodeText(doc, "REMARK1"));
		
		setRemark2(getNodeText(doc, "REMARK2"));
	}

}
