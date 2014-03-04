package common.xml.dp;

import org.dom4j.Document;

import common.utils.Charset;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbsLogin;

public class DpInf03001Request extends CommonReqAbsLogin {

	public DpInf03001Request(String xmlStr) throws Exception {

		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String sendType;

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getReqTime() {
		return reqTime;
	}

	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}

	@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)
	private String reqTime;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		staffCode = getNodeTextM(doc, "STAFFCODE");

		reqTime = getNodeTextM(doc, "REQTIME");
		sendType = getNodeText(doc, "SENDTYPE");
		if(Charset.isEmpty(sendType, true)){
			sendType = "0";//不送默认为0短信发送
		}
	}

}
