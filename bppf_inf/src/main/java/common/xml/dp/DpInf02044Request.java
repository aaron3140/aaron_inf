package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf02044Request extends CommonReqAbs {

	public DpInf02044Request(String xmlStr) throws Exception {
		super(xmlStr, null);
		// TODO Auto-generated constructor stub
	}

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	@CheckerAnnotion(len = 11, type = CheckerAnnotion.TYPE_NUM)
	private String phone;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_NUM)
	private String faceAmount;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_NUM)
	private String actionCode;
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_NUM)
	private String prodCode;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;
	

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub

		custCode = getNodeTextM(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		faceAmount = getNodeTextM(doc, "FACEAMOUNT");
		actionCode = getNodeTextM(doc, "ACTIONCODE");
		
		
		if("03010008".equals(actionCode)||"05010005".equals(actionCode)||"04010003".equals(actionCode)){
			
			phone = getNodeTextM(doc, "PHONE");
			
			prodCode = getNodeText(doc, "PRODCODE");
		}else{
			
			phone = getNodeText(doc, "PHONE");
			
			prodCode = getNodeTextM(doc, "PRODCODE");
		}
		

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


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getFaceAmount() {
		return faceAmount;
	}


	public void setFaceAmount(String faceAmount) {
		this.faceAmount = faceAmount;
	}


	public String getActionCode() {
		return actionCode;
	}


	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}


	public String getProdCode() {
		return prodCode;
	}


	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
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
