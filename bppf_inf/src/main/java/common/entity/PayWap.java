package common.entity;

public class PayWap {
	private String agentCode;
	private String areaCode;  //交易区域
	private String txnChannel;  //交易渠道
	private String payType;//支付方式
	private String txnAmount;  //交易金额
	private String merChanturl;  //前台返回地址
	private String backMerChanturl;  //后台返回地址
	private String goodsCode;  //外系统商品编码
	private String goodsName;  //外系统商品名称
	private String tradeSeq;  //交易流水号
	private String requestSeq;  //请求流水号
	private String tradTime;  //交易时间
	private String agentName;
	
	public PayWap(){}

	public PayWap(String agentCode,String areaCode, String txnChannel, String payType, String txnAmount, String merChanturl, String backMerChanturl, String goodsCode, String coodsName, String tradeSeq, String requestSeq, String tradTime,String agentName) {
		super();
		this.agentCode = agentCode;
		this.areaCode = areaCode;
		this.txnChannel = txnChannel;
		this.payType = payType;
		this.txnAmount = txnAmount;
		this.merChanturl = merChanturl;
		this.backMerChanturl = backMerChanturl;
		this.goodsCode = goodsCode;
		this.goodsName = coodsName;
		this.tradeSeq = tradeSeq;
		this.requestSeq = requestSeq;
		this.tradTime = tradTime;
		this.agentName=agentName;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getBackMerChanturl() {
		return backMerChanturl;
	}

	public void setBackMerChanturl(String backMerChanturl) {
		this.backMerChanturl = backMerChanturl;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsCode() {
		return goodsCode;
	}

	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}

	public String getMerChanturl() {
		return merChanturl;
	}

	public void setMerChanturl(String merChanturl) {
		this.merChanturl = merChanturl;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getRequestSeq() {
		return requestSeq;
	}

	public void setRequestSeq(String requestSeq) {
		this.requestSeq = requestSeq;
	}

	public String getTradeSeq() {
		return tradeSeq;
	}

	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}

	public String getTradTime() {
		return tradTime;
	}

	public void setTradTime(String tradTime) {
		this.tradTime = tradTime;
	}

	public String getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}

	public String getTxnChannel() {
		return txnChannel;
	}

	public void setTxnChannel(String txnChannel) {
		this.txnChannel = txnChannel;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	

}
