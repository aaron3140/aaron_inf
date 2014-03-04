package common.entity;

import java.util.Date;


public class TInfOperOutLog {

	private long operOutId;	//出站日志标识
	private long operInId;	//入站日志标识
	private String keep;		//相应流水号
	private String connectIp;	//系统IP
	private String svcCode;		//服务编码
	private Date outDate;		//出站时间
	private String retCode;		//结果编码
	private String retInfo;		//结果说明
	private String stat;		//状态

	public long getOperOutId() {
		return operOutId;
	}
	public void setOperOutId(long operOutId) {
		this.operOutId=operOutId;
	}
	public long getOperInId() {
		return operInId;
	}
	public void setOperInId(long operInId) {
		this.operInId=operInId;
	}
	public String getKeep() {
		return keep;
	}
	public void setKeep(String keep) {
		this.keep=keep;
	}
	public String getConnectIp() {
		return connectIp;
	}
	public void setConnectIp(String connectIp) {
		this.connectIp=connectIp;
	}
	public String getSvcCode() {
		return svcCode;
	}
	public void setSvcCode(String svcCode) {
		this.svcCode=svcCode;
	}
	public Date getOutDate() {
		return outDate;
	}
	public void setOutDate(Date outDate) {
		this.outDate=outDate;
	}
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode=retCode;
	}
	public String getRetInfo() {
		return retInfo;
	}
	public void setRetInfo(String retInfo) {
		this.retInfo=retInfo;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat=stat;
	}
	
}
