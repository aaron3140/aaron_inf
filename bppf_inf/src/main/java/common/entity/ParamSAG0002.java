package common.entity;

import java.util.List;

public class ParamSAG0002 {

	private String servCode; //服务编码
	
	private String merid;//前向商户编码
	
	private String tnmNum;//前向商户终端号�������
	
	private String channelCode; //渠道号�
	
	private String tradeSeq; //流水号��ˮ��
	
	private String infCode;//接口平台编码����
	
	private String actionCode;//业务编码�����
	
	private String prodCode;//产品编码��Ʒ����
	
	private String receiverCode;//受理区域编码�����������
	
	private String objCode;//用户标识�û���ʶ
	
	private String objType;//用户标识类型�û���ʶ����
	
	private String orgCode;//��Ȩ��鉴权单位代码����
	
	private List itemList;//鉴权附加信息

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

	public List getItemList() {
		return itemList;
	}

	public void setItemList(List itemList) {
		this.itemList = itemList;
	}

	public String getInfCode() {
		return infCode;
	}

	public void setInfCode(String infCode) {
		this.infCode = infCode;
	}

	public String getObjCode() {
		return objCode;
	}

	public void setObjCode(String objCode) {
		this.objCode = objCode;
	}

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
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

	public String getMerid() {
		return merid;
	}

	public void setMerid(String merid) {
		this.merid = merid;
	}

	public String getTnmNum() {
		return tnmNum;
	}

	public void setTnmNum(String tnmNum) {
		this.tnmNum = tnmNum;
	}
}
