package framework.exception;

public class INFLogID {
	public Long pk = null;
	
	public String svcInfName = null;
	
	public String partyGroup = null;
	
	public INFLogID(String svcInfName, String partyGroup) {
		this.pk = null;
		this.svcInfName = svcInfName;
		this.partyGroup = partyGroup;
	}
	
	public INFLogID(Long pk, String svcInfName, String partyGroup) {
		this.pk = pk;
		this.svcInfName = svcInfName;
		this.partyGroup = partyGroup;
	}
	
	public INFLogID(Long pk) {
		this.pk = pk;
	}
	
	public void setPartyGroup(String partyGroup) {
		this.partyGroup = partyGroup;
	}
	
	public String getPartyGroup() {
		return partyGroup;
	}
	
	public void setPk(Long pk) {
		this.pk = pk;
	}
	
	public void setSvcInfName(String svcInfName) {
		this.svcInfName = svcInfName;
	}
	
	public Long getPk() {
		return pk;
	}
	
	public String getSvcInfName() {
		return svcInfName;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
