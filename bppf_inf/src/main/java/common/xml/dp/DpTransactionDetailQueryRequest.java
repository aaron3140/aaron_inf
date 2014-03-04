package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;


/**
 *交易明细查询接口
 * 
 */

public class DpTransactionDetailQueryRequest extends CommonReqAbs {

	public DpTransactionDetailQueryRequest(String xmlStr) throws Exception {
		super(xmlStr, null);

	}

	@CheckerAnnotion(len = 15, type = CheckerAnnotion.TYPE_NUM)
	private String transSeq;

	@Override
	public void init(Document doc, Object reserved) throws Exception {
	}

	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		transSeq = getNodeTextM(doc, "TRANSSEQ");
	}

	public String getTransSeq() {
		return transSeq;
	}

	public void setTransSeq(String transSeq) {
		this.transSeq = transSeq;
	}

}
