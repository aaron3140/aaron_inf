package common.entity;

public class ScsOrder {

	private String acceptseqno; //终端流水号
	
	private String txnamounty; //支付金额
	
	private String productno; //业务对象

	
	public ScsOrder(){}
	
	public ScsOrder(String acceptseqno, String txnamounty, String productno){
		
		this.acceptseqno = acceptseqno;
		this.productno = productno;
		this.txnamounty = txnamounty;
	}
	
	public String getAcceptseqno() {
		return acceptseqno;
	}

	public void setAcceptseqno(String acceptseqno) {
		this.acceptseqno = acceptseqno;
	}

	public String getProductno() {
		return productno;
	}

	public void setProductno(String productno) {
		this.productno = productno;
	}

	public String getTxnamounty() {
		return txnamounty;
	}

	public void setTxnamounty(String txnamounty) {
		this.txnamounty = txnamounty;
	}
	
	
}
