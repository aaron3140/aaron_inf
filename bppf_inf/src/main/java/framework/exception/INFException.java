package framework.exception;

public class INFException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String errCode = "";
	
	private String errReason = "";
	
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	
	public void setErrReason(String errReason) {
		this.errReason = errReason;
	}
	
	public String getErrCode() {
		return errCode;
	}
	
	public String getErrReason() {
		return errReason;
	}
	
	public INFException(String errCode, String errReason) {
		// TODO Auto-generated constructor stub
		super("[" + errCode + "]" + errReason);
		this.errCode = errCode;
		this.errReason =errReason;
	}
	
	public INFException(String msg) {
		// TODO Auto-generated constructor stub
		super(msg);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
