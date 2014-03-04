package common.entity;

import java.util.Date;

/**
 * 
 * 本类描述: 
 * @version: 企业帐户前置接口 v1.0 
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email:  zhuxiaojun@tisson.com
 * @time: 2013-4-20上午12:07:15
 */
public class TSymCustom {

	private Long customId = null;//定制标识
	private Long custId = null;//客户标识
	private String customType = null;//定制定型
	private Long prodId = null;//定制产品标识
	private String customObj = null;//定制对象
	private Long actionId = null;//定制业务标识
	private String th = null;//阀值
	private String thType = null;//阀值类型
	private String operSign = null;//运算符
	private String periodType = null;//周期类型
	private Long contactId = null;//联系标识
	private String customContent = null;//定制内容
	private Date effDate = null;//生效日期
	private Date expDate = null;//失效日期
	private String stat = null;
	
	public Long getCustomId() {
		return customId;
	}
	public void setCustomId(Long customId) {
		this.customId = customId;
	}
	public Long getCustId() {
		return custId;
	}
	public void setCustId(Long custId) {
		this.custId = custId;
	}
	public String getCustomType() {
		return customType;
	}
	public void setCustomType(String customType) {
		this.customType = customType;
	}
	public Long getProdId() {
		return prodId;
	}
	public void setProdId(Long prodId) {
		this.prodId = prodId;
	}
	public String getCustomObj() {
		return customObj;
	}
	public void setCustomObj(String customObj) {
		this.customObj = customObj;
	}
	public Long getActionId() {
		return actionId;
	}
	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}
	public String getTh() {
		return th;
	}
	public void setTh(String th) {
		this.th = th;
	}
	public String getThType() {
		return thType;
	}
	public void setThType(String thType) {
		this.thType = thType;
	}
	public String getOperSign() {
		return operSign;
	}
	public void setOperSign(String operSign) {
		this.operSign = operSign;
	}
	public String getPeriodType() {
		return periodType;
	}
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	public Long getContactId() {
		return contactId;
	}
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	public String getCustomContent() {
		return customContent;
	}
	public void setCustomContent(String customContent) {
		this.customContent = customContent;
	}
	public Date getEffDate() {
		return effDate;
	}
	public void setEffDate(Date effDate) {
		this.effDate = effDate;
	}
	public Date getExpDate() {
		return expDate;
	}
	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	
}
