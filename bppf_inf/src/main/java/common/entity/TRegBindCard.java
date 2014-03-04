package common.entity;

/**
 * 邱亚建 2013-9-27 上午10:56:22<br/>
 * 
 * 本类描述:绑卡订单实体 ---T_INF_REGBINDCARD
 */
public class TRegBindCard {

	private long bindId;
	private long regId;
	private String staffCode;
	private String custCode;
	private String bindDate;
	private String bindOrderNo;
	/**
	 * 绑卡状态： S0A:生成 SOD:绑卡中 S0F:绑卡失败 S0C:绑卡成功
	 */
	private String bindState;
	private String bankCode;
	private String bankName;
	private String bankOpen;
	private String areaCode;
	private String bankAcct;
	/**
	 * 开户姓名
	 */
	private String transAccName;
	private String cerNo;
	private String openPhone;

	/**
	 *S0A: 有效 S0X: 无效
	 */
	private String stat;

	private String remark;

	public long getBindId() {
		return bindId;
	}

	public void setBindId(long bindId) {
		this.bindId = bindId;
	}

	public long getRegId() {
		return regId;
	}

	public void setRegId(long regId) {
		this.regId = regId;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getBindDate() {
		return bindDate;
	}

	public void setBindDate(String bindDate) {
		this.bindDate = bindDate;
	}

	public String getBindOrderNo() {
		return bindOrderNo;
	}

	public void setBindOrderNo(String bindOrderNo) {
		this.bindOrderNo = bindOrderNo;
	}

	/**
	 * 绑卡状态： S0A:生成 SOD:绑卡中 S0F:绑卡失败 S0C:绑卡成功
	 * 
	 * @return
	 */
	public String getBindState() {
		return bindState;
	}

	/**
	 * * 绑卡状态： S0A:生成 SOD:绑卡中 S0F:绑卡失败 S0C:绑卡成功
	 * 
	 * @param bindState
	 */
	public void setBindState(String bindState) {
		this.bindState = bindState;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankOpen() {
		return bankOpen;
	}

	public void setBankOpen(String bankOpen) {
		this.bankOpen = bankOpen;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getBankAcct() {
		return bankAcct;
	}

	public void setBankAcct(String bankAcct) {
		this.bankAcct = bankAcct;
	}

	public String getTransAccName() {
		return transAccName;
	}

	public void setTransAccName(String transAccName) {
		this.transAccName = transAccName;
	}

	public String getCerNo() {
		return cerNo;
	}

	public void setCerNo(String cerNo) {
		this.cerNo = cerNo;
	}

	public String getOpenPhone() {
		return openPhone;
	}

	public void setOpenPhone(String openPhone) {
		this.openPhone = openPhone;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
