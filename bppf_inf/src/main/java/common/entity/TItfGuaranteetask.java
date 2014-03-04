package common.entity;

import java.util.Date;

/**
 * TItfGuaranteetask entity. @author MyEclipse Persistence Tools
 */

public class TItfGuaranteetask implements java.io.Serializable {

	// Fields

	/** serialVersionUID */
	private static final long serialVersionUID = 2093129234386685679L;
	
	private Long taskId;
	
	private String url;
	
	private String param;
	
	private String expectedValue;
	
	private String createTime;
	
	private String endTime;
	
	private Integer runtimes;
	
	private String lastRuntime;
	
	private String stat;
	
	private String nextRuntime;

	// Constructors

	/** default constructor */
	public TItfGuaranteetask() {
	}

	/** minimal constructor */
	public TItfGuaranteetask(Long taskId) {
		this.taskId=taskId;
	}

	/** full constructor */
	public TItfGuaranteetask(Long taskId,String url,String param,
			String expectedValue,String createTime,String endTime,Integer runtimes,
			String lastRuntime,String stat,String nextRuntime) {
		this.taskId=taskId;
		this.url=url;
		this.param=param;
		this.expectedValue=expectedValue;
		this.createTime=createTime;
		this.endTime=endTime;
		this.runtimes=runtimes;
		this.lastRuntime=lastRuntime;
		this.stat=stat;
		this.nextRuntime=nextRuntime;
	}

	// Property accessors

	public Long getTaskId() {
		return this.taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId=taskId;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url=url;
	}

	public String getParam() {
		return this.param;
	}

	public void setParam(String param) {
		this.param=param;
	}

	public String getExpectedValue() {
		return this.expectedValue;
	}

	public void setExpectedValue(String expectedValue) {
		this.expectedValue=expectedValue;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime=createTime;
	}

	public String getEndTime() {
		return this.endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime=endTime;
	}

	public Integer getRuntimes() {
		return this.runtimes;
	}

	public void setRuntimes(Integer runtimes) {
		this.runtimes=runtimes;
	}

	public String getLastRuntime() {
		return this.lastRuntime;
	}

	public void setLastRuntime(String lastRuntime) {
		this.lastRuntime=lastRuntime;
	}

	public String getStat() {
		return this.stat;
	}

	public void setStat(String stat) {
		this.stat=stat;
	}

	public String getNextRuntime() {
		return this.nextRuntime;
	}

	public void setNextRuntime(String nextRuntime) {
		this.nextRuntime=nextRuntime;
	}

}