package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

  //8604400000121100
  //90000163
//ti04@189.com

public class DpInf05004Request extends CommonReqAbs {

	public DpInf05004Request(String xmlStr) throws Exception {
		super(xmlStr, null);
		// TODO Auto-generated constructor stub
	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String passWord;
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String preOrderSeq;
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub
		staffCode=getNodeTextM(doc,"STAFFCODE");
		passWord=getNodeTextM(doc,"PASSWORD");
		custCode=getNodeTextM(doc,"CUSTCODE");
		preOrderSeq=getNodeTextM(doc,"PREORDERSEQ");
	}
	public String getCustCode() {
		return custCode;
	}
	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getPreOrderSeq() {
		return preOrderSeq;
	}
	public void setPreOrderSeq(String preOrderSeq) {
		this.preOrderSeq = preOrderSeq;
	}
	public String getStaffCode() {
		return staffCode;
	}
	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

}
