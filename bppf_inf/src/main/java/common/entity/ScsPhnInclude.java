package common.entity;
/**
 * 话费充值规则限制实体类
 * @author Administrator
 *
 */
public class ScsPhnInclude {
	
	private String timeLimit ; //每天充值（返利）次数限制
	
	private String minValue ; //返利最低充值金额
	
	private String scsFlag ; //每天达到充值次数限制后是否允许再交易

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getScsFlag() {
		return scsFlag;
	}

	public void setScsFlag(String scsFlag) {
		this.scsFlag = scsFlag;
	}

	public String getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}

	public ScsPhnInclude(String timeLimit, String minValue, String scsFlag) {
		super();
		this.timeLimit = timeLimit;
		this.minValue = minValue;
		this.scsFlag = scsFlag;
	}
	
	
}
