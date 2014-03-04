package common.entity;

public class Account {
	
	private String acctType;// 账户类型
	private String acctStat;// 账户状态
	private String balance;// 余额
	private String activeBalance;// 账户可用余额
	private String frozenBalance;// 账户冻结余额
	
	public String getAcctType() {
		return acctType;
	}
	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}
	public String getAcctStat() {
		return acctStat;
	}
	public void setAcctStat(String acctStat) {
		this.acctStat = acctStat;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getActiveBalance() {
		return activeBalance;
	}
	public void setActiveBalance(String activeBalance) {
		this.activeBalance = activeBalance;
	}
	public String getFrozenBalance() {
		return frozenBalance;
	}
	public void setFrozenBalance(String frozenBalance) {
		this.frozenBalance = frozenBalance;
	}
	
}
