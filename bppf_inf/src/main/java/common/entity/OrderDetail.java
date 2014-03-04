package common.entity;

public class OrderDetail {

	private String businessId;//业务单号
	private String tsType;//交易类型
	private String time;//时间
	private Double money;//金额
	private String stat;
	
	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId=businessId;
	}
	public String getTsType() {
		return tsType;
	}
	public void setTsType(String tsType) {
		this.tsType=tsType;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time=time;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money=money;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat=stat;
	}
	
	
}
