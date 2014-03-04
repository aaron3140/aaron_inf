package common.entity;


/**
 * 充值/提现单据
 * File                 : SignOrder.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : bppf
 * JDK version used     : JDK 1.6
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2012-3-28 下午04:52:59 [created]
 * Author               : Zhilong Luo 罗志龙
 * Email                : luozhilong@tisson.cn
 **/
public class SignOrder {
	
	private String merId;

	private String keep;

	private String amount;// 金额
	
	private String channelCode;// 交易渠道
	
	private String tmnNum;// 交易终端号
	
	private String transSeq;// 交易序列号
	
	private String actionCode;// 交易类型
	
	private BankAcctInfo bankAcctInfo;// 客户绑定银行账户详细信息
	
	private String concessionType;//优惠方案类型
	
	private String concession;//优惠金额
	
	private String finalAmount;//最终扣费金额
	
	public String getMerId() {
		return merId;
	}
	
	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(String finalAmount) {
		this.finalAmount = finalAmount;
	}

	public String getConcessionType() {
		return concessionType;
	}

	public void setConcessionType(String concessionType) {
		this.concessionType = concessionType;
	}

	public String getConcession() {
		return concession;
	}

	public void setConcession(String concession) {
		this.concession = concession;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
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

	public String getTransSeq() {
		return transSeq;
	}

	public void setTransSeq(String transSeq) {
		this.transSeq = transSeq;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public BankAcctInfo getBankAcctInfo() {
		return bankAcctInfo;
	}

	public void setBankAcctInfo(BankAcctInfo bankAcctInfo) {
		this.bankAcctInfo = bankAcctInfo;
	}
	
	public String getKeep() {
		return keep;
	}

	public void setKeep(String keep) {
		this.keep = keep;
	}
}
