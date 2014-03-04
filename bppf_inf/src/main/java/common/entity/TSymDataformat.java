package common.entity;

/**
 * TSymDataformat entity. @author MyEclipse Persistence Tools
 */

public class TSymDataformat {

	private String objectName;
	private String fieldName;
	private String objectType;
	private String objectComment;
	private Integer orderSeq;
	private String fieldComment;
	private Integer maxLength;
	private Integer minLength;
	private Integer isRequired;
	private String gt;
	private String lt;
	private String ge;
	private String le;
	private String eq;
	private String regular;
	private String dataType;
	private String dataFormat;
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType=dataType;
	}

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat=dataFormat;
	}

	/** default constructor */
	public TSymDataformat() {
	}

	public String getObjectComment() {
		return this.objectComment;
	}

	public void setObjectComment(String objectComment) {
		this.objectComment=objectComment;
	}

	public Integer getOrderSeq() {
		return this.orderSeq;
	}

	public void setOrderSeq(Integer orderSeq) {
		this.orderSeq=orderSeq;
	}

	public String getFieldComment() {
		return this.fieldComment;
	}

	public void setFieldComment(String fieldComment) {
		this.fieldComment=fieldComment;
	}

	public Integer getMaxLength() {
		return this.maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength=maxLength;
	}

	public Integer getMinLength() {
		return this.minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength=minLength;
	}

	public Integer getIsRequired() {
		return this.isRequired;
	}

	public void setIsRequired(Integer isRequired) {
		this.isRequired=isRequired;
	}

	public String getGt() {
		return this.gt;
	}

	public void setGt(String gt) {
		this.gt=gt;
	}

	public String getLt() {
		return this.lt;
	}

	public void setLt(String lt) {
		this.lt=lt;
	}

	public String getGe() {
		return this.ge;
	}

	public void setGe(String ge) {
		this.ge=ge;
	}

	public String getLe() {
		return this.le;
	}

	public void setLe(String le) {
		this.le=le;
	}

	public String getEq() {
		return this.eq;
	}

	public void setEq(String eq) {
		this.eq=eq;
	}

	public String getRegular() {
		return this.regular;
	}

	public void setRegular(String regular) {
		this.regular=regular;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName=objectName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName=fieldName;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType=objectType;
	}
	
	

}