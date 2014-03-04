package websvc.domain;

import java.util.Map;


public   class CommonResponse {

	
	
	private  Map<String, Object> respBody;

	public Map<String, Object> getRespBody() {
		return respBody;
	}

	public void setRespBody(Map<String, Object> respBody) {
		this.respBody = respBody;
	}
	
	/**
	 * 返回类型 如：20
	 */
	private String type;

	private String ip;

	// private String result;
	private String sign;

	private String cer;

	private String keep;
	private String code;
	private String content;

	public String getCer() {
		return cer;
	}
	public String getCode() {
		return code;
	}

	public String getContent() {
		return content;
	}

//	@JsonIgnore
	public String getIp() {
		return ip;
	}

	public String getKeep() {
		return keep;
	}

	public String getSign() {
		return sign;
	}

	public String getType() {
		return type;
	}

	public void setCer(String cer) {
		this.cer = cer;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setKeep(String keep) {
		this.keep = keep;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public void setType(String type) {
		this.type = type;
	}

}
