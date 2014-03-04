package common.entity;

/**
 * 客户绑定银行账户详细信息
 * File                 : BankAcctInfo.java
 * Copy Right           : 天讯瑞达通信技术有限公司 www.tisson.cn
 * Project              : bppf
 * JDK version used     : JDK 1.6
 * Comments             : 
 * Version              : 1.00
 * Modification history : 2012-3-27 下午04:23:05 [created]
 * Author               : Zhilong Luo 罗志龙
 * Email                : luozhilong@tisson.cn
 **/
public class BankAcctInfo {
	
	private String bankAcctId;// 银行账户ID
	
	private String signContractId;// 签约ID
	
	private String bankAcctName;// 户名
	
	private String bankId;// 银行ID
	
	private String bankCode;// 银行code
	
	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	private String areaCode;// 区域编码
	
	private String bankAcctNbr;// 银行账户号
	
	private String cardType; //卡折标识
	
	private String certType;	//证件类型	
	
	private String certCode;	//证件号
	
	private String bankCardId;	//对公对私
	
	private String bankfilialeName; //开户行名称
	

	public String getBankfilialeName() {
		return bankfilialeName;
	}

	public void setBankfilialeName(String bankfilialeName) {
		this.bankfilialeName = bankfilialeName;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertCode() {
		return certCode;
	}

	public void setCertCode(String certCode) {
		this.certCode = certCode;
	}

	public String getBankCardId() {
		return bankCardId;
	}

	public void setBankCardId(String bankCardId) {
		this.bankCardId = bankCardId;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getBankAcctId() {
		return bankAcctId;
	}

	public void setBankAcctId(String bankAcctId) {
		this.bankAcctId = bankAcctId;
	}
	
	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getSignContractId() {
		return signContractId;
	}

	public void setSignContractId(String signContractId) {
		this.signContractId = signContractId;
	}

	public String getBankAcctName() {
		return bankAcctName;
	}

	public void setBankAcctName(String bankAcctName) {
		this.bankAcctName = bankAcctName;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getBankAcctNbr() {
		return bankAcctNbr;
	}

	public void setBankAcctNbr(String bankAcctNbr) {
		this.bankAcctNbr = bankAcctNbr;
	}

	
}
