package common.xml.dp;

import org.dom4j.Document;

import common.utils.ChannelCode;
import common.utils.ParamValid;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 *交易明细查询接口
 * 
 */

public class DpInf01007Request extends CommonReqAbs {

	public DpInf01007Request(String xmlStr) throws Exception {
		super(xmlStr, null);

	}

	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	private String transSeq;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;

	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String verify;
	@CheckerAnnotion(len = 100, type = CheckerAnnotion.TYPE_STR)
	private String payPassword;

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
		ParamValid paramValid = new ParamValid();
		String resultDesc = paramValid.Valid(doc, "INF_01_007");
		if (!resultDesc.equals("0")) {
			throw new Exception(resultDesc);
		}

		transSeq = getNodeTextM(doc, "TRANSSEQ");
		// if (ChannelCode.AGENT_CHANELCODE.equals(channelCode)) {
		// custCode = getNodeTextM(doc, "CUSTCODE");
		// }

		if (ChannelCode.AGENT_CHANELCODE.equals(this.channelCode)) {

			custCode = getNodeTextM(doc, "CUSTCODE");

			staffCode = getNodeTextM(doc, "STAFFCODE");
		} else {

			staffCode = getNodeText(doc, "STAFFCODE");
		}

		verify = getNodeText(doc, "VERIFY");
		if ("01".equals(verify)) {
			payPassword = getNodeTextM(doc, "PAYPASSWORD");
		} else {
			payPassword = getNodeText(doc, "PAYPASSWORD");
		}

	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getTransSeq() {
		return transSeq;
	}

	public void setTransSeq(String transSeq) {
		this.transSeq = transSeq;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

}
