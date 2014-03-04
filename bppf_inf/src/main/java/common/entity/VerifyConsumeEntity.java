package common.entity;

public class VerifyConsumeEntity {

	private String custId = null;
	private String staffCode = null;
	private String txnAmount = null;
	private String payPassword = null;
	private String channelCode = null;
	private String tmnNum = null;
	private String ip = null;

	public VerifyConsumeEntity(String custId, String staffCode,
			String txnAmount, String payPassword, String channelCode,
			String tmnNum, String ip) {
		super();
		this.custId = custId;
		this.staffCode = staffCode;
		this.txnAmount = txnAmount;
		this.payPassword = payPassword;
		this.channelCode = channelCode;
		this.tmnNum = tmnNum;
		this.ip = ip;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getTmnNum() {
		return tmnNum;
	}

	public void setTmnNum(String tmnNum) {
		this.tmnNum = tmnNum;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

}
