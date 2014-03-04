package common.entity;

import java.util.Date;

/**
 * 
 * 本类描述: 
 * @version: 企业帐户前置接口 v1.0 
 * @author: 广州天讯瑞达通讯技术有限公司(zhuxiaojun)
 * @email:  zhuxiaojun@tisson.com
 * @time: 2013-4-8上午09:41:24
 */
public class TInfOrderCtl implements java.io.Serializable{

	private String keep = null;
	private String tmnnum = null;
	private String orderCode = null;
	private String operInfo = null;
	private Date operDate = null;
	private String orderStat = null;
	private String stat = null;
	private String remark = null;
	
	public String getKeep() {
		return keep;
	}
	public void setKeep(String keep) {
		this.keep = keep;
	}
	public String getTmnnum() {
		return tmnnum;
	}
	public void setTmnnum(String tmnnum) {
		this.tmnnum = tmnnum;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getOperInfo() {
		return operInfo;
	}
	public void setOperInfo(String operInfo) {
		this.operInfo = operInfo;
	}
	public Date getOperDate() {
		return operDate;
	}
	public void setOperDate(Date operDate) {
		this.operDate = operDate;
	}
	public String getOrderStat() {
		return orderStat;
	}
	public void setOrderStat(String orderStat) {
		this.orderStat = orderStat;
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
