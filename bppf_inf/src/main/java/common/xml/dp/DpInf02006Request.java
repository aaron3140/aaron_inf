package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 *付款单查询接口 
 * 
 */
public class DpInf02006Request extends CommonReqAbs{
	
	public DpInf02006Request(String xmlStr) throws Exception {
		
		super(xmlStr, null);
	}
	
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	
	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String objCode;
	
	@CheckerAnnotion(len = 3, type = CheckerAnnotion.TYPE_STR)
	private String stat;

	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		custCode = getNodeTextM(doc, "CUSTCODE");
		
		if (ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)) {

			staffCode = getNodeTextM(doc, "STAFFCODE");
		} else {

			staffCode = getNodeText(doc, "STAFFCODE");
		}
		
		objCode = getNodeText(doc, "OBJCODE");
		stat = getNodeText(doc, "STAT");
	}
	
	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getObjCode() {
		return objCode;
	}

	public void setObjCode(String objCode) {
		this.objCode = objCode;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}
	
}
