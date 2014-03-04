package cn.tisson.intf.core.data;

public class PayPlatRequestParameter {

	private PayPlatRequestParameter PPRParam;

	private VerifyParameter verifyParam;

	/**
	 * @return the pPRParam
	 */
	public PayPlatRequestParameter getPPRParam() {
		return PPRParam;
	}

	/**
	 * @param pPRParam the pPRParam to set
	 */
	public void setPPRParam(PayPlatRequestParameter pPRParam) {
		PPRParam = pPRParam;
	}

	/**
	 * @return the verifyParam
	 */
	public VerifyParameter getVerifyParam() {
		return verifyParam;
	}

	/**
	 * @param verifyParam the verifyParam to set
	 */
	public void setVerifyParam(VerifyParameter verifyParam) {
		this.verifyParam = verifyParam;
	}



}
