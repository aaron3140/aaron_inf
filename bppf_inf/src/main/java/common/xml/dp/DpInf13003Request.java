package common.xml.dp;

import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;

public class DpInf13003Request extends CommonReqAbs {

	public DpInf13003Request(String xmlStr) throws Exception {
		super(xmlStr, null);
	}
	
	@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_STR)
	private String rankListType;
	
	@CheckerAnnotion(len = 7, type = CheckerAnnotion.TYPE_STR)
	private String searchMonth;
	
	@Override
	public void setParameters(Document doc, Object reserved) throws Exception {
		super.setCustCode(getNodeTextM(doc, "CUSTCODE"));
		super.setStaffCode(getNodeTextM(doc, "STAFFCODE"));
		rankListType = getNodeTextM(doc, "RANKLISTTYPE");
		searchMonth = getNodeText(doc, "SEARCHMONTH");
	}

	public String getRankListType() {
		return rankListType;
	}

	public void setRankListType(String rankListType) {
		this.rankListType = rankListType;
	}

	public String getSearchMonth() {
		return searchMonth;
	}

	public void setSearchMonth(String searchMonth) {
		this.searchMonth = searchMonth;
	}

}
