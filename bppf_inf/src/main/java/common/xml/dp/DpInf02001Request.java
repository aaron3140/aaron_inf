package common.xml.dp;

import org.dom4j.Document;

import common.utils.Charset;
import common.utils.ParamValid;
import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

/**
 *交易查询接口
 * 
 */
public class DpInf02001Request extends CommonReqAbs {

	public DpInf02001Request(String xmlStr) throws Exception {
		super(xmlStr, null);

	}

	@CheckerAnnotion(len = 54, type = CheckerAnnotion.TYPE_STR)
	private String custCode;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String queryMode;

	@CheckerAnnotion(len = 32, type = CheckerAnnotion.TYPE_STR)
	private String objectCode;

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String orderNo;
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String dataType;

	@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)
	private String transSeq;
	@CheckerAnnotion(len = 64, type = CheckerAnnotion.TYPE_STR)
	private String keepNo;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_NUM)
	private String remark1;

	@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_NUM)
	private String remark2;

	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {

		ParamValid paramValid = new ParamValid();
		String resultDesc = paramValid.Valid(doc, "INF02001");
		if (!resultDesc.equals("0")) {
			throw new Exception(resultDesc);
		}

		custCode = getNodeText(doc, "CUSTCODE");
		if (Charset.isEmpty(custCode)) {
			objectCode = getNodeTextM(doc, "OBJECTCODE");
		}

		queryMode = getNodeText(doc, "QUERYMODE");
		if (Charset.isEmpty(queryMode, true)) {// 不选默认为：一般模式 0
			queryMode = "0";
		}
		dataType = getNodeText(doc, "DATATYPE");
		if (Charset.isEmpty(dataType, true)) {// 不选默认为：在途 0
			dataType = "0";
		}
		
		orderNo = getNodeText(doc, "ORDERNO");
		keepNo = getNodeText(doc, "KEEPNO");
		transSeq = getNodeText(doc, "TRANSSEQ");
		remark1 = getNodeText(doc, "REMARK1");
		remark2 = getNodeText(doc, "REMARK2");
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getQueryMode() {
		return queryMode;
	}

	public void setQueryMode(String queryMode) {
		this.queryMode = queryMode;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getObjectCode() {
		return objectCode;
	}

	public void setObjectCode(String objectCode) {
		this.objectCode = objectCode;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTransSeq() {
		return transSeq;
	}

	public String getKeepNo() {
		return keepNo;
	}

	public void setKeepNo(String keepNo) {
		this.keepNo = keepNo;
	}

	public void setTransSeq(String transSeq) {
		this.transSeq = transSeq;
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
