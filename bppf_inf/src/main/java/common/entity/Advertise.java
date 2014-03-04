package common.entity;

public class Advertise {
	
	private String advertiseId; 
	private String fileId;     
	private String adImage;     
	private String adText;     
	private String action;     
	private String msgId;     
	private String appId; 
	private String url; 
	private String createDate; 
	private String stat; 
	
	public Advertise(){}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAdImage() {
		return adImage;
	}

	public void setAdImage(String adImage) {
		this.adImage = adImage;
	}

	public String getAdText() {
		return adText;
	}

	public void setAdText(String adText) {
		this.adText = adText;
	}

	public String getAdvertiseId() {
		return advertiseId;
	}

	public void setAdvertiseId(String advertiseId) {
		this.advertiseId = advertiseId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Advertise(String advertiseId, String fileId, String adImage, String adText, String action, String msgId, String appId, String url, String createDate, String stat) {
		super();
		this.advertiseId = advertiseId;
		this.fileId = fileId;
		this.adImage = adImage;
		this.adText = adText;
		this.action = action;
		this.msgId = msgId;
		this.appId = appId;
		this.url = url;
		this.createDate = createDate;
		this.stat = stat;
	}

}
