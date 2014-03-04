package common.entity;

public class SymError{
	
	private String codeOut;
	
	private String infoOut;
	
	public void setCodeOut(String codeOut) {
		this.codeOut = codeOut;
	}
	
	public String getCodeOut() {
		return codeOut;
	}
	
	public void setInfoOut(String infoOut) {
		this.infoOut = infoOut;
	}
	
	public String getInfoOut() {
		return infoOut;
	}
	
	public SymError(String codeOut, String infoOut) {
		this.codeOut = codeOut;
		this.infoOut = infoOut;
	}
}
