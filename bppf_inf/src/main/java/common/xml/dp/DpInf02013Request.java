package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 *快捷交易查询接口 
 * 
 */
public class DpInf02013Request extends CommonReqAbs{
	
	public DpInf02013Request(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
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
	
	
	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}
	
	
	
	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		
		custCode = getNodeTextM(doc, "CUSTCODE");
		
		if(ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)){
			
			staffCode = getNodeTextM(doc, "STAFFCODE");
		}else{
			
			staffCode = getNodeText(doc, "STAFFCODE");
		}
		
		remark1 = getNodeText(doc, "REMARK1");
		
		remark2 = getNodeText(doc, "REMARK2");
	}


}
