package common.entity;

/**
 * 软pos商户限制规则实体类
 * 
 * @author Administrator
 * 
 */
public class ScsPosInclude {

	private String posCode; // 商户号

	private String limit; // 每日限制收银次数

	private String errCode;

	private String errMsg;

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getPosCode() {
		return posCode;
	}

	public void setPosCode(String posCode) {
		this.posCode = posCode;
	}

	public ScsPosInclude(String posCode, String limit, String errCode,
			String errMsg) {
		super();
		this.posCode = posCode;
		this.limit = limit;
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

}
