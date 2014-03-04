package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 * @title DpInf02050Request.java
 * @description 理财明细列表查询请求参数类
 * @date 2014-02-07 14:30
 * @author lichunan
 * @version 1.0
 */
public class DpInf02049Request extends CommonReqAbs {

	public DpInf02049Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}

	/**
	 * 客户编码
	 */
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	/**
	 * 用户名
	 */
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
	/**
	 * 明细类型
	 */
	@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_STR)
	private String detailType;
	/**
	 * 开始日期
	 */
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String startDate;
	/**
	 * 结束日期
	 */
	@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_STR)
	private String endDate;
	/**
	 * 页码
	 */
	@CheckerAnnotion(len = 100, type = CheckerAnnotion.TYPE_STR)
	private String pageNo;
	/**
	 * 页大小
	 */
	@CheckerAnnotion(len = 100, type = CheckerAnnotion.TYPE_STR)
	private String pageSize;
	/**
	 * 升序、降序标记
	 */
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String sortFlag;
	/**
	 * 预留域1
	 */
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;
	/**
	 * 预留域2
	 */
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		// TODO Auto-generated method stub

		custCode = getNodeTextM(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
		detailType = getNodeTextM(doc, "DETAILTYPE");
		startDate = getNodeTextM(doc, "STARTDATE");
		endDate = getNodeTextM(doc, "ENDDATE");
		pageNo = getNodeTextM(doc, "PAGENO");
		pageSize = getNodeTextM(doc, "PAGESIZE");
		sortFlag = getNodeTextM(doc, "SORTFLAG");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getDetailType() {
		return detailType;
	}

	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getPageNo() {
		return pageNo;
	}

	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortFlag() {
		return sortFlag;
	}

	public void setSortFlag(String sortFlag) {
		this.sortFlag = sortFlag;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

}
