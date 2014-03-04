package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf06202Request extends CommonReqAbs {

	public DpInf06202Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 12, type = CheckerAnnotion.TYPE_STR)
	private String tmnNumNo;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_STR)
	private String tradeTime;
	
	
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String settDate;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String batchNo;
	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String totalSvnum;
	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String totalSvamt;
	@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)
	private String totalSaledep;
	
/*	@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)
	private String areaCode;*/

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		if (ChannelCode.IPOS_CHANELCODE.equals(this.channelCode)) {
			custCode = getNodeText(doc, "CUSTCODE");
		} else {
			custCode = getNodeTextM(doc, "CUSTCODE");
		}
		
		tmnNumNo = getNodeTextM(doc, "TMNNUMNO");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		tradeTime = getNodeTextM(doc, "TRADETIME");
		
		settDate = getNodeTextM(doc, "SETTDATE");
		batchNo = getNodeTextM(doc, "BATCHNO");
		totalSvnum = getNodeTextM(doc, "TOTALSVNUM");
		totalSvamt = getNodeTextM(doc, "TOTALSVAMT");
		totalSaledep = getNodeTextM(doc, "TOTALSALEDEP");
		
		
		//areaCode = getNodeText(doc, "AREACODE");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	



	public String getCustCode() {
		return custCode;
	}



	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}



	public String getTmnNumNo() {
		return tmnNumNo;
	}



	public void setTmnNumNo(String tmnNumNo) {
		this.tmnNumNo = tmnNumNo;
	}



	public String getStaffCode() {
		return staffCode;
	}



	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}



	public String getTradeTime() {
		return tradeTime;
	}



	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}




	public String getSettDate() {
		return settDate;
	}


	public void setSettDate(String settDate) {
		this.settDate = settDate;
	}


	public String getBatchNo() {
		return batchNo;
	}


	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}


	public String getTotalSvnum() {
		return totalSvnum;
	}


	public void setTotalSvnum(String totalSvnum) {
		this.totalSvnum = totalSvnum;
	}


	public String getTotalSvamt() {
		return totalSvamt;
	}


	public void setTotalSvamt(String totalSvamt) {
		this.totalSvamt = totalSvamt;
	}


	public String getTotalSaledep() {
		return totalSaledep;
	}


	public void setTotalSaledep(String totalSaledep) {
		this.totalSaledep = totalSaledep;
	}



/*	public String getAreaCode() {
		return areaCode;
	}


	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}*/


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
