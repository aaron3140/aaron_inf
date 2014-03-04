package common.entity;

public class VmClient {
	private String fileId;// 文件Id
	private String optional;// 更新选择
	private String version;// 最新版本号
	private String versionDesc;	//升级描述
	private String stat; // 状态
	private String clientId;
	private String isForceup;
	private String fileSize; //文件大小
	
	
	
	
	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getClientId() {
		return clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	
	public VmClient() {}
	
	public VmClient(String clientId,String fileId,String version,String versionDesc,String stat,String fileSize){
		this.clientId = clientId;
		this.fileId = fileId;
		this.version = version;
		this.versionDesc = versionDesc;
		this.stat = stat;
		this.fileSize = fileSize;
	}
	
	public VmClient(String fileId,String version, String clientId,String fileSize) {
		this.fileId = fileId;
		this.version = version;
		this.clientId = clientId;
		this.fileSize = fileSize;
	}
	
	public void fromGetLatestClient(String fileId,String version, String versionDesc,String clientId, String isForceup,String fileSize) {
		this.fileId = fileId;
		this.version = version;
		this.versionDesc = versionDesc;
		this.clientId = clientId;
		this.isForceup = isForceup;
		this.fileSize = fileSize;
	}
	
	public void setIsForceup(String isForceup) {
		this.isForceup = isForceup;
	}
	
	public String getIsForceup() {
		return isForceup;
	}
	
	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getOptional() {
		return optional;
	}

	public void setOptional(String optional) {
		this.optional = optional;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getVersionDesc() {
		return versionDesc;
	}

	public void setVersionDesc(String versionDesc) {
		this.versionDesc = versionDesc;
	}
	
	
		
}
