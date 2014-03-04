package common.entity;

import java.util.Date;


public class TInfOperInLog {

	private long operInId;	//日志标识
	private String keep;		//操作流水号
	private String connectIp;	//接入方IP
	private String tnmnum;		//终端号
	private String svcCode;		//服务编码
	private Date inDate;		//入站时间
	private String inType;		//入站方式
	private long allow;		//准许通过
	private String objCode;		//对象类型
	private String objValue;	//对象值
	private String objCode2;	//对象类型
	private String objValue2;	//对象值
	private String stat;		//状态
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
	public String getTnmnum() {
		return tnmnum;
	}
	public void setTnmnum(String tnmnum) {
		this.tnmnum=tnmnum;
	}
	public String getSvcCode() {
		return svcCode;
	}
	public void setSvcCode(String svcCode) {
		this.svcCode=svcCode;
	}
	public Date getInDate() {
		return inDate;
	}
	public void setInDate(Date inDate) {
		this.inDate=inDate;
	}
	public String getInType() {
		return inType;
	}
	public void setInType(String inType) {
		this.inType=inType;
	}
	public long getAllow() {
		return allow;
	}
	public void setAllow(long allow) {
		this.allow=allow;
	}
	public String getObjCode() {
		return objCode;
	}
	public void setObjCode(String objCode) {
		this.objCode=objCode;
	}
	public String getObjValue() {
		return objValue;
	}
	public void setObjValue(String objValue) {
		this.objValue=objValue;
	}
	public String getObjCode2() {
		return objCode2;
	}
	public void setObjCode2(String objCode2) {
		this.objCode2=objCode2;
	}
	public String getObjValue2() {
		return objValue2;
	}
	public void setObjValue2(String objValue2) {
		this.objValue2=objValue2;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat=stat;
	}
	
}