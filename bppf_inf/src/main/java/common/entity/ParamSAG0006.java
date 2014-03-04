package common.entity;


public class ParamSAG0006 {

	private String servCode; //服务编码�������
	
	private String channelCode; //渠道号�
	
	private String tradeSeq; //流水号��ˮ��
	
	private String infCode;//接口平台编码����
	
	private String actionCode;//业务编码�����
	
	private String prodCode;//产品编码��Ʒ����
	
	private String receiverCode;//受理区域编码�����������
	
	private String callBackMsg;//回调报文

	public String getReceiverCode() {
		return receiverCode;
	}

	public void setReceiverCode(String receiverCode) {
		this.receiverCode = receiverCode;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getInfCode() {
		return infCode;
	}

	public void setInfCode(String infCode) {
		this.infCode = infCode;
	}

	public String getCallBackMsg() {
		return callBackMsg;
	}

	public void setCallBackMsg(String callBackMsg) {
		this.callBackMsg = callBackMsg;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public String getServCode() {
		return servCode;
	}

	public void setServCode(String servCode) {
		this.servCode = servCode;
	}

	public String getTradeSeq() {
		return tradeSeq;
	}

	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}
}
