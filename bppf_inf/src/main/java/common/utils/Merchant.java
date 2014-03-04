package common.utils;

public class Merchant {

	private String custCode;  //客户编码(txrd)
	
	private String linkLimit; //连接上限(attr 302)
	
	private String termNo;   //终端号(attr 301)
	
	private String custName; //客户名字
	
	private String busiId;   //商户id（SUPPLYORGCODE 300）
	
	private String areaCode;  //客户区域编码
	
	private String haobaiCustName;  //号百客户编码
	
	private String prtnCode;   //商户合作编码(000009200000)
	
	private String prtnName;   //合作商户名称
	
	private String url;        //商户扣费通知地址(attr 304)
	
	private String procFlag;   //商户业务超时是否进入异常环节标志(001进入 002 不进入 对应attr 305)
	
	private String sourceIp; //商户ip地址（对应T_PNM_SERVER表的SERV_IP字段,可多个使用"|"分割）
	
	private String coEndDate; //合作期限
	
	private String unioncardPrefix;//联合卡编码
	
	private String unioncardName;//联合卡名称
	
	private String riskLevel;//风险机构
	
	private String indCode;//行业编码
	
	private String bindPos; //绑定的POS终端
	
	private String wapKey; //网关key
	
	public String getWapKey() {
		return wapKey;
	}
	
	public void setWapKey(String wapKey) {
		this.wapKey = wapKey;
	}
	
	public String getBindPos() {
		return bindPos;
	}
	
	public void setBindPos(String bindPos) {
		this.bindPos = bindPos;
	}
	
	
	public String getCoEndDate() {
		return coEndDate;
	}
	
	public String getIndCode() {
		return indCode;
	}
	
	public String getRiskLevel() {
		return riskLevel;
	}
	
	public String getUnioncardName() {
		return unioncardName;
	}
	
	public String getUnioncardPrefix() {
		return unioncardPrefix;
	}
	
	public void setCoEndDate(String coEndDate) {
		this.coEndDate = coEndDate;
	}
	
	public void setIndCode(String indCode) {
		this.indCode = indCode;
	}
	
	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	
	public void setUnioncardName(String unioncardName) {
		this.unioncardName = unioncardName;
	}
	
	public void setUnioncardPrefix(String unioncardPrefix) {
		this.unioncardPrefix = unioncardPrefix;
	}
	
	public void setHaobaiCustName(String haobaiCustName) {
		this.haobaiCustName = haobaiCustName;
	}
	
	public String getHaobaiCustName() {
		return haobaiCustName;
	}
	
	
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
	public String getAreaCode() {
		return areaCode;
	}
	
	
	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}
	
	public void setCustName(String custName) {
		this.custName = custName;
	}
	
	public void setLinkLimit(String linkLimit) {
		this.linkLimit = linkLimit;
	}
	
	public void setTermNo(String termNo) {
		this.termNo = termNo;
	}
	
	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}
	
	public String getBusiId() {
		return busiId;
	}
	
	public String getCustCode() {
		return custCode;
	}
	
	public String getCustName() {
		return custName;
	}
	
	public String getLinkLimit() {
		return linkLimit;
	}
	
	public String getTermNo() {
		return termNo;
	}
	
	public String getPrtnCode() {
		return prtnCode;
	}

	public void setPrtnCode(String prtnCode) {
		this.prtnCode = prtnCode;
	}

	
	public String getPrtnName() {
		return prtnName;
	}

	public void setPrtnName(String prtnName) {
		this.prtnName = prtnName;
	}

	public String getProcFlag() {
		return procFlag;
	}

	public void setProcFlag(String procFlag) {
		this.procFlag = procFlag;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
