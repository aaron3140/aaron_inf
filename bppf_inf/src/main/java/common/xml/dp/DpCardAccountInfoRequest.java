package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 *账户信息查询接口
 * 
 */
public class DpCardAccountInfoRequest extends CommonReqAbs {

	public DpCardAccountInfoRequest(String xmlStr) throws Exception {

		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String agentCode;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_STR)
	private String bankMode;

	public String getBankMode() {
		return bankMode;
	}

	public void setBankMode(String bankMode) {
		this.bankMode = bankMode;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		agentCode = getNodeTextM(doc, "AGENTCODE");
		if (ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)||ChannelCode.IVR_CHANELCODE.equals(this.channelCode)) {

			staffCode = getNodeTextM(doc, "STAFFCODE");
		} else {

			staffCode = getNodeText(doc, "STAFFCODE");
		}
		bankMode = getNodeText(doc, "BANKMODE");
		if(bankMode==null || "".equals(bankMode)){
			bankMode= "BT1001";
		}
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

}
