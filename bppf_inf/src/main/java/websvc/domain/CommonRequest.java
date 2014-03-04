package websvc.domain;



public abstract class CommonRequest {

	
	private String ip;
	/**
	 * 客户编码
	 */
	private String custCode;
	
	/**
	 *用户名 
	 */
	private String staffCode;
	
	private String merId;

	/**
	 * 渠道
	 */
	private String channelCode;

	
	private String tmnNum;

	/**
	 * 该请求对象的签名
	 */
	private String sign;

//	@NotNull
	private String cer;

	/**
	 * 防止重复提交，每次请求随机生成的值
	 */
	private String keep;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
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

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getCer() {
		return cer;
	}

	public void setCer(String cer) {
		this.cer = cer;
	}

	public String getKeep() {
		return keep;
	}

	public void setKeep(String keep) {
		this.keep = keep;
	}

}
