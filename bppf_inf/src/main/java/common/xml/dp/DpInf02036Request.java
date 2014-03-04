package common.xml.dp;

import org.dom4j.Document;

import common.utils.Charset;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 * 邱亚建 2013-11-28 上午10:33:16<br/>
 * 
 * 本类描述:
 */
public class DpInf02036Request extends CommonReqAbs {

	public DpInf02036Request(String xmlStr) throws Exception {

		super(xmlStr, null);
	}

	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String queryType;
	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 50, type = CheckerAnnotion.TYPE_STR)
	private String staffCode;
//	@CheckerAnnotion(len = 11, type = CheckerAnnotion.TYPE_STR)
//	private String phone;
	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)
	private String remark2;

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		queryType = getNodeText(doc, "QUERYTYPE");
		if(Charset.isEmpty(queryType)){
			queryType="0";
		}
		custCode = getNodeTextM(doc, "CUSTCODE");
		staffCode = getNodeTextM(doc, "STAFFCODE");
//		phone = getNodeTextM(doc, "PHONE");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");

	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
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
