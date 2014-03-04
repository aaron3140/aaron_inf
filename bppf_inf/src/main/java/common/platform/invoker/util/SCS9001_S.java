/**
 * 
 */
package common.platform.invoker.util;

/**
 * @author lyz
 *
 */
public class SCS9001_S {

	private String termSeq = ""; // 交易流水号
	private String termId = ""; // 终端号
	private String actStaff = ""; // 商户号
	private String actionCode = ""; // 业务代码
	private String subAct = ""; // 子业务代码
	private String qryFlag = ""; // 预付标识
	private String qryType = ""; // 入口类型
	private String qryInfo = ""; // 入口信息
	private String qryPwd = ""; // 入口密码
	private String bookDate = ""; // 预定日期
	private String ip = ""; // IP
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the actionCode
	 */
	public String getActionCode() {
		return actionCode;
	}
	
	/**
	 * @param actionCode the actionCode to set
	 */
	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	/**
	 * @return the actStaff
	 */
	public String getActStaff() {
		return actStaff;
	}

	/**
	 * @param actStaff the actStaff to set
	 */
	public void setActStaff(String actStaff) {
		this.actStaff = actStaff;
	}

	/**
	 * @return the bookDate
	 */
	public String getBookDate() {
		return bookDate;
	}

	/**
	 * @param bookDate the bookDate to set
	 */
	public void setBookDate(String bookDate) {
		this.bookDate = bookDate;
	}

	/**
	 * @return the qryFlag
	 */
	public String getQryFlag() {
		return qryFlag;
	}

	/**
	 * @param qryFlag the qryFlag to set
	 */
	public void setQryFlag(String qryFlag) {
		this.qryFlag = qryFlag;
	}

	/**
	 * @return the qryInfo
	 */
	public String getQryInfo() {
		return qryInfo;
	}

	/**
	 * @param qryInfo the qryInfo to set
	 */
	public void setQryInfo(String qryInfo) {
		this.qryInfo = qryInfo;
	}

	/**
	 * @return the qryPwd
	 */
	public String getQryPwd() {
		return qryPwd;
	}

	/**
	 * @param qryPwd the qryPwd to set
	 */
	public void setQryPwd(String qryPwd) {
		this.qryPwd = qryPwd;
	}

	/**
	 * @return the qryType
	 */
	public String getQryType() {
		return qryType;
	}

	/**
	 * @param qryType the qryType to set
	 */
	public void setQryType(String qryType) {
		this.qryType = qryType;
	}

	/**
	 * @return the subAct
	 */
	public String getSubAct() {
		return subAct;
	}

	/**
	 * @param subAct the subAct to set
	 */
	public void setSubAct(String subAct) {
		this.subAct = subAct;
	}

	/**
	 * @return the termId
	 */
	public String getTermId() {
		return termId;
	}

	/**
	 * @param termId the termId to set
	 */
	public void setTermId(String termId) {
		this.termId = termId;
	}

	/**
	 * @return the termSeq
	 */
	public String getTermSeq() {
		return termSeq;
	}

	/**
	 * @param termSeq the termSeq to set
	 */
	public void setTermSeq(String termSeq) {
		this.termSeq = termSeq;
	}
	
	
}