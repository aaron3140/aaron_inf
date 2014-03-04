package common.entity;

import java.util.Date;

public class TInfOrderBusCfg {

	private String cfgId;
	private String svcCode;
	private String busCode;
	private String busName;
	private Date createDate;
	private String stat;
	
	public TInfOrderBusCfg(){}

	public TInfOrderBusCfg(String cfgId, String svcCode, String busCode,
			String busName, Date createDate, String stat) {
		this.cfgId = cfgId;
		this.svcCode = svcCode;
		this.busCode = busCode;
		this.busName = busName;
		this.createDate = createDate;
		this.stat = stat;

	}

	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public String getSvcCode() {
		return svcCode;
	}

	public void setSvcCode(String svcCode) {
		this.svcCode = svcCode;
	}

	public String getBusCode() {
		return busCode;
	}

	public void setBusCode(String busCode) {
		this.busCode = busCode;
	}

	public String getBusName() {
		return busName;
	}

	public void setBusName(String busName) {
		this.busName = busName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

}
